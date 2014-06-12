/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.data;

import java.math.BigDecimal;
import java.util.Date;

import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.clinical.data.DerivativeType;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * Class used to hold data required to create caTissue information
 * for NA Lab derivatives.
 * 
 * @author pew
 *
 */
public class NaLabData
{
  private String patientId; 
  private String parentId;
  private Date dateReceived;
  
  private Double amountConsumed = 0D;
  private String naLabLabel;
  private Double concentration;
  private Double amount;
  private DerivativeType type;
  private CaTissueInstance instance = CaTissueInstance.TCRB;
  private Specimen parent;
  
  
  /**
   *  Default constructor
   */
  public NaLabData()
  {
    super();
  }

  /**
   * @return the patientId
   */
  public String getPatientId()
  {
    return this.patientId;
  }

  /**
   * @param patientId the patientId to set
   */
  public void setPatientId(String patientId)
  {
    this.patientId = patientId;
  }

  /**
   * @return the parentId
   */
  public String getParentId()
  {
    return this.parentId;
  }

  /**
   * @param parentId the parentId to set
   */
  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }

  /**
   * @return the dateReceived
   */
  public Date getDateReceived()
  {
    return this.dateReceived;
  }

  /**
   * @param dateReceived the dateReceived to set
   */
  public void setDateReceived(Date dateReceived)
  {
    this.dateReceived = dateReceived;
  }

  /**
   * @return the amountConsumed
   */
  public Double getAmountConsumed()
  {
    return this.amountConsumed;
  }

  /**
   * @param amountConsumed the amountConsumed to set
   */
  public void setAmountConsumed(Double amountConsumed)
  {
    if(amountConsumed != null)
    {
      this.amountConsumed = amountConsumed;
    }
  }

  /**
   * @return the naLabLabel
   */
  public String getNaLabLabel()
  {
    return this.naLabLabel;
  }

  /**
   * @param naLabLabel the naLabLabel to set
   */
  public void setNaLabLabel(String naLabLabel)
  {
    this.naLabLabel = naLabLabel;
  }

  /**
   * @return the concentration
   */
  public Double getConcentration()
  {
    return this.concentration;
  }

  /**
   * @param concentration the concentration to set
   */
  public void setConcentration(Double concentration)
  {
    this.concentration = concentration;
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

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((this.naLabLabel == null) ? 0 : this.naLabLabel.hashCode());
    return result;
  }

  /* (non-Javadoc)
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
    if (getClass() != obj.getClass())
    {
      return false;
    }
    NaLabData other = (NaLabData) obj;
    if (this.naLabLabel == null)
    {
      if (other.naLabLabel != null)
      {
        return false;
      }
    }
    else if (!this.naLabLabel.equals(other.naLabLabel))
    {
      return false;
    }
    return true;
  }

  /**
   * @return the instance
   */
  public CaTissueInstance getInstance()
  {
    return this.instance;
  }

  /**
   * @param instance the instance to set
   */
  public void setInstance(CaTissueInstance instance)
  {
    this.instance = instance;
  }

  /**
   * @return the type
   */
  public DerivativeType getType()
  {
    return this.type;
  }

  /**
   * @param type the type to set
   */
  public void setType(DerivativeType type)
  {
    this.type = type;
  }

  /**
   * @return the parent
   */
  public Specimen getParent()
  {
    return this.parent;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(Specimen parent)
  {
    this.parent = parent;
  }
  
  

}
