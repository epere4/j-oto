/**
 * 
 */
package com.google.code.joto;

/**
 * @author epere4
 * @author liliana.nu
 */
public class Joto
{
    private String name;
    private String version;

    public String getName()
    {
        return name;
    }
    public void setName( String name )
    {
        this.name = name;
    }
    public String getVersion()
    {
        return version;
    }
    public void setVersion( String version )
    {
        this.version = version;
    }
    public void setDefaultValues()
    {
        this.name = "j-oto";
        this.version = "1.0";
    }
}
