package com.google.code.joto.datatype;

import java.io.ObjectStreamException;



public final class FeeTypeCode
    implements EnumeratedType
{
  /**
   * FPT
   */

  public static final FeeTypeCode FPT = 
    new FeeTypeCode( "FPT" );


  /**
   * Tax
   */

  public static final FeeTypeCode TAX = 
    new FeeTypeCode( "TAX" );


  /**
   * PERDAY 
   */

  public static final FeeTypeCode PERDAY = 
    new FeeTypeCode( "PERDAY" );


  /**
   * Return an array of all valid instances
   * @return an array of all valid instances
   */

  public static FeeTypeCode[ ] getValues( )
  {
    return _VALUES;
  }


  /**
   * EnumeratedTypes are never publically constructed
   */

  private FeeTypeCode( String name ) 
  {
    _name = name;
  }


  /**
   * Return the instance associated with a particular String
   * or throw an IllegalArgumentException if the String does
   * not map to a valid instance.
   *
   * @param name the String associated the instance
   * @return an instance associated with the name
   */

  public static final FeeTypeCode newInstance( String name ) 
  {
    if ( name == null )
    {
      throw new IllegalArgumentException( "name is null" ); 
    }
    else if ( name.equals( "FPT" ) )
    {
      return FPT;
    }
    else if ( name.equals( "TAX" ) )
    {
      return TAX;
    }
    else if ( name.equals( "PERDAY" ) )
    {
      return PERDAY;
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


  public String toString( )
  {
    return _name;
  }


  /**
   * Check for object equality
   * 
   * @param object an object of any type
   * @return true if the object is the same as this instance
   */

   public final boolean equals( Object object )
   {
      return super.equals( object );
   }


  /**
   * Generate a unique hash code based on object data
   * 
   * @return a unique hash code
   */

  public final int hashCode( )
  {
    return super.hashCode( ); 
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

  private static final FeeTypeCode[ ] _VALUES = 
    { FPT, TAX, PERDAY };


  /**
   * For serialization
   */

  private Object readResolve( )
    throws ObjectStreamException
  {
     return _VALUES[ _ordinal ];
  }


}
