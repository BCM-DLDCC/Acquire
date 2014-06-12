package edu.bcm.dldcc.big.rac.data;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 6:43:37 AM
 */
public enum Degree
{
  BS("BS"), 
  MS("MS"), 
  PHD("PhD"), 
  MD("MD"), 
  MPH("MPH"), 
  MPA("MPA"), 
  OTHER("Other");

  private String name;

  /**
   * 
   * @param name
   */
  private Degree(String name)
  {
    this.name = name;
  }

  public String toString()
  {
    return this.name;
  }
  
}