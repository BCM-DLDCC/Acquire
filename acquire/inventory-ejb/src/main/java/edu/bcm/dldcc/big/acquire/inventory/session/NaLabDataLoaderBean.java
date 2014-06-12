package edu.bcm.dldcc.big.acquire.inventory.session;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.jboss.seam.mail.api.MailMessage;
import org.jboss.seam.mail.core.enumerations.ContentDisposition;

import edu.bcm.dldcc.big.acquire.exception.nalabloader.NaLabLoaderException;
import edu.bcm.dldcc.big.acquire.inventory.data.NaLabData;
import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.query.SearchManager;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.query.data.SpecimenSearchFields;
import edu.bcm.dldcc.big.clinical.data.DerivativeType;
import edu.bcm.dldcc.big.clinical.data.DnaQuality;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.NaLabAnnotation;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * NA Lab Data Loader Bean
 * 
 * @author amcowiti
 * 
 */
@Stateless
@LocalBean
public class NaLabDataLoaderBean implements Serializable
{

  private static final long serialVersionUID = 2546186508503796975L;

  @Inject
  private Event<NaLabData> nalabDataEvent;

  @Inject
  @Admin
  // non-conv sE
  private SearchManager searchEngine;

  @Inject
  private Instance<MailMessage> mail;

  // TODO produce/externalize these
  private static final String FROM =
      "Acquire HGSC Na Lab Data Loader<no-reply@breastcenter.tmc.edu>";
  private static final String toLoaderReportRecipeints =
      "tcrbsupport@breastcenter.tmc.edu";
  private static final String bcc = "amcowiti@bcm.edu";

  @Inject
  @Admin
  @Annotations
  private EntityManager em;

  @Inject
  @Named("serverName")
  private String server;

  private static final String EOL = "\n";
  private static final String SPACER = "\t";
  private static final String ROW = "Data Row";
  private static final String SDIVIDER =
      "--------------------Success Loads--------------";
  private static final String EDIVIDER =
      "--------------------Error Rows--------------";
  private static final String TOTAL = "Total number of success load = ";
  private static final String OF = " of ";

  DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
  DateFormat idformat = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
  DateFormat hiddenDformat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

  private final static BigDecimal LOWER = new BigDecimal(0.0).setScale(1);
  private final static BigDecimal UPPER = new BigDecimal(10.0).setScale(1);

  List<NaLabDataRow> rowMap;
  List<NaLabDataRow> loaded;
  int success = 0;
  StringBuilder logSb;
  StringBuilder logSbs;
  private static final int MAX_ROWS_ALLOWED = 5000;

  public static Logger log = Logger.getLogger(NaLabDataLoaderBean.class);

  @Inject
  private Event<NaLabData> naLabDataEvents;

  @PostConstruct
  public void setup()
  {

  }

