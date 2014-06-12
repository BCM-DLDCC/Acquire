package edu.bcm.dldcc.big.acquire.listeners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.jboss.logging.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import edu.bcm.dldcc.big.acquire.listener.MinerSearchResultField;
import edu.bcm.dldcc.big.acquire.listener.MinerSearchTerm;
import edu.bcm.dldcc.big.acquire.query.ColumnModel;
import edu.bcm.dldcc.big.acquire.query.MinerResultColumn;
import edu.bcm.dldcc.big.acquire.query.SearchManager;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;

/**
 * Download Excel file of the miner search results
 * This component needs to be improved in several ways indicated within the methods
 * @author amcowiti
 *
 */
@RequestScoped
@Named("minerExcelExporter")
public class MinerFileDownloadManager {

	private static final String SHEET_NAME = "Miner Data Export";

	private static final String LIST_SEPARATOR = ",";
	public static Logger log = Logger.getLogger(MinerFileDownloadManager.class);
	
	
	@Inject 
	SearchManager searchManager;
	
	@Inject 
	MinerResultColumn minerResultColumn;
	
	
	private StreamedContent file;
	private List<SearchResult> searchResult;
	
	 DateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
	 
	
	/**
	 * Get the Export File as  stream {@link StreamedContent}
	 * @return
	 */
	 public StreamedContent getFile() {
	    searchResult=searchManager.runQuery();
		 createFile();
	     return file;
	   }  
	 
	 /**
	  * Create Excel data file
	  * TODO Allow cleint to provide file name
	  * TODO allow client to receive file via other means, eg email
	  */
	public void createFile(){

		 HSSFWorkbook wb=new HSSFWorkbook();
		 HSSFSheet sheet1 = wb.createSheet(SHEET_NAME);
		 
		 CreationHelper createHelper  = wb.getCreationHelper();
		Row row=null; 
		int dataRowIndex=0;
				
			 row = sheet1.createRow((short)dataRowIndex);
			 row.createCell(0).setCellValue(createHelper.createRichTextString(MinerSearchTerm.CRITERIA.toString()));
			 row.createCell(1).setCellValue(createHelper.createRichTextString(searchManager.getTerms()));
		
			 dataRowIndex++;
			 row = sheet1.createRow((short)dataRowIndex);
			List<ColumnModel> cols=minerResultColumn.getColumns();
			int numCols=cols.size();
			ColumnModel columnModel=null;
			for(int i=0;i<numCols;i++ ){
				 columnModel=cols.get(i);
				 row.createCell(i).setCellValue(createHelper.createRichTextString(columnModel.getHeader()));
			}
			
			//if can replicate p:datatable, can access dynamic columns without extra logic in #getColumnData
			//ListDataModel<SearchResult> dt= new ListDataModel<SearchResult>(searchResult);
			
			dataRowIndex++;
			String data="";
			ColumnModel col=null;
			for(SearchResult result:searchResult){
				row = sheet1.createRow((short)dataRowIndex++);
				for(int i=0;i<numCols;i++){
					col=cols.get(i);
					try{
						data=getColumnData(result,col.getProperty());
					}catch (Exception e){
						log.error("Error (Exception swallowed) retrieving data for property "+col.getProperty());
					}
					createDataColumns(createHelper,row,i,data);
				}
			}
			
			
			String datePostFix=df.format(new Date());
			//TODO allow client to provide file name/not critical
			String fileName="minerDataExport_"+datePostFix+".xls";
			
			//TODO if user is to email file, make non temp/Do not delete till emailed
			File exfile= null;//new File(fileName);
			
			 try {
				 exfile = File.createTempFile(fileName,""); 
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
						//swallow
						log.error("failed delete file "+fileName+ "  error="+e.getMessage());
					}
				}
			}
			
	}

	/**
	 * Create Data for a column cell 
	 * @param createHelper
	 * @param row
	 * @param column
	 * @param data
	 */
	private void createDataColumns(CreationHelper createHelper,Row row,int column,String data){
		
		 row.createCell(column).setCellValue(createHelper.createRichTextString((data!=null)?data:""));
	}
	
	
	/**
	 * Get Datum for a given column
	 * TODO We need a way to automate this. 
	 * For example, find a way of allowing SearchResult to be accessed by property
	 * e.g. SearchResult[property]
	 * @param property
	 * @param res SearhResult object
	 * @return
	 */
	private String getColumnData(SearchResult res,String property){
		
		MinerSearchResultField sf=MinerSearchResultField.getMinerSearchResultFieldByProperty(property) ;
		String data="";
		
		if(res != null || sf != null)
		{
		  data = sf.getData(res);
		}
			
		return data;
	}
}
