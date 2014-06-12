/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.acquire.inventory.EntityResolver;
import edu.bcm.dldcc.big.acquire.inventory.data.EntityAdaptor;
import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.acquire.util.EntityAnnotation;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.bcm.dldcc.big.inventory.entity.EntityMap_;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;
import edu.wustl.common.domain.AbstractDomainObject;

@Stateless
/**
 * @author pew
 *
 */
public class CaTissueEntityResolver implements Serializable, EntityResolver
{

  @Inject
  @Any
  Instance<EntityManager> caTissueEntityManager;

  @Inject
  @Annotations
  @Admin
  EntityManager inventoryEntityManager;

  /**
   * 
   */
  public CaTissueEntityResolver()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.manager.EntityResolver#getEntityClass
   * (java.lang.String)
   */
  @Override
  public Class<?> getEntityClass(String id) throws ClassNotFoundException
  {
    EntityMap map = inventoryEntityManager.find(EntityMap.class, id);
    return Class.forName(map.getEntityName());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.manager.EntityResolver#getCaTissueEntity
   * (java.lang.String, java.lang.Class)
   */
  @Override
  public <T extends AbstractDomainObject> EntityAdaptor<T> getCaTissueEntity(
      String id, Class<T> entityClass)
  {
    EntityMap map = inventoryEntityManager.find(EntityMap.class, id);
    EntityManager em = caTissueEntityManager.select(
        new CaTissueLiteral(map.getCaTissue()), new AdminLiteral()).get();
    T entity = em.find(entityClass, map.getEntityId());

    return new EntityAdaptor<T>(map, entity);
  }

  @Override
  public EntityAdaptor<AbstractDomainObject> getCaTissueEntity(EntityMap map)
      throws ClassNotFoundException
  {
    EntityManager em = caTissueEntityManager.select(
        new CaTissueLiteral(map.getCaTissue()), new AdminLiteral()).get();
    return new EntityAdaptor<AbstractDomainObject>(map,
        (AbstractDomainObject) em.find(Class.forName(map.getEntityName()),
            map.getEntityId()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.manager.EntityResolver#
   * getCaTissueEntityList(java.lang.Class, java.util.List)
   */
  @Override
  public <T extends AbstractDomainObject> List<EntityAdaptor<T>> getCaTissueEntityList(
      Class<T> entityClass, List<String> mapIds)
  {
    List<EntityAdaptor<T>> results = new ArrayList<EntityAdaptor<T>>();
    Map<CaTissueInstance, Map<Long, EntityMap>> map = new HashMap<CaTissueInstance, Map<Long, EntityMap>>();
    CriteriaBuilder cb = inventoryEntityManager.getCriteriaBuilder();
    CriteriaQuery<EntityMap> criteria = cb.createQuery(EntityMap.class);
    Root<EntityMap> root = criteria.from(EntityMap.class);
    criteria.select(root);
    Predicate pred = cb.equal(root.get(EntityMap_.entityName),
        entityClass.getName());
    if (mapIds != null && mapIds.size() != 0)
    {
      pred = cb.and(pred, root.get(EntityMap_.id).in(mapIds));
    }
    criteria.where(pred);
    TypedQuery<EntityMap> query = inventoryEntityManager.createQuery(criteria);
    List<EntityMap> instanceIds = query.getResultList();

    for (EntityMap entity : instanceIds)
    {
      CaTissueInstance entityInstance = entity.getCaTissue();
      Map<Long, EntityMap> instanceMap = null;
      if (map.containsKey(entityInstance))
      {
        instanceMap = map.get(entityInstance);
      }
      else
      {
        instanceMap = new HashMap<Long, EntityMap>();
        map.put(entityInstance, instanceMap);
      }

      instanceMap.put(entity.getEntityId(), entity);
    }

    for (CaTissueInstance instance : EnumSet.allOf(CaTissueInstance.class))
    {
      List<T> caTissueEntities = this.getCaTissueEntities(
          this.extractCaTissueIds(instanceIds), entityClass, instance);

      for (T object : caTissueEntities)
      {
        EntityAdaptor<T> adaptor = new EntityAdaptor<T>();
        adaptor.setEntity(object);
        adaptor.setMap(map.get(instance).get(object.getId()));
        results.add(adaptor);
      }

    }
    return results;
  }

  private List<Long> extractCaTissueIds(List<EntityMap> list)
  {
    List<Long> id = new ArrayList<Long>();
    for (EntityMap map : list)
    {
      id.add(map.getEntityId());
    }

    return id;
  }

  private <T extends AbstractDomainObject> List<T> getCaTissueEntities(
      List<Long> ids, Class<T> entityClass, CaTissueInstance instance)
  {
    EntityManager em = caTissueEntityManager.select(
        new CaTissueLiteral(instance), new AdminLiteral()).get();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> criteria = cb.createQuery(entityClass);
    Root<T> root = criteria.from(entityClass);
    criteria.select(root);
    criteria.where(root.get("id").in(ids));
    TypedQuery<T> query = em.createQuery(criteria);
    return query.getResultList();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.EntityResolver#findParticipantForSpecimen
   * (edu.wustl.catissuecore.domain.Specimen)
   */
  @Override
  public Participant findParticipantForSpecimen(Specimen specimen)
  {
    Participant parent = specimen.getSpecimenCollectionGroup()
        .getCollectionProtocolRegistration().getParticipant();

    return parent;
  }

  @Override
  public AbstractSpecimen getAdam(AbstractSpecimen aliquot)
  {
    AbstractSpecimen adam = aliquot.getParentSpecimen() == null ? aliquot
        : this.getAdam(aliquot.getParentSpecimen());
    return adam;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.EntityResolver#findParticipantSpecimens
   * (edu.wustl.catissuecore.domain.Participant)
   */
  @Override
  public List<Specimen> findParticipantSpecimens(Participant participant)
  {
    List<Specimen> results = new ArrayList<Specimen>();
    for (CollectionProtocolRegistration registration : participant
        .getCollectionProtocolRegistrationCollection())
    {
      for (SpecimenCollectionGroup group : registration
          .getSpecimenCollectionGroupCollection())
      {
        results.addAll(group.getSpecimenCollection());
      }
    }
    return results;
  }

  /**
   * NOTE: This method returns null if no annotation is found. This is done
   * because of the delay between entity creation and annotation creation, so
   * the annotation might not be created yet.
   */
  @Override
  public <T extends EntityAnnotation, X extends AbstractDomainObject> T getAnnotationForEntity(
      Class<T> annotationType, X entity, CaTissueInstance instance,
      Class<? super X> entityClass)
  {
    CriteriaBuilder cb = inventoryEntityManager.getCriteriaBuilder();
    CriteriaQuery<T> criteria = cb.createQuery(annotationType);
    Root<T> root = criteria.from(annotationType);
    Root<EntityMap> mapRoot = criteria.from(EntityMap.class);
    criteria.select(root);
    criteria.where(cb.and(
        cb.equal(root.get("entityId"), mapRoot.get(EntityMap_.id)),
        cb.equal(mapRoot.get(EntityMap_.caTissue), instance),
        cb.equal(mapRoot.get(EntityMap_.entityName),
            entityClass.getName()),
        cb.equal(mapRoot.get(EntityMap_.entityId), entity.getId())));
    T result = null;
    try
    {
      result = inventoryEntityManager.createQuery(criteria).getSingleResult();
    }
    catch (NoResultException e)
    {
      // ignore, and return null
    }
    return result;
  }

  @Override
  public <T extends EntityAnnotation, X extends AbstractDomainObject> X getEntityForAnnotation(
      Class<X> entityType, T annotation)
  {
    EntityMap map = this.inventoryEntityManager.find(EntityMap.class,
        annotation.getEntityId());
    EntityManager caTissueEm = this.caTissueEntityManager.select(
        new AdminLiteral(), new CaTissueLiteral(map.getCaTissue())).get();
    return caTissueEm.find(entityType, map.getEntityId());
  }
  
  @Override
  public CaTissueInstance getInstanceFromAnnotation(EntityAnnotation annotation)
  {
    return this.inventoryEntityManager.find(EntityMap.class,
        annotation.getEntityId()).getCaTissue();
  }

}
