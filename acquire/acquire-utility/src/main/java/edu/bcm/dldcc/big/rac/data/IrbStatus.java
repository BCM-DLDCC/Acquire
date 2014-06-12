package edu.bcm.dldcc.big.rac.data;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 6:43:37 AM
 */
public enum IrbStatus
{
  APPROVED("Approved"),
  PENDING("Pending"),
  EXEMPT("Exempt");

  private String name;

  /**
   * 
   * @param name
   */
  private IrbStatus(String name)
  {
    this.name = name;
  }

  public String toString()
  {
    return this.name;
  }
}