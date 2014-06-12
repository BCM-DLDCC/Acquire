/**
 * 
 */
package edu.bcm.dldcc.big.acquire.event;

import java.io.Serializable;

/**
 * This class is an Event to encapsulate information needed to send an
 * email notifying the support team of an issue.
 * 
 * @author pew
 *
 */
public class MailExceptionEvent implements Serializable
{
  
  /**
   * 
   */
  private static final long serialVersionUID = 6945792766973063182L;
  /**
   * 
   */
  private String header;
  private Throwable cause;

  /**
   * 
   */
  public MailExceptionEvent()
  {
    super();
  }
  
  /**
   * Constructor to fully initialize the event.
   * 
   * @param header String to be used as a way of distinguishing the general
   * operation being performed when the exception occurred
   * @param cause The exception that led to the event.
   */
  public MailExceptionEvent(String header, Throwable cause)
  {
    this.header = header;
    this.cause = cause;
  }

  /**
   * @return the header
   */
  public String getHeader()
  {
    return this.header;
  }

  /**
   * @param header the header to set
   */
  public void setHeader(String header)
  {
    this.header = header;
  }

  /**
   * @return the cause
   */
  public Throwable getCause()
  {
    return this.cause;
  }

  /**
   * @param cause the cause to set
   */
  public void setCause(Throwable cause)
  {
    this.cause = cause;
  }

}
