/**
 * 
 */
package com.google.code.joto.datatype;

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
}
