/**
 * 
 */
package edu.bcm.dldcc.big.clinical.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.validator.constraints.Length;

import edu.bcm.dldcc.big.acquire.annotations.integration.CaTissueEntity;
import edu.bcm.dldcc.big.acquire.util.EntityAnnotation;
import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.acquire.util.YesNoChoices;
import edu.bcm.dldcc.big.clinical.values.entity.MStaging;
import edu.bcm.dldcc.big.clinical.values.entity.NStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TumorGrade;
import edu.bcm.dldcc.big.clinical.values.entity.TumorStage;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * @author pew
 * 
 */
@Entity
@Audited
public class SpecimenAnnotation implements EntityAnnotation
{

  /**
   * 
   */
  private Integer version;
  private String entityId;
  private TumorGrade tumorGrade;
  private TStaging tStaging;
  private String tStagingOther;
  private MStaging mStaging;
  private String mStagingOther;
  private NStaging nStaging;
  private String nStagingOther;
  private TumorStage tumorStage;
  private String tumorStageOther;
  private String rationale;
  private String filename;
  private Integer warmIschemiaTime;
  private YesNoChoices priorTreatment;
  private Integer ageAtCollection;
  private Set<SpecimenStatus> status = new HashSet<SpecimenStatus>();
  private List<AliquotAnnotation> aliquots = new ArrayList<AliquotAnnotation>();
  private AliquotAnnotation aliquotFields;
  private Date createDate;
  private ParticipantAnnotation patient;
  private Boolean normal = false;
  private Long legacyId;
  private String fileObjectId;
  
