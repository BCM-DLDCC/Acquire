package edu.bcm.dldcc.big.acquire.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

/**
 * Base class for components which manage a query result set. This class may be
 * reused by either configuration or extension, and may be bound directly to a
 * view, or accessed by some intermediate Seam component. Modified from Seam
 * code to work through JPA Criteria queries
 * 
 * @author Benjamin Pew
 * 
 */
public abstract class Query<T, E>
{
  
  protected static final String DIR_ASC = "asc";
  protected static final String DIR_DESC = "desc";
  protected static final String LOGIC_OPERATOR_AND = "and";
  protected static final String LOGIC_OPERATOR_OR = "or";

  private CriteriaQuery<E> ejbql;
  private CriteriaQuery<Long> countEjbql;
  private Integer firstResult;
  private Integer maxResults;
  private List<Predicate> restrictions = new ArrayList<Predicate>();
  private List<Expression> order;
  private String orderDirection;
  private String restrictionLogicOperator;
  private List<Expression<?>> groupBy;
  private String orderColumn;

  private Map<ParameterExpression<?>, Object> parameterMap = 
    new HashMap<ParameterExpression<?>, Object>();

  public abstract List<E> getResultList();

  public abstract E getSingleResult();

  public abstract Long getResultCount();

  @PostConstruct
  public void validate()
  {
    if (getEjbql() == null)
    {
      throw new IllegalStateException("ejbql is null");
    }
  }



  /**
   * Move the result set cursor to the beginning of the last page
   * 
   */
  @TransactionAttribute
  public void last()
  {
    setFirstResult(getLastFirstResult().intValue());
  }

  /**
   * Move the result set cursor to the beginning of the next page
   * 
   */
  public void next()
  {
    setFirstResult(getNextFirstResult());
  }

  /**
   * Move the result set cursor to the beginning of the previous page
   * 
   */
  public void previous()
  {
    setFirstResult(getPreviousFirstResult());
  }

  /**
   * Move the result set cursor to the beginning of the first page
   * 
   */
  public void first()
  {
    setFirstResult(0);
  }


  /**
   * Get the index of the first result of the last page
   * 
   */
  @TransactionAttribute
  public Long getLastFirstResult()
  {
    Integer pc = getPageCount();
    return pc == null ? null : (pc.longValue() - 1) * getMaxResults();
  }

  /**
   * Get the index of the first result of the next page
   * 
   */
  public int getNextFirstResult()
  {
    Integer fr = getFirstResult();
    return (fr == null ? 0 : fr) + getMaxResults();
  }

  /**
   * Get the index of the first result of the previous page
   * 
   */
  public int getPreviousFirstResult()
  {
    Integer fr = getFirstResult();
    Integer mr = getMaxResults();
    return mr >= (fr == null ? 0 : fr) ? 0 : fr - mr;
  }

  /**
   * Get the total number of pages
   * 
   */
  @TransactionAttribute
  public Integer getPageCount()
  {
    if (getMaxResults() == null)
    {
      return null;
    } else
    {
      int rc = getResultCount().intValue();
      int mr = getMaxResults().intValue();
      int pages = rc / mr;
      return rc % mr == 0 ? pages : pages + 1;
    }
  }

  /**
   * Return the ejbql to used in a count query (for calculating number of
   * results)
   * 
   * @return String The ejbql query
   */
  protected CriteriaQuery<Long> getCountEjbql()
  {
    return countEjbql;
  }

  public CriteriaQuery<E> getEjbql()
  {
    return ejbql;
  }

  /**
   * Set the ejbql to use. Calling this causes the ejbql to be reparsed and the
   * query to be refreshed
   */
  public void setEjbql(CriteriaQuery<E> ejbql)
  {
    this.ejbql = ejbql;
    refresh();
  }

  /**
   * Returns the index of the first result of the current page
   */
  public Integer getFirstResult()
  {
    return firstResult;
  }

