package com.google.code.joto;

import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.getFieldForClass;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.getFieldValue;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.getInstance;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.registerInstance;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.setFieldValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.google.code.joto.datatype.Access;
import com.google.code.joto.datatype.CodeType;
import com.google.code.joto.datatype.ErrorContext;
import com.google.code.joto.datatype.FeeTypeCode;
import com.google.code.joto.datatype.Vendor;
import com.google.code.joto.datatype.VendorCommission;
import com.google.code.joto.exportable.Creator;

public class ReverseEngineerObjectSandbox
    extends TestCase
{

    public void testSomeMethod()
    {
        List list = new Creator<ArrayList>()
        {
            public ArrayList create()
            {
                ArrayList col = new ArrayList();
                col.add( "some" );
                col.add( "234" );
                return col;
            }
        }.create();
        // the code from the "new" to the last "create()" was generated by the tool.
        SomeDummyClass testClass = new Creator<SomeDummyClass>(){ 
            public SomeDummyClass create()
            {
                SomeDummyClass obj = new SomeDummyClass();
                registerInstance( 30246505, obj ); /* @1cd8669 */
                setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "integer" ), 9 );
                setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "access" ), Access.FRAUD );
                setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "errorContext" ),
                               ErrorContext.INVALID_PRICE_OPTION );
                setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "feeTypeCode" ), FeeTypeCode.TAX );
                setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "vendor" ), new Creator<Vendor>()
                {
                    public Vendor create()
                    {
                        Vendor obj = new Vendor( null );
                        registerInstance( 24431647, obj ); /* @174cc1f */
                        obj.setVendorCode( "Dollar" );
                        obj.setAddEVoucherAsBillingReference( false );
                        obj.setEVoucherNumberLength( 12 );
                        obj.setAvNumberLength( "" );
                        if ( getFieldValue( obj, getFieldForClass( Vendor.class, "preferences" ) ) == null )
                        {
                            setFieldValue( obj, getFieldForClass( Vendor.class, "preferences" ), new HashMap() );

                        }
                        ( (Map) getFieldValue( obj, getFieldForClass( Vendor.class, "preferences" ) ) )
                            .putAll( new Creator<HashMap>()
                            {
                                public HashMap create()
                                {
                                    HashMap map = new HashMap();
                                    map.put( "US", new Creator<HashMap>()
                                    {
                                        public HashMap create()
                                        {
                                            HashMap map = new HashMap();
                                            map.put( CodeType.IT, "some code" );
                                            return map;
                                        }
                                    }.create() );
                                    return map;
                                }
                            }.create() );
                        if ( getFieldValue( obj, getFieldForClass( Vendor.class, "commissions" ) ) == null )
                        {
                            setFieldValue( obj, getFieldForClass( Vendor.class, "commissions" ), new HashMap() );

                }
                        ( (Map) getFieldValue( obj, getFieldForClass( Vendor.class, "commissions" ) ) )
                            .putAll( new Creator<HashMap>()
                            {
                                public HashMap create()
                                {
                                    HashMap map = new HashMap();
                                    return map;
                                }
                            }.create() );
                        return obj;
                    }
                }.create() );
                if ( getFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someFeeTypeCode" ) ) == null )
                {
                    setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someFeeTypeCode" ), new ArrayList() );

                }
                ( (Collection) getFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someFeeTypeCode" ) ) )
                    .addAll( new Creator<ArrayList>()
                    {
                        public ArrayList create()
                        {
                            ArrayList col = new ArrayList();
                            col.add( FeeTypeCode.PERDAY );
                            col.add( FeeTypeCode.FPT );
                            return col;
                        }
                    }.create() );
                setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "strings" ), new Creator<String[]>()
                {
                    public String[] create()
                    {
                        String[] array = new String[3];
                        array[0] = "lolo";
                        array[1] = "lala";
                        array[2] = "someString\n" + "With new line\tand\n" + "tab";
                        return array;
                    }
                }.create() );
                setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "objects" ), new Creator<Object[]>()
                {
                    public Object[] create()
                    {
                        Object[] array = new Object[8];
                        array[0] = 13;
                        array[1] = new Date( 1251070488031L ) /* Sun Aug 23 20:34:48 GMT-03:00 2009 */;
                        array[2] = "lolo";
                        array[3] = CodeType.RC;
                        array[4] = ErrorContext.INVALID_PRICE_OPTION;
                        array[5] = ErrorContext.DISPLAY_ACTION;
                        array[6] = new Creator<VendorCommission>()
                        {
                            public VendorCommission create()
                            {
                                VendorCommission obj = new VendorCommission();
                                registerInstance( 32320232, obj ); /* @1ed2ae8 */
                                obj.setCommission( 0.0 );
                                obj.setTAXCommissionApplicable( true );
                                return obj;
                            }
                        }.create();
                        array[7] = getInstance( 24431647 ) /* @174cc1f */;
                        return array;
                    }
                }.create() );
                setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someBigDecimal" ), new BigDecimal( "456.7" ) );
                setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someBigInteger" ), new BigInteger( "345" ) );
                if ( getFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someSynchronizedCollection" ) ) == null )
                {
                    setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someSynchronizedCollection" ),
                                   new LinkedList() );

                }
                ( (Collection) getFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someSynchronizedCollection" ) ) )
                    .addAll( new Creator<LinkedList>()
                    {
                        public LinkedList create()
                        {
                            LinkedList col = new LinkedList();
                            col.add( getInstance( 24431647 ) /* @174cc1f */);
                            col.add( 123 );
                            return col;
                        }
                    }.create() );
                return obj;
            }
        }.create();

        System.out.println( testClass );

    }
}