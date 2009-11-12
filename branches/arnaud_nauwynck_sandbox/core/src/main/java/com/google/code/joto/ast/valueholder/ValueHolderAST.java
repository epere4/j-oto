package com.google.code.joto.ast.valueholder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.code.joto.util.attr.DefaultAttributeSupport;
import com.google.code.joto.util.attr.IAttributeSupport;
import com.google.code.joto.util.attr.IAttributeSupportDelegate;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;

/**
 * base class of AST hierarchy for object tree values (or fragment of values)
 */
public abstract class ValueHolderAST implements IAttributeSupportDelegate {
	
	private IAttributeSupport attributeSupport;
	
	protected ValueHolderAST() {
	}
	
	public abstract void visit(ValueHolderVisitor v);
	public abstract <R,A> R visit(ValueHolderVisitor2<R,A> v, A arg);

	
	
	public IAttributeSupport getAttributeSupport() {
		 if (attributeSupport == null) {
			 attributeSupport = new DefaultAttributeSupport();
		}
		 return attributeSupport;
	}

    public static Class wrapperTypeFor(Class primitiveType) {
        if (primitiveType == Boolean.TYPE) return Boolean.class;
        if (primitiveType == Byte.TYPE) return Byte.class;
        if (primitiveType == Character.TYPE) return Character.class;
        if (primitiveType == Short.TYPE) return Short.class;
        if (primitiveType == Integer.TYPE) return Integer.class;
        if (primitiveType == Long.TYPE) return Long.class;
        if (primitiveType == Float.TYPE) return Float.class;
        if (primitiveType == Double.TYPE) return Double.class;
        if (primitiveType == Void.TYPE) return Void.class;
        return null;
    }

	private static void checkValueForPrimitiveType(Object value, Class<?> type) {
		if (value != null) {
			Class<?> valueClass = value.getClass();
			if (valueClass != wrapperTypeFor(type)) {
				throw new IllegalArgumentException();
			}
		} else {
			// no check or throw ??
		}
	}

	// -------------------------------------------------------------------------

	public static abstract class AbstractObjectValueHolder extends ValueHolderAST {
		
		protected final Class<?> objClass;
		
		private List<RefObjectValueHolder> linksFrom = new ArrayList<RefObjectValueHolder>(); 
		
		protected AbstractObjectValueHolder(Class<?> objClass) {
			this.objClass = objClass;
		}

		public Class<?> getObjClass() {
			return objClass;
		}

		public List<RefObjectValueHolder> getLinksFrom() {
			return linksFrom;
		}
		
		/*pp*/ void _inv_removeLinkFrom(RefObjectValueHolder p) {
			linksFrom.remove(p);
		}

		/*pp*/ void _inv_addLinkFrom(RefObjectValueHolder p) {
			linksFrom.add(p);
		}

	}
	
	/**
	 *
	 */
	public static abstract class RefObjectValueHolder extends ValueHolderAST {
		
		private final AbstractObjectValueHolder from;
		private AbstractObjectValueHolder to;
		
		public RefObjectValueHolder(AbstractObjectValueHolder from) {
			this.from = from;
		}
		
		public final AbstractObjectValueHolder getTo() {
			return to;
		}

		public final AbstractObjectValueHolder getFrom() {
			return from;
		}

