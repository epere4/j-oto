package com.google.code.joto.value2java;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.code.joto.ast.beanstmt.BeanAST;
import com.google.code.joto.ast.beanstmt.BeanAST.AssignExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.BeanStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.ExprStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.IndexedArrayExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.LiteralExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.MethodApplyExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.NewObjectExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.VarDeclStmt;
import com.google.code.joto.ast.beanstmt.BeanAST.VarRefExpr;
import com.google.code.joto.ast.valueholder.ValueHolderVisitor2;
import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ArrayEltRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.CollectionEltRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.CollectionValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.FieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ImmutableObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapEntryKeyRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapEntryValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapEntryValueRefValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveArrayEltValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveFieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefFieldValueHolder;
import com.google.code.joto.reflect.ClassDictionaryJotoInfo;
import com.google.code.joto.reflect.ClassJotoInfo;
import com.google.code.joto.reflect.ConstructorJotoInfo;
import com.google.code.joto.reflect.ParamToFieldInfo;
import com.google.code.joto.util.NameGenerator;

/**
 *
 */
public class ValueHolderToBeanASTStmt implements ValueHolderVisitor2<BeanAST,Object2ASTInfo> {

	private Map<AbstractObjectValueHolder,Object2ASTInfo> objInitInfoMap = 
		new IdentityHashMap<AbstractObjectValueHolder,Object2ASTInfo>();
	
	private ClassDictionaryJotoInfo classDicInfo = new ClassDictionaryJotoInfo();
	
	private NameGenerator nameGenerator = new NameGenerator(); 
	
	private VarDeclStmt logVarDeclStmt;
	
	private VHToStmtProcessorContext vhToStmtProcessorContext = 
		new VHToStmtProcessorContext();
	
	// -------------------------------------------------------------------------
	
	public ValueHolderToBeanASTStmt() {
	}
	
	// ------------------------------------------------------------------------

	public Map<AbstractObjectValueHolder, Object2ASTInfo> getResultObjInitInfoMap() {
		return objInitInfoMap;
	}

	public VHToStmtProcessorContext getVhToStmtProcessorContext() {
		return vhToStmtProcessorContext;
	}

	public void setVhToStmtProcessorContext(VHToStmtProcessorContext p) {
		this.vhToStmtProcessorContext = p;
	}
	
	// implements ValueHolderVisitor2
	// -------------------------------------------------------------------------

	public void visitRootObject(AbstractObjectValueHolder objVH, String name) {
		objToInitInfo(objVH, name);
	}
	
	@Override
	public BeanAST caseObject(ObjectValueHolder p, Object2ASTInfo objInfo) {
		Class<?> objClass = p.getObjClass();
		Map<Field,FieldValueHolder> fieldsToSet = new HashMap<Field,FieldValueHolder>(p.getFieldsValuesMap());
		
		// choose one ctor, public, with @ConstructorProperties..
		ClassJotoInfo classInfo = classDicInfo.getClassInfo(objClass);
		// List<ConstructorJotoInfo> ctorInfos = classInfo.getConstructorInfos();
		ConstructorJotoInfo ctorInfo = classInfo.choosePublicCtorWithInfo();

		List<BeanExpr> ctorExprs = new ArrayList<BeanExpr>();
		if (ctorInfo != null) {
			List<ParamToFieldInfo> ctorParamToFieldInfos = ctorInfo.getParamToFieldInfos();
			for(ParamToFieldInfo elt : ctorParamToFieldInfos) {
				Field field = elt.getTargetAssignedField();
				FieldValueHolder vh = fieldsToSet.remove(field);
				// convert field -> value -> BeanExpr   (not BeanStmt using visitor)
				BeanExpr valueExpr;
				if (vh instanceof PrimitiveFieldValueHolder) {
					PrimitiveFieldValueHolder vh2 = (PrimitiveFieldValueHolder) vh;
					Object value = vh2.getValue();
					valueExpr = new LiteralExpr(value);
				} else { // (vh instanceof RefObjectFieldValueNode)
					RefFieldValueHolder vh2 = (RefFieldValueHolder) vh;
					AbstractObjectValueHolder fieldVH = vh2.getTo();
					String prefixName = field.getName();
					valueExpr = objToLhsExpr(fieldVH, prefixName);
				}
				ctorExprs.add(valueExpr);
			}
		}				
		// use field values expr as ctor parameters
		BeanExpr initExpr = doNewDefaultObjInstanceExpr(objInfo, p, ctorExprs);
		doSetObjInitExpr(objInfo, initExpr);

		// convert remaining field values to setter stmt
		for(Map.Entry<Field,FieldValueHolder> e : fieldsToSet.entrySet()) {
			FieldValueHolder fieldVH = e.getValue();
			BeanStmt fieldStmt = (BeanStmt) fieldVH.visit(this, objInfo);
			objInfo.addInitStmt(fieldStmt);
		}
		return initExpr;
	}
	
