/**
 * 
 */
package edu.bcm.dldcc.big.inventory.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.acquire.annotations.integration.CaTissueEntity;
import edu.bcm.dldcc.big.acquire.util.EntityAnnotation;
import edu.wustl.catissuecore.domain.Site;

/**
 * @author pew
 *
 */
@Entity
@Audited
public class SiteAnnotation implements EntityAnnotation, Comparable<SiteAnnotation>
{

  private Integer version;
  private String entityId;
  private String name;
  private List<SiteAnnotation> childSites = new ArrayList<SiteAnnotation>();
  private SiteAnnotation parent;
  
  /**
   * 
   */
  public SiteAnnotation()
  {
    super();
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
   
  /**
   * @return the siteId
   */
  @Override
  @CaTissueEntity(entity = Site.class)
  @Id
  public String getEntityId()
  {
    return this.entityId;
  }
  /**
   * @param entityId the site Id to set
   */
  @Override
  public void setEntityId(String entityId)
  {
    this.entityId = entityId;
  }


  /**
   * @return the childSites
   */
  @OneToMany(mappedBy="parent", fetch=FetchType.EAGER)
  @OrderBy("name ASC")
  public List<SiteAnnotation> getChildSites()
  {
    return this.childSites;
  }


  /**
   * @param childSites the childSites to set
   */
  public void setChildSites(List<SiteAnnotation> childSites)
  {
    this.childSites = childSites;
  }


  /**
   * @return the parent
   */
  @ManyToOne
  public SiteAnnotation getParent()
  {
    return this.parent;
  }


  /**
   * @param parent the parent to set
   */
  public void setParent(SiteAnnotation parent)
  {
    this.parent = parent;
  }


  /**
   * This is stored here from caTissue for performance reasons, so that 
   * it can be used in the scoreboard.
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


  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(SiteAnnotation o)
  {
    return this.getName().compareTo(o.getName());
  }


  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
    SiteAnnotation other = (SiteAnnotation) obj;
    if (this.name == null)
    {
      if (other.name != null)
      {
        return false;
      }
    }
    else if (!this.name.equals(other.name))
    {
      return false;
    }
    return true;
  }
  


}
