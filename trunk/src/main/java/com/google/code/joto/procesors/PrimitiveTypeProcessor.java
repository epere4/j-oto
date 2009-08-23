package com.google.code.joto.procesors;

import static com.google.code.joto.ReverseEngineerData.concat;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;

public class PrimitiveTypeProcessor
    implements CustomProcessor
{

    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess != null && objectToProcess.getClass().isPrimitive();
    }

    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
        throws Exception
    {
        concat( sharedData, String.valueOf( objectToProcess ) );
    }
}

