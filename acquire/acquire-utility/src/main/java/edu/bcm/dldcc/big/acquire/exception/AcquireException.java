/**
 * 
 */
package edu.bcm.dldcc.big.acquire.exception;

/**
 * A base Exception to provide a common root for all exceptions in 
 * Acquire.
 * @author pew
 *
 */
public class AcquireException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = -6383310704040893543L;

  /**
   * 
   */
  public AcquireException()
  {
    super();
  }

  /**
   * @param message
   */
  public AcquireException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public AcquireException(Throwable cause)
  {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public AcquireException(String message, Throwable cause)
  {
    super(message, cause);
  }

}
