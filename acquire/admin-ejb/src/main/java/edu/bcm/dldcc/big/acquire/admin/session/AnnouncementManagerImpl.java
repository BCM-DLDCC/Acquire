/**
 * 
 */
package edu.bcm.dldcc.big.acquire.admin.session;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;

import edu.bcm.dldcc.big.acquire.admin.AnnouncementManager;
import edu.bcm.dldcc.big.acquire.admin.PermissionManager;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.Announcements;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminDatabase;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.admin.entity.Announcement;
import edu.bcm.dldcc.big.admin.entity.Announcement_;

/**
 * @author pew
 * 
 */
@ConversationScoped
@Named("announcementManager")
@Stateful
public class AnnouncementManagerImpl implements AnnouncementManager, 
Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -4457826544954747758L;

  private String text;
  
  private Date expiration;

  @Inject
  @AdminDatabase
  @Operations
  private EntityManager em;

  @Inject
  private PermissionManager pm;

  @Inject
  private Identity identity;

  @Inject
  @Named("currentProgram")
  private String program;
  
  @Inject
  private Messages messages;
  
  private static final String PARAM_NAME="program";

  /**
   * 
   */
  public AnnouncementManagerImpl()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.AnnouncementManager#setText(java.lang.String
   * )
   */
  @Override
  public void setText(String value)
  {
    this.text = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.AnnouncementManager#getText()
   */
  @Override
  public String getText()
  {
    return this.text;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.AnnouncementManager#getAnnouncements()
   */
  @Override
  @Produces
  @Named("announcements")
  public List<Announcement> getAnnouncements()
  {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    /*Parameter<String> programParam = cb.parameter(String.class, 
        AnnouncementManagerImpl.PARAM_NAME);*/
    CriteriaQuery<Announcement> criteria = cb.createQuery(
        Announcement.class);
    Root<Announcement> root = criteria.from(Announcement.class);
    criteria.select(root);
    criteria.where(cb.and(cb.equal(root.get(Announcement_.program), program),
        cb.or(cb.isNull(root.get(Announcement_.expireDate)), 
            cb.greaterThanOrEqualTo(root.get(Announcement_.expireDate), 
            cb.currentTimestamp()))));
    TypedQuery<Announcement> query = em.createQuery(criteria);
    //query.setParameter(programParam, program);
    return query.getResultList();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.AnnouncementManager#removeAnnouncement(
   * edu.bcm.dldcc.big.admin.entity.Announcement)
   */
  @Override
  @Announcements
  public void removeAnnouncement(Announcement remove)
  {
    em.remove(remove);
    //set a success message - TODO generalize this
    messages.info("The announcement was removed successfully.");  
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.AnnouncementManager#createAnnouncement()
   */
  @Override
  @Announcements
  public void createAnnouncement()
  {
    Announcement announce = new Announcement();
    announce.setCreateDate(new Date());
    announce.setProgram(pm.getUserCurrentProgram());
    announce.setUserName(identity.getUser().getId());
    announce.setText(this.getText());
    announce.setExpireDate(this.getExpiration());
    em.persist(announce);
    this.setExpiration(null);
    this.setText("");
    
    //set a success message - TODO generalize this
    messages.info("The announcement was created successfully.");
  
  }

  /**
   * @return the expiration
   */
  public Date getExpiration()
  {
    return this.expiration;
  }

  /**
   * @param expiration the expiration to set
   */
  public void setExpiration(Date expiration)
  {
    this.expiration = expiration;
  }

}
