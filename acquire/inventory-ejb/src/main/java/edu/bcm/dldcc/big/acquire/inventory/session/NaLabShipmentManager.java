package edu.bcm.dldcc.big.acquire.inventory.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;

import edu.bcm.dldcc.big.acquire.exception.TooManySamplesException;
import edu.bcm.dldcc.big.acquire.inventory.data.Data;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLab;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.query.data.ShipmentSearchFields;
import edu.bcm.dldcc.big.acquire.shipment.ShipmentManager;
import edu.bcm.dldcc.big.acquire.shipment.ShipmentType;
import edu.bcm.dldcc.big.acquire.shipment.entity.SampleData;
import edu.bcm.dldcc.big.acquire.shipment.naLab.entity.NaLabShipment;
import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.search.SearchCriteria;

/**
 * Session Bean implementation class ShipmentManagerImpl
 */
@Stateful
@ConversationScoped
@Named("naLabShipmentForm")
@NaLab
public class NaLabShipmentManager extends
    AbstractShipmentManager<NaLabShipment>
{
  private NaLabShipment naLabShipment = new NaLabShipment();

  @Inject
  private Data data;
  
  @Inject 
  private Messages message;
  

  /**
   * Default constructor.
   */
  public NaLabShipmentManager()
  {
    super();
  }

  /**
   * @see ShipmentManager#getShipmentField()
   */
  public ShipmentSearchFields getShipmentField()
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * @see ShipmentManager#clearShipment()
   */
  public void clearShipment()
  {

    this.naLabShipment.getBloodSamples().clear();
    this.naLabShipment.getSamples().clear();

  }

  
  
  /**
   * @see ShipmentManager#exportShipment(ShipmentType)
   */
  public void exportShipment()
  {
    super.exportShipment();
  }

  /**
   * @see ShipmentManager#shipmentSearch()
   */
  public void shipmentSearch()
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * @see ShipmentManager#addSpecimen(ShipmentType, SearchResult)
   */
  @Override
  public void addSpecimens()
  {
    super.addSpecimens();
    
    for (SearchResult specimen : data.getSelectedShipment())
    {
      this.processSpecimen(specimen);
    }
    
    message.info("The specimens were successfully " +
    		"added to the NA Lab Shipment form: \n");

  }
  
  private void processSpecimen(SearchResult specimen)
  {
    try
    {
      this.naLabShipment.addSample(specimen.getAnnotation(),
          specimen.getSpecimen(), specimen.getParticipant(),
          specimen.getpAnnotation());
    }
    catch (TooManySamplesException e)
    {
      message.warn(e.getMessage());
    }
  }

  /**
   * @see ShipmentManager#getShipmentResults()
   */
  @Override
  public List<NaLabShipment> getShipmentResults()
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * @see ShipmentManager#setShipmentField(ShipmentSearchFields)
   */
  @Override
  public void setShipmentField(ShipmentSearchFields field)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * @see ShipmentManager#setNaLabShipment(NaLabShipment)
   */
  @Override
  public void setShipment(NaLabShipment form)
  {
    this.naLabShipment = form;
  }

  /**
   * @see ShipmentManager#getNaLabShipment()
   */
  @Override
  public NaLabShipment getShipment()
  {
    return this.naLabShipment;
  }

  /**
   * @see ShipmentManager#getSearchValue()
   */
  @Override
  public SearchCriteria<?> getSearchValue()
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.acquire.inventory.session.AbstractShipmentManager#updateStatus(edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation)
   */
  @Override
  protected void updateStatus(AliquotAnnotation specimen)
  {
    specimen.getStatus().add(SpecimenStatus.SHIPPED_NA_LAB);
    Set<SpecimenStatus> parentStatus = specimen.getSpecimen().getStatus();
    parentStatus.remove(SpecimenStatus.NA_LAB_QUALIFIED);
    parentStatus.add(SpecimenStatus.SHIPPED_NA_LAB);
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.acquire.inventory.session.AbstractShipmentManager#addSpecimen(edu.bcm.dldcc.big.acquire.query.data.SearchResult)
   */
  @Override
  public void addSpecimen(SearchResult specimen)
  {
    super.addSpecimen(specimen);
    this.processSpecimen(specimen);
    this.message.info("Specimen successfully added to shipment form");
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.acquire.inventory.session.AbstractShipmentManager#getAllSpecimens()
   */
  @Override
  protected Collection<SampleData> getAllSpecimens()
  {
    Collection<SampleData> collectedResults = new ArrayList<SampleData>();
    collectedResults.addAll(this.getShipment().getBloodSamples());
    collectedResults.addAll(this.getShipment().getSamples());
    return collectedResults;
  }

}
