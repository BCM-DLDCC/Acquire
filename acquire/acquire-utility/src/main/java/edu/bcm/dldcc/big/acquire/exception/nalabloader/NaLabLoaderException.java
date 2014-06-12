/**
 * 
 */
package edu.bcm.dldcc.big.acquire.exception.nalabloader;

import edu.bcm.dldcc.big.acquire.exception.AcquireException;

/**
 * @author pew
 *
 */
public class NaLabLoaderException extends AcquireException
{

  /**
   * 
   */
  public NaLabLoaderException()
  {
    super();
  }

  /**
   * @param message
   */
  public NaLabLoaderException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public NaLabLoaderException(Throwable cause)
  {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public NaLabLoaderException(String message, Throwable cause)
  {
    super(message, cause);
  }

}
