package com.google.code.joto.exportable;

import java.lang.reflect.Field;

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
}