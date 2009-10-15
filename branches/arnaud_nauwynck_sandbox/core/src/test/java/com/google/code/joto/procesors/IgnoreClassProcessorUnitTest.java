package com.google.code.joto.procesors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.code.joto.ReverseEngineerData;
import com.google.code.joto.datatype.CodeType;
import com.google.code.joto.datatype.Vendor;

public class IgnoreClassProcessorUnitTest
{

    @Before
    public void setUp()
        throws Exception
    {
    }

    @Test
    public void testProcessThis()
        throws Exception
    {
        IgnoreClassProcessor proc = new IgnoreClassProcessor();
        ReverseEngineerData sharedData = new ReverseEngineerData();
        proc.processThis( null, sharedData, 4, null );
        assertEquals( "null", sharedData.sb.toString() );
    }

    @Test
    public void testCanProcessThis_FailsBecauseNotFullClassName()
    {
        IgnoreClassProcessor proc = new IgnoreClassProcessor( "Vendor" );
        assertFalse( proc.canProcessThis( new Vendor( null, null ) ) );
    }

    @Test
    public void testCanProcessThis_SucceedsUsingFullClassName()
    {
        IgnoreClassProcessor proc = new IgnoreClassProcessor( "com.google.code.joto.datatype.Vendor" );
        assertTrue( proc.canProcessThis( new Vendor( null, null ) ) );
    }

    @Test
    public void testCreateForClass()
    {
        IgnoreClassProcessor proc = IgnoreClassProcessor.createForClasses( Vendor.class );
        assertTrue( proc.canProcessThis( new Vendor( null, null ) ) );
    }

    @Test
    public void testCreateForClassName()
    {
        IgnoreClassProcessor proc = IgnoreClassProcessor.createForClassNames( "com.google.code.joto.datatype.Vendor" );
        assertTrue( proc.canProcessThis( new Vendor( null, null ) ) );
    }

    @Test
    public void testCreateForSimpleClassName()
    {
        IgnoreClassProcessor proc = IgnoreClassProcessor.createForSimpleClassNames( "Vendor" );
        assertTrue( proc.canProcessThis( new Vendor( null, null ) ) );
        assertFalse( proc.canProcessThis( CodeType.BS ) );
    }

    @Test
    public void testCreateForSimpleClassNames()
    {
        IgnoreClassProcessor proc = IgnoreClassProcessor.createForSimpleClassNames( "Vendor", "CodeType" );
        assertTrue( proc.canProcessThis( new Vendor( null, null ) ) );
        assertTrue( proc.canProcessThis( CodeType.BS ) );
    }

}
