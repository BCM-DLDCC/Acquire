/*
 * Created on Jul 15, 2005
 * @author mandar_deshmukh
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.catissuecore.action;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.SummaryForm;
import edu.wustl.catissuecore.bean.SummaryAdminDetails;
import edu.wustl.catissuecore.bean.SummaryPartDetails;
import edu.wustl.catissuecore.bean.SummarySpDetails;
import edu.wustl.catissuecore.bizlogic.SummaryBizLogic;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.XSSSupportedAction;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.logger.Logger;

/**
 * @author mandar_deshmukh
 * This class instantiates the QueryBizLogic class and retrieves data
 * from database and populates the SummaryForm
 */
public class SummaryAction extends XSSSupportedAction
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static Logger logger = Logger.getCommonLogger(SummaryAction.class);

	/**
	 * Overrides the executeSecureAction method of SecureAction class.
	 * @param mapping
	 *            object of ActionMapping
	 * @param form
	 *            object of ActionForm
	 * @param request
	 *            object of HttpServletRequest
	 * @param response
	 *            object of HttpServletResponse
	 * @throws Exception
	 *             generic exception
	 * @return ActionForward : ActionForward
	 */

	@Override
	@SuppressWarnings("unchecked")
	public ActionForward executeXSS(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		SummaryForm summaryForm = null;
		try
		{
			summaryForm = (SummaryForm) form;
			// preparing QueryBizLogic to query
			final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			final SummaryBizLogic bizLogic = (SummaryBizLogic) factory
					.getBizLogic(Constants.SUMMARY_BIZLOGIC_ID);

			//Populating the Map<String, Object> with data from database for summary report
			final Map<String, Object> summaryDataMap = bizLogic.getTotalSummaryDetails();

			//Populate the Summary Form
			summaryForm.setTotalSpCount(summaryDataMap.get("TotalSpecimenCount").toString());

			// Specimen Details
			final SummarySpDetails spDetails = new SummarySpDetails();

			spDetails.setCellCount(summaryDataMap.get("CellCount").toString());
			spDetails.setCellQuantity(summaryDataMap.get("CellQuantity").toString());
			spDetails
					.setCellTypeDetails((Collection<Object>) summaryDataMap.get("CellTypeDetails"));

			spDetails.setFluidCount(summaryDataMap.get("FluidCount").toString());
			spDetails.setFluidQuantity(summaryDataMap.get("FluidQuantity").toString());
			spDetails.setFluidTypeDetails((Collection<Object>) summaryDataMap
					.get("FluidTypeDetails"));

			spDetails.setMoleculeCount(summaryDataMap.get("MoleculeCount").toString());
			spDetails.setMoleculeQuantity(summaryDataMap.get("MoleculeQuantity").toString());
			spDetails.setMolTypeDetails((Collection<Object>) summaryDataMap
					.get("MoleculeTypeDetails"));

			spDetails.setTissueCount(summaryDataMap.get("TissueCount").toString());
			spDetails.setTissueQuantity(summaryDataMap.get("TissueQuantity").toString());
			spDetails.setTissueTypeDetails((Collection<Object>) summaryDataMap
					.get("TissueTypeDetails"));

			spDetails.setPatStDetails((Collection<Object>) summaryDataMap
					.get(Constants.SP_PATHSTAT));
			spDetails.setTSiteDetails((Collection<Object>) summaryDataMap.get(Constants.SP_TSITE));

			summaryForm.setSpecDetails(spDetails);

			// Participant Details
			final SummaryPartDetails prDetails = new SummaryPartDetails();
			prDetails.setPByCDDetails((Collection<Object>) summaryDataMap.get(Constants.P_BYCD));
			prDetails.setPByCSDetails((Collection<Object>) summaryDataMap.get(Constants.P_BYCS));
			prDetails.setTotPartCount(summaryDataMap.get(Constants.TOTAL_PART_COUNT).toString());

			summaryForm.setPartDetails(prDetails);

			// Administrative details
			final SummaryAdminDetails adminDetails = new SummaryAdminDetails();
			adminDetails.setColSites(summaryDataMap.get(Constants.COLL_SITE_COUNT).toString());
			adminDetails.setLabSites(summaryDataMap.get(Constants.LAB_SITE_COUNT).toString());
			adminDetails.setRepSites(summaryDataMap.get(Constants.REPO_SITE_COUNT).toString());

			adminDetails.setCpTot(summaryDataMap.get(Constants.TOTAL_CP_COUNT).toString());
			adminDetails.setDpTot(summaryDataMap.get(Constants.TOTAL_DP_COUNT).toString());
			adminDetails.setRegUsers(summaryDataMap.get(Constants.TOTAL_USER_COUNT).toString());

			adminDetails.setAdminInfo((List<List>) summaryDataMap.get(Constants.USER_DATA));

			summaryForm.setAdminDetails(adminDetails);

		}
		catch (final Exception e)
		{
			SummaryAction.logger.error(e.getMessage(), e);
			e.printStackTrace() ;
		}
		request.setAttribute("summaryForm", summaryForm);
		if (true)
		{
			;
		}
		//	throw new Exception("Mandar : defined excp");
		return mapping.findForward(Constants.SUCCESS);
	}
}
