package com.google.code.joto;

import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.getFieldForClass;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.getFieldValue;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.getInstance;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.registerInstance;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.setFieldValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
        TestClass testClass = new Creator<TestClass>()
        {
            public TestClass create()
            {
                TestClass obj = new TestClass();
                registerInstance( 20920201, obj ); /* @13f3789 */
                setFieldValue( obj, getFieldForClass( TestClass.class, "access" ), Access.FRAUD );
                setFieldValue( obj, getFieldForClass( TestClass.class, "errorContext" ),
                               ErrorContext.INVALID_PRICE_OPTION );
                setFieldValue( obj, getFieldForClass( TestClass.class, "feeTypeCode" ), FeeTypeCode.TAX );
                setFieldValue( obj, getFieldForClass( TestClass.class, "vendor" ), new Creator<Vendor>()
                {
                    public Vendor create()
                    {
                        Vendor obj = new Vendor( "" );
                        registerInstance( 15472292, obj ); /* @ec16a4 */
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
                if ( getFieldValue( obj, getFieldForClass( TestClass.class, "someFeeTypeCode" ) ) == null )
                {
                    setFieldValue( obj, getFieldForClass( TestClass.class, "someFeeTypeCode" ), new ArrayList() );

}
                ( (Collection) getFieldValue( obj, getFieldForClass( TestClass.class, "someFeeTypeCode" ) ) )
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
                setFieldValue( obj, getFieldForClass( TestClass.class, "strings" ), new Creator<String[]>()
                {
                    public String[] create()
                    {
                        String[] array = new String[2];
                        array[0] = "lolo";
                        array[1] = "lala";
                        return array;
                    }
                }.create() );
                setFieldValue( obj, getFieldForClass( TestClass.class, "objects" ), new Creator<Object[]>()
                {
                    public Object[] create()
                    {
                        Object[] array = new Object[8];
                        array[0] = 13;
                        array[1] = new Date( 1250634489294L ) /* Tue Aug 18 19:28:09 GMT-03:00 2009 */;
                        array[2] = "lolo";
                        array[3] = CodeType.RC;
                        array[4] = ErrorContext.INVALID_PRICE_OPTION;
                        array[5] = ErrorContext.DISPLAY_ACTION;
                        array[6] = new Creator<VendorCommission>()
                        {
                            public VendorCommission create()
                            {
                                VendorCommission obj = new VendorCommission();
                                registerInstance( 25839584, obj ); /* @18a47e0 */
                                obj.setCommission( 0.0 );
                                obj.setTAXCommissionApplicable( true );
                                return obj;
                            }
                        }.create();
                        array[7] = getInstance( 15472292 ) /* @ec16a4 */;
                        return array;
                    }
                }.create() );
                return obj;
            }
        }.create();

        System.out.println( testClass );

    }
}
