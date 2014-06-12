package edu.bcm.dldcc.big.inventory.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;

/**
 * Entity implementation class for Entity: ParticipantMap
 *
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(
    columnNames={"ENTITYID", "CATISSUE", "ENTITYNAME"}))
@Audited
public class EntityMap implements Serializable {

  
  private String id;
  private Long entityId;
  private Integer version;
  private CaTissueInstance caTissue;
  private static final long serialVersionUID = 1L;
  private String entityName;

  public EntityMap() {
    super();
  }   
  @Id    
  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }   
  public Long getEntityId() {
    return this.entityId;
  }

  public void setEntityId(Long participantId) {
    this.entityId = participantId;
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
   * @return the caTissue
   */
  @Enumerated
  public CaTissueInstance getCaTissue()
  {
    return this.caTissue;
  }
  /**
   * @param caTissue the caTissue to set
   */
  public void setCaTissue(CaTissueInstance caTissue)
  {
    this.caTissue = caTissue;
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
   * @return the entityName
   */
  public String getEntityName()
  {
    return this.entityName;
  }
  /**
   * @param entityName the entityName to set
   */
  public void setEntityName(String entityName)
  {
    this.entityName = entityName;
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
        + ((this.caTissue == null) ? 0 : this.caTissue.hashCode());
    result = prime * result
        + ((this.entityId == null) ? 0 : this.entityId.hashCode());
    result = prime * result
        + ((this.entityName == null) ? 0 : this.entityName.hashCode());
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
    EntityMap other = (EntityMap) obj;
    if (this.caTissue != other.caTissue)
      return false;
    if (this.entityId == null)
    {
      if (other.entityId != null)
        return false;
    }
    else if (!this.entityId.equals(other.entityId))
      return false;
    if (this.entityName == null)
    {
      if (other.entityName != null)
        return false;
    }
    else if (!this.entityName.equals(other.entityName))
      return false;
    return true;
  }
   
}
