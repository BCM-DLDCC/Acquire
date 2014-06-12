/**
 * 
 */
package edu.bcm.dldcc.big.clinical.entity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

/**
 * @author pew
 *
 */
@Entity
@Audited
@Table(name="CHILDANNOTATE")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class BaseAliquotAnnotation extends BaseAnnotation
{
  AliquotAnnotation parent;
  
  /**
   * 
   */
  public BaseAliquotAnnotation()
  {
    super();
  }

  /**
   * @return the parent
   */
  @ManyToOne
  @ForeignKey(name="parentFK")
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

}
