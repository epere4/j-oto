package com.google.code.joto.ast.valueholder.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.CollectionValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.FieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ImmutableObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveFieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefFieldValueHolder;
import com.google.code.joto.reflect.ReflectUtils;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;


/**
 *
 */
public class ObjectToValueHolderBuilder {

	private Map<Object,AbstractObjectValueHolder> identityMap =
		new IdentityHashMap<Object,AbstractObjectValueHolder>();

	
	// -------------------------------------------------------------------------

	public ObjectToValueHolderBuilder() {
		super();
	
		
	}
	
	// -------------------------------------------------------------------------
	
	public Map<Object,AbstractObjectValueHolder> getResultMap() {
		return identityMap;
	}
	
	public AbstractObjectValueHolder buildValue(Object obj) {
		if (obj == null) {
			return null; // NullValueHolder.getInstance();
		} if (obj.getClass().isArray()) {
			Class compType = obj.getClass().getComponentType();
			if (compType.isPrimitive()) {
				return casePrimitiveArray((Object[]) obj);
			} else {
				return caseRefArray((Object[]) obj);
			}
		} if (obj instanceof Collection) {
			return caseCollection((Collection) obj);
		} else if (obj instanceof Map) {
			return caseMap((Map) obj);
		} else if (obj instanceof String) {
			return caseImmutableObject(obj);
		} else {
			return caseObject(obj);
		}
	}



	protected ObjectValueHolder caseObject(Object obj) {
		ObjectValueHolder res = (ObjectValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new ObjectValueHolder(obj.getClass());
			identityMap.put(obj, res);

			caseObject(obj, res);
		}
		return res;
	}

	protected ImmutableObjectValueHolder caseImmutableObject(Object obj) {
		ImmutableObjectValueHolder res = (ImmutableObjectValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new ImmutableObjectValueHolder(obj);
			identityMap.put(obj, res);
		}
		return res;
	}

	protected PrimitiveArrayValueHolder casePrimitiveArray(Object obj) {
		PrimitiveArrayValueHolder res = (PrimitiveArrayValueHolder) identityMap.get(obj);
		if (res == null) {
			int len = Array.getLength(obj);
			res = new PrimitiveArrayValueHolder(obj.getClass(), len);
			identityMap.put(obj, res);

			casePrimitiveArray(obj, res);
		}
		return res;
	}

	protected RefArrayValueHolder caseRefArray(Object[] obj) {
		if (obj == null) {
			return null;
		}
		RefArrayValueHolder res = (RefArrayValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new RefArrayValueHolder(obj.getClass(), obj.length);
			identityMap.put(obj, res);

			caseRefArray(obj, res);
		}
		return res;
	}


	protected CollectionValueHolder caseCollection(Collection obj) {
		if (obj == null) {
			return null;
		}
		CollectionValueHolder res = (CollectionValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new CollectionValueHolder(); // obj.getClass());
			identityMap.put(obj, res);

			caseCollection(obj, res);
		}
		return res;
	}

	protected MapValueHolder caseMap(Map obj) {
		MapValueHolder res = (MapValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new MapValueHolder();
			identityMap.put(obj, res);

			caseMap(obj, res);
		}
		return res;
	}

	// -------------------------------------------------------------------------
	
	protected void caseObject(final Object obj, final ObjectValueHolder node) {
		// BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		// final Class<?> objClass = obj.getClass();
		final ReflectionProvider reflectionProvider = ReflectUtils.getReflectionProvider();
		
		reflectionProvider.visitSerializableFields(obj, new ReflectionProvider.Visitor() {
			public void visit(String fieldName, Class fieldType, 
					Class definedIn, Object value
					) {
				Field field = reflectionProvider.getField(definedIn, fieldName);
				FieldValueHolder fvh = node.getFieldValue(field);
				if (fieldType.isPrimitive()) {
					PrimitiveFieldValueHolder fvh2 = 
						(PrimitiveFieldValueHolder) fvh;
					fvh2.setValue(value);
				} else {
					RefFieldValueHolder fvh2 =
						(RefFieldValueHolder) fvh;
					//.. recurse
					AbstractObjectValueHolder valueHolder = buildValue(value);
					fvh2.setTo(valueHolder);
				}
			}
		});
	}

	protected void casePrimitiveArray(final Object obj, final PrimitiveArrayValueHolder valueHolder) {
		int len = Array.getLength(obj);
		Class compType = obj.getClass().getComponentType();
		
		if (compType == Boolean.TYPE) {
			boolean[] array = (boolean[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Byte.TYPE) {
			byte[] array = (byte[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Character.TYPE) {
			char[] array = (char[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Short.TYPE) {
			short[] array = (short[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Integer.TYPE) {
			int[] array = (int[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Long.TYPE) {
			long[] array = (long[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Float.TYPE) {
			float[] array = (float[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Double.TYPE) {
			double[] array = (double[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else {
			throw new RuntimeException("unrecognized primitive array " + compType);
		}
	}

	protected void caseRefArray(final Object[] obj, final RefArrayValueHolder valueHolder) {
		int len = obj.length;
		for (int i = 0; i < len; i++) {
			AbstractObjectValueHolder eltVH = buildValue(obj[i]);
			valueHolder.setValueAt(i, eltVH);
		}
	}

	protected void caseCollection(Collection obj, CollectionValueHolder valueHolder) {
		for(Object objElt : obj) {
			AbstractObjectValueHolder eltVH = buildValue(objElt);
			valueHolder.addRefElt(eltVH);
		}
	}

	protected <K,T> void caseMap(Map<K,T> obj, MapValueHolder valueHolder) {
		for(Map.Entry entry : obj.entrySet()) {
			AbstractObjectValueHolder keyVH = buildValue(entry.getKey());
			AbstractObjectValueHolder valueVH = buildValue(entry.getValue());
			valueHolder.putEntry(keyVH, valueVH);
		}
	}

}
