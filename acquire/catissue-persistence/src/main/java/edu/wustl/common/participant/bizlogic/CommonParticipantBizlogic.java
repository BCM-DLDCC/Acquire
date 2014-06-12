
package edu.wustl.common.participant.bizlogic;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.codec.language.Metaphone;

import edu.wustl.catissuecore.domain.Race;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.participant.domain.IParticipant;
import edu.wustl.common.participant.domain.IParticipantMedicalIdentifier;
import edu.wustl.common.participant.domain.IRace;
import edu.wustl.common.participant.domain.ISite;
import edu.wustl.common.participant.utility.Constants;
import edu.wustl.common.participant.utility.ParticipantManagerException;
import edu.wustl.common.participant.utility.ParticipantManagerUtility;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.exception.AuditException;
import edu.wustl.dao.exception.DAOException;

/**
 * The Class CommonParticipantBizlogic.
 */
public class CommonParticipantBizlogic extends CommonDefaultBizLogic
{

	/** The Constant logger. */
	private static final Logger logger = Logger.getCommonLogger(CommonParticipantBizlogic.class);

	/**
	 * Insert.
	 * @param obj Participant Object
	 * @param dao DAo Object
	 * @param ParticipantMedicalIdentifier Object
	 *
	 * @return the i participant
	 *
	 * @throws BizLogicException the biz logic exception
	 * @throws DAOException the DAO exception
	 */
	public static IParticipant insert(final Object obj, final DAO dao,
			final IParticipantMedicalIdentifier pmi) throws BizLogicException, DAOException
	{
		final IParticipant participant = (IParticipant) obj;
		setMetaPhoneCode(participant);
		Collection<IParticipantMedicalIdentifier<IParticipant, ISite>> pmiCollection = participant
				.getParticipantMedicalIdentifierCollection();
		if (pmiCollection == null)
		{
			pmiCollection = new LinkedHashSet<IParticipantMedicalIdentifier<IParticipant, ISite>>();
		}
		if (pmiCollection.isEmpty())
		{
			pmi.setMedicalRecordNumber(null);
			pmi.setSite(null);
			pmiCollection.add(pmi);
		}
		checkForSiteIdentifierInPMI(dao, pmiCollection);
		final Iterator<IParticipantMedicalIdentifier<IParticipant, ISite>> iterator = pmiCollection
				.iterator();
		while (iterator.hasNext())
		{
			final IParticipantMedicalIdentifier<IParticipant, ISite> pmIdentifier = iterator.next();
			pmIdentifier.setParticipant(participant);
		}
		dao.insert(participant);
		return participant;
	}

	/**
	 * For Bulk Operations: retrieving site_id from site_name.
	 * Check For Site Identifier In PMI.
	 * @param dao DAO
	 * @param pmiCollection Collection of ParticipantMedicalIdentifier
	 * @throws DAOException DAOException
	 * @throws BizLogicException BizLogicException
	 */
	private static void checkForSiteIdentifierInPMI(final DAO dao,
			final Collection<IParticipantMedicalIdentifier<IParticipant, ISite>> pmiCollection)
			throws DAOException, BizLogicException
	{
		final Iterator<IParticipantMedicalIdentifier<IParticipant, ISite>> pmiIterator = pmiCollection
				.iterator();
		while (pmiIterator.hasNext())
		{
			final IParticipantMedicalIdentifier<IParticipant, ISite> pmIdentifier = pmiIterator
					.next();
			if (pmIdentifier.getSite() != null && (pmIdentifier.getSite().getId() != null
					|| pmIdentifier.getSite().getName() != null))
			{
				final ISite site = pmIdentifier.getSite();
				final String sourceObjectName = ISite.class.getName();
				final String[] selectColumnName ={"id","name"};
				final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			//	List<ColumnValueBean> columnValueBeans = new ArrayList<ColumnValueBean>();
				String errMsg = "";
				String errKey = "";
				if(site.getName() != null)
				{
					errKey = "invalid.site.name";
					errMsg = site.getName();
					queryWhereClause.addCondition(new EqualClause("name", site.getName()));
				//	columnValueBeans.add(new ColumnValueBean("name",site.getName()));
				}
				else
				{
					errKey = "errors.item.format";
					errMsg = "Site Identifier";
					queryWhereClause.addCondition(new EqualClause("id", site.getId()));
				//	columnValueBeans.add(new ColumnValueBean("id",site.getId()));
				}

				final List list = ((HibernateDAO) dao).retrieve(sourceObjectName, selectColumnName,
						queryWhereClause);
				
				if (!list.isEmpty())
				{
					final Object[] valArr = (Object[]) list.get(0);
					site.setId((Long) valArr[0]);
					site.setName((String)valArr[1]);
					pmIdentifier.setSite(site);
				}
				else
				{
					throw new BizLogicException(ErrorKey.getErrorKey(errKey), null,errMsg);
				}
			}
		}
	}

