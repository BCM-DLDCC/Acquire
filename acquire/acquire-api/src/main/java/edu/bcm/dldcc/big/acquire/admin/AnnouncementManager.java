/**
 * 
 */
package edu.bcm.dldcc.big.acquire.admin;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import edu.bcm.dldcc.big.admin.entity.Announcement;

/**
 * This interfaces specifies methods used to create, view, and manage
 * Acquire program announcements. It can be accessed using the EL name
 * announcementManager
 * 
 * @author pew
 *
 */
@Local
public interface AnnouncementManager
{
  /**
   * Setter for the text of an announcement
   * 
   * @param value to be used as the content of the announcement
   */
  void setText(String value);
  
  /**
   * Getter for the text of the current announcement
   * 
   * @return the text specified as the content of the announcement
   */
  String getText();
  
  /**
   * Setter for providing an expiration date for the announcement
   * @param date
   */
  void setExpiration(Date date);
  
  /**
   * Getter for the expiration date for the current announcement
   * @return the expiration date
   */
  Date getExpiration();
  
  /**
   * Retrieves a List of all announcements currently in the system 
   * for the current program
   *  
   * @return the List&lt;Announcement&gt; of announcements for the current 
   * program
   */
  List<Announcement> getAnnouncements();
  
  /**
   * Removes the announcement from the system.
   * 
   * @param remove the Announcement to be removed
   */
  void removeAnnouncement(Announcement remove);
  
  /**
   * Creates a new Announcement for the current program
   */
  void createAnnouncement();
}
