package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import edu.wustl.catissuecore.domain.Site;

/**
 * Entity implementation class for Entity: NewSpecimenLog
 * 
 */
@Entity
public class NewSiteLog implements Serializable, CaTissueLog
{

  /**
   * 
   */
  private static final long serialVersionUID = -3755581304287949942L;
  private Long id;
  private Integer version;
  private Site site;

  public NewSiteLog()
  {
    super();
  }

  @Id
  @GeneratedValue(generator = "newSiteGenerator")
  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  @Version
  public Integer getVersion()
  {
    return this.version;
  }

  public void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * @return the participant
   */
  @OneToOne
  @JoinColumn(name="SITE_ID")
  public Site getSite()
  {
    return this.site;
  }

  /**
   * @param site
   *          the participant to set
   */
  public void setSite(Site site)
  {
    this.site = site;
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.utility.entity.CaTissueLog#getCorrelationId()
   */
  @Override
  @Transient
  public String getCorrelationId()
  {
    return this.getSite().getId().toString();
  }

}