	/**
	 * Updates the persistent object in the database.
	 *
	 * @param dao - DAO object
	 * @param participant the participant
	 * @param oldParticipant the old participant
	 *
	 * @throws BizLogicException throws BizLogicException
	 * @throws DAOException the DAO exception
	 */
	public static void update(final DAO dao, final IParticipant participant,
			final IParticipant oldParticipant) throws BizLogicException, DAOException
	{

		setMetaPhoneCode(participant);
		dao.update(participant, oldParticipant);
	}

	/**
	 * Sets the meta phone code.
	 *
	 * @param participant the new meta phone code
	 */
	private static void setMetaPhoneCode(final IParticipant participant)
	{
		final Metaphone metaPhoneObj = new Metaphone();
		final String lNameMetaPhone = metaPhoneObj.metaphone(participant.getLastName());
		participant.setMetaPhoneCode(lNameMetaPhone);
	}

	/**
	 * Update ParticipantMedicalIdentifier.
	 * @param dao DAo Object
	 * @param pmIdentifier ParticipantMedicalIdentifier Identifier
	 * @throws DAOException the DAO exception
	 */
	public static void updatePMI(final DAO dao,
			final IParticipantMedicalIdentifier<IParticipant, ISite> pmIdentifier)
			throws DAOException
	{
		if (pmIdentifier.getId() != null)
		{
			dao.update(pmIdentifier);
		}
		else if (pmIdentifier.getId() == null || pmIdentifier.getId().equals(""))
		{
			dao.insert(pmIdentifier);
		}
	}

