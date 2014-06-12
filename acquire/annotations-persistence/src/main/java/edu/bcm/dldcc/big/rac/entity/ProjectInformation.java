package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.rac.data.Consortium;
import edu.bcm.dldcc.big.rac.data.IrbStatus;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 8:34:07 AM
 */
@Entity
@Audited
public class ProjectInformation implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -2309053337227498733L;
  private Long id;
  private Integer version;
  private String projectTitle;
  private FundingSource fundingSource = new FundingSource();
  private FundingSource additionalFunding = new FundingSource();
  private IrbStatus irbStatus;
  private String irbName;
  private String irbProtocolNumber;
  private Date irbApprovalDate;
  private Date irbExpiration;
  private Consortium consortium;
  private String researchSummary;
  private Application application;

  public ProjectInformation()
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
   * @return the projectTitle
   */
  public String getProjectTitle()
  {
    return this.projectTitle;
  }

  /**
   * @param projectTitle the projectTitle to set
   */
  public void setProjectTitle(String projectTitle)
  {
    this.projectTitle = projectTitle;
  }

  /**
   * @return the fundingSource
   */
  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name="name", column = @Column(name="fundingSource")),
    @AttributeOverride(name="startDate", column = @Column(name="grantStartDate")),
    @AttributeOverride(name="endDate", column = @Column(name="grantEndDate"))
  })
  public FundingSource getFundingSource()
  {
    if(this.fundingSource == null)
    {
      this.fundingSource = new FundingSource();
    }
    return this.fundingSource;
  }

  /**
   * @param fundingSource the fundingSource to set
   */
  public void setFundingSource(FundingSource fundingSource)
  {
    this.fundingSource = fundingSource;
  }

  

  /**
   * @return the irbStatus
   */
  @Enumerated(EnumType.STRING)
  public IrbStatus getIrbStatus()
  {
    return this.irbStatus;
  }

  /**
   * @param irbStatus the irbStatus to set
   */
  public void setIrbStatus(IrbStatus irbStatus)
  {
    this.irbStatus = irbStatus;
  }

  /**
   * @return the irbName
   */
  public String getIrbName()
  {
    return this.irbName;
  }

  /**
   * @param irbName the irbName to set
   */
  public void setIrbName(String irbName)
  {
    this.irbName = irbName;
  }

  /**
   * @return the irbProtocolNumber
   */
  public String getIrbProtocolNumber()
  {
    return this.irbProtocolNumber;
  }

  /**
   * @param irbProtocolNumber the irbProtocolNumber to set
   */
  public void setIrbProtocolNumber(String irbProtocolNumber)
  {
    this.irbProtocolNumber = irbProtocolNumber;
  }

  /**
   * @return the irbApprovalDate
   */
  @Temporal(TemporalType.DATE)
  public Date getIrbApprovalDate()
  {
    return this.irbApprovalDate;
  }

  /**
   * @param irbApprovalDate the irbApprovalDate to set
   */
  public void setIrbApprovalDate(Date irbApprovalDate)
  {
    this.irbApprovalDate = irbApprovalDate;
  }

  /**
   * @return the irbExpiration
   */
  @Temporal(TemporalType.DATE)
  public Date getIrbExpiration()
  {
    return this.irbExpiration;
  }

  /**
   * @param irbExpiration the irbExpiration to set
   */
  public void setIrbExpiration(Date irbExpiration)
  {
    this.irbExpiration = irbExpiration;
  }

  /**
   * @return the consortium
   */
  @Enumerated(EnumType.STRING)
  public Consortium getConsortium()
  {
    return this.consortium;
  }

  /**
   * @param consortium the consortium to set
   */
  public void setConsortium(Consortium consortium)
  {
    this.consortium = consortium;
  }

  /**
   * @return the researchSummary
   */
  @Lob
  public String getResearchSummary()
  {
    return this.researchSummary;
  }

  /**
   * @param researchSummary the researchSummary to set
   */
  public void setResearchSummary(String researchSummary)
  {
    this.researchSummary = researchSummary;
  }

  /**
   * @return the application
   */
  @OneToOne
  @MapsId
  public Application getApplication()
  {
    return this.application;
  }

  /**
   * @param application the application to set
   */
  public void setApplication(Application application)
  {
    this.application = application;
  }

  /**
   * @return the id
   */
  @Id
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

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((this.irbName == null) ? 0 : this.irbName.hashCode());
    result =
        prime
            * result
            + ((this.irbProtocolNumber == null) ? 0 : this.irbProtocolNumber
                .hashCode());
    result =
        prime * result
            + ((this.projectTitle == null) ? 0 : this.projectTitle.hashCode());
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
    ProjectInformation other = (ProjectInformation) obj;
    if (this.irbName == null)
    {
      if (other.irbName != null)
      {
        return false;
      }
    }
    else if (!this.irbName.equals(other.irbName))
    {
      return false;
    }
    if (this.irbProtocolNumber == null)
    {
      if (other.irbProtocolNumber != null)
      {
        return false;
      }
    }
    else if (!this.irbProtocolNumber.equals(other.irbProtocolNumber))
    {
      return false;
    }
    if (this.projectTitle == null)
    {
      if (other.projectTitle != null)
      {
        return false;
      }
    }
    else if (!this.projectTitle.equals(other.projectTitle))
    {
      return false;
    }
    return true;
  }

  /**
   * @return the additionalFunding
   */
  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name="name", column = @Column(name="additionalFunding")),
    @AttributeOverride(name="startDate", column = @Column(name="additionalGrantStartDate")),
    @AttributeOverride(name="endDate", column = @Column(name="additionalGrantEndDate"))
  })
  public FundingSource getAdditionalFunding()
  {
    if(this.additionalFunding == null)
    {
      this.additionalFunding = new FundingSource();
    }
    return this.additionalFunding;
  }

  /**
   * @param additionalFunding the additionalFunding to set
   */
  public void setAdditionalFunding(FundingSource additionalFunding)
  {
    this.additionalFunding = additionalFunding;
  }
  
  @Override
  public String toString()
  {
    return this.getProjectTitle();
  }

}