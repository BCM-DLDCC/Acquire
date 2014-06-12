package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 8:34:06 AM
 */
@Entity
@Audited
public class Certification implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 1464578099760187239L;
  private Long id;
  private Integer version;
  private Boolean response;
  private AcquireUserInformation signature;
  private Date timestamp;
  private Application application;

  public Certification()
  {
    super();
  }
  
  /**
   * @return the id
   */
  @Id
  public Long getId()
  {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @return the version
   */
  @Version
  public Integer getVersion()
  {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * @return the response
   */
  public Boolean getResponse()
  {
    return this.response;
  }

  /**
   * @param response the response to set
   */
  public void setResponse(Boolean response)
  {
    this.response = response;
  }

  /**
   * @return the signatureId
   */
  @ManyToOne
  public AcquireUserInformation getSignature()
  {
    return this.signature;
  }

  /**
   * @param signatureId the signatureId to set
   */
  public void setSignature(AcquireUserInformation signatureId)
  {
    this.signature = signatureId;
  }

  /**
   * @return the timestamp
   */
  @Temporal(TemporalType.TIMESTAMP)
  public Date getTimestamp()
  {
    return this.timestamp;
  }

  /**
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(Date timestamp)
  {
    this.timestamp = timestamp;
  }

  /**
   * @return the application
   */
  @OneToOne
  @MapsId
  public Application getApplication()
  {
    return this.application;
  }

  /**
   * @param application the application to set
   */
  public void setApplication(Application application)
  {
    this.application = application;
  }
  
  public void signCertification(AcquireUserInformation signature)
  {
    this.setSignature(signature);
    this.setTimestamp(new Date());
  }

}