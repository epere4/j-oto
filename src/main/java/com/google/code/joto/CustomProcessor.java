package com.google.code.joto;

public interface CustomProcessor
{
    boolean canProcessThis( Object objectToProcess );

    void processThis( Object objectToProcess, ReverseEngineerObjectWorkerParameter sharedData, int depthLevel,
                      ProcessMoreCallback callback )
        throws Exception;
}