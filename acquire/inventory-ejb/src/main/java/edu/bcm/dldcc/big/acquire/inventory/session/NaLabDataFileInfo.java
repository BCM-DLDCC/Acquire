package edu.bcm.dldcc.big.acquire.inventory.session;

public class NaLabDataFileInfo {

	//TODO use  path sep
	protected static final String NALAB_DATA_DIR="/uploadfiles/";
	
	protected static final String NALAB_DATA_FILE="Lims-Acquire.xls";
	protected static final String JBOSS_DATA_DIRECTORY=System.getProperty("jboss.server.data.dir");
	protected static final String importFile=JBOSS_DATA_DIRECTORY+NALAB_DATA_DIR+NALAB_DATA_FILE;
	protected  static final String LOGFILE="NaLabDataUpload";
	protected static final String RENAME_POSTFIX=".processed.";
	protected static final String RENAME_PENING=".pending.";
}
