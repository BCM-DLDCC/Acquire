package edu.bcm.dldcc.big.acquire.inventory.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.jboss.logging.Logger;

/**
 * NA Lab Data Excel Reader
 * PreCondition: Excel 97-2004 format, non xml
 * First row is column header
 * First empty row is end of data
 * @author amcowiti
 *
 */

@Stateless
@LocalBean
public class NaLabDataExcelReader implements NaLabDataSource,Serializable{

	private static final long serialVersionUID = 6261822101920861474L;
	
	
	private static final int ALLOWED_EMPTY_ROWS=2;
	DateFormat filedf = new SimpleDateFormat("yyyy-MM-dd");
	
	 HSSFWorkbook wb=null;
	 HSSFSheet sheet = null;


	private FileInputStream inputStream;
	 
	 public static Logger log = Logger.getLogger(NaLabDataExcelReader.class);
	
	 /**
	  * Get NaLabData rows from Excel sheet
	  * Row 0 is the header row
	  * Allow two contiguous empty rows as end of data
	  * @return
	  */
	 public  List<NaLabDataRow> getNaLabDataRows(){
		 
		 if((new File(NaLabDataFileInfo.importFile)).exists())
		 {
			 openWb();
		 }
		 else
		 {
			 return null;
		 }
		 
		 if(wb == null){
			 log.warn("No workable worksheet found!");
			 return null;
		 }
		 
		 List<NaLabDataRow> dataRows= new ArrayList<NaLabDataRow>();
		int  noOfRows = sheet.getPhysicalNumberOfRows();
		int noOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
		String[] srow=new String[noOfColumn];
		Row row = null;
		
		int emptyRows=0;
		for (int i = 1; i < noOfRows; i++) {
			row = sheet.getRow(i);
			if(row == null){
				if(emptyRows++ < ALLOWED_EMPTY_ROWS)
					continue;
				break;
			}
			emptyRows=0;
			srow = getRowContent(row, noOfColumn);
			dataRows.add(extractNaLabData(i,srow));
		}
		 return dataRows;
	 }
	 
	 @PreDestroy
	 public void destroy(){
		 if(inputStream!=null)
			try {
				inputStream.close();
			} catch (IOException e) {
				log.trace("Exceptione closing file "+e.getMessage());
			}
		 if(wb != null){
			 wb=null;
		 }
	 }
	 
	 private void openWb(){
		 
		 try{
			 inputStream=new FileInputStream(NaLabDataFileInfo.importFile);
			 wb=new HSSFWorkbook( inputStream);
			 sheet=wb.getSheetAt(0);
		}catch (Exception e){
			 log.error("@openWb Failed to open Na Lab import file  "+e.getMessage());
			 throw new UnsupportedOperationException("Expected import data file "+ NaLabDataFileInfo.NALAB_DATA_FILE +"  in "+NaLabDataFileInfo.JBOSS_DATA_DIRECTORY+" has errors "+e.getMessage());
		}
	 }
	 
	 
	 
	 private NaLabDataRow extractNaLabData(Integer row,String[] srow){
		 NaLabDataRow dataRow= new NaLabDataRow();
		 dataRow.setRow(row);
		 dataRow.setParentId((srow[0] !=null)?srow[0].trim():null);
		 dataRow.setPatientId((srow[1] !=null)?srow[1].trim():null);
		 dataRow.setDateReceived(srow[2]);
		 dataRow.setAmountConsumed(srow[3]);
		 dataRow.setAmount(srow[4]);
		 dataRow.setConcentration(srow[5]);
		 dataRow.setRin(srow[6]);
		 dataRow.setQuality(srow[7]);
		 dataRow.setType(srow[8]);
		 dataRow.setDerivativeId((srow[9] !=null)?srow[9].trim():null);
		 
		 return dataRow;
	 }
	 
	 private  String[] getRowContent(Row row, int noOfColumn) {
			Cell cell = null;
			String[] rowContent = new String[noOfColumn];
			int cellType;
			for (short i = 0; i < noOfColumn; i++) {
				try {
					cell = row.getCell(i);
					if (cell == null)
						rowContent[i] = null;//was ""
					else {
						cellType = cell.getCellType();
						switch (cellType) {
								case 0: { 
									rowContent[i] = String.valueOf(BigDecimal.valueOf(cell.getNumericCellValue()));
									break;
								}
								case 1: {
									rowContent[i] = cell.getRichStringCellValue().toString();
									break;
								}
								case 2: {
									rowContent[i] = String.valueOf(BigDecimal.valueOf(cell.getNumericCellValue()));
									break;
								}
							}
					}

				} catch (Exception e) {
					log.error("Exception while reading  cell #="+i);
					rowContent[i] = null;
				}
			}
			return rowContent;
		}
}
