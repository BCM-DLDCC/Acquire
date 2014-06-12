package edu.bcm.dldcc.big.acquire.inventory;

import java.util.List;

import javax.ejb.Local;

import edu.bcm.dldcc.big.acquire.inventory.data.EntityAdaptor;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.util.EntityAnnotation;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * EntityResolver provides various operations for either finding connected
 * caTissue entities, or getting the corresponding caTissue/Acquire object 
 * from the other side.
 * 
 * @author pew
 *
 */
@Local
public interface EntityResolver
{

  /**
   * Determines the caTissue entity class referred to by an Acquire EntityMap.
   * 
   * @param id the Id of the EntityMap
   * @return Class&lt;?&gt; of caTissue entity that the EntityMap refers to
   * @throws ClassNotFoundException
   */
  Class<?> getEntityClass(String id) throws ClassNotFoundException;

  /**
   * Retrieves an EntityMap and the corresponding caTissue entity object for
   * an EntityMap id.
   * 
   * @param id The id of an EntityMap
   * @param entityClass the AbstractDomainObject class that the EntityMap 
   * refers to
   * @return
   */
  <T extends AbstractDomainObject> EntityAdaptor<T> getCaTissueEntity(
      String id, Class<T> entityClass);

  /**
   * Retrieves all EntityMaps and corresponding caTissue entity objects for
   * the provided list of EntityMap ids.
   * 
   * @param entityClass
   * @param mapIds
   * @return
   */
  <T extends AbstractDomainObject> List<EntityAdaptor<T>> getCaTissueEntityList(
      Class<T> entityClass, List<String> mapIds);
  
  /**
   * Retrieves the Particpant corresponding to a Specimen.
   * @param specimen
   * @return
   */
  Participant findParticipantForSpecimen(Specimen specimen);
  
  /**
   * Retrieves a List of all Specimen from a given Participant.
   * @param participant
   * @return
   */
  List<Specimen> findParticipantSpecimens(Participant participant);
  
  /**
   * Retrieves the caTissue entity object that corresponds to the provided 
   * EntityMap
   * 
   * @param map
   * @return
   * @throws ClassNotFoundException
   */
  EntityAdaptor<AbstractDomainObject> getCaTissueEntity(EntityMap map)
      throws ClassNotFoundException;
  
  /**
   * Finds and retrieves the ultimate parent specimen (ie, the specimen
   * with lineage "new") of the provided AbstractSpecimen.
   * @param aliquot
   * @return
   */
  AbstractSpecimen getAdam(AbstractSpecimen aliquot);
  
  /**
   * Retrieves the EntityAnnotation of the specified type that is mapped to
   * the provided AbstractDomainObject
   * 
   * @param annotationType
   * @param entity
   * @param instance
   * @param entityClass
   * @return
   */
  <T extends EntityAnnotation, X extends AbstractDomainObject> T getAnnotationForEntity(
      Class<T> annotationType, X entity, CaTissueInstance instance,
      Class<? super X> entityClass);
  
  /**
   * Retrieves the AbstractDomainObject that is mapped to the provided 
   * EntityAnnotation.
   * 
   * @param entityType
   * @param annotation
   * @return
   */
  <T extends EntityAnnotation, X extends AbstractDomainObject> X getEntityForAnnotation(
      Class<X> entityType, T annotation);
  
  /**
   * Determines the caTissue instance that contains the caTissue entity
   * that is mapped by the provided EntityAnnotation.
   * 
   * @param annotation
   * @return
   */
  CaTissueInstance getInstanceFromAnnotation(EntityAnnotation annotation);

}