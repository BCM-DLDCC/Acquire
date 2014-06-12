package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import edu.wustl.catissuecore.domain.deintegration.SpecimenRecordEntry;

/**
 * Entity implementation class for Entity: NewSpecimenLog
 * 
 */
@Entity
public class DynamicExtensionUpdateLog implements Serializable, CaTissueLog
{

  /**
   * 
   */
  private static final long serialVersionUID = -3755581304287949942L;
  private Long id;
  private SpecimenRecordEntry specimenRecordEntry;
  private Integer version;
  private Integer warmIschemia;
  private String priorTreatment;

  public DynamicExtensionUpdateLog()
  {
    super();
  }

  @Id
  @GeneratedValue(generator = "newSpecimenGenerator")
  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @return the specimen
   */
  @OneToOne
  @JoinColumn(name="SPECIMEN_RECORD_ENTRY_ID")
  public SpecimenRecordEntry getSpecimenRecordEntry()
  {
    return this.specimenRecordEntry;
  }

  /**
   * @param specimenRecEntry
   *          the participant to set
   */
  public void setSpecimenRecordEntry(SpecimenRecordEntry specimenRecEntry)
  {
    this.specimenRecordEntry = specimenRecEntry;
  }

  /**
   * @return the version
   */
  @Version
  protected Integer getVersion()
  {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  protected void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * @return the warmIschemia
   */
  @Column(name="WARM_ISCHEMIA")
  public Integer getWarmIschemia()
  {
    return this.warmIschemia;
  }

  /**
   * @param warmIschemia the warmIschemia to set
   */
  public void setWarmIschemia(Integer warmIschemia)
  {
    this.warmIschemia = warmIschemia;
  }

  /**
   * @return the priorTreatment
   */
  @Column(name="PRIOR_TREATMENT")
  public String getPriorTreatment()
  {
    return this.priorTreatment;
  }

  /**
   * @param priorTreatment the priorTreatment to set
   */
  public void setPriorTreatment(String priorTreatment)
  {
    this.priorTreatment = priorTreatment;
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.utility.entity.CaTissueLog#getCorrelationId()
   */
  @Override
  @Transient
  public String getCorrelationId()
  {
    String id = "null";
    if(this.getSpecimenRecordEntry() != null)
    {
      id = this.getSpecimenRecordEntry().getSpecimen().getId().toString();
    }
    return id;
  }


}
