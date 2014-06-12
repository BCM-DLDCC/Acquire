/**
 * 
 */
package edu.bcm.dldcc.big.acquire.event;

import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * An event to indicate that a specimen has been updated, and provide the 
 * updated Specimen, as well as the corresponding SpecimenAnnotation and 
 * information about which instance of caTissue the Specimen is stored in.
 * 
 * @author pew
 * 
 */
public class SpecimenUpdateEvent
{
  private Specimen specimen;
  private SpecimenAnnotation annotation;
  private CaTissueInstance instance;

  /**
   * 
   */
  public SpecimenUpdateEvent()
  {
    super();
  }

  /**
   * Constructor to specify the relevant information.
   * 
   * @param specimen
   * @param annotation
   * @param instance
   */
  public SpecimenUpdateEvent(Specimen specimen, SpecimenAnnotation annotation,
      CaTissueInstance instance)
  {
    this();
    this.specimen = specimen;
    this.annotation = annotation;
    this.instance = instance;
  }

  /**
   * @return the specimen
   */
  public Specimen getSpecimen()
  {
    return this.specimen;
  }

  /**
   * @return the annotation
   */
  public SpecimenAnnotation getAnnotation()
  {
    return this.annotation;
  }

  /**
   * @return the instance
   */
  public CaTissueInstance getInstance()
  {
    return this.instance;
  }

}
