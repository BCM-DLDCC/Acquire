/**
 * 
 */
package edu.bcm.dldcc.big.acquire.exception;

/**
 * An exception that alerts the system that too many samples have 
 * been added to a shipment form.
 * 
 * @author pew
 *
 */
public class TooManySamplesException extends AcquireException
{

  /**
   * 
   */
  private static final long serialVersionUID = -2452068620899873820L;

  /**
   * 
   */
  public TooManySamplesException()
  {
    super();
  }

  /**
   * @param message
   */
  public TooManySamplesException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public TooManySamplesException(Throwable cause)
  {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public TooManySamplesException(String message, Throwable cause)
  {
    super(message, cause);
  }

}