	@Override
	public BeanAST casePrimitiveField(PrimitiveFieldValueHolder p, Object2ASTInfo objInfo) {
		BeanExpr lhsExpr = objToLhsExpr(objInfo);
		BeanExpr argExpr = new LiteralExpr(p.getValue());

		return doCaseFieldSetterExprStmt(p.getField(), lhsExpr, argExpr);
	}

	@Override
	public BeanAST caseRefField(RefFieldValueHolder p, Object2ASTInfo objInfo) {
		BeanExpr lhsExpr = objToLhsExpr(objInfo);
		String namePrefix = p.getField().getName();
		AbstractObjectValueHolder refVH = p.getTo();
		
		// ** recurse **
		Object2ASTInfo refObjInfo = objToInitInfo(refVH, namePrefix);
		
		BeanExpr argExpr = objToLhsExpr(refObjInfo);

		return doCaseFieldSetterExprStmt(p.getField(), lhsExpr, argExpr);
	}

	private BeanStmt doCaseFieldSetterExprStmt(Field field,
			BeanExpr lhsExpr, BeanExpr argExpr) {
		// find corresponding setter for initializing "obj.setField(value)"
		PropertyDescriptor prop = findPropertyDesc(field);
		String setterName;
		if (prop == null) {
			return newLogWarnStmt("prop not found for field " + field);	
		} else if (prop.getWriteMethod() == null) {
			return newLogWarnStmt("writeMethod not found for prop " + field);	
		} else if (prop.getWriteMethod().isAccessible()) {
			return newLogWarnStmt("writeMethod not accessible for prop " + field);	
		} else {
			setterName = prop.getWriteMethod().getName();
		}			 
		BeanExpr expr = new MethodApplyExpr(lhsExpr, setterName, argExpr);
		return new ExprStmt(expr);
	}


	@Override
	public BeanAST caseImmutableObjectValue(ImmutableObjectValueHolder p, Object2ASTInfo objInfo) {
		BeanExpr initExpr;
		if (p.getObjClass().equals(String.class)) {
			initExpr = new LiteralExpr(p.getValue());
		} else {
			BeanExpr ctorArg = new LiteralExpr(p.getValue());
			initExpr = doNewDefaultObjInstanceExpr(objInfo, p, ctorArg);
		}
		doSetObjInitExpr(objInfo, initExpr);
		return initExpr;
	}
	
	@Override
	public BeanAST caseCollection(CollectionValueHolder p, Object2ASTInfo objInfo) {
		BeanExpr initExpr = doNewDefaultObjInstanceExpr(objInfo, p);
		doSetObjInitExpr(objInfo, initExpr);
		
		BeanExpr lsExpr = objToLhsExpr(objInfo);
		String addMethodName = "add";
		
		Collection<CollectionEltRefValueHolder> eltVHs = p.getEltRefs();
		String eltNamePrefix = objInfo.getVarNameWithSuffix("Elt");
		
		int eltIndex = 0;
		for(CollectionEltRefValueHolder eltVH : eltVHs) {
			String indexedEltNamePrefix = eltNamePrefix + eltIndex; // ??
			// *** recurse ***
			BeanExpr eltExpr = (BeanExpr) 
				// eltVH.visit(this, objInfo); ??? use name??
				objToLhsExpr(eltVH.getTo(), indexedEltNamePrefix);
			
			MethodApplyExpr eltAddExpr = new MethodApplyExpr(lsExpr, addMethodName, eltExpr); 
			objInfo.addInitStmt(new ExprStmt(eltAddExpr));
			eltIndex++;
		}
		return initExpr;
	}

	@Override
	public BeanAST caseCollectionElt(CollectionEltRefValueHolder p, Object2ASTInfo objInfo) {
		// NOT USED, cf caseCollection()!
		BeanExpr eltExpr = objToLhsExpr(p.getTo(), null);
		return eltExpr;
	}
	
