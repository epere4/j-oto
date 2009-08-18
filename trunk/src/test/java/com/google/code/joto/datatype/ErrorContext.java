package com.google.code.joto.datatype;

import java.io.ObjectStreamException;


/**
 */

public final class ErrorContext
    implements EnumeratedType
{
  public static final ErrorContext ACTION = 
 new ErrorContext( "error.Action" );



  public static final ErrorContext INVALID_PRICE_OPTION = 
 new ErrorContext( "error.DisplayPriceOptionAction" );


  public static final ErrorContext DISPLAY_ACTION = new ErrorContext( "error.DisplayAction" );


  public static final ErrorContext CLOSEST_ACTION = new ErrorContext( "error.ClosestAction" );


  public static final ErrorContext SIMILAR_CITIES_ACTION = 
 new ErrorContext( "error.PreSimilarCitiesAction" );


  public static final ErrorContext CODE_CITY_LOCATOR_ACTION = 
 new ErrorContext( "error.PreCodeCityLocatorAction" );


  /**
   * Return an array of all valid instances
   * @return an array of all valid instances
   */

  public static ErrorContext[ ] getValues( )
  {
    return _VALUES;
  }


  /**
   * EnumeratedTypes are never publically constructed
   */

  private ErrorContext( String name ) 
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

  public static final ErrorContext newInstance( String name ) 
  {
    if ( name == null )
    {
      throw new IllegalArgumentException( "name is null" ); 
    }
    else if ( name.equals( "ACTION" ) )
    {
      return ACTION;
    }
    else if ( name.equals( "INVALID_PRICE_OPTION" ) )
    {
      return INVALID_PRICE_OPTION;
    }
        else if ( name.equals( "DISPLAY_ACTION" ) )
    {
            return DISPLAY_ACTION;
    }
        else if ( name.equals( "CLOSEST_ACTION" ) )
    {
            return CLOSEST_ACTION;
    }
    else if ( name.equals( "SIMILAR_CITIES_ACTION" ) )
    {
      return SIMILAR_CITIES_ACTION;
    }
    else if ( name.equals( "CODE_CITY_LOCATOR_ACTION" ) )
    {
      return CODE_CITY_LOCATOR_ACTION;
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
     * @return a String representing this ErrorContext
     */

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

  private static final ErrorContext[ ] _VALUES = 
 {
        ACTION,
        INVALID_PRICE_OPTION,
        DISPLAY_ACTION,
        CLOSEST_ACTION,
        SIMILAR_CITIES_ACTION,
        CODE_CITY_LOCATOR_ACTION };


  /**
   * For serialization
   */

  private Object readResolve( )
    throws ObjectStreamException
  {
     return _VALUES[ _ordinal ];
  }


}
