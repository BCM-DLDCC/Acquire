package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the CATISSUE_AUDIT_EVENT_DETAILS database table.
 * 
 */
@Entity
@Table(name = "CATISSUE_AUDIT_EVENT_DETAILS")
public class CatissueAuditEventDetail implements Serializable
{
  private static final long serialVersionUID = 1L;
  private long identifier;
  private String currentValue;
  private String elementName;
  private String previousValue;
  private CatissueAuditEventLog catissueAuditEventLog;

  public CatissueAuditEventDetail()
  {
    super();
  }

  @Id
  @SequenceGenerator(
      name = "CATISSUE_AUDIT_EVENT_DETAILS_IDENTIFIER_GENERATOR",
      sequenceName = "CATISSUE_AUDIT_EVENT_DETAILS_SEQ")
  @GeneratedValue(strategy = GenerationType.SEQUENCE,
      generator = "CATISSUE_AUDIT_EVENT_DETAILS_IDENTIFIER_GENERATOR")
  public long getIdentifier()
  {
    return this.identifier;
  }

  public void setIdentifier(long identifier)
  {
    this.identifier = identifier;
  }

  @Lob()
  @Column(name = "CURRENT_VALUE")
  public String getCurrentValue()
  {
    return this.currentValue;
  }

  public void setCurrentValue(String currentValue)
  {
    this.currentValue = currentValue;
  }

  @Column(name = "ELEMENT_NAME")
  public String getElementName()
  {
    return this.elementName;
  }

  public void setElementName(String elementName)
  {
    this.elementName = elementName;
  }

  @Lob()
  @Column(name = "PREVIOUS_VALUE")
  public String getPreviousValue()
  {
    return this.previousValue;
  }

  public void setPreviousValue(String previousValue)
  {
    this.previousValue = previousValue;
  }

  // bi-directional many-to-one association to CatissueAuditEventLog
  @ManyToOne
  @JoinColumn(name = "AUDIT_EVENT_LOG_ID")
  public CatissueAuditEventLog getCatissueAuditEventLog()
  {
    return this.catissueAuditEventLog;
  }

  public void setCatissueAuditEventLog(
      CatissueAuditEventLog catissueAuditEventLog)
  {
    this.catissueAuditEventLog = catissueAuditEventLog;
  }

}