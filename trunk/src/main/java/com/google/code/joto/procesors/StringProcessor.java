package com.google.code.joto.procesors;

import static com.google.code.joto.ReverseEngineerData.concat;

import java.util.HashMap;
import java.util.Map;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;

public class StringProcessor
    implements CustomProcessor
{

    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess instanceof CharSequence;
    }

    private static Map<Character, String> replacementTable = new HashMap<Character, String>();
    static
    {
        replacementTable.put( '\n', "\\n\"\n + \"" );
        replacementTable.put( '\r', "\\r" );
        replacementTable.put( '\t', "\\t" );
    }

    /**
     * @see CustomProcessor#processThis(Object, ReverseEngineerData, int, ProcessMoreCallback)
     */
    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
        throws Exception
    {
        CharSequence stringToProcess = (CharSequence) objectToProcess;
        StringBuilder strProcessesed = new StringBuilder( (int) ( stringToProcess.length() * 1.5 ) );

        for ( int i = 0; i < stringToProcess.length(); i++ )
        {
            char c = stringToProcess.charAt( i );
            String replacementString = replacementTable.get( c );
            if ( replacementString != null )
            {
                strProcessesed.append( replacementString );
            }
            else
            {
                strProcessesed.append( c );
            }
        }
        
        concat( sharedData, "\"" + strProcessesed + "\"" );
    }
}
