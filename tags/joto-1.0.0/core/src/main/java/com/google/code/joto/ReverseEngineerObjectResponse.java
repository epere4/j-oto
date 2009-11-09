package com.google.code.joto;

public class ReverseEngineerObjectResponse
{

    public ReverseEngineerObjectResponse( String imports, String code )
    {
        this.imports = imports;
        this.code = code;
    }

    /** imports */
    public final String imports;

    /** code */
    public final String code;

    /** codeForCreatorInterface */
    public final String codeForCreatorInterface = "interface Creator<E> {\n    public E create();\n}";

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "//---------------------------------------------------------\n" );
        sb.append( imports ).append( "\n" );
        sb.append( "//---------------------------------------------------------\n" );
        sb.append( codeForCreatorInterface ).append( "\n" );
        sb.append( "//---------------------------------------------------------\n" );
        sb.append( code ).append( "\n" );
        sb.append( "//---------------------------------------------------------\n" );
        return sb.toString();
    }
}

