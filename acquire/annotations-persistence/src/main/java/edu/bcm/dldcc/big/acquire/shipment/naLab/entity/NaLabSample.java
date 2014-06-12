package edu.bcm.dldcc.big.acquire.shipment.naLab.entity;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import edu.bcm.dldcc.big.acquire.shipment.entity.SampleData;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation;
import edu.bcm.dldcc.big.submission.nalab.data.ProbandRelationship;
import edu.bcm.dldcc.big.submission.nalab.data.PatientRole;
import edu.bcm.dldcc.big.submission.nalab.data.SampleType;
import edu.wustl.catissuecore.domain.CollectionEventParameters;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenEventParameters;

@Entity
public abstract class NaLabSample extends SampleData
{

  private SampleType type;
  private PatientRole role;
  private ProbandRelationship relationship;
  private String comments;
  private String diseaseType;
  private String tubeBarcode;
  private String patientId;
  private Date collectionDate;
  private String preservative;
  private Double liquidAmount = 0.0D;
  private String sampleId;
   
  public NaLabSample()
  {
    super();
    this.setRelationship(ProbandRelationship.PROBAND);
    this.setRole(PatientRole.AFFECTED);
  }

  @Enumerated
  public SampleType getType()
  {
    return type;
  }

  public void setType(SampleType type)
  {
    this.type = type;
  }

  @Enumerated
  public PatientRole getRole()
  {
    return role;
  }

  public void setRole(PatientRole role)
  {
    this.role = role;
  }

  @Enumerated
  public ProbandRelationship getRelationship()
  {
    return relationship;
  }

  public void setRelationship(ProbandRelationship relationship)
  {
    this.relationship = relationship;
  }

  @Size(max=2000)
  public String getComments()
  {
    return comments;
  }

  public void setComments(String comments)
  {
    this.comments = comments;
  }
  
 public String getDiseaseType()
  {
    return this.diseaseType;
  }
  
  public Date getCollectionDate()
  {
    return this.collectionDate;
  }

  public String getPreservative()
  {
    return this.preservative;
  }

  public Double getLiquidAmount()
  {
    return this.liquidAmount;
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.acquire.shipment.SampleData#populateSample(edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation, edu.wustl.catissuecore.domain.Specimen, edu.wustl.catissuecore.domain.Participant, edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation)
   */
  @Override
  public void populateSample(AliquotAnnotation annotation, Specimen specimen,
      Participant patient, ParticipantAnnotation patientAnnotation)
  {
    super.populateSample(annotation, specimen, patient, patientAnnotation);
    Date value = null;
    Collection<SpecimenEventParameters> events = specimen.getSpecimenEventCollection();
    for(SpecimenEventParameters event : events)
    {
      if(event instanceof CollectionEventParameters)
      {
        value = event.getTimestamp();
      }
    }
    this.setCollectionDate(value);
    this.setPatientId(patientAnnotation.getEntityId());
    this.setTubeBarcode(specimen.getBarcode());
    this.setSampleId(specimen.getLabel());
    String disease = "";
    if(annotation.getSpecimen().getNormal())
    {
      disease = specimen.getSpecimenCharacteristics().getTissueSite() + ": ";
          
    }
    
    disease += specimen.getSpecimenCollectionGroup().getClinicalDiagnosis(); 
    
    this.setDiseaseType(disease);
    
  }

  /**
   * @return the tubeBarcode
   */
  public String getTubeBarcode()
  {
    return this.tubeBarcode;
  }

  /**
   * @param tubeBarcode the tubeBarcode to set
   */
  public void setTubeBarcode(String tubeBarcode)
  {
    this.tubeBarcode = tubeBarcode;
  }

  /**
   * @return the patientId
   */
  public String getPatientId()
  {
    return this.patientId;
  }

  /**
   * @param patientId the patientId to set
   */
  public void setPatientId(String patientId)
  {
    this.patientId = patientId;
  }

  /**
   * @param diseaseType the diseaseType to set
   */
  public void setDiseaseType(String diseaseType)
  {
    this.diseaseType = diseaseType;
  }

  /**
   * @param collectionDate the collectionDate to set
   */
  public void setCollectionDate(Date collectionDate)
  {
    this.collectionDate = collectionDate;
  }

  /**
   * @param preservative the preservative to set
   */
  public void setPreservative(String preservative)
  {
    this.preservative = preservative;
  }

  /**
   * @param liquidAmount the liquidAmount to set
   */
  public void setLiquidAmount(Double liquidAmount)
  {
    this.liquidAmount = liquidAmount;
  }

  /**
   * @return the sampleId
   */
  public String getSampleId()
  {
    return this.sampleId;
  }

  /**
   * @param sampleId the sampleId to set
   */
  public void setSampleId(String sampleId)
  {
    this.sampleId = sampleId;
  }
  

}
