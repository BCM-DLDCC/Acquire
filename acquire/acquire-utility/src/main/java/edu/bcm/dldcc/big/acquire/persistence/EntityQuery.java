package edu.bcm.dldcc.big.acquire.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * A Query object for JPA 2.
 * 
 * @author Gavin King Modified by Benjamin Pew
 * 
 */
public abstract class EntityQuery<E> extends Query<EntityManager, E>
{

  private List<E> resultList;
  private E singleResult;
  private Long resultCount;
  private Map<String, String> hints;
  protected Class<E> typeClass;
  private Root<E> queryRoot;
  
  @Inject
  @Any
  Instance<EntityManager> anyEntityManager;

  /**
   * Validate the query
   * 
   * @throws IllegalStateException
   *           if the query is not valid
   */
  @Override
  public void validate()
  {
    super.validate();

  }

  @Override
  @TransactionAttribute
  public boolean isNextExists()
  {
    return resultList != null && getMaxResults() != null
        && resultList.size() > getMaxResults();
  }

  /**
   * Get the list of results this query returns
   * 
   * Any changed restriction values will be applied
   */
  @TransactionAttribute
  @Override
  public List<E> getResultList()
  {
    refresh();
    this.initParameters();
    initResultList();
    return truncResultList(resultList);
  }

  private void initResultList()
  {
    if (resultList == null)
    {
      TypedQuery<E> query = createQuery();
      resultList = query == null ? null : query.getResultList();
    }
  }

  /**
   * Get a single result from the query
   * 
   * Any changed restriction values will be applied
   * 
   * @throws NonUniqueResultException
   *           if there is more than one result
   */
  @TransactionAttribute
  @Override
  public E getSingleResult()
  {
    refresh();
    this.initParameters();
    initSingleResult();
    return singleResult;
  }

  private void initSingleResult()
  {
    if (singleResult == null)
    {
      TypedQuery<E> query = createQuery();
      singleResult = (query == null ? null : query.getSingleResult());
    }
  }

  /**
   * Get the number of results this query returns
   * 
   * Any changed restriction values will be applied
   */
  @TransactionAttribute
  @Override
  public Long getResultCount()
  {
    refresh();
    this.initParameters();
    initResultCount();
    return resultCount;
  }

  protected abstract void initParameters();

  private void initResultCount()
  {
    if (resultCount == null)
    {
      javax.persistence.TypedQuery<Long> query = createCountQuery();
      resultCount = query == null ? null : (Long) query.getSingleResult();
    }
  }

  /**
   * The refresh method will cause the result to be cleared. The next access to
   * the result set will cause the query to be executed.
   * 
   * This method <b>does not</b> cause the ejbql or restrictions to reread. If
   * you want to update the ejbql or restrictions you must call
   * {@link #setEjbql(String)} or {@link #setRestrictions(List)}
   */
  @Override
  public void refresh()
  {
    super.refresh();
    resultCount = null;
    resultList = null;
    singleResult = null;
  }

  public EntityQuery()
  {
    super();
  }

  protected void initEjbql()
  {
    CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<E> entity = cb.createQuery(typeClass);
    queryRoot = entity.from(typeClass);
    entity.select(queryRoot);
    this.setEjbql(entity);
  }

  protected void initCountEjbql()
  {
    CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
    CriteriaQuery<Long> count = cb.createQuery(Long.class);
    Root<E> root = count.from(typeClass);
    count.select(cb.count(root));
    this.setCountEjbql(count);
  }


  @TransactionAttribute
  protected TypedQuery<E> createQuery()
  {
    CriteriaQuery<E> criteria = this.getEjbql();
    CriteriaBuilder cb = processRestrictions(criteria);
    if (this.getOrder() != null && !this.getOrder().isEmpty())
    {
      List<Expression> orderExpressions = this.getOrder();
      List<Order> orders = new ArrayList<Order>();
      for (Expression exp : orderExpressions)
      {
        if (this.getOrderDirection().equals(Query.DIR_ASC))
        {
          orders.add(cb.asc(exp));
        } else
        {
          orders.add(cb.desc(exp));
        }
      }

    }

    if (this.getGroupBy() != null && !this.getGroupBy().isEmpty())
    {
      criteria.groupBy(this.getGroupBy());
    }

    TypedQuery<E> query = getEntityManager().createQuery(criteria);

    return setupQuery(query);
  }

  protected CriteriaBuilder processRestrictions(CriteriaQuery<?> criteria)
  {
    CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
    if (this.getRestrictions() != null && !this.getRestrictions().isEmpty())
    {
      Predicate[] array = this.getRestrictions().toArray(
          new Predicate[this.getRestrictions().size()]);
      Predicate restrictionClause = null;
      if (this.getRestrictionLogicOperator().equals(Query.LOGIC_OPERATOR_AND))
      {
        restrictionClause = cb.and(array);
      } else
      {
        restrictionClause = cb.or(array);
      }

      criteria.where(restrictionClause);
    }
    return cb;
  }

  protected TypedQuery<E> setupQuery(TypedQuery<E> query)
  {
    setParameters(query);
    if (getFirstResult() != null)
      query.setFirstResult(getFirstResult());
    if (getMaxResults() != null)
      query.setMaxResults(getMaxResults() + 1); // add one, so we can tell if
                                                // there is another page
    if (getHints() != null)
    {
      for (Map.Entry<String, String> me : getHints().entrySet())
      {
        query.setHint(me.getKey(), me.getValue());
      }
    }
    return query;
  }

  @TransactionAttribute
  protected TypedQuery<Long> createCountQuery()
  {

    CriteriaQuery<Long> criteria = this.getCountEjbql();
    this.processRestrictions(criteria);

    TypedQuery<Long> query = getEntityManager().createQuery(criteria);
    this.setParameters(query);
    return query;
  }

  public void setParameters(TypedQuery query)
  {
    for (Map.Entry<ParameterExpression<?>, Object> entry : this
        .getParameterMap().entrySet())
    {
      query.setParameter(entry.getKey(), entry.getValue());
    }
  }

  public Map<String, String> getHints()
  {
    return hints;
  }

  public void setHints(Map<String, String> hints)
  {
    this.hints = hints;
  }

  public Class<E> getTypeClass()
  {
    return typeClass;
  }

  public void setTypeClass(Class<E> typeClass)
  {
    this.typeClass = typeClass;
  }

  public Root<E> getQueryRoot()
  {
    return queryRoot;
  }

  public void setQueryRoot(Root<E> queryRoot)
  {
    this.queryRoot = queryRoot;
  }
  
  protected EntityManager getEntityManager(AnnotationLiteral<?>... qualifier)
  {
    return anyEntityManager.select(qualifier).get();
  }

}
