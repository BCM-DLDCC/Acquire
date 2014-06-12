package edu.bcm.dldcc.big.submission.nalab.data;

public enum PatientRole
{
  AFFECTED("Affected"),
  NOT_AFFECTED("Not affected"),
  CONTROL("Control"),
  FAMILY_CONTROL("Family control"),
  POPULATION_CONTROL("Population control"),
  NA("NA");
  
  private String value;
  
  private PatientRole(String value)
  {
    this.value = value;
  }
  
  public String getValue()
  {
    return this.value;
  }

}
