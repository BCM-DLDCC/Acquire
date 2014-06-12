/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.acquire.util.YesNoChoices;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.NaLabAnnotation;
import edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.clinical.values.entity.MStaging;
import edu.bcm.dldcc.big.clinical.values.entity.NStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TumorGrade;
import edu.bcm.dldcc.big.clinical.values.entity.TumorStage;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier;
import edu.wustl.catissuecore.domain.Race;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * @author pew
 * 
 */
public class SearchResult implements Serializable
{

  private Participant participant;

  private Specimen specimen;

  private AliquotAnnotation annotation;
  
  private AbstractSpecimen parent;

  /**
   * 
   */
  public SearchResult()
  {
    super();
  }

  /**
   * @return the uuid
   */
  public String getUuid()
  {
    return this.getsAnnotation().getEntityId();
  }

  /**
   * @return the participant
   */
  public Participant getParticipant()
  {
    return this.participant;
  }

  /**
   * @param participant
   *          the participant to set
   */
  public void setParticipant(Participant participant)
  {
    this.participant = participant;
  }

  /**
   * @return the pAnnotation
   */
  public ParticipantAnnotation getpAnnotation()
  {
    return this.getsAnnotation().getPatient();
  }

  /**
   * @return the specimen
   */
  public Specimen getSpecimen()
  {
    return this.specimen;
  }

  /**
   * @param specimen
   *          the specimen to set
   */
  public void setSpecimen(Specimen specimen)
  {
    this.specimen = specimen;
  }

  /**
   * @return the sAnnotation
   */
  public SpecimenAnnotation getsAnnotation()
  {
    SpecimenAnnotation specimen = this.getAnnotation().getParent();
    if (specimen == null)
    {
      specimen = this.getAnnotation().getSpecimenFields();
    }
    return specimen;
  }

  /**
   * @return the aAnnotation
   */
  public AliquotAnnotation getAnnotation()
  {
    return this.annotation;
  }

  /**
   * @param aAnnotation
   *          the aAnnotation to set
   */
  public void setAnnotation(AliquotAnnotation aAnnotation)
  {
    this.annotation = aAnnotation;
  }

  public String getSpecimenLabel()
  {
    return this.getSpecimen().getLabel();
  }

  public String getSpecimenBarcode()
  {
    return this.getSpecimen().getBarcode();
  }

  public Long getParticipantId()
  {
    return this.getParticipant().getId();
  }

  public Date getSubmissionDate()
  {
    return this.getsAnnotation().getCreateDate();
  }

  public List<SpecimenStatus> getSpecimenStatus()
  {
    Set<SpecimenStatus> allStatus = new HashSet<SpecimenStatus>(this
        .getsAnnotation().getStatus());
    allStatus.addAll(this.getAnnotation().getStatus());
    return Collections
        .unmodifiableList(new ArrayList<SpecimenStatus>(allStatus));
  }

  public String getConcatenatedStatus()
  {
    String concatenatedStatus = "";

    for (SpecimenStatus status : this.getSpecimenStatus())
    {
      if (status != null && status.getDisplay().length() > 0)
      {
        concatenatedStatus = concatenatedStatus + status.getDisplay() + ", ";
      }
    }

    // lop off the final two chars which are the extra commas, making sure we
    // have enough text to do this
    int endIndex = concatenatedStatus.length() - 2;

    if (endIndex < 0)
    {
      endIndex = 1;
    }
    return concatenatedStatus.substring(0, endIndex);
  }

  public String getSpecimenId()
  {
    String id = this.getUuid();
    if (this.getAnnotation().getParent() != null)
    {
      id = this.getAnnotation().getParent().getEntityId();
    }
    return id;
  }

  public String getDiseaseDiagnosis()
  {
    String value = "";
    if (this.getSpecimen().getSpecimenCollectionGroup() != null)
    {
      value = this.getSpecimen().getSpecimenCollectionGroup()
          .getClinicalDiagnosis();
    }
    return value;
  }

