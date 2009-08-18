package com.google.code.joto.datatype;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Vendor
    implements Serializable
{

    /**
     * Default country flag.
     */
    public static final String DEFAULT_COUNTRY = "DEFAULT";

    /**
     * State holder for the vendor code.
     */
    private String vendorCode = "";

    private boolean addEVoucherAsBillingReference = false;

    private String siParam;

    private int eVoucherNumberLength = 12;

    private String avNumberLength = "";

    private Map<String, Map<CodeType, String>> preferences = new HashMap<String, Map<CodeType, String>>();

    private Map<String, VendorCommission> commissions = new HashMap<String, VendorCommission>();

    public Vendor( String vendorCode )
    {
        setVendorCode( vendorCode );
    }

    public String getVendorCode()
    {
        return vendorCode;
    }

    public void setVendorCode( String vendorCode )
    {
        if ( vendorCode != null && !vendorCode.trim().equalsIgnoreCase( this.vendorCode ) )
            this.vendorCode = vendorCode.trim();
    }

    public boolean addCodeToCountry( String country, CodeType codeType, String code )
    {
        boolean result = false;
        if ( codeType != null && code != null && !"".equals( code.trim() ) )
        {
            if ( country == null )
                country = DEFAULT_COUNTRY;
            Map<CodeType, String> codes = preferences.get( country );
            if ( codes == null )
            {
                codes = new HashMap<CodeType, String>();
                preferences.put( country, codes );
            }
            codes.put( codeType, code );
            result = true;
        }
        return result;
    }

    public Set<String> getAvailableCountries()
    {
        Set<String> result = new HashSet<String>();
        result.addAll( preferences.keySet() );
        return result;
    }

    public String getCodeForCountry( String country, CodeType codeType )
    {
        String result = null;
        if ( codeType != null )
        {
            if ( country == null )
                country = DEFAULT_COUNTRY;
            Map<CodeType, String> codes = preferences.get( country );
            if ( codes != null )
                result = codes.get( codeType );
        }
        return result;
    }

    public Map<CodeType, String> getCodesForCountry( String country )
    {
        Map<CodeType, String> result = new HashMap<CodeType, String>();
        if ( country == null )
            country = DEFAULT_COUNTRY;
        Map<CodeType, String> codes = preferences.get( country );
        if ( codes != null )
            result.putAll( codes );
        return result;
    }

    public Set<CodeType> getCodeTypesAvailable( String country )
    {
        Set<CodeType> result = new HashSet<CodeType>();
        if ( country == null )
            country = DEFAULT_COUNTRY;
        Map<CodeType, String> codes = preferences.get( country );
        if ( codes != null )
            result.addAll( codes.keySet() );
        return result;
    }

    public void setCommission( String country, VendorCommission commission )
    {
        if ( country == null )
            country = DEFAULT_COUNTRY;
        this.commissions.put( country, commission );
    }

    public VendorCommission getCommission( String country )
    {
        if ( country == null )
            country = DEFAULT_COUNTRY;
        return (VendorCommission) this.commissions.get( country );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null || !( obj instanceof Vendor ) )
            return false;
        return equals( (Vendor) obj );
    }

    private boolean equals( Vendor vendor )
    {
        if ( vendor == this )
            return true;
        else
            return vendorCode.equalsIgnoreCase( vendor.getVendorCode().trim() );
    }

    @Override
    public int hashCode()
    {
        return vendorCode.hashCode();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( getClass().getName() ).append( "@" ).append( Integer.toHexString( System.identityHashCode( this ) ) )
            .append( "\n" );
        sb.append( "vendorCode: " ).append( getVendorCode() ).append( "\n" );
        sb.append( "preferences: " ).append( preferences ).append( "\n" );

        return sb.toString();
    }

    public boolean getAddEVoucherAsBillingReference()
    {
        return addEVoucherAsBillingReference;
    }

    public void setAddEVoucherAsBillingReference( boolean addEVoucherAsBillingReference )
    {
        this.addEVoucherAsBillingReference = addEVoucherAsBillingReference;
    }

    public String getSiParam()
    {
        return siParam;
    }

    public void setSiParam( String siParam )
    {
        this.siParam = siParam;
    }

    public int getEVoucherNumberLength()
    {
        return eVoucherNumberLength;
    }

    public void setEVoucherNumberLength( int voucherNumberLength )
    {
        eVoucherNumberLength = voucherNumberLength;
    }

    public String getAvNumberLength()
    {
        return avNumberLength;
    }

    public void setAvNumberLength( String avNumberLength )
    {
        this.avNumberLength = avNumberLength;
    }
}
