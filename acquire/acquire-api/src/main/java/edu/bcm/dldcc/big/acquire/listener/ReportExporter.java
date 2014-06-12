package edu.bcm.dldcc.big.acquire.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.jboss.logging.Logger;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import edu.bcm.dldcc.big.acquire.inventory.ReportManager;
import edu.bcm.dldcc.big.acquire.query.Reports;
import edu.bcm.dldcc.big.acquire.query.data.NaLabReportData;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;

/**
 * For exporting reports in csv/excel format
 * 
 */
@RequestScoped
@Named("reportExporter")
public class ReportExporter
{

  private static final String SHEET_NAME = "Report Data Export";

  private static final String LIST_SEPARATOR = ",";
  public static Logger log = Logger.getLogger(ReportExporter.class);

  @Inject
  ReportManager reportManager;

  private StreamedContent file;
  private List<SearchResult> searchResult;

  private static final DateFormat DF = new SimpleDateFormat(
      "dd-MM-yyyy_HH-mm-ss");
  private static final DateFormat CELL_DF = new SimpleDateFormat("dd/MM/yyyy");

  /**
   * Create Excel data file for QC Report by Site
   * 
   * @throws FeatureNotSupportedException
   * @throws SecurityException
   * @throws IdentityException
   */
  public StreamedContent createQCFile() throws IdentityException,
      SecurityException, FeatureNotSupportedException
  {

    searchResult = reportManager.getQcReport();
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet1 = wb.createSheet(SHEET_NAME);

    CreationHelper createHelper = wb.getCreationHelper();
    Row row = null;
    int dataRowIndex = 0;

    row = sheet1.createRow((short) dataRowIndex);
    row.createCell(0).setCellValue(
        createHelper.createRichTextString(reportManager.getReportType()
            .toString()));

    dataRowIndex++;
    row = sheet1.createRow((short) dataRowIndex);
    row.createCell(0).setCellValue(createHelper.createRichTextString("MRN"));
    row.createCell(1).setCellValue(
        createHelper.createRichTextString("Specimen Label"));
    row.createCell(2).setCellValue(
        createHelper.createRichTextString("Disease Diagnosis"));
    row.createCell(3).setCellValue(
        createHelper.createRichTextString("Disease Site"));
    row.createCell(4).setCellValue(
        createHelper.createRichTextString("Specimen Type"));
    row.createCell(5).setCellValue(
        createHelper.createRichTextString("Prior Treatment"));
    row.createCell(6).setCellValue(
        createHelper.createRichTextString("% Necrosis"));
    row.createCell(7).setCellValue(
        createHelper.createRichTextString("% Nuclei"));
    row.createCell(8).setCellValue(
        createHelper.createRichTextString("Initial Amount"));
    row.createCell(9).setCellValue(
        createHelper.createRichTextString("Available Amount"));
    row.createCell(10).setCellValue(
        createHelper.createRichTextString("Shipped to NA Lab"));

    dataRowIndex++;
    for (SearchResult result : searchResult)
    {
      row = sheet1.createRow((short) dataRowIndex++);
      row.createCell(0).setCellValue(
          createHelper.createRichTextString(result.getConcatenatedMrn()));
      row.createCell(1).setCellValue(
          createHelper.createRichTextString(result.getSpecimenLabel()));
      row.createCell(2).setCellValue(
          createHelper.createRichTextString(result.getDiseaseDiagnosis()));
      row.createCell(3).setCellValue(
          createHelper.createRichTextString(result.getDiseaseSite()));
      row.createCell(4).setCellValue(
          createHelper.createRichTextString(result.getSpecimenType()));
      row.createCell(5).setCellValue(
          createHelper.createRichTextString(result.getPriorTreatment()));
      if (result.getPercentNecrosis() != null)
      {
        row.createCell(6).setCellValue(
            createHelper.createRichTextString(result.getPercentNecrosis()
                .toString()));
      }
      if (result.getPercentNuclei() != null)
      {
        row.createCell(7).setCellValue(
            createHelper.createRichTextString(result.getPercentNuclei()
                .toString()));
      }
      if (result.getSpecimenInitialAmount() != null)
      {
        row.createCell(8).setCellValue(
            createHelper.createRichTextString(result.getSpecimenInitialAmount()
                .toString()));
      }
      if (result.getSpecimenAmount() != null)
      {
        row.createCell(9).setCellValue(
            createHelper.createRichTextString(result.getSpecimenAmount()
                .toString()));
      }
      if (result.getShippedNaLab())
      {
        row.createCell(10).setCellValue(
            createHelper.createRichTextString("True"));
      }
    }

    String datePostFix = DF.format(new Date());
    // TODO allow client to provide file name/not critical
    String fileName = "qcReport_" + datePostFix + ".xls";

    try
    {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      wb.write(os);
      file = new DefaultStreamedContent(new ByteArrayInputStream(
          os.toByteArray()), "application/vnd.ms-excel", fileName);
    }
    catch (IOException e)
    {
      log.error("createFile error " + e.getMessage());
    }
    return this.file;
  }