	/**
	 * Validate.
	 *
	 * @param dao : DAO object. Overriding the parent class's method to validate
	 * the enumerated attribute values.
	 * @param participant the participant
	 * @param operation the operation
	 * @param validator the validator
	 *
	 * @return true, if validate
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	public static boolean validate(final IParticipant participant, final DAO dao,
			final String operation, final Validator validator) throws BizLogicException
	{
		String message = "";
		if (participant == null)
		{

			throw new BizLogicException(null, null, "domain.object.null.err.msg", "Participant");
		}

		String errorKeyForBirthDate = "";
		String errorKeyForDeathDate = "";

		final String birthDate = Utility.parseDateToString(participant.getBirthDate(),
				CommonServiceLocator.getInstance().getDatePattern());
		if (!Validator.isEmpty(birthDate))
		{
			errorKeyForBirthDate = validator.validateDate(birthDate, true);
			if (errorKeyForBirthDate.trim().length() > 0)
			{
				message = ApplicationProperties.getValue("participant.birthDate");
				throw new BizLogicException(null, null, errorKeyForBirthDate, message);
			}
		}

		final String deathDate = Utility.parseDateToString(participant.getDeathDate(),
				CommonServiceLocator.getInstance().getDatePattern());
		if (!Validator.isEmpty(deathDate))
		{
			errorKeyForDeathDate = validator.validateDate(deathDate, true);
			if (errorKeyForDeathDate.trim().length() > 0)
			{
				message = ApplicationProperties.getValue("participant.deathDate");
				throw new BizLogicException(null, null, errorKeyForDeathDate, message);
			}
		}

		if (participant.getVitalStatus() == null || !participant.getVitalStatus().equals("Dead"))
		{
			if (!Validator.isEmpty(deathDate))
			{
				throw new BizLogicException(null, null, "participant.invalid.enddate", "");
			}
		}
		if ((!Validator.isEmpty(birthDate) && !Validator.isEmpty(deathDate))
				&& (errorKeyForDeathDate.trim().length() == 0 && errorKeyForBirthDate.trim()
						.length() == 0))
		{
			final boolean errorKey1 = validator.compareDates(Utility.parseDateToString(participant
					.getBirthDate(), CommonServiceLocator.getInstance().getDatePattern()), Utility
					.parseDateToString(participant.getDeathDate(), CommonServiceLocator
							.getInstance().getDatePattern()));

			if (!errorKey1)
			{

				throw new BizLogicException(null, null, "participant.invaliddate", "");
			}
		}

		if (!Validator.isEmpty(participant.getSocialSecurityNumber()))
		{
			if (!validator.isValidSSN(participant.getSocialSecurityNumber()))
			{
				message = ApplicationProperties.getValue("participant.socialSecurityNumber");
				throw new BizLogicException(null, null, "errors.invalid", message);
			}
		}

		if (!Validator.isEmpty(participant.getVitalStatus()))
		{
			final List vitalStatusList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_VITAL_STATUS, null);
			if (!Validator.isEnumeratedOrNullValue(vitalStatusList, participant.getVitalStatus()))
			{
				throw new BizLogicException(null, null, "participant.vitalstatus.errMsg", "");
			}
		}

		if (!Validator.isEmpty(participant.getGender()))
		{
			final List genderList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_GENDER, null);

			if (!Validator.isEnumeratedOrNullValue(genderList, participant.getGender()))
			{
				throw new BizLogicException(null, null, "participant.gender.errMsg", "");
			}
		}

		if (!Validator.isEmpty(participant.getSexGenotype()))
		{
			final List genotypeList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_GENOTYPE, null);
			if (!Validator.isEnumeratedOrNullValue(genotypeList, participant.getSexGenotype()))
			{
				throw new BizLogicException(null, null, "participant.genotype.errMsg", "");
			}
		}

		final Collection paticipantMedCol = participant.getParticipantMedicalIdentifierCollection();
		//Created a new PMI collection for bulk operation functionality.
		final Collection newPMICollection = new LinkedHashSet();
		if (paticipantMedCol != null && !paticipantMedCol.isEmpty())
		{
			final Iterator itr = paticipantMedCol.iterator();
			while (itr.hasNext())
			{
				final IParticipantMedicalIdentifier<IParticipant, ISite> partiMedobj = (IParticipantMedicalIdentifier<IParticipant, ISite>) itr
						.next();
				final ISite site = partiMedobj.getSite();
				final String medicalRecordNo = partiMedobj.getMedicalRecordNumber();
				if (validator.isEmpty(medicalRecordNo) || site == null || site.getId() == null)
				{
					if (partiMedobj.getId() == null)
					{
						throw new BizLogicException(null, null,
								"errors.participant.extiden.missing", "");
					}
				}
				else
				{
					newPMICollection.add(partiMedobj);
				}
			}
		}
		participant.setParticipantMedicalIdentifierCollection(newPMICollection);

		final Collection<Race> raceCollection = participant.getRaceCollection();
		if (raceCollection != null && !raceCollection.isEmpty())
		{
			final List raceList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_RACE, null);
			final Iterator<Race> itr = raceCollection.iterator();
			while (itr.hasNext())
			{
				final IRace race = itr.next();
				if (race != null)
				{
					final String raceName = race.getRaceName();
					if (!Validator.isEmpty(raceName)
							&& !Validator.isEnumeratedOrNullValue(raceList, raceName))
					{
						throw new BizLogicException(null, null, "participant.race.errMsg", "");
					}
				}
			}
		}

		if (!Validator.isEmpty(participant.getEthnicity()))
		{
			final List ethnicityList = CDEManager.getCDEManager().getPermissibleValueList(
					Constants.CDE_NAME_ETHNICITY, null);
			if (!Validator.isEnumeratedOrNullValue(ethnicityList, participant.getEthnicity()))
			{
				throw new BizLogicException(null, null, "participant.ethnicity.errMsg", "");
			}
		}

		if (operation.equals(Constants.ADD))
		{
			if (!Status.ACTIVITY_STATUS_ACTIVE.toString().equals(participant.getActivityStatus()))
			{
				throw new BizLogicException(null, null, "activityStatus.active.errMsg", "");
			}
		}
		else
		{
			if (!Validator.isEnumeratedValue(Constants.ACTIVITY_STATUS_VALUES, participant
					.getActivityStatus()))
			{
				throw new BizLogicException(null, null, "activityStatus.errMsg", "");
			}
		}
		return true;
	}

	/**
	 * Modify participant object.
	 *
	 * @param dao the dao
	 * @param sessionDataBean the session data bean
	 * @param participant the participant
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	public void modifyParticipantObject(final DAO dao, final SessionDataBean sessionDataBean,
			final IParticipant participant, final IParticipant oldParticipant)
			throws BizLogicException
	{
		try
		{
			updateParticipant(dao, sessionDataBean, participant, oldParticipant);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		catch (BizLogicException biz)
		{
			logger.debug(biz.getMessage(), biz);
			throw getBizLogicException(biz, biz.getErrorKeyName(), biz.getMsgValues());

		}
		catch (Exception exception)
		{
			throw getBizLogicException(exception, "Error while updating object", "");
		}
	}

	/**
	 * This method will update Participant Object.
	 *
	 * @param participant Participant object
	 * @param oldParticipant Persistent participant object
	 * @param dao DAO Object
	 * @param sessionDataBean SessionDataBean Object
	 *
	 * @return AuditManager
	 *
	 * @throws BizLogicException BizLogicException Exception
	 * @throws DAOException DAOException Exception
	 * @throws AuditException AuditException Exception
	 */
	private void updateParticipant(final DAO dao, final SessionDataBean sessionDataBean,
			final IParticipant participant, final IParticipant oldParticipant)
			throws BizLogicException, DAOException
	{
		update(dao, participant, oldParticipant);
	}

