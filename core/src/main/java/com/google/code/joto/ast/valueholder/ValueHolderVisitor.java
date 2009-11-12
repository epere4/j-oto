package com.google.code.joto.ast.valueholder;

import com.google.code.joto.ast.valueholder.ValueHolderAST.CollectionValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ImmutableObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveArrayEltValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveFieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefObjectArrayEltValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefObjectArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefObjectFieldValueNode;

public interface ValueHolderVisitor {

	void caseObject(ObjectValueHolder p);
	void casePrimitiveArray(PrimitiveArrayValueHolder p);
	void caseRefObjectArray(RefObjectArrayValueHolder p);

	void casePrimitiveField(PrimitiveFieldValueHolder node);
	void caseRefField(RefObjectFieldValueNode node);
	void casePrimitiveArrayElt(PrimitiveArrayEltValueHolder p);
	void caseRefObjectArrayElt(RefObjectArrayEltValueHolder p);


	// non primitive helper sub-classes of ObjectValueHolder
	<T> void caseImmutableObjectValue(ImmutableObjectValueHolder<T> p);
	<T> void caseCollectionObject(CollectionValueHolder<T> p);
	<K,T> void caseMapObject(MapValueHolder<K,T> p);
//	void caseArrayObject(ArrayValueNode p);

	
}
