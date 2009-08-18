package com.google.code.joto.exportable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReverseEngineerReflectionUtil
{
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

    private static Map<Integer, Object> instances = new HashMap<Integer, Object>();

    public static <T> T registerInstance( Integer identityHashCode, T instance )
    {
        instances.put( identityHashCode, instance );
        return instance;
    }

    public static <T> T getInstance( Integer identityHashCode )
    {
        return (T) instances.get( identityHashCode );
    }
}