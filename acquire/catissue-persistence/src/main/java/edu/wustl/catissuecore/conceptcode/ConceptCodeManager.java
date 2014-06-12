
package edu.wustl.catissuecore.conceptcode;

import java.util.Date;
import java.util.List;

import edu.upmc.opi.caBIG.caTIES.server.CaTIES_ExporterPR;
import edu.upmc.opi.caBIG.caTIES.services.caTIES_TiesPipe.TiesPipe;
import edu.wustl.catissuecore.caties.util.CSVLogger;
import edu.wustl.catissuecore.caties.util.CaCoreAPIService;
import edu.wustl.catissuecore.caties.util.CaTIESConstants;
import edu.wustl.catissuecore.caties.util.CaTIESProperties;
import edu.wustl.catissuecore.caties.util.StopServer;
import edu.wustl.catissuecore.caties.util.Utility;
import edu.wustl.catissuecore.domain.pathology.DeidentifiedSurgicalPathologyReport;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import gate.Gate;

/**
 * This class is responsible to fetch de-identified reports and to spawn
 * a sepearate thread to generate concept codes for de-identified reports.
 * This class manages the thread pool so that excessive threads will not be spawned.
 * @author vijay_pande
 */
public class ConceptCodeManager
{
	/**
	 * logger.
	 */
	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	/**
	 * logger.
	 */
	private static final Logger logger = Logger.getCommonLogger(ConceptCodeManager.class);
	/**
	 * Field coderVersion.
	 */
	private String coderVersion = null;
	/**
	 * Field gateHome.
	 */
	private String gateHome = null;
	/**
	 * Field creoleUrlName.
	 */
	private String creoleUrlName = null;
	/**
	 * Field caseInsensitiveGazetteerUrlName.
	 */
	private String caseInsensitiveGazetteerUrlName = null;
	/**
	 * Field caseSensitiveGazetteerUrlName.
	 */
	private String caseSensitiveGazetteerUrlName = null;
	/**
	 * Field sectionChunkerUrlName.
	 */
	private String sectionChunkerUrlName = null;
	/**
	 * Field conceptFilterUrlName.
	 */
	private String conceptFilterUrlName = null;
	/**
	 * Field negExUrlName.
	 */
	private String negExUrlName = null;
	/**
	 * Field conceptCategorizerUrlName.
	 */
	private String conceptCategorizerUrlName = null;
	/**
	 * Field tiesPipe.
	 */
	private TiesPipe tiesPipe = null;
	/**
	 * Field exporterPR.
	 */
	private CaTIES_ExporterPR exporterPR = null;

	/**
	 * Default constructor of the class.
	 */
	public ConceptCodeManager()
	{
		try
		{
			this.initCoder();
		}
		catch (final Exception ex)
		{
			ConceptCodeManager.logger.error("Initialization of concept coding" +
					"process failed or error in main thread" +
					ex.getMessage(),ex);
			ex.printStackTrace();
		}
	}

	/**
	 * This method is responsible for creating prerequisite environment that
	 * is required for initialization of the concept coding process.
	 * @throws Exception throws exception occured in the initialization process.
	 */
	private void initCoder() throws Exception
	{
		Utility.init();
		//Initialize CaCoreAPISservice
		CaCoreAPIService.initialize();
		// Configuring CSV logger
		CSVLogger.configure(CaTIESConstants.LOGGER_CONCEPT_CODER);
		this.setAll();
		this.initialize();
	}

	/**
	 * Methos to set initial values for various processors like gate, gazeteer, chunker, etc.
	 */
	public void setAll()
	{
		this.coderVersion = CaTIESProperties.getValue("caties.coder.version");
		this.gateHome = CaTIESProperties.getValue("caties.gate.home");
		this.creoleUrlName = CaTIESProperties.getValue("caties.creole.url.name");
		this.caseInsensitiveGazetteerUrlName = CaTIESProperties
				.getValue("caties.case.insensitive.gazetteer.url.name");
		this.caseSensitiveGazetteerUrlName = CaTIESProperties
				.getValue("caties.case.sensitive.gazetteer.url.name");
		this.sectionChunkerUrlName = CaTIESProperties.getValue("caties.section.chunker.url.name");
		this.conceptFilterUrlName = CaTIESProperties.getValue("caties.concept.filter.url.name");
		this.negExUrlName = CaTIESProperties.getValue("caties.neg.ex.url.name");
		this.conceptCategorizerUrlName = CaTIESProperties
				.getValue("caties.concept.categorizer.url.name");
	}

	/**
	 * Method initialize.
	 */
	public void initialize()
	{
		this.establishCaTIESExporterPR();
		logger.debug("Established Exporter PR.");
		this.establishTiesPipeForDirectAccess();
		logger.debug("Established Ties Pipe.");
		this.establishGateInterface();
		logger.debug("Established Gate Interface.");
	}

	/**
	 * Method establishCaTIESExporterPR.
	 */
	private void establishCaTIESExporterPR()
	{
		this.exporterPR = new CaTIES_ExporterPR();
	}

	/**
	 * Method establishTiesPipeForDirectAccess.
	 */
	private void establishTiesPipeForDirectAccess()
	{
		try
		{
			this.tiesPipe = new TiesPipe();
			this.tiesPipe.setGateHome(this.gateHome);
			this.tiesPipe.setCreoleUrlName(this.creoleUrlName);
			this.tiesPipe.setCaseInsensitiveGazetteerUrlName(this.caseInsensitiveGazetteerUrlName);
			this.tiesPipe.setCaseSensitiveGazetteerUrlName(this.caseSensitiveGazetteerUrlName);
			this.tiesPipe.setSectionChunkerUrlName(this.sectionChunkerUrlName);
			this.tiesPipe.setConceptFilterUrlName(this.conceptFilterUrlName);
			this.tiesPipe.setNegExUrlName(this.negExUrlName);
			this.tiesPipe.setConceptCategorizerUrlName(this.conceptCategorizerUrlName);
		}
		catch (final Exception x)
		{
			ConceptCodeManager.logger.error(x.getMessage(),x);
			x.printStackTrace();
		}
	}

