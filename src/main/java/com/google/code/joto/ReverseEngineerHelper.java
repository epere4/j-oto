package com.google.code.joto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

public class ReverseEngineerHelper
{
    public static String getTypeAsString( Object object )
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

    public static Class getType( Object object )
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

    public static Method getMethodForSetting( Field field )
    {
        return getMethodForFieldWithPrefix( field, "set" );
    }

    public static Method getMethodForAdding( Field field )
    {
        return getMethodForFieldWithPrefix( field, "add" );
    }

    public static Method getMethodForFieldWithPrefix( Field field, String methodPrefix )
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

    public static String removeDashesAndFirstCharInUpperCase( String name )
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

    public static String getMethodNameWithoutPrefix( Method method, String methodPrefix )
    {
        return method.getName().length() >= methodPrefix.length() ? method.getName().substring( methodPrefix.length() )
                                                                 : method.getName();
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

}