	@Override
	public BeanAST caseMap(MapValueHolder p, Object2ASTInfo objInfo) {
		BeanExpr initExpr = doNewDefaultObjInstanceExpr(objInfo, p);
		doSetObjInitExpr(objInfo, initExpr);

		BeanExpr lsExpr = objToLhsExpr(objInfo);
		String keyNamePrefix = objInfo.getVarNameWithSuffix("Key");
		String valueNamePrefix = objInfo.getVarNameWithSuffix("Value");
		String putMethodName = "put";
		
		Collection<MapEntryValueHolder> entryVHs = p.getEntries();
		int eltIndex = 0;
		for(MapEntryValueHolder entryVH : entryVHs) {
			// *** recurse ***
			BeanExpr keyExpr = objToLhsExpr(entryVH.getKey(), keyNamePrefix);
			BeanExpr valueExpr = objToLhsExpr(entryVH.getValue(), valueNamePrefix);
			
			MethodApplyExpr eltAddExpr = new MethodApplyExpr(lsExpr, putMethodName, keyExpr, valueExpr); 
			objInfo.addInitStmt(new ExprStmt(eltAddExpr));
			eltIndex++;
		}
		return initExpr;
	}

	

	@Override
	public BeanAST caseMapEntry(MapEntryValueHolder p, Object2ASTInfo arg) {
		// NOT USED, cf caseMap()!
		return null;
	}

	@Override
	public BeanAST caseMapEntryKey(MapEntryKeyRefValueHolder p, Object2ASTInfo arg) {
		// NOT USED, cf caseMap()!
		return null;
	}

	@Override
	public BeanAST caseMapEntryValue(MapEntryValueRefValueHolder p, Object2ASTInfo arg) {
		// NOT USED, cf caseMap()!
		return null;
	}

	@Override
	public BeanAST casePrimitiveArray(PrimitiveArrayValueHolder p, Object2ASTInfo objInfo) {
		PrimitiveArrayEltValueHolder[] arrayVH = p.getHolderArray();
		int len = arrayVH.length;
		Class<?> compClass = p.getObjClass().getComponentType();
		BeanExpr initExpr = doNewObjectArrayExpr(compClass, len);
		doSetObjInitExpr(objInfo, initExpr);
		
		String arrayEltNamePrefix = objInfo.getVarNameWithSuffix("Elt");
		for(int i = 0; i < len; i++) {
			PrimitiveArrayEltValueHolder eltVH = arrayVH[i];
			BeanStmt eltStmt = (BeanStmt) eltVH.visit(this, arrayEltNamePrefix);
			objInfo.addInitStmt(eltStmt);
		}
		return initExpr;
	}


	@Override
	public BeanAST caseRefArray(RefArrayValueHolder p, Object2ASTInfo objInfo) {
		AbstractObjectValueHolder[] eltsVH = p.getElts();
		int len = eltsVH.length;
		Class<?> compClass = p.getObjClass().getComponentType();
		BeanExpr initExpr = doNewObjectArrayExpr(compClass, len);
		doSetObjInitExpr(objInfo, initExpr);
		
		String arrayEltNamePrefix = objInfo.getVarNameWithSuffix("Elt");
		for(int i = 0; i < len; i++) {
			AbstractObjectValueHolder eltVH = eltsVH[i];
			// *** recurse ***
			BeanExpr eltExpr = objToLhsExpr(eltVH, arrayEltNamePrefix);
			// stmt for "array[i] = expr"
			IndexedArrayExpr lhs = new IndexedArrayExpr(initExpr, new LiteralExpr(initExpr));
			AssignExpr assign = new AssignExpr(lhs, eltExpr); 
			objInfo.addInitStmt(new ExprStmt(assign));
		}
		return initExpr;
	}

	protected BeanExpr doNewObjectArrayExpr(Class<?> componentClass, int len) {
		return null;
	}

	@Override
	public BeanAST casePrimitiveArrayElt(PrimitiveArrayEltValueHolder p, Object2ASTInfo objInfo) {
		// TODO NOT_IMPLEMENTED_YET 
		return null;
	}
	
	@Override
	public BeanAST caseRefArrayElt(ArrayEltRefValueHolder p, Object2ASTInfo objInfo) {
		// TODO NOT_IMPLEMENTED_YET 
		return null;
	}

	
	// -------------------------------------------------------------------------
	
