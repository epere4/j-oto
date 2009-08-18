package com.google.code.joto.datatype;

import java.io.Serializable;

public class VendorCommission
implements
Serializable
{
  private double commission = 0.0;
  
  private boolean isTAXCommissionApplicable = true;
  

  public double getCommission()
  {
    return commission;
  }

  public void setCommission( double commission )
  {
    this.commission = commission;
  }

  public boolean isTAXCommissionApplicable()
  {
    return isTAXCommissionApplicable;
  }

  public void setTAXCommissionApplicable( boolean isTAXCommissionApplicable )
  {
    this.isTAXCommissionApplicable = isTAXCommissionApplicable;
  }

}
