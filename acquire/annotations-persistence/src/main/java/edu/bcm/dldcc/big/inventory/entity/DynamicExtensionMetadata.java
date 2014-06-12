/**
 * 
 */
package edu.bcm.dldcc.big.inventory.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;

/**
 * @author pew
 *
 */
@Entity
public class DynamicExtensionMetadata implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -3115070448537827718L;
  private Long id;
  private Integer version;
  private CaTissueInstance instance;
  private String tableName;
  private String priorTreatmentColumn;
  private String warmEschemiaColumn;
  private String identifierColumn;
  private String formId;

  /**
   * 
   */
  public DynamicExtensionMetadata()
  {
    super();
  }

  /**
   * @return the id
   */
  @Id
  @GeneratedValue
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

  /**
   * @return the instance
   */
  @Enumerated
  @Column(unique=true, updatable=false, insertable=true)
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
   * @return the tableName
   */
  public String getTableName()
  {
    return this.tableName;
  }

  /**
   * @param tableName the tableName to set
   */
  public void setTableName(String tableName)
  {
    this.tableName = tableName;
  }

  /**
   * @return the priorTreatmentColumn
   */
  public String getPriorTreatmentColumn()
  {
    return this.priorTreatmentColumn;
  }

  /**
   * @param priorTreatmentColumn the priorTreatmentColumn to set
   */
  public void setPriorTreatmentColumn(String priorTreatmentColumn)
  {
    this.priorTreatmentColumn = priorTreatmentColumn;
  }

  /**
   * @return the warmEschemiaColumn
   */
  public String getWarmEschemiaColumn()
  {
    return this.warmEschemiaColumn;
  }

  /**
   * @param warmEschemiaColumn the warmEschemiaColumn to set
   */
  public void setWarmEschemiaColumn(String warmEschemiaColumn)
  {
    this.warmEschemiaColumn = warmEschemiaColumn;
  }

  /**
   * @return the identifierColumn
   */
  public String getIdentifierColumn()
  {
    return this.identifierColumn;
  }

  /**
   * @param identifierColumn the identifierColumn to set
   */
  public void setIdentifierColumn(String identifierColumn)
  {
    this.identifierColumn = identifierColumn;
  }

  /**
   * @return the formId
   */
  public String getFormId()
  {
    return this.formId;
  }

  /**
   * @param formId the formId to set
   */
  public void setFormId(String formId)
  {
    this.formId = formId;
  }

}
