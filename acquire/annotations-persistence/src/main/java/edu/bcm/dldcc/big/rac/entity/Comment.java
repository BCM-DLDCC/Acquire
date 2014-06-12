package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 8:34:06 AM
 */
@Entity
@Audited
@Table(name="APPLICATIONCOMMENT")
public class Comment implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 8570963932804252184L;
  private Long id;
  private Integer version;
  private List<Comment> children = new ArrayList<Comment>();
  private Comment parent;
  private String text;
  private Date timestamp;
  private AcquireUserInformation user;
  private Application application;

  public Comment()
  {
    super();
  }

  /**
   * @return the id
   */
  @Id
  @GeneratedValue(generator="commentGenerator")
  @SequenceGenerator(name="commentGenerator", sequenceName="COMMENT_SEQ")
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
   * @return the children
   */
  @OneToMany(mappedBy="parent", cascade={CascadeType.ALL},
      orphanRemoval=true)
  @OrderBy("timestamp")
  public List<Comment> getChildren()
  {
    return this.children;
  }

  /**
   * @param children the children to set
   */
  public void setChildren(List<Comment> children)
  {
    this.children = children;
  }
  
  public void addChild(Comment child)
  {
    child.setParent(this);
    this.getChildren().add(child);
  }

  /**
   * @return the parent
   */
  @ManyToOne
  public Comment getParent()
  {
    return this.parent;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(Comment parent)
  {
    this.parent = parent;
  }

  /**
   * @return the text
   */
  @Lob
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
   * @return the timestamp
   */
  @Temporal(TemporalType.TIMESTAMP)
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
   * @return the userId
   */
  @ManyToOne
  public AcquireUserInformation getUser()
  {
    return this.user;
  }

  /**
   * @param userId the userId to set
   */
  public void setUser(AcquireUserInformation userId)
  {
    this.user = userId;
  }

  /**
   * @return the application
   */
  @ManyToOne
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
  
  /*
   * This method will remove comment objects that do not have text. 
   */
  @PrePersist
  @PreUpdate
  public void removeEmptyComments()
  {
    List<Comment> empty = new ArrayList<Comment>();
    for(Comment current : this.getChildren())
    {
      current.removeEmptyComments();
      if(current.getText() == null || current.getText().isEmpty())
      {
        empty.add(current);
      }
    }
    
    this.getChildren().removeAll(empty);
  }

}