		public final void setTo(AbstractObjectValueHolder p) {
			if (p == to) return;
			if (to != null) {
				to._inv_removeLinkFrom(this);
			}
			this.to = p;
			if (to != null) {
				to._inv_addLinkFrom(this);
			}
		}
		
	}
	
//	/**
//	 * 
//	 */
//	public static class NullValueHolder extends AbstractObjectValueHolder {
//
//		public static final NullValueHolder INSTANCE = new NullValueHolder();
//		
//		public static final NullValueHolder getInstance() { return INSTANCE; }
//		
//		private NullValueHolder() {
//			super(Void.class);
//		}
//		
//		@Override
//		public void visit(ValueHolderVisitor v) {
//			v.caseNull();
//		}
//
//		@Override
//		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
//			return v.caseNull(a);
//		}
//		
//	}
	

	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class ObjectValueHolder extends AbstractObjectValueHolder {
		
		private Map<Field,FieldValueHolder> fieldsValuesMap = new LinkedHashMap<Field,FieldValueHolder>();
		
		public ObjectValueHolder(Class<?> objClass) {
			super(objClass);
		}

		public void visit(ValueHolderVisitor v) {
			v.caseObject(this);
		}

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseObject(this, a);
		}

		public Map<Field,FieldValueHolder> getFieldsValuesMap() {
			return fieldsValuesMap;
		}

		public FieldValueHolder getFieldValue(FieldDictionary fieldDictionary, String fieldName, Class<?> fieldType) {
			Field field;
			try {
				// does not work: only public fields are found
				//... field = objClass.getField(fieldName);
				field = fieldDictionary.field(objClass, fieldName, null);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
			return getFieldValue(field);
		}
		
		public FieldValueHolder getFieldValue(Field field) {
			FieldValueHolder res = fieldsValuesMap.get(field);
			if (res == null) {
				// instanciate proer sub-class for Field
				res = newFieldValue(field);
				fieldsValuesMap.put(field, res);
			}
			return res;
		}
		
		protected FieldValueHolder newFieldValue(Field field) {
			FieldValueHolder res;
			Class<?> fieldType = field.getType();
			if (fieldType.isPrimitive()) {
				res = new PrimitiveFieldValueHolder(this, field);
			} else {
				res = new RefObjectFieldValueNode(this, field);
			}
			return res;
		}
	}

	/**
	 * 
	 */
	public static interface FieldValueHolder {
		ValueHolderAST getThisValueHolder();
		// helper for getThisValueHolder().visit(v) 
		public void visit(ValueHolderVisitor v);
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A arg);
		
		public ObjectValueHolder getParent();
		public Field getField();
		public Class<?> getFieldType(); // helper for getField().getType()
		
	}
	
