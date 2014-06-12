package edu.bcm.dldcc.big.acquire.inventory.session;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.acquire.inventory.InventoryProcessor;
import edu.bcm.dldcc.big.acquire.inventory.data.NaLabData;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.clinical.data.DerivativeType;
import edu.wustl.catissuecore.domain.CollectionEventParameters;
import edu.wustl.catissuecore.domain.FluidSpecimen;
import edu.wustl.catissuecore.domain.MolecularSpecimen;
import edu.wustl.catissuecore.domain.ReceivedEventParameters;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCharacteristics;
import edu.wustl.catissuecore.domain.SpecimenEventParameters;
import edu.wustl.catissuecore.domain.User;

/**
 * Session Bean implementation class InventoryProcessorImpl
 */
@Stateless
public class InventoryProcessorImpl implements InventoryProcessor
{

  /**
   * Default constructor.
   */
  public InventoryProcessorImpl()
  {
    super();
  }

  /**
   * @see InventoryProcessor#importNaLab(NaLabData, Instance<EntityManager>)
   */
  public void importNaLab(@Observes NaLabData data,
      @Any Instance<EntityManager> entityManagers)
  {
    EntityManager em = entityManagers.select(new AdminLiteral(),
        new CaTissueLiteral(data.getInstance())).get();
    /*
     * Find the parent specimen
     */
    Specimen parent = data.getParent();

    /*
     * Reduce the available quantity of the parent by the amount consumed
     */
    this.updateParentAmount(data, parent);

    /*
     * Instantiate the derivative
     */
    MolecularSpecimen derivative = new MolecularSpecimen();

    /*
     * Set the base values
     */
    derivative.setSpecimenClass("Molecular");
    derivative.setLineage("Derived");
    derivative.setCollectionStatus("Collected");
    derivative.setActivityStatus("Active");
    derivative.setLabel(data.getNaLabLabel());

    /*
     * Set additional values, using the parent where appropriate
     */
    derivative.setPathologicalStatus(parent.getPathologicalStatus());
    derivative.setSpecimenType(data.getType().toString());
    derivative.setSpecimenCollectionGroup(parent.getSpecimenCollectionGroup());
    parent.getSpecimenCollectionGroup().getSpecimenCollection().add(derivative);

    /*
     * Derivatives have their own SpecimenCharacteristics, but the data is the
     * same as the parent
     */
    derivative.setSpecimenCharacteristics(new SpecimenCharacteristics(parent
        .getSpecimenCharacteristics()));
    derivative.setInitialQuantity(data.getAmount());
    derivative.setAvailableQuantity(data.getAmount());
    derivative.setConcentrationInMicrogramPerMicroliter(data.getConcentration()
        * InventoryProcessor.CONCENTRATION_CONVERSION_FACTOR);
    derivative.setIsAvailable(true);
    derivative.setParentSpecimen(parent);

    /*
     * persist the derivative
     */
    em.persist(derivative);

    /*
     * Find the Admin user, which will be used as we can't know the actual user
     */
    User admin = em.find(User.class, 1L);

    /*
     * do specimen event parameters
     */
    this.setupCollectionEvents(data, em, parent, derivative, admin);

  }

  /**
   * @param data
   * @param em
   * @param parent
   * @param derivative
   * @param admin
   */
  private void setupCollectionEvents(NaLabData data, EntityManager em,
      Specimen parent, Specimen derivative, User admin)
  {
    Collection<SpecimenEventParameters> parentEvents = parent
        .getSpecimenEventCollection();
    CollectionEventParameters parentCollect = null;
    ReceivedEventParameters parentReceive = null;

    for (SpecimenEventParameters event : parentEvents)
    {
      if (event instanceof CollectionEventParameters)
      {
        parentCollect = (CollectionEventParameters) event;
      }
      else if (event instanceof ReceivedEventParameters)
      {
        parentReceive = (ReceivedEventParameters) event;
      }
    }

    CollectionEventParameters collection = new CollectionEventParameters();
    collection.setSpecimen(derivative);
    collection.setTimestamp(data.getDateReceived());
    collection.setUser(admin);
    collection.setCollectionProcedure(parentCollect.getCollectionProcedure());
    collection.setContainer(parentCollect.getContainer());

    ReceivedEventParameters received = new ReceivedEventParameters();
    received.setSpecimen(derivative);
    received.setTimestamp(data.getDateReceived());
    received.setUser(admin);
    received.setReceivedQuality(parentReceive.getReceivedQuality());

    Collection<SpecimenEventParameters> events = derivative
        .getSpecimenEventCollection();
    events.add(collection);
    events.add(received);

    em.persist(collection);
    em.persist(received);
  }

  /**
   * @param data
   * @param parent
   */
  private void updateParentAmount(NaLabData data, Specimen parent)
  {
    /*
     * If the parent is blood, then it is all consumed
     */
    if (parent instanceof FluidSpecimen)
    {
      parent.setAvailableQuantity(0D);
    }
    else
    {
      parent.setAvailableQuantity(parent.getAvailableQuantity()
          - data.getAmountConsumed());
    }
  }

}
