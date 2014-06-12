package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.rac.data.AgeRange;
import edu.bcm.dldcc.big.search.SearchOperator;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 8:34:06 AM
 */
@Embeddable
@Audited
public class AgeAtCollection implements Serializable,
    Comparable<AgeAtCollection>
{

  /**
   * 
   */
  private static final long serialVersionUID = 2264354419894223451L;
  private SearchOperator operator;
  private AgeRange ageRange;

  public AgeAtCollection()
  {
    super();
  }
  
  /**
   * Convenience constructor to pre-set the operator and age range.
   * 
   * @param operator
   * @param range
   */
  public AgeAtCollection(SearchOperator operator, AgeRange range)
  {
    this.setOperator(operator);
    this.setAgeRange(range);
  }

  /**
   * @return the operator
   */
  @Enumerated(EnumType.STRING)
  public SearchOperator getOperator()
  {
    return this.operator;
  }

  /**
   * @param operator
   *          the operator to set
   */
  public void setOperator(SearchOperator operator)
  {
    this.operator = operator;
  }

  /**
   * @return the ageRange
   */
  @Enumerated(EnumType.STRING)
  public AgeRange getAgeRange()
  {
    return this.ageRange;
  }

  /**
   * @param ageRange
   *          the ageRange to set
   */
  public void setAgeRange(AgeRange ageRange)
  {
    this.ageRange = ageRange;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
        prime * result
            + ((this.ageRange == null) ? 0 : this.ageRange.hashCode());
    result =
        prime * result
            + ((this.operator == null) ? 0 : this.operator.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof AgeAtCollection))
    {
      return false;
    }
    AgeAtCollection other = (AgeAtCollection) obj;
    if (this.ageRange != other.ageRange)
    {
      return false;
    }
    if (this.operator != other.operator)
    {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(AgeAtCollection o)
  {
    /*
     * When a new AgeAtCollection is added, it needs to be displayed at
     * the bottom of the list. Therefore, a null is treated as greater than
     * any value
     */
    if (o == null)
    {
      return -1;
    }
    if (this.getAgeRange() == null)
    {
      if (o.getAgeRange() == null)
      {
        return this.compareOperator(o);
      }

      return 1;
    }

    if (o.getAgeRange() == null)
    {
      return 1;
    }

    if (this.getAgeRange().equals(o.getAgeRange()))
    {
      return this.compareOperator(o);
    }

    return this.getAgeRange().getRange().getLow().getValue()
        .compareTo(o.getAgeRange().getRange().getLow().getValue());

  }

  private int compareOperator(AgeAtCollection o)
  {
    /*
     * When a new AgeAtCollection is added, it needs to be displayed at
     * the bottom of the list. Therefore, a null is treated as greater than
     * any value
     */
    if (this.getOperator() == null)
    {
      if (o.getOperator() == null)
      {
        return 0;
      }

      return 1;
    }
    if (o.getOperator() == null)
    {
      return -1;
    }

    return this.getOperator().toString().compareTo(o.getOperator().toString());
  }

}