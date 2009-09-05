package com.google.code.joto.procesors;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;

public class NumberProcessor
    implements CustomProcessor
{

    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess instanceof Number;
    }

    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
        throws Exception
    {
        if ( objectToProcess instanceof BigDecimal )
        {
            BigDecimal bigDecimalToProcess = (BigDecimal) objectToProcess;
            sharedData.concat( "new BigDecimal( \"", bigDecimalToProcess.toString(), "\" )" );
        }
        else if ( objectToProcess instanceof BigInteger )
        {
            BigInteger bigIntegerToProcess = (BigInteger) objectToProcess;

            sharedData.concat( "new BigInteger( \"", bigIntegerToProcess.toString(), "\" )" );
        }
        else
        {
            sharedData.concat( String.valueOf( objectToProcess ) );
        }

    }

}
