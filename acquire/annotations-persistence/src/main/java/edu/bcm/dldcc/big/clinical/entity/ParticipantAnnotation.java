package edu.bcm.dldcc.big.clinical.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.acquire.annotations.integration.CaTissueEntity;
import edu.bcm.dldcc.big.acquire.util.EntityAnnotation;
import edu.wustl.catissuecore.domain.Participant;

@Entity
@Audited
public class ParticipantAnnotation implements EntityAnnotation
{

  private static final long serialVersionUID = -2917275043293994795L;

  private String entityId;
  
  private Date vitalStatusDate;
  
  private Date consentDate;
  
  private String consentSignatory;  
  
  private String consentWitness;
  
  private String expandedVitalStatus;
  
  private List<SpecimenAnnotation> specimens = 
      new ArrayList<SpecimenAnnotation>();

  @Id
  @Override
  @CaTissueEntity(entity = Participant.class)
  public String getEntityId()
  {
    return entityId;
  }

  public void setEntityId(String participantId)
  {
    this.entityId = participantId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((this.entityId == null) ? 0 : this.entityId.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ParticipantAnnotation other = (ParticipantAnnotation) obj;
    if (this.entityId == null)
    {
      if (other.entityId != null)
        return false;
    }
    else if (!this.entityId.equals(other.entityId))
      return false;
    return true;
  }

  /**
   * @return the vitalStatusDate
   */
  @Temporal(TemporalType.DATE)
  public Date getVitalStatusDate()
  {
    return this.vitalStatusDate;
  }

  /**
   * @param vitalStatusDate the vitalStatusDate to set
   */
  public void setVitalStatusDate(Date vitalStatusDate)
  {
    this.vitalStatusDate = vitalStatusDate;
  }

  /**
   * @return the consentDate
   */
  @Temporal(TemporalType.DATE)
  public Date getConsentDate()
  {
    return this.consentDate;
  }

  /**
   * @param consentDate the consentDate to set
   */
  public void setConsentDate(Date consentDate)
  {
    this.consentDate = consentDate;
  }

  /**
   * @return the consentSignatory
   */
  public String getConsentSignatory()
  {
    return this.consentSignatory;
  }

  /**
   * @param consentSignatory the consentSignatory to set
   */
  public void setConsentSignatory(String consentSignatory)
  {
    this.consentSignatory = consentSignatory;
  }

  /**
   * @return the consentWitness
   */
  public String getConsentWitness()
  {
    return this.consentWitness;
  }

  /**
   * @param consentWitness the consentWitness to set
   */
  public void setConsentWitness(String consentWitness)
  {
    this.consentWitness = consentWitness;
  }

  /**
   * @return the expandedVitalStatus
   */
  public String getExpandedVitalStatus()
  {
    return this.expandedVitalStatus;
  }

  /**
   * @param expandedVitalStatus the expandedVitalStatus to set
   */
  public void setExpandedVitalStatus(String expandedVitalStatus)
  {
    this.expandedVitalStatus = expandedVitalStatus;
  }

  /**
   * @return the specimen
   */
  @OneToMany(mappedBy="patient", fetch=FetchType.LAZY)
  public List<SpecimenAnnotation> getSpecimens()
  {
    return this.specimens;
  }

  /**
   * @param specimen the specimen to set
   */
  public void setSpecimens(List<SpecimenAnnotation> specimen)
  {
    this.specimens = specimen;
  }
}
