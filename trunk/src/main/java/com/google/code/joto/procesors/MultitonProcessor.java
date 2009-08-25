/**
 * 
 */
package com.google.code.joto.procesors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;
import com.google.code.joto.exportable.ReverseEngineerReflectionUtil;

/**
 * @author epere4
 * @author liliana.nu
 */
public class MultitonProcessor implements CustomProcessor {

	/**
	 * @see com.google.code.joto.CustomProcessor#canProcessThis(java.lang.Object)
	 */
	public boolean canProcessThis(Object objectToProcess) {
		return objectToProcess != null && hasFieldOrMethodForThisInstance(objectToProcess);
	}

	/**
	 * @see com.google.code.joto.CustomProcessor#processThis(java.lang.Object, com.google.code.joto.ReverseEngineerData, int, com.google.code.joto.ProcessMoreCallback)
	 */
	public void processThis(Object objectToProcess,
			ReverseEngineerData sharedData, int depthLevel,
			ProcessMoreCallback callback) throws Exception {
		// TODO Auto-generated method stub

	}

	
	private boolean hasFieldOrMethodForThisInstance(Object objectToProcess) {
        Class<? extends Object> clazz = objectToProcess.getClass();
        while ( clazz != null )
        {
            Field[] fields = clazz.getDeclaredFields();
            Field candidateField;
            for ( Field field : fields )
            {
            	if (Modifier.isStatic(field.getModifiers())) {
            		if (ReverseEngineerReflectionUtil.getFieldValue(null, field) == objectToProcess) {
            			candidateField = field;
            			break;
            		}
            	}
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
				
			}
        }
		return false;
	}
}
