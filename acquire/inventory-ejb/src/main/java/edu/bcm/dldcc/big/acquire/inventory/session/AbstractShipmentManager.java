/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.seam.international.status.Messages;

import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.shipment.ShipmentManager;
import edu.bcm.dldcc.big.acquire.shipment.ShipmentType;
import edu.bcm.dldcc.big.acquire.shipment.entity.SampleData;
import edu.bcm.dldcc.big.acquire.shipment.entity.Shipment;
import edu.bcm.dldcc.big.acquire.util.Utilities;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;

/**
 * @author pew
 * 
 */
public abstract class AbstractShipmentManager<T extends Shipment> implements
    ShipmentManager<T>
{

  @Inject
  @Annotations
  @Operations
  private EntityManager em;

  @Inject
  private Conversation conversation;

  @Inject
  private Messages messages;

  @Inject
  private Utilities util;

  private List<SearchResult> addedSamples = new ArrayList<SearchResult>();

  /**
   * 
   */
  public AbstractShipmentManager()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.shipment.ShipmentManager#saveShipment(edu.bcm
   * .dldcc.big.acquire.shipment.ShipmentType)
   */
  @Override
  public void saveShipment()
  {
    if (this.getShipment().getId() == null)
    {
      em.persist(this.getShipment());
    }
    for (SampleData result : this.getAllSpecimens())
    {
      this.updateStatus(em.find(AliquotAnnotation.class,
          result.getSpecimenUUID()));
    }

    this.messages.info("Shipment form saved successfully");

  }

  /**
   * @see ShipmentManager#addSpecimen(ShipmentType, SearchResult)
   */
  @Override
  public void addSpecimens()
  {
    this.util.startConversation();
  }

  @Override
  public void addSpecimen(SearchResult specimen)
  {
    this.util.startConversation();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.shipment.ShipmentManager#exportShipment()
   */
  @Override
  public void exportShipment()
  {
    this.saveShipment();

  }

  protected abstract Collection<SampleData> getAllSpecimens();

  protected abstract void updateStatus(AliquotAnnotation specimen);

}
