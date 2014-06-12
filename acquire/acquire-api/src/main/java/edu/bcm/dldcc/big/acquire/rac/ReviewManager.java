/**
 * 
 */
package edu.bcm.dldcc.big.acquire.rac;

import javax.ejb.Local;

import org.primefaces.model.DualListModel;

import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.rac.entity.Application;

/**
 * Manager for assigning reviewers to an application.
 * 
 * @author pew
 *
 */
@Local
public interface ReviewManager
{
  /**
   * Set the DualListModel to hold the reviewers
   * @param model
   */
  void setReviewerModel(DualListModel<AcquireUserInformation> model);
  
  /**
   * get the DualListModel that holds the reviewers
   * @return
   */
  DualListModel<AcquireUserInformation> getReviewerModel();
  
  /**
   * Initialize the list of reviewers with information from the current
   * application
   */
  void initList(Application application);

  /**
   * This method will process the reviewers assigned to the current application,
   * validating them and notifying them.
   */
  void assignReviewers();
  

}
