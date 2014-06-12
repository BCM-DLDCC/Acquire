package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.hibernate.envers.Audited;

/**
 * @author Benjamin Pew
 * @version 1.0
 * @created 28-Jun-2013 8:34:06 AM
 */
@Embeddable
public class FileEntry implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -6761704339902939334L;
  private String fileId;
  private String fileName;

  public FileEntry()
  {
    super();
  }

  public void finalize() throws Throwable
  {

  }

  /**
   * @return the fileId
   */
  public String getFileId()
  {
    return this.fileId;
  }

  /**
   * @param fileId the fileId to set
   */
  public void setFileId(String fileId)
  {
    this.fileId = fileId;
  }

  /**
   * @return the fileName
   */
  public String getFileName()
  {
    return this.fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }
  
  @Override
  public String toString()
  {
    return this.getFileName();
  }

}