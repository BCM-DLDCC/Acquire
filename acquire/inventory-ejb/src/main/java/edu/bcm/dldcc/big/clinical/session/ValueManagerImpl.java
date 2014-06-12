package edu.bcm.dldcc.big.clinical.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.acquire.util.YesNoChoices;
import edu.bcm.dldcc.big.clinical.ValueManager;
import edu.bcm.dldcc.big.clinical.values.entity.MStaging;
import edu.bcm.dldcc.big.clinical.values.entity.NStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TStaging;
import edu.bcm.dldcc.big.clinical.values.entity.TumorGrade;
import edu.bcm.dldcc.big.clinical.values.entity.TumorStage;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * Session Bean implementation class ValueManager
 */
@Stateful
@ApplicationScoped
public class ValueManagerImpl implements ValueManager
{
  @Inject
  @Admin
  @Annotations
  private EntityManager em;
  
  @Inject
  @Any
  private Instance<EntityManager> emInstance;
  
  /**
   * Default constructor.
   */
  public ValueManagerImpl()
  {
    super();
  }
  
  public <T> List<T> getValueList(Class<T> type)
  {
    List<T> results = new ArrayList<T>();
    if(AbstractDomainObject.class.isAssignableFrom(type))
    {
      results = this.getCaTissueValues(type);
    }
    else if(type.isEnum())
    {
      results =  Arrays.asList(type.getEnumConstants());
    }
    else
    {
      results = this.getAquireValues(type);
    }
    
    return results;
  }
  
  private <T> List<T> getAquireValues(Class<T> type)
  {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> criteria = cb.createQuery(type);
    criteria.select(criteria.from(type));
    TypedQuery<T> query = em.createQuery(criteria);
    return query.getResultList();
  }
  
  private <T> List<T> getCaTissueValues(Class<T> type)
  {
    List<T> results = new ArrayList<T>();
    for(CaTissueInstance instance : EnumSet.allOf(CaTissueInstance.class))
    {
      EntityManager currentEm = emInstance.select(new AdminLiteral(),
          new CaTissueLiteral(instance)).get();
      CriteriaBuilder cb = currentEm.getCriteriaBuilder();
      CriteriaQuery<T> criteria = cb.createQuery(type);
      criteria.select(criteria.from(type));
      TypedQuery<T> query = currentEm.createQuery(criteria);
      results.addAll(query.getResultList());
    }
    
    return results;
  }
  
  @Produces
  @ConversationScoped
  @Named("tStagingList")
  @Override
  public List<TStaging> getTStagingList()
  {
    return this.getValueList(TStaging.class);
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.clinical.ValueManager#getMStagingList()
   */
  @Produces
  @ConversationScoped
  @Named("mStagingList")
  @Override
  public List<MStaging> getMStagingList()
  {
    return this.getValueList(MStaging.class);
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.clinical.ValueManager#getNStagingList()
   */
  @Produces
  @Named("nStagingList")
  @ConversationScoped
  @Override
  public List<NStaging> getNStagingList()
  {
    return this.getValueList(NStaging.class);
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.clinical.ValueManager#getTumorStageList()
   */
  @Produces
  @Named("tumorStageList")
  @ConversationScoped
  @Override
  public List<TumorStage> getTumorStageList()
  {
    return this.getValueList(TumorStage.class);
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.clinical.ValueManager#getTumorGradeList()
   */
  @Produces
  @ConversationScoped
  @Named("tumorGradeList")
  @Override
  public List<TumorGrade> getTumorGradeList()
  {
    return this.getValueList(TumorGrade.class);
  }
  
  @Produces
  @ConversationScoped
  @Named("collectionSiteList")
  public List<SiteAnnotation> getSiteAnnotations()
  {
    return this.getValueList(SiteAnnotation.class);
  }
  
  @Produces
  @Named("yesNoList")
  @ApplicationScoped
  @Override
  public List<YesNoChoices> getYesNoList()
  {
    return this.getValueList(YesNoChoices.class);
  }

}
