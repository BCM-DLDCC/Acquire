package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.rac.data.ApplicationStatus;
import edu.bcm.dldcc.big.rac.data.Vote;
import edu.bcm.dldcc.big.utility.entity.FileEntry;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 8:34:06 AM
 */
@Entity
@Audited
public class Application implements Serializable
{

  private Long id;
  private Integer version;
  private ApplicationStatus applicationStatus = ApplicationStatus.DRAFT;
  private InvestigatorInfo investigator = new InvestigatorInfo();
  private ProjectInformation project = new ProjectInformation();
  private MaterialRequestInformation materialRequests =
      new MaterialRequestInformation();
  private Certification certification = new Certification();
  private Set<FileEntry> supportingDocuments = new HashSet<FileEntry>();
  private Set<FileEntry> reviewDocuments = new HashSet<FileEntry>();
  private Map<AcquireUserInformation, VoteRecord> reviewers =
      new HashMap<AcquireUserInformation, VoteRecord>();
  private List<Comment> comments = new ArrayList<Comment>();
  private AcquireUserInformation owningUser;
  private Date submissionDate;

  public Application()
  {
    super();
    this.investigator.setApplication(this);
    this.project.setApplication(this);
    this.materialRequests.setApplication(this);
    this.certification.setApplication(this);
  }

  /**
   * @return the id
   */
  @Id
  @GeneratedValue(generator = "applicationGenerator")
  @SequenceGenerator(name = "applicationGenerator",
      sequenceName = "APPLICATION_SEQ")
  public Long getId()
  {
    return this.id;
  }

  /**
   * @param id
   *          the id to set
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
   * @param version
   *          the version to set
   */
  public void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * @return the applicationStatus
   */
  @Enumerated(EnumType.STRING)
  public ApplicationStatus getApplicationStatus()
  {
    return this.applicationStatus;
  }

  /**
   * @param applicationStatus
   *          the applicationStatus to set
   */
  public void setApplicationStatus(ApplicationStatus applicationStatus)
  {
    this.applicationStatus = applicationStatus;
  }

  /**
   * @return the investigator
   */
  @OneToOne(mappedBy = "application", cascade =
  { CascadeType.ALL })
  public InvestigatorInfo getInvestigator()
  {
    return this.investigator;
  }

  /**
   * @param investigator
   *          the investigator to set
   */
  public void setInvestigator(InvestigatorInfo investigator)
  {
    this.investigator = investigator;
    investigator.setApplication(this);
  }

  /**
   * @return the project
   */
  @OneToOne(mappedBy = "application", cascade =
  { CascadeType.ALL })
  public ProjectInformation getProject()
  {
    return this.project;
  }

  /**
   * @param project
   *          the project to set
   */
  public void setProject(ProjectInformation project)
  {
    this.project = project;
    project.setApplication(this);
  }

  /**
   * @return the materialRequests
   */
  @OneToOne(mappedBy = "application", cascade =
  { CascadeType.ALL })
  public MaterialRequestInformation getMaterialRequests()
  {
    return this.materialRequests;
  }

  /**
   * @param materialRequests
   *          the materialRequests to set
   */
  public void setMaterialRequests(MaterialRequestInformation materialRequests)
  {
    this.materialRequests = materialRequests;
    materialRequests.setApplication(this);
  }

  /**
   * @return the certification
   */
  @OneToOne(mappedBy = "application", cascade =
  { CascadeType.ALL })
  public Certification getCertification()
  {
    return this.certification;
  }

  /**
   * @param certification
   *          the certification to set
   */
  public void setCertification(Certification certification)
  {
    this.certification = certification;
    certification.setApplication(this);
  }

  /**
   * @return the supportingDocuments
   */
  @ElementCollection
  @Embedded
  @JoinTable(name = "SUPPORTINGDOCUMENTS")
  public Set<FileEntry> getSupportingDocuments()
  {
    return this.supportingDocuments;
  }

