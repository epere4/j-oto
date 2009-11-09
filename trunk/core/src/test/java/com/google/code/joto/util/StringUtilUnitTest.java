/**
 * 
 */
package com.google.code.joto.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Edu Pereda
 *
 */
public class StringUtilUnitTest
{

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp()
        throws Exception
    {
    }

    @Test
    public void testIsJavaReservedWord()
        throws Exception
    {
        assertFalse( StringUtil.isJavaReservedWord( null ) );
        assertTrue( StringUtil.isJavaReservedWord( "int" ) );
        assertTrue( StringUtil.isJavaReservedWord( "double" ) );
        assertTrue( StringUtil.isJavaReservedWord( "class" ) );
        assertTrue( StringUtil.isJavaReservedWord( "final" ) );
        assertFalse( StringUtil.isJavaReservedWord( "Double" ) );
    }

    /**
     * Test method for {@link com.google.code.joto.util.StringUtil#isJavaIdentifier(java.lang.String)}.
     */
    @Test
    public void testIsJavaIdentifier()
    {
        assertFalse( StringUtil.isJavaIdentifier( null ) );
        assertFalse( StringUtil.isJavaIdentifier( "" ) );
        assertFalse( StringUtil.isJavaIdentifier( " " ) );
        assertFalse( StringUtil.isJavaIdentifier( "a b" ) );
        assertFalse( StringUtil.isJavaIdentifier( "1" ) );
        assertFalse( StringUtil.isJavaIdentifier( "1b" ) );
        assertTrue( StringUtil.isJavaIdentifier( "a" ) );
        assertTrue( StringUtil.isJavaIdentifier( "ab" ) );
        assertTrue( StringUtil.isJavaIdentifier( "a1" ) );
        assertFalse( StringUtil.isJavaIdentifier( "1var" ) );
        assertFalse( StringUtil.isJavaIdentifier( "int" ) );
        assertTrue( StringUtil.isJavaIdentifier( "pipe1" ) );
        assertFalse( StringUtil.isJavaIdentifier( ";pipe" ) );
        assertFalse( StringUtil.isJavaIdentifier( "pi/pe" ) );
        assertFalse( StringUtil.isJavaIdentifier( "pi;pe" ) );
        assertFalse( StringUtil.isJavaIdentifier( "pipe;" ) );
        assertFalse( StringUtil.isJavaIdentifier( "pipe." ) );
        assertTrue( StringUtil.isJavaIdentifier( "integer" ) );
        assertFalse( StringUtil.isJavaIdentifier( ".log" ) );
        assertTrue( StringUtil.isJavaIdentifier( "$lol" ) );
        assertTrue( StringUtil.isJavaIdentifier( "Variable" ) );
    }

    @Test
    public void testIsEmpty()
        throws Exception
    {
        assertTrue( StringUtil.isEmpty( null ) );
        assertTrue( StringUtil.isEmpty( "" ) );
        assertFalse( StringUtil.isEmpty( " " ) );
        assertFalse( StringUtil.isEmpty( "a" ) );
    }

    @Test
    public void testIsBlank()
        throws Exception
    {
        assertTrue( StringUtil.isBlank( null ) );
        assertTrue( StringUtil.isBlank( "" ) );
        assertTrue( StringUtil.isBlank( " " ) );
        assertTrue( StringUtil.isBlank( " \n" ) );
        assertFalse( StringUtil.isBlank( "a" ) );
        assertFalse( StringUtil.isBlank( "a " ) );
        assertFalse( StringUtil.isBlank( " a" ) );
        assertFalse( StringUtil.isBlank( " a " ) );
    }

    /**
     * Test method for {@link StringUtil#isJavaPackage(String)}.
     */
    @Test
    public void testIsJavaPackage()
    {
        assertTrue( StringUtil.isJavaPackage( null ) );
        assertTrue( StringUtil.isJavaPackage( "" ) );
        assertTrue( StringUtil.isJavaPackage( "a" ) );
        assertTrue( StringUtil.isJavaPackage( "a.b" ) );
        assertTrue( StringUtil.isJavaPackage( "a.b.c" ) );
        assertFalse( StringUtil.isJavaPackage( " " ) );
        assertFalse( StringUtil.isJavaPackage( "9" ) );
        assertFalse( StringUtil.isJavaPackage( "/" ) );
        assertFalse( StringUtil.isJavaPackage( "1.b.c" ) );
        assertFalse( StringUtil.isJavaPackage( "a.1.c" ) );
        assertFalse( StringUtil.isJavaPackage( "a..b.c" ) );
        assertFalse( StringUtil.isJavaPackage( ".a.b.c" ) );
        assertFalse( StringUtil.isJavaPackage( "a.b s.c" ) );
        assertFalse( StringUtil.isJavaPackage( "a.bs.c;" ) );
        assertFalse( StringUtil.isJavaPackage( "a.b.c." ) );
        assertFalse( StringUtil.isJavaPackage( "a.b.c.." ) );
        assertFalse( StringUtil.isJavaPackage( "a.b/.c.." ) );
        assertFalse( StringUtil.isJavaPackage( "a.boolean.c" ) );
        assertTrue( StringUtil.isJavaPackage( "a.Boolean.c" ) );
        assertTrue( StringUtil.isJavaPackage( "com.test.packages" ) );
    }

    @Test
    public void testPackageNameToPath()
        throws Exception
    {
        File packageFolder = StringUtil.packageNameToFile( new File( "." ), "" );

        assertEquals( new File( "/tmp/a/b" ), StringUtil.packageNameToFile( new File( "/tmp" ), "a.b" ) );
        assertEquals( new File( "/tmp/" ), StringUtil.packageNameToFile( new File( "/tmp" ), "" ) );
        assertEquals( new File( "/tmp/" ), StringUtil.packageNameToFile( new File( "/tmp" ), null ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPackageNameToPath_WithIllegalPackageName()
        throws Exception
    {
        StringUtil.packageNameToFile( new File( "/tmp" ), "a b" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPackageNameToPath_WithIllegalPackageNameAndNoFile()
        throws Exception
    {
        StringUtil.packageNameToFile( "a b" );
    }

    @Test
    public void testPackageNameToPath_WithNullFile()
        throws Exception
    {
        assertEquals( new File( "a/b" ), StringUtil.packageNameToFile( null, "a.b" ) );
        assertEquals( new File( "a/b" ), StringUtil.packageNameToFile( "a.b" ) );
    }
}