  /**
   * Create Excel data file for most Reports
   * 
   * @throws FeatureNotSupportedException
   * @throws SecurityException
   * @throws IdentityException
   */
  public StreamedContent createReportFile() throws IdentityException,
      SecurityException, FeatureNotSupportedException
  {

    searchResult = reportManager.getQcReport();
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet1 = wb.createSheet(SHEET_NAME);

    CreationHelper createHelper = wb.getCreationHelper();
    Row row = null;
    int dataRowIndex = 0;

    row = sheet1.createRow((short) dataRowIndex);
    row.createCell(0).setCellValue(
        createHelper.createRichTextString(reportManager.getReportType()
            .toString()));

    dataRowIndex++;
    row = sheet1.createRow((short) dataRowIndex);
    row.createCell(0).setCellValue(createHelper.createRichTextString("MRN"));
    row.createCell(1).setCellValue(
        createHelper.createRichTextString("Specimen Label"));
    row.createCell(2).setCellValue(
        createHelper.createRichTextString("Disease Diagnosis"));
    row.createCell(3).setCellValue(
        createHelper.createRichTextString("Disease Site"));
    row.createCell(4).setCellValue(
        createHelper.createRichTextString("Collection Site"));
    row.createCell(5).setCellValue(
        createHelper.createRichTextString("Initial Specimen Amount"));
    if (reportManager.getReportType() == Reports.FULLY_QUALIFIED)
    {
      row.createCell(6).setCellValue(
          createHelper.createRichTextString("Shipped to NA Lab"));
    }
    dataRowIndex++;
    for (SearchResult result : searchResult)
    {
      row = sheet1.createRow((short) dataRowIndex++);
      row.createCell(0).setCellValue(
          createHelper.createRichTextString(result.getConcatenatedMrn()));
      row.createCell(1).setCellValue(
          createHelper.createRichTextString(result.getSpecimenLabel()));
      row.createCell(2).setCellValue(
          createHelper.createRichTextString(result.getDiseaseDiagnosis()));
      row.createCell(3).setCellValue(
          createHelper.createRichTextString(result.getDiseaseSite()));
      if (result.getCollectionSite() != null)
      {
        row.createCell(4).setCellValue(
            createHelper.createRichTextString(result.getCollectionSite()
                .toString()));
      }
      if (result.getSpecimenInitialAmount() != null)
      {
        row.createCell(5).setCellValue(
            createHelper.createRichTextString(result.getSpecimenInitialAmount()
                .toString()));
      }
      if (reportManager.getReportType() == Reports.FULLY_QUALIFIED
          && result.getShippedNaLab())
      {
        row.createCell(6).setCellValue(
            createHelper.createRichTextString("True"));
      }

    }

    String datePostFix = DF.format(new Date());
    // TODO allow client to provide file name/not critical
    String fileName = "qcReport_" + datePostFix + ".xls";

    try
    {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      wb.write(os);
      file = new DefaultStreamedContent(new ByteArrayInputStream(
          os.toByteArray()), "application/vnd.ms-excel", fileName);
    }
    catch (IOException e)
    {
      log.error("createFile error " + e.getMessage());
    }
    return this.file;
  }

