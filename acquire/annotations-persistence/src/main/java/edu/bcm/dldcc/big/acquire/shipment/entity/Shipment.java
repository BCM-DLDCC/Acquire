/**
 * 
 */
package edu.bcm.dldcc.big.acquire.shipment.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import edu.bcm.dldcc.big.acquire.exception.TooManySamplesException;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * @author pew
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Shipment implements Serializable
{

  private Long id;
  private Integer version;
  private String externalCollaborator;
  private Date creationDate;
  private String title;
  private String externalShippingId;
  private SiteAnnotation collectionSite;
  
  /**
   * 
   */
  public Shipment()
  {
    super();
  }
  
  @Id
  @GeneratedValue(generator = "submissionSequencer")
  @SequenceGenerator(name = "submissionSequencer",
      sequenceName = "SUBMISSION_SEQ")
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

  private void setVersion(Integer version)
  {
    this.version = version;
  }

  @Size(max = 200)
  @NotNull
  public String getExternalCollaborator()
  {
    return externalCollaborator;
  }

  public void setExternalCollaborator(String externalCollaborator)
  {
    this.externalCollaborator = externalCollaborator;
  }

  @Temporal(TemporalType.DATE)
  public Date getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate(Date creationDate)
  {
    this.creationDate = creationDate;
  }
  
  @PrePersist
  public void create()
  {
    if(this.getCreationDate() == null)
    {
      this.setCreationDate(new Date());
    }
  }

  /**
   * @return the title
   */
  @NotNull
  public String getTitle()
  {
    return this.title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title)
  {
    this.title = title;
  }
  
  /**
   * @return the externalShippingId
   */
  public String getExternalShippingId()
  {
    return this.externalShippingId;
  }

  /**
   * @param externalShippingId the externalShippingId to set
   */
  public void setExternalShippingId(String externalShippingId)
  {
    this.externalShippingId = externalShippingId;
  }

  /**
   * @return the collectionSite
   */
  @ManyToOne
  public SiteAnnotation getCollectionSite()
  {
    return this.collectionSite;
  }

  /**
   * @param collectionSite the collectionSite to set
   */
  public void setCollectionSite(SiteAnnotation collectionSite)
  {
    this.collectionSite = collectionSite;
  }
  
  public abstract void addSample(AliquotAnnotation annotation,
      Specimen specimen, Participant patient,
      ParticipantAnnotation patientAnnotation) throws TooManySamplesException;
  
  public abstract void removeSample(SampleData sample);
  
  


}
