package com.google.code.joto.procesors;

import static com.google.code.joto.ReverseEngineerData.concat;
import static com.google.code.joto.ReverseEngineerData.writeCreatorFooter;
import static com.google.code.joto.ReverseEngineerData.writeCreatorHeader;
import static com.google.code.joto.ReverseEngineerData.writeDeclarationAndInitializationOfVariable;
import static com.google.code.joto.ReverseEngineerData.writeReturnStatement;
import static com.google.code.joto.ReverseEngineerHelper.getType;

import java.util.Collection;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;

public class CollectionProcessor
    implements CustomProcessor
{

    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess instanceof Collection;
    }

    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
    {
        Collection collectionToProcess = (Collection) objectToProcess;
        writeCreatorHeader( sharedData, objectToProcess );

        {
            String variableName = "col";

            sharedData.addClassToImport( getType( collectionToProcess ) );
            writeDeclarationAndInitializationOfVariable( sharedData, variableName, collectionToProcess );

            for ( Object element : collectionToProcess )
            {
                concat( sharedData, variableName, ".add(" );
                callback.processThis( element );
                concat( sharedData, ");\n" );
            }

            writeReturnStatement( sharedData, variableName );
        }

        writeCreatorFooter( sharedData );
    }

}