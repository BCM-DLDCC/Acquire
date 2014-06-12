/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.data;

import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * Class to hold both an EntityMap and the corresponding caTissue entity
 * @author pew
 * 
 */
public class EntityAdaptor<T extends AbstractDomainObject>
{

  private EntityMap map;

  private T entity;

  /**
   * Default constructor
   */
  public EntityAdaptor()
  {
    super();
  }

  /**
   * Constructor to set the map and entity
   * 
   * @param id
   * @param object
   */
  public EntityAdaptor(EntityMap id, T object)
  {
    this.setMap(id);
    this.setEntity(object);
  }

  /**
   * @return the map
   */
  public EntityMap getMap()
  {
    return this.map;
  }

  /**
   * @param map
   *          the map to set
   */
  public void setMap(EntityMap map)
  {
    this.map = map;
  }

  /**
   * @return the entity
   */
  public T getEntity()
  {
    return this.entity;
  }

  /**
   * @param entity
   *          the entity to set
   */
  public void setEntity(T entity)
  {
    this.entity = entity;
  }
  
  @Override
  public String toString()
  {
    String value = "";
    if(this.getEntity() instanceof Site)
    {
      value = ((Site) this.getEntity()).getName();
    }
    else if (this.getEntity() instanceof Specimen)
    {
      value = ((Specimen) this.getEntity()).getLabel();
    }
    else if(this.getEntity() instanceof Participant)
    {
      Participant entity = ((Participant) this.getEntity());
      value = entity.getFirstName() + " " + entity.getLastName();
    }
    
    return value;
  }
  
  /**
   * Gets the id of the map in this EntityAdaptor
   * @return
   */
  public String getId()
  {
    return this.getMap().getId();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.map == null) ? 0 : this.map.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EntityAdaptor other = (EntityAdaptor) obj;
    if (this.map == null)
    {
      if (other.map != null)
        return false;
    }
    else if (!this.map.equals(other.map))
      return false;
    return true;
  }

}