  /**
   * Need make this method versatile for all types of sources
   * 
   * @return
   * @throws NaLabLoaderException
   * @throws Exception
   */
  public boolean processNaLabData(List<NaLabDataRow> rowCollection)
      throws NaLabLoaderException
  {

    logSbs =
        new StringBuilder("NA LAB Data Upload for "
            + dateformat.format(new Date())).append(EOL);
    logSb =
        new StringBuilder("The following rows had problems as shown ")
            .append(EOL);

    if (rowCollection.size() > MAX_ROWS_ALLOWED)
    {
      logSb.append(NaLabDataUploadLogMessage.TOO_MANY_ROWS).append(SPACER)
          .append(MAX_ROWS_ALLOWED).append(EOL);
      return true;
    }

    this.rowMap = rowCollection;

    Map<String, NaLabDataRow> duplicates = getDuplicateSampleIds(rowMap);
    loaded = new ArrayList<NaLabDataRow>();

    Integer row;
    NaLabData naLabData;

    List<SearchResult> result;
    SearchResult searchResult;
    Specimen specimen;
    NaLabDataElement<?> datum;

    for (NaLabDataRow data : rowMap)
    {

      row = data.getRow();

      try
      {
        if (!validId(row, data))
        {
          continue;
        }

        /*
         * Check if this is a duplicate of a previously processed derivative
         */
        if (this.searchDerivativeExisting(data))
        {
          logSb.append(ROW).append(row).append(SPACER)
              .append(NaLabDataUploadLogMessage.DERIVATIVE_EXISTS.getMessage())
              .append(EOL);
          continue;
        }

        // exists? 4now only specimen
        result = searchSpecimen(data);// searchParticipant(data);
        if (result == null || result.size() == 0)
        {
          logSb.append(ROW).append(row).append(SPACER)
              .append(NaLabDataUploadLogMessage.ID_NOT_FOUND.getMessage())
              .append(EOL);
          continue;
        }

        naLabData = new NaLabData();
        naLabData.setParentId(data.getParentId());
        naLabData.setPatientId(data.getPatientId());

        if (result.size() > 1)
        {
          log.warn("Why are there multiple specimen results  for parentId "
              + data.getParentId());
        }

        searchResult = result.get(0);
        specimen = searchResult.getSpecimen();

        // sets parent
        naLabData.setParent(specimen);

        // E is required
        datum = getAmount(row, data);
        if (!datum.valid)
        {
          continue;
        }
        naLabData.setAmount((Double) datum.data);

        // C,D,F, GHI are Annos
        setOtherAnnotations(naLabData, data);

        // I Type range
        datum = getDerivativeType(row, data);
        if (!datum.valid)
        {
          continue;
        }// !naLabData

        // G Rin range,
        // Expect Rin for RNA
        datum = getRin(row, data);
        if (!datum.valid)
        {
          if (data.getDerivativeType() == DerivativeType.RNA)
            continue;
        }// Rin !naLabData

        // H Quality range
        // Expected Quality for DNA,
        datum = getQuality(row, data);
        if (!datum.valid)
        {
          if (data.getDerivativeType() == DerivativeType.DNA)
            continue;
        }// !!naLabData

        if (!validDerivativeId(row, data))
        {
          continue;
        }

        // J UniQ
        if (duplicates.containsKey(data.getDerivativeId()))
        {
          continue;
        }

        naLabData.setNaLabLabel(data.getDerivativeId());
        naLabData.setType(data.getDerivativeType());

        data.setNaLabData(naLabData);

        // 1 save Acquire,add annos
        if (!persistNaLabAnnotation(data, searchResult))
        {
          throw new NaLabLoaderException(
              "Failed to persist and add annotation to result");
        }

        // 2 Save Catissue
        fireAcquireEvent(naLabData);
        data.setRow(row);
        loaded.add(data);
        success++;
      }
      catch (Throwable t)
      {
        logSb.append(ROW).append(row).append(SPACER)
            .append(NaLabDataUploadLogMessage.EXCEPTION_WITH_ROW.getMessage())
            .append(EOL);
        StringWriter trace = new StringWriter();
        t.printStackTrace(new PrintWriter(trace));
        logSb.append(trace);
        continue;
      }
    }

    logSuccess(rowMap.size());

    logSbs.append(logSb);
    String logfile = logSbs.toString();
    String summary = "Loaded " + success + " of total " + rowMap.size();

    File file = null;
    try
    {
      file = File.createTempFile(NaLabDataFileInfo.LOGFILE, ".log");
      writeLogFile(file, logfile);
      email(summary, file);
    }
    catch (IOException e)
    {
      throw new NaLabLoaderException(e);
    } finally
    {
      file.delete();
    }

    log.debug(logfile);

    success = 0;
    logSbs = null;
    logSb = null;
    return true;
  }

  @PreDestroy
  public void destroy()
  {
    if (logSbs != null)
      logSbs = null;
    if (logSb != null)
      logSb = null;
  }

  /**
   * This mailer assumes Mail is always send TODO remedy
   */
  private void email(String summary, File logFilefile)
  {
    long b = System.currentTimeMillis();
    MailMessage m = mail.get();
    m.from(FROM)
        .to(toLoaderReportRecipeints)
        .bcc(bcc)
        .subject(
            "HGSC NA LAB DATA LOADER Report from " + this.server + ": "
                + summary)
        .addAttachment(ContentDisposition.ATTACHMENT, logFilefile)
        .bodyText(summary).send();
    log.info("Email sent in (ms) " + (System.currentTimeMillis() - b));
  }

