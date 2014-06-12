package edu.bcm.dldcc.big.rac.data;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 6:43:37 AM
 */
public enum InstitutionType
{
  ACADEMIC("Academic"),
  GOVERNMENT("Government"),
  HOSPITAL("Hospital"),
  COMMERCIAL("Commercial"),
  NON_PROFIT("Non Profit");

  private String name;

  /**
   * 
   * @param name
   */
  private InstitutionType(String name)
  {
    this.name = name;
  }

  public String toString()
  {
    return this.name;
  }
}