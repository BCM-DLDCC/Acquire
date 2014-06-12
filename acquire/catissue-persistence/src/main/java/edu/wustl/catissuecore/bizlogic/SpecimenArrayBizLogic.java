/*
 * <p>Title: SpecimenArrayBizLogic Class </p> <p>Description:This class performs
 * business level logic for Specimen Array</p> Copyright: Copyright (c) year
 * 2006 Company: Washington University, School of Medicine, St. Louis.
 * @version 1.1 Created on Aug 28,2006
 */

package edu.wustl.catissuecore.bizlogic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.wustl.catissuecore.domain.CellSpecimen;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.ContainerPosition;
import edu.wustl.catissuecore.domain.FluidSpecimen;
import edu.wustl.catissuecore.domain.MolecularSpecimen;
import edu.wustl.catissuecore.domain.NewSpecimenArrayOrderItem;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenArray;
import edu.wustl.catissuecore.domain.SpecimenArrayContent;
import edu.wustl.catissuecore.domain.SpecimenArrayType;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;
import edu.wustl.catissuecore.domain.SpecimenPosition;
import edu.wustl.catissuecore.domain.StorageContainer;
import edu.wustl.catissuecore.domain.TissueSpecimen;
import edu.wustl.catissuecore.util.ApiSearchUtil;
import edu.wustl.catissuecore.util.Position;
import edu.wustl.catissuecore.util.StorageContainerUtil;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * <p>
 * This class initializes the fields of SpecimenArrayBizLogic.java
 * </p>
 * @author Ashwin Gupta
 * @version 1.1
 */
public class SpecimenArrayBizLogic extends CatissueDefaultBizLogic
{

	private transient final Logger logger = Logger.getCommonLogger(SpecimenArrayBizLogic.class);

	/**
	 * @see edu.wustl.common.bizlogic.AbstractBizLogic#insert(java.lang.Object,
	 *      edu.wustl.common.dao.DAO, edu.wustl.common.beans.SessionDataBean)
	 * @param obj : obj
	 * @param dao : dao
	 * @param sessionDataBean : sessionDataBean
	 * @throws BizLogicException : BizLogicException
	 */
	@Override
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		try
		{
			final SpecimenArray specimenArray = (SpecimenArray) obj;

			this.checkStorageContainerAvailablePos(specimenArray, dao, sessionDataBean);

			this.doUpdateSpecimenArrayContents(specimenArray, null, dao, sessionDataBean, true);

			dao.insert(specimenArray.getCapacity());
			dao.insert(specimenArray);
			SpecimenArrayContent specimenArrayContent = null;
			// TODO move this method to HibernateDAOImpl for common use (for
			// collection insertion)
			for (final Iterator iter = specimenArray.getSpecimenArrayContentCollection().iterator(); iter
					.hasNext();)
			{
				specimenArrayContent = (SpecimenArrayContent) iter.next();
				specimenArrayContent.setSpecimenArray(specimenArray);
				dao.insert(specimenArrayContent);
			}
		}
		catch (final DAOException daoExp)
		{
			this.logger.error(daoExp.getMessage(), daoExp);
			daoExp.printStackTrace();
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * @param obj : obj
	 * @param dao : dao
	 * @param sessionDataBean : sessionDataBean
	 * @throws BizLogicException : BizLogicException
	 */
	@Override
	public void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		super.postInsert(obj, dao, sessionDataBean);

	}

	/**
	 * @param dao : dao
	 * @param currentObj : currentObj
	 * @param oldObj : oldObj
	 * @param sessionDataBean : sessionDataBean
	 * @throws BizLogicException : BizLogicException
	 */
	@Override
	public void postUpdate(DAO dao, Object currentObj, Object oldObj,
			SessionDataBean sessionDataBean) throws BizLogicException

	{
		super.postUpdate(dao, currentObj, oldObj, sessionDataBean);
	}

	/**
	 *      @param dao : dao
	 *      @param obj : obj
	 *      @param oldObj : oldObj
	 *      @param sessionDataBean : sessionDataBean
	 *      @throws BizLogicException : BizLogicException
	 */