  /**
   * No need buffer, Raw write just as fast
   * 
   * @param datalogs
   * @return
   */
  private void writeLogFile(File file, String datalogs)
  {
    try
    {
      FileWriter writer = new FileWriter(file);
      writer.write(datalogs);
      writer.flush();
      writer.close();

    }
    catch (Exception e)
    {
      log.trace("Exception at file write" + e.getMessage());
    }

  }

  /**
   * Persist Derivative specimen to Acquire
   * 
   * @param naLabData
   * @return
   */
  private boolean persistNaLabAnnotation(NaLabDataRow data,
      SearchResult searchResult)
  {
    NaLabAnnotation naLabAnnotation = new NaLabAnnotation();
    naLabAnnotation.setRin(data.getRinBg());
    naLabAnnotation.setQuality(data.getDnaQuality());
    naLabAnnotation.setType(data.getDerivativeType());
    naLabAnnotation.setNaLabel(data.getDerivativeId());

    AliquotAnnotation aliquotAnnotation = searchResult.getAnnotation();

    aliquotAnnotation.addNaLabAnnotation(naLabAnnotation);// nu

    em.persist(naLabAnnotation);

    return true;
  }

  /**
   * Fires event for Acquire saving
   * 
   * @param naLabData
   * @return
   */
  private void fireAcquireEvent(NaLabData naLabData)
  {
    naLabDataEvents.fire(naLabData);
  }

  /**
   * Log success summary
   */
  private void logSuccess(Integer total)
  {

    logSbs.append(SDIVIDER).append(EOL).append(TOTAL).append(success)
        .append(OF).append(total).append(SPACER).append(EOL);

    for (NaLabDataRow data : loaded)
    {
      logSbs.append(ROW).append(data.getRow()).append(SPACER)
          .append(data.getParentId()).append(SPACER)
          .append(data.getDerivativeId()).append(EOL);
    }
    logSbs.append(EDIVIDER).append(EOL);
  }

  private Map<String, NaLabDataRow> getDuplicateSampleIds(
      List<NaLabDataRow> rowMap)
  {

    List<NaLabDataRow> derivatives;
    Map<String, NaLabDataRow> duplicates = new HashMap<String, NaLabDataRow>();

    Integer row;
    Map<String, List<NaLabDataRow>> naLabDataList =
        new HashMap<String, List<NaLabDataRow>>();

    for (NaLabDataRow data : rowMap)
    {
      row = data.getRow();

      derivatives =
          (!naLabDataList.containsKey(data.getParentId()))
              ? new ArrayList<NaLabDataRow>() : naLabDataList.get(data
                  .getParentId());

      derivatives.add(data);

      if (derivatives.size() > 1)
      {
        if (!validUniqueDerivativeIds(row, derivatives))
        {
          duplicates.put(data.getDerivativeId(), data);
          logSb
              .append(ROW)
              .append(row)
              .append(SPACER)
              .append(
                  NaLabDataUploadLogMessage.CHILD_LABEL_J_NOT_UNIQ.getMessage())
              .append(EOL);
          continue;
        }
      }
    }
    return duplicates;
  }

  /**
   * Other annotations;may or may not be present
   * 
   * @param naLabData
   * @param data
   */
  protected void setOtherAnnotations(NaLabData naLabData, NaLabDataRow data)
  {

    String date = data.getDateReceived();
    // C not critical
    // FIXME Excel UI shows idformat, but actual may be 2/1/2013 12:00:00 AM
    // so Guess date type
    String regex = "[a-zA-Z]";
    if (date != null && !date.equals(""))
    {
      try
      {
        Date sdate =
            (date.trim().substring(0, 1).matches(regex)) ? idformat.parse(date)
                : hiddenDformat.parse(date);
        naLabData.setDateReceived(sdate);
      }
      catch (Exception e)
      {
        log.trace("Date convertion problem " + e.getMessage());
      }
    }
    // D extracted == consumed
    String extract = data.getAmountConsumed();
    if (extract != null && !extract.equals(""))
    {
      naLabData.setAmountConsumed(new Double(extract));
    }

    // F conc
    String conc = data.getConcentration();
    if (conc != null && !conc.equals(""))
    {
      naLabData.setConcentration(new Double(conc));
    }

  }