  /**
   * @param supportingDocuments
   *          the supportingDocuments to set
   */
  public void setSupportingDocuments(Set<FileEntry> supportingDocuments)
  {
    this.supportingDocuments = supportingDocuments;
  }

  /**
   * @return the reviewDocuments
   */
  @ElementCollection
  @Embedded
  @JoinTable(name = "REVIEWDOCUMENTS")
  public Set<FileEntry> getReviewDocuments()
  {
    return this.reviewDocuments;
  }

  /**
   * @param reviewDocuments
   *          the reviewDocuments to set
   */
  public void setReviewDocuments(Set<FileEntry> reviewDocuments)
  {
    this.reviewDocuments = reviewDocuments;
  }

  /**
   * @return the reviewers
   */
  @ElementCollection
  @Embedded
  public Map<AcquireUserInformation, VoteRecord> getReviewers()
  {
    return this.reviewers;
  }

  /**
   * @param reviewers
   *          the reviewers to set
   */
  public void setReviewers(Map<AcquireUserInformation, VoteRecord> reviewers)
  {
    this.reviewers = reviewers;
  }

  @Transient
  public List<AcquireUserInformation> getCurrentReviewers()
  {
    return new ArrayList<AcquireUserInformation>(this.getReviewers().keySet());
  }

  /**
   * @return the comments
   */
  @OneToMany(mappedBy = "application", cascade =
  { CascadeType.ALL }, orphanRemoval = true)
  @OrderBy("timestamp")
  public List<Comment> getComments()
  {
    return this.comments;
  }

  public void addComment(Comment add)
  {
    add.setApplication(this);
    this.getComments().add(add);
  }

  /**
   * @param comments
   *          the comments to set
   */
  public void setComments(List<Comment> comments)
  {
    this.comments = comments;
  }

  /**
   * @return the userId
   */
  @ManyToOne
  public AcquireUserInformation getOwningUser()
  {
    return this.owningUser;
  }

  /**
   * @param userId
   *          the userId to set
   */
  public void setOwningUser(AcquireUserInformation user)
  {
    this.owningUser = user;
  }

  /**
   * @return the submissionDate
   */
  @Temporal(TemporalType.TIMESTAMP)
  public Date getSubmissionDate()
  {
    return this.submissionDate;
  }

  /**
   * @param submissionDate
   *          the submissionDate to set
   */
  public void setSubmissionDate(Date submissionDate)
  {
    this.submissionDate = submissionDate;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
        prime * result
            + ((this.investigator == null) ? 0 : this.investigator.hashCode());
    result =
        prime * result + ((this.project == null) ? 0 : this.project.hashCode());
    result =
        prime
            * result
            + ((this.submissionDate == null) ? 0 : this.submissionDate
                .hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
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
    Application other = (Application) obj;
    if (this.investigator == null)
    {
      if (other.investigator != null)
      {
        return false;
      }
    }
    else if (!this.investigator.equals(other.investigator))
    {
      return false;
    }
    if (this.project == null)
    {
      if (other.project != null)
      {
        return false;
      }
    }
    else if (!this.project.equals(other.project))
    {
      return false;
    }
    if (this.submissionDate == null)
    {
      if (other.submissionDate != null)
      {
        return false;
      }
    }
    else if (!this.submissionDate.equals(other.submissionDate))
    {
      return false;
    }
    return true;
  }

  /*
   * This method will remove comment objects that do not have text. It only
   * handles top-level comments.
   */
  @PrePersist
  @PreUpdate
  public void removeEmptyComments()
  {
    List<Comment> empty = new ArrayList<Comment>();
    for (Comment current : this.getComments())
    {
      current.removeEmptyComments();
      if (current.getText() == null || current.getText().isEmpty())
      {
        empty.add(current);
      }
    }

    this.getComments().removeAll(empty);
  }

  @Override
  public String toString()
  {
    return this.getInvestigator() != null ? this.getInvestigator().toString()
        : "No Investigator " + this.getProject() != null ? this.getProject()
            .toString() : "No Title";
  }

}