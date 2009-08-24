package com.google.code.joto;

import static com.google.code.joto.ReverseEngineerHelper.getBestConstructorInvocationCodeForObject;
import static com.google.code.joto.ReverseEngineerHelper.getTypeAsString;
import static com.google.code.joto.ReverseEngineerHelper.removeDashesAndFirstCharInUpperCase;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author SG0203967
 */
public class ReverseEngineerData
{

    public ReverseEngineerData()
    {
        this( null, null, null );
    }

    public ReverseEngineerData( final Set<Class> classesToImport )
    {
        this( classesToImport, null, null );
    }

    public ReverseEngineerData( final Set<Class> classesToImport, final StringBuilder sb,
                                                 List<CustomProcessor> listOfProcessors )
    {
        this.classesToImport = classesToImport == null ? new HashSet<Class>() : classesToImport;
        this.sb = sb == null ? new StringBuilder() : sb;
        this.listOfProcessors = listOfProcessors == null ? Collections.EMPTY_LIST : listOfProcessors;

    }

    public ReverseEngineerData( List<CustomProcessor> listOfProcessors )
    {
        this( null, null, listOfProcessors );
    }

    /** objectsAlreadyProcessed */
    public final Set<Object> objectsAlreadyProcessed = new HashSet<Object>();

    /** classesAlreadyDeclared */
    public final Set<String> classesAlreadyDeclared = new HashSet<String>();

    /** classesToImport */
    private final Set<Class> classesToImport;

    /** sb */
    public final StringBuilder sb;

    /** listOfProcessors */
    public final List<CustomProcessor> listOfProcessors;

    public void concat( Object... partsToConcat )
    {
        concat( this, partsToConcat );
    }

    public static void concat( ReverseEngineerData sharedData, Object... partsToConcat )
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

    public static void writeCreatorFooter( ReverseEngineerData sharedData )
    {
        concat( sharedData, "}}.create()" );
    }

    public static void writeCreatorHeader( ReverseEngineerData sharedData, Object objectToProcess )
    {
        concat( sharedData, "new Creator<", getTypeAsString( objectToProcess ), ">(){ \npublic ",
                getTypeAsString( objectToProcess ), " create() {\n" );
    }

    public static String writeDeclarationAndInitializationOfVariable( ReverseEngineerData sharedData,
                                                               String variableName, Object objectToProcess )
    {

        String variableType = getTypeAsString( objectToProcess );
        concat( sharedData, variableType, " ", variableName, " = ",
                getBestConstructorInvocationCodeForObject( objectToProcess ), ";\n" );
        return variableName;
    }

    public static void writeReturnStatement( ReverseEngineerData sharedData, String variableName )
    {
        concat( sharedData, "return ", variableName, ";\n" );
    }

    public static void setVarWithValueStart( ReverseEngineerData sharedData, String variableName,
                                             Field field )
    {
        String propertyName = removeDashesAndFirstCharInUpperCase( field.getName() );
        String setterName = "set" + propertyName;

        setVarWithValueStart( sharedData, variableName, setterName );
    }

    public static void setVarWithValueStart( ReverseEngineerData sharedData, String variableName,
                                              String setterMethodName )
    {
        concat( sharedData, variableName, ".", setterMethodName, "(" );
    }

    public static void setVarWithValueEnd( ReverseEngineerData sharedData )
    {
        concat( sharedData, ");\n" );
    }

    public static void setVarWithValue( StringBuilder sb, String varName, Field field, String valueToSet )
    {
        String propertyName = removeDashesAndFirstCharInUpperCase( field.getName() );
        concat( sb, varName, ".set", propertyName, "(", valueToSet, ");\n" );
    }

    /**
     * @param clazz
     */
    public void addClassToImport( Class clazz )
    {
        classesToImport.add( clazz );
    }

    /**
     * @param classes
     */
    public void addClassesToImport( Set<Class> classes )
    {
        classesToImport.addAll( classes );
    }

    /**
     * @return the classesToImport
     */
    public Set<Class> getClassesToImport()
    {
        return Collections.unmodifiableSet( classesToImport );
    }

}
