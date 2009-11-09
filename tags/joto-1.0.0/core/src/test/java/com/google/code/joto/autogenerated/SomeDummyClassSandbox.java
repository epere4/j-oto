package com.google.code.joto.autogenerated;
import java.util.Collection;
import java.util.Date;
import com.google.code.joto.datatype.VendorCommission;
import java.util.List;
import java.lang.String;
import com.google.code.joto.datatype.SomeDataTypeToBeIgnoredOnProcessing;
import java.util.Set;
import com.google.code.joto.SomeDummyClass;
import com.google.code.joto.datatype.Currency;
import com.google.code.joto.datatype.SomeSingleton;
import com.google.code.joto.datatype.Access;
import java.lang.Boolean;
import com.google.code.joto.exportable.Creator;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.code.joto.datatype.Vendor;
import java.lang.Double;
import com.google.code.joto.datatype.CodeType;
import com.google.code.joto.datatype.FeeTypeCode;
import java.lang.Integer;
import java.util.Map;
import com.google.code.joto.exportable.InstancesMap;
import com.google.code.joto.datatype.ErrorContext;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.math.BigInteger;
import java.lang.Object;
import static com.google.code.joto.exportable.ReverseEngineerReflectionUtil.*;

/** 
 * This class was automatically generated and it is present on the svn 
 * only as an example documentation.
 */
class SomeDummyClassSandbox {
  public static void main(String [] args) {
    System.out.println("Created instance: ");
    System.out.println(
new Creator<SomeDummyClass>(){ 
public SomeDummyClass create() {
final InstancesMap instancesMap = new InstancesMap();
SomeDummyClass generatedObject = new Creator<SomeDummyClass>(){ 
public SomeDummyClass create() {
SomeDummyClass obj = new SomeDummyClass(  );
instancesMap.registerInstance( 25300442, obj);  /* @1820dda */
obj.setInteger(56);
setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "access"), Access.FRAUD);
setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "errorContext"), ErrorContext.INVALID_PRICE_OPTION);
setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "feeTypeCode"), FeeTypeCode.TAX);
setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "vendor"), new Creator<Vendor>(){ 
public Vendor create() {
Vendor obj = new Vendor( (String) null, (String) null );
instancesMap.registerInstance( 31054905, obj);  /* @1d9dc39 */
obj.setVendorCode("Dollar");
obj.setAddEVoucherAsBillingReference(false);
obj.setEVoucherNumberLength(12);
obj.setAvNumberLength("leroi");
if (getFieldValue( obj, getFieldForClass(Vendor.class, "preferences")) == null) {
setFieldValue( obj, getFieldForClass(Vendor.class, "preferences"), new HashMap());

}
((Map) getFieldValue( obj, getFieldForClass(Vendor.class, "preferences"))).putAll(new Creator<HashMap>(){ 
public HashMap create() {
HashMap map = new HashMap(  );
map.put("US", new Creator<HashMap>(){ 
public HashMap create() {
HashMap map = new HashMap(  );
map.put(CodeType.IT, "some code");
return map;
}}.create());
return map;
}}.create());
if (getFieldValue( obj, getFieldForClass(Vendor.class, "commissions")) == null) {
setFieldValue( obj, getFieldForClass(Vendor.class, "commissions"), new HashMap());

}
((Map) getFieldValue( obj, getFieldForClass(Vendor.class, "commissions"))).putAll(new Creator<HashMap>(){ 
public HashMap create() {
HashMap map = new HashMap(  );
return map;
}}.create());
return obj;
}}.create());
if (getFieldValue( obj, getFieldForClass(SomeDummyClass.class, "someFeeTypeCode")) == null) {
setFieldValue( obj, getFieldForClass(SomeDummyClass.class, "someFeeTypeCode"), new ArrayList());

}
((Collection) getFieldValue( obj, getFieldForClass(SomeDummyClass.class, "someFeeTypeCode"))).addAll(new Creator<ArrayList>(){ 
public ArrayList create() {
ArrayList col = new ArrayList(  );
col.add(FeeTypeCode.PERDAY);
col.add(FeeTypeCode.FPT);
return col;
}}.create());
setFieldValue( obj, getFieldForClass(SomeDummyClass.class, "somePrimitiveArray"), new Creator<int[]>(){ 
public int[] create() {
int[] array = new int[3];
array[0] = 3;
array[1] = 4;
array[2] = -6;
return array;
}}.create());
setFieldValue( obj, getFieldForClass(SomeDummyClass.class, "strings"), new Creator<String[]>(){ 
public String[] create() {
String[] array = new String[3];
array[0] = "lolo";
array[1] = "lala";
array[2] = "someString\n"
 + "With new line\tand\n"
 + "tab\\";
return array;
}}.create());
setFieldValue( obj, getFieldForClass(SomeDummyClass.class, "objects"), new Creator<Object[]>(){ 
public Object[] create() {
Object[] array = new Object[8];
array[0] = 13;
array[1] = new Date(1257736863515L) /* Mon Nov 09 00:21:03 GMT-03:00 2009*/ ;
array[2] = "lolo";
array[3] = CodeType.RC;
array[4] = ErrorContext.INVALID_PRICE_OPTION;
array[5] = ErrorContext.DISPLAY_ACTION;
array[6] = new Creator<VendorCommission>(){ 
public VendorCommission create() {
VendorCommission obj = new VendorCommission(  );
instancesMap.registerInstance( 7486844, obj);  /* @723d7c */
obj.setCommission(0.0);
obj.setTAXCommissionApplicable(true);
return obj;
}}.create();
array[7] = (Vendor) instancesMap.getInstance( 31054905) /* @1d9dc39 */ ;
return array;
}}.create());
setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someBigDecimal"), new BigDecimal( "456.7" ));
obj.setSomeBigInteger(new BigInteger( "345" ));
if (getFieldValue( obj, getFieldForClass(SomeDummyClass.class, "someSynchronizedCollection")) == null) {
setFieldValue( obj, getFieldForClass(SomeDummyClass.class, "someSynchronizedCollection"), new LinkedList());

}
((Collection) getFieldValue( obj, getFieldForClass(SomeDummyClass.class, "someSynchronizedCollection"))).addAll(new Creator<LinkedList>(){ 
public LinkedList create() {
LinkedList col = new LinkedList(  );
col.add((Vendor) instancesMap.getInstance( 31054905) /* @1d9dc39 */ );
col.add(123);
return col;
}}.create());
setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someSingleton"), SomeSingleton.getInstance());
setFieldValue( obj, getFieldForClass(SomeDummyClass.class, "someCurencies"), new Creator<Currency[]>(){ 
public Currency[] create() {
Currency[] array = new Currency[2];
array[0] = Currency.ARS;
array[1] = Currency.getCurrencyForUSA();
return array;
}}.create());
setFieldValue( obj, getFieldForClass( SomeDummyClass.class, "someDataTypeToBeIgnoredOnProcessing"), null);
return obj;
}}.create();
return generatedObject;
}}.create()
    );
  }
}