	/**
	 * check not null.
	 *
	 * @param object object
	 *
	 * @return boolean
	 */

	public static boolean isNullobject(Object object)
	{
		boolean result = true;
		if (object != null)
		{
			result = false;
		}
		return result;
	}

	/**
	 * Sets the participant medical identifier default.
	 *
	 * @param partMedIdentifier the part med identifier
	 *
	 * @throws BizLogicException the biz logic exception
	 * @throws ParticipantManagerException
	 */
	public static void setParticipantMedicalIdentifierDefault(
			IParticipantMedicalIdentifier<IParticipant, ISite> partMedIdentifier)
			throws BizLogicException, ParticipantManagerException
	{
		if (isNullobject(partMedIdentifier.getSite()))
		{
			final ISite site = (ISite) ParticipantManagerUtility.getSiteInstance();
			partMedIdentifier.setSite(site);
		}
	}

	/**
	 * Post insert.
	 *
	 * @param obj the obj
	 * @param sessionDataBean the session data bean
	 * @throws ApplicationException
	 */
	public static void postInsert(final Object obj, LinkedHashSet<Long> userIdSet)
			throws BizLogicException
	{

	}


	/**
	 * Pre update.
	 *
	 * @param obj the obj
	 * @param sessionDataBean the session data bean
	 * @throws BizLogicException
	 * @throws ApplicationException
	 */
	public static void preUpdate(Object oldObj, Object obj, SessionDataBean sessionDataBean) throws BizLogicException
	{

	}

	/**
	 * Post update.
	 *
	 * @param obj the obj
	 * @param sessionDataBean the session data bean
	 *
	 * @throws BizLogicException the biz logic exception
	 */
	public static void postUpdate(Object oldObj, Object currentObj, SessionDataBean sessionDataBean)
			throws BizLogicException
	{


	}


}