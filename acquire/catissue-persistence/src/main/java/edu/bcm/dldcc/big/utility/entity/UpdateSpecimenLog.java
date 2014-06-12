package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import edu.wustl.catissuecore.domain.Specimen;

/**
 * Entity implementation class for Entity: NewSpecimenLog
 * 
 */
@Entity
public class UpdateSpecimenLog implements Serializable, CaTissueLog
{

  /**
   * 
   */
  private static final long serialVersionUID = -3755581304287949942L;
  private Long id;
  private Specimen specimen;
  private Integer version;

  public UpdateSpecimenLog()
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
  @JoinColumn(name="SPECIMEN_ID")
  public Specimen getSpecimen()
  {
    return this.specimen;
  }

  /**
   * @param specimen
   *          the participant to set
   */
  public void setSpecimen(Specimen specimen)
  {
    this.specimen = specimen;
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

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.utility.entity.CaTissueLog#getCorrelationId()
   */
  @Override
  @Transient
  public String getCorrelationId()
  {
    return this.getSpecimen().getId().toString();
  }

}
