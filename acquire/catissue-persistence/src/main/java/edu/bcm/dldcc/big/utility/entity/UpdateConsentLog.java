package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;

/**
 * Entity implementation class for Entity: NewSpecimenLog
 * 
 */
@Entity
public class UpdateConsentLog implements Serializable, CaTissueLog
{

  /**
   * 
   */
  private static final long serialVersionUID = -3755581304287949942L;
  private Long id;
  private CollectionProtocolRegistration registration;
  private Integer version;

  public UpdateConsentLog()
  {
    super();
  }

  @Id
  @GeneratedValue(generator = "newParticipantGenerator")
  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @return the participant
   */
  @OneToOne
  @JoinColumn(name="COLL_PROT_REG_ID")
  public CollectionProtocolRegistration getRegistration()
  {
    return this.registration;
  }

  /**
   * @param participant
   *          the participant to set
   */
  public void setRegistration(CollectionProtocolRegistration participant)
  {
    this.registration = participant;
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
    String id = "null";
    if(this.getRegistration() != null)
    {
      id = this.getRegistration().getId().toString();
    }
    return id;
  }

}
