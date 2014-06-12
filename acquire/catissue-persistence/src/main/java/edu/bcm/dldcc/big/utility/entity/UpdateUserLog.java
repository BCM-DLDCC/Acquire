package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import edu.wustl.catissuecore.domain.User;

/**
 * Entity implementation class for Entity: UpdateUserLog
 * 
 */
@Entity
public class UpdateUserLog implements Serializable, CaTissueLog
{

  /**
   * 
   */
  private static final long serialVersionUID = -3755581304287949942L;
  private Long id;
  private Integer version;
  private User user;

  public UpdateUserLog()
  {
    super();
  }

  @Id
  @GeneratedValue(generator = "newUserGenerator")
  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  @Version
  public Integer getVersion()
  {
    return this.version;
  }

  public void setVersion(Integer version)
  {
    this.version = version;
  }

  /**
   * @return the participant
   */
  @OneToOne
  @JoinColumn(name="USER_ID")
  public User getUser()
  {
    return this.user;
  }

  /**
   * @param site
   *          the participant to set
   */
  public void setUser(User user)
  {
    this.user = user;
  }

  /* (non-Javadoc)
   * @see edu.bcm.dldcc.big.utility.entity.CaTissueLog#getCorrelationId()
   */
  @Override
  @Transient
  public String getCorrelationId()
  {
    return this.getUser().getLoginName();
  }

}
