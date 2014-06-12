package edu.wustl.catissuecore.action;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.common.dynamicextensions.ui.util.Constants;
import edu.wustl.catissuecore.bizlogic.ComboDataBizLogic;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 *
 * @author
 * @version
 */
public class ClinicalDiagnosisDataAction extends BaseAction
{

	@Override
	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String limit = request.getParameter("limit");
		String query = request.getParameter("query");
		String start = request.getParameter("start");
		Integer limitFetch = Integer.parseInt(limit);
		Integer startFetch = Integer.parseInt(start);

		JSONArray jsonArray = new JSONArray();
		JSONObject mainJsonObject = new JSONObject();

		Integer total = limitFetch + startFetch;

		List clinicalDiagnosis = getClinicalDiagnosisValues(query);

		List<NameValueBean> clinicalDiagnosisBean = new ArrayList<NameValueBean>();
		populateQuerySpecificNameValueBeansList(clinicalDiagnosisBean, clinicalDiagnosis, query);
		mainJsonObject.put("totalCount", clinicalDiagnosisBean.size());
		request.setAttribute(Constants.SELECTED_VALUES, clinicalDiagnosisBean);

		for (int i = startFetch; i < total && i < clinicalDiagnosisBean.size(); i++)
		{
			JSONObject jsonObject = new JSONObject();
			Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();

			if (query == null
					|| clinicalDiagnosisBean.get(i).getName().toLowerCase(locale).contains(
							query.toLowerCase(locale)) || query.length() == 0)
			{
				jsonObject.put("id", clinicalDiagnosisBean.get(i).getValue());
				jsonObject.put("field", clinicalDiagnosisBean.get(i).getName());
				jsonArray.put(jsonObject);
			}
		}

		mainJsonObject.put("row", jsonArray);
		response.flushBuffer();
		PrintWriter out = response.getWriter();
		out.write(mainJsonObject.toString());

		return null;
	}

	/**
	 * returns the user list present in the system
	 * @param query
	 * @throws BizLogicException
	 */
	private List getClinicalDiagnosisValues(String query) throws BizLogicException
	{
		ComboDataBizLogic comboDataBizObj = new ComboDataBizLogic();
		return comboDataBizObj.getClinicalDiagnosisList(query,false);
	}

	/**
	 * This method populates name value beans list as per query,
	 * i.e. word typed into the auto-complete drop-down text field.
	 * @param querySpecificNVBeans
	 * @param users
	 * @param query
	 */
	private void populateQuerySpecificNameValueBeansList(List<NameValueBean> querySpecificNVBeans,
			List users, String query)
	{
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();

		for (Object obj : users)
		{
			NameValueBean nvb = (NameValueBean) obj;

			if (nvb.getName().toLowerCase(locale).contains(query.toLowerCase(locale)))
			{
				querySpecificNVBeans.add(nvb);
			}
		}
	}

}
