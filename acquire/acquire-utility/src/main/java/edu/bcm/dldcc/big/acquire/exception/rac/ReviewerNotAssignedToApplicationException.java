/**
 * 
 */
package edu.bcm.dldcc.big.acquire.exception.rac;

import edu.bcm.dldcc.big.acquire.exception.AcquireException;

/**
 * This exception indicates that an operation was attempted for a user
 * not assigned to the application as a reviewer
 * @author pew
 *
 */
public class ReviewerNotAssignedToApplicationException extends AcquireException
{

  /**
   * 
   */
  public ReviewerNotAssignedToApplicationException()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   */
  public ReviewerNotAssignedToApplicationException(String message)
  {
    super(message);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param cause
   */
  public ReviewerNotAssignedToApplicationException(Throwable cause)
  {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   */
  public ReviewerNotAssignedToApplicationException(String message,
      Throwable cause)
  {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

}