  /**
   * Get Derivative type Apply Rule 7
   * 
   * @param row
   * @param data
   * @return
   */
  private NaLabDataElement<DerivativeType> getDerivativeType(Integer row,
      NaLabDataRow data)
  {

    // Rule 7:Acceptable values for column I are:DNA,RNA Err: skip out of range

    String type = data.getType();
    NaLabDataElement<DerivativeType> datum =
        new NaLabDataElement<DerivativeType>();

    if (type != null && !type.equals(""))
    {
      datum.data = DerivativeType.valueOf(type.toUpperCase());
      data.setDerivativeType(datum.data);
      if (datum.data == null)
      {
        logSb
            .append(ROW)
            .append(row)
            .append(SPACER)
            .append(
                NaLabDataUploadLogMessage.DERIVATIVE_TYPE_UNREGOGNIZED
                    .getMessage()).append(EOL);
        datum.valid = false;
      }
    }
    else
    {
      datum.data = null;
    }
    return datum;
  }

  /**
   * Get Quality Apply rule 6 Rule 6:Acceptable values for column H will be Not
   * degraded Partially degraded Degraded Err: Skipped out of range
   * 
   * @param row
   * @param data
   * @return
   */
  private NaLabDataElement<DnaQuality>
      getQuality(Integer row, NaLabDataRow data)
  {

    String quality = data.getQuality();
    NaLabDataElement<DnaQuality> datum = new NaLabDataElement<DnaQuality>();

    if (quality != null && !quality.equals(""))
    {
      datum.data = getDnaQuality(quality);
      data.setDnaQuality(datum.data);
      if (datum.data == null)
      {
        logSb
            .append(ROW)
            .append(row)
            .append(SPACER)
            .append(
                NaLabDataUploadLogMessage.DNA_QUALITY_NOT_RECOGNIZED
                    .getMessage()).append(EOL);
        datum.valid = false;
      }
    }
    else
    {
      datum.data = null;
      datum.valid = false;
    }
    return datum;
  }

  /**
   * Get Ids( Sample,Patient) Apply Rule 1
   * 
   * @param data
   * @return
   */
  protected boolean validId(Integer row, NaLabDataRow data)
  {
    // Rule 1 A+B must be unique in db , must exist in Db:
    if (data.getParentId() == null || data.getPatientId() == null)
    {
      logSb.append(ROW).append(row).append(SPACER)
          .append(NaLabDataUploadLogMessage.ID_MISSING.getMessage())
          .append(EOL);
      return false;
    }
    return true;
  }

  protected boolean validDerivativeId(Integer row, NaLabDataRow data)
  {
    if (data.getDerivativeId() == null
        || data.getDerivativeId().trim().equals(""))
    {
      logSb.append(ROW).append(row).append(SPACER)
          .append(NaLabDataUploadLogMessage.CHILD_LABEL_J_MISS.getMessage())
          .append(EOL);
      return false;
    }
    return true;
  }

  /**
   * Validate Sample Id uniqueness Apply Rule 2 Rule 2 There may be multiple A+B
   * rows, J uniq for A+B : skipped row, duplicate J found
   * 
   * @param derivatives
   * @param row
   * @return
   */
  protected boolean validUniqueDerivativeIds(Integer row,
      List<NaLabDataRow> derivatives)
  {

    String sampleId = derivatives.get(0).getDerivativeId();
    for (NaLabDataRow naDatum : derivatives)
    {
      if (naDatum.getDerivativeId().equals(sampleId))
      {
        logSb
            .append(ROW)
            .append(row)
            .append(SPACER)
            .append(
                NaLabDataUploadLogMessage.CHILD_LABEL_J_NOT_UNIQ.getMessage())
            .append(EOL);

        return false;
      }
    }
    return true;
  }

