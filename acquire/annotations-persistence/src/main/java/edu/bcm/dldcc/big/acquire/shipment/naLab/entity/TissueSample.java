package edu.bcm.dldcc.big.acquire.shipment.naLab.entity;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation;
import edu.bcm.dldcc.big.submission.nalab.data.BoxLocation;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;

@Entity
public class TissueSample extends NaLabSample
{

  private BoxLocation boxLocation = BoxLocation.NA;
  private NaLabShipment shipment;
  private String collectionSite;
  private Double cellAmount;

  public TissueSample()
  {
   super();
   this.setLiquidAmount(0.0D);
  }

  @Enumerated
  public BoxLocation getBoxLocation()
  {
    return boxLocation;
  }

  public void setBoxLocation(BoxLocation boxLocation)
  {
    this.boxLocation = boxLocation;
  }

  public String getCollectionSite()
  {
    return this.collectionSite;
  }

  public Double getCellAmount()
  {
    return this.cellAmount;
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
    String site = "";
    if(annotation.getSpecimen().getNormal())
    {
      site = "Other";
    }
    else
    {
      if(specimen.getSpecimenCharacteristics() != null)
      {
        site = specimen.getSpecimenCharacteristics().getTissueSite();
      }
    }
    this.setCollectionSite(site);
    
    this.setCellAmount(specimen.getAvailableQuantity());
    
    this.setPreservative(specimen.getSpecimenType());
    
  }

  /**
   * @param collectionSite the collectionSite to set
   */
  public void setCollectionSite(String collectionSite)
  {
    this.collectionSite = collectionSite;
  }

  /**
   * @param cellAmount the cellAmount to set
   */
  public void setCellAmount(Double cellAmount)
  {
    this.cellAmount = cellAmount;
  }

}
