/**
 * 
 */
package edu.bcm.dldcc.big.acquire.rac;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.RequestScoped;

/**
 * @author pew
 *
 */
@RequestScoped
public class RacEmailTemplateProvider
{
  private String submitRacEmail = "/templates/applicationSubmitRacCoordinatorEmail";

  private String applicationSaveEmail = "/templates/applicationSaveEmailTemplate";

  private String applictionSubmittedEmail = "/templates/applicationSubmittedTemplate";

  private String reviewerNotifyEmail = "/templates/reviewerNotifyTemplate";

  private String commentNotifyEmail = "/templates/commentNotifyEmail";

  private String postSubmitSaveEmail = "/templates/postSubmitSaveTemplate";

  /**
   * 
   */
  public RacEmailTemplateProvider()
  {
    super();
  }
  
  public InputStream getCoordinatorSubmitEmail()
  {
    return this.openTemplateFile(this.submitRacEmail);
  }
  
  public InputStream getApplicationSaveEmail()
  {
    return this.openTemplateFile(this.applicationSaveEmail);
  }
  
  public InputStream getUserSubmitEmail()
  {
    return this.openTemplateFile(this.applictionSubmittedEmail);
  }
  
  public InputStream getReviewerNotifyEmail()
  {
    return this.openTemplateFile(this.reviewerNotifyEmail);
  }
  
  public InputStream getCommentNotifyEmail()
  {
    return this.openTemplateFile(this.commentNotifyEmail);
  }
  
  public InputStream getPostSubmitSaveEmail()
  {
    return this.openTemplateFile(this.postSubmitSaveEmail);
  }
  
  private InputStream openTemplateFile(String path)
  {
    return this.getClass().getResourceAsStream(path);
  }
  
  private void closeTemplateFile(InputStream template)
  {
    try
    {
      template.close();
    }
    catch(IOException e)
    {
      //Can't close stream, do nothing.
    }
  }

}