  /**
   * Returns true if the previous page exists
   */
  public boolean isPreviousExists()
  {
    return getFirstResult() != null && getFirstResult() != 0;
  }

  /**
   * Returns true if next page exists
   */
  public abstract boolean isNextExists();

  /**
   * Returns true if the query is paginated, revealing whether navigation
   * controls are needed.
   */
  public boolean isPaginated()
  {
    return isNextExists() || isPreviousExists();
  }

  /**
   * Set the index at which the page to display should start
   */
  public void setFirstResult(Integer firstResult)
  {
    this.firstResult = firstResult;
    refresh();
  }

  /**
   * The page size
   */
  public Integer getMaxResults()
  {
    return maxResults;
  }

  public void setMaxResults(Integer maxResults)
  {
    this.maxResults = maxResults;
    refresh();
  }

  /**
   * List of restrictions to apply to the query.
   * 
   * For a query such as 'from Foo f' a restriction could be 'f.bar =
   * #{foo.bar}'
   */
  public List<Predicate> getRestrictions()
  {
    return restrictions;
  }

  /**
   * Calling setRestrictions causes the restrictions to be reparsed and the
   * query refreshed
   */
  public void setRestrictions(List<Predicate> restrictions)
  {
    this.restrictions = restrictions;
    refresh();
  }

  public List<Expression<?>> getGroupBy()
  {
    return groupBy;
  }

  public void setGroupBy(List<Expression<?>> groupBy)
  {
    this.groupBy = groupBy;
  }

  /**
   * The order clause of the query
   */

  public List<Expression> getOrder()
  {
    return order;
  }

  public void setOrder(List<Expression> order)
  {
    this.order = order;
    refresh();
  }

  protected void refresh()
  {
    // TODO Auto-generated method stub
    
  }

  public String getOrderDirection()
  {
    return orderDirection;
  }

  public void setOrderDirection(String orderDirection)
  {
    this.orderDirection = sanitizeOrderDirection(orderDirection);
  }

  private String sanitizeOrderDirection(String direction)
  {
    if (direction == null || direction.length() == 0)
    {
      return null;
    } else if (direction.equalsIgnoreCase(DIR_ASC))
    {
      return DIR_ASC;
    } else if (direction.equalsIgnoreCase(DIR_DESC))
    {
      return DIR_DESC;
    } else
    {
      throw new IllegalArgumentException("invalid order direction");
    }
  }

  public String getRestrictionLogicOperator()
  {
    return restrictionLogicOperator != null ? restrictionLogicOperator
        : LOGIC_OPERATOR_AND;
  }

  public void setRestrictionLogicOperator(String operator)
  {
    restrictionLogicOperator = sanitizeRestrictionLogicOperator(operator);
  }

  private String sanitizeRestrictionLogicOperator(String operator)
  {
    if (operator == null || operator.trim().length() == 0)
    {
      return LOGIC_OPERATOR_AND;
    }
    if (!(LOGIC_OPERATOR_AND.equals(operator) || LOGIC_OPERATOR_OR
        .equals(operator)))
    {
      throw new IllegalArgumentException("Invalid restriction logic operator: "
          + operator);
    } else
    {
      return operator;
    }
  }

  protected Map<ParameterExpression<?>, Object> getParameterMap()
  {
    return parameterMap;
  }

  protected void setParameterMap(Map<ParameterExpression<?>, Object> parameters)
  {
    this.parameterMap = parameters;
  }

  protected List<E> truncResultList(List<E> results)
  {
    Integer mr = getMaxResults();
    if (mr != null && results.size() > mr)
    {
      return results.subList(0, mr);
    } else
    {
      return results;
    }
  }

  protected void setCountEjbql(CriteriaQuery<Long> countEjbql)
  {
    this.countEjbql = countEjbql;
    refresh();
  }

  public String getOrderColumn()
  {
    return orderColumn;
  }

  public void setOrderColumn(String orderColumn)
  {
    this.orderColumn = orderColumn;
  }

}
