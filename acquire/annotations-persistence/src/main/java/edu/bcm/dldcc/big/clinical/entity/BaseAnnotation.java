/**
 * 
 */
package edu.bcm.dldcc.big.clinical.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;

import org.hibernate.envers.Audited;

/**
 * @author pew
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Audited
public abstract class BaseAnnotation
{

  private String id;
  
  /**
   * 
   */
  public BaseAnnotation()
  {
    super();
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

}
