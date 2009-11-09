package com.google.code.joto.datatype;

import java.io.ObjectStreamException;

/**
 * This class comes from a real world application from before the existence of type safe enums in
 * java 5. It has been changed a little bit for confidentiality reasons.
 */
public final class Access
    implements EnumeratedType
{
    /**
     * Indicates that the user is a fraud agent.
     */

    public static final Access FRAUD = new Access( "101101100" );

    public static final Access ADMIN = new Access( "101100111" );

    /**
     * Return an array of all valid instances
     * @return an array of all valid instances
     */

    public static Access[] getValues()
    {
        return _VALUES;
    }

    /**
     * EnumeratedTypes are never publically constructed
     */

    private Access( String name )
    {
        _name = name;
    }

    /**
     * Return the instance associated with a particular String or throw an IllegalArgumentException
     * if the String does not map to a valid instance.
     * @param name the String associated the instance
     * @return an instance associated with the name
     */

    public static final Access newInstance( String name )
    {
        if ( name == null )
        {
            throw new IllegalArgumentException( "name is null" );
        }
        else if ( name.equals( "FRAUD" ) )
        {
            return FRAUD;
        }
        else
        {
            throw new IllegalArgumentException( name + " is not a valid instance" );
        }
    }

    /**
     * The private representation of this type
     */

    private final transient String _name;

    /**
     * Represent this AgentAccess as a String
     * @return a String representing this AgentAccess
     */

    public String toString()
    {
        return _name;
    }

    /**
     * Check for object equality
     * @param object an object of any type
     * @return true if the object is the same as this instance
     */

    public final boolean equals( Object object )
    {
        return super.equals( object );
    }

    /**
     * Generate a unique hash code based on object data
     * @return a unique hash code
     */

    public final int hashCode()
    {
        return super.hashCode();
    }

    /**
     * The static location within the values
     */

    private static int _nextOrdinal = 0;

    /**
     * The current location within the values
     */

    private final int _ordinal = _nextOrdinal++;

    /**
     * The values that this type can assume (for serialization)
     */

    private static final Access[] _VALUES = { FRAUD, ADMIN };

    /**
     * For serialization
     */

    private Object readResolve()
        throws ObjectStreamException
    {
        return _VALUES[_ordinal];
    }

}