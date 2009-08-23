package com.google.code.joto.procesors;

import static com.google.code.joto.ReverseEngineerData.concat;
import static com.google.code.joto.ReverseEngineerData.writeCreatorFooter;
import static com.google.code.joto.ReverseEngineerData.writeCreatorHeader;
import static com.google.code.joto.ReverseEngineerData.writeReturnStatement;

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
        Object[] arrayToProcess = (Object[]) objectToProcess;
        writeCreatorHeader( sharedData, objectToProcess );

        {
            String variableName = "array";
            String arrayType = arrayToProcess.getClass().getComponentType().getSimpleName();
            concat( sharedData, arrayType, "[] ", variableName, " = new ", arrayType, "[", arrayToProcess.length,
                    "];\n" );

            for ( int i = 0; i < arrayToProcess.length; i++ )
            {
                concat( sharedData, variableName + "[" + i + "] = " );
                callback.processThis( arrayToProcess[i] );
                concat( sharedData, ";\n" );
            }
            writeReturnStatement( sharedData, variableName );
        }
        writeCreatorFooter( sharedData );
    }
}