  public String getDiseaseSite()
  {
    String value = "";
    if (this.getSpecimen().getSpecimenCharacteristics() != null)
    {
      value = this.getSpecimen().getSpecimenCharacteristics().getTissueSite();
    }
    return value;
  }

  public TumorGrade getTumorGrade()
  {
    return this.getsAnnotation().getTumorGrade();
  }

  public void setTumorGrade(TumorGrade grade)
  {
    this.getsAnnotation().setTumorGrade(grade);
  }

  public TStaging getStagingT()
  {
    return this.getsAnnotation().getTStaging();
  }

  public void setStagingT(TStaging stage)
  {
    this.getsAnnotation().setTStaging(stage);
  }

  public NStaging getStagingN()
  {
    return this.getsAnnotation().getNStaging();
  }

  public void setStagingN(NStaging stage)
  {
    this.getsAnnotation().setNStaging(stage);
  }

  public MStaging getStagingM()
  {
    return this.getsAnnotation().getMStaging();
  }

  public void setStagingM(MStaging stage)
  {
    this.getsAnnotation().setMStaging(stage);
  }

  public TumorStage getTumorStage()
  {
    return this.getsAnnotation().getTumorStage();
  }

  public void setTumorStage(TumorStage stage)
  {
    this.getsAnnotation().setTumorStage(stage);
  }

  public String getPathReport()
  {
    return this.getsAnnotation().getFilename();
  }

  public void setPathReport(String filename)
  {
    this.getsAnnotation().setFilename(filename);
  }

  public String getPathReportId()
  {
    return this.getsAnnotation().getFileObjectId();
  }

  public void setPathReportId(String fileId)
  {
    this.getsAnnotation().setFileObjectId(fileId);
  }

  public Site getCollectionSite()
  {
    return this.getSpecimen().getSpecimenCollectionGroup()
        .getSpecimenCollectionSite();
  }

  public String getAliquotId()
  {
    return this.getAnnotation().getEntityId();
  }

  public Integer getPercentNuclei()
  {
    return this.getAnnotation().getPercentNuclei();
  }

  public void setPercentNuclei(Integer percent)
  {
    this.getAnnotation().setPercentNuclei(percent);
  }

  public Integer getPercentNecrosis()
  {
    return this.getAnnotation().getPercentNecrosis();
  }

  public void setPercentNecrosis(Integer percent)
  {
    this.getAnnotation().setPercentNecrosis(percent);
  }

  public Integer getPercentStroma()
  {
    return this.getAnnotation().getPercentStroma();
  }

  public void setPercentStroma(Integer percent)
  {
    this.getAnnotation().setPercentStroma(percent);
  }

  public String getPathImage()
  {
    return this.getAnnotation().getImages();
  }

  public void setPathImage(String filename)
  {
    this.getAnnotation().setImages(filename);
  }

  public String getPathImageId()
  {
    return this.getAnnotation().getImageObjectId();
  }

  public void setPathImageId(String imageId)
  {
    this.getAnnotation().setImageObjectId(imageId);
  }

  public String getAcquirePatientId()
  {
    return this.getpAnnotation().getEntityId();
  }

  public Boolean getShippedNaLab()
  {
    /*
     * Shipped status is stored in aliquots, but also if an aliquot is shipped,
     * the parent must also be marked. So, to know if this row is shipped,
     * if it is an aliquot we check the status set. If it is the parent, then
     * we check the status set of the SpecimenAnnotation
     */
    return this.isAliquot() ? this.getAnnotation().getStatus()
        .contains(SpecimenStatus.SHIPPED_NA_LAB) : this.getsAnnotation()
        .getStatus().contains(SpecimenStatus.SHIPPED_NA_LAB);
  }

  public Boolean getShippedTCGA()
  {
    return this.getSpecimenStatus().contains(SpecimenStatus.SHIPPED_TCGA);
  }

