package com.google.code.joto.procesors;

import static com.google.code.joto.ReverseEngineerData.concat;
import static com.google.code.joto.ReverseEngineerData.setVarWithValueEnd;
import static com.google.code.joto.ReverseEngineerData.setVarWithValueStart;
import static com.google.code.joto.ReverseEngineerData.writeCreatorFooter;
import static com.google.code.joto.ReverseEngineerData.writeCreatorHeader;
import static com.google.code.joto.ReverseEngineerData.writeDeclarationAndInitializationOfVariable;
import static com.google.code.joto.ReverseEngineerData.writeReturnStatement;
import static com.google.code.joto.ReverseEngineerHelper.getMethodForSetting;
import static com.google.code.joto.ReverseEngineerHelper.getTypeAsString;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;
import com.google.code.joto.exportable.ReverseEngineerReflectionUtil;

public class DefaultObjectProcessor
    implements CustomProcessor
{

    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess != null;
    }

    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
        throws IllegalAccessException
    {
        if ( sharedData.objectsAlreadyProcessed.contains( objectToProcess ) )
        {
            concat( sharedData, "(", getTypeAsString( objectToProcess ), ") getInstance( ", System
                .identityHashCode( objectToProcess ), ")" );
            concat( sharedData, " /* @", Integer.toHexString( System.identityHashCode( objectToProcess ) ), " */ " );
            return;
        }
        sharedData.objectsAlreadyProcessed.add( objectToProcess );
        writeCreatorHeader( sharedData, objectToProcess );

        {
            String variableName = "obj";
            writeDeclarationAndInitializationOfVariable( sharedData, variableName, objectToProcess );
            concat( sharedData, "registerInstance( ", System.identityHashCode( objectToProcess ), ", ", variableName,
                    "); " );
            concat( sharedData, " /* @", Integer.toHexString( System.identityHashCode( objectToProcess ) ), " */\n" );

            Class<? extends Object> clazz = objectToProcess.getClass();
            while ( clazz != null )
            {
                Field[] fields = clazz.getDeclaredFields();
                for ( Field field : fields )
                {
                    if ( dontHaveToProcessField( field ) )
                    {
                        continue;
                    }
                    Object valueOfFieldToProcess = ReverseEngineerReflectionUtil.getFieldValue( objectToProcess, field );
                    if ( valueOfFieldToProcess == null )
                    {
                        continue;
                    }

                    if ( Collection.class.isAssignableFrom( field.getType() ) )
                    {
                        getCodeForEnsuringFieldIsNotNull( sharedData, variableName, field, valueOfFieldToProcess );

                        concat( sharedData, "((Collection) ", getCodeForGettingField( variableName, field ),
                                ").addAll(" );
                        callback.processThis( valueOfFieldToProcess );
                        concat( sharedData, ");\n" );
                    }
                    else if ( Map.class.isAssignableFrom( field.getType() ) )
                    {
                        getCodeForEnsuringFieldIsNotNull( sharedData, variableName, field, valueOfFieldToProcess );
                        concat( sharedData, "((Map) ", getCodeForGettingField( variableName, field ), ").putAll(" );
                        callback.processThis( valueOfFieldToProcess );
                        concat( sharedData, ");\n" );
                    }
                    else if ( field.getType().isArray() )
                    {
                        concat( sharedData, getCodeForSettingFieldStart( variableName, field ) );
                        callback.processThis( valueOfFieldToProcess );
                        concat( sharedData, getCodeForSettingFieldEnd(), ";\n" );
                    }
                    else
                    {
                        if ( clazz.isAnonymousClass() )
                        {
                            setVarWithValueStart( sharedData, variableName, field );
                            callback.processThis( valueOfFieldToProcess );
                            setVarWithValueEnd( sharedData );
                        }
                        else if ( getMethodForSetting( field ) != null )
                        {
                            setVarWithValueStart( sharedData, variableName, getMethodForSetting( field ).getName() );
                            callback.processThis( valueOfFieldToProcess );
                            setVarWithValueEnd( sharedData );
                        }
                        else
                        {
                            concat( sharedData, "setFieldValue( ", variableName, ", getFieldForClass( ", clazz
                                .getSimpleName(), ".class, \"", field.getName(), "\"), " );
                            callback.processThis( valueOfFieldToProcess );
                            concat( sharedData, ");\n" );
                        }

                    }
                }
                clazz = clazz.getSuperclass();
            }
            writeReturnStatement( sharedData, variableName );
        }

        writeCreatorFooter( sharedData );

    }

    static boolean dontHaveToProcessField( Field field )
    {
        return Modifier.isStatic( field.getModifiers() ) || field.isSynthetic();
    }

    private void getCodeForEnsuringFieldIsNotNull( ReverseEngineerData sharedData,
                                                   String variableName, Field field, Object valueOfFieldToProcess )
    {
        concat( sharedData, "if (", getCodeForGettingField( variableName, field ), " == null) {\n" );
        concat( sharedData, getCodeForSettingFieldStart( variableName, field ) );
        concat( sharedData, "new ", getTypeAsString( valueOfFieldToProcess ), "()" );
        concat( sharedData, getCodeForSettingFieldEnd(), ";\n" );
        concat( sharedData, "\n}\n" );
    }

    private String getCodeForGettingField( String originationVariable, Field field )
    {
        return "getFieldValue( " + originationVariable + ", getFieldForClass("
            + field.getDeclaringClass().getSimpleName() + ".class, \"" + field.getName() + "\"))";

    }

    private String getCodeForSettingFieldStart( String destinationVariable, Field field )
    {
        return "setFieldValue( " + destinationVariable + ", getFieldForClass("
            + field.getDeclaringClass().getSimpleName() + ".class, \"" + field.getName() + "\"), ";

    }

    private String getCodeForSettingFieldEnd()
    {
        return ")";
    }

}