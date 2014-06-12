/**
 * 
 */
package edu.bcm.dldcc.big.acquire.exception.rac;

import edu.bcm.dldcc.big.acquire.exception.AcquireException;

/**
 * Exception indicating that a comment cannot be deleted.
 * @author pew
 *
 */
public class DeleteCommentException extends AcquireException
{

  /**
   * 
   */
  public DeleteCommentException()
  {
    super();
  }

  /**
   * @param message
   */
  public DeleteCommentException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public DeleteCommentException(Throwable cause)
  {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public DeleteCommentException(String message, Throwable cause)
  {
    super(message, cause);
  }

}
