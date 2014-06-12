/**
 * 
 */
package edu.bcm.dldcc.big.acquire.annotations.file;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.TransactionAttribute;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.filesystem.api.FileTransfer;
import edu.bcm.dldcc.big.filesystem.api.FilesystemManager;
import edu.bcm.dldcc.big.utility.entity.FileEntry;

/**
 * @author pew
 * 
 */
@Named("files")
@ConversationScoped
public class FileHandler implements Serializable
{

  @Inject
  private FilesystemManager fm;

  private String filename;

  private SearchResult record;

  private String updateComponentId;
  
  private FileEntry fileEntry;

  @Inject
  private Messages messages;

  @Inject
  private Event<SearchResult> fileEvent;

  /**
   * 
   */
  public FileHandler()
  {
    super();
  }

  private String fileUpload(FileUploadEvent event)
  {
    UploadedFile file = event.getFile();
    String filename = "";
    try
    {
      filename = fm.storeFile(file.getInputstream(), file.getFileName());
      messages.info("File successfully uploaded");

    }
    catch (IOException e)
    {
      messages.error("Failed", file.getFileName() + " failed to upload");
    } finally
    {
      try
      {
        file.getInputstream().close();
      }
      catch (IOException e)
      {
        // Ignore if close fails
      }
    }

    return filename;
  }

  @TransactionAttribute
  public void uploadPathReport(FileUploadEvent event)
  {
    if (event.getFile() != null)
    {
      record.setPathReport(event.getFile().getFileName());
      record.setPathReportId(this.fileUpload(event));
      fileEvent.fire(record);
    }
  }

  @TransactionAttribute
  public void uploadPathImage(FileUploadEvent event)
  {
    if (event.getFile() != null)
    {
      record.setPathImage(event.getFile().getFileName());
      record.setPathImageId(this.fileUpload(event));
      fileEvent.fire(record);
    }
  }
  
  public FileEntry uploadFile(FileUploadEvent event)
  {
    FileEntry entry = null;
    if (event.getFile() != null)
    {
      entry = new FileEntry();
      entry.setFileName(event.getFile().getFileName());
      entry.setFileId(this.fileUpload(event));
    }
    return entry;
  }

  private StreamedContent getFile(String filename)
  {
    FileTransfer file = fm.retrieveFile(filename);
    return new DefaultStreamedContent(file.getInputStream(),
        file.getContentType(), file.getFilename());
  }

  public StreamedContent getPathReport()
  {
    return this.getFile(record.getPathReportId());
  }

  public StreamedContent getPathImage()
  {
    return this.getFile(record.getPathImageId());
  }
  
  public StreamedContent downloadFile()
  {
    return this.getFile(this.getFileEntry().getFileId());
  }
  
  public StreamedContent downloadFile(FileEntry entry)
  {
    return this.getFile(entry.getFileId());
  }

  private void removeFile(String filename)
  {
    fm.removeFile(filename);
  }

  public void removePathReport()
  {
    this.removeFile(record.getPathReportId());
    record.setPathReport("");
    record.setPathReportId("");
    fileEvent.fire(record);
  }
  
  public void removeFile()
  {
    this.removeFile(this.getFileEntry().getFileId());
    this.getFileEntry().setFileId("");
    this.getFileEntry().setFileName("");
  }
  
  public void removeFile(FileEntry file)
  {
    this.removeFile(file.getFileId());
    file.setFileId("");
    file.setFileName("");
  }

  public void removePathImage()
  {
    this.removeFile(record.getPathImageId());
    record.setPathImage("");
    record.setPathImageId("");
    fileEvent.fire(record);
  }

  /**
   * @return the filename
   */
  public String getFilename()
  {
    return this.filename;
  }

  /**
   * @param filename
   *          the filename to set
   */
  public void setFilename(String filename)
  {
    this.filename = filename;
  }

  /**
   * @return the record
   */
  public SearchResult getRecord()
  {
    return this.record;
  }

  /**
   * @param record
   *          the record to set
   */
  public void setRecord(SearchResult record)
  {
    this.record = record;
  }

  public void rowToggle(ToggleEvent event)
  {
    this.setRecord((SearchResult) event.getData());
    this.messages.info("row toggled");
  }

  /**
   * @return the updateComponentId
   */
  public String getUpdateComponentId()
  {
    return this.updateComponentId;
  }

  /**
   * @param updateComponentId
   *          the updateComponentId to set
   */
  public void setUpdateComponentId(String updateComponentId)
  {
    this.updateComponentId = updateComponentId;
  }

  /**
   * @return the file
   */
  public FileEntry getFileEntry()
  {
    return this.fileEntry;
  }

  /**
   * @param file the file to set
   */
  public void setFileEntry(FileEntry file)
  {
    this.fileEntry = file;
  }

}
