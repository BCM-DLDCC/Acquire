/**
 * <p>
 * Description: NewSpecimenBizLogicHDAO is used to add new specimen information
 * into the database using Hibernate.
 * </p>
 * Copyright: Copyright (c) year Company: Washington University, School of
 * Medicine, St. Louis.
 * @version Suite V1.1 Code re factoring on 15th May 2008
 */

package edu.wustl.catissuecore.bizlogic;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

import edu.wustl.catissuecore.TaskTimeCalculater;
import edu.wustl.catissuecore.actionForm.NewSpecimenForm;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.AbstractSpecimenCollectionGroup;
import edu.wustl.catissuecore.domain.Biohazard;
import edu.wustl.catissuecore.domain.CollectionEventParameters;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolEvent;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.ConsentTierStatus;
import edu.wustl.catissuecore.domain.ContainerPosition;
import edu.wustl.catissuecore.domain.DisposalEventParameters;
import edu.wustl.catissuecore.domain.ExternalIdentifier;
import edu.wustl.catissuecore.domain.MolecularSpecimen;
import edu.wustl.catissuecore.domain.ReceivedEventParameters;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCharacteristics;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;
import edu.wustl.catissuecore.domain.SpecimenEventParameters;
import edu.wustl.catissuecore.domain.SpecimenPosition;
import edu.wustl.catissuecore.domain.SpecimenRequirement;
import edu.wustl.catissuecore.domain.StorageContainer;
import edu.wustl.catissuecore.domain.User;
import edu.wustl.catissuecore.namegenerator.BarcodeGenerator;
import edu.wustl.catissuecore.namegenerator.BarcodeGeneratorFactory;
import edu.wustl.catissuecore.namegenerator.LabelException;
import edu.wustl.catissuecore.namegenerator.LabelGenException;
import edu.wustl.catissuecore.namegenerator.LabelGenerator;
import edu.wustl.catissuecore.namegenerator.LabelGeneratorFactory;
import edu.wustl.catissuecore.namegenerator.NameGeneratorException;
import edu.wustl.catissuecore.util.ApiSearchUtil;
import edu.wustl.catissuecore.util.ConsentUtil;
import edu.wustl.catissuecore.util.EventsUtil;
import edu.wustl.catissuecore.util.IdComparator;
import edu.wustl.catissuecore.util.MultipleSpecimenValidationUtil;
import edu.wustl.catissuecore.util.Position;
import edu.wustl.catissuecore.util.SpecimenUtil;
import edu.wustl.catissuecore.util.StorageContainerUtil;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.catissuecore.util.global.Variables;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.HibernateMetaData;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.exception.UserNotAuthorizedException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.locator.CSMGroupLocator;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

// TODO: Auto-generated Javadoc
/**
 * NewSpecimenHDAO is used to add new specimen information into the database
 * using hibernate.
 */
public class NewSpecimenBizLogic extends CatissueDefaultBizLogic
{

	/** Logger added for Specimen class. */
	private static final Logger LOGGER = Logger.getCommonLogger(NewSpecimenBizLogic.class);

	/** containerHoldsSpecimenClasses. */
	private Map<Long, Collection<String>> containerHoldsSpecimenClasses = new HashMap<Long, Collection<String>>();

	/** containerHoldsCPs. */
	private Map<Long, Collection<CollectionProtocol>> containerHoldsCPs = new HashMap<Long, Collection<CollectionProtocol>>();

	/** storageContainerIds. */
	private HashSet<String> storageContainerIds = new HashSet<String>();

	/** The storage positions. */
	private String storagePositions = "";

	/** cpbased. */
	private boolean cpbased = false;

