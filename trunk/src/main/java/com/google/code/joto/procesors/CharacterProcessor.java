package com.google.code.joto.procesors;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;

public class CharacterProcessor
    implements CustomProcessor
{

    /**
     * @see com.google.code.joto.CustomProcessor#canProcessThis(java.lang.Object)
     */
    public boolean canProcessThis( Object objectToProcess )
    {
        return objectToProcess instanceof Character;
    }

    /**
     * @see com.google.code.joto.CustomProcessor#processThis(java.lang.Object,
     *      com.google.code.joto.ReverseEngineerData, int, com.google.code.joto.ProcessMoreCallback)
     */
    public void processThis( Object objectToProcess, ReverseEngineerData sharedData, int depthLevel,
                             ProcessMoreCallback callback )
        throws Exception
    {
        sharedData.concat( String.valueOf( objectToProcess ) );
    }
}
