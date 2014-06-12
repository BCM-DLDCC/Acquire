/**
 * 
 */
package edu.bcm.dldcc.big.acquire.exception.nalabloader;

/**
 * @author pew
 *
 */
public class LoaderFileRenameException extends NaLabLoaderException
{

  /**
   * 
   */
  public LoaderFileRenameException()
  {
    super();
  }

  /**
   * @param message
   */
  public LoaderFileRenameException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public LoaderFileRenameException(Throwable cause)
  {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public LoaderFileRenameException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public LoaderFileRenameException(String originalName, String newName)
  {
    this(LoaderFileRenameException.buildMessage(originalName, newName));
  }
  
  public LoaderFileRenameException(String originalName, String newName, 
      Throwable cause)
  {
    this(LoaderFileRenameException.buildMessage(originalName, newName), cause);
  }
  
  private static String buildMessage(String originalName, String newName)
  {
    return "Failed rename file "+ originalName +"to "+ newName;
  }

}
