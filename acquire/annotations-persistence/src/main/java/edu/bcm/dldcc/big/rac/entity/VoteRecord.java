/**
 * 
 */
package edu.bcm.dldcc.big.rac.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.rac.data.Vote;

/**
 * @author pew
 *
 */
@Embeddable
public class VoteRecord implements Serializable
{
  private Vote vote;
  private AcquireUserInformation recordingUser;
  
  /**
   * 
   */
  public VoteRecord()
  {
    super();
  }
  
  /**
   * Constructor that sets the vote
   * 
   * @param vote
   */
  public VoteRecord(Vote vote)
  {
    this.setVote(vote);
  }

  /**
   * @return the vote
   */
  @Enumerated(EnumType.STRING)
  public Vote getVote()
  {
    return this.vote;
  }

  /**
   * @param vote the vote to set
   */
  public void setVote(Vote vote)
  {
    this.vote = vote;
  }

  /**
   * @return the recordingUser
   */
  @ManyToOne
  public AcquireUserInformation getRecordingUser()
  {
    return this.recordingUser;
  }

  /**
   * @param recordingUser the recordingUser to set
   */
  public void setRecordingUser(AcquireUserInformation recordingUser)
  {
    this.recordingUser = recordingUser;
  }

}
