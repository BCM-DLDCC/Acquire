package edu.bcm.dldcc.big.submission.nalab.data;

public enum ProbandRelationship
{
  PROBAND("Proband"),
  FATHER("Father"),
  MOTHER("Mother"),
  BROTHER("Brother"),
  SISTER("Sister"),
  SON("Son"),
  DAUGHTER("Daughter"),
  UNCLE("Uncle"),
  AUNT("Aunt"),
  GRANDFATHER("Grandfather"),
  GRANDMOTHER("Grandmother"),
  GRANDSON("Grandson"),
  GRANDDAUGHTER("Granddaughter"),
  GREAT_GRANDFATHER("Great grandfather"),
  GREAT_GRANDMOTHER("Great grandmother"),
  GREAT_GRANDSON("Great grandson"),
  GREAT_GRANDDAUGHTER("Great granddaughter"),
  COUSIN("Cousin"),
  NONE("No relation");
  
  private String value;
  
  private ProbandRelationship(String value)
  {
    this.value = value;
  }
  
  public String getValue()
  {
    return this.value;
  }

}
