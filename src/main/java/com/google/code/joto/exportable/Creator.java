package com.google.code.joto.exportable;

/**
 * The idea of this interface is that you can create a big object in only one sentence instantiating
 * this as an anonymous class. This is useful if you need to pass this newly created to a method in
 * only one line.
 * <p>
 * Example:
 * 
 * <pre>
 * invokeSomeMethodThatReceivesAVendor(new new Creator&lt;Vendor&gt;() {
 *      public Vendor create() {
 *          Vendor obj = new Vendor( &quot;&quot; );
 *          obj.setVendorCode( &quot;Dollar&quot; );
 *          obj.setAddEVoucherAsBillingReference( false );
 *          return obj;
 *      }}.create());
 * </pre>
 * @author Liliana.nu
 * @author epere4
 * @param <E> the type of object this interface will create.
 */
public interface Creator<E>
{
    /**
     * @return an instance of type E fully configured.
     */
    public E create();
}