//	/**
//	 * 
//	 */
//	public static abstract class AbstractFieldValueHolder extends ValueHolderAST {
//
//		protected AbstractObjectValueHolder parent;
//		protected final Field field;
//
//		public AbstractFieldValueHolder(AbstractObjectValueHolder parent, Field field) {
//			super();
//			this.parent = parent;
//			this.field = field;
//		}
//
//		
//		public AbstractObjectValueHolder getParent() {
//			return parent;
//		}
//
//		public Field getField() {
//			return field;
//		}
//
//		public Class<?> getFieldType() {
//			return field.getType();
//		}
//
//	}

	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class PrimitiveFieldValueHolder<T> extends ValueHolderAST implements FieldValueHolder { // extends AbstractFieldValueHolder 
	    
		protected final ObjectValueHolder parent;
		protected final Field field;

		private T value;
		
		public PrimitiveFieldValueHolder(ObjectValueHolder parent, Field field) {
			this.parent = parent;
			this.field = field;
		}

		public void visit(ValueHolderVisitor v) {
			v.casePrimitiveField(this);
		}		

		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.casePrimitiveField(this, a);
		}

		public ValueHolderAST getThisValueHolder() {
			return this;
		}
		
		public ObjectValueHolder getParent() {
			return parent;
		}

		public Field getField() {
			return field;
		}

		public Class<?> getFieldType() {
			return field.getType();
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			checkValueForPrimitiveType(value, field.getType());
			this.value = value;
		}

	}


	/**
	 * 
	 */
	public static class RefObjectFieldValueNode extends RefObjectValueHolder implements FieldValueHolder {

		protected final Field field;
		
		public RefObjectFieldValueNode(ObjectValueHolder parent, Field field) {
			super(parent);
			this.field = field;
		}

		public void visit(ValueHolderVisitor v) {
			v.caseRefField(this);
		}
		
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseRefField(this, a);
		}

		public ValueHolderAST getThisValueHolder() {
			return this;
		}
		
		public ObjectValueHolder getParent() {
			return (ObjectValueHolder) getFrom();
		}

		public Field getField() {
			return field;
		}

		public Class<?> getFieldType() {
			return field.getType();
		}

	}

	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class ImmutableObjectValueHolder<T> extends AbstractObjectValueHolder {
		
		private final T value;

		public ImmutableObjectValueHolder(T value) {
			super(value.getClass());
			this.value = value;
		}
		
		@Override
		public void visit(ValueHolderVisitor v) {
			v.caseImmutableObjectValue(this);
		}

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseImmutableObjectValue(this, a);
		}

		public T getValue() {
			return value;
		}
		
		
		
		
	}

	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class PrimitiveArrayEltValueHolder<T> extends ValueHolderAST {
	    
		private PrimitiveArrayValueHolder<T> parent;
		private int index;
		
		private T value;
		
		public PrimitiveArrayEltValueHolder(PrimitiveArrayValueHolder<T> parent, int index) {
			super();
			this.parent = parent;
			this.index = index;
		}

		public void visit(ValueHolderVisitor v) {
			v.casePrimitiveArrayElt(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.casePrimitiveArrayElt(this, a);
		}

		public PrimitiveArrayValueHolder<T> getParent() {
			return parent;
		}

		public int getIndex() {
			return index;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			checkValueForPrimitiveType(value, parent.componentWrapperType);
			this.value = value;
		}

	}

	/**
	 * 
	 */
	public static class PrimitiveArrayValueHolder<T> extends ObjectValueHolder {

 		private Class<?> componentWrapperType;
		private PrimitiveArrayEltValueHolder[] holderArray;
		
		public PrimitiveArrayValueHolder(Class<?> arrayObjType, int len) {
			super(arrayObjType);
			componentWrapperType = wrapperTypeFor(arrayObjType.getComponentType());
			holderArray = new PrimitiveArrayEltValueHolder[len];
			for (int i = 0; i < len; i++) {
				holderArray[i] = new PrimitiveArrayEltValueHolder(this, i);
			}
		}

		public void visit(ValueHolderVisitor v) {
			v.casePrimitiveArray(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.casePrimitiveArray(this, a);
		}

		public PrimitiveArrayEltValueHolder[] getHolderArray() {
			return holderArray;
		}

		public PrimitiveArrayEltValueHolder getHolderArrayAt(int index) {
			return holderArray[index];
		}
		
		public void setValueAt(int index, T value) {
			PrimitiveArrayEltValueHolder<T> h = getHolderArrayAt(index);
			h.setValue(value);
		}

		public T getValueAt(int index) {
			PrimitiveArrayEltValueHolder<T> h = getHolderArrayAt(index);
			return h.getValue();
		}

	}


	// -------------------------------------------------------------------------

	/**
	 * 
	 */
	public static class RefObjectArrayEltValueHolder<T> extends ValueHolderAST {
	    
		private RefObjectArrayValueHolder<T> parent;
		private int index;
		
		private AbstractObjectValueHolder value;
		
		public RefObjectArrayEltValueHolder(RefObjectArrayValueHolder<T> parent, int index) {
			this.parent = parent;
			this.index = index;
		}

		public void visit(ValueHolderVisitor v) {
			v.caseRefObjectArrayElt(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseRefObjectArrayElt(this, a);
		}

		public RefObjectArrayValueHolder getParent() {
			return parent;
		}

		public int getIndex() {
			return index;
		}
		
		public AbstractObjectValueHolder getValue() {
			return value;
		}

		public void setValue(AbstractObjectValueHolder p) {
			this.value = p;
		}

	}

	/**
	 * 
	 */
	public static class RefObjectArrayValueHolder<T> extends AbstractObjectValueHolder {
	    
		private RefObjectArrayEltValueHolder[] holderArray;
		
		public RefObjectArrayValueHolder(Class<T[]> arrayType, int len) {
			super(arrayType);
			holderArray = new RefObjectArrayEltValueHolder[len];
			for (int i = 0; i < len; i++) {
				holderArray[i] = new RefObjectArrayEltValueHolder(this, i);
			}
		}

		@Override
		public void visit(ValueHolderVisitor v) {
			v.caseRefObjectArray(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseRefObjectArray(this, a);
		}

		public RefObjectArrayEltValueHolder[] getHolderArray() {
			return holderArray;
		}

		public RefObjectArrayEltValueHolder getHolderArrayEltAt(int index) {
			return holderArray[index];
		}
		
		public void setValueAt(int index, AbstractObjectValueHolder value) {
			RefObjectArrayEltValueHolder h = getHolderArrayEltAt(index);
			h.setValue(value);
		}

		public AbstractObjectValueHolder getValueAt(int index) {
			RefObjectArrayEltValueHolder h = getHolderArrayEltAt(index);
			return h.getValue();
		}

	}


	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class CollectionValueHolder<T> extends ObjectValueHolder {
		
		private Collection<AbstractObjectValueHolder> elts;
		
		public CollectionValueHolder() {
			this(ArrayList.class, new ArrayList<AbstractObjectValueHolder>());
		}

		public CollectionValueHolder(Class type, Collection<AbstractObjectValueHolder> value) {
			super(type);
			this.elts = value;
		}

		public void visit(ValueHolderVisitor v) {
			v.caseCollectionObject(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseCollectionObject(this, a);
		}
		
		public Collection<AbstractObjectValueHolder> getElts() {
			return elts;
		}

		public void setElts(Collection<AbstractObjectValueHolder> p) {
			this.elts = p;
		}

		public void addElt(AbstractObjectValueHolder p) {
			elts.add(p);
		}

	}

	/**
	 *
	 */
	public static class MapEntryValueHolder<K,T> {
		AbstractObjectValueHolder/*<K>*/ key;
		AbstractObjectValueHolder/*<T>*/ value;
		
		public MapEntryValueHolder(AbstractObjectValueHolder key,
				AbstractObjectValueHolder value) {
			super();
			this.key = key;
			this.value = value;
		}

		public AbstractObjectValueHolder getKey() {
			return key;
		}

		public void setKey(AbstractObjectValueHolder key) {
			this.key = key;
		}

		public AbstractObjectValueHolder getValue() {
			return value;
		}

		public void setValue(AbstractObjectValueHolder value) {
			this.value = value;
		}

		
	}
	
	/**
	 * 
	 */
	public static class MapValueHolder<K,T> extends ObjectValueHolder {
		
		private Collection<MapEntryValueHolder<K,T>> entries;
		
		public MapValueHolder() {
			this(HashMap.class);
		}

		public MapValueHolder(Class<?> type) {
			super(type);
			this.entries = new ArrayList<MapEntryValueHolder<K,T>>();
		}

		public void visit(ValueHolderVisitor v) {
			v.caseMapObject(this);
		}		

		@Override
		public <R,A> R visit(ValueHolderVisitor2<R,A> v, A a) {
			return v.caseMapObject(this, a);
		}
		
		public Collection<MapEntryValueHolder<K,T>> getEntries() {
			return entries;
		}

//		public void setEntries(Collection<MapEntryValueHolder<K,T>> p) {
//			this.entries = p;
//		}

		public void putEntry(AbstractObjectValueHolder/*<K>*/ key, AbstractObjectValueHolder/*<T>*/ value) {
			MapEntryValueHolder<K,T> e = new MapEntryValueHolder<K,T>(key, value);
			this.entries.add(e);
		}

	}

}

