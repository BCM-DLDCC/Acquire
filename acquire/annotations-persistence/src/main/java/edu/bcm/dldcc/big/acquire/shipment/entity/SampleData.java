/**
 * 
 */
package edu.bcm.dldcc.big.acquire.shipment.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * @author pew
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SampleData implements Serializable
{

  private Long id;
  private Integer version;
  private String specimenUUID;

  /**
   * 
   */
  public SampleData()
  {
    super();
  }

  @Id
  @GeneratedValue(generator = "sampleDataGenerator")
  @SequenceGenerator(name = "sampleDataGenerator",
      sequenceName = "SAMPLE_DATA_SEQ")
  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  @Version
  public Integer getVersion()
  {
    return version;
  }

  public void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * @return the specimenUUID
   */
  public String getSpecimenUUID()
  {
    return this.specimenUUID;
  }

  /**
   * @param specimenUUID
   *          the specimenUUID to set
   */
  public void setSpecimenUUID(String specimenUUID)
  {
    this.specimenUUID = specimenUUID;
  }

  public void populateSample(AliquotAnnotation annotation,
      Specimen specimen, Participant patient,
      ParticipantAnnotation patientAnnotation)
  {
    this.setSpecimenUUID(annotation.getEntityId());
  }

}