	/**
	 * Method establishGateInterface.
	 */
	private void establishGateInterface()
	{
		try
		{
			System.setProperty("gate.home", this.gateHome);
			Gate.init();
		}
		catch (final Exception x)
		{
			ConceptCodeManager.logger.error(x.getMessage(),x);
			x.printStackTrace();
		}
	}

	/**
	 * This method is responsible for managing the overall process of de-identification.
	 * @throws InterruptedException - InterruptedException
	 */
	public void startProcess() throws InterruptedException
	{
		while (true)
		{
			try
			{
				ConceptCodeManager.logger.info("Concept Coding process started at "
						+ new Date().toString());
				final List deidReportIDList = this.getReportIDList();
				this.processReports(deidReportIDList);
			}
			catch (final Exception ex)
			{
				ConceptCodeManager.logger.error(
					"Unexpected Exception in Concept Code Pipeline"+ex.getMessage(), ex);
				ConceptCodeManager.logger.info("Concept Coding process finished at "
						+ new Date().toString()
						+ ". Thread is going to sleep.");
				Thread.sleep(Integer.parseInt(CaTIESProperties
						.getValue(CaTIESConstants.CONCEPT_CODER_SLEEPTIME)));
			}
		}
	}

	/**
	 * This method is responsible for managing the pool of thread, fetching individual
	 * reports by ID and intiating the de-identification rpocess.
	 * @param deidReportIDList deidentified surgical pathology report ID list
	 * @throws Exception generic exception
	 */
	private void processReports(List deidReportIDList) throws Exception
	{
		if (deidReportIDList != null && !deidReportIDList.isEmpty())
		{
			logger.info(deidReportIDList.size() + " reports found for Concept Coding");
			try
			{
				CSVLogger.info(CaTIESConstants.LOGGER_CONCEPT_CODER,
						CaTIESConstants.CSVLOGGER_DATETIME
								+ CaTIESConstants.CSVLOGGER_SEPARATOR
								+ CaTIESConstants.CSVLOGGER_DEIDENTIFIED_REPORT
								+ CaTIESConstants.CSVLOGGER_SEPARATOR
								+ CaTIESConstants.CSVLOGGER_STATUS
								+ CaTIESConstants.CSVLOGGER_SEPARATOR
								+ CaTIESConstants.CSVLOGGER_MESSAGE
								+ CaTIESConstants.CSVLOGGER_SEPARATOR
								+ CaTIESConstants.CSVLOGGER_PROCESSING_TIME);
				DeidentifiedSurgicalPathologyReport deidReport = null;
				ConceptCoder conceptCoder = null;
				for (int i = 0; i < deidReportIDList.size(); i++)
				{
					deidReport = (DeidentifiedSurgicalPathologyReport)
							CaCoreAPIService.getObject(
							DeidentifiedSurgicalPathologyReport.class,
							Constants.SYSTEM_IDENTIFIER,
							deidReportIDList.get(i));
					conceptCoder = new ConceptCoder(
							deidReport, this.exporterPR, this.tiesPipe);
					logger.info("Concept coding of report serial no " + i + " started....");
					conceptCoder.process();
					System.gc();
					logger.info("Concept coding of report serial no " + i + " finished.");
				}
			}
			catch (final Exception ex)
			{
				ConceptCodeManager.logger.error("Concept Coding pipeline failed:"
						+ ex.getMessage(), ex);
				ex.printStackTrace();
			}
		}
		else
		{
			ConceptCodeManager.logger.info("Concept Coding process finished at "
					+ new Date().toString()
					+ ". Thread is going to sleep.");
			Thread.sleep(Integer.parseInt(CaTIESProperties
					.getValue(CaTIESConstants.CONCEPT_CODER_SLEEPTIME)));
		}
	}

	/**
	 * This method fetche a list of ID's of identified surgical pathology reports
	 * pending for de-indetification.
	 * @return deidReportIDList De-identified report ID list
	 * @throws Exception generic exception
	 */
	private List getReportIDList() throws Exception
	{
		final String hqlQuery = "select id from edu.wustl.catissuecore.domain." +
				"pathology.DeidentifiedSurgicalPathologyReport where "
				+ CaTIESConstants.COLUMN_NAME_REPORT_STATUS
				+ "='"
				+ CaTIESConstants.PENDING_FOR_XML + "'";
		final List deidReportIDList = (List) CaCoreAPIService.executeQuery(hqlQuery,
				DeidentifiedSurgicalPathologyReport.class.getName());
		return deidReportIDList;
	}

	/**
	 * Main method for the ConceptCodeManager class.
	 * @param args commandline arguments
	 */
	public static void main(String[] args)
	{
		try
		{
			final ConceptCodeManager conceptCodeManager = new ConceptCodeManager();
			final Thread stopThread = new StopServer(CaTIESConstants.CONCEPT_CODER_PORT);
			stopThread.start();
			conceptCodeManager.startProcess();
		}
		catch (final Exception ex)
		{
			ConceptCodeManager.logger.error("Concept code manager failed"+ex.getMessage(),ex);
			ex.printStackTrace();
		}
	}}