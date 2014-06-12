/**
 * 
 */
package edu.bcm.dldcc.big.acquire.util;


/**
 * @author pew
 * 
 */
public enum SpecimenStatus
{
  CONSENT("Consent Not Recorded", "Consent Needed"),
  SHIPPED_CELL_LAB("Shipped to Cell Lab", "Shipped - Cell Lab"),
  PATHOLOGY("Awaiting Pathology", "Awaiting Path"),
  NA_LAB_QUALIFIED("Qualified for NA Lab", "NA Lab Qualified"),
  SHIPPED_NA_LAB("Shipped to NA Lab", "Shipped - NA Lab"),
  TCGA_QUALIFIED("Qualified for TCGA", "TCGA Qualified"),
  SHIPPED_TCGA("Shipped to TCGA", "Shipped - TCGA"),
  CONDITION("Missing Warm Ischemia Time and Prior Treatment", "WIT and Prior tx Needed");

  private SpecimenStatus(String title, String display)
  {
    this.title = title;
    this.displayName = display;
  }

  private String title;
  private String displayName;

  public String toString()
  {
    return this.title;
  }

  public String getDisplay()
  {
    return this.displayName;
  }

}
