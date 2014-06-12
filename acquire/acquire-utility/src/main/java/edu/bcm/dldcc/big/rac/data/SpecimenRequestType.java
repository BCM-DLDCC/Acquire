package edu.bcm.dldcc.big.rac.data;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 6:43:37 AM
 */
public enum SpecimenRequestType
{
  IF_AVAILABLE("If Available"),
  REQUIRED("Required");

  private String name;

  /**
   * 
   * @param name
   */
  private SpecimenRequestType(String name)
  {
    this.name = name;
  }

  public String toString()
  {
    return this.name;
  }
}