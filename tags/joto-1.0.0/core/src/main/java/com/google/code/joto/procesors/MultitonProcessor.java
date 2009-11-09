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
import com.google.code.joto.ReverseEngineerHelper;
import com.google.code.joto.exportable.ReverseEngineerReflectionUtil;

/**
 * @author epere4
 * @author liliana.nu
 */
public class MultitonProcessor
    implements CustomProcessor
{

    /**
     * @see com.google.code.joto.CustomProcessor#canProcessThis(java.lang.Object)
     */
    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess != null && hasFieldOrMethodForThisInstance( objectToProcess );
    }

    /**
     * @see com.google.code.joto.CustomProcessor#processThis(java.lang.Object,
     *      com.google.code.joto.ReverseEngineerData, int, com.google.code.joto.ProcessMoreCallback)
     */
    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
        throws Exception
    {
        FieldAndMethodCandidates candidates = getFieldAndMethodCandidatesForObject( objectToProcess );

        sharedData.concat( ReverseEngineerHelper.getTypeAsString( objectToProcess ), "." );
        if (candidates.candidateMethod != null) {
            sharedData.concat( candidates.candidateMethod.getName(), "()" );
        }
        else
        {
            sharedData.concat( candidates.candidateField.getName() );
        }
    }

    private boolean hasFieldOrMethodForThisInstance( Object objectToProcess )
    {
        FieldAndMethodCandidates candidates = getFieldAndMethodCandidatesForObject( objectToProcess );

        return candidates.candidateField != null || candidates.candidateMethod != null;
    }

    private FieldAndMethodCandidates getFieldAndMethodCandidatesForObject( Object objectToProcess )
    {
        FieldAndMethodCandidates candidates = new FieldAndMethodCandidates();

        Class<? extends Object> clazz = objectToProcess.getClass();
        while ( clazz != null && ( candidates.candidateField == null && candidates.candidateMethod == null ) )
        {
            if ( candidates.candidateField == null )
            {
                candidates.candidateField = getCandidateFieldForClass( clazz, objectToProcess );
            }
            if ( candidates.candidateMethod == null )
            {
                candidates.candidateMethod = getCandidateMethodForClass( clazz, objectToProcess );
            }
            clazz = clazz.getSuperclass();
        }
        return candidates;
    }

    private Method getCandidateMethodForClass( Class<? extends Object> clazz, Object objectToProcess )
    {
        Method[] methods = clazz.getDeclaredMethods();
        for ( Method method : methods )
        {
            if ( Modifier.isPublic( method.getModifiers() ) && Modifier.isStatic( method.getModifiers() )
                && hasNoParameters( method ) && returnTypeInstanceOfObjectToProcessType( method, objectToProcess ) )
            {
                if ( ReverseEngineerReflectionUtil.invokeMethod( method, null ) == objectToProcess )
                {
                    return method;
                }
            }
        }
        return null;
    }

    private Field getCandidateFieldForClass( Class<? extends Object> clazz, Object objectToProcess )
    {
        Field[] fields = clazz.getDeclaredFields();
        for ( Field field : fields )
        {
            if ( Modifier.isStatic( field.getModifiers() ) )
            {
                if ( ReverseEngineerReflectionUtil.getFieldValue( null, field ) == objectToProcess )
                {
                    return field;
                }
            }
        }
        return null;
    }

    private boolean returnTypeInstanceOfObjectToProcessType( Method method, Object objectToProcess )
    {
        return objectToProcess.getClass().isAssignableFrom( method.getReturnType() );
    }

    private boolean hasNoParameters( Method method )
    {
        return method.getParameterTypes().length == 0;
    }

    private static class FieldAndMethodCandidates
    {
        public Field candidateField;

        public Method candidateMethod;
    }
}