	@Override
	protected void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		try
		{
			
			final SpecimenArray specimenArray = (SpecimenArray) obj;
			final SpecimenArray oldSpecimenArray = (SpecimenArray) oldObj;

			boolean flag = true;
			if (oldSpecimenArray.getLocatedAtPosition() != null && specimenArray.getLocatedAtPosition().getParentContainer().getId().longValue() == oldSpecimenArray
					.getLocatedAtPosition().getParentContainer().getId().longValue()
					// && specimenArray.getLocatedAtPosition() != null
					&& specimenArray.getLocatedAtPosition().getPositionDimensionOne().longValue() == oldSpecimenArray
							.getLocatedAtPosition().getPositionDimensionOne().longValue()
					&& specimenArray.getLocatedAtPosition().getPositionDimensionTwo().longValue() == oldSpecimenArray
							.getLocatedAtPosition().getPositionDimensionTwo().longValue())
			{
				flag = false;
			}

			if (flag)
			{
				final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
				final StorageContainerBizLogic storageContainerBizLogic = (StorageContainerBizLogic) factory
						.getBizLogic(Constants.STORAGE_CONTAINER_FORM_ID);
				final String contId= specimenArray.getLocatedAtPosition()
				.getParentContainer().getId().toString();
				final String posOne= specimenArray
				.getLocatedAtPosition().getPositionDimensionOne().toString();
				final String posTwo= specimenArray
				.getLocatedAtPosition().getPositionDimensionTwo().toString();
				storageContainerBizLogic.checkContainer(dao,StorageContainerUtil.setparameterList
				(contId, posOne, posTwo, false),sessionDataBean,null);
			}
			this.doUpdateSpecimenArrayContents(specimenArray, oldSpecimenArray, dao,
					sessionDataBean, false);

			dao.update(specimenArray.getCapacity(),oldSpecimenArray.getCapacity());
			dao.update(specimenArray,oldSpecimenArray);
			SpecimenArrayContent specimenArrayContent = null;
			// SpecimenArray oldSpecimenArray = (SpecimenArray) oldObj;
			final Collection oldSpecArrayContents = ((SpecimenArray) oldObj)
					.getSpecimenArrayContentCollection();

			for (final Iterator iter = specimenArray.getSpecimenArrayContentCollection().iterator(); iter
					.hasNext();)
			{
				specimenArrayContent = (SpecimenArrayContent) iter.next();
				specimenArrayContent.setSpecimenArray(specimenArray);
				// increment by 1 because of array index starts from 0.
				if (specimenArrayContent.getPositionDimensionOne() != null)
				{
					// Bug: 2365: grid location of parent array was getting
					// changed
					if (specimenArray.isAliquot())
					{
						specimenArrayContent.setPositionDimensionOne(new Integer(
								specimenArrayContent.getPositionDimensionOne().intValue()));
						specimenArrayContent.setPositionDimensionTwo(new Integer(
								specimenArrayContent.getPositionDimensionTwo().intValue()));
					}
					else
					{
						specimenArrayContent.setPositionDimensionOne(new Integer(
								specimenArrayContent.getPositionDimensionOne().intValue() + 1));
						specimenArrayContent.setPositionDimensionTwo(new Integer(
								specimenArrayContent.getPositionDimensionTwo().intValue() + 1));
					}
				}

				if (this.checkExistSpecimenArrayContent(specimenArrayContent, oldSpecArrayContents) == null)
				{
					dao.insert(specimenArrayContent);
					
				}
				else
				{
					Iterator<SpecimenArrayContent> specimenArrayContentItr = oldSpecArrayContents.iterator();
					SpecimenArrayContent oldSpecArrayContent = null;
					
					while(specimenArrayContentItr.hasNext())
					{
						SpecimenArrayContent specimenArrContent =  (SpecimenArrayContent)specimenArrayContentItr.next();
						if(specimenArrContent.getId().equals(specimenArrayContent.getId()))
						{
							oldSpecArrayContent = specimenArrContent;
							break;
						}
					}
					
					dao.update(specimenArrayContent,oldSpecArrayContent);
				}
			}

			if (Status.ACTIVITY_STATUS_DISABLED.toString()
					.equals(specimenArray.getActivityStatus()))
			{
				final ContainerPosition prevPosition = specimenArray.getLocatedAtPosition();

				specimenArray.setLocatedAtPosition(null);
				dao.update(specimenArray,oldSpecimenArray);

				if (prevPosition != null)
				{
					dao.delete(prevPosition);
				}

				
			}
		}
		catch (final DAOException daoExp)
		{
			this.logger.error(daoExp.getMessage(), daoExp);
			daoExp.printStackTrace();
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * @param specimenArrayContent
	 *            array contents
	 * @param specArrayContentCollection
	 *            spec array contents
	 * @return whether it is new or old
	 */
	private SpecimenArrayContent checkExistSpecimenArrayContent(
			SpecimenArrayContent specimenArrayContent, Collection specArrayContentCollection)
	{
		boolean isNew = true;
		SpecimenArrayContent arrayContent = null;

		for (final Iterator iter = specArrayContentCollection.iterator(); iter.hasNext();)
		{
			arrayContent = (SpecimenArrayContent) iter.next();
			if (specimenArrayContent.getId() == null)
			{
				isNew = true;
				break;
			}
			else if (arrayContent.getId() != null)
			{
				if (arrayContent.getId().longValue() == specimenArrayContent.getId().longValue())
				{
					isNew = false;
					break;
				}
			}
		}
		if (isNew)
		{
			arrayContent = null;
		}
		return arrayContent;
	}

	/**
	 * @param oldSpecimenArray : oldSpecimenArray
	 * @param specimenArray
	 *            specimen array
	 * @param dao
	 *            dao
	 * @param sessionDataBean
	 *            session data bean
	 * @param isInsertOperation
	 *            is insert operation
	 * @throws BizLogicException : BizLogicException
	 */
	private void doUpdateSpecimenArrayContents(SpecimenArray specimenArray,
			SpecimenArray oldSpecimenArray, DAO dao, SessionDataBean sessionDataBean,
			boolean isInsertOperation) throws BizLogicException
	{
		try
		{
			Collection oldSpecimenArrayContentCollection = null;
			if (oldSpecimenArray != null)
			{
				oldSpecimenArrayContentCollection = oldSpecimenArray
						.getSpecimenArrayContentCollection();
			}

			final Collection specimenArrayContentCollection = specimenArray
					.getSpecimenArrayContentCollection();
			final Collection updatedSpecArrayContentCollection = new HashSet();
			SpecimenArrayContent specimenArrayContent = null;
			Specimen specimen = null;
	
			if (specimenArrayContentCollection != null && !specimenArrayContentCollection.isEmpty())
			{
				double quantity = 0.0;
				// fetch array type to check specimen class
				final Object object = dao.retrieveById(SpecimenArrayType.class.getName(),
						specimenArray.getSpecimenArrayType().getId());
				SpecimenArrayType arrayType = null;

				if (object != null)
				{
					arrayType = (SpecimenArrayType) object;
				}

				for (final Iterator iter = specimenArrayContentCollection.iterator(); iter
						.hasNext();)
				{
					specimenArrayContent = (SpecimenArrayContent) iter.next();

					/**
					 * Start: Change for API Search --- Jitendra 06/10/2006 In
					 * Case of Api Search, previoulsy it was failing since there
					 * was default class level initialization on domain object.
					 * For example in User object, it was initialized as
					 * protected String lastName=""; So we removed default class
					 * level initialization on domain object and are
					 * initializing in method setAllValues() of domain object.
					 * But in case of Api Search, default values will not get
					 * set since setAllValues() method of domainObject will not
					 * get called. To avoid null pointer exception, we are
					 * setting the default values same as we were setting in
					 * setAllValues() method of domainObject.
					 */
					// ApiSearchUtil.setSpecimenArrayContentDefault(
					// specimenArrayContent);
					// End:- Change for API Search
					specimen = this.getSpecimen(dao, specimenArrayContent);
					if (specimen != null)
					{
						// check whether array & specimen are compatible on the
						// basis of class
						if (!this.isArrayAndSpecimenCompatibile(arrayType, specimen))
						{
							throw this.getBizLogicException(null, "spec.not.compatible", "");
						}

						// set quantity object to null when there is no value..
						// [due to Hibernate exception]
						if (specimenArrayContent.getInitialQuantity() != null)
						{
							if (specimenArrayContent.getInitialQuantity() == null)
							{
								specimenArrayContent.setInitialQuantity(null);
							}
						}

						// if molecular then check available quantity
						if (specimen instanceof MolecularSpecimen)
						{
							if (specimenArrayContent.getInitialQuantity() != null)
							{
								quantity = specimenArrayContent.getInitialQuantity().doubleValue();
								final double tempQuantity = quantity;
								SpecimenArrayContent oldArrayContent = null;
								// incase if specimenArray is created from
								// aliquot page, then skip the Available
								// quantity of specimen.
								if (!specimenArray.isAliquot())
								{
									// in case of update, reduce specimen's
									// quantity by difference of new
									// specimenArrayContent's quantiy
									// and old specimenArrayContent's quantiy.
									if (oldSpecimenArrayContentCollection != null)
									{
										oldArrayContent = this.checkExistSpecimenArrayContent(
												specimenArrayContent,
												oldSpecimenArrayContentCollection);
										if (oldArrayContent != null)
										{
											quantity = quantity
													- oldArrayContent.getInitialQuantity()
															.doubleValue();
										}
									}

									if (!this.isAvailableQty(specimen, quantity))
									{
										throw this.getBizLogicException(null,
												"quantity.more.then.distri.quantity", tempQuantity
														+ ":"
														+ specimen.getAvailableQuantity()
																.doubleValue() + ":"
														+ specimen.getLabel());
									}
								}
							}
							else
							{
								throw this.getBizLogicException(null, "enter.quantity.mol.spec",
										specimen.getLabel());
							}
						}
						specimenArrayContent.setSpecimen(specimen);
						// Added by jitendra
						if (specimenArrayContent.getPositionDimensionOne() == null
								|| specimenArrayContent.getPositionDimensionTwo() == null)
						{
							throw this.getBizLogicException(null, "array.contentPosition.err.msg",
									"");
						}
						updatedSpecArrayContentCollection.add(specimenArrayContent);
					}
				}
			}

			// There should be at least one valid specimen in array
			if (updatedSpecArrayContentCollection.isEmpty())
			{
				throw this.getBizLogicException(null, "spec.array.should.contain.atleast.one.spec",
						"");
			}

			// In case of update, if specimen is removed from specimen array,
			// then specimen array content's quantity
			// should get added into specimen's available quantity.
			if (!isInsertOperation)
			{
				final Iterator itr = oldSpecimenArrayContentCollection.iterator();
				while (itr.hasNext())
				{
					final SpecimenArrayContent oldSpecimenArrayContent = (SpecimenArrayContent) itr
							.next();
					final SpecimenArrayContent newSpecimenArrayContent = this
							.checkExistSpecimenArrayContent(oldSpecimenArrayContent,
									specimenArrayContentCollection);
					if (newSpecimenArrayContent == null
							|| newSpecimenArrayContent.getSpecimen().getLabel() == null
							|| newSpecimenArrayContent.getSpecimen().getLabel().equals(""))
					{
						final Specimen oldSpecimen = this.getSpecimen(dao, oldSpecimenArrayContent);
						if (oldSpecimen != null && oldSpecimen instanceof MolecularSpecimen)
						{
							final Double oldQuantity = oldSpecimenArrayContent.getInitialQuantity();
							Double quantity = oldSpecimen.getAvailableQuantity();
							final double newQuantity = quantity.doubleValue()
									+ oldQuantity.doubleValue();
							quantity = newQuantity;
							oldSpecimen.setAvailableQuantity(quantity);
							dao.update(oldSpecimen);
						}
					}
				}
			}
			specimenArray.setSpecimenArrayContentCollection(updatedSpecArrayContentCollection);

		}
		catch (final DAOException daoExp)
		{
			this.logger.error(daoExp.getMessage(), daoExp);
			daoExp.printStackTrace();
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * @param specimenArray : specimenArray
	 * @param dao : dao
	 * @param sessionDataBean : sessionDataBean
	 * @throws BizLogicException : BizLogicException
	 */
	private void checkStorageContainerAvailablePos(SpecimenArray specimenArray, DAO dao,
			SessionDataBean sessionDataBean) throws BizLogicException

	{
		if (specimenArray.getLocatedAtPosition() != null
				&& specimenArray.getLocatedAtPosition().getParentContainer() != null)
		{
			this.retriveScId(dao, specimenArray);
			this.retriveScName(specimenArray, dao);
			final StorageContainer storageContainerObj = (StorageContainer) specimenArray
					.getLocatedAtPosition().getParentContainer();
			// check for closed Storage Container
			this.checkStatus(dao, storageContainerObj, "Storage Container");
			final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			final StorageContainerBizLogic storageContainerBizLogic = (StorageContainerBizLogic) factory
					.getBizLogic(Constants.STORAGE_CONTAINER_FORM_ID);
			final String contId= storageContainerObj.getId().toString();
			final String posOne= specimenArray
			.getLocatedAtPosition().getPositionDimensionOne().toString();
			final String posTwo= specimenArray
			.getLocatedAtPosition().getPositionDimensionTwo().toString();
			storageContainerBizLogic.checkContainer(dao,StorageContainerUtil.setparameterList
			(contId, posOne, posTwo, false),sessionDataBean,null);
			specimenArray.getLocatedAtPosition().setParentContainer(storageContainerObj);
			specimenArray.getLocatedAtPosition().setOccupiedContainer( specimenArray );//bug 15137
		}
	}

	/**
	 * @param specimenArray : specimenArray
	 * @param dao  : dao
	 * @throws BizLogicException : BizLogicException
	 */
	private void retriveScName(SpecimenArray specimenArray, DAO dao) throws BizLogicException
	{
		try
		{
			if (specimenArray.getLocatedAtPosition().getParentContainer().getId() != null)
			{
				final StorageContainer storageContainerObj = new StorageContainer();
				storageContainerObj.setId(specimenArray.getLocatedAtPosition().getParentContainer()
						.getId());
				final String sourceObjectName = StorageContainer.class.getName();
				final String[] selectColumnName = {"name"};
				// String[] whereColumnName = {"id"};
				// //"storageContainer."+edu.wustl
				// .common.util.global.Constants.SYSTEM_IDENTIFIER
				// String[] whereColumnCondition = {"="};
				// Object[] whereColumnValue =
				// {specimenArray.getLocatedAtPosition
				// ().getParentContainer().getId()};
				// String joinCondition = null;

				final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
				queryWhereClause.addCondition(new EqualClause("id", specimenArray
						.getLocatedAtPosition().getParentContainer().getId()));

				final List list = dao
						.retrieve(sourceObjectName, selectColumnName, queryWhereClause);

				if (!list.isEmpty())
				{
					storageContainerObj.setName((String) list.get(0));
					specimenArray.getLocatedAtPosition().setParentContainer(storageContainerObj);
				}
			}
		}
		catch (final DAOException daoExp)
		{
			this.logger.error(daoExp.getMessage(), daoExp);
			daoExp.printStackTrace();
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * @param specimen
	 *            specimen
	 * @param quantity
	 *            quantity
	 * @return whether the quantity is available.
	 */
	private boolean isAvailableQty(Specimen specimen, double quantity)
	{

		if (specimen instanceof MolecularSpecimen)
		{
			final MolecularSpecimen molecularSpecimen = (MolecularSpecimen) specimen;
			double availabeQty = Double.parseDouble(molecularSpecimen.getAvailableQuantity()
					.toString());// molecularSpecimen.
			// getAvailableQuantityInMicrogram
			// ().doubleValue();
			if (quantity > availabeQty)
			{
				return false;
			}
			else
			{
				availabeQty = availabeQty - quantity;
				molecularSpecimen.setAvailableQuantity(new Double(availabeQty));// molecularSpecimen
				// .
				// setAvailableQuantityInMicrogram
				// (
				// new
				// Double
				// (
				// availabeQty
				// )
				// )
				// ;
			}
		}
		return true;
	}

	/**
	 * @param dao
	 *            dao
	 * @param arrayContent : arrayContent
	 * @return Specimen
	 * @throws BizLogicException : BizLogicException
	 */
	private Specimen getSpecimen(DAO dao, SpecimenArrayContent arrayContent)
			throws BizLogicException
	{
		try
		{
			// get list of Participant's names
			Specimen specimen = arrayContent.getSpecimen();

			if (specimen != null)
			{
				String columnName = null;
				String columnValue = null;

				if ((specimen.getLabel() != null) && (!specimen.getLabel().trim().equals("")))
				{
					columnName = Constants.SPECIMEN_LABEL_COLUMN_NAME;
					columnValue = specimen.getLabel();
				}
				else if ((specimen.getBarcode() != null)
						&& (!specimen.getBarcode().trim().equals("")))
				{
					columnName = Constants.SPECIMEN_BARCODE_COLUMN_NAME;
					columnValue = specimen.getBarcode();
				}
				else
				{
					return null;
				}
				final String sourceObjectName = Specimen.class.getName();
				final String whereColumnName = columnName;
				final String whereColumnValue = columnValue;

				final List list = dao.retrieve(sourceObjectName, whereColumnName, whereColumnValue);
				if (!list.isEmpty())
				{
					specimen = (Specimen) list.get(0);
					/**
					 * Name : Virender Reviewer: Prafull Calling Domain object
					 * from Proxy Object
					 */
					specimen = (Specimen) HibernateMetaData.getProxyObjectImpl(specimen);
					final String activityStatus = specimen.getActivityStatus();
					// Bug: 2872:- User should not able to add close/disable
					// specimen in Specimen Array.
					if (!activityStatus.equals(Status.ACTIVITY_STATUS_ACTIVE.toString()))
					{
						throw this.getBizLogicException(null, "spec.array.spec.invalid",
								columnValue);
					}
					// return specimenCollectionGroup;
				}
				else
				{
					throw this.getBizLogicException(null, "spec.array.spec.does.nt.exists",
							columnValue);
				}
			}
			return specimen;

		}
		catch (final DAOException daoExp)
		{
			this.logger.error(daoExp.getMessage(), daoExp);
			daoExp.printStackTrace();
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * @param arrayType
	 *            array
	 * @param specimen
	 *            specimen
	 * @return true if compatible else false | | ----- on the basis of specimen
	 *         class
	 */
	private boolean isArrayAndSpecimenCompatibile(SpecimenArrayType arrayType, Specimen specimen)
	{
		boolean compatible = false;
		final String arraySpecimenClassName = arrayType.getSpecimenClass();
		final String specSpecimenClassName = this.getClassName(specimen);

		if (arraySpecimenClassName.equals(specSpecimenClassName))
		{
			compatible = true;
		}
		return compatible;
	}

	/**
	 * This function returns the actual type of the specimen i.e Cell / Fluid /
	 * Molecular / Tissue.
	 * @param specimen : specimen
	 * @return String
	 */

	public final String getClassName(Specimen specimen)
	{
		String className = "";

		if (specimen instanceof CellSpecimen)
		{
			className = Constants.CELL;
		}
		else if (specimen instanceof MolecularSpecimen)
		{
			className = Constants.MOLECULAR;
		}
		else if (specimen instanceof FluidSpecimen)
		{
			className = Constants.FLUID;
		}
		else if (specimen instanceof TissueSpecimen)
		{
			className = Constants.TISSUE;
		}
		return className;
	}

	/**
	 * Overriding the parent class's method to validate the enumerated attribute
	 * values
	 * @param obj : obj
	 * @param dao :dao
	 * @param operation : operation
	 * @return boolean
	 * @throws BizLogicException : BizLogicException
	 */
	@Override
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		try
		{
			final SpecimenArray specimenArray = (SpecimenArray) obj;

			/**
			 * Start: Change for API Search --- Jitendra 06/10/2006 In Case of
			 * Api Search, previoulsy it was failing since there was default
			 * class level initialization on domain object. For example in User
			 * object, it was initialized as protected String lastName=""; So we
			 * removed default class level initialization on domain object and
			 * are initializing in method setAllValues() of domain object. But
			 * in case of Api Search, default values will not get set since
			 * setAllValues() method of domainObject will not get called. To
			 * avoid null pointer exception, we are setting the default values
			 * same as we were setting in setAllValues() method of domainObject.
			 */
			ApiSearchUtil.setSpecimenArrayDefault(specimenArray);
			// End:- Change for API Search

			// Added by Ashish
			if (specimenArray == null)
			{
				throw this.getBizLogicException(null, "domain.object.null.err.msg",
						"Specimen Array");
			}

			final Validator validator = new Validator();

			if (specimenArray.getActivityStatus() == null)
			{
				specimenArray.setActivityStatus(Status.ACTIVITY_STATUS_ACTIVE.toString());
			}
			String message = "";
			if (specimenArray.getSpecimenArrayType() == null
					|| specimenArray.getSpecimenArrayType().getId() == null
					|| specimenArray.getSpecimenArrayType().getId().longValue() == -1)
			{
				message = ApplicationProperties.getValue("array.arrayType");
				throw this.getBizLogicException(null, "errors.item.required", message);
			}

			// fetch array type to check specimen class
			final Object object = dao.retrieveById(SpecimenArrayType.class.getName(), specimenArray
					.getSpecimenArrayType().getId());
			SpecimenArrayType specimenArrayType = null;

			if (object != null)
			{
				specimenArrayType = (SpecimenArrayType) object;
			}
			else
			{
				message = ApplicationProperties.getValue("array.arrayType");
				throw this.getBizLogicException(null, "errors.invalid", message);
			}

			// validate name of array
			if (Validator.isEmpty(specimenArray.getName()))
			{
				message = ApplicationProperties.getValue("array.arrayLabel");
				throw this.getBizLogicException(null, "errors.item.required", message);
			}

			// validate storage position
			/*
			 * if (specimenArray.getPositionDimensionOne() == null ||
			 * specimenArray.getPositionDimensionTwo() == null ||
			 * !validator.isNumeric
			 * (String.valueOf(specimenArray.getPositionDimensionOne()), 1) ||
			 * !validator
			 * .isNumeric(String.valueOf(specimenArray.getPositionDimensionTwo
			 * ()), 1) ||(!validator.isNumeric(String.valueOf(specimenArray.
			 * getStorageContainer().getId()), 1) &&
			 * validator.isEmpty(specimenArray
			 * .getStorageContainer().getName())))
			 */
			if(specimenArray.getLocatedAtPosition()!=null && specimenArray.getLocatedAtPosition().getParentContainer()!=null)
			{
				if ((!validator.isNumeric(String.valueOf(specimenArray.getLocatedAtPosition()
						.getParentContainer().getId()), 1) && Validator.isEmpty(specimenArray
								.getLocatedAtPosition().getParentContainer().getName())))
				{
					message = ApplicationProperties.getValue("array.positionInStorageContainer");
					throw this.getBizLogicException(null, "errors.item.format", message);
				}
			}

			if (specimenArray.getLocatedAtPosition() != null
					&& specimenArray.getLocatedAtPosition().getParentContainer() != null)
			{
				this.retriveScId(dao, specimenArray);
			}

			Integer xPos = null;
			Integer yPos = null;
			if (specimenArray.getLocatedAtPosition() != null)
			{
				xPos = specimenArray.getLocatedAtPosition().getPositionDimensionOne();
				yPos = specimenArray.getLocatedAtPosition().getPositionDimensionTwo();
			}
			/**
			 * Following code is added to set the x and y dimension in case only
			 * storage container is given and x and y positions are not given
			 */
			if (xPos == null || yPos == null)
			{
				{
					if (specimenArray.getLocatedAtPosition().getParentContainer() != null)
					{
						final Position position = StorageContainerUtil.getFirstAvailablePositionInContainer(
								specimenArray.getLocatedAtPosition().getParentContainer(), dao);
						if (position != null)
						{
							final ContainerPosition locatedAtPos = specimenArray
									.getLocatedAtPosition();
							locatedAtPos.setPositionDimensionOne(position.getXPos());
							locatedAtPos.setPositionDimensionTwo(position.getYPos());
						}
						else
						{
							throw this.getBizLogicException(null, "storage.specified.full", "");
						}
						xPos = specimenArray.getLocatedAtPosition().getPositionDimensionOne();
						yPos = specimenArray.getLocatedAtPosition().getPositionDimensionTwo();
					}
				}
			}

			if (xPos == null || yPos == null || xPos.intValue() < 0 || yPos.intValue() < 0)
			{
				throw this.getBizLogicException(null, "errors.item.format", ApplicationProperties
						.getValue("array.positionInStorageContainer"));
			}

			if (specimenArray.getCreatedBy() == null
					|| specimenArray.getCreatedBy().getId() == null
					|| !validator.isValidOption(String
							.valueOf(specimenArray.getCreatedBy().getId())))
			{
				message = ApplicationProperties.getValue("array.user");
				throw this.getBizLogicException(null, "errors.item.required", message);
			}

			// validate capacity
			if (specimenArray.getCapacity() == null
					|| specimenArray.getCapacity().getOneDimensionCapacity() == null
					|| specimenArray.getCapacity().getTwoDimensionCapacity() == null)
			{
				throw this.getBizLogicException(null, "array.capacity.err.msg", "");
			}

			final List specimenClassList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_SPECIMEN_CLASS, null);
			final String specimenClass = specimenArrayType.getSpecimenClass();

			if (!this.isValidClassName(specimenClass))
			{
				throw this.getBizLogicException(null, "protocol.class.errMsg", "");
			}

			if (!Validator.isEnumeratedValue(specimenClassList, specimenClass))
			{
				throw this.getBizLogicException(null, "protocol.class.errMsg", "");
			}

			final Collection specimenTypes = specimenArrayType.getSpecimenTypeCollection();
			if (specimenTypes == null || specimenTypes.isEmpty())
			{
				throw this.getBizLogicException(null, "protocol.type.errMsg", "");
			}
			else
			{
				final Iterator itr = specimenTypes.iterator();
				while (itr.hasNext())
				{
					final String specimenType = (String) itr.next();
					if (!Validator.isEnumeratedValue(AppUtility.getSpecimenTypes(specimenClass),
							specimenType))
					{
						throw this.getBizLogicException(null, "protocol.type.errMsg", "");
					}
				}
			}
			/*
			 * Bug no. 7810 Bug Description : Incompatible specimen gets added
			 * to the specimen array
			 */
			final Collection specimenArrayContentCollection = specimenArray
					.getSpecimenArrayContentCollection();
			if (!specimenArrayContentCollection.isEmpty())
			{
				final Iterator iterator = specimenArrayContentCollection.iterator();
				while (iterator.hasNext())
				{
					final SpecimenArrayContent tempSpecimenArrayContent = (SpecimenArrayContent) iterator
							.next();
					final Specimen tempSpecimen = this.getSpecimen(dao, tempSpecimenArrayContent);
					if (specimenClass != null && tempSpecimen != null
							&& !specimenClass.equalsIgnoreCase(tempSpecimen.getClassName()))
					{
						message = this.getMessage(tempSpecimenArrayContent);
						throw this.getBizLogicException(null, "class.name.different", message);
					}
					if (specimenTypes != null && !specimenTypes.isEmpty() && tempSpecimen != null)
					{
						if (!specimenTypes.contains(tempSpecimen.getSpecimenType()))
						{
							message = this.getMessage(tempSpecimenArrayContent);
							throw this.getBizLogicException(null, "type.different", message);
						}
					}
				}
			}
			else
			{
				throw this.getBizLogicException(null, "spec.array.null", "");
			}
			return true;
		}
		catch (final DAOException daoExp)
		{
			this.logger.error(daoExp.getMessage(), daoExp);
			daoExp.printStackTrace();
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * @param dao : dao
	 * @param specimenArray : specimenArray
	 * @throws BizLogicException : BizLogicException
	 */
	private void retriveScId(DAO dao, SpecimenArray specimenArray) throws BizLogicException
	{
		try
		{
			String message = null;
			if (specimenArray.getLocatedAtPosition() != null
					&& specimenArray.getLocatedAtPosition().getParentContainer() != null
					&& specimenArray.getLocatedAtPosition().getParentContainer().getName() != null)
			{

				final StorageContainer storageContainerObj = (StorageContainer) HibernateMetaData
						.getProxyObjectImpl(specimenArray.getLocatedAtPosition()
								.getParentContainer());
				final String sourceObjectName = StorageContainer.class.getName();
				final String[] selectColumnName = {"id"};

				// String[] whereColumnName = {"name"};
				// String[] whereColumnCondition = {"="};
				// Object[] whereColumnValue =
				// {specimenArray.getLocatedAtPosition
				// ().getParentContainer().getName()};
				// String joinCondition = null;

				final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
				queryWhereClause.addCondition(new EqualClause("name", specimenArray
						.getLocatedAtPosition().getParentContainer().getName()));

				final List list = dao
						.retrieve(sourceObjectName, selectColumnName, queryWhereClause);

				if (!list.isEmpty())
				{
					storageContainerObj.setId((Long) list.get(0));
					specimenArray.getLocatedAtPosition().setParentContainer(storageContainerObj);
				}
				else
				{
					message = ApplicationProperties.getValue("array.positionInStorageContainer");
					throw this.getBizLogicException(null, "errors.invalid", message);
				}
			}
		}
		catch (final DAOException daoExp)
		{
			this.logger.error(daoExp.getMessage(), daoExp);
			daoExp.printStackTrace();
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * @param className : className
	 * @return boolean
	 */
	private boolean isValidClassName(String className)
	{
		if ((className != null) && (className.equalsIgnoreCase(Constants.CELL))
				|| (className.equalsIgnoreCase(Constants.MOLECULAR))
				|| (className.equalsIgnoreCase(Constants.FLUID))
				|| (className.equalsIgnoreCase(Constants.TISSUE)))
		{
			return true;
		}
		return false;
	}

	/**
	 * get Unique index to be appended to Name
	 * @return unique no. to be appended to array name
	 * @throws BizLogicException : BizLogicException
	 */
	public int getUniqueIndexForName() throws BizLogicException
	{
		try
		{
			final String sourceObjectName = "CATISSUE_CONTAINER";
			final String[] selectColumnName = {"max(IDENTIFIER) as MAX_IDENTIFIER"};
			return AppUtility.getNextUniqueNo(sourceObjectName, selectColumnName);
		}
		catch (final ApplicationException exp)
		{
			this.logger.error(exp.getMessage(), exp);
			exp.printStackTrace();
			throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp.getMsgValues());
		}
	}

	/**
	 * @param tempSpecimenArrayContent : tempSpecimenArrayContent
	 * @return the message to be displayed when exception occurs
	 */
	public String getMessage(SpecimenArrayContent tempSpecimenArrayContent)
	{
		final Specimen specimen = tempSpecimenArrayContent.getSpecimen();
		String msg = " ";
		if (specimen != null)
		{
			if ((specimen.getLabel() != null) && (!specimen.getLabel().trim().equals("")))
			{
				msg = "label " + specimen.getLabel();
			}
			else if ((specimen.getBarcode() != null) && (!specimen.getBarcode().trim().equals("")))
			{
				msg = "barcode " + specimen.getBarcode();
			}
		}
		return msg;
	}

	/**
	 * @param orderItemId : orderItemId
	 * @return NewSpecimenArrayOrderItem
	 * @throws BizLogicException : BizLogicException
	 */
	public NewSpecimenArrayOrderItem getNewSpecimenArrayOrderItem(Long orderItemId)
			throws BizLogicException
	{
		DAO dao = null;
		NewSpecimenArrayOrderItem newSpecimenArrayOrderItem = null;
		try
		{
			dao = this.openDAOSession(null);
			newSpecimenArrayOrderItem = (NewSpecimenArrayOrderItem) dao.retrieveById(
					NewSpecimenArrayOrderItem.class.getName(), orderItemId);

		}
		catch (final DAOException daoExp)
		{
			this.logger.error(daoExp.getMessage(), daoExp);
			daoExp.printStackTrace();
		}
		finally
		{
			this.closeDAOSession(dao);
		}

		return newSpecimenArrayOrderItem;

	}

	// END

	/**
	 * Called from DefaultBizLogic to get ObjectId for authorization check
	 * (non-Javadoc)
	 * @param dao : dao
	 * @param domainObject : domainObject
	 * @return String
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#getObjectId(edu.wustl.common.dao.DAO,
	 *      java.lang.Object)
	 */
	@Override
	public String getObjectId(DAO dao, Object domainObject)
	{
		SpecimenArray specimenArray = null;
		Specimen specimen = null;
		final StringBuffer stringBuffer = new StringBuffer();
		try
		{
			stringBuffer.append(Constants.COLLECTION_PROTOCOL_CLASS_NAME);
			Collection<SpecimenArrayContent> specimenArrayContentCollection = null;

			if (domainObject instanceof SpecimenArray)
			{
				specimenArray = (SpecimenArray) domainObject;
			}

			if (specimenArray.getSpecimenArrayContentCollection().isEmpty())
			{
				specimenArray = (SpecimenArray) dao.retrieveById(SpecimenArray.class.getName(),
						specimenArray.getId());
				specimenArrayContentCollection = specimenArray.getSpecimenArrayContentCollection();
			}
			else
			{
				specimenArrayContentCollection = specimenArray.getSpecimenArrayContentCollection();
			}

			for (final SpecimenArrayContent specimenArrayContent : specimenArrayContentCollection)
			{
				specimen = this.getSpecimen(dao, specimenArrayContent);
				if (specimen != null)
				{
					final SpecimenCollectionGroup scg = specimen.getSpecimenCollectionGroup();
					final CollectionProtocolRegistration cpr = scg
							.getCollectionProtocolRegistration();
					stringBuffer.append(Constants.UNDERSCORE).append(cpr.getCollectionProtocol().getId());
				}
			}

		}
		catch (final Exception e)
		{
			this.logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}

	/**
	 * To get PrivilegeName for authorization check from
	 * 'PermissionMapDetails.xml' (non-Javadoc)
	 * @param domainObject : domainObject
	 * @return String
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#getPrivilegeName(java.lang.Object)
	 */
	@Override
	protected String getPrivilegeKey(Object domainObject)
	{
		return edu.wustl.catissuecore.util.global.Constants.ADD_EDIT_SPECIMEN_ARRAY;
	}

	/**
	 * (non-Javadoc)
	 * @param dao : dao
	 * @param domainObject : domainObject
	 * @param sessionDataBean : sessionDataBean
	 * @throws BizLogicException : BizLogicException
	 * @return boolean
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#isAuthorized(edu.wustl.common.dao.DAO,
	 *      java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	 */
	@Override
	public boolean isAuthorized(DAO dao, Object domainObject, SessionDataBean sessionDataBean)
			throws BizLogicException
	{

		boolean isAuthorized = false;

		String protectionElementName = null;
		SpecimenArray specimenArray = null;
		Specimen specimen = null;
		SpecimenPosition specimenPosition = null;
		try
		{
			if (sessionDataBean != null && sessionDataBean.isAdmin())
			{
				return true;
			}

			// Get the base object id against which authorization will take
			// place
			protectionElementName = this.getObjectId(dao, domainObject);
			Site site = null;
			StorageContainer storContainer = null;
			// Handle for SERIAL CHECKS, whether user has access to source site
			// or not
			if (domainObject instanceof SpecimenArray)
			{
				specimenArray = (SpecimenArray) domainObject;
			}

			final Collection<SpecimenArrayContent> specimenArrayContentCollection = specimenArray
					.getSpecimenArrayContentCollection();

			for (final SpecimenArrayContent specimenArrayContent : specimenArrayContentCollection)
			{
				try
				{
					specimen = this.getSpecimen(dao, specimenArrayContent);

					if (specimen == null)
					{
						continue;
					}
					if (specimen.getSpecimenPosition() != null)
					{
						storContainer = specimen.getSpecimenPosition().getStorageContainer();
					}

					if (specimen.getSpecimenPosition() != null
							&& specimen.getSpecimenPosition().getStorageContainer().getSite() == null)
					{
						storContainer = (StorageContainer) dao.retrieveById(StorageContainer.class.getName(),
								specimen.getSpecimenPosition().getStorageContainer().getId());
					}

					specimenPosition = specimen.getSpecimenPosition();

					if (specimenPosition != null) // Specimen is NOT Virtually
					// Located
					{
						site = storContainer.getSite();
						final Set<Long> siteIdSet = new UserBizLogic()
								.getRelatedSiteIds(sessionDataBean.getUserId());

						if (!siteIdSet.contains(site.getId()))
						{
							// bug 11611 and 11659
							throw AppUtility.getUserNotAuthorizedException(Constants.Association,
									site.getObjectId(), domainObject.getClass().getSimpleName());
						}
					}
				}
				catch (final DAOException e)
				{
					this.logger.error(e.getMessage(), e);
					e.printStackTrace();
				}
			}

			// Get the required privilege name which we would like to check for
			// the logged in user.
			final String privilegeName = this.getPrivilegeName(domainObject);
			final PrivilegeCache privilegeCache = PrivilegeManager.getInstance().getPrivilegeCache(
					sessionDataBean.getUserName());

			// Checking whether the logged in user has the required privilege on
			// the given protection element
			final String[] prArray = protectionElementName.split("_");
			final String baseObjectId = prArray[0];
			String objId = "";
			for (int i = 1; i < prArray.length; i++)
			{
				objId = baseObjectId + "_" + prArray[i];
				isAuthorized = privilegeCache.hasPrivilege(objId, privilegeName);
				if (!isAuthorized)
				{
					break;
				}
			}

			if (isAuthorized)
			{
				return isAuthorized;
			}
			else
			// Check for ALL CURRENT & FUTURE CASE
			{
				isAuthorized = AppUtility.checkOnCurrentAndFuture(sessionDataBean,
						protectionElementName, privilegeName);
			}
			if (!isAuthorized)
			{
				// throw Utility.getUserNotAuthorizedException(privilegeName,
				// protectionElementName);
				throw AppUtility.getUserNotAuthorizedException(privilegeName,
						protectionElementName, domainObject.getClass().getSimpleName());
			}

		}
		catch (final SMException e)
		{
			this.logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw this.getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
		catch (final ApplicationException e)
		{
			this.logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new BizLogicException(e.getErrorKey(), e, e.getMsgValues());
		}
		return isAuthorized;
	}

	/**
	 * To check weather the Container to display can holds the given
	 * specimenArrayTypeId.
	 * @param specimenArrayTypeId
	 *            The Specimen Array Type Id.
	 * @param storageContainer
	 *            The StorageContainer reference to be displayed on the page.
	 * @return true if the given container can hold the specimenArrayType.
	 * @throws BizLogicException throws BizLogicException
	 */
	public boolean canHoldSpecimenArrayType(int specimenArrayTypeId,
			StorageContainer storageContainer) throws BizLogicException
	{

		boolean canHold = true;
		final Collection specimenArrayTypes = (Collection) this.retrieveAttribute(
				StorageContainer.class.getName(), storageContainer.getId(),
				"elements(holdsSpecimenArrayTypeCollection)");
		final Iterator itr = specimenArrayTypes.iterator();
		canHold = false;
		while (itr.hasNext())
		{
			final SpecimenArrayType specimenarrayType = (SpecimenArrayType) itr.next();
			final long arraytypeId = specimenarrayType.getId().longValue();

			if (arraytypeId == Constants.ALL_SPECIMEN_ARRAY_TYPE_ID
					|| arraytypeId == specimenArrayTypeId)
			{
				return true;
			}
		}
		return canHold;
	}

}