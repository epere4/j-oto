/**
 * 
 */
package com.google.code.joto.procesors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.code.joto.ProcessMoreCallback;
import com.google.code.joto.ReverseEngineerData;

/**
 * @author Edu Pereda
 *
 */
@RunWith(JMock.class)
public class StringProcessorUnitTest
{
    Mockery context = new JUnit4Mockery();

    private StringProcessor processor;

    private ProcessMoreCallback callback;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp()
        throws Exception
    {
        processor = new StringProcessor();
        callback = context.mock( ProcessMoreCallback.class );
    }

    /**
     * Test method for {@link com.google.code.joto.procesors.StringProcessor#canProcessThis(java.lang.Object)}.
     */
    @Test
    public void testCanProcessThis()
    {
        assertTrue( processor.canProcessThis( "some strin" ) );
        assertFalse( processor.canProcessThis( null ) );
        assertFalse( processor.canProcessThis( 34 ) );
        assertTrue( processor.canProcessThis( new StringBuilder() ) );
    }

    /**
     * Test method for
     * {@link com.google.code.joto.procesors.StringProcessor#processThis(java.lang.Object, com.google.code.joto.ReverseEngineerData, int, com.google.code.joto.ProcessMoreCallback)}
     * .
     * @throws Exception
     */
    @Test
    public void testProcessThis()
        throws Exception
    {
        assertStringProcessing( "\"expected\"", "expected" );
        assertStringProcessing( "\"some\\n\"\n + \"enter\\n\"\n + \"\"", "some\nenter\n" );
        assertStringProcessing( "\"expect\\ted\"", "expect\ted" );
        assertStringProcessing( "\"expect\\red\"", "expect\red" );
        assertStringProcessing( "\"expect\\\\ed\"", "expect\\ed" );
        assertStringProcessing( "\"expect\\r\\ted\"", "expect\r\ted" );
    }

    private void assertStringProcessing( String expected, String actual )
        throws Exception
    {
        ReverseEngineerData sharedData = new ReverseEngineerData();
        processor.processThis( actual, sharedData, -1, callback );
        System.out.println( "transformed: " + sharedData.sb );
        assertEquals( expected, sharedData.sb.toString() );
    }

}
