/**
 * 
 */
package edu.bcm.dldcc.big.admin.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

/**
 * @author pew
 *
 */
@Entity
public class Announcement implements Serializable
{
  Long id;
  Integer version;
  Date createDate;
  String userName;
  String text;
  String program;
  Date expireDate;

  /**
   * 
   */
  public Announcement()
  {
    super();
  }

  /**
   * @return the id
   */
  @Id
  @GeneratedValue(generator="announcementGenerator")
  @SequenceGenerator(name="announcementGenerator", sequenceName="ANNOUNCEMENT_SEQ")
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
   * @return the createDate
   */
  @Temporal(TemporalType.TIMESTAMP)
  public Date getCreateDate()
  {
    return this.createDate;
  }

  /**
   * @param createDate the createDate to set
   */
  public void setCreateDate(Date createDate)
  {
    this.createDate = createDate;
  }

  /**
   * @return the userName
   */
  public String getUserName()
  {
    return this.userName;
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  /**
   * @return the text
   */
  @Length(max=200)
  public String getText()
  {
    return this.text;
  }

  /**
   * @param text the text to set
   */
  public void setText(String text)
  {
    this.text = text;
  }

  /**
   * @return the program
   */
  public String getProgram()
  {
    return this.program;
  }

  /**
   * @param program the program to set
   */
  public void setProgram(String program)
  {
    this.program = program;
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
        + ((this.createDate == null) ? 0 : this.createDate.hashCode());
    result = prime * result
        + ((this.program == null) ? 0 : this.program.hashCode());
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
    Announcement other = (Announcement) obj;
    if (this.createDate == null)
    {
      if (other.createDate != null)
        return false;
    }
    else if (!this.createDate.equals(other.createDate))
      return false;
    if (this.program == null)
    {
      if (other.program != null)
        return false;
    }
    else if (!this.program.equals(other.program))
      return false;
    return true;
  }

  /**
   * @return the expireDate
   */
  @Temporal(TemporalType.TIMESTAMP)
  public Date getExpireDate()
  {
    return this.expireDate;
  }

  /**
   * @param expireDate the expireDate to set
   */
  public void setExpireDate(Date expireDate)
  {
    this.expireDate = expireDate;
  }

}
