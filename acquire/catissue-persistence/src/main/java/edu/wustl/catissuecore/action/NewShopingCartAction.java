
package edu.wustl.catissuecore.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.xmi.AnnotationUtil;
import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.catissuecore.action.annotations.AnnotationConstants;
import edu.wustl.catissuecore.actionForm.AliquotForm;
import edu.wustl.catissuecore.actionForm.SpecimenForm;
import edu.wustl.catissuecore.actionForm.ViewSpecimenSummaryForm;
import edu.wustl.catissuecore.bizlogic.QueryShoppingCartBizLogic;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.querysuite.QueryShoppingCart;
import edu.wustl.catissuecore.util.CatissueCoreCacheManager;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.CommonUtilities;
import edu.wustl.query.util.querysuite.EntityCacheFactory;

/**
 * @author renuka_bajpai
 *
 */
public class NewShopingCartAction extends BaseAction
{

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
	 * @throws BizLogicException
	 *             : BizLogicException
	 * @throws DynamicExtensionsSystemException
	 *             : DynamicExtensionsSystemException
	 * @throws CheckedException
	 *             : CheckedException
	 * @throws DynamicExtensionsApplicationException
	 *             : DynamicExtensionsApplicationException
	 * @return ActionForward : ActionForward
	 */
	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws BizLogicException,
			DynamicExtensionsSystemException, CheckedException,
			DynamicExtensionsApplicationException
	{

		List<List<String>> cartnew = new ArrayList<List<String>>();
		final HttpSession session = request.getSession();
		int count = 0;
		int newCartSize = 0;
		int oldCartSize = 0;
		QueryShoppingCart queryShoppingCart = (QueryShoppingCart) session
				.getAttribute(edu.wustl.catissuecore.util.global.Constants.QUERY_SHOPPING_CART);
		final QueryShoppingCartBizLogic queryShoppingCartBizLogic = new QueryShoppingCartBizLogic();
		List<AttributeInterface> oldAttributeList = new ArrayList<AttributeInterface>();
		List<AttributeInterface> cartAttributeList = new ArrayList<AttributeInterface>();
		final List<String> columnList = new ArrayList<String>();
		final String pageOf = request
				.getParameter(edu.wustl.catissuecore.util.global.Constants.PAGE_OF);
		/**
		 * value of readOnly attribute is specified in UpdateBulkSpecimensAction
		 * and UpdateSpecimenStatusAction This value will only true in case of
		 * addition of multiple specimen. This is done to disable collected
		 * checkbox in specimen summary page. bug 12959
		 */
		final Boolean readOnly = (Boolean) request.getAttribute("readOnly");
		if (readOnly != null)
		{
			if (form instanceof ViewSpecimenSummaryForm)
			{
				final ViewSpecimenSummaryForm summaryForm = (ViewSpecimenSummaryForm) form;
				summaryForm.setReadOnly(readOnly);
			}
		}

		// int[] searchTarget = prepareSearchTarget();
		// int basedOn = 0;

		// Set<EntityInterface> entityCollection = new
		// HashSet<EntityInterface>();
		// String[] searchString ={"specimen"};
		Collection<AttributeInterface> attributeCollection = new ArrayList<AttributeInterface>();
		final EntityCache cache = EntityCacheFactory.getInstance();
		// MetadataSearch advancedSearch = new MetadataSearch(cache);
		Long specimenEntityId = (Long) CatissueCoreCacheManager.getInstance().getObjectFromCache(
				"specimenEntityId");

		if (CatissueCoreCacheManager.getInstance().getObjectFromCache("specimenEntityId") != null)
		{
			specimenEntityId = (Long) CatissueCoreCacheManager.getInstance().getObjectFromCache(
					"specimenEntityId");
		}
		else
		{
			specimenEntityId = AnnotationUtil.getEntityId(AnnotationConstants.ENTITY_NAME_SPECIMEN);
			CatissueCoreCacheManager.getInstance().addObjectToCache("specimenEntityId",
					specimenEntityId);
		}
		attributeCollection = cache.getEntityById(specimenEntityId).getEntityAttributesForQuery();
		// MatchedClass matchedClass = advancedSearch.search(searchTarget,
		// searchString, basedOn);
		// entityCollection = matchedClass.getEntityCollection();
		// List resultList = new ArrayList(entityCollection);
		// for (int i = 0; i < resultList.size(); i++)
		// {
		// EntityInterface entity = (EntityInterface) resultList.get(i);
		// String fullyQualifiedEntityName = entity.getName();
		// if(fullyQualifiedEntityName.equals(Specimen.class.getName()))
		// {
		// attributeCollection=entity.getEntityAttributesForQuery();
		// break;
		// }
		// }
		final Iterator<AttributeInterface> attributreItr = attributeCollection.iterator();

		String[] selectColumnName = new String[attributeCollection.size()];
		for (int i = 0; i < attributeCollection.size(); i++)
		{

			final AttributeInterface elem = attributreItr.next();
			final String columnName = elem.getName().toString();
			selectColumnName[i] = columnName;
			columnList.add(columnName + " : " + "Specimen");
			cartAttributeList.add(elem);
		}

		if (queryShoppingCart == null)
		{
			queryShoppingCart = new QueryShoppingCart();
			queryShoppingCart.setCartAttributeList(cartAttributeList);
			queryShoppingCart.setColumnList(columnList);
			oldAttributeList = cartAttributeList;

		}
		else
		{
			/* deleted the cart */
			if (queryShoppingCart.isEmpty())
			{
				queryShoppingCart.setCartAttributeList(cartAttributeList);
				queryShoppingCart.setColumnList(columnList);
				oldAttributeList = cartAttributeList;
			}
			if (queryShoppingCart != null && queryShoppingCart.getCartAttributeList() != null)
			{
				oldAttributeList = queryShoppingCart.getCartAttributeList();
				oldCartSize = queryShoppingCart.getCart().size();

				if (queryShoppingCart.getCartAttributeList().get(0).getEntity().getName().equals(
						Specimen.class.getName()))
				{
					selectColumnName = new String[oldAttributeList.size()];
					selectColumnName = this.getManiputedColumnList(queryShoppingCart
							.getCartAttributeList());
					// cartAttributeList=new ArrayList<AttributeInterface>();
					cartAttributeList = oldAttributeList;

				}
				else
				{
					selectColumnName = null;
				}

			}
		}

		if (selectColumnName != null)
		{
			cartnew = this.createListOfItems(form, selectColumnName, request);

			// Action Errors changed to Action Messages
			final ActionMessages messages = new ActionMessages();
			final int indexArray[] = queryShoppingCartBizLogic.getNewAttributeListIndexArray(
					oldAttributeList, cartAttributeList);
			if (indexArray != null)
			{

				count = queryShoppingCartBizLogic.add(queryShoppingCart, cartnew, null);
				newCartSize = queryShoppingCart.getCart().size();

				if (count > 0)
				{
					if ((cartnew.size() + oldCartSize - newCartSize) > 0)
					{
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shoppingcart.duplicateObjError", count, cartnew.size()
										+ oldCartSize - newCartSize));
						this.saveMessages(request, messages);
					}
					else
					{
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shoppingCart.addMessage", cartnew.size()));
						this.saveMessages(request, messages);
					}

				}
				else if (count == 0)
				{
					if ((cartnew.size() + oldCartSize - newCartSize) > 0)
					{
						messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
								"shoppingcart.duplicateObjError", count, cartnew.size()
										+ oldCartSize - newCartSize));
						this.saveMessages(request, messages);
					}
				}

			}
			else
			{
				this.addDifferentCartViewError(request);
			}

		}
		else
		{
			this.addDifferentCartViewError(request);
		}

		session.setAttribute(edu.wustl.catissuecore.util.global.Constants.QUERY_SHOPPING_CART,
				queryShoppingCart);
		return mapping.findForward(pageOf);
	}

	/**
	 *
	 * @param oldAttributeList : oldAttributeList
	 * @return String[] : String[]
	 */
	private String[] getManiputedColumnList(List<AttributeInterface> oldAttributeList)
	{

		final String[] selectColumnName = new String[oldAttributeList.size()];

		for (int i = 0; i < oldAttributeList.size(); i++)
		{

			final AttributeInterface attributeInterface = oldAttributeList.get(i);

			selectColumnName[i] = attributeInterface.getName();

		}
		return selectColumnName;
	}

	/**
	 *
	 * @param form : form
	 * @param selectColumnName : selectColumnName
	 * @param request : request
	 * @return List < List < String >> : List < List < String >>
	 * @throws BizLogicException : BizLogicException
	 */
	private List<List<String>> createListOfItems(ActionForm form, String[] selectColumnName,
			HttpServletRequest request) throws BizLogicException
	{
		final String objName = Specimen.class.getName();
		final IBizLogic bizLogic = this.getBizLogic(objName);
		// Object searchObjects = null;
		AliquotForm aliquotForm = new AliquotForm();
		SpecimenForm specimenForm = new SpecimenForm();

		final List<List<String>> cartnew = new ArrayList<List<String>>();
		List<String> columnList = new ArrayList<String>();
		final String[] whereColumnCondition = {"="};
		final String[] whereColumnName = {"id"};

		if (form instanceof AliquotForm)
		{
			aliquotForm = (AliquotForm) form;
			final List<AbstractDomainObject> specimenList = aliquotForm.getSpecimenList();
			final Iterator<AbstractDomainObject> it = specimenList.iterator();

			while (it.hasNext())
			{

				List<String> columnList2 = new ArrayList<String>();
				final Specimen specimen = (Specimen) it.next();
				final Object[] whereColumnValue = {specimen.getId()};
				final List ls1 = bizLogic.retrieve(objName, selectColumnName, whereColumnName,
						whereColumnCondition, whereColumnValue, null);
				columnList2 = this.createList(ls1);
				cartnew.add(columnList2);
			}
		}
		else if (form instanceof SpecimenForm)
		{
			specimenForm = (SpecimenForm) form;
			final Object[] whereColumnValue = {specimenForm.getId()};
			final List ls = bizLogic.retrieve(objName, selectColumnName, whereColumnName,
					whereColumnCondition, whereColumnValue, null);
			columnList = this.createList(ls);
			cartnew.add(columnList);
		}
		else if (form instanceof ViewSpecimenSummaryForm)
		{
			final List ls = (List) request.getAttribute("specimenIdList");
			if (ls != null)
			{
				final Iterator itr = ls.iterator();
				while (itr.hasNext())
				{

					List<String> columnList2 = new ArrayList<String>();
					final Object[] whereColumnValue = {Long.valueOf((itr.next()).toString())};
					final List ls1 = bizLogic.retrieve(objName, selectColumnName, whereColumnName,
							whereColumnCondition, whereColumnValue, null);
					columnList2 = this.createList(ls1);
					cartnew.add(columnList2);
				}
			}
		}

		return cartnew;

	}

	/**
	 * @param domainObjectName
	 *            name of domain object
	 * @return IBizLogic : IBizLogic
	 * @throws BizLogicException : BizLogicException
	 */
	private IBizLogic getBizLogic(String domainObjectName) throws BizLogicException
	{
		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final IBizLogic bizLogic = factory.getBizLogic(domainObjectName);
		return bizLogic;

	}

	/**
	 * @param list : list
	 * @return List < String > : list
	 */
	private List<String> createList(List list)
	{

		final Object[] obj1 = (Object[]) list.get(0);
		final String[] cartList = new String[obj1.length];
		for (int j = 0; j < obj1.length; j++)
		{
			if (obj1[j] != null)
			{
				if (obj1[j] instanceof Date)
				{
					cartList[j] = CommonUtilities.parseDateToString((Date) obj1[j],
							CommonServiceLocator.getInstance().getDatePattern());
				}
				else
				{
					cartList[j] = obj1[j].toString();
				}
			}
			else
			{
				cartList[j] = null;
			}

		}

		return Arrays.asList(cartList);
	}

	/**
	 * @param request
	 *            : request
	 */
	private void addDifferentCartViewError(HttpServletRequest request)
	{
		final ActionErrors errors = new ActionErrors();
		final ActionError error = new ActionError("shoppingcart.differentViewError");
		errors.add(ActionErrors.GLOBAL_ERROR, error);
		this.saveErrors(request, errors);
		new String(edu.wustl.catissuecore.util.global.Constants.DIFFERENT_VIEW_IN_CART);
	}

	// private int[] prepareSearchTarget()
	// {
	// List<Integer> target = new ArrayList<Integer>();
	// System.out.println();
	// target.add(new Integer(Constants.CLASS));
	// target.add(new Integer(Constants.ATTRIBUTE));
	// target.add(new Integer(Constants.PV));
	// int[] searchTarget = new int[target.size()];
	//
	// for (int i = 0; i < target.size(); i++)
	// {
	// searchTarget[i] = ((Integer) (target.get(i))).intValue();
	// }
	// return searchTarget;
	// }
}
