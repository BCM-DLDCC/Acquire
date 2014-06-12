package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the CATISSUE_AUDIT_EVENT database table.
 * 
 */
@Entity
@Table(name = "CATISSUE_AUDIT_EVENT")
public class CatissueAuditEvent implements Serializable
{
  private static final long serialVersionUID = 1L;
  private long identifier;
  private String comments;
  private Date eventTimestamp;
  private String eventType;
  private String ipAddress;
  private java.math.BigDecimal userId;
  private List<CatissueAuditEventLog> catissueAuditEventLogs;
  private List<CatissueAuditEventQueryLog> catissueAuditEventQueryLogs;

  public CatissueAuditEvent()
  {
    super();
  }

  @Id
  @SequenceGenerator(name = "CATISSUE_AUDIT_EVENT_IDENTIFIER_GENERATOR",
      sequenceName = "CATISSUE_AUDIT_EVENT_SEQ")
  @GeneratedValue(strategy = GenerationType.SEQUENCE,
      generator = "CATISSUE_AUDIT_EVENT_IDENTIFIER_GENERATOR")
  public long getIdentifier()
  {
    return this.identifier;
  }

  public void setIdentifier(long identifier)
  {
    this.identifier = identifier;
  }

  public String getComments()
  {
    return this.comments;
  }

  public void setComments(String comments)
  {
    this.comments = comments;
  }

  @Temporal(TemporalType.DATE)
  @Column(name = "EVENT_TIMESTAMP")
  public Date getEventTimestamp()
  {
    return this.eventTimestamp;
  }

  public void setEventTimestamp(Date eventTimestamp)
  {
    this.eventTimestamp = eventTimestamp;
  }

  @Column(name = "EVENT_TYPE")
  public String getEventType()
  {
    return this.eventType;
  }

  public void setEventType(String eventType)
  {
    this.eventType = eventType;
  }

  @Column(name = "IP_ADDRESS")
  public String getIpAddress()
  {
    return this.ipAddress;
  }

  public void setIpAddress(String ipAddress)
  {
    this.ipAddress = ipAddress;
  }

  @Column(name = "USER_ID")
  public java.math.BigDecimal getUserId()
  {
    return this.userId;
  }

  public void setUserId(java.math.BigDecimal userId)
  {
    this.userId = userId;
  }

  // bi-directional many-to-one association to CatissueAuditEventLog
  @OneToMany(mappedBy = "catissueAuditEvent")
  public List<CatissueAuditEventLog> getCatissueAuditEventLogs()
  {
    return this.catissueAuditEventLogs;
  }

  public void setCatissueAuditEventLogs(
      List<CatissueAuditEventLog> catissueAuditEventLogs)
  {
    this.catissueAuditEventLogs = catissueAuditEventLogs;
  }

  // bi-directional many-to-one association to CatissueAuditEventQueryLog
  @OneToMany(mappedBy = "catissueAuditEvent")
  public List<CatissueAuditEventQueryLog> getCatissueAuditEventQueryLogs()
  {
    return this.catissueAuditEventQueryLogs;
  }

  public void setCatissueAuditEventQueryLogs(
      List<CatissueAuditEventQueryLog> catissueAuditEventQueryLogs)
  {
    this.catissueAuditEventQueryLogs = catissueAuditEventQueryLogs;
  }

}