package edu.bcm.dldcc.big.submission.nalab.data;

public enum SampleType
{
  PRIMARY_TUMOR("Primary Tumor", "Z"),
  RECURRENT_TUMOR("Recurrent Tumor", "X"),
  METASTATIC("Metastatic", "W"),
  XENOGRAFT("Xenograft", "U"),
  TUMOR_LINE("Tumor Cell Line", "T"),
  SOLID_NORMAL("Solid Tissue Normal", "S"),
  BUCCAL_NORMAL("Buccal Cell Normal", "R"),
  MARROW_NORMAL("Bone Marrow Normal", "Q"),
  PRIMARY_BLOOD("Primary Blood Derived Cancer", "O"),
  RECURRENT_BLOOD("Recurrent Blood Derived Cancer", "N"),
  BLOOD_NORMAL("Blood Derived Normal", "M"),
  CELL_PELLET("Normal Cell Pellet", "P"),
  SALIVA("Saliva", "L");
  
  private String value;
  private String code;
  
  private SampleType(String value, String typeCode)
  {
    this.value = value;
    this.code = typeCode;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public String getCode()
  {
    return this.code;
  }


}
