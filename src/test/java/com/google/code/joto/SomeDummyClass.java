package com.google.code.joto;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.google.code.joto.datatype.Access;
import com.google.code.joto.datatype.CodeType;
import com.google.code.joto.datatype.ErrorContext;
import com.google.code.joto.datatype.FeeTypeCode;
import com.google.code.joto.datatype.Vendor;
import com.google.code.joto.datatype.VendorCommission;

public class SomeDummyClass
{
    private int integer;

    Access access;

    ErrorContext errorContext;

    FeeTypeCode feeTypeCode;

    Vendor vendor;

    List<FeeTypeCode> someFeeTypeCode;

    String[] strings;

    Object[] objects;

    BigDecimal someBigDecimal;

    BigInteger someBigInteger;

    Collection someSynchronizedCollection = Collections.synchronizedCollection( new ArrayList() );

    /**
     * @param integer the integer to set
     */
    public void setInteger( int integer )
    {
        this.integer = integer;
    }

    /**
     * @param someBigDecimal the someBigDecimal to set
     */
    void setSomeBigDecimal( BigDecimal someBigDecimal )
    {
        this.someBigDecimal = someBigDecimal;
    }

    /**
     * @param someBigInteger the someBigInteger to set
     */
    public void setSomeBigInteger( BigInteger someBigInteger )
    {
        this.someBigInteger = someBigInteger;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this, ToStringStyle.MULTI_LINE_STYLE );
    }

    public void setValues()
    {
        integer = 56;
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
        someSynchronizedCollection.add( vendor );
        someSynchronizedCollection.add( 123 );
        someBigDecimal = BigDecimal.valueOf( 456.7 );
        someBigInteger = BigInteger.valueOf( 345 );
    }
}
