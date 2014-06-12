package edu.bcm.dldcc.big.acquire.shipment.naLab.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import edu.bcm.dldcc.big.acquire.exception.TooManySamplesException;
import edu.bcm.dldcc.big.acquire.shipment.entity.SampleData;
import edu.bcm.dldcc.big.acquire.shipment.entity.Shipment;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation;
import edu.wustl.catissuecore.domain.FluidSpecimen;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;

@Entity
public class NaLabShipment extends Shipment
{
  private String internalCollaborator;
  private List<TissueSample> samples = new ArrayList<TissueSample>();
  private List<BloodSample> bloodSamples = new ArrayList<BloodSample>();
  private static final int MAX_TISSUE_SAMPLES = 96;
  
  public NaLabShipment()
  {
    super();
  }
  
  @Size(max = 200)
  public String getInternalCollaborator()
  {
    return internalCollaborator;
  }

  public void setInternalCollaborator(String internalCollaborator)
  {
    this.internalCollaborator = internalCollaborator;
  }

 
  @OneToMany(mappedBy = "shipment",
      orphanRemoval = true, cascade={CascadeType.PERSIST})
  public List<TissueSample> getSamples()
  {
    return samples;
  }

  public void setSamples(List<TissueSample> samples)
  {
    this.samples = samples;
  }
  
  @OneToMany(mappedBy = "shipment",
      orphanRemoval = true, cascade={CascadeType.PERSIST})
  public List<BloodSample> getBloodSamples()
  {
    return bloodSamples;
  }

  public void setBloodSamples(List<BloodSample> samples)
  {
    this.bloodSamples = samples;
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.acquire.shipment.Shipment#addSample(edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation, edu.wustl.catissuecore.domain.Specimen, edu.wustl.catissuecore.domain.Participant, edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation)
   */
  @Override
  public void addSample(AliquotAnnotation annotation, Specimen specimen,
      Participant patient, ParticipantAnnotation patientAnnotation)
      throws TooManySamplesException
  {
    
    if(specimen instanceof FluidSpecimen)
    {
      BloodSample blood = new BloodSample();
      blood.populateSample(annotation, specimen, patient, patientAnnotation);
      blood.setShipment(this);
      this.getBloodSamples().add(blood);
    }
    else
    {
      TissueSample tissue = new TissueSample();
      tissue.populateSample(annotation, specimen, patient, patientAnnotation);
      tissue.setShipment(this);
      this.getSamples().add(tissue);
      if(this.getSamples().size() >= NaLabShipment.MAX_TISSUE_SAMPLES )
      {
        throw new TooManySamplesException("The NA Lab Shipment form only" +
        		"allows for "  + NaLabShipment.MAX_TISSUE_SAMPLES + 
        		" tissue samples. Please submit on two forms.");
      }
    }
    
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.acquire.shipment.Shipment#removeSample(edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation, edu.wustl.catissuecore.domain.Specimen)
   */
  @Override
  public void removeSample(SampleData sample)
  {
    this.getBloodSamples().remove(sample);
    this.getSamples().remove(sample);
    
  }

  

  

}
