/**
 * 
 */
package com.google.code.joto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.code.joto.customized.EnumeratedTypeProcessor;
import com.google.code.joto.datatype.Access;
import com.google.code.joto.datatype.CodeType;
import com.google.code.joto.datatype.ErrorContext;
import com.google.code.joto.datatype.FeeTypeCode;
import com.google.code.joto.datatype.Vendor;
import com.google.code.joto.datatype.VendorCommission;

/**
 */
public class ReverseEngineerObjectUnitTest
    extends TestCase
{

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
    }

    /**
     * Test method for
     * {@link lili.generator.ReverseEngineerObject#generateCode(java.lang.Object, java.util.List)}.
     */
    public void testGenerateCode()
    {
        TestClass obj = new TestClass();
        obj.setValues();
        ReverseEngineerObject reverseEngineerObject = new ReverseEngineerObject();

        List<String> someStrings = new ArrayList<String>();
        someStrings.add( "some" );
        someStrings.add( "234" );
        List<EnumeratedTypeProcessor> userSuppliedProcessors = Arrays
            .asList( new EnumeratedTypeProcessor() );
        final ReverseEngineerObjectResponse response1 = reverseEngineerObject.generateCode( someStrings,
                                                                                            userSuppliedProcessors );
        printAResponse( response1 );

        System.out.println( obj );
        final ReverseEngineerObjectResponse response2 = reverseEngineerObject
            .generateCode( obj, userSuppliedProcessors );
        printAResponse( response2 );
    }

    private void printAResponse( ReverseEngineerObjectResponse response )
    {
        System.out.println( response );
    }

}

class TestClass
{
    int integer = 9;

    Access access;

    ErrorContext errorContext;

    FeeTypeCode feeTypeCode;

    Vendor vendor;

    List<FeeTypeCode> someFeeTypeCode;

    String[] strings;

    Object[] objects;

    BigDecimal someBigDecimal = BigDecimal.valueOf( 456.7 );

    BigInteger someBigInteger = BigInteger.valueOf( 345 );

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this, ToStringStyle.MULTI_LINE_STYLE );
    }

    public void setValues()
    {
        access = Access.FRAUD;
        errorContext = ErrorContext.INVALID_PRICE_OPTION;
        feeTypeCode = FeeTypeCode.TAX;
        vendor = new Vendor( "Dollar" );
        someFeeTypeCode = new ArrayList<FeeTypeCode>();
        someFeeTypeCode.add( FeeTypeCode.PERDAY );
        someFeeTypeCode.add( FeeTypeCode.FPT );
        vendor.addCodeToCountry( "US", CodeType.IT, "some code" );
        strings = new String[] { "lolo", "lala", "someString\nWith new line\tand\ntab" };
        objects = new Object[] {
            13,
            new Date(),
            "lolo",
            CodeType.RC,
            ErrorContext.INVALID_PRICE_OPTION,
            ErrorContext.DISPLAY_ACTION,
            new VendorCommission(),
            vendor };
    }
}