  public Boolean getShippedCellLab()
  {
    /*
     * Shipped status is stored in aliquots, but also if an aliquot is shipped,
     * the parent must also be marked. So, to know if this row is shipped,
     * if it is an aliquot we check the status set. If it is the parent, then
     * we check the status set of the SpecimenAnnotation
     */
    return this.isAliquot() ? this.getAnnotation().getStatus()
        .contains(SpecimenStatus.SHIPPED_CELL_LAB) : this.getsAnnotation()
        .getStatus().contains(SpecimenStatus.SHIPPED_CELL_LAB);
  }

  public Double getSpecimenAmount()
  {
    return this.getSpecimen().getAvailableQuantity();
  }

  public Double getSpecimenInitialAmount()
  {
    return this.getSpecimen().getInitialQuantity();
  }

  public String getLineage()
  {
    return this.getSpecimen().getLineage();
  }

  public String getPriorTreatment()
  {
    String value = "";
    YesNoChoices priorTreatment = this.getsAnnotation().getPriorTreatment();
    if (priorTreatment != null)
    {
      value = priorTreatment.toString();
    }
    return value;
  }

  public String getSpecimenType()
  {
    return this.getSpecimen().getSpecimenClass();
  }

  public String getType()
  {
    return this.getSpecimen().getSpecimenType();
  }

  public String getTumorType()
  {
    return this.getSpecimen().getPathologicalStatus();
  }

  public String getCollectionType()
  {
    return this.getSpecimen().getLineage();
  }

  public Long getInventoryId()
  {
    return this.getSpecimen().getId();
  }

  public List<String> getMrn()
  {
    Collection<Object> identifiers = this.getParticipant()
        .getParticipantMedicalIdentifierCollection();
    List<String> mrns = new ArrayList<String>();
    for (Object identifier : identifiers)
    {
      ParticipantMedicalIdentifier mrn = (ParticipantMedicalIdentifier) identifier;
      mrns.add(mrn.getMedicalRecordNumber());
    }
    return mrns;
  }

  public String getConcatenatedMrn()
  {
    String concatenatedMrn = "";

    for (String mrn : this.getMrn())
    {
      if (mrn != null && mrn.length() > 0)
      {
        concatenatedMrn = concatenatedMrn + mrn + ", ";
      }
    }

    // lop off the final two chars which are the extra commas, making sure we
    // have enough text to do this
    int endIndex = concatenatedMrn.length() - 2;
    if (endIndex < 0)
      endIndex = 0;
    return concatenatedMrn.substring(0, endIndex);
  }

  public List<String> getRace()
  {
    List<String> raceValues = new ArrayList<String>();
    for (Race race : this.getParticipant().getRaceCollection())
    {
      raceValues.add(race.getRaceName());
    }

    return raceValues;
  }

  public String getEthnicity()
  {
    return this.getParticipant().getEthnicity();
  }

  public String getGender()
  {
    return this.getParticipant().getGender();
  }

  public Integer getAgeAtCollection()
  {
    return this.getsAnnotation().getAgeAtCollection();
  }

  public Integer getWarmIschemiaTime()
  {
    return this.getsAnnotation().getWarmIschemiaTime();
  }

  public boolean isAliquot()
  {
    return this.getAnnotation().getParent() != null;
  }

  public void shipToTcga()
  {
    Set<SpecimenStatus> status = this.getsAnnotation().getStatus();
    status.add(SpecimenStatus.SHIPPED_TCGA);
    status.remove(SpecimenStatus.TCGA_QUALIFIED);
  }

  public String getParentLabel()
  {
    return this.parent != null ? this.parent.getLabel() : "--";
  }

  /**
   * @param parent
   *          the parent to set
   */
  public void setParent(AbstractSpecimen parent)
  {
    this.parent = parent;
  }
  
  public AbstractSpecimen getParent()
  {
    return this.parent;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
        prime * result
            + ((this.annotation == null) ? 0 : this.annotation.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof SearchResult))
    {
      return false;
    }
    SearchResult other = (SearchResult) obj;
    if (this.annotation == null)
    {
      if (other.annotation != null)
      {
        return false;
      }
    }
    else if (!this.annotation.equals(other.annotation))
    {
      return false;
    }
    return true;
  }
  

}
