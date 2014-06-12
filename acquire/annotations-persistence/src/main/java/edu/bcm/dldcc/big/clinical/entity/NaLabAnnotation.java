package edu.bcm.dldcc.big.clinical.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.clinical.data.DerivativeType;
import edu.bcm.dldcc.big.clinical.data.DnaQuality;

/**
 * Entity implementation class for Entity: NaLabAnnotation
 * 
 */
@Entity
@Table(name="NaAnnotation")
@Audited
public class NaLabAnnotation implements
    Serializable
{

  private static final long serialVersionUID = 1L;
  
  private DerivativeType type;
  private DnaQuality quality;
  private BigDecimal rin;
  private AliquotAnnotation parent;
  private String id;
  private String naLabel;

  public NaLabAnnotation()
  {
    super();
  }


  /**
   * @return the type
   */
  @Enumerated(EnumType.STRING)
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
   * @return the quality
   */
  @Enumerated(EnumType.STRING)
  public DnaQuality getQuality()
  {
    return this.quality;
  }


  /**
   * @param quality the quality to set
   */
  public void setQuality(DnaQuality quality)
  {
    this.quality = quality;
  }


  /**
   * @return the metric
   */
  @DecimalMax(value="10.0")
  @DecimalMin(value="0.0")
  public BigDecimal getRin()
  {
    return this.rin;
  }


  /**
   * @param metric the metric to set
   */
  public void setRin(BigDecimal metric)
  {
    this.rin = metric;
  }
  
  /**
   * @return the parent
   */
  @ManyToOne
  public AliquotAnnotation getParent()
  {
    return this.parent;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(AliquotAnnotation parent)
  {
    this.parent = parent;
  }
  
  /**
   * @return the id
   */
  @Id
  public String getId()
  {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id)
  {
    this.id = id;
  }
  
  @PrePersist
  public void generateId()
  {
    if(this.getId() == null || this.getId().isEmpty())
    {
      this.setId(UUID.randomUUID().toString());
    }
  }


  /**
   * @return the naLabel
   */
  public String getNaLabel()
  {
    return this.naLabel;
  }


  /**
   * @param naLabel the naLabel to set
   */
  public void setNaLabel(String naLabel)
  {
    this.naLabel = naLabel;
  }

}