  /**
   * Get Rin Apply Rule 5. Rin may be empty
   * 
   * @param row
   * @param data
   * @return
   */
  protected NaLabDataElement<BigDecimal> getRin(Integer row, NaLabDataRow data)
  {
    // Rule 5:Column G is RIN values - acceptable values 0.0-10.0
    NaLabDataElement<BigDecimal> datum = new NaLabDataElement<BigDecimal>();

    if (data.getRin() != null && !data.getRin().equals(""))
      datum.data =
          new BigDecimal(data.getRin()).setScale(1, RoundingMode.HALF_UP);

    if (datum.data != null)
    {
      if (datum.data.compareTo(LOWER) == -1
          || (datum.data.compareTo(UPPER) == 1))
      {
        logSb
            .append(ROW)
            .append(row)
            .append(SPACER)
            .append(
                NaLabDataUploadLogMessage.RIN_VALUE_G_OUTTA_RANGE.getMessage())
            .append(EOL);
        datum.valid = false;
      }
      else
      {
        data.setRinBg(datum.data);
      }
    }
    else
    {
      datum.data = null;
      datum.valid = false;
    }
    return datum;
  }

  protected NaLabDataElement<Double> getAmount(Integer row, NaLabDataRow data)
  {
    // Rule Nu:Column E is required
    NaLabDataElement<Double> datum = new NaLabDataElement<Double>();

    if (data.getAmount() != null && !data.getAmount().equals(""))
      try
      {
        datum.data = new Double(data.getAmount());
      }
      catch (Exception e)
      {
        logSb.append(ROW).append(row).append(SPACER)
            .append(NaLabDataUploadLogMessage.AMOUNT_E_FORMAT.getMessage())
            .append(EOL);
        datum = null;
        datum.valid = false;
        return datum;
      }

    if (datum.data == null)
    {
      logSb.append(ROW).append(row).append(SPACER)
          .append(NaLabDataUploadLogMessage.AMOUNT_E.getMessage()).append(EOL);
      datum.valid = false;
    }

    return datum;
  }

  /**
   * Rule 1 A+B must be unique in db , must exist in Db: Row skipped, not in
   * Acquire Db
   * 
   * @param data
   * @return
   */
  protected List<SearchResult> searchParticipant(NaLabDataRow data)
  {

    return null;
  }

  /**
   * Rule 1 A+B must be unique in db , must exist in Db: Row skipped, not in
   * Acquire Db
   * 
   * @param data
   * @return
   */
  private List<SearchResult> searchSpecimen(NaLabDataRow data)
  {

    this.searchEngine.clearFields();
    this.searchEngine.setIncludeAliquots(true);
    this.searchEngine.setIncludeNormals(true);
    this.searchEngine.addSpecimenSearchField(SpecimenSearchFields.LABEL_EQUALS);
    List<String> values =
        this.searchEngine.getSpecimenFieldValues(
            SpecimenSearchFields.LABEL_EQUALS, String.class);
    values.add(data.getParentId());
    List<SearchResult> result = this.searchEngine.runQuery();
    this.searchEngine.setIncludeAliquots(false);
    this.searchEngine.setIncludeNormals(false);
    return result;
  }

  private boolean searchDerivativeExisting(NaLabDataRow data)
  {
    this.searchEngine.clearFields();
    this.searchEngine.setIncludeAliquots(true);
    this.searchEngine.setIncludeNormals(true);
    this.searchEngine.addSpecimenSearchField(SpecimenSearchFields.LABEL_EQUALS);
    List<String> values =
        this.searchEngine.getSpecimenFieldValues(
            SpecimenSearchFields.LABEL_EQUALS, String.class);
    values.add(data.getDerivativeId());
    List<SearchResult> result = this.searchEngine.runQuery();
    this.searchEngine.setIncludeAliquots(false);
    this.searchEngine.setIncludeNormals(false);

    return !result.isEmpty();
  }

  private DnaQuality getDnaQuality(String quality)
  {

    if (quality == null)
    {
      return null;
    }
    for (DnaQuality q : DnaQuality.values())
    {
      if (q.toString().equalsIgnoreCase(quality.trim()))
      {
        return q;
      }
    }
    return null;

  }

}
