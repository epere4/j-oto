package com.google.code.joto.procesors;

import static com.google.code.joto.ReverseEngineerData.concat;
import static com.google.code.joto.ReverseEngineerData.writeCreatorFooter;
import static com.google.code.joto.ReverseEngineerData.writeCreatorHeader;
import static com.google.code.joto.ReverseEngineerData.writeReturnStatement;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;

public class ArrayProcessor
    implements CustomProcessor
{

    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess != null && objectToProcess.getClass().isArray();
    }

    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
    {
        writeCreatorHeader( sharedData, objectToProcess );

        {
            String variableName = "array";
            String arrayType = objectToProcess.getClass().getComponentType().getSimpleName();
            concat( sharedData, arrayType, "[] ", variableName, " = new ", arrayType, "[", Array.getLength(objectToProcess),
                    "];\n" );

            for ( int i = 0; i < Array.getLength(objectToProcess); i++ )
            {
                concat( sharedData, variableName + "[" + i + "] = " );
                callback.processThis( Array.get(objectToProcess, i) );
                concat( sharedData, ";\n" );
            }
            writeReturnStatement( sharedData, variableName );
        }
        writeCreatorFooter( sharedData );
    }
}
