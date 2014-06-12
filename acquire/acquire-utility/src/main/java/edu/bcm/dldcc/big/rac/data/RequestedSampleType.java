package edu.bcm.dldcc.big.rac.data;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 6:43:37 AM
 */
public enum RequestedSampleType
{
  FROZEN("Frozen Solid Tissue", true, true, "mg")
  {
    {
      this.containers = true;
      this.containerLabel = "aliquots";
    }

  },
  SLIDE("Stained/Unstained Slides", true, true, "Slides"),
  RNA("RNA", true, true, "µg")
  {
    {
      this.containers = true;
      this.containerLabel = "aliquots";
    }

  },
  DNA("DNA", true, true, "µg")
  {
    {
      this.containers = true;
      this.containerLabel = "aliquots";
    }

  },
  PARAFFIN("Paraffin Block", false, true, "Slices")
  {
    {
      this.secondaryMeasure = true;
      this.secondaryUnitOfMeasure = "Micron Thickness";
    }

  },
  CELL_LINE("Cell Line", true, false, "cells"),
  XENOGRAFT("Xenograft", true, false, "mm")
  {
    {
      this.secondaryMeasure = true;
      this.secondaryUnitOfMeasure = "mm";
      this.tertiaryMeasure = true;
      this.tertiaryUnitOfMeasure = "mm";
    }
    
  },
  WHOLE_BLOOD("Whole Blood", false, true, "ml")
  {
    {
      this.containers = true;
      this.containerLabel = "Tubes";
    }

  },
  TRANSFORMED_CELL_LINE("EBV Transformed Cell Line", false, true, "cells"),
  SINGLE_PARAFFIN("Single-Tumor Paraffin Block", true, false, "Slices")
  {
    {
      this.secondaryMeasure = true;
      this.secondaryUnitOfMeasure = "Micron Thickness";
    }

  };

  private String name;
  private boolean tumor;
  private boolean normal;
  private String unitOfMeasure;
  protected boolean containers = false;
  protected String containerLabel = "";
  protected boolean secondaryMeasure = false;
  protected boolean tertiaryMeasure = false;
  protected String secondaryUnitOfMeasure = "";
  protected String tertiaryUnitOfMeasure = "";

  /**
   * 
   * @param name
   * @param tumor
   * @param normal
   */
  private RequestedSampleType(String name, boolean tumor, boolean normal,
      String measure)
  {
    this.name = name;
    this.tumor = tumor;
    this.normal = normal;
    this.unitOfMeasure = measure;
  }

  public String toString()
  {
    return this.name;
  }

  public boolean tumorType()
  {
    return this.tumor;
  }

  public boolean normalType()
  {
    return this.normal;
  }

  public static Set<RequestedSampleType> tumorTypes()
  {
    EnumSet<RequestedSampleType> tumors =
        EnumSet.noneOf(RequestedSampleType.class);
    for (RequestedSampleType current : RequestedSampleType.values())
    {
      if (current.tumorType())
      {
        tumors.add(current);
      }

    }

    return tumors;
  }

  public static Set<RequestedSampleType> normalTypes()
  {
    EnumSet<RequestedSampleType> normals =
        EnumSet.noneOf(RequestedSampleType.class);
    for (RequestedSampleType current : RequestedSampleType.values())
    {
      if (current.normalType())
      {
        normals.add(current);
      }

    }

    return normals;
  }

  /**
   * @return the hasContainers
   */
  public boolean containers()
  {
    return this.containers;
  }

  /**
   * @return the containerLabel
   */
  public String containerLabel()
  {
    return this.containerLabel;
  }

  /**
   * @return the secondaryMeasure
   */
  public boolean secondaryMeasure()
  {
    return this.secondaryMeasure;
  }

  /**
   * @return the tertiaryMeasure
   */
  public boolean tertiaryMeasure()
  {
    return this.tertiaryMeasure;
  }

  /**
   * @return the secondaryUnitOfMeasure
   */
  public String secondaryUnitOfMeasure()
  {
    return this.secondaryUnitOfMeasure;
  }

  /**
   * @return the tertiaryUnityOfMeasure
   */
  public String tertiaryUnitOfMeasure()
  {
    return this.tertiaryUnitOfMeasure;
  }

  /**
   * @return the unitOfMeasure
   */
  public String unitOfMeasure()
  {
    return this.unitOfMeasure;
  }
}