	/**
	 * Pre-insert method.
	 *
	 * @param obj the obj
	 * @param dao the dao
	 * @param sessionDataBean the session data bean
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	protected void preInsert(final Object obj, final DAO dao, final SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		this.storageContainerIds = new HashSet<String>();
		storagePositions = "";
	}

	/**
	 * Saves the Specimen object in the database.
	 *
	 * @param obj The Specimen object to be saved.
	 * @param sessionDataBean The session in which the object is saved.
	 * @param dao DAO object
	 *
	 * @throws BizLogicException Database related Exception
	 */
	protected void insert(final Object obj, final DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		try
		{
			final Specimen specimen = (Specimen) obj;
			this.setParent(dao, specimen);
			//bug 15392 start
			Integer pos1 = null;
			Integer pos2 = null;
			if(!storagePositions.trim().equals(""))
			{
				final String positionStr = storagePositions;
				final String positions[] = positionStr.split(",");
				final String strContainerName = positions[0];
				if((!Validator.isEmpty(strContainerName.trim()))
					&& (specimen.getSpecimenPosition() != null
							&& !Validator.isEmpty(specimen.getSpecimenPosition().
									getStorageContainer().getName()))
								&& (strContainerName.equals(specimen.getSpecimenPosition().
									getStorageContainer().getName())))
				{
					pos1 = Integer.valueOf(positions[1]);
					pos2 = Integer.valueOf(positions[2]);
				}
			}
			this.populateDomainObjectToInsert(dao, sessionDataBean, specimen,pos1,pos2);
			specimen.doRoundOff();
			checkLabel(specimen);
			dao.insert(specimen);
			if(specimen.getSpecimenPosition()!=null)
			{
				final StringBuffer posBuffer =  new StringBuffer();
				posBuffer.append(specimen.getSpecimenPosition().getStorageContainer().getName());
				posBuffer.append(',');
				posBuffer.append(specimen.getSpecimenPosition().getPositionDimensionOne());
				posBuffer.append(',');
				posBuffer.append(specimen.getSpecimenPosition().getPositionDimensionTwo());
				storagePositions = posBuffer.toString();
			}
			//Bug 15392 end
		}
		catch (final ApplicationException exp)
		{
			LOGGER.error(exp.getMessage(), exp);
			throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp.getMsgValues());
		}
	}

	private void checkLabel(Specimen specimen)throws BizLogicException
	{
		if(specimen.getCollectionStatus() != null && specimen.getCollectionStatus().equals(Constants.COLLECTION_STATUS_COLLECTED) && Validator.isEmpty(specimen.getLabel()))
		{
			throw this.getBizLogicException(null, "label.mandatory", "");
		}

	}

	/**
	 * Sets the parent.
	 *
	 * @param dao DAO Object
	 * @param specimen Specimen Object
	 *
	 * @throws BizLogicException : BizLogicException
	 * @throws DAOException : DAOException
	 */
	private void setParent(final DAO dao,final Specimen specimen) throws BizLogicException, DAOException
	{
		Specimen parentSpecimen = (Specimen) specimen.getParentSpecimen();
		if (parentSpecimen == null)
		{
			this.setSCGToSpecimen(specimen, dao);
		}
		else
		{
			if (parentSpecimen.getId() != null)
			{
				/*parentSpecimen = (Specimen) dao.retrieveById(Specimen.class.getName(), specimen
						.getParentSpecimen().getId());*/
				final String sourceObjectName = Specimen.class.getName();
				final String[] selectColumnName = {"activityStatus", "createdOn",
						"specimenCollectionGroup.id", "specimenCollectionGroup.activityStatus",
						"label", "pathologicalStatus", "specimenCharacteristics.id",
						"availableQuantity", "collectionStatus","barcode"};
				final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
				queryWhereClause.addCondition(new EqualClause("id", parentSpecimen.getId()));
				final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
				if (list.isEmpty())
				{
					throw this.getBizLogicException(null, "invalid.parent.specimen.identifier",
							parentSpecimen.getId().toString());

				}
				else if(Status.ACTIVITY_STATUS_DISABLED.toString().equals(((Object[])list.get(0))[0]))
				{
					throw this.getBizLogicException(null, "error.object.disabled",
							Constants.SPECIMEN);
				}
				else
				{
					this.retrieveParentSpecimenDetailsFromId(dao, parentSpecimen, list);
				}
				parentSpecimen = this.retrieveParentSpecimenCollectionTypeData(dao, parentSpecimen);
			}
			else if (parentSpecimen.getLabel() != null)
			{
				parentSpecimen = this.getParentSpecimenByLabel(dao, parentSpecimen);
			}
			specimen.setParentSpecimen(parentSpecimen);
			this.checkStatus(dao, parentSpecimen, Constants.SPECIMEN);
		}
	}

	/**
	 * retrieve Parent Specimen Details From Id.
	 *
	 * @param dao the dao
	 * @param parentSpecimen the parent specimen
	 * @param list the list
	 *
	 * @throws DAOException the DAO exception
	 */
	private void retrieveParentSpecimenDetailsFromId(final DAO dao,final Specimen parentSpecimen,
			final List list) throws DAOException
	{
		final Object[] valArr = (Object[]) list.get(0);
		if (valArr != null)
		{
			parentSpecimen.setActivityStatus((String) valArr[0]);
			parentSpecimen.setCreatedOn((Date) valArr[1]);

			SpecimenCollectionGroup scg = null;
			if (parentSpecimen.getSpecimenCollectionGroup() == null)
			{
				scg = new SpecimenCollectionGroup();
			}
			else
			{
				scg = parentSpecimen.getSpecimenCollectionGroup();

			}
			scg.setId((Long) valArr[2]);
			scg.setActivityStatus((String) valArr[3]);
			parentSpecimen.setSpecimenCollectionGroup(scg);

			parentSpecimen.setLabel((String) valArr[4]);
			parentSpecimen.setPathologicalStatus((String) valArr[5]);

			final SpecimenCharacteristics characteristics = (SpecimenCharacteristics) dao
					.retrieveById(SpecimenCharacteristics.class.getName(), (Long) valArr[6]);
			if (characteristics != null)
			{
				parentSpecimen.setSpecimenCharacteristics(characteristics);
			}
			parentSpecimen.setAvailableQuantity((Double) valArr[7]);
			parentSpecimen.setCollectionStatus((String) valArr[8]);
			parentSpecimen.setBarcode((String) valArr[9]);
			this.retrieveStorageContainerInfo(dao, parentSpecimen);
		}
	}

	/**
	 * Method to retrieve storage container info.
	 *
	 * @param dao the dao
	 * @param parentSpecimen the parent specimen
	 *
	 * @return the specimen
	 *
	 * @throws DAOException the DAO exception
	 */
	private Specimen retrieveStorageContainerInfo(final DAO dao,final Specimen parentSpecimen)
			throws DAOException
	{
		final String sourceObjectName = Specimen.class.getName();
		final String[] selectColumnName = {"specimenPosition.id",
				"specimenPosition.storageContainer.id", "specimenPosition.storageContainer.name"};
		final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.addCondition(new EqualClause("id", parentSpecimen.getId()));
		final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
		if (!list.isEmpty())
		{
			final Object[] valArr = (Object[]) list.get(0);
			if (valArr != null)
			{
				SpecimenPosition position = null;
				if (parentSpecimen.getSpecimenPosition() == null)
				{
					position = new SpecimenPosition();
				}
				else
				{
					position = parentSpecimen.getSpecimenPosition();

				}
				position.setId((Long) valArr[0]);

				StorageContainer storageContainer = null;
				if (position.getStorageContainer() == null)
				{
					storageContainer = new StorageContainer();
				}
				else
				{
					storageContainer = parentSpecimen.getSpecimenPosition().getStorageContainer();

				}
				storageContainer.setId((Long) valArr[1]);
				storageContainer.setName((String) valArr[2]);
				position.setStorageContainer(storageContainer);
				parentSpecimen.setSpecimenPosition(position);
			}
		}
		return parentSpecimen;
	}

	/**
	 * Retrieve Parent Specimen Collection Type data.
	 *
	 * @param dao the dao
	 * @param parentSpecimen the parent specimen
	 *
	 * @return Specimen object
	 *
	 * @throws DAOException the DAO exception
	 * @throws BizLogicException the biz logic exception
	 */
	private Specimen retrieveParentSpecimenCollectionTypeData(final DAO dao,final Specimen parentSpecimen)
			throws DAOException, BizLogicException
	{
		Collection childSpecimenColl = parentSpecimen.getChildSpecimenCollection();
		if (isChildSpeCollectionEmpty(childSpecimenColl))
		{
			childSpecimenColl = (Collection) this.retrieveAttribute(dao, Specimen.class,
					parentSpecimen.getId(), "elements(childSpecimenCollection)");
			parentSpecimen.setChildSpecimenCollection(childSpecimenColl);
		}
		/*else
		{
			throw new DAOException("Error in ChildSpecimenCollection retrieval for
			Parent Specimen : " + parentSpecimen.getId());
		}*/

		Collection consentTierStatusCollection = parentSpecimen.getConsentTierStatusCollection();
		if (isChildSpeCollectionEmpty(consentTierStatusCollection))
		{
			consentTierStatusCollection = (Collection) this.retrieveAttribute(dao, Specimen.class,
					parentSpecimen.getId(), "elements(consentTierStatusCollection)");
			parentSpecimen.setConsentTierStatusCollection(consentTierStatusCollection);
		}
		/*else
		{
			throw this.getBizLogicException(null, "error.parent.specimen.consent.tier",
					parentSpecimen.getId().toString());
		}*/

		Collection bioHazardColl = parentSpecimen.getBiohazardCollection();
		if (isChildSpeCollectionEmpty(bioHazardColl))
		{
			bioHazardColl = (Collection) this.retrieveAttribute(dao, Specimen.class,
					parentSpecimen.getId(), "elements(biohazardCollection)");
			parentSpecimen.setBiohazardCollection(bioHazardColl);
		}
		/*else
		{
			throw this.getBizLogicException(null, "error.parent.specimen.biohazard",
					parentSpecimen.getId().toString());
		}*/

		Collection specimenEventCollection = parentSpecimen.getSpecimenEventCollection();
		if (isChildSpeCollectionEmpty(specimenEventCollection))
		{
			specimenEventCollection = (Collection) this.retrieveAttribute(dao, Specimen.class,
					parentSpecimen.getId(), "elements(specimenEventCollection)");
			parentSpecimen.setSpecimenEventCollection(specimenEventCollection);
		}
		/*else
		{
			throw new DAOException("Error in SpecimenEventCollection retrieval
			for Parent Specimen : " + parentSpecimen.getId());
		}*/
		return parentSpecimen;
	}

	/**
	 * Checks if is child spe collection empty.
	 *
	 * @param childSpecimenCollection the child specimen collection
	 *
	 * @return true, if checks if is child spe collection empty
	 */
	private boolean isChildSpeCollectionEmpty(Collection childSpecimenCollection)
	{
		return childSpecimenCollection == null || childSpecimenCollection.isEmpty();
	}

	/**
	 * Populate domain object to insert.
	 *
	 * @param specimen Specimen Object to be save
	 * @param sessionDataBean The session in which the object is saved.
	 * @param dao DAO object
	 * @param pos1 the pos1
	 * @param pos2 the pos2
	 *
	 * @throws BizLogicException Database related Exception
	 * @throws DAOException : DAOException
	 * User Not Authorized Exception
	 * @throws SMException : SMException
	 * Bug 15392
	 */
	private void populateDomainObjectToInsert(final DAO dao, final SessionDataBean sessionDataBean,
			Specimen specimen,Integer pos1,Integer pos2) throws BizLogicException, DAOException, SMException
	{
		this.setSpecimenCreatedOnDate(specimen);
		this.setSpecimenParent(specimen, dao);
		// bug no. 4265
		if (specimen.getLineage() != null && specimen.getLineage().equalsIgnoreCase("Derived")
				&& specimen.getDisposeParentSpecimen())
		{
			this.checkParentSpecimenDisposal(sessionDataBean, specimen, dao,
					Constants.DERIVED_SPECIMEN_DISPOSAL_REASON);
		}
		// allocatePositionForSpecimen(specimen);
		this.setStorageLocationToNewSpecimen(dao, specimen, sessionDataBean, true,pos1,pos2);
		this.setSpecimenAttributes(dao, specimen, sessionDataBean);
		this.generateLabel(specimen);
		this.generateBarCode(specimen);
		this.insertChildSpecimens(specimen, dao, sessionDataBean,pos1,pos2);
	}

	/**
	 * Calculate quantity.
	 *
	 * @param specimen Specimen object to insert
	 */
	private void calculateQuantity(Specimen specimen)
	{
		final Specimen parentSpecimen = (Specimen) specimen.getParentSpecimen();
		Double availableQuantity = parentSpecimen.getAvailableQuantity().doubleValue();
		final DecimalFormat dFormat = new DecimalFormat("#.000");
		availableQuantity = availableQuantity - specimen.getAvailableQuantity();
		availableQuantity = Double.parseDouble(dFormat.format(availableQuantity));
		parentSpecimen.setAvailableQuantity(availableQuantity);
		if (availableQuantity <= 0)
		{
			parentSpecimen.setIsAvailable(Boolean.FALSE);
			parentSpecimen.setAvailableQuantity(new Double(0));
		}
		else
		{
			// bug 11174
			parentSpecimen.setIsAvailable(Boolean.TRUE);
			if (Constants.COLLECTION_STATUS_COLLECTED.equals(specimen.getCollectionStatus()))
			{
				specimen.setIsAvailable(Boolean.TRUE);
			}
		}
	}

	/**
	 * Sets the parent specimen data.
	 *
	 * @param specimen Parent Specimen
	 *
	 * @throws BizLogicException Database related Exception This method retrieves the parent
	 * specimen events and sets them in the parent specimen
	 */
	private void setParentSpecimenData(Specimen specimen)
	{
		final Specimen parentSpecimen = (Specimen) specimen.getParentSpecimen();
		// 11177 S
		if (specimen.getPathologicalStatus() == null
				|| Constants.DOUBLE_QUOTES.equals(specimen.getPathologicalStatus()))
		{
			specimen.setPathologicalStatus(parentSpecimen.getPathologicalStatus());
		}
		//specimen.setPathologicalStatus(parentSpecimen.getPathologicalStatus())
		// ;
		// 11177 E
		this.setParentCharacteristics(parentSpecimen, specimen);
		this.setConsentTierStatus(specimen, parentSpecimen.getConsentTierStatusCollection());
		this.setParentBioHazard(parentSpecimen, specimen);
	}

	/**
	 * Sets the parent bio hazard.
	 *
	 * @param specimen Current Specimen
	 * @param parentSpecimen Parent Specimen Object
	 */
	private void setParentBioHazard(Specimen parentSpecimen, Specimen specimen)
	{
		final Set<Biohazard> set = new HashSet<Biohazard>();
		final Collection<Biohazard> biohazardCollection = parentSpecimen.getBiohazardCollection();
		if (biohazardCollection != null)
		{
			final Iterator<Biohazard> iterator = biohazardCollection.iterator();
			while (iterator.hasNext())
			{
				final Biohazard hazard = (Biohazard) iterator.next();
				set.add(hazard);
			}
		}
		specimen.setBiohazardCollection(set);
	}

	/**
	 * Sets the created on date.
	 *
	 * @param specimen This method sets the created on date = collection date
	 */
	private void setCreatedOnDate(Specimen specimen)
	{
		final Collection<SpecimenEventParameters> specimenEventsCollection = specimen
				.getSpecimenEventCollection();
		if (specimenEventsCollection != null)
		{
			final Iterator<SpecimenEventParameters> specimenEventsCollectionIterator = specimenEventsCollection
					.iterator();
			while (specimenEventsCollectionIterator.hasNext())
			{
				final Object eventObject = specimenEventsCollectionIterator.next();
				if (eventObject instanceof CollectionEventParameters)
				{
					final CollectionEventParameters collEventParam = (CollectionEventParameters) eventObject;
					specimen.setCreatedOn(collEventParam.getTimestamp());
				}
			}
		}
	}

	/**
	 * Sets the default events to specimen.
	 *
	 * @param specimen Set default events to specimens
	 * @param sessionDataBean Session data bean This method sets the default events to
	 * specimens if they are null
	 */
	private void setDefaultEventsToSpecimen(Specimen specimen, SessionDataBean sessionDataBean)
	{
		final Collection<SpecimenEventParameters> specimenEventColl = new HashSet<SpecimenEventParameters>();
		final User user = new User();
		user.setId(sessionDataBean.getUserId());
		final CollectionEventParameters collectionEventParameters = EventsUtil
				.populateCollectionEventParameters(user);
		collectionEventParameters.setSpecimen(specimen);
		specimenEventColl.add(collectionEventParameters);

		final ReceivedEventParameters receivedEventParameters = EventsUtil
				.populateReceivedEventParameters(user);
		receivedEventParameters.setSpecimen(specimen);
		specimenEventColl.add(receivedEventParameters);

		specimen.setSpecimenEventCollection(specimenEventColl);
	}

	/**
	 * This method gives the error message. This method is override for
	 * customizing error message
	 *
	 * @param obj - Object
	 * @param operation Type of operation
	 * @param daoException Database related Exception
	 *
	 * @return formatedException returns formated exception
	 */
	public String getErrorMessage(DAOException daoException, Object obj, String operation)
	{
		if (obj instanceof HashMap)
		{
			obj = new Specimen();
		}
		String formatedException = this.formatException(daoException.getWrapException(), obj,
				operation);

		if (formatedException == null)
		{
			formatedException = daoException.getMessage();
		}
		return formatedException;
	}

	/**
	 * Dispose specimen.
	 *
	 * @param sessionDataBean : sessionDataBean
	 * @param specimen  : specimen
	 * @param disposalReason  : disposalReason
	 *
	 * @throws BizLogicException  : BizLogicException
	 * @throws UserNotAuthorizedException : UserNotAuthorizedException
	 */
	public void disposeSpecimen(SessionDataBean sessionDataBean, AbstractSpecimen specimen,
			String disposalReason) throws BizLogicException, UserNotAuthorizedException
	{
		final DisposalEventParameters disposalEvent = this.createDisposeEvent(sessionDataBean,
				specimen, disposalReason);
		final SpecimenEventParametersBizLogic sepBizLogic = new SpecimenEventParametersBizLogic();
		sepBizLogic.insert(disposalEvent, sessionDataBean, 0);
		((Specimen) specimen).setIsAvailable(Boolean.FALSE);
		specimen.setActivityStatus(Status.ACTIVITY_STATUS_CLOSED.toString());
	}

	/**
	 * Dispose specimen.
	 *
	 * @param disposalReason : disposalReason
	 * @param dao DAO object
	 * @param sessionDataBean Session details
	 * @param specimen parent specimen object
	 *
	 * @throws BizLogicException the biz logic exception
	 * @throws UserNotAuthorizedException User is not Authorized
	 */
	public void disposeSpecimen(SessionDataBean sessionDataBean, AbstractSpecimen specimen,
			DAO dao, String disposalReason) throws BizLogicException, UserNotAuthorizedException
	{
		final DisposalEventParameters disposalEvent = this.createDisposeEvent(sessionDataBean,
				specimen, disposalReason);
		final SpecimenEventParametersBizLogic sepBizLogic = new SpecimenEventParametersBizLogic();
		sepBizLogic.insert(disposalEvent, dao, sessionDataBean);
		((Specimen) specimen).setIsAvailable(Boolean.FALSE);
		specimen.setActivityStatus(Status.ACTIVITY_STATUS_CLOSED.toString());
	}

	/**
	 * Creates the dispose event.
	 *
	 * @param disposalReason : disposalReason
	 * @param sessionDataBean : sessionDataBean
	 * @param specimen : specimen
	 *
	 * @return DisposalEventParameters
	 */

	private DisposalEventParameters createDisposeEvent(SessionDataBean sessionDataBean,
			AbstractSpecimen specimen, String disposalReason)
	{
		final DisposalEventParameters disposalEvent = new DisposalEventParameters();
		disposalEvent.setSpecimen(specimen);
		disposalEvent.setReason(disposalReason);
		disposalEvent.setTimestamp(new Date(System.currentTimeMillis()));
		final User user = new User();
		user.setId(sessionDataBean.getUserId());
		disposalEvent.setUser(user);
		disposalEvent.setActivityStatus(Status.ACTIVITY_STATUS_CLOSED.toString());
		return disposalEvent;
	}

	/**
	 * Sets the specimen created on date.
	 *
	 * @param specimen : specimen
	 */
	private void setSpecimenCreatedOnDate(Specimen specimen)
	{
		final Specimen parentSpecimen = (Specimen) specimen.getParentSpecimen();
		if (specimen.getCreatedOn() == null)
		{
			if ((specimen.getParentSpecimen() == null))
			{
				this.setCreatedOnDate(specimen);
			}
			else
			{
				if (parentSpecimen.getCreatedOn() != null)
				{
					specimen.setCreatedOn(Calendar.getInstance().getTime());
				}
			}
		}
	}

	/**
	 * Sets the quantity.
	 *
	 * @param specimen  : specimen
	 */
	private void setQuantity(Specimen specimen)
	{
		final Double avQty = specimen.getAvailableQuantity();
		if (avQty != null && avQty == 0 && Constants.COLLECTION_STATUS_COLLECTED.equals(specimen.getCollectionStatus()))
		{
//			if (Constants.COLLECTION_STATUS_COLLECTED.equals(specimen.getCollectionStatus()))
//			{
				specimen.setAvailableQuantity(specimen.getInitialQuantity());
				specimen.setIsAvailable(Boolean.TRUE);
//			}
		}
		if (Constants.ALIQUOT.equals(specimen.getLineage()))
		{
			this.calculateQuantity(specimen);
		}
	}

	/**
	 * Sets the specimen events.
	 *
	 * @param specimen : specimen
	 * @param sessionDataBean  : sessionDataBean
	 */
	private void setSpecimenEvents(Specimen specimen, SessionDataBean sessionDataBean)
	{
		if (specimen.getCollectionStatus() != null
				&& Constants.COLLECTION_STATUS_PENDING.equals(specimen.getCollectionStatus()))
		{
			specimen.setSpecimenEventCollection(null);
		}
		else
		{
			final Specimen parentSpecimen = (Specimen) specimen.getParentSpecimen();
			if (specimen.getParentSpecimen() == null)
			{
				final Collection<SpecimenEventParameters> specimenEventColl = specimen
						.getSpecimenEventCollection();
				if (sessionDataBean != null
						&& (specimenEventColl == null || specimenEventColl.isEmpty()))
				{
					this.setDefaultEventsToSpecimen(specimen, sessionDataBean);
				}
			}
			else
			{
				if (specimen.getSpecimenEventCollection() == null
						|| specimen.getSpecimenEventCollection().isEmpty())
				{
					specimen.setSpecimenEventCollection(this.populateDeriveSpecimenEventCollection(
							parentSpecimen, specimen));
				}
			}
		}
	}

	/**
	 * Insert child specimens.
	 *
	 * @param specimen Specimen Object
	 * @param dao DAO object
	 * @param sessionDataBean Session data
	 * @param pos1 the pos1
	 * @param pos2 the pos2
	 *
	 * @throws BizLogicException Database related exception
	 * @throws SMException Security related exception
	 * @throws DAOException : DAOException
	 * Bug 15392
	 */
	private void insertChildSpecimens(Specimen specimen, DAO dao, SessionDataBean sessionDataBean,Integer pos1,Integer pos2)
			throws BizLogicException, DAOException, SMException
	{
		final Collection<AbstractSpecimen> childSpecimenCollection = specimen
				.getChildSpecimenCollection();
		if(childSpecimenCollection != null)
		{
			final Iterator<AbstractSpecimen> iterator = childSpecimenCollection.iterator();
			while (iterator.hasNext())
			{
				final Specimen childSpecimen = (Specimen) iterator.next();
				this.populateDomainObjectToInsert(dao, sessionDataBean, childSpecimen,pos1,pos2);
			}
		}
	}

	/**
	 * Sets the parent characteristics.
	 *
	 * @param parentSpecimen Parent Specimen Object
	 * @param childSpecimen Child Specimen Object
	 */
	private void setParentCharacteristics(Specimen parentSpecimen, Specimen childSpecimen)
	{
		SpecimenCharacteristics characteristics = null;
		if (Constants.ALIQUOT.equals(childSpecimen.getLineage()))
		{
			childSpecimen.setSpecimenCharacteristics(parentSpecimen.getSpecimenCharacteristics());
		}
		else
		{
			final SpecimenCharacteristics parentSpecChar = parentSpecimen
					.getSpecimenCharacteristics();
			if (parentSpecChar != null)
			{
				characteristics = new SpecimenCharacteristics();
				characteristics.setTissueSide(parentSpecChar.getTissueSide());
				characteristics.setTissueSite(parentSpecChar.getTissueSite());
			}
			childSpecimen.setSpecimenCharacteristics(characteristics);
		}
	}

	/**
	 * Generate bar code.
	 *
	 * @param specimen Specimen Object
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void generateBarCode(Specimen specimen) throws BizLogicException
	{
		if (edu.wustl.catissuecore.util.global.Variables.isSpecimenBarcodeGeneratorAvl &&
				(specimen.getBarcode() == null || "".equals(specimen.getBarcode())))
		{
			try
			{
				final BarcodeGenerator spBarcodeGenerator = BarcodeGeneratorFactory
						.getInstance(Constants.SPECIMEN_BARCODE_GENERATOR_PROPERTY_NAME);
				spBarcodeGenerator.setBarcode(specimen);
			}
			catch (final NameGeneratorException e)
			{
				LOGGER.error(e.getMessage(), e);
				throw this.getBizLogicException(e, "name.generator.exp", "");
			}
	    }
	 }

	/**
	 * Sets the default external identifiers.
	 *
	 * @param specimen Specimen Object
	 * Collection of external identifier.
	 */
	private void setDefaultExternalIdentifiers(Specimen specimen)
	{
		Collection<ExternalIdentifier> extIdntyColl = specimen.getExternalIdentifierCollection();
		if (extIdntyColl != null)
		{
			if (extIdntyColl.isEmpty()) // Dummy entry added for query
			{
				this.setEmptyExternalIdentifier(specimen, extIdntyColl);
			}
			else
			{
				this.setSpecimenToExternalIdentifier(specimen, extIdntyColl);
			}
		}
		else
		{
			// Dummy entry added for query.
			extIdntyColl = new HashSet<ExternalIdentifier>();
			this.setEmptyExternalIdentifier(specimen, extIdntyColl);
			specimen.setExternalIdentifierCollection(extIdntyColl);
		}
	}

	/**
	 * Sets the specimen parent.
	 *
	 * @param specimen Specimen Object
	 * @param dao DAO object
	 * Parent Specimen Object
	 *
	 * @throws BizLogicException Database related Exception
	 */
	private void setSpecimenParent(Specimen specimen, DAO dao) throws BizLogicException
	{
		try
		{
			final Specimen parentSpecimen = (Specimen) specimen.getParentSpecimen();
			if (parentSpecimen != null)
			{
				specimen.setParentSpecimen(parentSpecimen);
				parentSpecimen.getChildSpecimenCollection().add(specimen);
				specimen.setSpecimenCollectionGroup(parentSpecimen.getSpecimenCollectionGroup());
				this.setParentSpecimenData(specimen);
			}
			// Bug 11481 S
			final String lineage = specimen.getLineage();
			final CollectionProtocol protocol = new CollectionProtocol();

			Long colpId;
			String activityStatus = null;
			if (specimen.getSpecimenCollectionGroup().getCollectionProtocolRegistration() == null)
			{
				activityStatus = this.getCollectionProtocolIdAndActivityStatus(specimen, dao, protocol,
						activityStatus);
			}
			else
			{
				colpId = (Long) specimen.getSpecimenCollectionGroup()
				.getCollectionProtocolRegistration().getCollectionProtocol().getId();
		activityStatus = specimen.getSpecimenCollectionGroup()
				.getCollectionProtocolRegistration().getCollectionProtocol()
				.getActivityStatus();
		protocol.setId(colpId);
		protocol.setActivityStatus(activityStatus);
			}
			if (lineage != null && !lineage.equalsIgnoreCase("New"))
			{
				final String parentCollStatus = ((Specimen) specimen.getParentSpecimen())
						.getCollectionStatus();
				if (!parentCollStatus.equalsIgnoreCase(Constants.COLLECTION_STATUS_COLLECTED))
				{
					this.checkStatus(dao, protocol, "Collection Protocol");
				}
			}
			else
			{
				if (activityStatus!=null && Status.ACTIVITY_STATUS_CLOSED.toString().equals(activityStatus))
				{
					this.checkStatus(dao, protocol, "Collection Protocal");
				}
			}
			// Bug 11481 E

			this.checkStatus(dao, specimen.getSpecimenCollectionGroup(),
					"Specimen Collection Group");
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * get Collection Protocol Id And ActivityStatus.
	 *
	 * @param specimen the specimen
	 * @param dao the dao
	 * @param collectionProtocol the cp
	 * @param activityStatus the activity status
	 *
	 * @return the collection protocol id and activity status
	 *
	 * @throws DAOException the DAO exception
	 * @throws BizLogicException
	 */
	private String getCollectionProtocolIdAndActivityStatus(Specimen specimen, DAO dao,
			final CollectionProtocol collectionProtocol, String activityStatus) throws DAOException, BizLogicException
	{
		Long colpId;
		final String sourceObjectName = SpecimenCollectionGroup.class.getName();
		final String[] selectColumnName = {"collectionProtocolRegistration.id",
				"collectionProtocolRegistration.collectionProtocol.id", "activityStatus",
				"collectionProtocolRegistration.collectionProtocol.activityStatus","name"};
		final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		String errMssg = Constants.DOUBLE_QUOTES;
		if(specimen.getSpecimenCollectionGroup().getId()!=null)
		{
			errMssg = "Specimen Collection Group identifier";
			queryWhereClause.addCondition(new EqualClause("id", specimen.getSpecimenCollectionGroup().getId()));
		}
		else if(specimen.getSpecimenCollectionGroup().getName()!= null)
		{
			errMssg = "Specimen Collection Group name";
			queryWhereClause.addCondition(new EqualClause("name",specimen.getSpecimenCollectionGroup().getName()));
		}
		final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
		if (list.isEmpty())
		{
			/*String errMssg = "Error while inserting record. Possible reason could be incorrect Specimen Collection Group Name or Identifier.";
			LOGGER.error(errMssg, null);
			ErrorKey errorkey = ErrorKey.getErrorKey("customized.message");
			throw new BizLogicException(errorkey , null, errMssg);*/
			final ErrorKey errorkey = ErrorKey.getErrorKey("errors.item.format");
			throw new BizLogicException(errorkey , null, errMssg);
		}
		final Object[] valArr = (Object[]) list.get(0);
		if (valArr != null)
		{
			colpId = (Long) valArr[1];
			activityStatus = (String) valArr[3];
			collectionProtocol.setId(colpId);
			collectionProtocol.setActivityStatus(activityStatus);
			specimen.getSpecimenCollectionGroup().setName((String) valArr[4]);
		}
		return activityStatus;
	}

	// Bug 11481 S
	/**
	 * This Function is used to get Activity Status and Collection Protocol ID.
	 *
	 * @param dao DAO object
	 * @param parentSpecimen the parent specimen
	 *
	 * @return CollectionProtocol
	 *
	 * @throws BizLogicException the biz logic exception
	 * @throws ClassNotFoundException 	 */
	/*
	 * public CollectionProtocol getActivityStatusOfCollectionProtocol(DAO dao,
	 * Long scgId) throws BizLogicException { List activityStatusList = null;
	 * CollectionProtocol cp = new CollectionProtocol(); try { String
	 * activityStatusHQL =
	 * "select scg.collectionProtocolRegistration.collectionProtocol.id," +
	 * "scg.collectionProtocolRegistration.collectionProtocol.activityStatus " +
	 * "from edu.wustl.catissuecore.domain.SpecimenCollectionGroup as scg " +
	 * "where scg.id = " + scgId; activityStatusList =
	 * dao.executeQuery(activityStatusHQL); if (!activityStatusList.isEmpty()) {
	 * Object[] array = new Object[1]; array = (Object[])
	 * activityStatusList.get(0); Long id = (Long) array[0]; String
	 * activityStatus = (String) array[1]; cp.setId(id);
	 * cp.setActivityStatus(activityStatus); } } catch (DAOException daoExp) {
	 * logger.debug(daoExp.getMessage(), daoExp); throw
	 * getBizLogicException(daoExp, "dao.error", ""); } return cp; }
	 */

	// Bug 11481 E
	/**
	 * @param dao : dao
	 * @param parentSpecimen : parentSpecimen
	 * @return Specimen
	 * @throws BizLogicException : BizLogicException
	 */
	private Specimen getParentSpecimenByLabel(DAO dao, Specimen parentSpecimen)
			throws BizLogicException
	{
		try
		{
			String value = parentSpecimen.getLabel();
			String column = "label";
			if ((value == null) || (value != null && value.equals("")))
			{
				column = "barcode";
				value = parentSpecimen.getBarcode();
			}
			final String sourceObjectName = Specimen.class.getName();
			final String[] selectColumnName = {"activityStatus", "createdOn",
					"specimenCollectionGroup.id", "specimenCollectionGroup.activityStatus", "id",
					"pathologicalStatus", "specimenCharacteristics.id", "availableQuantity",
					"collectionStatus","barcode"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new EqualClause(column, value));
			final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
			if (!list.isEmpty())
			{
				if(Status.ACTIVITY_STATUS_DISABLED.toString().equals(((Object[]) list.get(0))[0]))
				{
					throw this.getBizLogicException(null, "error.object.disabled",
					Constants.SPECIMEN);
				}
				final Object[] valArr = (Object[]) list.get(0);
				if (valArr != null)
				{
					this.getParentSpecimenObjectByLabel(dao, parentSpecimen, valArr);
				}
			}
			else
			{
				throw this.getBizLogicException(null, "invalid.label.barcode", value);
			}
			parentSpecimen = this.retrieveParentSpecimenCollectionTypeData(dao, parentSpecimen);
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
		return parentSpecimen;
	}

	/**
	 * get Parent Specimen Object By Label.
	 *
	 * @param dao the dao
	 * @param parentSpecimen the parent specimen
	 * @param valArr the val arr
	 *
	 * @throws DAOException the DAO exception
	 */
	private void getParentSpecimenObjectByLabel(DAO dao, Specimen parentSpecimen,
			final Object[] valArr) throws DAOException
	{
		parentSpecimen.setActivityStatus((String) valArr[0]);
		parentSpecimen.setCreatedOn((Date) valArr[1]);
		final SpecimenCollectionGroup specimenCollectionGroup = new SpecimenCollectionGroup();
		specimenCollectionGroup.setId((Long) valArr[2]);
		specimenCollectionGroup.setActivityStatus((String) valArr[3]);
		parentSpecimen.setSpecimenCollectionGroup(specimenCollectionGroup);

		parentSpecimen.setId((Long) valArr[4]);
		parentSpecimen.setPathologicalStatus((String) valArr[5]);

		final SpecimenCharacteristics characteristics = (SpecimenCharacteristics) dao.retrieveById(
				SpecimenCharacteristics.class.getName(), (Long) valArr[6]);
		if (characteristics != null)
		{
			parentSpecimen.setSpecimenCharacteristics(characteristics);
		}
		parentSpecimen.setSpecimenCharacteristics(characteristics);
		parentSpecimen.setAvailableQuantity((Double) valArr[7]);
		parentSpecimen.setCollectionStatus((String) valArr[8]);
		parentSpecimen.setBarcode((String) valArr[9]);
		this.retrieveStorageContainerInfo(dao, parentSpecimen);
	}

	/**
	 * Sets the scg to specimen.
	 *
	 * @param specimen : specimen
	 * @param dao : dao
	 *
	 * @throws BizLogicException  : BizLogicException
	 */
	private void setSCGToSpecimen(Specimen specimen, DAO dao) throws BizLogicException
	{
		Collection<ConsentTierStatus> consentTierStatusCollection = null;
		SpecimenCollectionGroup scg = specimen.getSpecimenCollectionGroup();
		scg = new SpecimenCollectionGroupBizLogic().retrieveSCG(dao, scg);
		consentTierStatusCollection = ((SpecimenCollectionGroup) scg)
				.getConsentTierStatusCollection();
		this.setConsentTierStatus(specimen, consentTierStatusCollection);
		specimen.setSpecimenCollectionGroup(scg);
	}

	/**
	 * Generate label.
	 *
	 * @param specimen Specimen Object
	 *
	 * @throws BizLogicException Database related Exception
	 */
	private void generateLabel(Specimen specimen) throws BizLogicException
	{
		/**
		 * Call Specimen label generator if automatic generation is specified
		 */

		if(isStatusCollected(specimen))
		{
			try
			{
					final LabelGenerator spLblGenerator;
//					if(Variables.isSpecimenLabelGeneratorAvl){
						spLblGenerator = LabelGeneratorFactory
						.getInstance(Constants.SPECIMEN_LABEL_GENERATOR_PROPERTY_NAME);

//					}else{
//						spLblGenerator = LabelGeneratorFactory
//						.getInstance(Constants.CUSTOM_SPECIMEN_LABEL_GENERATOR_PROPERTY_NAME);
//					}
						if(spLblGenerator != null)
						{
							spLblGenerator.setLabel(specimen);
						}
			}
			catch(LabelException e)
			{
				LOGGER.error(e.getMessage(), e);
				throw this.getBizLogicException(e, "errors.item", e.getMessage());
			}
			catch (LabelGenException e)
			{
				LOGGER.info(e);
			}
			catch (final NameGeneratorException e)
			{
				LOGGER.error(e.getMessage(), e);
				throw this.getBizLogicException(e, "name.generator.exp", "");
			}

		}
 	}

	/**
	 * Checks if is status collected.
	 *
	 * @param specimen the specimen
	 *
	 * @return true, if checks if is status collected
	 */
	private boolean isStatusCollected(Specimen specimen)
	{
		return specimen.getCollectionStatus() != null && specimen.getCollectionStatus().equals(Constants.COLLECTION_STATUS_COLLECTED);
	}

	/**
	 * Sets the specimen to external identifier.
	 *
	 * @param specimen the specimen
	 * @param externalIdentifierCollection the external identifier collection
	 */
//	private boolean isGenerateLabel(Specimen specimen)
//	{
//		boolean generateLabel = false;
//		if(specimen.getSpecimenCollectionGroup() != null && specimen.getSpecimenCollectionGroup().getCollectionProtocolRegistration() != null)
//		{
//			String parentLabelformat = specimen.getSpecimenCollectionGroup().getCollectionProtocolRegistration().getCollectionProtocol().getSpecimenLabelFormat();
//			String deriveLabelFormat = specimen.getSpecimenCollectionGroup().getCollectionProtocolRegistration().getCollectionProtocol().getDerivativeLabelFormat();
//			String aliquotLabelFormat = specimen.getSpecimenCollectionGroup().getCollectionProtocolRegistration().getCollectionProtocol().getAliquotLabelFormat();
//			String lineage = specimen.getLineage();
//			generateLabel= SpecimenUtil.isLblGenOnForCP(parentLabelformat, deriveLabelFormat, aliquotLabelFormat, lineage);
//		}
//		else
//		{
//			String hql = "select scg.collectionProtocolRegistration.protocolParticipantIdentifier,"+
//				" scg.collectionProtocolRegistration.collectionProtocol.id,"+
//				" scg.collectionProtocolRegistration.collectionProtocol.specimenLabelFormat,"+
//				" scg.collectionProtocolRegistration.collectionProtocol.derivativeLabelFormat," +
//				" scg.collectionProtocolRegistration.collectionProtocol.aliquotLabelFormat" +
//				" from edu.wustl.catissuecore.domain.SpecimenCollectionGroup as scg where scg.id="+specimen.getSpecimenCollectionGroup().getId();
//			try
//			{
//				List list = AppUtility.executeQuery(hql);
//				if(list!=null && !list.isEmpty())
//				{
//					Object[] obje = (Object[])list.get(0);
//					generateLabel = (Boolean)obje[3];
//					String PPI= obje[0].toString();
//					String labelFormat = null;
//					if(obje[2] != null)
//					labelFormat = obje[2].toString();
//					Long cpId = Long.valueOf(obje[1].toString());
//					//SpecimenCollectionGroup scg = new SpecimenCollectionGroup();
//					CollectionProtocolRegistration cpr = new CollectionProtocolRegistration();
//					CollectionProtocol cp = new CollectionProtocol();
//					cp.setId(cpId);
//					cp.setGenerateLabel(generateLabel);
//					cp.setSpecimenLabelFormat(labelFormat);
//					cpr.setCollectionProtocol(cp);
//					cpr.setProtocolParticipantIdentifier(PPI);
//					//specimen.setSpecimenCollectionGroup(scg);
//					specimen.getSpecimenCollectionGroup().setCollectionProtocolRegistration(cpr);
//				}
//			}
//			catch (ApplicationException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//		return generateLabel;
//	}

	/**
	 * @param specimen
	 *            Specimen object
	 * @param externalIdentifierCollection
	 *            Collection of external identifier
	 */
	private void setSpecimenToExternalIdentifier(Specimen specimen,
			Collection<ExternalIdentifier> externalIdentifierCollection)
	{
		final Iterator<ExternalIdentifier> iterator = externalIdentifierCollection.iterator();
		while (iterator.hasNext())
		{
			final ExternalIdentifier exId = (ExternalIdentifier) iterator.next();
			exId.setSpecimen(specimen);
		}
	}

	/**
	 * Sets the empty external identifier.
	 *
	 * @param specimen Specimen object
	 * @param externalIdentifierCollection Collection of external identifier
	 */
	private void setEmptyExternalIdentifier(Specimen specimen,
			Collection<ExternalIdentifier> externalIdentifierCollection)
	{
		final ExternalIdentifier exId = new ExternalIdentifier();
		exId.setName(null);
		exId.setValue(null);
		exId.setSpecimen(specimen);
		externalIdentifierCollection.add(exId);
	}

	/**
	 * Post insert.
	 *
	 * @param obj Domain object
	 * @param dao DAO object
	 * @param sessionDataBean The session in which the object is saved.
	 *
	 * @throws BizLogicException Database related Exception
	 */
	public void postInsert(Object obj, DAO dao, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		super.postInsert(obj, dao, sessionDataBean);
	}

	/**
	 * Past Insert method.
	 *
	 * @param speCollection Specimen Collection
	 * @param dao DAO object
	 * @param sessionDataBean The session in which the object is saved.
	 *
	 * @throws BizLogicException Database related Exception
	 * @throws UserNotAuthorizedException User Not Authorized Exception
	 */
	protected void postInsert(Collection<AbstractDomainObject> speCollection, DAO dao,
			SessionDataBean sessionDataBean) throws BizLogicException
	{
		final Iterator<AbstractDomainObject> specimenIterator = speCollection.iterator();
		while (specimenIterator.hasNext())
		{
			final Specimen specimen = (Specimen) specimenIterator.next();
			this.postInsert(specimen, dao, sessionDataBean);
			final Collection childSpecimens = specimen.getChildSpecimenCollection();
			if (!childSpecimens.isEmpty() && childSpecimens != null)
			{
				this.postInsert(childSpecimens, dao, sessionDataBean);
			}
		}
		this.storageContainerIds.clear();
		super.postInsert(speCollection, dao, sessionDataBean);
	}

	/**
	 * Update child attributes.
	 *
	 * @param currentObj Current Object
	 * @param oldObj Persistent Object
	 *
	 * @throws BizLogicException : BizLogicException
	 */
	private void updateChildAttributes(Object currentObj, Object oldObj) throws BizLogicException
	{
		JDBCDAO jdbcDao = null;
		final Specimen currentSpecimen = (Specimen) currentObj;
		final Specimen oldSpecimen = (Specimen) oldObj;
		final String type = currentSpecimen.getSpecimenType();
		final String pathologicalStatus = currentSpecimen.getPathologicalStatus();
		final String identifier = currentSpecimen.getId().toString();
		if (!currentSpecimen.getPathologicalStatus().equals(oldSpecimen.getPathologicalStatus())
				|| !currentSpecimen.getSpecimenType().equals(oldSpecimen.getSpecimenType()))
		{
			try
			{
				jdbcDao = this.openJDBCSession();
				final String queryStr = "UPDATE CATISSUE_SPECIMEN SET TYPE = '" + type
						+ "',PATHOLOGICAL_STATUS = '" + pathologicalStatus
						+ "' WHERE LINEAGE = 'ALIQUOT' AND PARENT_SPECIMEN_ID ='" + identifier
						+ "';";
				jdbcDao.executeUpdate(queryStr);
			}
			catch (final Exception e)
			{
				LOGGER.error("Exception occured while updating aliquots"+e.getMessage(),e);
			}
			finally
			{
				this.closeJDBCSession(jdbcDao);
			}
		}
	}

	/**
	 * Gets the dynamic groups.
	 *
	 * @param obj AbstractDomainObject
	 *
	 * @return dynamicGroups Dynamic group entry for CSM
	 *
	 * @throws BizLogicException : BizLogicException
	 */
	protected String[] getDynamicGroups(AbstractSpecimenCollectionGroup obj)
			throws BizLogicException
	{
		final TaskTimeCalculater getDynaGrps = TaskTimeCalculater.startTask("DynamicGroup",
				NewSpecimenBizLogic.class);
		final String[] dynamicGroups = new String[1];
		try
		{
			final ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
			String name;

			name = CSMGroupLocator.getInstance().getPGName(null, CollectionProtocol.class);

			dynamicGroups[0] = securityManager.getProtectionGroupByName(obj, name);

			LOGGER.debug("Dynamic Group name: " + dynamicGroups[0]);
			TaskTimeCalculater.endTask(getDynaGrps);
		}
		catch (final ApplicationException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw this.getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
		return dynamicGroups;
	}

	/**
	 * Check Container is valid for the specimen.
	 *
	 * @param container Storage Container Object
	 * @param specimen Current Specimen Object
	 * @param dao DAO Object
	 *
	 * @throws BizLogicException Database related exception
	 * @throws DAOException : DAOException
	 */
	protected void chkContainerValidForSpecimen(StorageContainer container, Specimen specimen,
			DAO dao) throws BizLogicException, DAOException
	{
		Collection holdsSpecimenClassColl = this.containerHoldsSpecimenClasses.get(container
				.getId());
		if (isChildSpeCollectionEmpty(holdsSpecimenClassColl))
		{
			if (container.getHoldsSpecimenClassCollection() == null
					|| container.getHoldsSpecimenClassCollection().isEmpty())
			{
				holdsSpecimenClassColl = (Collection) this.retrieveAttribute(dao,
						StorageContainer.class, container.getId(),
						"elements(holdsSpecimenClassCollection)");
			}
			else
			{
				holdsSpecimenClassColl = container.getHoldsSpecimenClassCollection();
			}
			this.containerHoldsSpecimenClasses.put(container.getId(), holdsSpecimenClassColl);
		}
		if (!holdsSpecimenClassColl.contains(specimen.getClassName()))
		{
			throw this.getBizLogicException(null, "storage.nt.hold.spec", specimen.getClassName());
		}
		Collection collectionProtColl = this.containerHoldsCPs.get(container.getId());
		if (collectionProtColl == null)
		{
			collectionProtColl = container.getCollectionProtocolCollection();
			if (isChildSpeCollectionEmpty(collectionProtColl))
			{
				collectionProtColl = (Collection) this.retrieveAttribute(dao,
						StorageContainer.class, container.getId(),
						"elements(collectionProtocolCollection)");

			}
			this.containerHoldsCPs.put(container.getId(), collectionProtColl);
		}
		final CollectionProtocol protocol = this.retriveSCGAndCP(specimen, dao);
		if (collectionProtColl != null && !collectionProtColl.isEmpty())
		{
			if (this.getCorrespondingOldObject(collectionProtColl, protocol.getId()) == null)
			{
				throw this.getBizLogicException(null, "spec.nt.held.by.storage", protocol
						.getTitle());
			}
		}
	}

	/**
	 * Retrive scg and cp.
	 *
	 * @param specimen Current Specimen
	 * @param dao DAO object
	 *
	 * @return CP
	 *
	 * @throws BizLogicException database exception
	 * @throws DAOException : DAOException
	 */
	private CollectionProtocol retriveSCGAndCP(Specimen specimen, DAO dao)
			throws BizLogicException, DAOException
	{
		AbstractSpecimenCollectionGroup scg = null;
		CollectionProtocol protocol = null;
		if (specimen.getSpecimenCollectionGroup() != null)
		{
			scg = specimen.getSpecimenCollectionGroup();
		}
		else if (specimen.getId() != null)
		{
			/*scg = (AbstractSpecimenCollectionGroup) this.retrieveAttribute(dao, Specimen.class,
					specimen.getId(), "specimenCollectionGroup");*/
			final String sourceObjectName = Specimen.class.getName();
			final String[] selectColumnName = {"specimenCollectionGroup.id"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new EqualClause("id", specimen.getId()));
			final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
			if (!list.isEmpty())
			{
				scg = new SpecimenCollectionGroup();
				scg.setId((Long) list.get(0));
			}
		}
		if (scg != null)
		{
			/*protocol = (CollectionProtocol) this.retrieveAttribute(dao,
					SpecimenCollectionGroup.class, scg.getId(),
					"collectionProtocolRegistration.collectionProtocol");*/
			final String sourceObjectName = SpecimenCollectionGroup.class.getName();
			final String[] selectColumnName = {"collectionProtocolRegistration.collectionProtocol.id"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new EqualClause("id", scg.getId()));
			final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
			if (!list.isEmpty())
			{
				protocol = new CollectionProtocol();
				protocol.setId((Long) list.get(0));
			}
		}
		if (protocol == null)
		{
			throw this.getBizLogicException(null, "cp.nt.found", "");
		}
		return protocol;
	}

	/**
	 * Load specimen collection group.
	 *
	 * @param specimenID Specimen Identifier
	 * @param dao DAO object
	 *
	 * @return specimenCollectionGroup SCG object
	 *
	 * @throws BizLogicException Database related Exception
	 */
	private SpecimenCollectionGroup loadSpecimenCollectionGroup(Long specimenID, DAO dao)
			throws BizLogicException
	{
		try
		{
			final String sourceObjectName = Specimen.class.getName();
			final String[] selectedColumn = {"specimenCollectionGroup."
					+ Constants.SYSTEM_IDENTIFIER};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new EqualClause(Constants.SYSTEM_IDENTIFIER, specimenID));

			final List list = dao.retrieve(sourceObjectName, selectedColumn, queryWhereClause);

			if (!list.isEmpty())
			{
				final Long specimenCollectionGroupId = (Long) list.get(0);
				final SpecimenCollectionGroup specimenCollectionGroup = new SpecimenCollectionGroup();
				specimenCollectionGroup.setId(specimenCollectionGroupId);
				return specimenCollectionGroup;
			}
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
		return null;
	}

	/**
	 * Updates the persistent object in the database.
	 *
	 * @param obj The object to be updated.
	 * @param oldObj Persistent object
	 * @param dao DAO object
	 * @param sessionDataBean The session in which the object is saved
	 *
	 * @throws BizLogicException Database related Exception
	 */
	public void update(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		try
		{
			if (obj.getClass().hashCode() == LinkedHashSet.class.hashCode())
			{
				this.updateAnticipatorySpecimens(dao, (LinkedHashSet) obj, sessionDataBean);
			}
			else if (obj instanceof Specimen)
			{
				this.updateSpecimen(dao, obj, oldObj, sessionDataBean);
			}
			else
			{
				throw this.getBizLogicException(null, "obj.not.instanceof.specimen", "");
			}
		}
		catch (final DAOException doexp)
		{
			LOGGER.error(doexp.getMessage(),doexp);
			throw this.getBizLogicException(doexp, doexp.getErrorKeyName(), doexp.getMsgValues());
		}
	}

	/**
	 * Update specimen.
	 *
	 * @param obj The object to be updated.
	 * @param oldObj Persistent object
	 * @param dao DAO object
	 * @param sessionDataBean The session in which the object is saved
	 *
	 * @throws BizLogicException Database related Exception
	 */
	private void updateSpecimen(DAO dao, Object obj, Object oldObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		try
		{
			final Specimen specimen = (Specimen) obj;
			final Specimen specimenOld = (Specimen) HibernateMetaData.getProxyObjectImpl(oldObj);
			ApiSearchUtil.setSpecimenDefault(specimen);
			this.validateSpecimen(dao, specimen, specimenOld);
			specimen.doRoundOff();
			this.updateSpecimenData(dao, sessionDataBean, specimen, specimenOld);
			final Specimen persistentSpecimen = (Specimen) dao.retrieveById(Specimen.class
					.getName(), specimenOld.getId());
			// Calculate Quantity
			this.calculateAvailableQunatity(specimen, persistentSpecimen);
			// To assign storage locations to anticipated specimen
			if (Constants.COLLECTION_STATUS_PENDING.equals(specimenOld.getCollectionStatus())
					&& Constants.COLLECTION_STATUS_COLLECTED.equals(specimen.getCollectionStatus())
					&& specimen.getSpecimenPosition() != null)
			{
				this.storageContainerIds.clear();
				this.allocatePositionForSpecimen(specimen);
				this.storageContainerIds.clear();
				this.setStorageLocationToNewSpecimen(dao, specimen, sessionDataBean, true,null,null);//bug 15260
				persistentSpecimen.setSpecimenPosition(specimen.getSpecimenPosition());
			}
			// Set Specimen Domain Object
			this.createPersistentSpecimenObj(dao, sessionDataBean, specimen, specimenOld,
					persistentSpecimen);
			dao.update(persistentSpecimen,specimenOld);
			this.updateChildAttributes(specimen, specimenOld);

			// Disable functionality
			this.disableSpecimen(dao, specimen, persistentSpecimen);
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
		finally
		{
			this.storageContainerIds.clear();
		}
	}

	/**
	 * postUpdate method.
	 *
	 * @param dao the dao
	 * @param currentObj the current obj
	 * @param oldObj the old obj
	 * @param sessionDataBean the session data bean
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	protected void postUpdate(DAO dao, Object currentObj, Object oldObj,
			SessionDataBean sessionDataBean) throws BizLogicException
	{
		super.postUpdate(dao, currentObj, oldObj, sessionDataBean);
	}

	/**
	 * Disable specimen.
	 *
	 * @param dao DAO object
	 * @param specimen Specimen
	 * @param persistentSpecimen parentSpecimen
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void disableSpecimen(DAO dao, Specimen specimen, Specimen persistentSpecimen)
			throws BizLogicException
	{
		if (specimen.getConsentWithdrawalOption().equalsIgnoreCase(
				Constants.WITHDRAW_RESPONSE_NOACTION))
		{
			if (Status.ACTIVITY_STATUS_DISABLED.toString().equals(specimen.getActivityStatus()))
			{
				boolean disposalEventPresent = false;
				final Collection<SpecimenEventParameters> eventCollection = persistentSpecimen
						.getSpecimenEventCollection();
				final Iterator<SpecimenEventParameters> itr = eventCollection.iterator();
				while (itr.hasNext())
				{
					final Object eventObject = itr.next();
					if (eventObject instanceof DisposalEventParameters)
					{
						disposalEventPresent = true;
						break;
					}
				}
				if (!disposalEventPresent)
				{
					throw this.getBizLogicException(null,
							"errors.specimen.not.disabled.no.disposalevent", "");
				}
				this.setDisableToSubSpecimen(specimen);
				final Long specimenIDArr[] = new Long[Constants.FIRST_COUNT_1];
				specimenIDArr[0] = specimen.getId();
				this.disableSubSpecimens(dao, specimenIDArr);
			}
		}
	}

	/**
	 * Update specimen data.
	 *
	 * @param dao DAO object
	 * @param sessionDataBean Session details
	 * @param specimen Current Specimen
	 * @param specimenOld Old Specimen
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void updateSpecimenData(DAO dao, SessionDataBean sessionDataBean, Specimen specimen,
			Specimen specimenOld) throws BizLogicException
	{
		try
		{
			if (!Constants.ALIQUOT.equals(specimen.getLineage()))
			{
				dao.update(specimen.getSpecimenCharacteristics(),specimenOld.getSpecimenCharacteristics());
			}
			if (!specimen.getConsentWithdrawalOption().equalsIgnoreCase(
					Constants.WITHDRAW_RESPONSE_NOACTION))
			{
				this.updateConsentWithdrawStatus(specimen, dao, sessionDataBean);
			}
			else if (!specimen.getApplyChangesTo().equalsIgnoreCase(Constants.APPLY_NONE))
			{
				this.updateConsentStatus(specimen, dao, specimenOld);
			}
			if ((specimen.getAvailableQuantity() != null && specimen.getAvailableQuantity() == 0)
					|| specimen.getCollectionStatus() == null
					|| specimen.getCollectionStatus().equalsIgnoreCase(
							Constants.COLLECTION_STATUS_PENDING))
			{
				specimen.setIsAvailable(Boolean.FALSE);
			}
			else if (specimenOld.getAvailableQuantity() != null
					&& specimenOld.getAvailableQuantity() == 0)
			{
				specimen.setIsAvailable(Boolean.TRUE);
			}
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * Validate specimen.
	 *
	 * @param dao DAO object
	 * @param specimen Current Specimen
	 * @param specimenOld Old Specimen
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void validateSpecimen(DAO dao, Specimen specimen, Specimen specimenOld)
			throws BizLogicException
	{
		if(!Validator.isEmpty(specimenOld.getLabel())&& Validator.isEmpty(specimen.getLabel()))
		{
			throw this.getBizLogicException(null, "label.mandatory", "");
		}
		if (this.isStoragePositionChanged(specimenOld, specimen))
		{
			if (Constants.COLLECTION_STATUS_PENDING.equals(specimenOld.getCollectionStatus())
					&& Constants.COLLECTION_STATUS_PENDING.equals(specimen.getCollectionStatus())
					&& specimen.getSpecimenPosition() != null)
			{
				throw this.getBizLogicException(null, "status.collected", "");
			}
			throw this.getBizLogicException(null, "position.nt.changed", "");
		}
		if (!specimenOld.getLineage().equals(specimen.getLineage()))
		{
			throw this.getBizLogicException(null, "lineage.nt.changed", "");
		}
		if (!specimenOld.getClassName().equals(specimen.getClassName()))
		{
			throw this.getBizLogicException(null, "clzz.nt.changed", "");
		}
		if (specimen.isParentChanged())
		{
			// Check whether container is moved to one of its sub container.
			if (this.isUnderSubSpecimen(specimen, specimen.getParentSpecimen().getId()))
			{
				throw this.getBizLogicException(null, "errors.specimen.under.subspecimen", "");
			}
			LOGGER.debug("Loading ParentSpecimen: " + specimen.getParentSpecimen().getId());
			final SpecimenCollectionGroup scg = this.loadSpecimenCollectionGroup(specimen
					.getParentSpecimen().getId(), dao);
			specimen.setSpecimenCollectionGroup(scg);
		}
		this.validateCollectionStatus(specimen);
	}

	/**
	 * Creates the persistent specimen obj.
	 *
	 * @param dao DAO object
	 * @param sessionDataBean Session data
	 * @param specimen Current Specimen
	 * @param specimenOld Persistent Specimen
	 * @param persistentSpecimen Persistent Specimen
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void createPersistentSpecimenObj(DAO dao, SessionDataBean sessionDataBean,
			Specimen specimen, Specimen specimenOld, Specimen persistentSpecimen)
			throws BizLogicException
	{
		persistentSpecimen.setLabel(specimen.getLabel());
		persistentSpecimen.setBarcode(specimen.getBarcode());
		persistentSpecimen.setCreatedOn(specimen.getCreatedOn());
		persistentSpecimen.setSpecimenCollectionGroup(specimen.getSpecimenCollectionGroup());
		// bug #8039
		/*
		 * if
		 * (((Constants.COLLECTION_STATUS_COLLECTED).equals(persistentSpecimen
		 * .getCollectionStatus()) &&
		 * (Constants.COLLECTION_STATUS_COLLECTED).equals
		 * (specimen.getCollectionStatus()) && (new
		 * Double(0.0).equals(persistentSpecimen.getAvailableQuantity())))) {
		 * throw newDAOException(ApplicationProperties.getValue(
		 * "specimen.available.operation")); }
		 */
		persistentSpecimen.setIsAvailable(specimen.getIsAvailable());
		persistentSpecimen.setBiohazardCollection(specimen.getBiohazardCollection());
		persistentSpecimen.setNoOfAliquots(specimen.getNoOfAliquots());
		persistentSpecimen.setActivityStatus(specimen.getActivityStatus());
		persistentSpecimen.setAliqoutMap(specimen.getAliqoutMap());
		persistentSpecimen.setComment(specimen.getComment());
		persistentSpecimen.setDisposeParentSpecimen(specimen.getDisposeParentSpecimen());
		persistentSpecimen.setLineage(specimen.getLineage());
		persistentSpecimen.setPathologicalStatus(specimen.getPathologicalStatus());
		persistentSpecimen.setSpecimenType(specimen.getSpecimenType());
		persistentSpecimen.setCollectionStatus(specimen.getCollectionStatus());
		persistentSpecimen
				.setConsentTierStatusCollection(specimen.getConsentTierStatusCollection());
		Double conc = 0D;
		if (Constants.MOLECULAR.equals(specimen.getClassName()))
		{
			conc = ((MolecularSpecimen) specimen).getConcentrationInMicrogramPerMicroliter();
			((MolecularSpecimen) persistentSpecimen).setConcentrationInMicrogramPerMicroliter(conc);
		}
		final String oldStatus = specimenOld.getCollectionStatus();
		this.addSpecimenEvents(persistentSpecimen, specimen, sessionDataBean, oldStatus);
		if (!Constants.COLLECTION_STATUS_COLLECTED.equals(oldStatus))
		{
			this.generateLabel(persistentSpecimen);
			this.generateBarCode(persistentSpecimen);
		}
		this.setExternalIdentifier(dao,specimen,persistentSpecimen);
	}

	/**
	 * add Specimen Events.
	 *
	 * @param persistentSpecimen the persistent specimen
	 * @param specimen the specimen
	 * @param sessionDataBean the session data bean
	 * @param oldStatus the old status
	 */
	private void addSpecimenEvents(Specimen persistentSpecimen, Specimen specimen,
			SessionDataBean sessionDataBean, String oldStatus)
	{
		if (Constants.COLLECTION_STATUS_PENDING.equals(oldStatus)
				&& Constants.COLLECTION_STATUS_COLLECTED.equals(specimen.getCollectionStatus()))
		{
			final SpecimenRequirement reqSpecimen = persistentSpecimen.getSpecimenRequirement();

			if (reqSpecimen != null && reqSpecimen.getParentSpecimen() == null)
			{
				if (reqSpecimen != null && reqSpecimen.getSpecimenEventCollection() != null
						&& !reqSpecimen.getSpecimenEventCollection().isEmpty())
				{
					persistentSpecimen.setPropogatingSpecimenEventCollection(reqSpecimen
							.getSpecimenEventCollection(), sessionDataBean.getUserId(), specimen);
				}
				if (reqSpecimen != null && reqSpecimen.getSpecimenEventCollection() != null
						&& reqSpecimen.getSpecimenEventCollection().isEmpty())
				{
					persistentSpecimen.setDefaultSpecimenEventCollection(sessionDataBean
							.getUserId());
				}
			}
			else if (persistentSpecimen.getParentSpecimen() != null)
			{
				persistentSpecimen.setSpecimenEventCollection(this
						.populateDeriveSpecimenEventCollection((Specimen) persistentSpecimen
								.getParentSpecimen(), persistentSpecimen));
			}
			this.setSpecimenCreatedOnDate(persistentSpecimen);
		}
	}

	/**
	 * Sets the external identifier.
	 *
	 * @param dao DAO object
	 * @param sessionDataBean Session data
	 * @param specimen Current Specimen
	 * @param specimenOld Persistent Specimen
	 * @param persistentSpecimen Persistent Specimen
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void setExternalIdentifier(DAO dao,Specimen specimen,
			Specimen persistentSpecimen) throws BizLogicException
	{
		try
		{
			final Collection<ExternalIdentifier> externalIdentifierCollection = specimen
					.getExternalIdentifierCollection();
			if (externalIdentifierCollection != null)
			{
				final Iterator<ExternalIdentifier> iterator = externalIdentifierCollection.iterator();
				final Collection<ExternalIdentifier> perstExIdColl = persistentSpecimen
						.getExternalIdentifierCollection();
				while (iterator.hasNext())
				{
					final ExternalIdentifier exId = (ExternalIdentifier) iterator.next();
					ExternalIdentifier persistExId = null;
					if (exId.getId() == null)
					{
						exId.setSpecimen(persistentSpecimen);
						persistExId = exId;
						dao.insert(exId);
					}
					else
					{
						persistExId = (ExternalIdentifier) this.getCorrespondingOldObject(
								perstExIdColl, exId.getId());
						persistExId.setName(exId.getName());
						persistExId.setValue(exId.getValue());
					}
				}
				persistentSpecimen.setExternalIdentifierCollection(perstExIdColl);
			}
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * Checks if is under sub specimen.
	 *
	 * @param specimen Current Specimen
	 * @param parentSpecimenID Parent Specimen Identifier
	 *
	 * @return boolean true or false
	 */
	private boolean isUnderSubSpecimen(Specimen specimen, Long parentSpecimenID)
	{
		if (specimen != null)
		{
			final Iterator<AbstractSpecimen> iterator = specimen.getChildSpecimenCollection()
					.iterator();
			while (iterator.hasNext())
			{
				final Specimen childSpecimen = (Specimen) iterator.next();
				if (parentSpecimenID.longValue() == childSpecimen.getId().longValue())
				{
					return true;
				}
				if (this.isUnderSubSpecimen(childSpecimen, parentSpecimenID))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sets the disable to sub specimen.
	 *
	 * @param specimen Current Specimen
	 */
	private void setDisableToSubSpecimen(Specimen specimen)
	{
		if (specimen != null)
		{
			final Iterator<AbstractSpecimen> iterator = specimen.getChildSpecimenCollection()
					.iterator();
			while (iterator.hasNext())
			{
				final Specimen childSpecimen = (Specimen) iterator.next();
				childSpecimen.setActivityStatus(Status.ACTIVITY_STATUS_DISABLED.toString());
				this.setDisableToSubSpecimen(childSpecimen);
			}
		}
	}

	/**
	 * Sets the specimen attributes.
	 *
	 * @param dao DAO object
	 * @param specimen Current Specimen
	 * @param sessionDataBean Session details
	 *
	 * @throws BizLogicException Database related exception
	 * @throws SMException Security related exception
	 */
	private void setSpecimenAttributes(DAO dao, Specimen specimen, SessionDataBean sessionDataBean)
			throws BizLogicException, SMException
	{
		this.setSpecimenEventParameterUserIdentifier(specimen, dao);
		this.setSpecimenEvents(specimen, sessionDataBean);
		this.setDefaultExternalIdentifiers(specimen);
		if (specimen.getLineage() == null)
		{
			if (specimen.getParentSpecimen() == null)
			{
				specimen.setLineage(Constants.NEW_SPECIMEN);
			}
			else
			{
				specimen.setLineage(Constants.DERIVED_SPECIMEN);
			}
		}
		this.setQuantity(specimen);
		specimen.setActivityStatus(Status.ACTIVITY_STATUS_ACTIVE.toString());
		if (specimen.getBarcode() != null && specimen.getBarcode().trim().equals(""))
		{
			specimen.setBarcode(null);
		}
	}

	/**
	 * Set Specimen Event Parameter User Identifier.
	 *
	 * @param specimen Specimen
	 * @param dao DAO
	 *
	 * @throws BizLogicException BizLogicException
	 */
	private void setSpecimenEventParameterUserIdentifier(Specimen specimen,
			DAO dao) throws BizLogicException
	{
		Collection<SpecimenEventParameters> specimenEventCollection =
			specimen.getSpecimenEventCollection();
		for(SpecimenEventParameters parameters : specimenEventCollection)
		{
			if(parameters.getUser().getId() == null && parameters.getUser().getLoginName() != null)
			{
				try
				{
					final String sourceObjectName = User.class.getName();
					final String[] selectColumnName = { "id" };
					final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
					queryWhereClause.addCondition(new EqualClause("loginName",
							parameters.getUser().getLoginName()));
					final List list = dao
							.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
					if(list.isEmpty())
					{
						throw this.getBizLogicException(null, "user.not.exists",
								parameters.getUser().getLoginName());
					}
					else
					{
						parameters.getUser().setId((Long)list.get(0));
					}
				}
				catch (final DAOException daoExp)
				{
					LOGGER.error(daoExp.getMessage(), daoExp);
					throw this.getBizLogicException(daoExp,
							daoExp.getErrorKeyName(), daoExp.getMsgValues());
				}
			}
		}
	}

	/**
	 * Sets the consent tier status.
	 *
	 * @param specimen : specimen
	 * @param consentTierStatusCollection : specimen
	 */
	private void setConsentTierStatus(Specimen specimen,
			Collection<ConsentTierStatus> consentTierStatusCollection)
	{
		final Collection<ConsentTierStatus> consentTierStatusCollectionForSpecimen = new HashSet<ConsentTierStatus>();
		if (consentTierStatusCollection != null)
		{
			final Iterator<ConsentTierStatus> itr = consentTierStatusCollection.iterator();
			while (itr.hasNext())
			{
				final ConsentTierStatus conentTierStatus = (ConsentTierStatus) itr.next();
				final ConsentTierStatus consentTierStatusForSpecimen = new ConsentTierStatus();
				consentTierStatusForSpecimen.setStatus(conentTierStatus.getStatus());
				consentTierStatusForSpecimen.setConsentTier(conentTierStatus.getConsentTier());
				consentTierStatusCollectionForSpecimen.add(consentTierStatusForSpecimen);
			}
			specimen.setConsentTierStatusCollection(consentTierStatusCollectionForSpecimen);
		}
	}

	/**
	 * Sets the parent specimen.
	 *
	 * @param dao DAO Object
	 * @param specimen Current Specimen
	 *
	 * @throws BizLogicException Database related exception
	 *//*
	private void setParentSpecimen(DAO dao, Specimen specimen) throws BizLogicException
	{
		final Specimen parentSpecimen = (Specimen) specimen.getParentSpecimen();
		if (specimen.getParentSpecimen() != null)
		{
			specimen.setSpecimenEventCollection(this.populateDeriveSpecimenEventCollection(
					parentSpecimen, specimen));
		}
	}*/

	/**
	 * Sets the storage location to new specimen.
	 *
	 * @param dao DAO object
	 * @param specimen Current Specimen
	 * @param sessionDataBean Session details
	 * @param partOfMultipleSpecimen boolean true or false
	 * @param specPos1 the spec pos1
	 * @param specPos2 the spec pos2
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	// Bug 15392
	private void setStorageLocationToNewSpecimen(DAO dao, Specimen specimen,
			SessionDataBean sessionDataBean, boolean partOfMultipleSpecimen,Integer specPos1,Integer specPos2)
			throws BizLogicException
	{
		try
		{
			if (specimen.getSpecimenPosition() != null
					&& specimen.getSpecimenPosition().getStorageContainer() != null)
			{
				StorageContainer storageContainerObj = null;
				if (specimen.getParentSpecimen() != null)
				{
					storageContainerObj = this.chkParentStorageContainer(specimen,
							storageContainerObj);
				}
				if (storageContainerObj == null)
				{
					if (specimen.getSpecimenPosition().getStorageContainer().getId() != null)
					{
						storageContainerObj = this.retreieveStorageContainerOfSpecimen(dao,
								specimen, storageContainerObj);
						if (storageContainerObj == null)
						{
							storageContainerObj = specimen.getSpecimenPosition()
									.getStorageContainer();
						}
					}
					else
					{
						storageContainerObj = this.setStorageContainerId(dao, specimen);
					}
					if (!Status.ACTIVITY_STATUS_ACTIVE.toString().equals(
							storageContainerObj.getActivityStatus()))
					{
						throw this.getBizLogicException(null, "st.colosed", "");
					}
					this.chkContainerValidForSpecimen(storageContainerObj, specimen, dao);
					//method not in use
					//this.validateUserForContainer(sessionDataBean, storageContainerObj);
				}
				SpecimenPosition specPos = specimen.getSpecimenPosition();
				if (specPos.getPositionDimensionOne() == null
						|| specPos.getPositionDimensionTwo() == null)
				{
					final Position position = StorageContainerUtil
							.getFirstAvailablePositionsInContainer(storageContainerObj,
									this.storageContainerIds, dao,specPos1,specPos2);//janhavi

					specPos = new SpecimenPosition();
					specPos.setPositionDimensionOne(position.getXPos());
					specPos.setPositionDimensionTwo(position.getYPos());
				}
				specPos.setSpecimen(specimen);
				specPos.setStorageContainer(storageContainerObj);
				specimen.setSpecimenPosition(specPos);

				// bug 8294
				String storageValue = null;
				final Long identifier = specimen.getSpecimenPosition().getStorageContainer().getId();
				final Integer pos1 = specimen.getSpecimenPosition().getPositionDimensionOne();
				final Integer pos2 = specimen.getSpecimenPosition().getPositionDimensionTwo();
				final String containerName = specimen.getSpecimenPosition().getStorageContainer()
						.getName();
				if (containerName != null)
				{
					storageValue = StorageContainerUtil.getStorageValueKey(containerName, null,
							pos1, pos2);
				}
				else
				{
					storageValue = StorageContainerUtil.getStorageValueKey(null, identifier.toString(),
							pos1, pos2);
				}
				if (!this.storageContainerIds.contains(storageValue))
				{
					this.storageContainerIds.add(storageValue);
				}
				else
				{
					throw AppUtility.getApplicationException(null,
							"errors.storageContainer.Multiple.inUse", "StorageContainerUtil.java");
				}
				final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
				final StorageContainerBizLogic storageContainerBizLogic = (StorageContainerBizLogic) factory
						.getBizLogic(Constants.STORAGE_CONTAINER_FORM_ID);

				final String contId= storageContainerObj.getId().toString();
				final String posOne= specimen.getSpecimenPosition()
				.getPositionDimensionOne().toString();
				final String posTwo=specimen
				.getSpecimenPosition().getPositionDimensionTwo().toString();
				storageContainerBizLogic.checkContainer(dao,StorageContainerUtil.setparameterList
				(contId, posOne, posTwo, partOfMultipleSpecimen),sessionDataBean,specimen);
			}
		}
		catch (final ApplicationException exp)
		{
			LOGGER.error(exp.getMessage(), exp);
			throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp.getMsgValues());
		}
	}

	/**
	 * retreieve Storage Container Of Specimen.
	 *
	 * @param dao the dao
	 * @param specimen the specimen
	 * @param storageContainerObj the storage container obj
	 *
	 * @return the storage container
	 *
	 * @throws DAOException the DAO exception
	 */
	private StorageContainer retreieveStorageContainerOfSpecimen(DAO dao, Specimen specimen,
			StorageContainer storageContainerObj) throws DAOException
	{
		final String sourceObjectName = StorageContainer.class.getName();
		/*storageContainerObj = (StorageContainer) dao.retrieveById(
		 * sourceObjectName,specimen.getSpecimenPosition().
		 * getStorageContainer().getId());*/
		final String[] selectColumnName = {"name", "activityStatus"};
		final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
		queryWhereClause.addCondition(new EqualClause("id", specimen.getSpecimenPosition()
				.getStorageContainer().getId()));
		final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
		if (!list.isEmpty())
		{
			final Object[] valArr = (Object[]) list.get(0);
			if (valArr != null)
			{
				storageContainerObj = new StorageContainer();
				storageContainerObj.setId(specimen.getSpecimenPosition().getStorageContainer()
						.getId());
				storageContainerObj.setName((String) valArr[0]);
				storageContainerObj.setActivityStatus((String) valArr[1]);
				if (storageContainerObj != null)
				{
					final String[] columnName = {"locatedAtPosition.id"};
					final QueryWhereClause queryClause = new QueryWhereClause(sourceObjectName);
					queryClause.addCondition(new EqualClause(Constants.SYSTEM_IDENTIFIER, specimen
							.getSpecimenPosition().getStorageContainer().getId()));
					final List innerList = dao.retrieve(sourceObjectName, columnName, queryClause);
					if (!innerList.isEmpty())
					{
						final ContainerPosition containerPosition = new ContainerPosition();
						containerPosition.setId((Long) innerList.get(0));
						storageContainerObj.setLocatedAtPosition(containerPosition);
					}
				}
				specimen.getSpecimenPosition().setStorageContainer(storageContainerObj);
			}
		}
		return storageContainerObj;
	}

/**
 * removed unused method
 */
//	/**
//	 * Validate user for container.
//	 *
//	 * @param sessionDataBean Session Details
//	 * @param storageContainerObj Storage Container Object
//	 *
//	 * @throws BizLogicException Database related exception
//	 */
//	private void validateUserForContainer(SessionDataBean sessionDataBean,
//			Container storageContainerObj) throws BizLogicException
//	{
//		try
//		{
//			Container parentStorageContainer = null;
//			final ContainerPosition cntPos = storageContainerObj.getLocatedAtPosition();
//			if (cntPos != null)
//			{
//				parentStorageContainer = cntPos.getParentContainer();
//			}
//			// To get privilegeCache through
//			// Singleton instance of PrivilegeManager, requires User LoginName
//			final PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
//			final PrivilegeCache privilegeCache = privilegeManager
//					.getPrivilegeCache(sessionDataBean.getUserName());
//			if (parentStorageContainer != null)
//			{
//				this.validateUserForContainer(sessionDataBean, parentStorageContainer);
//			}
//			final Object object = HibernateMetaData.getProxyObjectImpl(storageContainerObj);
//			final String storageContainerSecObj = object.getClass().getName() + "_"
//					+ storageContainerObj.getId();
//			final boolean userAuthorize = true;
//			// Commented by Vishvesh & Ravindra for MSR for C1
//			// privilegeCache.hasPrivilege(storageContainerSecObj,
//			// Permissions.USE);
//
//			if (!userAuthorize)
//			{
//				throw this.getBizLogicException(null, "user.not.auth.use.storage",
//						storageContainerObj.getName());
//			}
//		}
//		catch (final SMException e)
//		{
//			LOGGER.error(e.getMessage(), e);
//			throw AppUtility.handleSMException(e);
//		}
//	}

	/**
	 * Chk parent storage container.
	 *
	 * @param specimen Current Specimen
	 * @param storageContainerObj Storage Container Object
	 *
	 * @return storageContainerObj Storage Container Object
	 */
	private StorageContainer chkParentStorageContainer(Specimen specimen,
			StorageContainer storageContainerObj)
	{
		final AbstractSpecimen parent = specimen.getParentSpecimen();
		if (((Specimen) parent).getSpecimenPosition() != null
				&& ((Specimen) parent).getSpecimenPosition().getStorageContainer() != null)
		{
			final StorageContainer parentContainer = ((Specimen) parent).getSpecimenPosition()
					.getStorageContainer();
			final StorageContainer specimenContainer = specimen.getSpecimenPosition()
					.getStorageContainer();
			if (parentContainer.getId().equals(specimenContainer.getId())
					|| parentContainer.getName().equals(specimenContainer.getName()))
			{
				storageContainerObj = parentContainer;
			}
		}
		return storageContainerObj;
	}

	/**
	 * Sets the storage container id.
	 *
	 * @param specimen Current Specimen
	 * @param dao object
	 *
	 * @return storageContainerObj Storage Container Object
	 *
	 * @throws BizLogicException Database related exception
	 */
	private StorageContainer setStorageContainerId(DAO dao, Specimen specimen)
			throws BizLogicException
	{
		try
		{
			final String sourceObjectName = StorageContainer.class.getName();
			final List list = dao.retrieve(sourceObjectName, "name", specimen.getSpecimenPosition()
					.getStorageContainer().getName());
			if (!list.isEmpty())
			{
				return (StorageContainer) list.get(0);
			}
			else
			{
				throw this.getBizLogicException(null, "incorrect.storage", "");
			}
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}

	}

	/**
	 * Sets the specimen storage recursively.
	 *
	 * @param newSpecimen : newSpecimen
	 * @param dao : dao
	 * @param sessionDataBean : sessionDataBean
	 * @param partOfMultipleSpecimen : partOfMultipleSpecimen
	 * @param pos1 position two
	 * @param pos2 the pos2
	 *
	 * @throws BizLogicException : BizLogicException
	 */
	 //Bug 15392
	private void setSpecimenStorageRecursively(Specimen newSpecimen, DAO dao,
			SessionDataBean sessionDataBean,Integer pos1,Integer pos2)
			throws BizLogicException
	{
		setSpecimenPosition(newSpecimen, dao, sessionDataBean,pos1, pos2);
	}

	/**
	 * Set Specimen Position.
	 * @param newSpecimen Specimen
	 * @param dao DAO
	 * @param sessionDataBean SessionDataBean
	 * @param pos1 Integer
	 * @param pos2 Integer
	 * @throws BizLogicException BizLogicException
	 */
	//Bug17288
	private void setSpecimenPosition(Specimen newSpecimen, DAO dao,
			SessionDataBean sessionDataBean,
			Integer pos1, Integer pos2) throws BizLogicException
	{
		this.setStorageLocationToNewSpecimen(dao, newSpecimen, sessionDataBean, true,pos1,pos2);
		if (newSpecimen.getChildSpecimenCollection() != null)
		{
			final Collection<AbstractSpecimen> specimenCollection = newSpecimen
					.getChildSpecimenCollection();
			final AbstractSpecimen array[] = new AbstractSpecimen[specimenCollection.size()];
			specimenCollection.toArray(array);
			//final Iterator<AbstractSpecimen> iterator = specimenCollection.iterator();
			//while (iterator.hasNext())

			for(int i=0;i<array.length;i++)
			{
				final Specimen specimen = (Specimen) array[i];
				specimen.setSpecimenCollectionGroup(newSpecimen.getSpecimenCollectionGroup());
				if(specimen.getSpecimenPosition()!=null)//if specimen is virtual position will be null.
				{
				  pos1 = specimen.getSpecimenPosition().getPositionDimensionOne();
				  pos2 = specimen.getSpecimenPosition().getPositionDimensionTwo();
				}
				if(((pos1 == null || pos2 == null) && i!=0))
				{
					if(((Specimen) array[i-1]).getSpecimenPosition() != null)
					{
						pos1 = ((Specimen) array[i-1]).getSpecimenPosition().getPositionDimensionOne();
						pos2 = ((Specimen) array[i-1]).getSpecimenPosition().getPositionDimensionTwo();
					}
				}
				this.setSpecimenPosition(specimen, dao, sessionDataBean, pos1, pos2);

			}
		}
	}

	/**
	 * Allocate position for specimen.
	 *
	 * @param specimen Current Specimen
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void allocatePositionForSpecimen(Specimen specimen) throws BizLogicException
	{
		if (specimen != null
				&& specimen.getSpecimenPosition() != null
				&& (specimen.getSpecimenPosition().getPositionDimensionOne() != null || specimen
						.getSpecimenPosition().getPositionDimensionTwo() != null))
		{
			if (specimen.getSpecimenPosition() != null
					&& specimen.getSpecimenPosition().getStorageContainer() != null)
			{
				// bug 8294
				String storageValue = null;
				final Long storageContainerId = specimen.getSpecimenPosition().getStorageContainer().getId();
				final Integer pos1 = specimen.getSpecimenPosition().getPositionDimensionOne();
				final Integer pos2 = specimen.getSpecimenPosition().getPositionDimensionTwo();
				final String containerName = specimen.getSpecimenPosition().getStorageContainer()
						.getName();

				if (storageContainerId != null)
				{
					storageValue = StorageContainerUtil.getStorageValueKey(null, storageContainerId.toString(),
							pos1, pos2);
				}
				else
				{
					storageValue = StorageContainerUtil.getStorageValueKey(containerName, null,
							pos1, pos2);
				}
				if (!this.storageContainerIds.contains(storageValue))
				{
					this.storageContainerIds.add(storageValue);
				}
				else
				{
					//final Object[] arguments = {specimen.getLabel(), containerName, pos1, pos2};
					//final String errorMsg = Constants.CONTAINER_ERROR_MSG;
					throw this.getBizLogicException(null, "spec.storage.not.free", specimen
							.getLabel()
							+ ":" + containerName + ":" + pos1 + ":" + pos2);
				}
			}
		}
	}

	/**
	 * Disable related objects for specimen collection group.
	 *
	 * @param dao DAO Object
	 * @param specimenCollectionGroupArr Array of Specimen Collection Group
	 *
	 * @throws BizLogicException Database related exception
	 */
	public void disableRelatedObjectsForSpecimenCollectionGroup(DAO dao,
			Long specimenCollectionGroupArr[]) throws BizLogicException
	{
		LOGGER.debug("disableRelatedObjects NewSpecimenBizLogic");
		final List listOfSpecimenId = super.disableObjects(dao, Specimen.class,
				"specimenCollectionGroup", "CATISSUE_SPECIMEN", "SPECIMEN_COLLECTION_GROUP_ID",
				specimenCollectionGroupArr);
		if (!listOfSpecimenId.isEmpty())
		{
			this.disableSubSpecimens(dao, Utility.toLongArray(listOfSpecimenId));
		}
	}

	/**
	 * Disable sub specimens.
	 *
	 * @param dao DAO object
	 * @param speIDArr Array of Specimen Id
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void disableSubSpecimens(DAO dao, Long speIDArr[]) throws BizLogicException
	{
		/*
		 * List listOfSubElement = disableObjects(dao, AbstractSpecimen.class,
		 * "parentSpecimen", "CATISSUE_ABSTRACT_SPECIMEN", "PARENT_SPECIMEN_ID",
		 * speIDArr);
		 */
		final List listOfSubElement = this.disableObjects(dao, "CATISSUE_SPECIMEN", Specimen.class,
				"parentSpecimen", speIDArr);

		if (listOfSubElement.isEmpty())
		{
			return;
		}
		this.disableSubSpecimens(dao, Utility.toLongArray(listOfSubElement));
	}

	/**
	 * Todo Remove this method.
	 *
	 * @param dao DAO object
	 * @param obj the obj
	 * @param operation the operation
	 *
	 * @return true, if validate
	 *
	 * @throws BizLogicException Database related exception
	 * @throws SMException Security related exception
	 */
	/*
	 * public void assignPrivilegeToRelatedObjectsForSCG(DAO dao, String
	 * privilegeName, Long[] specimenCollectionGroupArr, Long userId, String
	 * roleId, boolean assignToUser, boolean assignOperation) throws
	 * SMException, DAOException {
	 * Logger.out.debug("assignPrivilegeToRelatedObjectsForSCG NewSpecimenBizLogic"
	 * ); List listOfSpecimenId = super.getRelatedObjects(dao, Specimen.class,
	 * "specimenCollectionGroup", specimenCollectionGroupArr); if
	 * (!listOfSpecimenId.isEmpty()) { super.setPrivilege(dao, privilegeName,
	 * Specimen.class, Utility.toLongArray(listOfSpecimenId), userId, roleId,
	 * assignToUser, assignOperation); List specimenCharacteristicsIds =
	 * super.getRelatedObjects(dao, AbstractSpecimen.class, new
	 * String[]{"specimenCharacteristics." + Constants.SYSTEM_IDENTIFIER}, new
	 * String[]{Constants.SYSTEM_IDENTIFIER},
	 * Utility.toLongArray(listOfSpecimenId)); super.setPrivilege(dao,
	 * privilegeName, Address.class,
	 * Utility.toLongArray(specimenCharacteristicsIds), userId, roleId,
	 * assignToUser, assignOperation); assignPrivilegeToSubSpecimens(dao,
	 * privilegeName, Specimen.class, Utility.toLongArray(listOfSpecimenId),
	 * userId, roleId, assignToUser, assignOperation); } }
	 *//**
								 * Todo Remove this method
								 *
								 * @param dao
								 *            DAO object
								 * @param privilegeName
								 *            privilegeName
								 * @param class1
								 *            Class
								 * @param speIDArr
								 *            Array of Specimen Id
								 * @param roleId
								 *            Role Identifier
								 * @param assignToUser
								 *            boolean true or false
								 * @param assignOperation
								 *            boolean
								 */
	/*
	 * private void assignPrivilegeToSubSpecimens(DAO dao, String privilegeName,
	 * Class class1, Long[] speIDArr, Long userId, String roleId, boolean
	 * assignToUser, boolean assignOperation) throws SMException, DAOException {
	 * List listOfSubElement = super.getRelatedObjects(dao,
	 * AbstractSpecimen.class, "parentSpecimen", speIDArr); if
	 * (listOfSubElement.isEmpty()) { return; } super.setPrivilege(dao,
	 * privilegeName, Specimen.class, Utility.toLongArray(listOfSubElement),
	 * userId, roleId, assignToUser, assignOperation); List
	 * specimenCharacteristicsIds = super.getRelatedObjects(dao,
	 * AbstractSpecimen.class, new String[]{"specimenCharacteristics." +
	 * Constants.SYSTEM_IDENTIFIER}, new String[]{Constants.SYSTEM_IDENTIFIER},
	 * Utility.toLongArray(listOfSubElement)); super.setPrivilege(dao,
	 * privilegeName, Address.class,
	 * Utility.toLongArray(specimenCharacteristicsIds), userId, roleId,
	 * assignToUser, assignOperation); assignPrivilegeToSubSpecimens(dao,
	 * privilegeName, Specimen.class, Utility.toLongArray(listOfSubElement),
	 * userId, roleId, assignToUser, assignOperation); } Todo Remove this method
	 * (non-Javadoc)
	 * @see
	 * edu.wustl.common.bizlogic.DefaultBizLogic#setPrivilege(edu.wustl.common
	 * .dao.DAO, java.lang.String, java.lang.Class, java.lang.Long[],
	 * java.lang.Long, java.lang.String, boolean, boolean) public void
	 * setPrivilege(DAO dao, String privilegeName, Class objectType, Long[]
	 * objectIds, Long userId, String roleId, boolean assignToUser, boolean
	 * assignOperation) throws SMException, DAOException {
	 * super.setPrivilege(dao, privilegeName, objectType, objectIds, userId,
	 * roleId, assignToUser, assignOperation); List specimenCharacteristicsIds =
	 * super.getRelatedObjects(dao, AbstractSpecimen.class, new
	 * String[]{"specimenCharacteristics." + Constants.SYSTEM_IDENTIFIER}, new
	 * String[]{Constants.SYSTEM_IDENTIFIER}, objectIds);
	 * super.setPrivilege(dao, privilegeName, Address.class,
	 * Utility.toLongArray(specimenCharacteristicsIds), userId, roleId,
	 * assignToUser, assignOperation); assignPrivilegeToSubSpecimens(dao,
	 * privilegeName, Specimen.class, objectIds, userId, roleId, assignToUser,
	 * assignOperation); }
	 *//**
								 * Todo Remove this method
								 *
								 * @param dao
								 *            DAO object
								 * @param privilegeName
								 *            privilegeName
								 * @param objectIds
								 *            Array of Passed object Id
								 * @param userId
								 *            User Identifier
								 * @param roleId
								 *            Role Identifier
								 * @param assignToUser
								 *            boolean true or false
								 * @param assignOperation
								 *            boolean
								 * @throws SMException
								 *             Security related Exception
								 * @throws BizLogicException
								 *             Database related exception
								 */
	/*
	 * public void assignPrivilegeToRelatedObjectsForDistributedItem(DAO dao,
	 * String privilegeName, Long[] objectIds, Long userId, String roleId,
	 * boolean assignToUser, boolean assignOperation) throws SMException,
	 * DAOException { String[] selectColumnNames = {"specimen.id"}; String[]
	 * whereColumnNames = {"id"}; List listOfSubElement =
	 * super.getRelatedObjects(dao, DistributedItem.class, selectColumnNames,
	 * whereColumnNames, objectIds); if (!listOfSubElement.isEmpty()) {
	 * super.setPrivilege(dao, privilegeName, Specimen.class,
	 * Utility.toLongArray(listOfSubElement), userId, roleId, assignToUser,
	 * assignOperation); } }
	 */

	/**
	 * Overriding the parent class's method to validate the enumerated attribute
	 * values.
	 *
	 * @param obj
	 *            Type of object linkedHashSet or domain object
	 * @param dao
	 *            DAO object
	 * @param operation
	 *            Type of Operation
	 * @return result
	 * @throws BizLogicException
	 *             Database related exception
	 */
	protected boolean validate(Object obj, DAO dao, String operation) throws BizLogicException
	{
		boolean result = false;
		// Bug 11481 S

		try
		{
			if (obj instanceof Specimen)
			{
				final Specimen specimen = (Specimen) obj;
				List collStatusList = null;
				//Added for bug #16319
				if(specimen.getSpecimenCollectionGroup() == null)
				{
					specimen.setSpecimenCollectionGroup(new SpecimenCollectionGroup());
				}
				final Long scgId = specimen.getSpecimenCollectionGroup().getId();
				final CollectionProtocol collectionProtocol = new CollectionProtocol();

				final String collStatusHQL = "select sp.collectionStatus "
						+ "from edu.wustl.catissuecore.domain.Specimen as sp " + "where sp.id = "
						+ specimen.getId();
				collStatusList = dao.executeQuery(collStatusHQL);
				// cp = getActivityStatusOfCollectionProtocol(dao, scgId);

				if (scgId != null)
				{
					final AbstractSpecimenCollectionGroup specimenCollectionGroup = (AbstractSpecimenCollectionGroup) dao
							.retrieveById("edu.wustl.catissuecore.domain.SpecimenCollectionGroup",
									scgId);
					final Long colpId = (Long) specimenCollectionGroup
							.getCollectionProtocolRegistration().getCollectionProtocol().getId();
					final String activityStatus = specimenCollectionGroup
							.getCollectionProtocolRegistration().getCollectionProtocol()
							.getActivityStatus();
					collectionProtocol.setId(colpId);
					collectionProtocol.setActivityStatus(activityStatus);

				}
				String collStatus = null;
				if (!collStatusList.isEmpty())
				{
					collStatus = (String) collStatusList.get(0);
				}
				if (specimen.getParentSpecimen() != null)
				{
					final String ParentCollectionStatus = ((Specimen) specimen.getParentSpecimen())
							.getCollectionStatus();
					if (ParentCollectionStatus != null
							&& !ParentCollectionStatus
									.equalsIgnoreCase(Constants.COLLECTION_STATUS_COLLECTED))
					{
						if (collStatus != null
								&& !collStatus
										.equalsIgnoreCase(Constants.COLLECTION_STATUS_COLLECTED)
								&& !ParentCollectionStatus
										.equalsIgnoreCase(Constants.COLLECTION_STATUS_PENDING))
						{
							this.checkStatus(dao, collectionProtocol, "Collection Protocol");
						}
					}
				}
				else
				{
					if (collStatus != null
							&& !collStatus.equalsIgnoreCase(Constants.COLLECTION_STATUS_COLLECTED))
					{
						this.checkStatus(dao, collectionProtocol, "Collection Protocol");
					}
				}
			}
			// Bug 11481 E
			if (obj instanceof LinkedHashSet)
			{
				// bug no. 8081 and 8083
						this.validateLable(obj);

						if (operation.equals(Constants.ADD))
				{
					return MultipleSpecimenValidationUtil.validateMultipleSpecimen(
							(LinkedHashSet) obj, dao, operation);
				}
				else
				{
					return true;
				}
			}
			else
			{
				result = this.validateSingleSpecimen((Specimen) obj, dao, operation, false);
			}
		}
		catch (final ApplicationException exp)
		{
			LOGGER.error(exp.getMessage(), exp);
			throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp.getMsgValues());
		}
		return result;
	}

	/**
	 * Validate lable.
	 *
	 * @param obj : obj
	 * @param isLabelOnForCP the is label on for cp
	 *
	 * @throws BizLogicException  : BizLogicException
	 */
	private void validateLable(Object obj) throws BizLogicException
	{
		if(!Variables.isSpecimenLabelGeneratorAvl)
		{
		final Iterator specimenIterator = ((LinkedHashSet) obj).iterator();

		SpecimenCollectionGroup scg = null;
		while (specimenIterator.hasNext())
		{
			final Specimen temp = (Specimen) specimenIterator.next();

				if (Validator.isEmpty(temp.getLabel()) && temp.getCollectionStatus().equalsIgnoreCase("Collected"))
				{
					throw this.getBizLogicException(null, "label.mandatory", "");
				}
				final Collection aliquotsCollection = temp.getChildSpecimenCollection();
				if (aliquotsCollection != null)
				{
					final Iterator aliquotItert = aliquotsCollection.iterator();
					while (aliquotItert.hasNext())
					{
						final Specimen tempAliquot = (Specimen) aliquotItert.next();
						this.validateLable(aliquotsCollection);
							if (Validator.isEmpty(tempAliquot.getLabel()) && tempAliquot.getCollectionStatus().equalsIgnoreCase("Collected"))
							{
								throw this.getBizLogicException(null, "label.mandatory", "");
							}
					}
				}
		}
		}
	}


	/**
	 * Checks if is label onfor specimen.
	 *
	 * @param objSpecimen the obj specimen
	 * @param isLabelOnForCP the is label on for cp
	 *
	 * @return true, if is label onfor specimen
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	private boolean isLabelOnforSpecimen(final Specimen objSpecimen) throws BizLogicException
	{
		boolean isLabelOnForSpecimen = false;
		try
		{
		if(objSpecimen.getSpecimenRequirement() == null && objSpecimen.getId() != null)
		{
			String hql = "select specimen.specimenRequirement from edu.wustl.catissuecore.domain.Specimen as specimen"
				+" where specimen.id="+ objSpecimen.getId();

			List<Object[]> list=null;

				list=AppUtility.executeQuery(hql);
				if(list!=null)
				{
					Object object = list.get(0);
					objSpecimen.setSpecimenRequirement((SpecimenRequirement)object);
				}
			}

//					if(objSpecimen.getSpecimenRequirement() != null)
//					{
//						if(objSpecimen.getSpecimenRequirement().getGenLabel() && !Validator.isEmpty(objSpecimen.getSpecimenRequirement().getLabelFormat()))
//						{
//							isLabelOnForSpecimen = objSpecimen.getSpecimenRequirement().getGenLabel();
//						}
//						else if(objSpecimen.getSpecimenRequirement().getGenLabel() && Validator.isEmpty(objSpecimen.getSpecimenRequirement().getLabelFormat()))
//						{
//							isLabelOnForSpecimen = isLabelOnForCP;
//						}
//					}
//					else
//					{
//						isLabelOnForSpecimen = isLabelOnForCP;
//					}
					isLabelOnForSpecimen = SpecimenUtil.isGenLabel(objSpecimen);


			}
			catch(ApplicationException exp)
			{
				throw this.getBizLogicException(exp, exp.getErrorKeyAsString(), exp.getMessage());
			}
		return isLabelOnForSpecimen;
	}

	/**
	 * Validate Single Specimen.
	 *
	 * @param specimen Specimen Object to validate
	 * @param dao DAO object
	 * @param operation Add/Edit
	 * @param partOfMulipleSpecimen boolean
	 *
	 * @return boolean
	 *
	 * @throws BizLogicException Database related exception
	 */
	private boolean validateSingleSpecimen(Specimen specimen, DAO dao, String operation,
			boolean partOfMulipleSpecimen) throws BizLogicException
	{
		if (specimen == null)
		{
			throw this.getBizLogicException(null, "specimen.object.null.err.msg", Constants.SPECIMEN);
		}
		final Validator validator = new Validator();
		this.validateSpecimenData(specimen, validator);
		this.validateStorageContainer(specimen, dao);
		this.validateSpecimenEvent(specimen, validator);
		this.validateBioHazard(specimen, validator, dao);
		this.validateExternalIdentifier(specimen, validator);
		this.validateFields(specimen, dao, partOfMulipleSpecimen);
		this.validateEnumeratedData(specimen, operation, validator);
		this.validateSpecimenCharacterstics(specimen);
		validateLabelForSingleSpecimen(specimen);
		if (operation.equals(Constants.ADD))
		{
			this.validateDerivedSpecimens(specimen, dao, operation);
		}
		// new check added for bug #15185.
		if (!specimen.getActivityStatus().equalsIgnoreCase(Constants.ACTIVITY_STATUS_VALUES[1])
				&&!specimen.getActivityStatus().equalsIgnoreCase(Constants.ACTIVITY_STATUS_VALUES[2])
				&& !specimen.getActivityStatus().equalsIgnoreCase(Constants.ACTIVITY_STATUS_VALUES[3]))
		{

			throw this.getBizLogicException(null, "errors.item.selected", ApplicationProperties.getValue("disposaleventparameters.activityStatus"));
		}

		if(specimen.getId() != null)
		{
			SpecimenUtil.validateSpecimenStatus(specimen.getId(), dao);
		}

		return true;
	}

	/**
	 * Validate Label For Single Specimen.
	 *
	 * @param specimen Specimen.
	 *
	 * @throws BizLogicException BizLogicException.
	 */
	private void validateLabelForSingleSpecimen(Specimen specimen) throws BizLogicException
	{


		boolean generateLabel = false;

		if(specimen.getSpecimenRequirement() == null && specimen.getId() != null)
		{
			String hql = "select specimen.specimenRequirement from edu.wustl.catissuecore.domain.Specimen as specimen"
				+" where specimen.id="+ specimen.getId();

			List<Object[]> list=null;
			try
			{
				list=AppUtility.executeQuery(hql);
				if(list!=null && !list.isEmpty())
				{
					Object object = list.get(0);

					specimen.setSpecimenRequirement((SpecimenRequirement)object);
				}
				if(specimen.getLineage() == null || !specimen.getLineage().equals("New"))
				{
						initSCG(specimen);
				}
			if(!edu.wustl.catissuecore.util.global.Variables.isSpecimenLabelGeneratorAvl)
			{
				if ((specimen.getLabel() == null || specimen.getLabel().equals(""))
						&& specimen.getCollectionStatus().equalsIgnoreCase("Collected"))
				{
					throw this.getBizLogicException(null, "label.mandatory", "");
				}
			}
			}
			catch(ApplicationException ex)
			{
				throw this.getBizLogicException(ex, ex.getErrorKeyAsString(), ex.getLogMessage());
			}
		}

	}

	/**
	 * Inits the scg.
	 *
	 * @param specimen the specimen
	 *
	 * @return true, if inits the scg
	 *
	 * @throws ApplicationException 	 * @throws BizLogicException the biz logic exception
	 */
	private void initSCG(Specimen specimen) throws BizLogicException
	{
		String hql = null;
		try
		{
			if(specimen.getParentSpecimen() != null && specimen.getParentSpecimen().getId() != null)
			{
				hql = "select specimen.specimenCollectionGroup.collectionProtocolRegistration.protocolParticipantIdentifier, " +
						"specimen.specimenCollectionGroup.id, "+
						"specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.specimenLabelFormat, "+
						"specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.derivativeLabelFormat, "+
						"specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.aliquotLabelFormat "+
				"from edu.wustl.catissuecore.domain.Specimen as specimen where specimen.id ="+ specimen.getParentSpecimen().getId();
			}
			else if(specimen.getParentSpecimen() != null && !Validator.isEmpty(specimen.getParentSpecimen().getLabel()))
			{
				hql = "select specimen.specimenCollectionGroup.collectionProtocolRegistration.protocolParticipantIdentifier, " +
						"specimen.specimenCollectionGroup.id, "+
						"specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.specimenLabelFormat, "+
						"specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.derivativeLabelFormat, "+
						"specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.aliquotLabelFormat "+
				"from edu.wustl.catissuecore.domain.Specimen as specimen where specimen.label ='"+ specimen.getParentSpecimen().getLabel()+"'";
			}
			else
			{
				hql = "select scg.collectionProtocolRegistration.protocolParticipantIdentifier, " +
				"scg.id, "  +
				"scg.collectionProtocolRegistration.collectionProtocol.specimenLabelFormat, "+
				"scg.collectionProtocolRegistration.collectionProtocol.derivativeLabelFormat, "+
				"scg.collectionProtocolRegistration.collectionProtocol.aliquotLabelFormat "+
				"from edu.wustl.catissuecore.domain.SpecimenCollectionGroup as scg where scg.id ="+ specimen.getSpecimenCollectionGroup().getId();
			}
			List list = AppUtility.executeQuery(hql);
			Object[] obje = (Object[])list.get(0);

			String PPI=null;
			if(obje[0] != null)
			{
				PPI= obje[0].toString();
			}
			Long scgId = Long.valueOf(obje[1].toString());

			String specimenLabelFormat = null;
			if(obje[2] != null)
			{
				specimenLabelFormat = obje[2].toString();
			}

			String derivativeLabelFormat = null;
			if(obje[3] != null)
			{
				derivativeLabelFormat = obje[3].toString();
			}

			String aliquotLabelFormat = null;
			if(obje[4] != null)
			{
				aliquotLabelFormat = obje[4].toString();
			}

			SpecimenCollectionGroup scg = new SpecimenCollectionGroup();
			scg.setId(scgId);
			CollectionProtocolRegistration cpr = new CollectionProtocolRegistration();
			CollectionProtocol collectionProtocol = new CollectionProtocol();
			collectionProtocol.setSpecimenLabelFormat(specimenLabelFormat);
			collectionProtocol.setDerivativeLabelFormat(derivativeLabelFormat);
			collectionProtocol.setAliquotLabelFormat(aliquotLabelFormat);

			cpr.setProtocolParticipantIdentifier(PPI);

			cpr.setCollectionProtocol(collectionProtocol);
			specimen.setSpecimenCollectionGroup(scg);
			specimen.getSpecimenCollectionGroup().setCollectionProtocolRegistration(cpr);
		}
		catch (ApplicationException e)
		{
			throw this.getBizLogicException(e, e.getErrorKeyAsString(), e.getMessage());
		}
	}


	/**
	 * validate Derived Specimens.
	 *
	 * @param specimen the specimen
	 * @param dao the dao
	 * @param operation the operation
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	private void validateDerivedSpecimens(Specimen specimen, DAO dao, String operation)
			throws BizLogicException
	{
		//final boolean result = false;
		final Collection derivedSpecimens = specimen.getChildSpecimenCollection();
		if (derivedSpecimens != null)
		{
			final Iterator iterator = derivedSpecimens.iterator();
			// validate derived specimens
			int count = 0;
			while (iterator.hasNext())
			{
				final Specimen derivedSpecimen = (Specimen) iterator.next();
				derivedSpecimen.setSpecimenCharacteristics(specimen.getSpecimenCharacteristics());
				derivedSpecimen.setSpecimenCollectionGroup(specimen.getSpecimenCollectionGroup());
				// 11177 S
				if (derivedSpecimen.getPathologicalStatus() == null
						|| Constants.DOUBLE_QUOTES.equals(derivedSpecimen.getPathologicalStatus()))
				{
					derivedSpecimen.setPathologicalStatus(specimen.getPathologicalStatus());
				}
				// derivedSpecimen.setPathologicalStatus(specimen.
				// getPathologicalStatus());
				// 11177 E
				derivedSpecimen.getParentSpecimen().setId(specimen.getId());
				try
				{
					this.validateSingleSpecimen(derivedSpecimen, dao, operation, false);
				}
				catch (final BizLogicException exp)
				{
					final int derivedSpecimenCount = count + 1;
					StringBuffer message =new StringBuffer(exp.getMessage());
					message .append(" (This message is for Derived Specimen " ).append(derivedSpecimenCount)
							.append(" of Parent Specimen number");
					LOGGER.error(message.toString(), exp);
					throw this.getBizLogicException(exp, "msg.for.derived.spec", Integer.valueOf(derivedSpecimenCount)
							.toString());
				}

				/*
				 * if (!result) { break; }
				 */
				count++;
			}
		}
	}

	/**
	 * Validate enumerated data.
	 *
	 * @param specimen Specimen to validate
	 * @param operation Add/Edit
	 * @param validator Validator ObjectClass contains the methods used for validation
	 * of the fields in the userform
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void validateEnumeratedData(Specimen specimen, String operation, Validator validator)
			throws BizLogicException
	{
		final List specimenClassList = CDEManager.getCDEManager().getPermissibleValueList(
				Constants.CDE_NAME_SPECIMEN_CLASS, null);
		final String specimenClass = AppUtility.getSpecimenClassName(specimen);
		if (!Validator.isEnumeratedValue(specimenClassList, specimenClass))
		{
			throw this.getBizLogicException(null, "protocol.class.errMsg", "");
		}

		if (!Validator.isEnumeratedValue(AppUtility.getSpecimenTypes(specimenClass), specimen
				.getSpecimenType()))
		{
			throw this.getBizLogicException(null, "protocol.type.errMsg", "");
		}
		/*
		 * bug # 7594 if (operation.equals(Constants.EDIT)) { if
		 * (specimen.getCollectionStatus() != null &&
		 * specimen.getCollectionStatus().equals("Collected") &&
		 * !specimen.getAvailable().booleanValue()) { throw new
		 * DAOException(ApplicationProperties
		 * .getValue("specimen.available.operation")); } }
		 */
		if (operation.equals(Constants.ADD))
		{
			if ((specimen.getIsAvailable() == null)
					|| (!specimen.getIsAvailable().booleanValue() && !"Pending".equals(specimen
							.getCollectionStatus())))
			{
				throw this.getBizLogicException(null, "specimen.available.errMsg", "");
			}

			if (!Status.ACTIVITY_STATUS_ACTIVE.toString().equals(specimen.getActivityStatus()))
			{
				throw this.getBizLogicException(null, "activityStatus.active.errMsg", "");
			}
		}
		else
		{
			if (!Validator.isEnumeratedValue(Constants.ACTIVITY_STATUS_VALUES, specimen
					.getActivityStatus()))
			{
				throw this.getBizLogicException(null, "activityStatus.errMsg", "");
			}
		}
		if (specimen.getCreatedOn() != null && specimen.getLineage() != null
				&& !specimen.getLineage().equalsIgnoreCase(Constants.NEW_SPECIMEN))
		{
			final String tempDate = Utility.parseDateToString(specimen.getCreatedOn(),
					CommonServiceLocator.getInstance().getDatePattern());
			if (!validator.checkDate(tempDate))
			{
				throw this.getBizLogicException(null, "error.invalid.createdOnDate", "");
			}
		}
		if (!Validator.isEnumeratedValue(Constants.SPECIMEN_COLLECTION_STATUS_VALUES, specimen
				.getCollectionStatus()))
		{
			throw this.getBizLogicException(null, "errors.item.format", ApplicationProperties.getValue("specimen.collectionStatus"));
		}
	}

	/**
	 * Specimen to validate.
	 *
	 * @param validator Validator ObjectClass contains the methods used for validation
	 * of the fields in the userform
	 * @param specimen the specimen
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void validateSpecimenData(Specimen specimen, Validator validator)
			throws BizLogicException
	{
		final SpecimenCollectionGroup scg = specimen.getSpecimenCollectionGroup();

		if (specimen.getParentSpecimen() == null
				&& (scg == null || ((scg.getId() == null || scg.getId().equals("-1")) && (scg
						.getGroupName() == null || scg.getGroupName().equals("")))))
		{
			final String message = ApplicationProperties
					.getValue("specimen.specimenCollectionGroup");
			throw this.getBizLogicException(null, "errors.item.required", message);
		}
		/*
		 * if (!Variables.isSpecimenLabelGeneratorAvl) { if
		 * (specimen.getParentSpecimen() != null && ((Specimen)
		 * specimen.getParentSpecimen()).getLabel() == null &&
		 * specimen.getParentSpecimen().getId() == null) { String message =
		 * ApplicationProperties.getValue("createSpecimen.parent"); throw
		 * getBizLogicException(null, "errors.item.required", message); } }
		 */
		if (validator.isEmpty(specimen.getSpecimenClass()))
		{
			final String message = ApplicationProperties.getValue("specimen.type");
			throw this.getBizLogicException(null, "errors.item.required", message);
		}
		if (validator.isEmpty(specimen.getSpecimenType()))
		{
			final String message = ApplicationProperties.getValue("specimen.subType");
			throw this.getBizLogicException(null, "errors.item.required", message);
		}
	}

	/**
	 * Validate specimen characterstics.
	 *
	 * @param specimen Specimen to validate
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void validateSpecimenCharacterstics(Specimen specimen) throws BizLogicException
	{
		SpecimenCharacteristics characters = specimen.getSpecimenCharacteristics();

		if (characters == null)
		{
			characters = new SpecimenCharacteristics();
			specimen.setSpecimenCharacteristics(characters);
			//throw this.getBizLogicException(null, "specimen.characteristics.errMsg", "");
		}

		if (specimen.getSpecimenCollectionGroup() != null)
		{
			final List tissueSiteList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_TISSUE_SITE, null);
			if (!Validator.isEnumeratedValue(tissueSiteList, characters.getTissueSite()))
			{
				if (specimen.getParentSpecimen() == null)
				{
					throw this.getBizLogicException(null, "protocol.tissueSite.errMsg", "");
				}
			}
			final List tissueSideList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_TISSUE_SIDE, null);

			if (!Validator.isEnumeratedValue(tissueSideList, characters.getTissueSide()))
			{
				if (specimen.getParentSpecimen() == null)
				{
					throw this.getBizLogicException(null, "specimen.tissueSide.errMsg", "");
				}
			}
			final List pathologicalStatusList = CDEManager.getCDEManager()
					.getPermissibleValueList(Constants.CDE_NAME_PATHOLOGICAL_STATUS, null);
			if (!Validator.isEnumeratedValue(pathologicalStatusList, specimen
					.getPathologicalStatus()))
			{
				if (specimen.getParentSpecimen() == null)
				{
					throw this
							.getBizLogicException(null, "protocol.pathologyStatus.errMsg", "");
				}
			}
		}
	}

	/**
	 * Validate specimen event.
	 *
	 * @param specimen Specimen to validate
	 * @param validator Validator ObjectClass contains the methods used for validation
	 * of the fields in the userform
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void validateSpecimenEvent(Specimen specimen, Validator validator)
			throws BizLogicException
	{
		try
		{
			Collection<SpecimenEventParameters> specimenEventCollection = null;
			specimenEventCollection = specimen.getSpecimenEventCollection();
			if (specimenEventCollection != null && !specimenEventCollection.isEmpty())
			{
				final Iterator<SpecimenEventParameters> specimenEventCollectionIterator = specimenEventCollection
						.iterator();
				while (specimenEventCollectionIterator.hasNext())
				{
					final Object eventObject = specimenEventCollectionIterator.next();
					EventsUtil.validateEventsObject(eventObject, validator);
				}
			}
			else
			{
				if (specimen.getParentSpecimen() == null
						&& (specimen.getCollectionStatus() == null))
				{
					throw this.getBizLogicException(null, "error.specimen.noevents", "");
				}
			}
		}
		catch (final ApplicationException exp)
		{
			LOGGER.error(exp.getMessage(), exp);
			throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp.getMsgValues());
		}
	}

	/**
	 * Validate storage container.
	 *
	 * @param specimen Specimen to validate
	 * @param dao DAO object
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void validateStorageContainer(Specimen specimen, DAO dao) throws BizLogicException
	{
		try
		{
			if (specimen.getSpecimenPosition() != null
					&& specimen.getSpecimenPosition().getStorageContainer() != null
					&& (specimen.getSpecimenPosition().getStorageContainer().getId() == null && specimen
							.getSpecimenPosition().getStorageContainer().getName() == null))
			{
				final String message = ApplicationProperties.getValue("specimen.storageContainer");
				throw this.getBizLogicException(null, "errors.invalid", message);
			}
			if (specimen.getSpecimenPosition() != null
					&& specimen.getSpecimenPosition().getStorageContainer() != null
					&& specimen.getSpecimenPosition().getStorageContainer().getName() != null)
			{
				final StorageContainer storageContainerObj = specimen.getSpecimenPosition()
						.getStorageContainer();
				final String sourceObjectName = StorageContainer.class.getName();
				final String[] selectColumnName = {"id"};
				// String[] whereColumnName = {"name"};
				// String[] whereColumnCondition = {"="};
				// Object[] whereColumnValue =
				// {specimen.getSpecimenPosition().getStorageContainer
				// ().getName()};
				// String joinCondition = null;
				final String storageContainerName = specimen.getSpecimenPosition()
						.getStorageContainer().getName();

				final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
				queryWhereClause.addCondition(new EqualClause("name", storageContainerName));
				final List list = dao
						.retrieve(sourceObjectName, selectColumnName, queryWhereClause);

				if (!list.isEmpty())
				{
					storageContainerObj.setId((Long) list.get(0));
					specimen.getSpecimenPosition().setStorageContainer(storageContainerObj);
				}
				else
				{
					final String message = ApplicationProperties
							.getValue("specimen.storageContainer");
					throw this.getBizLogicException(null, "errors.invalid", message);
				}
			}
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * Validate external identifier.
	 *
	 * @param specimen Specimen to validate
	 * @param validator Validator ObjectClass contains the methods used for validation
	 * of the fields in the userform
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void validateExternalIdentifier(Specimen specimen, Validator validator)
			throws BizLogicException
	{
		final Collection<ExternalIdentifier> extIdentifierCollection = specimen
				.getExternalIdentifierCollection();
		ExternalIdentifier extIdentifier = null;
		if (extIdentifierCollection != null && !extIdentifierCollection.isEmpty())
		{
			final Iterator<ExternalIdentifier> itr = extIdentifierCollection.iterator();
			while (itr.hasNext())
			{
				extIdentifier = (ExternalIdentifier) itr.next();
				if (extIdentifier.getName() != null || extIdentifier.getValue() != null)
				{
					if (validator.isEmpty(extIdentifier.getName()))
					{
						final String message = ApplicationProperties.getValue("specimen.msg");
						throw this.getBizLogicException(null,
								"errors.specimen.externalIdentifier.missing", message);
					}
					if (validator.isEmpty(extIdentifier.getValue()))
					{
						final String message = ApplicationProperties.getValue("specimen.msg");
						throw this.getBizLogicException(null,
								"errors.specimen.externalIdentifier.missing", message);
					}
				}
			}
		}
	}

	/**
	 * Validate bio hazard.
	 *
	 * @param specimen Specimen to validate
	 * @param validator Validator ObjectClass contains the methods used for validation
	 * of the fields in the userform
	 * @param dao the dao
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void validateBioHazard(Specimen specimen, Validator validator, DAO dao) throws BizLogicException
	{
		final Collection<Biohazard> bioHazardCollection = specimen.getBiohazardCollection();
		Biohazard biohazard = null;
		if (bioHazardCollection != null && !bioHazardCollection.isEmpty())
		{
			final Iterator<Biohazard> itr = bioHazardCollection.iterator();
			while (itr.hasNext())
			{
				biohazard = (Biohazard) itr.next();
				if (!validator.isValidOption(biohazard.getType()))
				{
					final String message = ApplicationProperties.getValue("newSpecimen.msg");
					throw this.getBizLogicException(null, "errors.newSpecimen.biohazard.missing",
							message);
				}
				if (biohazard.getId() == null)
				{
					try
					{
						//added for bug #15860
						final String sourceObjectName = Biohazard.class.getName();
						final String[] selectColumnName = {"id"};
						final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
						queryWhereClause.addCondition(new EqualClause("name", biohazard.getName())).andOpr();
						queryWhereClause.addCondition(new EqualClause("type", biohazard.getType()));
						final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
						if(list.isEmpty())
						{
							final String message = ApplicationProperties.getValue("specimen.biohazards");
							throw this.getBizLogicException(null, "errors.invalid",
								message + ": Name - " + biohazard.getName() + " : Type -" + biohazard.getType());
						}
						else
						{
							biohazard.setId((Long)list.get(0));
						}
					}
					catch (DAOException daoExp)
					{
						LOGGER.error(daoExp.getMessage(), daoExp);
						throw new BizLogicException(daoExp.getErrorKey(), daoExp, daoExp
								.getMsgValues());
					}
				}
			}
		}
	}

	/**
	 * Validate fields.
	 *
	 * @param specimen Specimen to validate
	 * @param dao DAO object
	 * @param partOfMulipleSpecimen boolean
	 *
	 * @throws BizLogicException Database related exception
	 */
	private void validateFields(Specimen specimen, DAO dao,
			boolean partOfMulipleSpecimen) throws BizLogicException
	{
		try
		{
			final Validator validator = new Validator();

			if (partOfMulipleSpecimen)
			{
				if (specimen.getSpecimenCollectionGroup() == null
						|| validator.isEmpty(specimen.getSpecimenCollectionGroup().getGroupName()))
				{
					final String quantityString = ApplicationProperties
							.getValue("specimen.specimenCollectionGroup");
					throw this.getBizLogicException(null, "errors.item.required", quantityString);
				}
				final List spgList = dao.retrieve(SpecimenCollectionGroup.class.getName(),
						Constants.NAME, specimen.getSpecimenCollectionGroup().getGroupName());
				if (spgList.isEmpty())
				{
					throw this.getBizLogicException(null, "errors.item.unknown",
							"Specimen Collection Group "
									+ specimen.getSpecimenCollectionGroup().getGroupName());
				}
			}
			if (specimen.getInitialQuantity() == null)
			{
				final String quantityString = ApplicationProperties.getValue("specimen.quantity");
				throw this.getBizLogicException(null, "errors.item.required", quantityString);
			}

			if (specimen.getAvailableQuantity() == null)
			{
				final String quantityString = ApplicationProperties
						.getValue("specimen.availableQuantity");
				throw this.getBizLogicException(null, "errors.item.required", quantityString);
			}
			if (1 == new BigDecimal(String.valueOf(specimen.getAvailableQuantity()))
							.compareTo(new BigDecimal(String.valueOf(specimen.getInitialQuantity()))))
			{
				final String quantityString = ApplicationProperties
					.getValue("specimen.availableQuantity");
				throw this.getBizLogicException(null, "errors.availablequantity", quantityString);
			}
			/**
			 * This method gives first valid storage position to a specimen if
			 * it is not given If storage position is given it validates the
			 * storage position
			 **/
			 //Bug 15392
			/*StorageContainerUtil.validateStorageLocationForSpecimen(specimen, dao,
					this.storageContainerIds);*/
		}
		catch (final ApplicationException exp)
		{
			LOGGER.error(exp.getMessage(), exp);
			throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp.getMsgValues());
		}

	}

	/**
	 * This function checks whether the storage position of a specimen is
	 * changed or not & returns the status accordingly.
	 *
	 * @param oldSpecimen Persistent Object
	 * @param newSpecimen New Object
	 *
	 * @return boolean
	 */
	private boolean isStoragePositionChanged(Specimen oldSpecimen, Specimen newSpecimen)
	{
		boolean isEqual = true;
		StorageContainer oldContainer = null;
		StorageContainer newContainer = null;
		if (oldSpecimen.getSpecimenPosition() != null)
		{
			oldContainer = oldSpecimen.getSpecimenPosition().getStorageContainer();
		}
		if (newSpecimen.getSpecimenPosition() != null)
		{
			newContainer = newSpecimen.getSpecimenPosition().getStorageContainer();
		}
		if (oldContainer == null && newContainer == null)
		{
			return false;
		}
		if ((oldContainer == null && newContainer != null)
				|| (oldContainer != null && newContainer == null))
		{
			if (oldSpecimen.getCollectionStatus().equals("Pending")
					&& newSpecimen.getCollectionStatus().equals("Collected")
					&& newSpecimen.getSpecimenPosition() != null)
			{
				return false;
			}
			return isEqual;
		}
		if (oldContainer.getId().longValue() == newContainer.getId().longValue())
		{
			if (oldSpecimen != null && oldSpecimen.getSpecimenPosition() != null
					&& newSpecimen != null && newSpecimen.getSpecimenPosition() != null)
			{
				final int oldConatinerPos1 = oldSpecimen.getSpecimenPosition()
						.getPositionDimensionOne();
				final int newConatinerPos1 = newSpecimen.getSpecimenPosition()
						.getPositionDimensionOne();
				final int oldConatinerPos2 = oldSpecimen.getSpecimenPosition()
						.getPositionDimensionTwo();
				final int newConatinerPos2 = newSpecimen.getSpecimenPosition()
						.getPositionDimensionTwo();

				if (oldConatinerPos1 == newConatinerPos1 && oldConatinerPos2 == newConatinerPos2)
				{
					isEqual = false;
				}
			}
		}
		return isEqual;
	}

	/**
	 * Set event parameters from parent specimen to derived specimen.
	 *
	 * @param parentSpecimen specimen
	 * @param deriveSpecimen Derived Specimen
	 *
	 * @return set
	 */
	private Set<AbstractDomainObject> populateDeriveSpecimenEventCollection(
			Specimen parentSpecimen, Specimen deriveSpecimen)
	{
		final Set<AbstractDomainObject> deriveEventCollection = new HashSet<AbstractDomainObject>();
		final Set<SpecimenEventParameters> parentSpecimeneventCollection = (Set<SpecimenEventParameters>) parentSpecimen
				.getSpecimenEventCollection();
		SpecimenEventParameters specimenEventParameters = null;
		SpecimenEventParameters deriveSpecimenEventParameters = null;
		try
		{
			if (parentSpecimeneventCollection != null
					&& (deriveSpecimen.getSpecimenEventCollection() == null || deriveSpecimen
							.getSpecimenEventCollection().isEmpty()))
			{
				for (final SpecimenEventParameters specimenEventParameters2 : parentSpecimeneventCollection)
				{
					specimenEventParameters = (SpecimenEventParameters) specimenEventParameters2;
					deriveSpecimenEventParameters = (SpecimenEventParameters) specimenEventParameters
							.clone();
					deriveSpecimenEventParameters.setId(null);
					deriveSpecimenEventParameters.setSpecimen(deriveSpecimen);
					deriveEventCollection.add(deriveSpecimenEventParameters);
				}
			}
		}
		catch (final CloneNotSupportedException exception)
		{
			LOGGER.error(exception.getMessage(), exception);
		}
		return deriveEventCollection;
	}

	/**
	 * This method will retrive no of specimen in the catissue_specimen table.
	 *
	 * @param sessionData Session data
	 *
	 * @return Count of Specimen
	 */
	public int totalNoOfSpecimen(SessionDataBean sessionData)
	{
		final String sql = "select MAX(IDENTIFIER) from CATISSUE_SPECIMEN";
		JDBCDAO jdbcDao = null;
		int noOfRecords = 0;
		try
		{
			jdbcDao = this.openJDBCSession();
			final List resultList = jdbcDao.executeQuery(sql);
			String number = (String) ((List) resultList.get(0)).get(0);
			if (number == null || number.equals(""))
			{
				number = "0";
			}
			noOfRecords = Integer.parseInt(number);
			jdbcDao.closeSession();
		}
		catch (final Exception exception)
		{
			LOGGER.error(exception.getMessage(), exception);
		}

		return noOfRecords;
	}

	/**
	 * This function will retrive SCG Id from SCG Name.
	 *
	 * @param specimen Current Specimen
	 * @param dao DAO Object
	 *
	 * @throws BizLogicException Database related exception
	 */
	public void retriveSCGIdFromSCGName(Specimen specimen, DAO dao) throws BizLogicException
	{
		try
		{
			final String specimenCollGpName = specimen.getSpecimenCollectionGroup().getGroupName();
			if (specimenCollGpName != null && !specimenCollGpName.equals(""))
			{
				final String[] selectColumnName = {"id"};
				// String[] whereColumnName = {"name"};
				// String[] whereColumnCondition = {"="};
				// String[] whereColumnValue = {specimenCollGpName};

				final QueryWhereClause queryWhereClause = new QueryWhereClause(
						SpecimenCollectionGroup.class.getName());
				queryWhereClause.addCondition(new EqualClause("name", specimenCollGpName));

				final List scgList = dao.retrieve(SpecimenCollectionGroup.class.getName(),
						selectColumnName, queryWhereClause);

				if (scgList != null && !scgList.isEmpty())
				{
					specimen.getSpecimenCollectionGroup().setId(((Long) scgList.get(0)));
				}
			}
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * This method updates the consents and specimen based on the the consent
	 * withdrawal option.
	 *
	 * @param specimen New object
	 * @param oldSpecimen Old Object
	 * @param dao DAO object
	 * @param sessionDataBean Session Details
	 *
	 * @throws BizLogicException Database exception
	 */
	private void updateConsentWithdrawStatus(Specimen specimen, DAO dao,
			SessionDataBean sessionDataBean) throws BizLogicException
	{
		try
		{
			if (!specimen.getConsentWithdrawalOption().equalsIgnoreCase(
					Constants.WITHDRAW_RESPONSE_NOACTION))
			{

				final String consentWithdrawOption = specimen.getConsentWithdrawalOption();
				final Collection<ConsentTierStatus> consentTierStatusCollection = specimen
						.getConsentTierStatusCollection();
				final Iterator<ConsentTierStatus> itr = consentTierStatusCollection.iterator();
				while (itr.hasNext())
				{
					final ConsentTierStatus status = (ConsentTierStatus) itr.next();
					final long consentTierID = status.getConsentTier().getId().longValue();
					if (status.getStatus().equalsIgnoreCase(Constants.WITHDRAWN))
					{

						ConsentUtil.updateSpecimenStatus(specimen, consentWithdrawOption,
								consentTierID, dao, sessionDataBean);
					}
				}
			}
		}
		catch (final ApplicationException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw this.getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
		}
	}

	/**
	 * This method is used to update the consent status of child specimens as
	 * per the option selected by the user.
	 *
	 * @param specimen New object
	 * @param oldSpecimen Old Object
	 * @param dao DAO object
	 *
	 * @throws BizLogicException Database exception
	 * @throws DAOException : DAOException
	 */
	private void updateConsentStatus(Specimen specimen, DAO dao, Specimen oldSpecimen)
			throws BizLogicException, DAOException
	{
		if (!specimen.getApplyChangesTo().equalsIgnoreCase(Constants.APPLY_NONE))
		{
			final String applyChangesTo = specimen.getApplyChangesTo();
			final Collection<ConsentTierStatus> consentTierStatusCollection = specimen
					.getConsentTierStatusCollection();
			final Collection<ConsentTierStatus> oldConsentTierStatusCollection = oldSpecimen
					.getConsentTierStatusCollection();
			final Iterator<ConsentTierStatus> itr = consentTierStatusCollection.iterator();
			while (itr.hasNext())
			{
				final ConsentTierStatus status = (ConsentTierStatus) itr.next();
				final long consentTierID = status.getConsentTier().getId().longValue();
				final String statusValue = status.getStatus();
				final Collection childSpecimens = (Collection) this.retrieveAttribute(dao,
						Specimen.class, specimen.getId(), "elements(childSpecimenCollection)");
				if (childSpecimens != null)
				{

					final Iterator childItr = childSpecimens.iterator();
					while (childItr.hasNext())
					{
						final Specimen childSpecimen = (Specimen) childItr.next();
						ConsentUtil.updateSpecimenConsentStatus(childSpecimen, applyChangesTo,
								consentTierID, statusValue, consentTierStatusCollection,
								oldConsentTierStatusCollection, dao);
					}
				}
			}
		}
	}

	/**
	 * This function is used to update specimens and their dervied & aliquot
	 * specimens.
	 *
	 * @param newSpecimenCollection List of specimens to update along with children specimens.
	 * @param sessionDataBean current user session information
	 * @param dao DAO object
	 *
	 * @throws BizLogicException If DAO fails to update one or more specimens this function
	 * will throw DAOException.
	 * @throws DAOException : DAOException
	 */
	public void updateAnticipatorySpecimens(DAO dao,
			Collection<AbstractDomainObject> newSpecimenCollection, SessionDataBean sessionDataBean)
			throws BizLogicException, DAOException
	{
		this.updateMultipleSpecimens(dao, newSpecimenCollection, sessionDataBean, true);
	}

	/**
	 * This function is used to bulk update multiple specimens. If any specimen
	 * contains children specimens those will be inserted
	 *
	 * @param newSpecimenCollection List of specimens to update along with new children specimens
	 * if any. 7
	 * @param sessionDataBean current user session information
	 *
	 * @throws BizLogicException If DAO fails to update one or more specimens this function
	 * will throw DAOException.
	 */
	public void bulkUpdateSpecimens(Collection<AbstractDomainObject> newSpecimenCollection,
			SessionDataBean sessionDataBean) throws BizLogicException
	{
		Iterator iterator = newSpecimenCollection.iterator();
		DAO dao = null;
		//int specimenCtr = Constants.FIRST_COUNT_1;
		int childSpecimenCtr = 0;
		try
		{
			dao = this.openDAOSession(sessionDataBean);
			while (iterator.hasNext())
			{
				final Specimen newSpecimen = (Specimen) iterator.next();
				if (newSpecimen.getSpecimenPosition() != null
						&& newSpecimen.getSpecimenPosition().getStorageContainer() != null
						&& newSpecimen.getSpecimenPosition().getStorageContainer().getId() == null)
				{
					newSpecimen.getSpecimenPosition().setStorageContainer(
							this.setStorageContainerId(dao, newSpecimen));
				}
			}
			iterator = newSpecimenCollection.iterator();
			while (iterator.hasNext())
			{
				final Specimen newSpecimen = (Specimen) iterator.next();
				final Specimen specimenDO = this.updateSingleSpecimen(dao, newSpecimen,
						sessionDataBean, false);
				final Collection<AbstractSpecimen> childrenSpecimenCollection = newSpecimen
						.getChildSpecimenCollection();
				if (childrenSpecimenCollection != null && !childrenSpecimenCollection.isEmpty())
				{
					childSpecimenCtr = this.updateChildSpecimen(sessionDataBean, dao,
							childSpecimenCtr, specimenDO, childrenSpecimenCollection);
				}
				//specimenCtr++;
			}
			//specimenCtr = 0;
			dao.commit();
			this.postInsert(newSpecimenCollection, dao, sessionDataBean);
		}
		catch (final ApplicationException exception)
		{
			LOGGER.error(exception.getMessage(), exception);
			try
			{
				dao.rollback();
			}
			catch (final DAOException e)
			{
				LOGGER.error(e.getMessage(), e);
				throw this.getBizLogicException(e, e.getErrorKeyName(), e.getMsgValues());
			}

			final String errMsg = this.getErrorMessage(exception, newSpecimenCollection,
					"bulkUpdateSpecimens");
			throw new BizLogicException(exception.getErrorKey(), exception, exception
					.getMsgValues(), errMsg);
		}
		finally
		{
			this.closeDAOSession(dao);
		}
	}

	/**
	 * Update child specimen.
	 *
	 * @param sessionDataBean Session details
	 * @param dao DAO Object
	 * @param childSpecimenCtr Count
	 * @param specimenDO Persistent object
	 * @param childrenSpecimenCollection childSpecimen Collection
	 *
	 * @return count
	 *
	 * @throws BizLogicException Database Exception
	 */
	private int updateChildSpecimen(SessionDataBean sessionDataBean, DAO dao, int childSpecimenCtr,
			Specimen specimenDO, Collection<AbstractSpecimen> childrenSpecimenCollection)
			throws BizLogicException
	{
		final Iterator<AbstractSpecimen> childIterator = childrenSpecimenCollection.iterator();
		while (childIterator.hasNext())
		{
			childSpecimenCtr++;
			final Specimen childSpecimen = (Specimen) childIterator.next();
			childSpecimen.setParentSpecimen(specimenDO);
			specimenDO.getChildSpecimenCollection().add(childSpecimen);
			this.insert(childSpecimen, dao, sessionDataBean);
		}
		childSpecimenCtr = 0;
		return childSpecimenCtr;
	}

	/**
	 * allocate Specimen Postions Recursively.
	 *
	 * @param newSpecimen the new specimen
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	private void allocateSpecimenPostionsRecursively(Specimen newSpecimen) throws BizLogicException
	{
		final StorageContainer container = this.getStorageContainer(newSpecimen);
		if (container != null && container.getName() != null)
		{
			container.setId(null);
		}

		this.allocatePositionForSpecimen(newSpecimen);
		if (newSpecimen.getChildSpecimenCollection() != null)
		{
			final Iterator<AbstractSpecimen> childrenIterator = newSpecimen
					.getChildSpecimenCollection().iterator();
			while (childrenIterator.hasNext())
			{
				final Specimen childSpecimen = (Specimen) childrenIterator.next();
				childSpecimen.setParentSpecimen(newSpecimen);
				//newSpecimen.getChildSpecimenCollection().add(childSpecimen);
				this.allocateSpecimenPostionsRecursively(childSpecimen);
			}
		}
	}

	/**
	 * get Storage Container.
	 *
	 * @param specimen the specimen
	 *
	 * @return the storage container
	 */
	private StorageContainer getStorageContainer(Specimen specimen)
	{
		StorageContainer stCont = null;
		final SpecimenPosition specimenPosition = specimen.getSpecimenPosition();
		if (specimenPosition != null)
		{
			stCont = specimenPosition.getStorageContainer();
		}
		return stCont;
	}

	/**
	 * Update multiple specimens.
	 *
	 * @param dao DAO object
	 * @param newSpecimenCollection Specimen Collection
	 * @param sessionDataBean Session Details
	 * @param updateChildrens boolean
	 *
	 * @throws BizLogicException Database exception
	 * @throws DAOException : DAOException
	 */
	protected void updateMultipleSpecimens(DAO dao,
			Collection<AbstractDomainObject> newSpecimenCollection,
			SessionDataBean sessionDataBean, boolean updateChildrens) throws BizLogicException,
			DAOException
	{
		Iterator<AbstractDomainObject> iterator = newSpecimenCollection.iterator();
		try
		{
			SpecimenCollectionGroup scg = null;
			while (iterator.hasNext())
			{
				final Specimen newSpecimen = (Specimen) iterator.next();
				if (scg == null)
				{
					scg = (SpecimenCollectionGroup) this.retrieveAttribute(dao, Specimen.class,
							newSpecimen.getId(), "specimenCollectionGroup");
				}
				newSpecimen.setSpecimenCollectionGroup(scg);
				this.allocateSpecimenPostionsRecursively(newSpecimen);

			}
			//iterator = newSpecimenCollection.iterator();
			//Bug 15392 start
			AbstractSpecimen array[] = new AbstractSpecimen[newSpecimenCollection.size()];
			newSpecimenCollection.toArray(array);
			//final Iterator<AbstractSpecimen> iterator = specimenCollection.iterator();
			//while (iterator.hasNext())
			Integer position1 = null;
			Integer position2 = null;
			//while (iterator.hasNext())
			storageContainerIds.clear();
			for(int i=0;i<array.length;i++)
			{
				final Specimen newSpecimen = (Specimen) array[i];
				String newSpecContName = "";
				Long newSpecContId = 0l;
				if(newSpecimen.getSpecimenPosition()!=null)
				{
					newSpecContName= newSpecimen.getSpecimenPosition().getStorageContainer().getName();
					newSpecContId = newSpecimen.getSpecimenPosition().getStorageContainer().getId();
					position1 = newSpecimen.getSpecimenPosition().getPositionDimensionOne();
					position2 = newSpecimen.getSpecimenPosition().getPositionDimensionTwo();
				}
//				final Specimen newSpecimen = (Specimen) iterator.next();
				if(((position1 == null || position2 == null) && i!=0))
				{
				    //bug 15448
					if(((Specimen) array[i-1]).getSpecimenPosition() != null)
					{
						String prevSpecContName = ((Specimen) array[i-1]).getSpecimenPosition().getStorageContainer().getName();
						Long precSpecContId = ((Specimen) array[i-1]).getSpecimenPosition().getStorageContainer().getId();
						if(isContNameEqual(newSpecContName, prevSpecContName)
								|| isContIdEqual(newSpecContId, precSpecContId))
						{
							position1 = ((Specimen) array[i-1]).getSpecimenPosition().getPositionDimensionOne();
							position2 = ((Specimen) array[i-1]).getSpecimenPosition().getPositionDimensionTwo();
						}
					}
				}
				this.setSpecimenStorageRecursively(newSpecimen, dao, sessionDataBean,
							position1,position2);
				//bug 15260 end
			}
//			generateLabel(newSpecimenCollection);
			iterator = newSpecimenCollection.iterator();

			while (iterator.hasNext())
			{
				final Specimen newSpecimen = (Specimen) iterator.next();
				// validateCollectionStatus(newSpecimen );
				this.updateSingleSpecimen(dao, newSpecimen, sessionDataBean, updateChildrens);
			}
			this.postInsert(newSpecimenCollection, dao, sessionDataBean);
		}

		finally
		{
			this.storageContainerIds.clear();
		}
	}

	/**
	 * @param newSpecContId
	 * @param precSpecContId
	 * @return
	 */
	private boolean isContIdEqual(Long newSpecContId, Long precSpecContId)
	{
		return (newSpecContId != null && newSpecContId != 0 && precSpecContId != null && precSpecContId != 0
				&& precSpecContId == newSpecContId);
	}

	/**
	 * @param newSpecContName
	 * @param prevSpecContName
	 * @return
	 */
	private boolean isContNameEqual(String newSpecContName, String prevSpecContName)
	{
		return (!Validator.isEmpty(newSpecContName) && !Validator.isEmpty(prevSpecContName) &&
				newSpecContName.equals(prevSpecContName));
	}

	// Bug 11481 S
	/**
	 * This function validate if CP is closed then anticipatory specimen can not
	 * be collected.
	 *
	 * @param dao the dao
	 * @param newSpecimen : newSpecimen
	 * @param specimenDO : specimenDO
	 *
	 * @throws BizLogicException Database exception
	 */
	public void validateIfCPisClosed(Specimen specimenDO, Specimen newSpecimen, DAO dao)
			throws BizLogicException
	{
		try
		{
			final String lineage = specimenDO.getLineage();
			final Long scgId = newSpecimen.getSpecimenCollectionGroup().getId();
			final CollectionProtocol collProtocol = new CollectionProtocol();
			// cp = getActivityStatusOfCollectionProtocol(dao, scgId);

			final AbstractSpecimenCollectionGroup specimenCollectionGroup = (AbstractSpecimenCollectionGroup) dao
					.retrieveById("edu.wustl.catissuecore.domain.SpecimenCollectionGroup", scgId);
			String activityStatus = "";
			// if(specimenCollectionGroup instanceof SpecimenCollectionGroup)
			// {

			final Long colpId = (Long) specimenCollectionGroup.getCollectionProtocolRegistration()
					.getCollectionProtocol().getId();
			activityStatus = specimenCollectionGroup.getCollectionProtocolRegistration()
					.getCollectionProtocol().getActivityStatus();

			collProtocol.setId(colpId);
			collProtocol.setActivityStatus(activityStatus);
			// }
			// String activityStatus = cp.getActivityStatus();
			final String oldCollectionStatus = specimenDO.getCollectionStatus();
			final String newCollectionStatus = newSpecimen.getCollectionStatus();
			if ("New".equals(lineage)
					&& activityStatus.equals(Status.ACTIVITY_STATUS_CLOSED.toString()))
			{
				if (!oldCollectionStatus.equalsIgnoreCase(newCollectionStatus))
				{
					this.checkStatus(dao, collProtocol, "Collection Protocol");
				}
			}
		}
		catch (final DAOException exp)
		{
			LOGGER.error(exp.getMessage(),exp);
			throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp.getMsgValues());
		}
	}

	// Bug 11481 E

	/**
	 * Update single specimen.
	 *
	 * @param dao DAO object
	 * @param sessionDataBean Session Details
	 * @param updateChildrens boolean
	 * @param newSpecimen Specimen Object
	 *
	 * @return Specimen object
	 *
	 * @throws BizLogicException Database exception
	 */
	public Specimen updateSingleSpecimen(DAO dao, Specimen newSpecimen,
			SessionDataBean sessionDataBean, boolean updateChildrens) throws BizLogicException
	{
		try
		{
			Specimen specimenDO = null;
			if (this.isAuthorized(dao, newSpecimen, sessionDataBean))
			{
				final Object object = dao.retrieveById(Specimen.class.getName(), newSpecimen
						.getId());
				if (object != null)
				{
					specimenDO = (Specimen) object;
					// Bug 11481 S
					this.validateIfCPisClosed(specimenDO, newSpecimen, dao);
					// Bug 11481 E
					this.updateSpecimenDomainObject(dao, newSpecimen, specimenDO, sessionDataBean);
					if(specimenDO.getParentSpecimen() != null && !Constants.COLLECTION_STATUS_COLLECTED.equals(((Specimen)specimenDO.getParentSpecimen()).getCollectionStatus()))
					{
						throw this.getBizLogicException(null, "errors.item", ApplicationProperties.getValue("specimen.parent.label.required"));
					}
					if (updateChildrens)
					{
						this.updateChildrenSpecimens(dao, newSpecimen, specimenDO, sessionDataBean);
					}
					dao.update(specimenDO);
				}
				else
				{
					throw this.getBizLogicException(null, "invalid.label", newSpecimen.getLabel());
				}
			}
			return specimenDO;
		}
		catch (final DAOException exception)
		{
			LOGGER.error(exception.getMessage(), exception);
			throw this.getBizLogicException(exception, exception.getErrorKeyName(), exception
					.getMsgValues());
		}

	}

	/**
	 * Update children specimens.
	 *
	 * @param dao DAO object
	 * @param specimenVO New Object
	 * @param specimenDO Persistent object
	 * @param sessionDataBean Session Details
	 *
	 * @throws BizLogicException Database exception
	 */
	private void updateChildrenSpecimens(DAO dao, Specimen specimenVO, Specimen specimenDO,
			SessionDataBean sessionDataBean) throws BizLogicException
	{
		final Collection<AbstractSpecimen> childrenSpecimens = specimenDO
				.getChildSpecimenCollection();
		if (childrenSpecimens == null || childrenSpecimens.isEmpty())
		{
			return;
		}

		List<AbstractSpecimen> lstSpecimen = new ArrayList<AbstractSpecimen>();
		Iterator<AbstractSpecimen> itr = childrenSpecimens.iterator();
		while(itr.hasNext())
		{
			lstSpecimen.add(itr.next());
		}

		final Comparator spIdComp = new IdComparator();
		Collections.sort(lstSpecimen, spIdComp);

		final Iterator<AbstractSpecimen> iterator = lstSpecimen.iterator();
		while (iterator.hasNext())
		{
			final Specimen specimen = (Specimen) iterator.next();
			final Specimen relatedSpecimen = this.getCorelatedSpecimen(specimen.getId(), specimenVO
					.getChildSpecimenCollection());
			if (relatedSpecimen != null)
			{
				this.updateSpecimenDomainObject(dao, relatedSpecimen, specimen, sessionDataBean);
				this.updateChildrenSpecimens(dao, relatedSpecimen, specimen, sessionDataBean);
			}
		}
	}

	/**
	 * Get the related specimen.
	 *
	 * @param identifier Identifier
	 * @param specimenCollection Specimen Collection
	 *
	 * @return Specimen
	 *
	 * @throws BizLogicException Database exception
	 */
	private Specimen getCorelatedSpecimen(Long identifier, Collection<AbstractSpecimen> specimenCollection)
			throws BizLogicException
	{
		final Iterator<AbstractSpecimen> iterator = specimenCollection.iterator();
		while (iterator.hasNext())
		{
			final Specimen specimen = (Specimen) iterator.next();
			if (specimen.getId().longValue() == identifier.longValue())
			{
				return specimen;
			}
		}
		return null;
	}

	/**
	 * Checks duplicate specimen fields.
	 *
	 * @param specimen Specimen
	 * @param dao DAO object
	 *
	 * @throws BizLogicException Database exception
	 */
	private void checkDuplicateSpecimenFields(Specimen specimen, DAO dao) throws BizLogicException
	{
		try
		{
			List list = null;
			// If label generation is off then label is null and it gives error as
			//"Label is already exists"
			if (specimen.getLabel() != null)//bug 13100
			{
				list = dao
						.retrieve(Specimen.class.getCanonicalName(), "label", specimen.getLabel());
				if (!list.isEmpty())
				{
					for (int i = 0; i < list.size(); i++)
					{
						final Specimen specimenObject = (Specimen) (list.get(i));
						if (!specimenObject.getId().equals(specimen.getId()))
						{
							throw this.getBizLogicException(null, "label.already.exits", specimen
									.getLabel());

						}
					}
				}
			}
			if (specimen.getBarcode() != null)
			{
				list = dao.retrieve(Specimen.class.getCanonicalName(), "barcode", specimen
						.getBarcode());
				if (!list.isEmpty())
				{
					for (int i = 0; i < list.size(); i++)
					{
						final Specimen specimenObject = (Specimen) (list.get(i));
						if (!specimenObject.getId().equals(specimen.getId()))
						{
							throw this.getBizLogicException(null, "barcode.already.exits ",
									specimen.getBarcode());
						}
					}
				}
			}
		}
		catch (final DAOException daoExp)
		{
			LOGGER.error(daoExp.getMessage(), daoExp);
			throw this
					.getBizLogicException(daoExp, daoExp.getErrorKeyName(), daoExp.getMsgValues());
		}
	}

	/**
	 * Update specimen domain object.
	 *
	 * @param dao DAO object
	 * @param specimenVO New Object
	 * @param specimenDO Persistent object
	 * @param sessionDataBean session details
	 *
	 * @throws BizLogicException Database exception
	 */
	private void updateSpecimenDomainObject(DAO dao, Specimen specimenVO, Specimen specimenDO,
			SessionDataBean sessionDataBean) throws BizLogicException
	{
		if (specimenVO.getBarcode() != null && specimenVO.getBarcode().trim().length() == 0)
		{
			specimenVO.setBarcode(null);
		}
		this.checkDuplicateSpecimenFields(specimenVO, dao);
		specimenDO.setLabel(specimenVO.getLabel());
		specimenDO.setBarcode(specimenVO.getBarcode());
		specimenDO.setIsAvailable(specimenVO.getIsAvailable());
		if (specimenVO.getSpecimenPosition() != null
				&& specimenVO.getSpecimenPosition().getStorageContainer() != null)
		{
			this.setStorageContainer(specimenVO, specimenDO);
		}
		else
		{
			specimenDO.setSpecimenPosition(null);
			// specimenDO.setStorageContainer(null);
		}
		this.calculateAvailableQunatity(specimenVO, specimenDO);
		final String oldStatus = specimenDO.getCollectionStatus();
		if (specimenVO.getCollectionStatus() != null)
		{
			specimenDO.setCollectionStatus(specimenVO.getCollectionStatus());
		}
		this.addSpecimenEvents(specimenDO, specimenVO, sessionDataBean, oldStatus);
		if (!Constants.COLLECTION_STATUS_COLLECTED.equals(oldStatus))
		{
			this.generateLabel(specimenDO);
			this.generateBarCode(specimenDO);
		}
		checkLabel(specimenDO);
		// code for multiple specimen edit
		if (specimenVO.getCreatedOn() != null)
		{
			specimenDO.setCreatedOn(specimenVO.getCreatedOn());
		}
		this.setSpecimenData(dao, specimenVO, specimenDO);
		if (Constants.MOLECULAR.equals(specimenVO.getClassName()))
		{
			final Double concentration = ((MolecularSpecimen) specimenVO)
					.getConcentrationInMicrogramPerMicroliter();
			((MolecularSpecimen) specimenDO)
					.setConcentrationInMicrogramPerMicroliter(concentration);
		}
	}

	/**
	 * Sets the specimen data.
	 *
	 * @param dao DAO object
	 * @param specimenVO New Object
	 * @param specimenDO Persistent object
	 * @param sessionDataBean session details
	 *
	 * @throws BizLogicException Database exception
	 */
	private void setSpecimenData(DAO dao, Specimen specimenVO, Specimen specimenDO
			) throws BizLogicException
	{
		if (specimenVO.getPathologicalStatus() != null)
		{
			specimenDO.setPathologicalStatus(specimenVO.getPathologicalStatus());
		}
		if (specimenVO.getSpecimenCharacteristics() != null)
		{
			final SpecimenCharacteristics characteristics = specimenVO.getSpecimenCharacteristics();
			if (characteristics.getTissueSide() != null || characteristics.getTissueSite() != null)
			{
				final SpecimenCharacteristics specimenCharacteristics = specimenDO
						.getSpecimenCharacteristics();
				if (specimenCharacteristics != null)
				{
					specimenCharacteristics.setTissueSide(specimenVO.getSpecimenCharacteristics()
							.getTissueSide());
					specimenCharacteristics.setTissueSite(specimenVO.getSpecimenCharacteristics()
							.getTissueSite());
				}
			}
		}
		if (specimenVO.getComment() != null)
		{
			specimenDO.setComment(specimenVO.getComment());
		}
		if (specimenVO.getBiohazardCollection() != null
				&& !specimenVO.getBiohazardCollection().isEmpty())
		{
			specimenDO.setBiohazardCollection(specimenVO.getBiohazardCollection());
		}
		this.updateExtenalIdentifier(dao, specimenVO, specimenDO);
	}

	/**
	 * Update extenal identifier.
	 *
	 * @param dao DAO object
	 * @param specimenVO New Object
	 * @param specimenDO Persistent object
	 * @param sessionDataBean session details
	 *
	 * @throws BizLogicException Database exception
	 */
	private void updateExtenalIdentifier(DAO dao, Specimen specimenVO, Specimen specimenDO
			) throws BizLogicException
	{
		if (specimenVO.getExternalIdentifierCollection() != null
				&& !specimenVO.getExternalIdentifierCollection().isEmpty())
		{
			final Iterator<ExternalIdentifier> itr = specimenVO.getExternalIdentifierCollection()
					.iterator();
			while (itr.hasNext())
			{
				final ExternalIdentifier exception = itr.next();
				exception.setSpecimen(specimenVO);
				try
				{
					if (exception.getId() == null)
					{
						dao.insert(exception);
					}
					else
					{
						final ExternalIdentifier persistetnExt = (ExternalIdentifier) this
								.getCorrespondingOldObject(specimenDO
										.getExternalIdentifierCollection(), exception.getId());
						if ((persistetnExt.getName() != exception.getName())
								|| (persistetnExt.getValue() != exception.getValue()))
						{
							persistetnExt.setName(exception.getName());
							persistetnExt.setValue(exception.getValue());
							dao.update(persistetnExt);

						}
					}
				}
				catch (final DAOException e)
				{
					LOGGER.error(e.getMessage(), e);
					throw this.getBizLogicException(e, "ext.mult.spec.nt.updated", "");
				}

			}
		}
	}

	/**
	 * Logic for Calculate Quantity.
	 *
	 * @param specimenVO New Specimen object
	 * @param specimenDO Persistent Object
	 *
	 * @throws BizLogicException Database Exception
	 */
	private void calculateAvailableQunatity(Specimen specimenVO, Specimen specimenDO)
			throws BizLogicException
	{
		if (specimenVO.getInitialQuantity() != null)
		{
			final Double quantity = specimenVO.getInitialQuantity();
			Double availableQuantity = null;
			if (specimenVO.getAvailableQuantity() != specimenDO.getAvailableQuantity()
					&& specimenVO.getAvailableQuantity() < specimenDO.getInitialQuantity())
			{
				availableQuantity = specimenVO.getAvailableQuantity();
			}
			else
			{
				availableQuantity = specimenDO.getAvailableQuantity();
			}
			if (availableQuantity == null)
			{
				availableQuantity = new Double(0);
				specimenDO.setAvailableQuantity(availableQuantity);
			}
			final double modifiedInitQty = quantity.doubleValue();
			final double oldInitQty = specimenDO.getInitialQuantity().doubleValue();
			final double differenceQty = modifiedInitQty - oldInitQty;
			double newAvailQty = 0.0;

			if (differenceQty == 0
					&& !Constants.COLLECTION_STATUS_COLLECTED.equalsIgnoreCase(specimenDO
							.getCollectionStatus())
					&& Constants.COLLECTION_STATUS_COLLECTED.equalsIgnoreCase(specimenVO
							.getCollectionStatus()))
			{
				newAvailQty = modifiedInitQty;
				specimenVO.setIsAvailable(Boolean.TRUE);// bug 11174
			}
			else if (differenceQty == 0 || !specimenDO.getCollectionStatus().equals("Pending"))
			{
				if (differenceQty < 0)
				{
					newAvailQty = availableQuantity.doubleValue();
				}
				else
				{
					newAvailQty = differenceQty + availableQuantity.doubleValue();
				}
			}
			else
			{
				newAvailQty = modifiedInitQty;
			}
			if (newAvailQty < 0)
			{
				newAvailQty = 0;
			}
			availableQuantity = specimenDO.getAvailableQuantity();
			if (availableQuantity == null)
			{
				availableQuantity = new Double(0);
				specimenDO.setAvailableQuantity(availableQuantity);
			}
			specimenDO.setAvailableQuantity(newAvailQty);
			if (specimenDO.getParentSpecimen() != null)
			{
				this.calculateParentQuantity(specimenDO, differenceQty, newAvailQty);
			}
			if (specimenDO.getChildSpecimenCollection() == null
					|| specimenDO.getChildSpecimenCollection().isEmpty())
			{
				availableQuantity = newAvailQty;
			}
			/*
			 * if ((specimenDO.getAvailableQuantity() != null &&
			 * specimenDO.getAvailableQuantity() > 0)) {
			 * specimenDO.setAvailable(Boolean.TRUE); }
			 */
			Double oldInitialQty = null;
			if (specimenDO.getInitialQuantity() == null)
			{
				oldInitialQty = new Double(0);
				specimenDO.setInitialQuantity(oldInitialQty);
			}
			else
			{
				oldInitialQty = specimenDO.getInitialQuantity();
			}
			specimenDO.setInitialQuantity(modifiedInitQty);
		}
	}

	/**
	 * Calculate parent quantity.
	 *
	 * @param specimenDO Persistent object
	 * @param differenceQty Change in quantity
	 * @param newAvailQty New Available quantity
	 *
	 * @throws BizLogicException Database Exception
	 */
	private void calculateParentQuantity(Specimen specimenDO, double differenceQty,
			double newAvailQty) throws BizLogicException
	{
		if (specimenDO.getLineage().equals("Aliquot"))
		{
			double parentAvl = 0.0;
			final Specimen parentSpecimen = (Specimen) specimenDO.getParentSpecimen();
			if (!specimenDO.getCollectionStatus().equals("Pending"))
			{
				parentAvl = parentSpecimen.getAvailableQuantity().doubleValue() - differenceQty;
			}
			else
			{
				parentAvl = parentSpecimen.getAvailableQuantity().doubleValue() - newAvailQty;
			}
			if (parentAvl < 0)
			{
				throw this.getBizLogicException(null, "insuff.avai.quan", "");
			}
			parentSpecimen.setAvailableQuantity(parentAvl);
		}
	}

	/**
	 * Gets the container holds c ps.
	 *
	 * @return containerHoldsCPs
	 */
	public Map<Long, Collection<CollectionProtocol>> getContainerHoldsCPs()
	{
		return this.containerHoldsCPs;
	}

	/**
	 * Sets the container holds c ps.
	 *
	 * @param containerHoldsCPs Map of container that can holds CP
	 */
	public void setContainerHoldsCPs(Map<Long, Collection<CollectionProtocol>> containerHoldsCPs)
	{
		this.containerHoldsCPs = containerHoldsCPs;
	}

	/**
	 * Gets the container holds specimen classes.
	 *
	 * @return containerHoldsSpecimenClasses containerHoldsSpecimenClasses
	 */
	public Map<Long, Collection<String>> getContainerHoldsSpecimenClasses()
	{
		return this.containerHoldsSpecimenClasses;
	}

	/**
	 * Sets the container holds specimen classes.
	 *
	 * @param containerHoldsSpecimenClasses container of SpecimenClasses
	 */
	public void setContainerHoldsSpecimenClasses(
			Map<Long, Collection<String>> containerHoldsSpecimenClasses)
	{
		this.containerHoldsSpecimenClasses = containerHoldsSpecimenClasses;
	}

	/**
	 * Sets the storage container.
	 *
	 * @param dao DAO object
	 * @param specimenVO New Specimen object
	 * @param specimenDO Persistent Object
	 *
	 * @throws BizLogicException Database Exception
	 */
	private void setStorageContainer(Specimen specimenVO, Specimen specimenDO)
			throws BizLogicException
	{
		SpecimenPosition specPos = specimenDO.getSpecimenPosition();
		if (specimenVO != null && specimenVO.getSpecimenPosition() != null)
		{
			final StorageContainer storageContainer = specimenVO.getSpecimenPosition()
					.getStorageContainer();

			if (specPos == null)
			{
				specPos = new SpecimenPosition();
			}

			specPos.setPositionDimensionOne(specimenVO.getSpecimenPosition()
					.getPositionDimensionOne());
			specPos.setPositionDimensionTwo(specimenVO.getSpecimenPosition()
					.getPositionDimensionTwo());
			specPos.setSpecimen(specimenDO);
			specPos.setStorageContainer(storageContainer);
		}

		specimenDO.setSpecimenPosition(specPos);

		// specimenDO.setStorageContainer(storageContainer);
	}

	/**
	 * Checks if is cpbased.
	 *
	 * @return boolean
	 */
	public boolean isCpbased()
	{
		return this.cpbased;
	}

	/**
	 * Sets the cpbased.
	 *
	 * @param cpbased boolean
	 */
	public void setCpbased(boolean cpbased)
	{
		this.cpbased = cpbased;
	}

	/**
	 * This function throws BizLogicException if the domainObj is of type
	 * SpecimenCollectionRequirementGroup.
	 *
	 * @param domainObj current domain object
	 * @param uiForm current form
	 *
	 * @throws BizLogicException BizLogic exception
	 */
	protected void prePopulateUIBean(AbstractDomainObject domainObj, IValueObject uiForm)
			throws BizLogicException
	{

		final Specimen specimen = (Specimen) domainObj;
		final AbstractSpecimenCollectionGroup absspecimenCollectionGroup = specimen
				.getSpecimenCollectionGroup();
		final Object proxySpecimenCollectionGroup = HibernateMetaData
				.getProxyObjectImpl(absspecimenCollectionGroup);
		if ((proxySpecimenCollectionGroup instanceof CollectionProtocolEvent))
		{
			final NewSpecimenForm newSpecimenForm = (NewSpecimenForm) uiForm;
			newSpecimenForm.setForwardTo(Constants.PAGE_OF_SPECIMEN_COLLECTION_REQUIREMENT_GROUP);
			throw this.getBizLogicException(null, "req.spec.nt.edited", "");

		}
	}

	/**
	 * This function is used for retriving specimen and sub specimen's
	 * attributes.
	 *
	 * @param sessionData : sessionData
	 * @param specimenID id of the specimen
	 * @param finalDataList the data list to be populated
	 * @param dao the dao
	 *
	 * @return Specimen
	 *
	 * @throws BizLogicException : BizLogicException
	 */
	public Specimen getSpecimen(String specimenID, List finalDataList, SessionDataBean sessionData,
			DAO dao) throws BizLogicException
	{

		try
		{
			final Specimen specimen = this.getSpecimenObj(specimenID, dao);
			this.getSpecimenInternal(specimen, finalDataList);
			specimen.getConsentTierStatusCollection();
			specimen.getSpecimenCollectionGroup().getCollectionProtocolRegistration()
					.getConsentTierResponseCollection();
			return specimen;
		}
		catch (final Exception exception)
		{
			LOGGER.error(exception.getMessage(), exception);
			throw this.getBizLogicException(exception, "failed.spec.details", " "
					+ exception.getMessage());
		}

	}

	/**
	 * Gets the specimen internal.
	 *
	 * @param specimen  : specimen
	 * @param finalDataList : finalDataList
	 *
	 * @throws BizLogicException  :BizLogicException
	 */
	private void getSpecimenInternal(Specimen specimen, List finalDataList)
			throws BizLogicException
	{

		final List specimenDetailList = new ArrayList();
		specimenDetailList.add(specimen.getLabel());
		specimenDetailList.add(specimen.getSpecimenType());
		if (specimen.getSpecimenPosition() == null
				|| specimen.getSpecimenPosition().getStorageContainer() == null)
		{
			specimenDetailList.add(Constants.VIRTUALLY_LOCATED);
		}
		else
		{
			if (specimen.getSpecimenPosition() != null)
			{
				final String storageLocation = specimen.getSpecimenPosition().getStorageContainer()
						.getName()
						+ ": X-Axis-"
						+ specimen.getSpecimenPosition().getPositionDimensionOne()
						+ ", Y-Axis-" + specimen.getSpecimenPosition().getPositionDimensionTwo();
				specimenDetailList.add(storageLocation);
			}
		}
		specimenDetailList.add(specimen.getClassName());
		finalDataList.add(specimenDetailList);
		final Collection childrenSpecimen = specimen.getChildSpecimenCollection();
		final Iterator itr = childrenSpecimen.iterator();
		while (itr.hasNext())
		{
			final Specimen childSpecimen = (Specimen) itr.next();
			this.getSpecimenInternal(childSpecimen, finalDataList);
		}

	}

	/**
	 * return the specimen object.
	 *
	 * @param specimenID : specimenID
	 * @param dao : dao
	 *
	 * @return Specimen
	 *
	 * @throws BizLogicException : BizLogicException
	 */
	public Specimen getSpecimenObj(String specimenID, DAO dao) throws BizLogicException
	{
		try
		{
			final Object object = dao.retrieveById(Specimen.class.getName(), Long
					.valueOf(specimenID));// new
			if (object == null)
			{
				throw this.getBizLogicException(null, "no.spec.returned", "");
			}
			return (Specimen) object;
		}
		catch (final DAOException exp)
		{
			LOGGER.error(exp.getMessage(), exp);
			throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp.getMsgValues());
		}
	}

	/**
	 * Check parent specimen disposal.
	 *
	 * @param sessionDataBean : sessionDataBean
	 * @param specimen : specimen
	 * @param dao : dao
	 * @param disposalReason : disposalReason
	 *
	 * @throws UserNotAuthorizedException : UserNotAuthorizedException
	 * @throws DAOException : DAOException
	 */
	public void checkParentSpecimenDisposal(SessionDataBean sessionDataBean, Specimen specimen,
			DAO dao, String disposalReason) throws UserNotAuthorizedException, DAOException
	{
		if (specimen.getParentSpecimen() != null)
		{
			try
			{
				final AbstractSpecimen parentSp = specimen.getParentSpecimen();
				this.disposeSpecimen(sessionDataBean, parentSp, dao, disposalReason);
			}
			catch (final BizLogicException ex)
			{
				LOGGER.error(ex.getMessage(), ex);
				final ActionErrors actionErrors = new ActionErrors();
				actionErrors.add(actionErrors.GLOBAL_MESSAGE, new ActionError("errors.item", ex
						.getMessage()));
			}
		}
	}

	/**
	 * Called from DefaultBizLogic to get ObjectId for authorization check.
	 * (non-Javadoc)
	 *
	 * @param dao : dao
	 * @param domainObject : domainObject
	 *
	 * @return String
	 *
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#getObjectId(edu.wustl.common.dao.DAO,
	 * java.lang.Object)
	 */
	public String getObjectId(DAO dao, Object domainObject)
	{
		String objectId = "";
		Specimen specimen = null;
		Long cpId = null;
		List<Long> list = null;
		try
		{
			if (domainObject instanceof LinkedHashSet)
			{
				final LinkedHashSet linkedHashSet = (LinkedHashSet) domainObject;
				specimen = (Specimen) linkedHashSet.iterator().next();
			}
			else if (domainObject instanceof Specimen)
			{
				specimen = (Specimen) domainObject;
			}

			final SpecimenCollectionGroup scg = specimen.getSpecimenCollectionGroup();
			//bug 13082 and 13261 start
			if (scg != null)
			{
				final CollectionProtocolRegistration cpRegistration = scg
						.getCollectionProtocolRegistration();
				if (cpRegistration != null && cpRegistration.getCollectionProtocol() != null)
				{
					cpId = cpRegistration.getCollectionProtocol().getId();
				}
			}
			if (cpId == null)//bug 13082 and 13261 end
			{
				cpId = this.getCPId(dao, cpId, specimen);
				if (cpId == null)
				{
					final StringBuffer query = getQuery(scg);
					list = dao.executeQuery(query.toString());
					final Iterator<Long> itr = list.iterator();
					while (itr.hasNext())
					{
						cpId = (Long) itr.next();
					}
				}
			}
		}
		catch (final Exception e)
		{
			LOGGER.error(e.getMessage(), e);
		}

		if (cpId != null)
		{
			objectId = Constants.COLLECTION_PROTOCOL_CLASS_NAME + "_" + cpId;

		}
		else
		{
			objectId = Constants.ADMIN_PROTECTION_ELEMENT;
		}

		return objectId;
	}

	/**
	 * generated query to retrieve the CPR id.
	 * @param scg
	 * @return
	 */
	private StringBuffer getQuery(final SpecimenCollectionGroup scg) {
		final StringBuffer query = new StringBuffer("select specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.id  from edu.wustl.catissuecore.domain.SpecimenCollectionGroup as specimenCollectionGroup where "
			);
		if( scg.getId() != null )
		{
			query.append("specimenCollectionGroup.id = " ).append(scg.getId());
		}
		else if(scg.getName()!=null){
			query.append("specimenCollectionGroup.name = '" ).append(scg.getName()).append("'");
		}
		return query;
	}

	/**
	 * To get PrivilegeName for authorization check from
	 * 'PermissionMapDetails.xml' (non-Javadoc).
	 *
	 * @param domainObject : domainObject
	 *
	 * @return String
	 *
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#getPrivilegeName(java.lang.Object)
	 */
	protected String getPrivilegeKey(Object domainObject)
	{
		Specimen specimen = null;

		if (domainObject instanceof LinkedHashSet)
		{
			final LinkedHashSet linkedHashSet = (LinkedHashSet) domainObject;
			specimen = (Specimen) linkedHashSet.iterator().next();
		}
		else if (domainObject instanceof Specimen)
		{
			specimen = (Specimen) domainObject;
		}

		if ((specimen.getLineage() != null)
				&& (specimen.getLineage().equals(Constants.DERIVED_SPECIMEN)))
		{
			return Constants.DERIVE_SPECIMEN;
		}

		else if ((specimen.getLineage() != null)
				&& (specimen.getLineage().equals(Constants.ALIQUOT)))
		{
			return Constants.ALIQUOT_SPECIMEN;
		}

		return Constants.ADD_EDIT_SPECIMEN;
	}

	/**
	 * (non-Javadoc).
	 *
	 * @param dao : dao
	 * @param domainObject : domainObject
	 * @param sessionDataBean : sessionDataBean
	 *
	 * @return boolean
	 *
	 * @throws BizLogicException : BizLogicException
	 *
	 * @see edu.wustl.common.bizlogic.DefaultBizLogic#isAuthorized(edu.wustl.common.dao.DAO,
	 * java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	 */
	public boolean isAuthorized(DAO dao, Object domainObject, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
		boolean isAuthorized = false;
		String protectionElementName = null;
		String query = null;
		try
		{
			if (sessionDataBean != null && sessionDataBean.isAdmin())
			{
				return true;
			}
			// Get the base object id against which authorization will take
			// place
			if (domainObject != null)
			{
				if (domainObject instanceof List)
				{
					final List list = (List) domainObject;
					for (final Object domainObject2 : list)
					{
						protectionElementName = this.getObjectId(dao, domainObject2);
					}
				}
				else
				{
					protectionElementName = this.getObjectId(dao, domainObject);
					Site site = null;
					StorageContainer stCont = null;
					// Handle for SERIAL CHECKS, whether user has access to
					// source site or not

					if (domainObject instanceof Specimen)
					{
//						final SpecimenPosition specimenPosition = null;
						final Specimen specimen = (Specimen) domainObject;
//						final Specimen parentSpecimen = (Specimen) specimen.getParentSpecimen();
						List<Site> list = null;
						if (specimen.getLineage() != null
								&& ((Constants.DERIVED_SPECIMEN).equals(specimen.getLineage()) || (Constants.ALIQUOT)
										.equals(specimen.getLineage())))
						{
							//bug 13288
							/**
							 * To edit specimen of lineage derivative/aliquot, if the parent specimen is located at site
							 * at which the user does not have association privileges.
							 */
							if (specimen.getLabel() != null
									&& !specimen.getLabel().equals(""))
							{
								query = "select specimen.specimenPosition.storageContainer.site from edu.wustl.catissuecore.domain.Specimen as specimen where "
										+ "specimen.label = '"
										+ specimen.getLabel() + "'";
							}
							else if (specimen.getBarcode() != null
									&& !specimen.getBarcode().equals(""))
							{
								query = "select specimen.specimenPosition.storageContainer.site from edu.wustl.catissuecore.domain.Specimen as specimen where "
										+ "specimen.barcode = '"
										+ specimen.getBarcode()
										+ "'";
							}
							else
							{
								query = "select specimen.specimenPosition.storageContainer.site from edu.wustl.catissuecore.domain.Specimen as specimen where "
										+ " specimen.id = " + specimen.getId();
							}

							if (query != null)
							{
								list = this.executeQuery(query);
								if (list != null)
								{
									final Iterator<Site> itr = list.iterator();
									while (itr.hasNext())
									{
										site = (Site) itr.next();
									}
								}
							}
						}
						else
						{
							if (specimen.getSpecimenPosition() != null)
							{
								stCont = specimen.getSpecimenPosition().getStorageContainer();
							}
							if (specimen.getSpecimenPosition() != null
									&& specimen.getSpecimenPosition().getStorageContainer()
											.getSite() == null)
							{
								if (stCont.getId() != null)
								{
									query = "select storageContainer.site from edu.wustl.catissuecore.domain.StorageContainer as storageContainer where "
											+ " storageContainer.id = " + stCont.getId();
								}
								else
								{
									query = "select storageContainer.site from edu.wustl.catissuecore.domain.StorageContainer as storageContainer where "
											+ " storageContainer.name = '" + stCont.getName() + "'";
								}
								if (query != null)
								{
									list = this.executeQuery(query);
									if (list != null)
									{
										final Iterator<Site> itr = list.iterator();
										while (itr.hasNext())
										{
											site = (Site) itr.next();
										}
									}
									if (list.isEmpty())
									{
										throw this.getBizLogicException(null,
												"sc.unableToFindContainer", "");
									}
								}
							}
						}

						//bug 13094 start
						if (site != null) // Specimen is NOT Virtually Located
						{
							final Set<Long> siteIdSet = new UserBizLogic()
									.getRelatedSiteIds(sessionDataBean.getUserId());
							if (!siteIdSet.contains(site.getId()))
							{
								final BizLogicException exp = AppUtility
										.getUserNotAuthorizedException(Constants.Association, site
												.getObjectId(), domainObject.getClass()
												.getSimpleName());
								throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp
										.getMsgValues());
							}
						}
						//bug 13094 end
					}
				}

				if (protectionElementName.equals(Constants.allowOperation))
				{
					return true;
				}
				// Get the required privilege name which we would like to check
				// for the logged in user.
				final String privilegeName = this.getPrivilegeName(domainObject);
				final PrivilegeCache privilegeCache = PrivilegeManager.getInstance()
						.getPrivilegeCache(sessionDataBean.getUserName());

				final String[] privilegeNames = privilegeName.split(",");
				// Checking whether the logged in user has the required
				// privilege on the given protection element
				if (privilegeNames.length > 1)
				{
					if ((privilegeCache.hasPrivilege(protectionElementName, privilegeNames[0]))
							|| (privilegeCache.hasPrivilege(protectionElementName,
									privilegeNames[1])))
					{
						isAuthorized = true;
					}
				}
				else
				{
					isAuthorized = privilegeCache
							.hasPrivilege(protectionElementName, privilegeName);
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
					throw AppUtility.getUserNotAuthorizedException(privilegeName,
							protectionElementName, domainObject.getClass().getSimpleName());
				}
			}
		}
		catch (final SMException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw AppUtility.handleSMException(e);
		}
		return isAuthorized;
	}

	/**
	 * isReadDeniedTobeChecked.
	 *
	 * @return true, if checks if is read denied tobe checked
	 */
	@Override
	public boolean isReadDeniedTobeChecked()
	{
		return true;
	}

	/**
	 * getReadDeniedPrivilegeName.
	 *
	 * @return the read denied privilege name
	 */
	@Override
	public String getReadDeniedPrivilegeName()
	{
		return Permissions.READ_DENIED;
	}

	/**
	 * validate Collection Status.
	 *
	 * @param specimen the specimen
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	private void validateCollectionStatus(Specimen specimen) throws BizLogicException
	{
		final Specimen parent = (Specimen) specimen.getParentSpecimen();

		if (parent != null
				&& !Constants.COLLECTION_STATUS_COLLECTED.equals(parent.getCollectionStatus())
				&& (Constants.COLLECTION_STATUS_COLLECTED.equals(specimen.getCollectionStatus())))
		{
			throw this.getBizLogicException(null, "child.nt.coll", "");
		}
	}

	/**
	 * This method called from orderbizlogic to distribute and close specimen.
	 *
	 * @param sessionDataBean : sessionDataBean
	 * @param specimen : specimen
	 * @param dao  :dao
	 * @param disposalReason : disposalReason
	 *
	 * @throws BizLogicException : BizLogicException
	 * @throws UserNotAuthorizedException : UserNotAuthorizedException
	 */
	public void disposeAndCloseSpecimen(SessionDataBean sessionDataBean, AbstractSpecimen specimen,
			DAO dao, String disposalReason) throws BizLogicException, UserNotAuthorizedException
	{
		if (!Status.ACTIVITY_STATUS_CLOSED.toString().equals(specimen.getActivityStatus()))
		{
			this.disposeSpecimen(sessionDataBean, specimen, dao, disposalReason);
		}
	}

	/**
	 * refresh Titli Search/ Keyword Search Index Single.
	 *
	 * @param operation the operation
	 * @param obj the obj
	 */
	protected void refreshTitliSearchIndexSingle(String operation, Object obj)
	{
		List result = null;
		super.refreshTitliSearchIndexSingle(operation, obj);

		try
		{
			if(obj instanceof Specimen)
			{
				final Specimen specimen = (Specimen) obj;
				final String selectColumnName[] = {"id"};
				final String whereColumnName[] = {"parentSpecimen.id"};
				final String whereColumnCondition[] = {"="};
				final Object whereColumnValue[] = {specimen.getId()};

				final QueryWhereClause queryWhereClause = new QueryWhereClause(obj.getClass().getName());
				queryWhereClause.addCondition(new EqualClause("parentSpecimen.id", specimen.getId()));

				result = this.retrieve(obj.getClass().getName(), selectColumnName, queryWhereClause);
			}
		}
		catch (final BizLogicException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		catch (final DAOException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		if (result != null)
		{
			final Iterator itr = result.iterator();
			while (itr.hasNext())
			{
				final Object identifier = (Object) itr.next();
				if (identifier != null)
				{
					final String idString = String.valueOf(identifier);
					final Long idLong = Long.valueOf(idString);// new Long(idString);
					final Specimen childSpecimen = new Specimen();
					childSpecimen.setId(idLong);
					this.refreshTitliSearchIndexSingle(operation, childSpecimen);
				}
			}
		}
	}

	/**
	 * Gets the total no of aliquot specimen.
	 *
	 * @param specId : specId
	 * @param dao the dao
	 *
	 * @return long : long
	 *
	 * @throws BizLogicException : BizLogicException
	 */
	public synchronized long getTotalNoOfAliquotSpecimen(Long specId, DAO dao)
			throws BizLogicException
	{
		long aliquotChildCount = 0;
		try
		{
			final String[] selectColumnName = {"id"};
			/*
			 * final String[] whereColumnName = { "parentSpecimen.id",
			 * "lineage", "collectionStatus" }; final String[]
			 * whereColumnCondition = { "=", "=", "=" }; final Object[]
			 * whereColumnValue = { specId, "Aliquot", "Collected" }; final
			 * String joinCondition = Constants.AND_JOIN_CONDITION;
			 */
			final QueryWhereClause queryWhereClause = new QueryWhereClause(Specimen.class.getName());
			queryWhereClause.addCondition(new EqualClause("parentSpecimen.id", specId));
			queryWhereClause.andOpr();
			queryWhereClause.addCondition(new EqualClause("lineage", "Aliquot"));
			queryWhereClause.andOpr();
			queryWhereClause.addCondition(new EqualClause("collectionStatus", "Collected"));
			final List AliquotChildList = dao.retrieve(Specimen.class.getName(), selectColumnName,
					queryWhereClause);
			if (AliquotChildList != null && !AliquotChildList.isEmpty())
			{
				aliquotChildCount = AliquotChildList.size();
			}

		}
		catch (final DAOException e)
		{
			this.LOGGER.error(e.getMessage(),e);
			throw new BizLogicException(e);
		}
		return aliquotChildCount;
	}

	/**
	 * Gets the cp id.
	 *
	 * @param dao - dao
	 * @param cpId - cp Id
	 * @param specimen - specimen
	 *
	 * @return cp Id
	 *
	 * @throws DAOException - DAOException
	 */

	public Long getCPId(DAO dao, Long cpId, AbstractSpecimen specimen) throws DAOException
	{
		String query = null;
		if (specimen.getParentSpecimen() != null)
		{
			query = "select specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.id from edu.wustl.catissuecore.domain.Specimen as specimen where "
					+ "specimen.label = '" + specimen.getParentSpecimen().getLabel() + "'";
			cpId = executeQuery(dao,query);
		}
		else if (cpId == null)
		{
			if(specimen.getId() != null)
			{
				query = "select specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.id  from edu.wustl.catissuecore.domain.Specimen as specimen where "
					+ "specimen.id = '" + specimen.getId() + "'";
				cpId = executeQuery(dao, query);
			}
			else if(specimen.getLabel() != null)
			{
				query = "select specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.id  from edu.wustl.catissuecore.domain.Specimen as specimen where "
					+ "specimen.label = '" + specimen.getLabel() + "'";
				cpId = executeQuery(dao,  query);
			}
			else if(specimen instanceof Specimen && ((Specimen)specimen).getBarcode() != null)
			{
				query = "select specimen.specimenCollectionGroup.collectionProtocolRegistration.collectionProtocol.id  from edu.wustl.catissuecore.domain.Specimen as specimen where "
					+ "specimen.barcode = '" + ((Specimen)specimen).getBarcode() + "'";
				cpId = executeQuery(dao, query);
			}

		}
		return cpId;
	}

	/**
	 * executed teh given query.
	 * @param dao
	 * @param cpId
	 * @param query
	 * @return
	 * @throws DAOException
	 */
	private Long executeQuery(DAO dao, String query)
			throws DAOException {
		Long cpId=null;
		List<Long> list = dao.executeQuery(query);
		final Iterator<Long> itr = list.iterator();
		while (itr.hasNext())
		{
			cpId = (Long) itr.next();
		}
		return cpId;
	}

	/**
	 * To check wether the Continer to display can holds the given
	 * specimenClass.
	 *
	 * @param specimenClass The specimenClass Name.
	 * @param storageContainer The StorageContainer reference to be displayed on the page.
	 *
	 * @return true if the given continer can hold the specimenClass.
	 *
	 * @throws BizLogicException throws BizLogicException
	 */
	public boolean canHoldSpecimenClass(String specimenClass, StorageContainer storageContainer)
			throws BizLogicException
	{
		final Collection specimenClasses = (Collection) this.retrieveAttribute(
				StorageContainer.class.getName(), storageContainer.getId(),
				"elements(holdsSpecimenClassCollection)");// storageContainer.getHoldsSpecimenClassCollection();
		final Iterator itr = specimenClasses.iterator();
		while (itr.hasNext())
		{
			final String className = (String) itr.next();
			if (className.equals(specimenClass))
			{
				return true;
			}

		}
		return false;
	}

	/**
	 * To check wether the Continer to display can holds the given
	 * specimenType.
	 *
	 * @param specimenType The specimenType Name.
	 * @param storageContainer The StorageContainer reference to be displayed on the page.
	 *
	 * @return true if the given continer can hold the specimenType.
	 *
	 * @throws BizLogicException throws BizLogicException
	 */
	public boolean canHoldSpecimenType(String specimenType, StorageContainer storageContainer)
			throws BizLogicException
	{
		final Collection spType = (Collection) this.retrieveAttribute(
				StorageContainer.class.getName(), storageContainer.getId(),
				"elements(holdsSpecimenTypeCollection)");// storageContainer.getHoldsSpecimenClassCollection();
		final Iterator itr = spType.iterator();
		while (itr.hasNext())
		{
			final String typeName = (String) itr.next();
			if (typeName.equals(specimenType))
			{
				return true;
			}

		}
		return false;
	}



	/**
	 * Gets the specimen count for cp.
	 *
	 * @param ppi the ppi
	 *
	 * @return the specimen count for cp
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	public Long getSpecimenCountForCP(String ppi) throws BizLogicException
	{
		Long count=0l;
		String hql= "select count(specimen) from edu.wustl.catissuecore.domain.Specimen as specimen"
			+" where specimen.specimenCollectionGroup.collectionProtocolRegistration.id ='"+ ppi +"'"
			+" and (specimen.lineage='New' or specimen.lineage='Derived') and specimen.collectionStatus = 'Collected'";
		List<Object[]> list=null;
		try
		{
			list=AppUtility.executeQuery(hql);
			if(list!=null)
			{
				Object object = list.get(0);
				count=Long.valueOf(object.toString());
			}
		}
		catch(ApplicationException exp)
		{
			throw this.getBizLogicException(exp, exp.getErrorKeyName(), exp.getMsgValues());

		}
		return count;
	}


}