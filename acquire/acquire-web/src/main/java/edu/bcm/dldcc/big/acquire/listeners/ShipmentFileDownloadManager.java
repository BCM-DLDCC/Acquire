package edu.bcm.dldcc.big.acquire.listeners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.jboss.logging.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import edu.bcm.dldcc.big.acquire.qualifiers.NaLab;
import edu.bcm.dldcc.big.acquire.shipment.NaLabShipmentFormColumn;
import edu.bcm.dldcc.big.acquire.shipment.NaLabShipmentFormTerms;
import edu.bcm.dldcc.big.acquire.shipment.ShipmentManager;
import edu.bcm.dldcc.big.acquire.shipment.naLab.entity.BloodSample;
import edu.bcm.dldcc.big.acquire.shipment.naLab.entity.NaLabShipment;
import edu.bcm.dldcc.big.acquire.shipment.naLab.entity.TissueSample;


/**
 * HGSC NA Lab Shipment form Export as Excel
 * @author amcowiti
 *
 */
@RequestScoped
@Named("naLabShipmentFile")
public class ShipmentFileDownloadManager {

	private static final String FILE_NAME="HGSCtissue-intake_v1.05_";
	private static final String FILE_POSTFIX=".xls";
	private static int SPECIMEN_START_ROW=18;// tissue data@19
	private static final Integer MAX_SPECIMEN_TISSUE_ROWS=95;
	private static int BLOOD_START_ROW=125;// blood data@126
	
	protected String dataFileDirectory=System.getProperty("jboss.server.data.dir");
	private static String TISSUE_INTAKE="TissueIntake.xls";
	private String exportFileTemplate=dataFileDirectory+"/"+TISSUE_INTAKE;
	
	DateFormat filedf = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat df = new SimpleDateFormat("M/d/yy");
	
	private StreamedContent file;
	 
	public static Logger log = Logger.getLogger(ShipmentFileDownloadManager.class);
		
	@Inject
	 @NaLab
	 private ShipmentManager<NaLabShipment> naLabShipmentForm;
	 
	 HSSFWorkbook wb=null;
	 HSSFSheet sheet1 = null;
	 CreationHelper createHelper  = null;
	 
	 
	 NaLabShipment shipment=null;
	 String fileName=null;
	 private boolean validTemplate=true;
	 
	 	/**
		 * Get the Export File as  stream {@link StreamedContent}
		 * @return
		 */
		 public StreamedContent getFile() {
			 naLabShipmentForm.exportShipment();
		     return file;
		 } 
		
		
		
	 public void openWb(){
		 try{
			 wb=new HSSFWorkbook( new FileInputStream(exportFileTemplate));
			 sheet1=wb.getSheetAt(0);
			 createHelper=wb.getCreationHelper();
		}catch (Exception e){
			validTemplate=false;
			 log.error("@openWb No template export file  "+e.getMessage());
			 throw new UnsupportedOperationException("Expected template file "+ TISSUE_INTAKE +" not found");
		}
	 }
	 
	 
		
