/*
 * Created on Aug 25, 2005 TODO To change the template for this generated file
 * go to Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.catissuecore.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.AdvanceSearchForm;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.util.global.AQConstants;

/**
 * @author gautam_shetty TODO To change the template for this generated type
 *         comment go to Window - Preferences - Java - Code Style - Code
 *         Templates
 */
public class SpreadsheetViewAction extends BaseAction
{

	/**
	 * logger.
	 */
	private transient final Logger logger = Logger.getCommonLogger(SpreadsheetViewAction.class);

	/**
	 * Overrides the executeSecureAction method of SecureAction class.
	 *
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
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		/**
		 * Name: Deepti Description: Query performance issue. Instead of saving
		 * complete query results in session, resultd will be fetched for each
		 * result page navigation. object of class QuerySessionData will be
		 * saved session, which will contain the required information for query
		 * execution while navigating through query result pages. Here,
		 * extending this class from BaseAction
		 */
		final HttpSession session = request.getSession();
		// changes are done for check all
		String checkAllPages = "";
		final String ch = request.getParameter(Constants.CHECK_ALL_PAGES);
		if (ch == null || ch.equals(""))
		{
			checkAllPages = (String) session.getAttribute(Constants.CHECK_ALL_PAGES);
		}
		else
		{
			checkAllPages = ch;
		}
		session.setAttribute(Constants.CHECK_ALL_PAGES, checkAllPages);
		final String isAjax = request.getParameter("isAjax");
		if (isAjax != null && isAjax.equals("true"))
		{
			response.setContentType("text/html");
			response.getWriter().write(checkAllPages);
			return null;
		}
		final QuerySessionData querySessionData = (QuerySessionData) session
				.getAttribute(edu.wustl.common.util.global.Constants.QUERY_SESSION_DATA);

		String pageOf = (String) request.getAttribute(Constants.PAGE_OF);
		if (pageOf == null)
		{
			pageOf = request.getParameter(Constants.PAGE_OF);
		}
		if (request.getAttribute(AQConstants.IDENTIFIER_FIELD_INDEX) == null)
		{
			final String identifierFieldIndex = request
					.getParameter(AQConstants.IDENTIFIER_FIELD_INDEX);
			if (identifierFieldIndex != null && !identifierFieldIndex.equals(""))
			{
				request.setAttribute(AQConstants.IDENTIFIER_FIELD_INDEX, new Integer(
						identifierFieldIndex));
			}
		}
		this.logger.debug("Pageof in spreadsheetviewaction.........:" + pageOf);
		final Object defaultViewAttribute = request
				.getAttribute(Constants.SPECIMENT_VIEW_ATTRIBUTE);
		if (defaultViewAttribute != null)
		{
			final List list = (List) request.getAttribute(AQConstants.SPREADSHEET_DATA_LIST);
			final List columnNames = (List) request.getAttribute(Constants.SPREADSHEET_COLUMN_LIST);
			// edu.wustl.catissuecore.util.global.AppUtility.setGridData(
			// list,columnNames, request);
			session.setAttribute(Constants.SPREADSHEET_COLUMN_LIST, columnNames);
			request.setAttribute(AQConstants.SPREADSHEET_DATA_LIST, list);
		}
		List list = null;
		final String pagination = request.getParameter("isPaging");
		if (pagination == null || pagination.equals("false"))
		{

			list = (List) request.getAttribute(AQConstants.SPREADSHEET_DATA_LIST);
			final List columnNames = (List) request.getAttribute(Constants.SPREADSHEET_COLUMN_LIST);

			// Set the SPREADSHEET_DATA_LIST and SPREADSHEET_COLUMN_LIST in the
			// session.
			// Next time when user clicks on pages of pagination Tag, it get the
			// same list from the session
			// and based on current page no, it will calculate
			// paginationDataList to be displayed by grid control.
			session.setAttribute(Constants.SPREADSHEET_COLUMN_LIST, columnNames);
			request.setAttribute(Constants.PAGINATION_DATA_LIST, list);
			AppUtility.setGridData(list, columnNames, request);
			session.setAttribute(Constants.TOTAL_RESULTS, querySessionData
					.getTotalNumberOfRecords());
			final AdvanceSearchForm advanceSearchForm = (AdvanceSearchForm) request
					.getAttribute("advanceSearchForm");
			if (advanceSearchForm != null)
			{
				session.setAttribute("advanceSearchForm", advanceSearchForm);
			}
		}

		int pageNum = Constants.START_PAGE;
		System.out.println(pageNum);
		String recordsPerPageStr = (String) session.getAttribute(Constants.RESULTS_PER_PAGE);
		List paginationDataList = null, columnList = null;

		// Get the SPREADSHEET_DATA_LIST and SPREADSHEET_COLUMN_LIST from the
		// session.
		columnList = (List) session.getAttribute(Constants.SPREADSHEET_COLUMN_LIST);

		if (request.getParameter(Constants.PAGE_NUMBER) != null)
		{
			pageNum = Integer.parseInt(request.getParameter(Constants.PAGE_NUMBER));
		}
		else if (session.getAttribute(Constants.PAGE_NUMBER) != null)
		{
			pageNum = Integer.parseInt(session.getAttribute(Constants.PAGE_NUMBER).toString());
		}
		if (request.getParameter(Constants.RESULTS_PER_PAGE) != null)
		{
			recordsPerPageStr = request.getParameter(Constants.RESULTS_PER_PAGE);
		}
		else if (request.getAttribute(Constants.RESULTS_PER_PAGE) != null)
		{
			recordsPerPageStr = request.getAttribute(Constants.RESULTS_PER_PAGE).toString();
		}
		final int recordsPerPage = new Integer(recordsPerPageStr);
		if (pagination != null && pagination.equalsIgnoreCase("true"))
		{
			paginationDataList = AppUtility.getPaginationDataList(request, this
					.getSessionData(request), recordsPerPage, pageNum, querySessionData);
			request.setAttribute(Constants.PAGINATION_DATA_LIST, paginationDataList);
			AppUtility.setGridData(paginationDataList, columnList, request);
		}

		/*
		 * if(pagination != null && pagination.equalsIgnoreCase("true")) {
		 * request
		 * .setAttribute(Constants.PAGINATION_DATA_LIST,paginationDataList); }
		 */

		request.setAttribute(Constants.SPREADSHEET_COLUMN_LIST, columnList);
		request.setAttribute(Constants.PAGE_NUMBER, Integer.toString(pageNum));

		session.setAttribute(Constants.RESULTS_PER_PAGE, recordsPerPage + "");
		// session.setAttribute(Constants.RESULTS_PER_PAGE,recordsPerPage);
		// Set the result per page attribute in the request to be uesd by
		// pagination Tag.
		// Prafull:Commented this can be retrived directly from constants on
		// jsp, so no need to save it in request.
		// request.setAttribute(Constants.RESULTS_PER_PAGE,Integer.toString(
		// Constants.NUMBER_RESULTS_PER_PAGE_SEARCH));
		request.setAttribute(Constants.PAGE_OF, pageOf);
		return mapping.findForward(pageOf);
	}

}
