/**
 * 
 */
package edu.bcm.dldcc.big.auditing.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import edu.bcm.dldcc.big.annotation.listeners.RevisionListener;

/**
 * @author pew
 *
 */
@Entity
@Table(name="REVINFO")
@RevisionEntity(RevisionListener.class)
public class AcquireAuditingRevision
{

  @Id
  @Column(name="REV")
  @GeneratedValue(generator="revisionGenerator")
  @SequenceGenerator(name="revisionGenerator", sequenceName="REVISION_SEQ")
  @RevisionNumber
  private Long id;
  
  @Temporal(TemporalType.TIMESTAMP)
  @RevisionTimestamp
  private Date timestamp;
  
  private String username;
  
  private String rationale;
  
  /**
   * 
   */
  public AcquireAuditingRevision()
  {
    super();
  }

  /**
   * @return the id
   */
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
   * @return the timestamp
   */
  public Date getTimestamp()
  {
    return this.timestamp;
  }

  /**
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(Date timestamp)
  {
    this.timestamp = timestamp;
  }

  /**
   * @return the username
   */
  public String getUsername()
  {
    return this.username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username)
  {
    this.username = username;
  }

  /**
   * @return the rationale
   */
  public String getRationale()
  {
    return this.rationale;
  }

  /**
   * @param rationale the rationale to set
   */
  public void setRationale(String rationale)
  {
    this.rationale = rationale;
  }

}
