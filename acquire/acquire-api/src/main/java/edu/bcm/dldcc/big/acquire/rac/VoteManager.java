/**
 * 
 */
package edu.bcm.dldcc.big.acquire.rac;

import java.util.Date;

import javax.ejb.Local;

import edu.bcm.dldcc.big.acquire.exception.rac.ReviewerNotAssignedToApplicationException;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.rac.entity.Application;
import edu.bcm.dldcc.big.security.exception.UserNotFoundException;

/**
 * @author pew
 *
 */
@Local
public interface VoteManager
{
  void setReviewer(String username) throws UserNotFoundException;
  String getReviewer();
  void setApplicationId(Long id);
  Long getApplicationId();
  void setExpiration(Date expire);
  Date getExpiration();
  void setVote(boolean vote);
  boolean getVote();
  void processVote() throws ReviewerNotAssignedToApplicationException;
  void cancelVote() throws ReviewerNotAssignedToApplicationException;
  AcquireUserInformation getReviewerInfo();
  Application getApplication();
}
