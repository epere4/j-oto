/**
 * 
 */
package com.google.code.joto.datatype;

public class Currency
{
    private String code;
    private String description;

    public Currency( String code, String description )
    {
        this.code = code;
        this.description = description;
    }

    public static final Currency AUD = new Currency( "AUD", "Australian Dollar" );

    public static final Currency ARS = new Currency( "ARS", "Argentina Peso" );

    public static final Currency USD = new Currency( "USD", "United States Dollar" );

    public static Currency getCurrencyForUSA()
    {
        return USD;
    }
}
