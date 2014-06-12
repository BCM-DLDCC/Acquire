package edu.bcm.dldcc.big.acquire.shipment.naLab.entity;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation;
import edu.wustl.catissuecore.domain.CollectionEventParameters;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenEventParameters;

//import edu.bcm.dldcc.big.acquire.legacy.entity.information.Specimen;

@Entity
public class BloodSample extends NaLabSample
{
  private NaLabShipment shipment;
  
  public BloodSample()
  {
    super();
  }

  /**
   * @return the shipment
   */
  @ManyToOne
  @JoinColumn(nullable=false)
  public NaLabShipment getShipment()
  {
    return this.shipment;
  }

  /**
   * @param shipment the shipment to set
   */
  public void setShipment(NaLabShipment shipment)
  {
    this.shipment = shipment;
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.acquire.shipment.naLab.NaLabSample#populateSample(edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation, edu.wustl.catissuecore.domain.Specimen, edu.wustl.catissuecore.domain.Participant, edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation)
   */
  @Override
  public void populateSample(AliquotAnnotation annotation, Specimen specimen,
      Participant patient, ParticipantAnnotation patientAnnotation)
  {
    super.populateSample(annotation, specimen, patient, patientAnnotation);
    String value = "";
    Collection<SpecimenEventParameters> events = specimen
        .getSpecimenEventCollection();
    for (SpecimenEventParameters event : events)
    {
      if (event instanceof CollectionEventParameters)
      {
        CollectionEventParameters collect = (CollectionEventParameters) event;
        value = collect.getContainer();
      }
    }
    this.setPreservative(value);
    
    this.setLiquidAmount(specimen.getAvailableQuantity());
  }

}
