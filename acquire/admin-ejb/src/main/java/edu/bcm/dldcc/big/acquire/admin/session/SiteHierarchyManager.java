package edu.bcm.dldcc.big.acquire.admin.session;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CollectionSite;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation_;

/**
 * Session Bean implementation class SiteHierarchyManager
 */
@ApplicationScoped
public class SiteHierarchyManager implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 8605549186632988740L;
  @Inject
  @Annotations
  @Admin
  private EntityManager em;

  /**
   * Default constructor.
   */
  public SiteHierarchyManager()
  {
    super();
  }
  
  @Produces
  @CollectionSite
  public List<SiteAnnotation> getParentSites()
  {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<SiteAnnotation> criteria = cb.createQuery(SiteAnnotation.class);
    Root<SiteAnnotation> root = criteria.from(SiteAnnotation.class);
    criteria.select(root);
    criteria.where(cb.isNull(root.get(SiteAnnotation_.parent)));
    TypedQuery<SiteAnnotation> query = em.createQuery(criteria);
    return query.getResultList();
  }
  
  @Produces
  @Named("acquireSiteList")
  public List<SiteAnnotation> allSites()
  {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<SiteAnnotation> criteria = cb.createQuery(SiteAnnotation.class);
    Root<SiteAnnotation> root = criteria.from(SiteAnnotation.class);
    criteria.select(root);
    criteria.orderBy(cb.asc(root.get(SiteAnnotation_.name)));
    TypedQuery<SiteAnnotation> query = em.createQuery(criteria);
    return query.getResultList();
  }

}
