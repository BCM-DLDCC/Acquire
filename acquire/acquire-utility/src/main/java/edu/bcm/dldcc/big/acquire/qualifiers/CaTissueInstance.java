/**
 * 
 */
package edu.bcm.dldcc.big.acquire.qualifiers;

/**
 * This Enum represents the different caTissue instances that are accessed by
 * Acquire. 
 * @author pew
 *
 */
public enum CaTissueInstance
{
  TCRB("caTissueBase");
  
  private String persistenceUnit;
  
  /**
   * Private constructor
   * @param unitName the name of the Persistence Unit that this Enum represents.
   */
  private CaTissueInstance(String unitName)
  {
    this.persistenceUnit = unitName;
  }
  
  public String getPersistenceUnit()
  {
    return this.persistenceUnit;
  }
}
