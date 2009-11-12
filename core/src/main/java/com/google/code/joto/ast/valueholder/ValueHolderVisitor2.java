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

public interface ValueHolderVisitor2<R,A> {

	R caseNull(A arg);
	R caseObject(ObjectValueHolder p, A arg);
	R casePrimitiveArray(PrimitiveArrayValueHolder p, A arg);
	R caseRefObjectArray(RefObjectArrayValueHolder p, A arg);

	R casePrimitiveField(PrimitiveFieldValueHolder p, A arg);
	R caseRefField(RefObjectFieldValueNode p, A arg);
	R casePrimitiveArrayElt(PrimitiveArrayEltValueHolder p, A arg);
	R caseRefObjectArrayElt(RefObjectArrayEltValueHolder p, A arg);


	// non primitive helper sub-classes of ObjectValueHolder
	<T> R caseImmutableObjectValue(ImmutableObjectValueHolder<T> p, A arg);
	<T> R caseCollectionObject(CollectionValueHolder<T> p, A arg);
	<K,T> R caseMapObject(MapValueHolder<K,T> p, A arg);
//	R caseArrayObject(ArrayValueNode p, A arg);

	
}
