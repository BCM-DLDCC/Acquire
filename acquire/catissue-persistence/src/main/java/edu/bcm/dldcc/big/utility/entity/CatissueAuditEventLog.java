package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the CATISSUE_AUDIT_EVENT_LOG database table.
 * 
 */
@Entity
@Table(name = "CATISSUE_AUDIT_EVENT_LOG")
public class CatissueAuditEventLog implements Serializable
{
  private static final long serialVersionUID = 1L;
  private long identifier;
  private List<CatissueAuditEventDetail> catissueAuditEventDetails;
  private CatissueAuditEvent catissueAuditEvent;

  public CatissueAuditEventLog()
  {
    super();
  }

  @Id
  @SequenceGenerator(name = "CATISSUE_AUDIT_EVENT_LOG_IDENTIFIER_GENERATOR",
      sequenceName = "CATISSUE_AUDIT_EVENT_LOG_SEQ")
  @GeneratedValue(strategy = GenerationType.SEQUENCE,
      generator = "CATISSUE_AUDIT_EVENT_LOG_IDENTIFIER_GENERATOR")
  public long getIdentifier()
  {
    return this.identifier;
  }

  public void setIdentifier(long identifier)
  {
    this.identifier = identifier;
  }

  // bi-directional many-to-one association to CatissueAuditEventDetail
  @OneToMany(mappedBy = "catissueAuditEventLog")
  public List<CatissueAuditEventDetail> getCatissueAuditEventDetails()
  {
    return this.catissueAuditEventDetails;
  }

  public void setCatissueAuditEventDetails(
      List<CatissueAuditEventDetail> catissueAuditEventDetails)
  {
    this.catissueAuditEventDetails = catissueAuditEventDetails;
  }

  // bi-directional many-to-one association to CatissueAuditEvent
  @ManyToOne
  @JoinColumn(name = "AUDIT_EVENT_ID")
  public CatissueAuditEvent getCatissueAuditEvent()
  {
    return this.catissueAuditEvent;
  }

  public void setCatissueAuditEvent(CatissueAuditEvent catissueAuditEvent)
  {
    this.catissueAuditEvent = catissueAuditEvent;
  }

}