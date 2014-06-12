/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissue;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * @author pew
 * 
 */
@ApplicationScoped
public class CaTissueUtilities implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 6010345974294237865L;

  @Inject
  @CaTissue(instance = CaTissueInstance.TCRB)
  @Admin
  private EntityManager em;

  @Inject
  @Annotations
  @Admin
  private EntityManager acquireEm;

  @Inject
  @Any
  private Instance<EntityManager> emInstance;

  private static final String PARENT_SQL = "SELECT value FROM "
      + "catissue_permissible_value " + "WHERE public_id = 'Tissue_Site_PID'";

  private static final String CHILD_SQL = "SELECT value FROM "
      + "catissue_permissible_value "
      + "WHERE parent_identifier in (SELECT identifier FROM "
      + "catissue_permissible_value where " + "value = ?)";
  
  private static final String VALUE_SQL = "SELECT value FROM " +
  		"catissue_permissible_value WHERE public_id = ? ORDER BY value ASC";

  /**
   * 
   */
  public CaTissueUtilities()
  {
    super();
  }

  @Produces
  @ApplicationScoped
  public Map<String, List<String>> getDiseaseListHierarchy()
  {
    Map<String, List<String>> hierarchy = new TreeMap<String, List<String>>();

    @SuppressWarnings("unchecked")
    List<Object> parentList = em
        .createNativeQuery(CaTissueUtilities.PARENT_SQL).getResultList();
    for (Object parent : parentList)
    {
      List<String> childList = new ArrayList<String>();
      /*
       * Add the parent to the list, as some specimen might be given this value
       */
      childList.add(parent.toString());
      childList.addAll(this.findChildValues(parent.toString()));
      hierarchy.put(parent.toString(), childList);
    }
    return Collections.unmodifiableMap(hierarchy);
  }

  private List<String> findChildValues(String parent)
  {
    List<String> children = new ArrayList<String>();
    Query childQuery = em.createNativeQuery(CaTissueUtilities.CHILD_SQL);
    childQuery.setParameter(1, parent);
    @SuppressWarnings("unchecked")
    List<Object> result = childQuery.getResultList();
    for (Object current : result)
    {
      children.add(current.toString());
      children.addAll(this.findChildValues(current.toString()));
    }

    return children;

  }

  public Specimen findSpecimenFromAliquotAnnotation(AliquotAnnotation aliquot)
  {
    String mapId = aliquot.getParent() != null ? aliquot.getParent()
        .getEntityId() : aliquot.getEntityId();
    EntityMap specimenMap = acquireEm.find(EntityMap.class, mapId);

    EntityManager caTissueEm = emInstance.select(
        new CaTissueLiteral(specimenMap.getCaTissue())).get();
    return caTissueEm.find(Specimen.class, specimenMap.getEntityId());

  }
  
  private List<String> getPermissibleValues(String id)
  {
    List<String> permissibleValues = new ArrayList<String>();
    
    Query parentQuery = em.createNativeQuery(CaTissueUtilities.VALUE_SQL);
    parentQuery.setParameter(1, id);
    List<Object> parentList = parentQuery.getResultList();
    for(Object current : parentList)
    {
      permissibleValues.add(current.toString());
      permissibleValues.addAll(this.findChildValues(current.toString()));
    }
    
    return permissibleValues;
  }
  
  @Produces
  @RequestScoped
  @Named("raceList")
  public List<String> getRaceList()
  {
    return this.getPermissibleValues("Race_PID");
  }
  
  @Produces
  @ConversationScoped
  @Named("ethnicityList")
  public List<String> getEthnicityList()
  {
    return this.getPermissibleValues("Ethnicity_PID");
  }
  
  @Produces
  @ConversationScoped
  @Named("genderList")
  public List<String> getGenderList()
  {
    return this.getPermissibleValues("2003989");
  }
  
  @Produces
  @ApplicationScoped
  @Named("diseaseSiteList")
  public List<String> getDiseaseSiteList()
  {
    return this.getPermissibleValues("Tissue_Site_PID");
  }
  
  @Produces
  @ApplicationScoped
  @Named("pathStatusList")
  public List<String> getPathStatusList()
  {
    return this.getPermissibleValues("2003993");
  }

}
