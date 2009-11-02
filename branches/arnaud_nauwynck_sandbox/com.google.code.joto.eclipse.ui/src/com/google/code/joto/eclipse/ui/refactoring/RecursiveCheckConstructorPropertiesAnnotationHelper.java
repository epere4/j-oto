package com.google.code.joto.eclipse.ui.refactoring;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.code.joto.eclipse.ui.util.ConsoleUtil;
import com.google.code.joto.eclipse.ui.util.JavaASTAccessorUtil;
import com.google.code.joto.eclipse.ui.util.JavaASTUtil;
import com.google.code.joto.eclipse.ui.util.JavaASTAccessorUtil.AccessType;
import com.google.code.joto.eclipse.ui.util.JavaASTAccessorUtil.Param2FieldAccessInfo;

public class RecursiveCheckConstructorPropertiesAnnotationHelper extends AbstractASTRewriteRefactoringHelper {

	private static final String JAVA_BEANS_CONSTRUCTOR_PROPERTIES = "java.beans.ConstructorProperties";

	private static class RefactoringInfo {
		final List<CtorDeclRefactoringInfo> ls = new ArrayList<CtorDeclRefactoringInfo>();
		final Set<String> importToAdds = new HashSet<String>();
		
	}
	
	private static class CtorDeclRefactoringInfo {
		MethodDeclaration ctor;
		SingleMemberAnnotation oldAnnotation;
		List<String> annotationValue;
		
		public CtorDeclRefactoringInfo(MethodDeclaration ctor, SingleMemberAnnotation oldAnnotation, List<String> annotationValue) {
			super();
			this.ctor = ctor;
			this.oldAnnotation = oldAnnotation;
			this.annotationValue = annotationValue;
		}
		
	}
	
	public RecursiveCheckConstructorPropertiesAnnotationHelper(IProgressMonitor monitor, Set<ICompilationUnit> selection) {
		super(monitor, selection);
	}
	
	// -------------------------------------------------------------------------
	
	@Override
	protected Object prepareRefactorUnit(CompilationUnit unit) throws Exception {
		RefactoringInfo res = new RefactoringInfo();
		
		Set<String> importAnnotationToChecks = new HashSet<String>();
		
		// find corresponding ClassDescriptor 
		List jdt_types = unit.types();
		if (jdt_types == null || jdt_types.size() == 0) return null;
		AbstractTypeDeclaration jdt_abstracttype = (AbstractTypeDeclaration) jdt_types.get(0);
		if (!(jdt_abstracttype instanceof TypeDeclaration)) return null;
		TypeDeclaration jdt_type = (TypeDeclaration) jdt_abstracttype;
		
		List<MethodDeclaration> ctorDecls = JavaASTUtil.typeDeclToCtorDecl(jdt_type);
		for(MethodDeclaration ctorDecl : ctorDecls) {
			List params = ctorDecl.parameters();
			if (params == null || params.size() == 0) {
				continue;
			}
			SingleMemberAnnotation annotation = JavaASTUtil.findSingleMemberAnnotationFQN(ctorDecl, JAVA_BEANS_CONSTRUCTOR_PROPERTIES);
			List<String> annotationValue;
			try {
				annotationValue = computeCtorAnnotationConstructorProperties(jdt_type, ctorDecl);
			} catch(UnsupportedOperationException ex) {
				continue; // ignore, no rethrow!
			}
			if (annotationValue == null) {
				ConsoleUtil.log("can not compute @ConstructorProperties for ctor:\n" + ctorDecl);
				continue;
			}
			
			if (annotation == null) {
				res.ls.add(new CtorDeclRefactoringInfo(ctorDecl, null, annotationValue));
				importAnnotationToChecks.add(JAVA_BEANS_CONSTRUCTOR_PROPERTIES);
			} else {
				// TODO check/update existing annotation...
			}
			
		}
		
		if (res.ls.isEmpty()) {
			return null;
		}
		

		if (!importAnnotationToChecks.isEmpty()) {
			JavaASTUtil.checkMissingImports(res.importToAdds, unit, importAnnotationToChecks);
		}
		
		return res;
	}

	private List<String> computeCtorAnnotationConstructorProperties(TypeDeclaration typeDecl, MethodDeclaration ctorDecl) {
		int argSize = (ctorDecl.parameters() != null)? ctorDecl.parameters().size() : 0;
		if (argSize == 0) {
			return null;
		}
		List<String> res = new ArrayList<String>(argSize);
		Param2FieldAccessInfo[] fieldAccessInfos = JavaASTAccessorUtil.methParametersToFieldAccessInfos(ctorDecl);
		
		boolean isAllParamDetected = true;
		for (Param2FieldAccessInfo elt : fieldAccessInfos) {
			if (elt == null) {
				isAllParamDetected = false;
				break;
			}
		}
		if (!isAllParamDetected) {
			return null; // can not continue to compute annotation !
		}
		
		if (argSize != fieldAccessInfos.length) {
			throw new IllegalStateException();
		}
		for (int i = 0; i < argSize; i++) {
			Param2FieldAccessInfo elt = fieldAccessInfos[i];
			if (elt.getAccessType() != AccessType.ASSIGNMENT) {
				return null;
			}
			if (elt.getAccessFieldPath().indexOf(".") != -1) {
				// compond path...
				return null;
			}
			String resAnnotationElt = elt.getAccessFieldPath();
			res.add(resAnnotationElt);
		}

		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doRefactorUnit(CompilationUnit unit, Object refactoringInfoObject) throws Exception {
		AST ast = unit.getAST();
		RefactoringInfo refactoringInfo = (RefactoringInfo) refactoringInfoObject;
				
		for(CtorDeclRefactoringInfo ctorRefactoringInfo : refactoringInfo.ls) {
			SingleMemberAnnotation annotation = ctorRefactoringInfo.oldAnnotation;
			if (annotation == null) {
				annotation = ast.newSingleMemberAnnotation();
				annotation.setTypeName(ast.newSimpleName("ConstructorProperties"));
			}
			ArrayInitializer annotationValue = ast.newArrayInitializer();
			List annotationValueNameList = annotationValue.expressions();
			annotationValueNameList.clear();
			for(String name : ctorRefactoringInfo.annotationValue) {
				StringLiteral elt = ast.newStringLiteral();
				elt.setLiteralValue(name);
				annotationValueNameList.add(elt);
			}
			annotation.setValue(annotationValue);
			
			if (ctorRefactoringInfo.oldAnnotation == null) {
				if (ctorRefactoringInfo.ctor.modifiers().isEmpty()) {
					ctorRefactoringInfo.ctor.modifiers().add(0, annotation);
				} else {
					// prepend annotation
					ctorRefactoringInfo.ctor.modifiers().add(0, annotation);
				}
			}
		}
		
		if (!refactoringInfo.importToAdds.isEmpty()) {
			JavaASTUtil.addImports(unit, refactoringInfo.importToAdds);
		}
	}
	
}