  /**
   * 
   */
  public SpecimenAnnotation()
  {
    super();
    this.status.add(SpecimenStatus.CONSENT);
    this.status.add(SpecimenStatus.PATHOLOGY);
    this.status.add(SpecimenStatus.CONDITION);
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
   * @param version
   *          the version to set
   */
  public void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * @return the specimenId
   */
  @Override
  @CaTissueEntity(entity = Specimen.class)
  @Id
  public String getEntityId()
  {
    return this.entityId;
  }

  /**
   * @param specimenId
   *          the specimenId to set
   */
  @Override
  public void setEntityId(String specimenId)
  {
    this.entityId = specimenId;
  }

  /**
   * @return the tumorGrade
   */
  @ManyToOne(fetch=FetchType.LAZY)
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public TumorGrade getTumorGrade()
  {
    return this.tumorGrade;
  }

  /**
   * @param tumorGrade
   *          the tumorGrade to set
   */
  public void setTumorGrade(TumorGrade tumorGrade)
  {
    this.tumorGrade = tumorGrade;
  }

  /**
   * @return the tStaging
   */
  @ManyToOne(fetch=FetchType.LAZY)
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public TStaging getTStaging()
  {
    return this.tStaging;
  }

  /**
   * @param tStaging
   *          the tStaging to set
   */
  public void setTStaging(TStaging tStaging)
  {
    this.tStaging = tStaging;
  }

  /**
   * @return the mStaging
   */
  @ManyToOne(fetch=FetchType.LAZY)
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public MStaging getMStaging()
  {
    return this.mStaging;
  }

  /**
   * @param mStaging
   *          the mStaging to set
   */
  public void setMStaging(MStaging mStaging)
  {
    this.mStaging = mStaging;
  }

  /**
   * @return the nStaging
   */
  @ManyToOne(fetch=FetchType.LAZY)
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public NStaging getNStaging()
  {
    return this.nStaging;
  }

  /**
   * @param nStaging
   *          the nStaging to set
   */
  public void setNStaging(NStaging nStaging)
  {
    this.nStaging = nStaging;
  }

  /**
   * @return the tumorStage
   */
  @ManyToOne(fetch=FetchType.LAZY)
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  public TumorStage getTumorStage()
  {
    return this.tumorStage;
  }

  /**
   * @param tumorStage
   *          the tumorStage to set
   */
  public void setTumorStage(TumorStage tumorStage)
  {
    this.tumorStage = tumorStage;
  }

  /**
   * @return the fileObjectIds
   */
  public String getFilename()
  {
    return this.filename;
  }

  /**
   * @param fileObjectIds
   *          the fileObjectIds to set
   */
  public void setFilename(String filename)
  {
    this.filename = filename;
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
    SpecimenAnnotation other = (SpecimenAnnotation) obj;
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
   * @return the status
   */
  @ElementCollection
  @Enumerated
  public Set<SpecimenStatus> getStatus()
  {
    return this.status;
  }

  /**
   * @param status
   *          the status to set
   */
  public void setStatus(Set<SpecimenStatus> status)
  {
    this.status = status;
  }

  /**
   * @return the rationale
   */
  public String getRationale()
  {
    return this.rationale;
  }

  /**
   * @param rationale
   *          the rationale to set
   */
  @Length(max = 4000)
  public void setRationale(String rationale)
  {
    this.rationale = rationale;
  }

  /**
   * @return the warmEschemiaTime
   */
  public Integer getWarmIschemiaTime()
  {
    return this.warmIschemiaTime;
  }

  /**
   * @param warmEschemiaTime
   *          the warmEschemiaTime to set
   */
  public void setWarmIschemiaTime(Integer warmIschemiaTime)
  {
    this.warmIschemiaTime = warmIschemiaTime;
  }

  /**
   * @return the priorTreatment
   */
  public YesNoChoices getPriorTreatment()
  {
    return this.priorTreatment;
  }

  /**
   * @param priorTreatment
   *          the priorTreatment to set
   */
  public void setPriorTreatment(YesNoChoices priorTreatment)
  {
    this.priorTreatment = priorTreatment;
    
  }

  /**
   * @return the ageAtCollection
   */
  public Integer getAgeAtCollection()
  {
    return this.ageAtCollection;
  }

  /**
   * @param ageAtCollection
   *          the ageAtCollection to set
   */
  public void setAgeAtCollection(Integer ageAtCollection)
  {
    this.ageAtCollection = ageAtCollection;
  }

  /**
   * @return the aliquots
   */
  @OneToMany(mappedBy = "parent")
  public List<AliquotAnnotation> getAliquots()
  {
    return this.aliquots;
  }

  /**
   * @param aliquots
   *          the aliquots to set
   */
  public void setAliquots(List<AliquotAnnotation> aliquots)
  {
    this.aliquots = aliquots;
  }

  public void addAliquot(AliquotAnnotation aliquot)
  {
    this.getAliquots().add(aliquot);
    aliquot.setParent(this);
  }

  /**
   * @return the aliquotFields
   */
  @OneToOne
  public AliquotAnnotation getAliquotFields()
  {
    return this.aliquotFields;
  }

  /**
   * @param aliquotFields
   *          the aliquotFields to set
   */
  public void setAliquotFields(AliquotAnnotation aliquotFields)
  {
    this.aliquotFields = aliquotFields;
    if(aliquotFields != null)
    {
      aliquotFields.setSpecimenFields(this);
    }
  }

  /**
   * @return the createDate
   */
  @Temporal(TemporalType.TIMESTAMP)
  public Date getCreateDate()
  {
    return this.createDate;
  }

  /**
   * @param createDate
   *          the createDate to set
   */
  public void setCreateDate(Date createDate)
  {
    this.createDate = createDate;
  }

  /**
   * @return the patient
   */
  @ManyToOne(fetch=FetchType.LAZY)
  public ParticipantAnnotation getPatient()
  {
    return this.patient;
  }

  /**
   * @param patient
   *          the patient to set
   */
  public void setPatient(ParticipantAnnotation patient)
  {
    this.patient = patient;
  }

  /**
   * @return the normal
   */
  public Boolean getNormal()
  {
    return this.normal;
  }

  /**
   * @param normal
   *          the normal to set
   */
  public void setNormal(Boolean normal)
  {
    this.normal = normal;
  }

  /**
   * @return the tStagingOther
   */
  public String getTStagingOther()
  {
    return this.tStagingOther;
  }

  /**
   * @param tStagingOther
   *          the tStagingOther to set
   */
  public void setTStagingOther(String tStagingOther)
  {
    this.tStagingOther = tStagingOther;
  }

  /**
   * @return the mStagingOther
   */
  public String getMStagingOther()
  {
    return this.mStagingOther;
  }

  /**
   * @param mStagingOther
   *          the mStagingOther to set
   */
  public void setMStagingOther(String mStagingOther)
  {
    this.mStagingOther = mStagingOther;
  }

  /**
   * @return the nStagingOther
   */
  public String getNStagingOther()
  {
    return this.nStagingOther;
  }

  /**
   * @param nStagingOther
   *          the nStagingOther to set
   */
  public void setNStagingOther(String nStagingOther)
  {
    this.nStagingOther = nStagingOther;
  }

  /**
   * @return the tumorStageOther
   */
  public String getTumorStageOther()
  {
    return this.tumorStageOther;
  }

  /**
   * @param tumorStageOther
   *          the tumorStageOther to set
   */
  public void setTumorStageOther(String tumorStageOther)
  {
    this.tumorStageOther = tumorStageOther;
  }

  /**
   * @return the legacyId
   */
  public Long getLegacyId()
  {
    return this.legacyId;
  }

  /**
   * @param legacyId the legacyId to set
   */
  public void setLegacyId(Long legacyId)
  {
    this.legacyId = legacyId;
  }

  /**
   * @return the fileObjectId
   */
  public String getFileObjectId()
  {
    return this.fileObjectId;
  }

  /**
   * The id used to store the attached file in the filesystem. This value
   * should only be set by the file handling code.
   * 
   * @param fileObjectId the fileObjectId to set
   */
  public void setFileObjectId(String fileObjectId)
  {
    this.fileObjectId = fileObjectId;
  }
  
  public void checkPathologyStatus()
  {
    boolean pathologyNeeded = true;

    if (this.getAliquotFields().getPercentNecrosis() != null
        || this.getAliquotFields().getPercentNuclei() != null)
    {
      pathologyNeeded = false;
    }
    else
    {
      for (AliquotAnnotation aliquot : this.getAliquots())
      {
        if (aliquot.getPercentNecrosis() != null
            || aliquot.getPercentNuclei() != null)
        {
          pathologyNeeded = false;
          break;
        }
      }
    }

    if (pathologyNeeded)
    {
      this.getStatus().add(SpecimenStatus.PATHOLOGY);
    }
    else
    {
      this.getStatus().remove(SpecimenStatus.PATHOLOGY);
    }
  }
  
  @PrePersist
  @PreUpdate
  private void checkDynamicExtensionStatus()
  {
    if (this.getPriorTreatment() != null
        && this.getWarmIschemiaTime() != null)
    {
      this.getStatus().remove(SpecimenStatus.CONDITION);
    }
    else
    {
      this.getStatus().add(SpecimenStatus.CONDITION);
    }
  }

 

}
