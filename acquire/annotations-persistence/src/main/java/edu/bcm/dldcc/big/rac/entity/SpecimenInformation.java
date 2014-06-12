package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.rac.data.RequestedSampleType;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 8:34:07 AM
 */
@Entity
@Audited
public class SpecimenInformation implements Serializable
{
  /*
   * This class is designated as entity to overcome issues in auditing a 
   * map of Embeddables, where the key is a field in the Embeddable
   */

  /**
   * 
   */
  private static final long serialVersionUID = -2495152836746156824L;
  private Long id;
  private Integer version;
  private RequestedSampleType type;
  private Double amount;
  private Integer numberContainers;
  private Integer totalNumber;
  private Double secondaryAmount;
  private Double tertiaryAmount;

  public SpecimenInformation()
  {
    super();
  }

  /**
   * @return the type
   */
  @Enumerated(EnumType.STRING)
  public RequestedSampleType getType()
  {
    return this.type;
  }

  /**
   * @param type the type to set
   */
  public void setType(RequestedSampleType type)
  {
    this.type = type;
  }

  /**
   * @return the amount
   */
  public Double getAmount()
  {
    return this.amount;
  }

  /**
   * @param amount the amount to set
   */
  public void setAmount(Double amount)
  {
    this.amount = amount;
  }

  /**
   * @return the numberContainers
   */
  public Integer getNumberContainers()
  {
    return this.numberContainers;
  }

  /**
   * @param numberContainers the numberContainers to set
   */
  public void setNumberContainers(Integer numberContainers)
  {
    this.numberContainers = numberContainers;
  }

  /**
   * @return the totalNumber
   */
  public Integer getTotalNumber()
  {
    return this.totalNumber;
  }

  /**
   * @param totalNumber the totalNumber to set
   */
  public void setTotalNumber(Integer totalNumber)
  {
    this.totalNumber = totalNumber;
  }

  /**
   * @return the secondaryAmount
   */
  public Double getSecondaryAmount()
  {
    return this.secondaryAmount;
  }

  /**
   * @param secondaryAmount the secondaryAmount to set
   */
  public void setSecondaryAmount(Double secondaryAmount)
  {
    this.secondaryAmount = secondaryAmount;
  }

  /**
   * @return the tertiaryAmount
   */
  public Double getTertiaryAmount()
  {
    return this.tertiaryAmount;
  }

  /**
   * @param tertiaryAmount the tertiaryAmount to set
   */
  public void setTertiaryAmount(Double tertiaryAmount)
  {
    this.tertiaryAmount = tertiaryAmount;
  }

  /**
   * @return the id
   */
  @Id
  @GeneratedValue(generator="specimenInfo")
  @SequenceGenerator(name="specimenInfo", sequenceName="SPECIMEN_INFO_SEQ")
  public Long getId()
  {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * @return the version
   */
  @Version
  public Integer getVersion()
  {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(Integer version)
  {
    this.version = version;
  }

}