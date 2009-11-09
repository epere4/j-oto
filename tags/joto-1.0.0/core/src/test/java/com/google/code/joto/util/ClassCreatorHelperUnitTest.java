package com.google.code.joto.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.code.joto.CustomProcessor;
import com.google.code.joto.SomeDummyClass;

public class ClassCreatorHelperUnitTest
{

    @Before
    public void setUp()
        throws Exception
    {
    }

    @Test
    public void testClassCreatorHelper_CanCreate()
    {
        ClassCreatorHelper classCreatorHelper = new ClassCreatorHelper();
        assertEquals( new File( "." ), classCreatorHelper.getOutputSrcFolder() );
        assertEquals( "", classCreatorHelper.getOutputPackageName() );
    }

    @Test
    public void testClassCreatorHelper_CanGenerateAClassAndItCompiles()
        throws Exception
    {
        File outputSrcFolder = new File( "target" );
        String outputPackageName = "com.test.packages";
        List<? extends CustomProcessor> userSuppliedProcessors = null;
        ClassCreatorHelper classCreatorHelper = new ClassCreatorHelper( outputSrcFolder, outputPackageName,
                                                                        userSuppliedProcessors );

        SomeDummyClass someDummyClass = new SomeDummyClass();
        someDummyClass.setValues();
        File generatedClassFile = classCreatorHelper
            .extractToDummyObject( someDummyClass, "SomeDummyClass", "Scenary1" );

        System.out.println( "Generated class file: " + generatedClassFile );
        assertNotNull( generatedClassFile );
        assertTrue( generatedClassFile.exists() );

        StringBuilder fileInMemoryStringBuffer = new StringBuilder();
        BufferedReader reader = new BufferedReader( new FileReader( generatedClassFile ) );
        String line;
        while ( ( line = reader.readLine() ) != null )
        {
            fileInMemoryStringBuffer.append( line ).append( "\n" );
        }
        String fileInMemory = fileInMemoryStringBuffer.toString();
        assertTrue( fileInMemory.contains( "public static " + SomeDummyClass.class.getSimpleName() + " createInstance" ) );
    }
}
