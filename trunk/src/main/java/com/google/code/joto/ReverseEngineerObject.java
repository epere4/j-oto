/**
 * 
 */
package com.google.code.joto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import com.google.code.joto.exportable.Creator;
import com.google.code.joto.exportable.ReverseEngineerReflectionUtil;

/**
 * @author SG0203967
 */
public class ReverseEngineerObject
{

    public ReverseEngineerObjectResponse generateCode( Object objectToProcess,
                                                       List<? extends CustomProcessor> listOfUserSuppliedProcessors )
    {
        List<CustomProcessor> listOfProcessors = makeListOfDefaultProcessors();
        if ( listOfUserSuppliedProcessors != null )
        {
            listOfProcessors.addAll( 0, listOfUserSuppliedProcessors );
        }

        ReverseEngineerObjectWorkerParameter sharedData = new ReverseEngineerObjectWorkerParameter( listOfProcessors );

        addSomeClassesToImportAlways( sharedData.classesToImport );

        generateCodeRecursively( objectToProcess, sharedData, 0 );

        String imports = makeImports( sharedData.classesToImport );
        String code = sharedData.sb.toString();

        return new ReverseEngineerObjectResponse( imports, code );

    }

    private void addSomeClassesToImportAlways( Set<Class> classesToImport )
    {
        classesToImport.add( List.class );
        classesToImport.add( Set.class );
        classesToImport.add( Map.class );
        classesToImport.add( Creator.class );
        classesToImport.add( Collection.class );

    }

    private LinkedList<CustomProcessor> makeListOfDefaultProcessors()
    {
        LinkedList<CustomProcessor> listOfDefaultProcessors = new LinkedList<CustomProcessor>();
        listOfDefaultProcessors.add( new CollectionProcessor() );
        listOfDefaultProcessors.add( new MapProcessor() );
        listOfDefaultProcessors.add( new ArrayProcessor() );
        listOfDefaultProcessors.add( new EnumProcessor() );
        listOfDefaultProcessors.add( new DefaultObjectProcessor() );
        return listOfDefaultProcessors;
    }

