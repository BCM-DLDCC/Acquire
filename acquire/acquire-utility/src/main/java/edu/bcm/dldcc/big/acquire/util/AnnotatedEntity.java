/**
 * 
 */
package edu.bcm.dldcc.big.acquire.util;

import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author pew
 *
 */
public class AnnotatedEntity<E extends AbstractDomainObject, A extends EntityAnnotation>
{

  E entity;
  A annotation;
  
  /**
   * 
   */
  public AnnotatedEntity()
  {
    super();
  }
  
  public AnnotatedEntity(E entity, A annotation)
  {
    this.setAnnotation(annotation);
    this.setEntity(entity);
  }

  /**
   * @return the entity
   */
  public E getEntity()
  {
    return this.entity;
  }

  /**
   * @param entity the entity to set
   */
  public void setEntity(E entity)
  {
    this.entity = entity;
  }

  /**
   * @return the annotation
   */
  public A getAnnotation()
  {
    return this.annotation;
  }

  /**
   * @param annotation the annotation to set
   */
  public void setAnnotation(A annotation)
  {
    this.annotation = annotation;
  }

}
