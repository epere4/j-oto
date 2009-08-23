package com.google.code.joto.exportable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Some utility methods for reflection that wraps all the checked exceptions and rethrows them as
 * {@link RuntimeException RuntimeExceptions}.
 * @author Liliana.nu
 * @author epere4
 */
public class ReverseEngineerReflectionUtil
{
    /**
     * Allows you to dynamically set a field value using reflection. This is similar to executing
     * something like this:
     * <p>
     * <code>destination.field = valueToSet;</code>
     * <p>
     * It will work even for private {@link Field fields}.
     * @param destination the object in which the field is going to be set.
     * @param field the field to set.
     * @param valueToSet the object to set in the field.
     */
    public static void setFieldValue( Object destination, Field field, Object valueToSet )
    {
        boolean oldAccessible = field.isAccessible();
        try
        {
            field.setAccessible( true );
            field.set( destination, valueToSet );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            field.setAccessible( oldAccessible );
        }
    }

    /**
     * Allows you to dynamically get a value field by reflection. This is similar to having this on
     * the right side of an expression: <code>origination.field</code>
     * <p>
     * It will work even for private {@link Field fields}.
     * @param origination the object from which to obtain the value.
     * @param field the field that you want the value from.
     * @return the value of the field for the origination object.
     */
    public static Object getFieldValue( Object origination, Field field )
    {
        boolean oldAccessible = field.isAccessible();
        try
        {
            field.setAccessible( true );
            return field.get( origination );
        }
        catch ( IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            field.setAccessible( oldAccessible );
        }
    }

    /**
     * This is just a wrapper for <code>clazz.getDeclaredField( fieldName )</code> that catches the
     * checked exceptions and re throws them as {@link RuntimeException RuntimeExceptions}
     * @param clazz the class to which ask for the fieldName.
     * @param fieldName the fieldName that you want.
     * @return the {@link Field} object.
     */
    @SuppressWarnings("unchecked")
    public static Field getFieldForClass( Class clazz, String fieldName )
    {
        try
        {
            return clazz.getDeclaredField( fieldName );
        }
        catch ( NoSuchFieldException e )
        {
            throw new RuntimeException( e );
        }
    }

    /** instances */
    private static Map<Integer, Object> instances = new HashMap<Integer, Object>();

    /**
     * This is just a wrapper to a {@link Map} that will hold all the instances using its
     * identityHashCode as key. You can get the identityHashCode for an object by calling
     * {@link System#identityHashCode(Object)}.
     * <p>
     * Example of intended usage:
     * <p>
     * <code>ReverseEngineerReflectionUtil.registerInstance( System.identityHashCode( someObject ), someObject);</code>
     * @param <T> the type of the instance.
     * @param identityHashCode the value that will be used as key in the map.
     * @param instance the instance to store.
     * @return the same instance received as parameter.
     */
    public static <T> T registerInstance( Integer identityHashCode, T instance )
    {
        instances.put( identityHashCode, instance );
        return instance;
    }

    /**
     * This method retrieves objects stored by {@link #registerInstance(Integer, Object)}. *
     * <p>
     * Example of intended usage:
     * 
     * <pre>
     * Integer identityHashCode = ...;
     * Object someObject = ReverseEngineerReflectionUtil.getInstance(identityHashCode)
     * </pre>
     * @param <T>
     * @param identityHashCode
     * @return the object previously stored for that identityHashCode.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance( Integer identityHashCode )
    {
        return (T) instances.get( identityHashCode );
    }
}