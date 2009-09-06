/**
 * 
 */
package com.google.code.joto.datatype;

import org.apache.commons.lang.builder.ToStringBuilder;

public class SomeSingleton
{
    private static SomeSingleton instance;

    public static SomeSingleton getInstance()
    {
        if ( instance == null )
        {
            instance = new SomeSingleton();
        }
        return instance;
    }

    private SomeSingleton()
    {

    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }
}
