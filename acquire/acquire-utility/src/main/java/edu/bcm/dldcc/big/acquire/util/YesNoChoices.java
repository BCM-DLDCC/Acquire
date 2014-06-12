/**
 * 
 */
package edu.bcm.dldcc.big.acquire.util;

/**
 * @author pew
 *
 */
public enum YesNoChoices
{
  YES("Yes")
  { 
    @Override public boolean getBooleanValue()
    {
      return true;
    }
  },
  NO("No")
  { 
    @Override public boolean getBooleanValue()
    {
      return false;
    }
  },
  UNKNOWN("Unknown")
  {
    @Override public boolean getBooleanValue()
    {
      return false;
    }
  },
  NOT_APPLICABLE("N/A")
  {
    @Override public boolean getBooleanValue()
    {
      return false;
    }
  };
  
  private YesNoChoices(String name)
  {
    this.name = name;
  }
  
  public abstract boolean getBooleanValue();
  
  private String name;
  
  @Override
  public String toString()
  {
    return this.name;
  }
}
