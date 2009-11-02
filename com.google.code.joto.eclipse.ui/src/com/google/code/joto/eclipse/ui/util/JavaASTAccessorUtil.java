package com.google.code.joto.eclipse.ui.util;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaASTAccessorUtil {

	public static final String JAVA_BEANS_CONSTRUCTOR_PROPERTIES = "java.beans.ConstructorProperties";

	private JavaASTAccessorUtil() {
	}
	
	public static enum AccessType {
		ASSIGNMENT,
		COLLECTION_ADD
		// others..
	}
	
	public static class Param2FieldAccessInfo {
		int parameterIndex;
		String paramName;
		
		String accessFieldPath;
		AccessType accessType;
		
		public Param2FieldAccessInfo(int parameterIndex, String paramName, String accessFieldPath, AccessType accessType) {
			super();
			this.parameterIndex = parameterIndex;
			this.paramName = paramName;
			this.accessFieldPath = accessFieldPath;
			this.accessType = accessType;
		}

		public int getParameterIndex() {
			return parameterIndex;
		}

		public String getParamName() {
			return paramName;
		}

		public String getAccessFieldPath() {
			return accessFieldPath;
		}

		public AccessType getAccessType() {
			return accessType;
		}
		
		
	}
	
	
	public static Map<String,Integer> nameToParameterIndex(List<SingleVariableDeclaration> params) {
		final Map<String,Integer> res = new HashMap<String,Integer>();
		for(int i = 0; i < params.size(); i++) {
			String paramName = params.get(i).getName().getIdentifier();
			res.put(paramName , Integer.valueOf(i));
		}
		return res;
	}
	
	public static Param2FieldAccessInfo[] methParametersToFieldAccessInfos(MethodDeclaration meth) {
		final List<SingleVariableDeclaration> params = meth.parameters();
		final Param2FieldAccessInfo[] res = new Param2FieldAccessInfo[(params != null)? params.size() : 0];
		
		if (params != null && params.size() != 0 && meth.getBody() != null) {
			final Map<String,Integer> nameToParameterIndex = nameToParameterIndex(params);

			if (meth.isConstructor()) {
				SingleMemberAnnotation annotation = JavaASTUtil.findSingleMemberAnnotationFQN(meth, JAVA_BEANS_CONSTRUCTOR_PROPERTIES);
				if (annotation != null) {
					List<String> ctorProps = constructorPropertiesToList(annotation);
					if (ctorProps != null) {
						for (int i = 0; i < params.size(); i++) {
							String paramName = params.get(i).getName().getIdentifier();
							String fieldCtorProp = ctorProps.get(i);
							res[i] = new Param2FieldAccessInfo(i, paramName, fieldCtorProp, AccessType.ASSIGNMENT);
						}
						return res;
					}
				}
			}
			
			ASTVisitor detectRhsParameterVisitor = new ASTVisitor() {

				@Override
				public boolean visit(Assignment node) {
					Expression lhs = node.getLeftHandSide();
					Expression rhs = node.getRightHandSide();
					if (rhs instanceof SimpleName) {
						String rhsName = ((SimpleName) rhs).getIdentifier();
						Integer foundParamIndexObj = nameToParameterIndex.get(rhsName);
						if (foundParamIndexObj != null) {
							// found rhs as parameter
							int foundParamIndex = foundParamIndexObj.intValue();
							
							// ... now analyze lhs => assume this. or super. ??
							if (lhs instanceof FieldAccess) {
								FieldAccess lhsFieldAccess = (FieldAccess) lhs; 
								String lhsFieldName = lhsFieldAccess.getName().getIdentifier();
								Expression lhsFieldExpr = lhsFieldAccess.getExpression();
								if (lhsFieldExpr  instanceof ThisExpression) {
									// ok .. .detected "this.<fieldName> = <parameterName>"
									res[foundParamIndex] = new Param2FieldAccessInfo(foundParamIndex, rhsName, lhsFieldName, AccessType.ASSIGNMENT);
								} else {
									// not a this. assignement! ... assume fieldName != parameterNAme 
									if (!lhsFieldName.equals(rhsName)) {
										res[foundParamIndex] = new Param2FieldAccessInfo(foundParamIndex, rhsName, lhsFieldName, AccessType.ASSIGNMENT);
									} else {
										// unrecognized / self assigned var=var ??!!
									}
								}
							} else if (lhs instanceof SuperFieldAccess) {
								SuperFieldAccess lhsSuper = (SuperFieldAccess) lhs;
								String lhsSuperName = lhsSuper.getName().getIdentifier();
								// ok detected "super.<fieldName> = <parameterName>"
								res[foundParamIndex] = new Param2FieldAccessInfo(foundParamIndex, rhsName, lhsSuperName, AccessType.ASSIGNMENT);
							} else if (lhs instanceof SimpleName) {
								String lhsFieldName = ((SimpleName) lhs).getIdentifier();
								// not a this. assignement! ... assume fieldName != parameterNAme 
								if (!lhsFieldName.equals(rhsName)) {
									res[foundParamIndex] = new Param2FieldAccessInfo(foundParamIndex, rhsName, lhsFieldName, AccessType.ASSIGNMENT);
								} else {
									// unrecognized / self assigned var=var ??!!
								}
								
							} else {
								// unrecognized assignement?
							}
						}
					}
					return false;
				}
				
				@Override
				public boolean visit(MethodInvocation node) {
					List<Expression> args = node.arguments();
					int argSize = args.size();
					Map<Integer,Integer> argValueToParentParameterIndexMapping = new HashMap<Integer,Integer>();  
					for(int i = 0; i < argSize; i++) {
						Expression arg = args.get(i);
						if (arg instanceof SimpleName) {
							String argName = ((SimpleName) arg).getIdentifier();
							Integer foundParamIndexObj = nameToParameterIndex.get(argName);
							if (foundParamIndexObj != null) {
								argValueToParentParameterIndexMapping.put(i, foundParamIndexObj);
							}
						}
					}
					if (!argValueToParentParameterIndexMapping.isEmpty()) {
						// detected method call, using parent parameter value
						
						IMethodBinding binding = node.resolveMethodBinding();
						if (binding == null) {
							return false; // cannot continue..
						}
						ITypeBinding declaringClassBinding = binding.getDeclaringClass();
						// TODO test if binding is in same CompilationUnit ... 
						// if (declaringClassBinding.)
						{
							// resolve ASTNode in same CompilationUnit
							CompilationUnit cu = JavaASTUtil.getParentCompilationUnit(node);
							MethodDeclaration resolvedInvocationMeth = (MethodDeclaration) cu.findDeclaringNode(binding);
							if (resolvedInvocationMeth != null) {
								recurseResolvedInvocationMeth(args, resolvedInvocationMeth);
								
								return true;
							}
						}

						// not resolved... try JavaBean naming conventions
						String methodName = node.getName().getIdentifier();
						
						if (methodName.startsWith("set") && argSize == 1) {
							String propName = prefixedMethodNameToPropertyName(methodName, "set");
							Integer parentParameterIndex = argValueToParentParameterIndexMapping.get(Integer.valueOf(0));
							String parentParamName = params.get(0).getName().getIdentifier();
							// ok detected "set<<Name>>(<<paramName>>);
							// TODO ... should handle current path  (getX().getY().set<<Name>>(<<paramName>>)  !!!
							res[parentParameterIndex] = new Param2FieldAccessInfo(parentParameterIndex, parentParamName, 
									propName, AccessType.ASSIGNMENT);
						} else {
							return false; // not resolved + JavaBean setter not detected
						}
					}
					
					return false;
				}


				

				
				@Override
				public boolean visit(ConstructorInvocation node) {
					List args = node.arguments();
					if (args.size() == 0) {
						return false;
					}
					
					IMethodBinding binding = node.resolveConstructorBinding();
					if (binding == null) {
						return false; // can not continue!
					}
					IJavaElement ctorElt = binding.getJavaElement();
					if (ctorElt == null) {
						return false; // can not continue!
					}
					// resolve ASTNode in same CompilationUnit
					CompilationUnit cu = JavaASTUtil.getParentCompilationUnit(node);
					MethodDeclaration resolvedInvocationMeth = (MethodDeclaration) cu.findDeclaringNode(binding);
					if (resolvedInvocationMeth == null) {
						return false; // can not continue ?!
					}

					recurseResolvedInvocationMeth(args, resolvedInvocationMeth);
					
					return super.visit(node);
				}


				@Override
				public boolean visit(SuperConstructorInvocation node) {
					List args = node.arguments();
					if (args.size() == 0) {
						return false;
					}
					
					TypeDeclaration typeDecl = JavaASTUtil.getParentTypeDecl(node);
					Type superClass = typeDecl.getSuperclassType();
					ITypeBinding superClassBinding = superClass.resolveBinding();
					IJavaElement superClassJE = superClassBinding.getJavaElement();
					
					ICompilationUnit superClassICU = (ICompilationUnit) superClassJE.getAncestor(IJavaElement.COMPILATION_UNIT);

					// need parse CompilationUnit from superClass, to call recursive ...
					IProgressMonitor monitor = new NullProgressMonitor();
					CompilationUnit superClassCU = JavaASTUtil.parseCompilationUnit(superClassICU, monitor);

					IMethodBinding ctorBinding = node.resolveConstructorBinding();
					MethodDeclaration resolvedCtorMeth = (MethodDeclaration) superClassCU.findDeclaringNode(ctorBinding.getKey());
					if (resolvedCtorMeth == null) {
						return false; // can not continue ?!
					}
					
					// recurse!
					recurseResolvedInvocationMeth(args, resolvedCtorMeth);
					
					return super.visit(node);
				}
				
				
				private void recurseResolvedInvocationMeth(List<Expression> args, MethodDeclaration calledCtorDecl) {
					// recurse to compute params for target ctor! 
					Param2FieldAccessInfo[] calleeDeclAccessInfos = methParametersToFieldAccessInfos(calledCtorDecl);
					
					if (calleeDeclAccessInfos != null) {
						// re-inject mapping ctor(arg1..N) { this(arg1...P); to field access info
						for(int i = 0; i < args.size(); i++) {
							Expression arg = args.get(i);
							if (arg instanceof SimpleName) {
								String argName = ((SimpleName) arg).getIdentifier();
								Integer foundParamIndexObj = nameToParameterIndex.get(argName);
								if (foundParamIndexObj != null) {
									int foundParamIndex = foundParamIndexObj.intValue();
									
									// compute mapping caller-callee
									Param2FieldAccessInfo argInfo = calleeDeclAccessInfos[i]; // mapping index.. not foundParamIndex !!!
									if (argInfo == null) {
										continue; // did not find info in callee!
									}
									// ok detected "this(...<<paramName>> ... ) ==> redirect to <<fieldName>>
									res[foundParamIndex] = new Param2FieldAccessInfo(foundParamIndex, argName, argInfo.getAccessFieldPath(), argInfo.getAccessType());
								}
							}
						}
					}
				}


				
			};
			
			meth.getBody().accept(detectRhsParameterVisitor);
		}
		return res;
	}
	
	public static String prefixedMethodNameToPropertyName(String name, String prefix) {
		if (!name.startsWith(prefix) || name.length() == prefix.length()) {
			throw new IllegalStateException();
		}
		String tmp = name.substring(prefix.length());
		String res = "" + Character.toLowerCase(tmp.charAt(0)) + ((tmp.length() > 0)? tmp.substring(1) : "");
		return res;
	}
	
	
	public static List<String> constructorPropertiesToList(SingleMemberAnnotation annotation) {
		List<String> res = new ArrayList<String>();
		Expression value = annotation.getValue();
		if (!(value instanceof ArrayInitializer)) {
			return null;
		}
		ArrayInitializer arrayValue = (ArrayInitializer) value;
		List<Expression> arrayValueList = arrayValue.expressions();
		for(Expression elt : arrayValueList) {
			if (!(elt instanceof StringLiteral)) {
				return null;
			}
			StringLiteral eltStr = (StringLiteral) elt;
			res.add(eltStr.getLiteralValue());
		}
		return res;
	}
	
}