  /**
   * Create Excel data file for NA Lab Reports
   * 
   * @throws FeatureNotSupportedException
   * @throws SecurityException
   * @throws IdentityException
   */
  public StreamedContent createNaLabFile() throws IdentityException,
      SecurityException, FeatureNotSupportedException
  {

    List<NaLabReportData> naLabResult = reportManager.getNaLabReport();
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet1 = wb.createSheet(SHEET_NAME);

    CreationHelper createHelper = wb.getCreationHelper();
    Row row = null;
    int dataRowIndex = 0;

    row = sheet1.createRow((short) dataRowIndex);
    row.createCell(0).setCellValue(
        createHelper.createRichTextString(reportManager.getReportType()
            .toString()));

    dataRowIndex++;
    row = sheet1.createRow((short) dataRowIndex);
    row.createCell(0).setCellValue(
        createHelper.createRichTextString("TCRB Aliquot Label"));
    row.createCell(1).setCellValue(
        createHelper.createRichTextString("Parent Specimen Label"));
    row.createCell(2).setCellValue(createHelper.createRichTextString("MRN"));
    row.createCell(3).setCellValue(
        createHelper.createRichTextString("NA Lab Id"));
    row.createCell(4)
        .setCellValue(createHelper.createRichTextString("NA Type"));
    row.createCell(5).setCellValue(
        createHelper.createRichTextString("Concentration"));
    row.createCell(6).setCellValue(
        createHelper.createRichTextString("Quantity"));
    row.createCell(7).setCellValue(
        createHelper.createRichTextString("RIN Value"));
    row.createCell(8).setCellValue(
        createHelper.createRichTextString("Gel Metrics"));

    dataRowIndex++;
    for (NaLabReportData result : naLabResult)
    {
      row = sheet1.createRow((short) dataRowIndex++);
      row.createCell(0).setCellValue(
          createHelper.createRichTextString(result.getAliquotLabel()));
      row.createCell(1).setCellValue(
          createHelper.createRichTextString(result.getParentLabel()));
      row.createCell(2).setCellValue(
          createHelper.createRichTextString(result.getConcatenatedMrn()));
      row.createCell(3).setCellValue(
          createHelper.createRichTextString(result.getNaLabId()));
      row.createCell(4).setCellValue(
          createHelper.createRichTextString(result.getNaType().toString()));
      if (result.getConcentration() != null)
      {
        row.createCell(5).setCellValue(
            createHelper.createRichTextString(result.getConcentration()
                .toString()));
      }
      if (result.getQuantity() != null)
      {
        row.createCell(6).setCellValue(
            createHelper.createRichTextString(result.getQuantity().toString()));
      }
      if(result.getRinValue() != null)
      {
        row.createCell(7).setCellValue(
            createHelper.createRichTextString(result.getRinValue().toString()));
      }
      if (result.getQuality() != null)
      {
        row.createCell(8).setCellValue(
            createHelper.createRichTextString(result.getQuality().toString()));
      }

    }

    String datePostFix = DF.format(new Date());
    // TODO allow client to provide file name/not critical
    String fileName = "naLabReport_" + datePostFix + ".xls";

    try
    {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      wb.write(os);
      file = new DefaultStreamedContent(new ByteArrayInputStream(
          os.toByteArray()), "application/vnd.ms-excel", fileName);
    }
    catch (IOException e)
    {
      log.error("createFile error " + e.getMessage());
    }
    return this.file;
  }

  /**
   * Create Excel data file for most Update page
   * 
   * @throws FeatureNotSupportedException
   * @throws SecurityException
   * @throws IdentityException
   */
  public StreamedContent createUpdateFile() throws IdentityException,
      SecurityException, FeatureNotSupportedException
  {

    searchResult = (List<SearchResult>) reportManager.getNotifications().getWrappedData();
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet1 = wb.createSheet(SHEET_NAME);

    CreationHelper createHelper = wb.getCreationHelper();
    Row row = null;
    int dataRowIndex = 0;

    row = sheet1.createRow((short) dataRowIndex);
    row.createCell(0).setCellValue(
        createHelper.createRichTextString("Specimen Update Report"));

    dataRowIndex++;
    row = sheet1.createRow((short) dataRowIndex);
    row.createCell(0).setCellValue(
        createHelper.createRichTextString("Specimen Label"));
    row.createCell(1)
        .setCellValue(createHelper.createRichTextString("Barcode"));
    row.createCell(2).setCellValue(
        createHelper.createRichTextString("caTissue ID"));
    row.createCell(3).setCellValue(
        createHelper.createRichTextString("Acquire UUID"));
    row.createCell(4).setCellValue(createHelper.createRichTextString("MRN"));
    row.createCell(5).setCellValue(createHelper.createRichTextString("Type"));
    row.createCell(6).setCellValue(createHelper.createRichTextString("Status"));
    row.createCell(7).setCellValue(
        createHelper.createRichTextString("Submission Date"));

    dataRowIndex++;
    for (SearchResult result : searchResult)
    {
      row = sheet1.createRow((short) dataRowIndex++);
      row.createCell(0).setCellValue(
          createHelper.createRichTextString(result.getSpecimenLabel()));
      row.createCell(1).setCellValue(
          createHelper.createRichTextString(result.getSpecimenBarcode()));
      row.createCell(2)
          .setCellValue(
              createHelper.createRichTextString(result.getInventoryId()
                  .toString()));
      row.createCell(3).setCellValue(
          createHelper.createRichTextString(result.getUuid()));
      row.createCell(4).setCellValue(
          createHelper.createRichTextString(result.getConcatenatedMrn()));
      row.createCell(5).setCellValue(
          createHelper.createRichTextString(result.getSpecimenType()));
      if (!result.getSpecimenStatus().isEmpty())
      {
        row.createCell(6).setCellValue(
            createHelper.createRichTextString(result.getConcatenatedStatus()));
      }
      if (result.getSubmissionDate() != null)
      {
        row.createCell(7).setCellValue(
            createHelper.createRichTextString(CELL_DF.format(result
                .getSubmissionDate())));
      }

    }

    String datePostFix = DF.format(new Date());
    // TODO allow client to provide file name/not critical
    String fileName = "updates_" + datePostFix + ".xls";

    try
    {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      wb.write(os);
      file = new DefaultStreamedContent(new ByteArrayInputStream(
          os.toByteArray()), "application/vnd.ms-excel", fileName);
    }
    catch (IOException e)
    {
      log.error("createFile error " + e.getMessage());
    }
    return this.file;
  }
}