	protected Object2ASTInfo objToInitInfo(AbstractObjectValueHolder objVH, String optGeneratePrefixName) {
		if (objVH == null) {
			return null;
		}
		Object2ASTInfo res = objInitInfoMap.get(objVH);
		if (res == null) {
			res = new Object2ASTInfo(objVH);
			objInitInfoMap.put(objVH, res);

			if (optGeneratePrefixName != null) {
				checkGeneratedVarName(res, optGeneratePrefixName);
			}
			
			// *** recurse (non lazy) ****
			BeanExpr initExpr = (BeanExpr) objVH.visit(this, res);
			doSetObjInitExpr(res, initExpr); // ... TOCHECK already set?
		}
		return res;
	}

	protected void doSetObjInitExpr(Object2ASTInfo res, BeanExpr initExpr) {
		Class<?> objType = res.getObjectVH().getObjClass();
		res.setTypeAndInitExpr(objType, initExpr);	
	}
	
	protected Object2ASTInfo objToInitInfo(AbstractObjectValueHolder objVH) {
		Object2ASTInfo res = objToInitInfo(objVH, null);
		return res;
	}

	protected void checkGeneratedVarName(Object2ASTInfo objInfo, String optGeneratePrefixName) {
		if (objInfo == null) {
			return;
		}
		if (objInfo.getVarName() == null) {
			String varName = nameGenerator.newName(optGeneratePrefixName);
			objInfo.setVarName(varName);
		}
	}

	protected BeanExpr objToLhsExpr(AbstractObjectValueHolder objVH, String optGeneratePrefixName) {
		if (objVH == null) return new LiteralExpr(null);
		Object2ASTInfo objInfo = objToInitInfo(objVH);
		if (objInfo.getVarName() == null) {
			if (optGeneratePrefixName == null) {
				optGeneratePrefixName = "tmp";
			}
			checkGeneratedVarName(objInfo, optGeneratePrefixName);
		}
		return objToLhsExpr(objInfo);
	}

	protected BeanExpr objToLhsExpr(Object2ASTInfo objInfo) {
		if (objInfo == null) {
			return new LiteralExpr(null);
		}
		return new VarRefExpr(objInfo.getVarDeclStmt());
	}
	
	private NewObjectExpr doNewDefaultObjInstanceExpr(
			Object2ASTInfo objInfo, 
			AbstractObjectValueHolder p, 
			BeanExpr... optArgs
			) {
		if (objInfo.getVarName() == null) {
			String classAlias = nameGenerator.classToAlias(p.getObjClass());
			checkGeneratedVarName(objInfo, classAlias);
		}
		NewObjectExpr initExpr = new NewObjectExpr(p.getObjClass(), optArgs);
		return initExpr;
	}

	private NewObjectExpr doNewDefaultObjInstanceExpr(
			Object2ASTInfo objInfo, 
			AbstractObjectValueHolder p, 
			List<BeanExpr> optArgs
			) {
		if (objInfo.getVarName() == null) {
			String classAlias = nameGenerator.classToAlias(p.getObjClass());
			checkGeneratedVarName(objInfo, classAlias);
		}
		NewObjectExpr initExpr = new NewObjectExpr(p.getObjClass(), optArgs);
		return initExpr;
	}

	public static PropertyDescriptor findPropertyDesc(Field field) {
		PropertyDescriptor res = null;
		String fieldName = field.getName();
		Class<?> beanClass = field.getDeclaringClass();
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(beanClass);
		} catch(Exception ex) {
			return null; // ??? SHOULD NOT OCCUR
		}
		for(PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
			if (prop.getName().equals(fieldName)) {
				res = prop;
				break;
			}
		}
		return res;
	}

	private BeanStmt newLogWarnStmt(String msg) {
		if (logVarDeclStmt == null) {
			logVarDeclStmt = new VarDeclStmt(Logger.class, "log", null);
		}
		BeanExpr logFieldExpr = new VarRefExpr(logVarDeclStmt);
		String logMethName = "warn";
		BeanExpr logArgMsg = new LiteralExpr(msg);
		BeanExpr methExpr = new MethodApplyExpr(logFieldExpr, logMethName, logArgMsg);
		return new ExprStmt(methExpr );
	}

	public VarDeclStmt getLogVarDeclStmt() {
		return logVarDeclStmt;
	}

}
