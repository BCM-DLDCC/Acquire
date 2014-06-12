package edu.bcm.dldcc.big.acquire.inventory.session;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.jboss.seam.mail.api.MailMessage;

import edu.bcm.dldcc.big.acquire.event.MailExceptionEvent;
import edu.bcm.dldcc.big.acquire.event.NaLabReportEvent;
import edu.bcm.dldcc.big.acquire.exception.nalabloader.LoaderFileRenameException;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLab;

/**
 * Timer Bean to upload NA LAB Data TODO externalize setting
 * 
 * @author amcowiti
 * 
 */

@Singleton
@Startup
public class NaLabDataUploadTimerBean
{

  @Inject
  private NaLabDataLoaderBean naLabDataLoaderBean;

  @Inject
  NaLabDataSource naLabDataSource;

  @Inject
  private Instance<MailMessage> mail;

  @Inject
  private Event<MailExceptionEvent> mailEvent;

  @Inject
  private Event<NaLabReportEvent> event;
  
  @Inject
  private Session session;
  
  @Inject
  @NaLab
  private MessageProducer messageProducer;

  DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy-HH-mm");

  private Date lastAutomaticTimeout;

  public static Logger logger = Logger.getLogger(NaLabDataUploadTimerBean.class
      .getName());

  /**
   * Will do every n day @4am morning? (every 15 minutes of that hour) Config
   * /Here / in ejb-jar.xml / bean.xml? !FR Provide admin UI to change it if
   * want programmatic
   */
  @Schedule(minute = "*/15", hour = "*", persistent = false)
  // @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public void automaticTimeout()
  {
    // TODO 4now uses Excel. Make several sources possible
    this.setLastAutomaticTimeout(new Date());
    logger.info("HGSC NaLabData Loader procesing timeout @" + new Date());

    long b = System.currentTimeMillis();

    try
    {
      List<NaLabDataRow> rows = naLabDataSource.getNaLabDataRows();
      if (rows == null)
        return;

      naLabDataLoaderBean.processNaLabData(rows);

      markProcessed();

      //event.fire(new NaLabReportEvent());
      this.messageProducer.send(this.session.createMessage());

    }
    catch (Exception e)
    {//NaLabLoaderException e, we need capture all exceptions
      logger.log(Level.SEVERE,
          "Error processing HGSC upload file " + e.getMessage());
      try
      {
        temporarySuspendFile();
      }
      catch (LoaderFileRenameException e1)
      {
        logger.log(Level.SEVERE,
            "Error renaming pending HGSC upload file " + e.getMessage());
      } finally
      {
        this.mailEvent.fire(new MailExceptionEvent(
            "An error has occurred processing the NA Lab Data Upload on ", e));
      }
    }

    logger.info("HGSC NaLabData Loader processing done @" + new Date()
        + " in time(ms)" + (System.currentTimeMillis() - b));
  }

  private void markProcessed() throws LoaderFileRenameException
  {

    String newName = NaLabDataFileInfo.importFile
        + NaLabDataFileInfo.RENAME_POSTFIX + dateformat.format(new Date());

    renameFile(newName);
  }

  /**
   * @param newName
   * @throws LoaderFileRenameException
   */
  private void renameFile(String newName) throws LoaderFileRenameException
  {
    try
    {
      if (!(new File(NaLabDataFileInfo.importFile)).renameTo(new File(newName)))
      {
        throw new LoaderFileRenameException(NaLabDataFileInfo.importFile,
            newName);
      }
    }
    catch (SecurityException e)
    {
      throw new LoaderFileRenameException(NaLabDataFileInfo.importFile,
          newName, e);
    }
  }

  private void temporarySuspendFile() throws LoaderFileRenameException
  {
    String newName = NaLabDataFileInfo.importFile
        + NaLabDataFileInfo.RENAME_PENING + dateformat.format(new Date());

    renameFile(newName);
  }

  /**
   * @return the lastAutomaticTimeout
   */
  public String getLastAutomaticTimeout()
  {
    if (lastAutomaticTimeout != null)
    {
      return lastAutomaticTimeout.toString();
    }
    else
    {
      return "never";
    }
  }

  /**
   * @param lastAutomaticTimeout
   *          the lastAutomaticTimeout to set
   */
  public void setLastAutomaticTimeout(Date lastAutomaticTimeout)
  {
    this.lastAutomaticTimeout = lastAutomaticTimeout;
  }
}