    private void generateCodeRecursively( Object objectToProcess, ReverseEngineerObjectWorkerParameter sharedData,
                                          int depthLevel )
    {
        try
        {
            if ( objectToProcess == null || isPrimitiveOrBaseClass( objectToProcess.getClass() ) )
            {
                sharedData.sb.append( compilerValueOf( objectToProcess, sharedData.classesToImport ) );
                return;
            }

            sharedData.classesToImport.add( objectToProcess.getClass() );
            for ( CustomProcessor customProc : sharedData.listOfProcessors )
            {
                if ( customProc.canProcessThis( objectToProcess ) )
                {
                    customProc.processThis( objectToProcess, sharedData, depthLevel,
                                            new ProcessMoreCallbackImpl( sharedData, depthLevel + 1 ) );
                    break;
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    public static String makeImports( Set<Class> classesToImport )
    {
        StringBuilder sb = new StringBuilder();
        for ( Class clazz : classesToImport )
        {
            if ( !clazz.isArray() && !clazz.isAnonymousClass() && !clazz.isLocalClass() && !clazz.isMemberClass() )
            {
                concat( sb, "import ", clazz.getName(), ";\n" );
            }
        }
        concat( sb, "import static ", ReverseEngineerReflectionUtil.class.getName(), ".*;\n" );
        return sb.toString();
    }

    public static void concat( ReverseEngineerObjectWorkerParameter sharedData, Object... partsToConcat )
    {
        concat( sharedData.sb, partsToConcat );
    }

    static void concat( StringBuilder sb, Object... partsToConcat )
    {
        for ( Object part : partsToConcat )
        {
            sb.append( part );
        }
    }

    static boolean dontHaveToProcessField( Field field )
    {
        return Modifier.isStatic( field.getModifiers() ) || field.isSynthetic();
    }

    private static boolean isPrimitiveOrBaseClass( Class clazz )
    {
        return clazz.isPrimitive() || Boolean.class.isAssignableFrom( clazz ) || String.class.isAssignableFrom( clazz )
            || Number.class.isAssignableFrom( clazz ) || Date.class.isAssignableFrom( clazz );
    }

    private static Object compilerValueOf( Object objectToProcess, Set<Class> classesToImport )
    {
        if ( objectToProcess instanceof CharSequence )
        {
            return "\"" + String.valueOf( objectToProcess ) + "\"";
        }
        else if ( objectToProcess instanceof Date )
        {
            classesToImport.add( Date.class );
            return "new Date(" + ( (Date) objectToProcess ).getTime() + "L) /* " + objectToProcess + "*/ ";
        }
        else
        {
            return String.valueOf( objectToProcess );
        }

    }

    /** properyNames */
    private static final Map<String, Integer> properyNames = new HashMap<String, Integer>();

    /**
     * Similar to {@link #getVarNameForClass(Class)}, but for strings that represent property names.
     */
    static String getVarNameForProperty( String propertyName )
    {
        Integer currentCounter = properyNames.get( propertyName );
        if ( currentCounter == null )
        {
            currentCounter = 1;
        }
        properyNames.put( propertyName, currentCounter + 1 );
        return getFirstLetterInLowerCase( propertyName ) + currentCounter;
    }

    /** classes */
    private static final Map<Class, Integer> classes = new HashMap<Class, Integer>();

    /**
     * A unique variable name after each invocation. For example, for the java.lang.String class it
     * will return (after each sucessive invocation) string1, string2, string3, etc.
     */
    static String getVarNameForClass( Class clazz )
    {
        Integer currentCounter = classes.get( clazz );
        if ( currentCounter == null )
        {
            currentCounter = 1;
        }
        classes.put( clazz, currentCounter + 1 );
        return getFirstLetterInLowerCase( clazz.getSimpleName() ) + currentCounter;
    }

    private static String getFirstLetterInLowerCase( String str )
    {
        if ( str == null || str.length() == 0 )
        {
            return str;
        }
        else
        {
            return str.substring( 0, 1 ).toLowerCase() + str.substring( 1 );
        }
    }

    static String getFirstLetterInUpperCase( String str )
    {
        if ( str == null || str.length() == 0 )
        {
            return str;
        }
        else
        {
            return str.substring( 0, 1 ).toUpperCase() + str.substring( 1 );
        }
    }

    static String writeDeclarationAndInitializationOfVariable( ReverseEngineerObjectWorkerParameter sharedData,
                                                               String variableName,
                                                               Object objectToProcess )
    {
        String variableType = getTypeAsString( objectToProcess );
        concat( sharedData, variableType, " ", variableName, " = new ", variableType, "();\n" );
        return variableName;
    }

    static void writeReturnStatement( ReverseEngineerObjectWorkerParameter sharedData, String variableName )
    {
        concat( sharedData, "return ", variableName, ";\n" );
    }

    static String getTypeAsString( Object object )
    {
        if ( object == null )
        {
            throw new IllegalArgumentException( "valueObject cannot be null" );
        }

        final Class valueObjectType = getType( object );
        return valueObjectType.getSimpleName().length() == 0 ? "Object" : valueObjectType.getSimpleName();
    }

    private static final Set<Class<?>> instantiableClasses = new HashSet<Class<?>>( Arrays.asList( HashMap.class,
                                                                                                   Hashtable.class,
                                                                                                   ArrayList.class,
                                                                                                   LinkedList.class,
                                                                                                   HashSet.class,
                                                                                                   Vector.class,
                                                                                                   TreeMap.class,
                                                                                                   LinkedHashMap.class,
                                                                                                   LinkedHashSet.class ) );

    static Class getType( Object object )
    {
        if ( object == null )
        {
            throw new IllegalArgumentException( "valueObject cannot be null" );
        }

        final Class valueObjecType;
        if ( instantiableClasses.contains( object.getClass() ) )
        {
            valueObjecType = object.getClass();
        }
        else if ( object instanceof Collection )
        {
            if ( object instanceof List )
            {
                valueObjecType = ArrayList.class;
            }
            else if ( object instanceof Set )
            {
                valueObjecType = HashSet.class;
            }
            else
            {
                valueObjecType = object.getClass();
            }
        }
        else if ( object instanceof Map )
        {
            valueObjecType = HashMap.class;
        }
        else
        {
            valueObjecType = object.getClass();
        }
        return valueObjecType;
    }

    static void writeCreatorFooter( ReverseEngineerObjectWorkerParameter sharedData )
    {
        concat( sharedData, "}}.create()" );
    }

    static void writeCreatorHeader( ReverseEngineerObjectWorkerParameter sharedData, Object objectToProcess )
    {
        concat( sharedData, "new Creator<", getTypeAsString( objectToProcess ), ">(){ \npublic ",
                getTypeAsString( objectToProcess ), " create() {\n" );
    }

    static Method getMethodForSetting( Field field )
    {
        return getMethodForFieldWithPrefix( field, "set" );
    }

    static Method getMethodForAdding( Field field )
    {
        return getMethodForFieldWithPrefix( field, "add" );
    }

    static Method getMethodForFieldWithPrefix( Field field, String methodPrefix )
    {
        Method[] declaredMethods = field.getDeclaringClass().getDeclaredMethods();

        int maxLengthThatMatched = -1;
        Method candidate = null;
        for ( Method method : declaredMethods )
        {
            String fieldCleaned = removeDashesAndFirstCharInUpperCase( field.getName() );
            String methodCleaned = getMethodNameWithoutPrefix( method, methodPrefix );
            if ( method.getName().startsWith( methodPrefix ) && fieldCleaned.contains( methodCleaned ) )
            {
                if ( methodCleaned.length() > maxLengthThatMatched )
                {
                    candidate = method;
                    maxLengthThatMatched = methodCleaned.length();
                }
            }
        }
        return candidate;
    }

    static String getMethodNameWithoutPrefix( Method method, String methodPrefix )
    {
        return method.getName().length() >= methodPrefix.length() ? method.getName().substring( methodPrefix.length() )
                                                                 : method.getName();
    }

    static void setVarWithValueStart( ReverseEngineerObjectWorkerParameter sharedData, String variableName, Field field )
    {
        String propertyName = removeDashesAndFirstCharInUpperCase( field.getName() );
        concat( sharedData, variableName, ".set", propertyName, "(" );
    }

    static void setVarWithValueEnd( ReverseEngineerObjectWorkerParameter sharedData )
    {
        concat( sharedData, ");\n" );
    }

    static void setVarWithValue( StringBuilder sb, String varName, Field field, String valueToSet )
    {
        String propertyName = removeDashesAndFirstCharInUpperCase( field.getName() );
        concat( sb, varName, ".set", propertyName, "(", valueToSet, ");\n" );
    }

    static String removeDashesAndFirstCharInUpperCase( String name )
    {
        if ( name.startsWith( "_" ) )
        {
            return ( getFirstLetterInUpperCase( name.subSequence( 1, name.length() ).toString() ) );
        }
        else
        {
            return getFirstLetterInUpperCase( name ).toString();
        }
    }

    class ProcessMoreCallbackImpl
        implements ProcessMoreCallback
    {

        public ProcessMoreCallbackImpl( ReverseEngineerObjectWorkerParameter sharedData, int depthLevel )
        {
            this.sharedData = sharedData;
            this.depthLevel = depthLevel;
        }

        final ReverseEngineerObjectWorkerParameter sharedData;

        final int depthLevel;

        public void processThis( Object object )
        {
            ReverseEngineerObject.this.generateCodeRecursively( object, sharedData, depthLevel + 1 );
        }

    }

    class EnumProcessor
        implements CustomProcessor
    {

        public boolean canProcessThis( Object objectToProcess )
        {
            return objectToProcess instanceof Enum;
        }

        public void processThis( Object objectToProcess, ReverseEngineerObjectWorkerParameter sharedData,
                                 int depthLevel, ProcessMoreCallback callback )
            throws Exception
        {
            Enum enumToProcess = (Enum) objectToProcess;
            concat( sharedData, enumToProcess.getDeclaringClass().getSimpleName(), ".", enumToProcess.name() );
        }

    }

    class CollectionProcessor
        implements CustomProcessor
    {

        public boolean canProcessThis( Object objectToProcess )
        {
            return objectToProcess instanceof Collection;
        }

        public void processThis( Object objectToProcess, ReverseEngineerObjectWorkerParameter sharedData,
                                 int depthLevel, ProcessMoreCallback callback )
        {
            Collection collectionToProcess = (Collection) objectToProcess;
            writeCreatorHeader( sharedData, objectToProcess );

            {
                String variableName = "col";

                writeDeclarationAndInitializationOfVariable( sharedData, variableName, collectionToProcess );

                for ( Object element : collectionToProcess )
                {
                    concat( sharedData, variableName, ".add(" );
                    callback.processThis( element );
                    concat( sharedData, ");\n" );
                }

                writeReturnStatement( sharedData, variableName );
            }

            writeCreatorFooter( sharedData );
        }

    }

    class MapProcessor
        implements CustomProcessor
    {

        public boolean canProcessThis( Object objectToProcess )
        {
            return objectToProcess instanceof Map;
        }

        public void processThis( Object objectToProcess, ReverseEngineerObjectWorkerParameter sharedData,
                                 int depthLevel, ProcessMoreCallback callback )
        {
            Map mapToProcess = (Map) objectToProcess;
            writeCreatorHeader( sharedData, objectToProcess );

            {
                String variableName = "map";

                writeDeclarationAndInitializationOfVariable( sharedData, variableName, mapToProcess );

                for ( Entry entry : (Set<Entry>) mapToProcess.entrySet() )
                {
                    concat( sharedData, variableName, ".put(" );
                    callback.processThis( entry.getKey() );
                    concat( sharedData, ", " );
                    callback.processThis( entry.getValue() );
                    concat( sharedData, ");\n" );
                }

                writeReturnStatement( sharedData, variableName );
            }

            writeCreatorFooter( sharedData );

        }
    }

    class ArrayProcessor
        implements CustomProcessor
    {

        public boolean canProcessThis( Object objectToProcess )
        {
            return objectToProcess != null && objectToProcess.getClass().isArray();
        }

        public void processThis( Object objectToProcess, ReverseEngineerObjectWorkerParameter sharedData,
                                 int depthLevel, ProcessMoreCallback callback )
        {
            Object[] arrayToProcess = (Object[]) objectToProcess;
            writeCreatorHeader( sharedData, objectToProcess );

            {
                String variableName = "array";
                String arrayType = arrayToProcess.getClass().getComponentType().getSimpleName();
                concat( sharedData.sb, arrayType, "[] ", variableName, " = new ", arrayType, "[",
                        arrayToProcess.length, "];\n" );

                for ( int i = 0; i < arrayToProcess.length; i++ )
                {
                    concat( sharedData.sb, variableName + "[" + i + "] = " );
                    callback.processThis( arrayToProcess[i] );
                    concat( sharedData.sb, ";\n" );
                }
                writeReturnStatement( sharedData, variableName );
            }
            writeCreatorFooter( sharedData );
        }
    }

    class DefaultObjectProcessor
        implements CustomProcessor
    {

        public boolean canProcessThis( Object objectToProcess )
        {
            return true;
        }

        public void processThis( Object objectToProcess, ReverseEngineerObjectWorkerParameter sharedData,
                                 int depthLevel, ProcessMoreCallback callback )
            throws IllegalAccessException
        {
            if ( sharedData.objectsAlreadyProcessed.contains( objectToProcess ) )
            {
                concat( sharedData.sb, "getInstance( ", System
                    .identityHashCode( objectToProcess ), ")" );
                concat( sharedData.sb, " /* @", Integer.toHexString( System.identityHashCode( objectToProcess ) ),
                        " */ " );
                return;
            }
            sharedData.objectsAlreadyProcessed.add( objectToProcess );
            writeCreatorHeader( sharedData, objectToProcess );

            {
                String variableName = "obj";
                writeDeclarationAndInitializationOfVariable( sharedData, variableName, objectToProcess );
                concat( sharedData, "registerInstance( ", System
                    .identityHashCode( objectToProcess ), ", ", variableName, "); " );
                concat( sharedData.sb, " /* @", Integer.toHexString( System.identityHashCode( objectToProcess ) ),
                        " */\n" );

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
                        Object valueOfFieldToProcess = ReverseEngineerReflectionUtil.getFieldValue( objectToProcess,
                                                                                                    field );
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
                            if ( clazz.isAnonymousClass() || getMethodForSetting( field ) != null )
                            {
                                setVarWithValueStart( sharedData, variableName, field );
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

        private void getCodeForEnsuringFieldIsNotNull( ReverseEngineerObjectWorkerParameter sharedData,
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
}

