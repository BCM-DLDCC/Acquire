/**
 * 
 */
package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * @author pew
 *
 */
@Embeddable
public class FundingSource implements Serializable
{
  String name ="";
  Date startDate;
  Date endDate;
  /**
   * 
   */
  public FundingSource()
  {
    super();
  }
  /**
   * @return the name
   */
  public String getName()
  {
    return this.name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name)
  {
    this.name = name;
  }
  /**
   * @return the grantStartDate
   */
  @Temporal(TemporalType.DATE)
  public Date getStartDate()
  {
    return this.startDate;
  }
  /**
   * @param grantStartDate the grantStartDate to set
   */
  public void setStartDate(Date grantStartDate)
  {
    this.startDate = grantStartDate;
  }
  /**
   * @return the grantEndDate
   */
  @Temporal(TemporalType.DATE)
  public Date getEndDate()
  {
    return this.endDate;
  }
  /**
   * @param grantEndDate the grantEndDate to set
   */
  public void setEndDate(Date grantEndDate)
  {
    this.endDate = grantEndDate;
  }

}