		 /**
		  * 
		  */
	 	@PostConstruct
		 public void createFile(){

	 		openWb();
	 		if(!validTemplate)
	 			return;
	 		
			 shipment=naLabShipmentForm.getShipment();
			 
			 String collaborator=shipment.getExternalCollaborator();
			 
			 Row row=null; 
			 
			 row = sheet1.getRow((short)2);
			 row.getCell(1).setCellValue(createHelper.createRichTextString(collaborator));
			
			createSectionData(SPECIMEN_START_ROW,NaLabShipmentFormTerms.SECTION_TITLE_TISSUE);
			createSectionData(BLOOD_START_ROW,NaLabShipmentFormTerms.SECTION_TITLE_BLOOD);
			
			String datePostFix=filedf.format(new Date());
			fileName=FILE_NAME+datePostFix+FILE_POSTFIX;
				
			File exfile= null;
			try {
					exfile = File.createTempFile(FILE_NAME+datePostFix, ".tmp"); 
					wb.write(new FileOutputStream(exfile));
					file = new DefaultStreamedContent(new FileInputStream(exfile), "application/vnd.ms-excel", fileName);
			} catch (IOException e) {
					log.error("createFile error "+e.getMessage());
			}finally{
				if(exfile!=null){
					try{
						exfile.delete();
						exfile=null;
					}catch (Exception e){
						log.error("failed delete file(server shutdown should delete it) "+fileName+ "  error="+e.getMessage());
					}
				}
			}
		 }
		
		
		 /**
		  * Tissue and Blood data rows
		  * Template Blurb says Tissue data begins row 10, Blood at row 116, but
		  * actual start rows may vary
		  * @param dataRowIndex
		  * @param term
		  */
		 public void createSectionData(int dataRowIndex,NaLabShipmentFormTerms term){
			 
			 switch(term){
			 case SECTION_TITLE_TISSUE:
				 createTissueSampleData(dataRowIndex,shipment.getSamples());
				 break;
			 case SECTION_TITLE_BLOOD:
				createBloodSampleData(dataRowIndex,shipment.getBloodSamples());
				break;
			 }
		 }
		 
		
		 /**
		  * Get Tissue Sample Data
		  * Since using template provided, and that template seems to allow max of 95 Max sample rows, will limit to
		 * TODO NB: This methods and {@link #createBloodSampleData(int, List)} could be improved/merged 
		 * if the Collection of Samples(Blood/Tissues) implemented a common  interface
		  * @param dataRowIndex start row for sample data
		  * @param samples
		  */
		public void createTissueSampleData(final int dataRowIndex,List<TissueSample> samples){
			
					 String cellValue="";
					 Row row=null; 
					 int cellNo=0;
					 
					 int rowNo=dataRowIndex;
					for(TissueSample sample:samples){
					
						 if(rowNo > dataRowIndex+MAX_SPECIMEN_TISSUE_ROWS){
							 log.warn("Template allows maximum of "+ MAX_SPECIMEN_TISSUE_ROWS +" TissueSample data rows. Extra rows truncated  file "+fileName);
							 break;
						 }
						 row = sheet1.getRow((short)rowNo);
						 
						 for(NaLabShipmentFormColumn column:NaLabShipmentFormColumn.values()){
							 cellNo=column.getCol();
							 switch(column.getCol()){
							 case 0:
								 cellValue=(sample.getBoxLocation()!=null)?sample.getBoxLocation().toString():"";
								 break;
							 case 1:
								 cellValue=(sample.getDiseaseType()!=null)?sample.getDiseaseType():"";
								 break;
							 case 2:
								 continue;
							 case 3:
								cellValue=(sample.getTubeBarcode()!=null)?sample.getTubeBarcode():"";
								 break;
							 case 4:
								 cellValue=(sample.getSampleId()!=null)?sample.getSampleId():"";
								  break;
							 case 5:
								 cellValue=(sample.getPatientId()!=null)?sample.getPatientId():"";
								 break;
							 case 6:
								cellValue=(sample.getType()!=null)?(sample.getType().getValue()!=null)?sample.getType().getValue():"":"";
								 break;
							case 7:
								 cellValue=(sample.getRole()!=null)?(sample.getRole().getValue()!=null)?sample.getRole().getValue():"":"";
								 break;
							case 8:
								 cellValue=(sample.getRelationship()!=null)?(sample.getRelationship().getValue()!=null)?sample.getRelationship().getValue():"":"";
								 break;
							
							 case 9:
								 cellValue=(sample.getCollectionDate()!=null)?df.format(sample.getCollectionDate()):"";
								 break;
							 case 10:
								cellValue=(sample.getPreservative()!=null)?sample.getPreservative():"";
								 break;	
								 
							 case 11://double field, exported as text
								cellValue=(sample.getLiquidAmount()!=null)?sample.getLiquidAmount().toString():"";
								 break;	 
								 
							 case 12:
								 cellValue=(sample.getCollectionSite()!=null)?sample.getCollectionSite():"";
								 break;	 
							 case 13://double, exported as text
								cellValue=(sample.getCellAmount()!=null)?sample.getCellAmount().toString():"";
								 break;	 
							 case 14:
								 cellValue=(sample.getComments()!=null)?sample.getComments():"";
								break;	 
						 }
						
						if(row.getCell(cellNo) !=null)
							row.getCell(cellNo).setCellValue(createHelper.createRichTextString(cellValue));
						else
							row.createCell(cellNo).setCellValue(createHelper.createRichTextString(cellValue));
						
					}
					rowNo++;
				 }
		}
		
		 
		/**
		 * Create Blood tissue data
		 * TODO NB: This methods and {@link #createTissueSampleData(int, List)} could be  improved/merged id the Collection of Samples(Blood/Tissues) were interfaces
		 * @param dataRowIndex start row for blood data
		 * @param samples
		 */
		 public void createBloodSampleData(final int dataRowIndex,List<BloodSample> samples){
			
			 String cellValue="";
			 Row row=null; 
			 int cellNo=0;
			 int rowNo=dataRowIndex;
			for(BloodSample sample:samples){
				
				 row = sheet1.getRow((short)rowNo);
				 for(NaLabShipmentFormColumn column:NaLabShipmentFormColumn.values()){
					 cellNo=column.getCol();
					 switch(column.getCol()){
						 case 0:
							 continue;
						case 1:
							 cellValue=(sample.getDiseaseType()!=null)?sample.getDiseaseType():"";
							  break;
						 case 2:
							 continue;
						 case 3:
							cellValue=(sample.getTubeBarcode()!=null)?sample.getTubeBarcode():"";
							 break;
						 case 4:
							cellValue=(sample.getSampleId()!=null)?sample.getSampleId():"";
							 break;
						 case 5:
							cellValue=(sample.getPatientId()!=null)?sample.getPatientId():"";
							 break;
						 case 6:
							 cellValue=(sample.getType()!=null)?(sample.getType().getValue()!=null)?sample.getType().getValue():"":"";
							  break;
						
						 case 7:
							 cellValue=(sample.getRole()!=null)?(sample.getRole().getValue()!=null)?sample.getRole().getValue():"":"";
							  break;
						
						 case 8:
							 cellValue=(sample.getRelationship()!=null)?(sample.getRelationship().getValue()!=null)?sample.getRelationship().getValue():"":"";
							 break;
						
						 case 9://date field, exported as text
							cellValue=(sample.getCollectionDate()!=null)?df.format(sample.getCollectionDate()):"";
							  break;
						 case 10:
							cellValue=(sample.getPreservative()!=null)?sample.getPreservative():"";
							  break;	
							 
						 case 11://double field, exported as text
							 cellValue=(sample.getLiquidAmount()!=null)?sample.getLiquidAmount().toString():"";
							   break;
						 case 12:
						 case 13:
							 continue;
						case 14:
							cellValue=(sample.getComments()!=null)?sample.getComments():"";
							cellNo=12;
							 break;	 
					 }
					 if(row.getCell(cellNo) !=null)
							row.getCell(cellNo).setCellValue(createHelper.createRichTextString(cellValue));
						else
							row.createCell(cellNo).setCellValue(createHelper.createRichTextString(cellValue));
				}
			
				 rowNo++;
			 }
		}	 
}
