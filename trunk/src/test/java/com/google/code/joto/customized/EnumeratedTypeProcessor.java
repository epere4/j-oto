package com.google.code.joto.customized;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerObjectWorkerParameter;
import com.google.code.joto.datatype.EnumeratedType;

public class EnumeratedTypeProcessor
    implements CustomProcessor
{

    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess instanceof EnumeratedType;
    }

    public void processThis( Object objectToProcess, ReverseEngineerObjectWorkerParameter sharedData, int depthLevel,
                             ProcessMoreCallback callback )
    {
        EnumeratedType enumeratedType = (EnumeratedType) objectToProcess;
        String fieldNameResolved = enumeratedType.toString();
        // This makes the algorithm to work on classes
        // like
        // AgentAccess, where the toString does
        // not match the value used for constructing
        // each of
        // the enums.
        Field[] allEnumVariables = enumeratedType.getClass().getDeclaredFields();
        for ( Field anEnumVariable : allEnumVariables )
        {
            boolean oldAnEnumVariableAccessible = anEnumVariable.isAccessible();
            try
            {
                anEnumVariable.setAccessible( true );
                Object valueOfCurrentEnumVariable = anEnumVariable.get( null );
                if ( Modifier.isStatic( anEnumVariable.getModifiers() )
                    && valueOfCurrentEnumVariable.toString().equals( enumeratedType.toString() ) )
                {
                    fieldNameResolved = anEnumVariable.getName();
                    break;
                }
            }
            catch ( IllegalAccessException e )
            {
                throw new RuntimeException( e );
            }
            finally
            {
                anEnumVariable.setAccessible( oldAnEnumVariableAccessible );
            }
        }

        sharedData.sb.append( enumeratedType.getClass().getSimpleName() ).append( "." ).append( fieldNameResolved );
    }

}
