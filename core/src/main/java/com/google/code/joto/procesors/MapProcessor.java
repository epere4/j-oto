package com.google.code.joto.procesors;

import static com.google.code.joto.ReverseEngineerData.concat;
import static com.google.code.joto.ReverseEngineerData.writeCreatorFooter;
import static com.google.code.joto.ReverseEngineerData.writeCreatorHeader;
import static com.google.code.joto.ReverseEngineerData.writeDeclarationAndInitializationOfVariable;
import static com.google.code.joto.ReverseEngineerData.writeReturnStatement;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;

public class MapProcessor
    implements CustomProcessor
{

    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess instanceof Map;
    }

    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
    {
        Map mapToProcess = (Map) objectToProcess;
        writeCreatorHeader( sharedData, objectToProcess );

        {
            String variableName = "map";

            writeDeclarationAndInitializationOfVariable( sharedData, variableName, mapToProcess );

            for ( Entry entry : (Set<Entry>) mapToProcess.entrySet() )
            {
                concat( sharedData, variableName, ".put(" );
                callback.processThis( entry.getKey() );
                concat( sharedData, ", " );
                callback.processThis( entry.getValue() );
                concat( sharedData, ");\n" );
            }

            writeReturnStatement( sharedData, variableName );
        }

        writeCreatorFooter( sharedData );

    }
}