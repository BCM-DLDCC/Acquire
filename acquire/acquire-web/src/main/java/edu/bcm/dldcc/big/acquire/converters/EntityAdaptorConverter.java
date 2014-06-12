/**
 * 
 */
package edu.bcm.dldcc.big.acquire.converters;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.acquire.inventory.EntityResolver;
import edu.bcm.dldcc.big.acquire.inventory.data.EntityAdaptor;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author pew
 *
 */
@RequestScoped
@FacesConverter("entityAdaptorConverter")
public class EntityAdaptorConverter implements Converter
{

  @Inject
  private EntityResolver resolver;
  
  @Inject
  @Operations
  @Annotations
  private EntityManager em;
  
  /**
   * 
   */
  public EntityAdaptorConverter()
  {
    super();
  }

  /* (non-Javadoc)
   * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component,
      String value)
  {
    EntityAdaptor<AbstractDomainObject> object = null;
    EntityMap map = em.find(EntityMap.class, value);
    try
    {
      object = resolver.getCaTissueEntity(map);
    }
    catch (ClassNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return object;
  }

  /* (non-Javadoc)
   * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component,
      Object value)
  {
    return value.toString();
  }

}
