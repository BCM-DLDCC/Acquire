package edu.wustl.catissuecore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;

import edu.wustl.catissuecore.actionForm.NewSpecimenForm;
import edu.wustl.catissuecore.bean.CollectionProtocolBean;
import edu.wustl.catissuecore.bean.CollectionProtocolEventBean;
import edu.wustl.catissuecore.bean.GenericSpecimen;
import edu.wustl.catissuecore.bean.SpecimenRequirementBean;
import edu.wustl.catissuecore.bizlogic.CollectionProtocolBizLogic;
import edu.wustl.catissuecore.bizlogic.NewSpecimenBizLogic;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.CellSpecimenRequirement;
import edu.wustl.catissuecore.domain.ClinicalDiagnosis;
import edu.wustl.catissuecore.domain.CollectionEventParameters;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolEvent;
import edu.wustl.catissuecore.domain.ConsentTier;
import edu.wustl.catissuecore.domain.FluidSpecimenRequirement;
import edu.wustl.catissuecore.domain.MolecularSpecimenRequirement;
import edu.wustl.catissuecore.domain.ReceivedEventParameters;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCharacteristics;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;
import edu.wustl.catissuecore.domain.SpecimenEventParameters;
import edu.wustl.catissuecore.domain.SpecimenRequirement;
import edu.wustl.catissuecore.domain.TissueSpecimenRequirement;
import edu.wustl.catissuecore.domain.User;
import edu.wustl.catissuecore.multiRepository.bean.SiteUserRolePrivilegeBean;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;

// TODO: Auto-generated Javadoc
/**
 * The Class CollectionProtocolUtil.
 */
public class CollectionProtocolUtil
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getCommonLogger(CollectionProtocolUtil.class);

	/** The Constant MOLECULAR_SPECIMEN_CLASS. */
	private static final String MOLECULAR_SPECIMEN_CLASS = "Molecular";

	/** The event bean. */
	private LinkedHashMap<String, CollectionProtocolEventBean> eventBean =
						new LinkedHashMap<String, CollectionProtocolEventBean> ();

	/** The Constant STORAGE_TYPE_ARR. */
	private static final String STORAGE_TYPE_ARR[]= {"Virtual", "Auto","Manual"};

	/**
	 * Gets the storage type value.
	 *
	 * @param type the type
	 *
	 * @return the storage type value
	 */
	public static Integer getStorageTypeValue(String type)
	{
		int returnVal=Integer.valueOf(0);
		for (int ctr=0;ctr<STORAGE_TYPE_ARR.length;ctr++)
		{
			if(STORAGE_TYPE_ARR[ctr].equals(type))
			{
				returnVal=Integer.valueOf(ctr);
				break;
			}
		}

		return returnVal; 	//default considered as 'Virtual';
	}

	/**
	 * Gets the storage type value.
	 *
	 * @param type the type
	 *
	 * @return the storage type value
	 */
	public static String getStorageTypeValue(Integer type)
	{
		String storeArr;//STORAGE_TYPE_ARR[type.intValue()];
		if (type == null)
		{
			storeArr=STORAGE_TYPE_ARR[0]; //default considered as 'Virtual';
		}
		else
		{
			storeArr=STORAGE_TYPE_ARR[type.intValue()];
		}
		//if(type.intValue()>2) return storageTypeArr[1];
		return storeArr;
	}

	/**
	 * Gets the collection protocol bean.
	 *
	 * @param collectionProtocol the collection protocol
	 *
	 * @return the collection protocol bean
	 */
	public static CollectionProtocolBean getCollectionProtocolBean(CollectionProtocol collectionProtocol)
	{
		CollectionProtocolBean collectionProtocolBean;

		collectionProtocolBean = new CollectionProtocolBean();
		collectionProtocolBean.setConsentTierCounter(collectionProtocol.getConsentTierCollection().size());
		Long identifier = Long.valueOf(collectionProtocol.getId().longValue());
		collectionProtocolBean.setIdentifier(identifier);

		long[] coordinatorIds = null;
		coordinatorIds = getProtocolCordnateIds(collectionProtocol, coordinatorIds);

		collectionProtocolBean.setCoordinatorIds(coordinatorIds);

		/**For Clinical Diagnosis subset **/
		collectionProtocolBean.setClinicalDiagnosis(getClinicalDiagnosis(collectionProtocol));

		setBasicCPProps(collectionProtocol, collectionProtocolBean);
		if(collectionProtocol.getCollectionProtocolRegistrationCollection().size() > 0)
		{
			collectionProtocolBean.setParticiapantReg(true);
		}

		collectionProtocolBean.setType(collectionProtocol.getType());
		collectionProtocolBean.setSequenceNumber(collectionProtocol.getSequenceNumber());
		collectionProtocolBean.setStudyCalendarEventPoint(collectionProtocol.getStudyCalendarEventPoint());

		if(collectionProtocol.getParentCollectionProtocol() != null)
		{
			collectionProtocolBean.setParentCollectionProtocolId(collectionProtocol.getParentCollectionProtocol().getId());
		}

		return collectionProtocolBean;
	}

	/**
	 * Sets the basic cp props.
	 *
	 * @param collectionProtocol the collection protocol
	 * @param collectionProtocolBean the collection protocol bean
	 */
	private static void setBasicCPProps(CollectionProtocol collectionProtocol,
			CollectionProtocolBean collectionProtocolBean)
	{
		collectionProtocolBean.setPrincipalInvestigatorId(collectionProtocol.getPrincipalInvestigator().getId().longValue());
		Date date = collectionProtocol.getStartDate();
		collectionProtocolBean.setStartDate(edu.wustl.common.util.Utility.parseDateToString(date, Constants.DATE_FORMAT) );
		collectionProtocolBean.setDescriptionURL(collectionProtocol.getDescriptionURL());
		collectionProtocolBean.setUnsignedConsentURLName(collectionProtocol.getUnsignedConsentDocumentURL());
		setLabelFormatProps(collectionProtocol, collectionProtocolBean);
		if(collectionProtocol.getConsentsWaived()==null)
		{
			collectionProtocol.setConsentsWaived(false);
		}
		collectionProtocolBean.setConsentWaived (collectionProtocol.getConsentsWaived().booleanValue());
		collectionProtocolBean.setIrbID(collectionProtocol.getIrbIdentifier());
		collectionProtocolBean.setTitle(collectionProtocol.getTitle());
		collectionProtocolBean.setShortTitle(collectionProtocol.getShortTitle());
		collectionProtocolBean.setEnrollment(String.valueOf(collectionProtocol.getEnrollment()));
		collectionProtocolBean.setConsentValues(prepareConsentTierMap(collectionProtocol.getConsentTierCollection()));
		collectionProtocolBean.setActivityStatus(collectionProtocol.getActivityStatus());
		collectionProtocolBean.setAliqoutInSameContainer(collectionProtocol.getAliquotInSameContainer().booleanValue());
		String endDate = Utility.parseDateToString(collectionProtocol.getEndDate(),CommonServiceLocator.getInstance().getDatePattern());
		collectionProtocolBean.setEndDate(endDate);
	}

	/**
	 * Sets label format properties.
	 *
	 * @param collectionProtocol the collection protocol
	 * @param collectionProtocolBean the collection protocol bean
	 */
	private static void setLabelFormatProps(CollectionProtocol collectionProtocol,
			CollectionProtocolBean collectionProtocolBean)
	{
		collectionProtocolBean.setLabelFormat(collectionProtocol.getSpecimenLabelFormat());
		collectionProtocolBean.setDerivativeLabelFormat(collectionProtocol.getDerivativeLabelFormat());
		collectionProtocolBean.setAliquotLabelFormat(collectionProtocol.getAliquotLabelFormat());
	}

	/**
	 * Gets the protocol cordnate ids.
	 *
	 * @param collectionProtocol the collection protocol
	 * @param coordinatorIds the coordinator ids
	 *
	 * @return the protocol cordnate ids
	 */
	private static long[] getProtocolCordnateIds(CollectionProtocol collectionProtocol,
			long[] coordinatorIds)
	{
		Collection userCollection = collectionProtocol.getCoordinatorCollection();
		if(userCollection != null)
		{
			coordinatorIds = new long[userCollection.size()];
			int counter=0;
			Iterator iterator = userCollection.iterator();
			while(iterator.hasNext())
			{
				User user = (User)iterator.next();
				coordinatorIds[counter] = user.getId().longValue();
				counter++;
			}
		}
		return coordinatorIds;
	}

	/**
	 * Returns string array of clinical diagnosis.
	 *
	 * @param collectionProtocol the collection protocol
	 *
	 * @return the clinical diagnosis
	 */
	private static String[] getClinicalDiagnosis(CollectionProtocol collectionProtocol)
	{
		String[] clinicalDiagnosisArr = null;
		Collection<ClinicalDiagnosis> clinicDiagnosisCollection = collectionProtocol.getClinicalDiagnosisCollection();
		if(clinicDiagnosisCollection != null)
		{
			clinicalDiagnosisArr = new String[clinicDiagnosisCollection.size()];
			int index=0;
			Iterator<ClinicalDiagnosis> iterator = clinicDiagnosisCollection.iterator();
			while(iterator.hasNext())
			{
				ClinicalDiagnosis clinicalDiagnosis = (ClinicalDiagnosis)iterator.next();
				clinicalDiagnosisArr[index] = clinicalDiagnosis.getName();
				index++;
			}
		}
		return clinicalDiagnosisArr;
	}


	/**
	 * Prepare consent tier map.
	 *
	 * @param consentTierColl the consent tier coll
	 *
	 * @return the map
	 */
	public static Map prepareConsentTierMap(Collection consentTierColl)
	{
		Map tempMap = new LinkedHashMap();//bug 8905
		List<ConsentTier> consentsList = new ArrayList<ConsentTier>();
		if(consentTierColl!=null)
		{
			consentsList.addAll(consentTierColl);//bug 8905
			Collections.sort(consentsList,new IdComparator());//bug 8905
			//Iterator consentTierCollIter = consentTierColl.iterator();
			Iterator consentTierCollIter = consentsList.iterator();//bug 8905
			int counter = 0;
			while(consentTierCollIter.hasNext())
			{
				ConsentTier consent = (ConsentTier)consentTierCollIter.next();
				String statement = "ConsentBean:"+counter+"_statement";
				String statementkey = "ConsentBean:"+counter+"_consentTierID";
				tempMap.put(statement, consent.getStatement());
				tempMap.put(statementkey, String.valueOf(consent.getId()));
				counter++;
			}
		}
		return tempMap;
	}

	/**
	 * Gets the collection protocol event bean.
	 *
	 * @param collectionProtocolEvent the collection protocol event
	 * @param counter the counter
	 * @param dao the dao
	 *
	 * @return the collection protocol event bean
	 *
	 * @throws DAOException the DAO exception
	 */
	public static CollectionProtocolEventBean getCollectionProtocolEventBean(
			CollectionProtocolEvent  collectionProtocolEvent, int counter, DAO dao) throws DAOException
	{
		CollectionProtocolEventBean eventBean = new CollectionProtocolEventBean();
		eventBean.setId(collectionProtocolEvent.getId().longValue());
		eventBean.setStudyCalenderEventPoint(new Double
				(collectionProtocolEvent.getStudyCalendarEventPoint()));
		eventBean.setCollectionPointLabel(collectionProtocolEvent.getCollectionPointLabel());
		eventBean.setClinicalDiagnosis(collectionProtocolEvent.getClinicalDiagnosis());
		eventBean.setClinicalStatus(collectionProtocolEvent.getClinicalStatus());
		eventBean.setId(collectionProtocolEvent.getId().longValue());
		eventBean.setUniqueIdentifier("E"+ counter++);
		eventBean.setSpecimenCollRequirementGroupId(
				collectionProtocolEvent.getId().longValue());
		eventBean.setSpecimenRequirementbeanMap(
				getSpecimensMap(collectionProtocolEvent.getSpecimenRequirementCollection(),
						eventBean.getUniqueIdentifier()) );

		eventBean.setLabelFormat(collectionProtocolEvent.getLabelFormat());

		return eventBean;
	}

	/**
	 * Gets the sorted cp event list.
	 *
	 * @param genericList the generic list
	 *
	 * @return the sorted cp event list
	 */
	public static List getSortedCPEventList(List genericList)
	{
		//Comparator to sort the List of Map chronologically.
		final Comparator identifierComparator = new Comparator()
		{
			public int compare(Object object1, Object object2)
			{
				Long identifier1 = null;
				Long identifier2 = null;

				if(object1 instanceof CollectionProtocolEvent)
				{
					identifier1 = ((CollectionProtocolEvent)object1).getId();
					identifier2 = ((CollectionProtocolEvent)object2).getId();
				}
				else if(object1 instanceof SpecimenRequirement)
				{
					identifier1 = ((SpecimenRequirement)object1).getId();
					identifier2 = ((SpecimenRequirement)object2).getId();
				}
				else if (object1 instanceof AbstractSpecimen)
				{
					identifier1 = ((AbstractSpecimen) object1).getId();
					identifier2 = ((AbstractSpecimen) object2).getId();
				}

				if(identifier1!= null && identifier2 != null)
				{
					return identifier1.compareTo(identifier2);
				}
				if(identifier1 ==null)
				{
					return -1;
				}
				if(identifier2 == null)
				{
					return 1;
				}
				return 0;
			}
		};
		Collections.sort(genericList, identifierComparator);
		return genericList;
	}

	/**
	 * Gets the specimens map.
	 *
	 * @param reqSpecimenCollection the req specimen collection
	 * @param parentUniqueId the parent unique id
	 *
	 * @return the specimens map
	 */
	public static LinkedHashMap<String, GenericSpecimen> getSpecimensMap(Collection<SpecimenRequirement> reqSpecimenCollection,
			String parentUniqueId)
	{
		LinkedHashMap<String, GenericSpecimen> reqSpecimenMap = new LinkedHashMap<String, GenericSpecimen>();
		List<SpecimenRequirement> reqSpecimenList = new LinkedList<SpecimenRequirement>(reqSpecimenCollection);
		getSortedCPEventList(reqSpecimenList);
		Iterator<SpecimenRequirement> specimenIterator = reqSpecimenList.iterator();
		int specCtr=0;
		while(specimenIterator.hasNext())
		{
			SpecimenRequirement reqSpecimen = specimenIterator.next();
			if (reqSpecimen.getParentSpecimen() == null)
			{
				SpecimenRequirementBean specBean =getSpecimenBean(reqSpecimen, null, parentUniqueId, specCtr++);
				reqSpecimenMap.put(specBean.getUniqueIdentifier(), specBean);
			}
		}
		return reqSpecimenMap;
	}

	/**
	 * Gets the child aliquots.
	 *
	 * @param reqSpecimen the req specimen
	 * @param parentuniqueId the parentunique id
	 * @param parentName the parent name
	 *
	 * @return the child aliquots
	 */
	private static LinkedHashMap<String, GenericSpecimen> getChildAliquots(SpecimenRequirement reqSpecimen,
			String parentuniqueId, String parentName)
	{
		Collection reqSpecimenChildren = reqSpecimen.getChildSpecimenCollection();
		List reqSpecimenList = new LinkedList<SpecimenRequirement>(reqSpecimenChildren);
		getSortedCPEventList(reqSpecimenList);
		Iterator<SpecimenRequirement> iterator = reqSpecimenList.iterator();
		LinkedHashMap<String, GenericSpecimen>  aliquotMap = new
			LinkedHashMap<String, GenericSpecimen> ();
		int aliqCtr =1;

		while(iterator.hasNext())
		{
			SpecimenRequirement childReqSpecimen = iterator.next();
			if(Constants.ALIQUOT.equals(childReqSpecimen.getLineage()))
			{
				SpecimenRequirementBean specimenBean = getSpecimenBean(
						childReqSpecimen, parentName, parentuniqueId, aliqCtr++);
				aliquotMap.put(specimenBean.getUniqueIdentifier(), specimenBean);
			}
		}
		return aliquotMap;
	}

	/**
	 * Gets the child derived.
	 *
	 * @param specimen the specimen
	 * @param parentuniqueId the parentunique id
	 * @param parentName the parent name
	 *
	 * @return the child derived
	 */
	private static LinkedHashMap<String, GenericSpecimen> getChildDerived(SpecimenRequirement specimen,
			String parentuniqueId, String parentName)
	{
		Collection specimenChildren = specimen.getChildSpecimenCollection();
		List specimenList = new LinkedList(specimenChildren);
		getSortedCPEventList(specimenList);
		Iterator<SpecimenRequirement> iterator = specimenList.iterator();
		LinkedHashMap<String, GenericSpecimen>  derivedMap = new
			LinkedHashMap<String, GenericSpecimen> ();
		int deriveCtr=1;
		while(iterator.hasNext())
		{
			SpecimenRequirement childReqSpecimen = iterator.next();
			if(Constants.DERIVED_SPECIMEN.equals(childReqSpecimen.getLineage()))
			{
				SpecimenRequirementBean specimenBean =
					getSpecimenBean(childReqSpecimen, parentName,
							parentuniqueId, deriveCtr++);
				derivedMap.put(specimenBean.getUniqueIdentifier(), specimenBean);
			}
		}


		return derivedMap;
	}

	/**
	 * Gets the unique id.
	 *
	 * @param lineage the lineage
	 * @param ctr the ctr
	 *
	 * @return the unique id
	 */
	private static String getUniqueId(String lineage, int ctr)
	{	String constantVal=null;
		if(Constants.NEW_SPECIMEN.equals(lineage))
		{
			constantVal=Constants.UNIQUE_IDENTIFIER_FOR_NEW_SPECIMEN + ctr;
		}

		else if(Constants.DERIVED_SPECIMEN.equals(lineage))
		{
			constantVal=Constants.UNIQUE_IDENTIFIER_FOR_DERIVE + ctr;
		}
		else if(Constants.ALIQUOT.equals(lineage))
		{
			constantVal= Constants.UNIQUE_IDENTIFIER_FOR_ALIQUOT + ctr;
		}
		return constantVal;
	}

	/**
	 * Gets the specimen bean.
	 *
	 * @param reqSpecimen the req specimen
	 * @param parentName the parent name
	 * @param parentUniqueId the parent unique id
	 * @param specCtr the spec ctr
	 *
	 * @return the specimen bean
	 */
	private static SpecimenRequirementBean getSpecimenBean(SpecimenRequirement reqSpecimen, String parentName,
										String parentUniqueId, int specCtr)
	{

		SpecimenRequirementBean speRequirementBean = new SpecimenRequirementBean();
		speRequirementBean.setId(reqSpecimen.getId().longValue());
		speRequirementBean.setLineage(reqSpecimen.getLineage());
		speRequirementBean.setUniqueIdentifier(
				parentUniqueId + getUniqueId(reqSpecimen.getLineage(),specCtr));

		speRequirementBean.setDisplayName(Constants.ALIAS_SPECIMEN
						+ "_" + speRequirementBean.getUniqueIdentifier());

		speRequirementBean.setClassName(reqSpecimen.getClassName());
		speRequirementBean.setType(reqSpecimen.getSpecimenType());
		speRequirementBean.setId(reqSpecimen.getId().longValue());
		SpecimenCharacteristics characteristics = reqSpecimen.getSpecimenCharacteristics();
		updateSpeRequirementBean(reqSpecimen, speRequirementBean,
				characteristics);

		Double quantity = reqSpecimen.getInitialQuantity();

		if(quantity != null)
		{
			speRequirementBean.setQuantity(String.valueOf(quantity));
		}

		if(reqSpecimen.getStorageType() != null)
		{
			speRequirementBean.setStorageContainerForSpecimen(reqSpecimen.getStorageType());
		}
		setSpecimenEventParameters(reqSpecimen,speRequirementBean );

		setAliquotAndDerivedColl(reqSpecimen, parentName, speRequirementBean);
		speRequirementBean.setLabelFormat(reqSpecimen.getLabelFormat());
		return speRequirementBean;
	}

	/**
	 * set Specimen Requirements.
	 *
	 * @param reqSpecimen the req specimen
	 * @param speRequirementBean the spe requirement bean
	 * @param characteristics the characteristics
	 */
	private static void updateSpeRequirementBean(
			SpecimenRequirement reqSpecimen,
			SpecimenRequirementBean speRequirementBean,
			SpecimenCharacteristics characteristics)
	{
		if(characteristics != null)
		{
			speRequirementBean.setTissueSite(characteristics.getTissueSite());
			speRequirementBean.setTissueSide(characteristics.getTissueSide());
		}

		speRequirementBean.setSpecimenCharsId(reqSpecimen.getSpecimenCharacteristics().getId().longValue());
		speRequirementBean.setPathologicalStatus(reqSpecimen.getPathologicalStatus());

		if(MOLECULAR_SPECIMEN_CLASS.equals(reqSpecimen.getClassName()))
		{
			Double concentration = ((MolecularSpecimenRequirement)reqSpecimen).getConcentrationInMicrogramPerMicroliter();
			if (concentration != null)
			{
				speRequirementBean.setConcentration( String.valueOf(concentration.doubleValue()));
			}
		}
	}

	/**
	 * Sets the aliquot and derived coll.
	 *
	 * @param reqSpecimen the req specimen
	 * @param parentName the parent name
	 * @param speRequirementBean the spe requirement bean
	 */
	private static void setAliquotAndDerivedColl(
			SpecimenRequirement reqSpecimen, String parentName,
			SpecimenRequirementBean speRequirementBean)
	{
		speRequirementBean.setParentName(parentName);

		LinkedHashMap<String, GenericSpecimen> aliquotMap =
			getChildAliquots(reqSpecimen, speRequirementBean.getUniqueIdentifier(),speRequirementBean.getDisplayName());
		LinkedHashMap<String, GenericSpecimen> derivedMap =
			getChildDerived(reqSpecimen, speRequirementBean.getUniqueIdentifier(), speRequirementBean.getDisplayName());

		Collection aliquotCollection = aliquotMap.values();
		Collection derivedCollection = derivedMap.values();
		//added method
		setQuantityPerAliquot(speRequirementBean, aliquotCollection);

		speRequirementBean.setNoOfAliquots(String.valueOf(aliquotCollection.size()));
		speRequirementBean.setAliquotSpecimenCollection(aliquotMap);

		speRequirementBean.setDeriveSpecimenCollection(derivedMap);
		speRequirementBean.setNoOfDeriveSpecimen(derivedCollection.size());
		derivedMap = getDerviredObjectMap(derivedMap.values());
	    speRequirementBean.setDeriveSpecimen(derivedMap);
	    setDeriveQuantity(speRequirementBean, derivedCollection);

	}


	/**
	 * set specimen requirement bean by DerivedCollection.
	 *
	 * @param speRequirementBean the spe requirement bean
	 * @param derivedCollection the derived collection
	 */
	private static void setDeriveQuantity(
			SpecimenRequirementBean speRequirementBean,
			Collection derivedCollection)
	{
		if (derivedCollection != null && !derivedCollection.isEmpty())
		{
			Iterator iterator = derivedCollection.iterator();
			GenericSpecimen derivedSpecimen = (GenericSpecimen)iterator.next();
			speRequirementBean.setDeriveClassName(derivedSpecimen.getClassName());
			speRequirementBean.setDeriveType(derivedSpecimen.getType());
			speRequirementBean.setDeriveConcentration(derivedSpecimen.getConcentration());
			speRequirementBean.setDeriveQuantity(derivedSpecimen.getQuantity());
		}
	}

	/**
	 * set specimen requirement bean by AliquotCollection.
	 *
	 * @param speRequirementBean the spe requirement bean
	 * @param aliquotCollection the aliquot collection
	 */
	private static void setQuantityPerAliquot(
			SpecimenRequirementBean speRequirementBean,
			Collection aliquotCollection)
	{
		if (aliquotCollection != null && !aliquotCollection.isEmpty())
		{
			Iterator iterator = aliquotCollection.iterator();
			GenericSpecimen aliquotSpecimen = (GenericSpecimen)iterator.next();
			speRequirementBean.setStorageContainerForAliquotSpecimem(
					aliquotSpecimen.getStorageContainerForSpecimen() );
			speRequirementBean.setQuantityPerAliquot(aliquotSpecimen.getQuantity());
		}
	}

	/**
	 * Gets the dervired object map.
	 *
	 * @param derivedCollection the derived collection
	 *
	 * @return the dervired object map
	 */
	public static LinkedHashMap getDerviredObjectMap(Collection<GenericSpecimen> derivedCollection)
	{
		LinkedHashMap<String, String> derivedObjectMap = new LinkedHashMap<String, String> ();
		Iterator<GenericSpecimen> iterator = derivedCollection.iterator();
		int deriveCtr=1;
		while (iterator.hasNext())
		{
			SpecimenRequirementBean derivedSpecimen = (SpecimenRequirementBean) iterator.next();

			StringBuffer derivedSpecimenKey = getKeyBase(deriveCtr);
			derivedSpecimenKey.append("_id");
			derivedObjectMap.put(derivedSpecimenKey.toString(), String.valueOf(derivedSpecimen.getId()));

			derivedSpecimenKey = getKeyBase(deriveCtr);
			derivedSpecimenKey.append("_specimenClass" );
			derivedObjectMap.put(derivedSpecimenKey.toString(), derivedSpecimen.getClassName());

			derivedSpecimenKey = getKeyBase(deriveCtr);
			derivedSpecimenKey.append("_specimenType" );
			derivedObjectMap.put(derivedSpecimenKey.toString(), derivedSpecimen.getType());

			derivedSpecimenKey = getKeyBase(deriveCtr);
			derivedSpecimenKey.append("_storageLocation" );
			derivedObjectMap.put(derivedSpecimenKey.toString(),
					derivedSpecimen.getStorageContainerForSpecimen());

			derivedSpecimenKey = getKeyBase(deriveCtr);
			derivedSpecimenKey.append("_quantity" );
			String quantity = derivedSpecimen.getQuantity();
			derivedObjectMap.put(derivedSpecimenKey.toString(), quantity);

			derivedSpecimenKey = getKeyBase(deriveCtr);
			derivedSpecimenKey.append("_concentration" );

			derivedObjectMap.put(derivedSpecimenKey.toString(),
					derivedSpecimen.getConcentration());
			derivedSpecimenKey = getKeyBase(deriveCtr);
			derivedSpecimenKey.append("_unit" );
			derivedObjectMap.put(derivedSpecimenKey.toString(), "");


			derivedSpecimenKey = getKeyBase(deriveCtr);
			derivedSpecimenKey.append("_labelFormat" );
			derivedObjectMap.put(derivedSpecimenKey.toString(), derivedSpecimen.getLabelFormat());

			deriveCtr++;
		}
		return derivedObjectMap;
	}

	/**
	 * Gets the key base.
	 *
	 * @param deriveCtr the derive ctr
	 *
	 * @return the key base
	 */
	private static StringBuffer getKeyBase(int deriveCtr)
	{
		StringBuffer derivedSpecimenKey = new StringBuffer();
		derivedSpecimenKey.append("DeriveSpecimenBean:");
		derivedSpecimenKey.append(String.valueOf(deriveCtr));
		return derivedSpecimenKey;
	}

	/**
	 * Sets the specimen event parameters.
	 *
	 * @param reqSpecimen the req specimen
	 * @param specimenRequirementBean the specimen requirement bean
	 */
	private  static void setSpecimenEventParameters(SpecimenRequirement reqSpecimen, SpecimenRequirementBean specimenRequirementBean)
	{
		Collection eventsParametersColl = reqSpecimen.getSpecimenEventCollection();
		if(eventsParametersColl == null || eventsParametersColl.isEmpty())
		{
			return;
		}

		Iterator iter = eventsParametersColl.iterator();

		while(iter.hasNext())
		{
			setSpecimenEvents(specimenRequirementBean, iter);
		}

	}

	/**
	 * set setSpeciEevntParams.
	 *
	 * @param specimenRequirementBean the specimen requirement bean
	 * @param iter the iter
	 */
	private static void setSpecimenEvents(
			SpecimenRequirementBean specimenRequirementBean, Iterator iter)
	{
		Object tempObj = iter.next();

		if(tempObj instanceof CollectionEventParameters)
		{
			CollectionEventParameters collectionEventParameters = (CollectionEventParameters)tempObj;
			specimenRequirementBean.setCollectionEventId(collectionEventParameters.getId().longValue());
			//this.collectionEventSpecimenId = collectionEventParameters.getSpecimen().getId().longValue();
			specimenRequirementBean.setCollectionEventUserId(
					collectionEventParameters.getUser().getId().longValue());
			specimenRequirementBean.setCollectionEventCollectionProcedure(
					collectionEventParameters.getCollectionProcedure());

			specimenRequirementBean.setCollectionEventContainer(collectionEventParameters.getContainer());
		}
		else if(tempObj instanceof ReceivedEventParameters)
		{
			ReceivedEventParameters receivedEventParameters = (ReceivedEventParameters)tempObj;

			specimenRequirementBean.setReceivedEventId(receivedEventParameters.getId().longValue());
			specimenRequirementBean.setReceivedEventUserId(
					receivedEventParameters.getUser().getId().longValue());
			specimenRequirementBean.setReceivedEventReceivedQuality(
					receivedEventParameters.getReceivedQuality());
		}
	}

	/**
	 * Update session.
	 *
	 * @param request the request
	 * @param identifieer the identifieer
	 *
	 * @throws ApplicationException the application exception
	 */
	public static void updateSession(HttpServletRequest request,  Long identifieer)
			throws ApplicationException{

		List sessionCpList = new CollectionProtocolBizLogic().retrieveCP(identifieer);

		if (sessionCpList == null || sessionCpList.size()<2){

			throw new ApplicationException(ErrorKey.getErrorKey("errors.item"),null,"Fail to retrieve Collection protocol..");
		}

		HttpSession session = request.getSession();
		session.removeAttribute(Constants.COLLECTION_PROTOCOL_SESSION_BEAN);
		session.removeAttribute(Constants.COLLECTION_PROTOCOL_EVENT_SESSION_MAP);
		setPrivilegesForCP(identifieer,session);
		CollectionProtocolBean collectionProtocolBean =
			(CollectionProtocolBean)sessionCpList.get(0);
		collectionProtocolBean.setOperation("update");
		session.setAttribute(Constants.COLLECTION_PROTOCOL_SESSION_BEAN,
				sessionCpList.get(0));
		session.setAttribute(Constants.COLLECTION_PROTOCOL_EVENT_SESSION_MAP,
				sessionCpList.get(1));
		String cptitle = collectionProtocolBean.getTitle();
		String treeNode = "cpName_"+cptitle;
		session.setAttribute(Constants.TREE_NODE_ID, treeNode);
		session.setAttribute("tempKey", treeNode);

	}

	/**
	 * Sets the privileges for cp.
	 *
	 * @param cpId the cp id
	 * @param session the session
	 */
	private static  void setPrivilegesForCP(Long cpId,HttpSession session)
	{
		Map<String,SiteUserRolePrivilegeBean> map = CaTissuePrivilegeUtility.getPrivilegesOnCP(cpId);
		session.setAttribute(Constants.ROW_ID_OBJECT_BEAN_MAP, map);

	}


	/**
	 * Gets the collection protocol event map.
	 *
	 * @param collectionProtocolEventColl the collection protocol event coll
	 * @param dao the dao
	 *
	 * @return the collection protocol event map
	 *
	 * @throws DAOException the DAO exception
	 */
	public static  LinkedHashMap<String, CollectionProtocolEventBean> getCollectionProtocolEventMap(
			Collection collectionProtocolEventColl, DAO dao) throws DAOException
	{
		Iterator iterator = collectionProtocolEventColl.iterator();
		LinkedHashMap<String, CollectionProtocolEventBean> eventMap =
			new LinkedHashMap<String, CollectionProtocolEventBean>();
		int ctr=1;
		while(iterator.hasNext())
		{
			CollectionProtocolEvent collectionProtocolEvent=
				(CollectionProtocolEvent)iterator.next();

			CollectionProtocolEventBean eventBean =
				CollectionProtocolUtil.getCollectionProtocolEventBean(collectionProtocolEvent,ctr++,dao);
			eventMap.put(eventBean.getUniqueIdentifier(), eventBean);
		}
		return eventMap;
	}

	/**
	 * Populate collection protocol objects.
	 *
	 * @param request the request
	 *
	 * @return the collection protocol
	 *
	 * @throws Exception the exception
	 */
	public static CollectionProtocol populateCollectionProtocolObjects(HttpServletRequest request)
		throws Exception
	{

		HttpSession session = request.getSession();
		CollectionProtocolBean collectionProtocolBean = (CollectionProtocolBean) session
				.getAttribute(Constants.COLLECTION_PROTOCOL_SESSION_BEAN);

		LinkedHashMap<String, CollectionProtocolEventBean> cpEventMap = (LinkedHashMap) session
				.getAttribute(Constants.COLLECTION_PROTOCOL_EVENT_SESSION_MAP);
		if (cpEventMap == null)
		{
			throw AppUtility.getApplicationException(null, "event.req", "");
		}
		CollectionProtocol collectionProtocol = createCollectionProtocolDomainObject(collectionProtocolBean);
		Collection collectionProtocolEventList = new LinkedHashSet();

		Collection collectionProtocolEventBeanColl = cpEventMap.values();
		if (collectionProtocolEventBeanColl != null)
		{

			Iterator cpEventIterator = collectionProtocolEventBeanColl.iterator();

			while (cpEventIterator.hasNext()) {

				CollectionProtocolEventBean cpEventBean = (CollectionProtocolEventBean) cpEventIterator
						.next();
				CollectionProtocolEvent collectionProtocolEvent = getCollectionProtocolEvent(cpEventBean);
				collectionProtocolEvent.setCollectionProtocol(collectionProtocol);
				collectionProtocolEventList.add(collectionProtocolEvent);
			}
		}
		collectionProtocol
				.setCollectionProtocolEventCollection(collectionProtocolEventList);
		return collectionProtocol;
	}



	/**
	 * Creates collection protocol domain object from given collection protocol bean.
	 *
	 * @param cpBean the cp bean
	 *
	 * @return the collection protocol
	 *
	 * @throws Exception the exception
	 */
	private static CollectionProtocol createCollectionProtocolDomainObject(
			CollectionProtocolBean cpBean) throws Exception
	{

		CollectionProtocol collectionProtocol = new CollectionProtocol();
		collectionProtocol.setId(cpBean.getIdentifier());
		collectionProtocol.setSpecimenLabelFormat(cpBean.getLabelFormat());
		collectionProtocol.setDerivativeLabelFormat(cpBean.getDerivativeLabelFormat());
		collectionProtocol.setAliquotLabelFormat(cpBean.getAliquotLabelFormat());
		collectionProtocol.setActivityStatus(cpBean.getActivityStatus());
		collectionProtocol.setConsentsWaived(cpBean.isConsentWaived());
		collectionProtocol.setAliquotInSameContainer(cpBean.isAliqoutInSameContainer());
		collectionProtocol.setConsentTierCollection(collectionProtocol.prepareConsentTierCollection(cpBean.getConsentValues()));
		Collection coordinatorCollection = new LinkedHashSet();
		Collection<Site> siteCollection = new LinkedHashSet<Site>();
		Collection<ClinicalDiagnosis> clinicalDiagnosisCollection = new LinkedHashSet<ClinicalDiagnosis>();

		setCoordinatorColl(collectionProtocol,
				coordinatorCollection, cpBean);

		/**For Clinical Diagnosis Subset **/
		setClinicalDiagnosis(collectionProtocol,
				clinicalDiagnosisCollection, cpBean);

		setSiteColl(collectionProtocol, siteCollection, cpBean);

		collectionProtocol.setDescriptionURL(cpBean.getDescriptionURL());
		Integer enrollmentNo=null;
		try{
			enrollmentNo = Integer.valueOf(cpBean.getEnrollment());
		}
		catch(NumberFormatException e)
		{
			CollectionProtocolUtil.LOGGER.error(e.getMessage(), e);
			enrollmentNo = Integer.valueOf(0);
		}
		collectionProtocol.setEnrollment(enrollmentNo);
		User principalInvestigator = new User();
		principalInvestigator.setId(Long.valueOf(cpBean
				.getPrincipalInvestigatorId()));

		collectionProtocol.setPrincipalInvestigator(principalInvestigator);
		collectionProtocol.setShortTitle(cpBean.getShortTitle());
		Date startDate = Utility.parseDate(cpBean.getStartDate(), Utility
				.datePattern(cpBean.getStartDate()));
		collectionProtocol.setStartDate(startDate);
		collectionProtocol.setTitle(cpBean.getTitle());
		collectionProtocol.setUnsignedConsentDocumentURL(cpBean
				.getUnsignedConsentURLName());
		collectionProtocol.setIrbIdentifier(cpBean.getIrbID());

		collectionProtocol.setType(cpBean.getType());
		collectionProtocol.setSequenceNumber(cpBean.getSequenceNumber());
		collectionProtocol.setStudyCalendarEventPoint(cpBean.getStudyCalendarEventPoint());

		if(cpBean.getParentCollectionProtocolId() != null && cpBean.getParentCollectionProtocolId() != 0 && !"".equals(cpBean.getParentCollectionProtocolId()))
		{
		 collectionProtocol.setParentCollectionProtocol(getParentCollectionProtocol(cpBean.getParentCollectionProtocolId()));
		}


		return collectionProtocol;
	}

	/**
	 * This method will be called to return the CollectionProtocol.
	 *
	 * @param parentCollectionProtocolId : parentCollectionProtocolId.
	 *
	 * @return the collectionProtocol.
	 *
	 * @throws ApplicationException ApplicationException.
	 */
	public static CollectionProtocol getParentCollectionProtocol(Long parentCollectionProtocolId) throws ApplicationException
	{
		CollectionProtocol collectionProtocol = null;
		DAO dao = null;
		try
		{
			dao = AppUtility.openDAOSession(null);
			collectionProtocol = (CollectionProtocol)dao.retrieveById(CollectionProtocol.class.getName(), parentCollectionProtocolId);

		}
		finally
		{
			AppUtility.closeDAOSession(dao);
		}

		return collectionProtocol;
	}

	/**
	 * Sets the site coll.
	 *
	 * @param collectionProtocol the collection protocol
	 * @param siteCollection the site collection
	 * @param cpBean the cp bean
	 */
	private static void setSiteColl(
			CollectionProtocol collectionProtocol,
			Collection<Site> siteCollection,CollectionProtocolBean cpBean)
	{
		long[] siteArr = cpBean.getSiteIds();
		if (siteArr != null)
		{
			for (int i = 0; i < siteArr.length; i++) {
				if (siteArr[i] != -1) {
					Site site = new Site();
					site.setId(Long.valueOf(siteArr[i]));
					siteCollection.add(site);
				}
			}
			collectionProtocol.setSiteCollection(siteCollection);
		}
	}

	/**
	 * Sets the coordinator coll.
	 *
	 * @param collectionProtocol the collection protocol
	 * @param coordinatorCollection the coordinator collection
	 * @param cpBean the cp bean
	 */
	private static void setCoordinatorColl(
			CollectionProtocol collectionProtocol,
			Collection coordinatorCollection, CollectionProtocolBean cpBean)
	{
		long[] coordinatorsArr = cpBean.getCoordinatorIds();
		if (coordinatorsArr != null)
		{
			for (int i = 0; i < coordinatorsArr.length; i++) {
				if (coordinatorsArr[i] >= 1) {
					User coordinator = new User();
					coordinator.setId(Long.valueOf(coordinatorsArr[i]));
					coordinatorCollection.add(coordinator);
				}
			}
			collectionProtocol.setCoordinatorCollection(coordinatorCollection);
		}
	}

	/**
	 * Sets the clinical diagnosis.
	 *
	 * @param collectionProtocol the collection protocol
	 * @param clinicalDiagnosis the clinical diagnosis
	 * @param cpBean the cp bean
	 */
	private static void setClinicalDiagnosis(
			CollectionProtocol collectionProtocol,
			Collection clinicalDiagnosis, CollectionProtocolBean cpBean)
	{
		String[] clinicalDiagnosisArr = cpBean.getClinicalDiagnosis();


		if (clinicalDiagnosisArr != null)
		{
			for (int i = 0; i < clinicalDiagnosisArr.length; i++) {
				if (!"".equals(clinicalDiagnosisArr[i]))
				{
					final ClinicalDiagnosis clinicalDiagnosisObj = new ClinicalDiagnosis();
					clinicalDiagnosisObj.setName(clinicalDiagnosisArr[i]);
					clinicalDiagnosisObj.setCollectionProtocol(collectionProtocol);
					clinicalDiagnosis.add(clinicalDiagnosisObj);
				}
			}
			collectionProtocol.setClinicalDiagnosisCollection(clinicalDiagnosis);
		}
	}

	/**
	 * This function used to create CollectionProtocolEvent domain object
	 * from given CollectionProtocolEventBean Object.
	 *
	 * @param cpEventBean the cp event bean
	 *
	 * @return CollectionProtocolEvent domain object.
	 */
	private static CollectionProtocolEvent getCollectionProtocolEvent(
		CollectionProtocolEventBean cpEventBean)
	{

		CollectionProtocolEvent collectionProtocolEvent = new CollectionProtocolEvent();
		collectionProtocolEvent.setClinicalStatus(cpEventBean.getClinicalStatus());
		collectionProtocolEvent.setCollectionPointLabel(cpEventBean.getCollectionPointLabel());
		collectionProtocolEvent.setStudyCalendarEventPoint(cpEventBean.getStudyCalenderEventPoint());
		collectionProtocolEvent.setActivityStatus(Status.ACTIVITY_STATUS_ACTIVE.toString());
		collectionProtocolEvent.setClinicalDiagnosis(cpEventBean.getClinicalDiagnosis());
		if (cpEventBean.getId()==-1)
		{
			collectionProtocolEvent.setId(null);
		}
		else
		{
			collectionProtocolEvent.setId(Long.valueOf(cpEventBean.getId()));
		}

		Collection specimenCollection =null;
		Map specimenMap =(Map)cpEventBean.getSpecimenRequirementbeanMap();

		if (specimenMap!=null && !specimenMap.isEmpty())
		{
			specimenCollection =getReqSpecimens(specimenMap.values()
					,null, collectionProtocolEvent);
		}

		collectionProtocolEvent.setSpecimenRequirementCollection(specimenCollection);
		collectionProtocolEvent.setLabelFormat(cpEventBean.getLabelFormat());

		return collectionProtocolEvent;
	}


	/**
	 * creates collection of Specimen domain objects.
	 *
	 * @param specimenRequirementBeanColl the specimen requirement bean coll
	 * @param parentSpecimen the parent specimen
	 * @param cpEvent the cp event
	 *
	 * @return the req specimens
	 */
	public static Collection getReqSpecimens(Collection specimenRequirementBeanColl,
			SpecimenRequirement parentSpecimen, CollectionProtocolEvent cpEvent )
	{
		Collection<SpecimenRequirement> reqSpecimenCollection = new LinkedHashSet<SpecimenRequirement>();
		Iterator iterator = specimenRequirementBeanColl.iterator();
		while(iterator.hasNext())
		{
			SpecimenRequirementBean specimenRequirementBean =
				(SpecimenRequirementBean)iterator.next();
			SpecimenRequirement reqSpecimen = getSpecimenDomainObject(specimenRequirementBean);
			reqSpecimen.setParentSpecimen(parentSpecimen);
			if (parentSpecimen == null)
			{
					SpecimenCharacteristics specimenCharacteristics =
							new SpecimenCharacteristics();
					long identifier =specimenRequirementBean.getSpecimenCharsId();
					if(identifier != -1)
					{
						specimenCharacteristics.setId(Long.valueOf(identifier));
					}
					specimenCharacteristics.setTissueSide(
							specimenRequirementBean.getTissueSide());
					specimenCharacteristics.setTissueSite(
							specimenRequirementBean.getTissueSite());
					reqSpecimen.setCollectionProtocolEvent(cpEvent);
					reqSpecimen.setSpecimenCharacteristics(specimenCharacteristics);
					//Collected and received events
					if(reqSpecimen.getId()==null)
					{
						setSpecimenEvents(reqSpecimen, specimenRequirementBean);
					}
			}
			else
			{
				reqSpecimen.setSpecimenCharacteristics(
						parentSpecimen.getSpecimenCharacteristics());
				//bug no. 7489
				//Collected and received events
				if(specimenRequirementBean.getCollectionEventContainer()!=null && specimenRequirementBean.getReceivedEventReceivedQuality()!=null)
				{
					if(reqSpecimen.getId()==null)
					{
						setSpecimenEvents(reqSpecimen, specimenRequirementBean);
					}
				}
				else
				{
					reqSpecimen.setSpecimenEventCollection(
							parentSpecimen.getSpecimenEventCollection());
				}
			}
			reqSpecimen.setLineage(specimenRequirementBean.getLineage());
			reqSpecimenCollection.add(reqSpecimen);
			Map aliquotColl = (LinkedHashMap)specimenRequirementBean.getAliquotSpecimenCollection();
			Collection childSpecimens = new HashSet();
			if(aliquotColl!=null && !aliquotColl.isEmpty())
			{
				Collection aliquotCollection= specimenRequirementBean.getAliquotSpecimenCollection().values();
				childSpecimens  =
					getReqSpecimens(aliquotCollection, reqSpecimen, cpEvent);
				reqSpecimenCollection.addAll(childSpecimens);
			}
			Map drivedColl = (LinkedHashMap)specimenRequirementBean.getDeriveSpecimenCollection();
			if(drivedColl!=null && !drivedColl.isEmpty())
			{
				Collection derivedCollection= specimenRequirementBean.getDeriveSpecimenCollection().values();

				Collection derivedSpecimens =
					getReqSpecimens(derivedCollection, reqSpecimen, cpEvent);
				if(childSpecimens == null || childSpecimens.isEmpty())
				{
					childSpecimens = derivedSpecimens;
				}
				else
				{
					childSpecimens.addAll(derivedSpecimens);
				}
				reqSpecimenCollection.addAll(childSpecimens);
			}
			reqSpecimen.setChildSpecimenCollection(childSpecimens);
		}
		return reqSpecimenCollection;
	}


	/**
	 * Sets the specimen events.
	 *
	 * @param reqSpecimen the req specimen
	 * @param specimenRequirementBean the specimen requirement bean
	 */
	private static  void setSpecimenEvents(SpecimenRequirement reqSpecimen, SpecimenRequirementBean specimenRequirementBean)
	{
		//seting collection event values
		Collection<SpecimenEventParameters> specimenEventCollection =
			new LinkedHashSet<SpecimenEventParameters>();

		if(specimenRequirementBean.getCollectionEventContainer()!=null)
		{
			CollectionEventParameters collectionEvent = new CollectionEventParameters();
			collectionEvent.setCollectionProcedure(specimenRequirementBean.getCollectionEventCollectionProcedure());
			collectionEvent.setContainer(specimenRequirementBean.getCollectionEventContainer());
			User collectionEventUser = new User();
			collectionEventUser.setId(Long.valueOf(specimenRequirementBean.getCollectionEventUserId()));
			collectionEvent.setUser(collectionEventUser);
			collectionEvent.setSpecimen(reqSpecimen);
			specimenEventCollection.add(collectionEvent);
		}

		//setting received event values

		if(specimenRequirementBean.getReceivedEventReceivedQuality()!=null)
		{
			ReceivedEventParameters receivedEvent = new ReceivedEventParameters();
			receivedEvent.setReceivedQuality(specimenRequirementBean.getReceivedEventReceivedQuality());
			User receivedEventUser = new User();
			receivedEventUser.setId(Long.valueOf(specimenRequirementBean.getReceivedEventUserId()));
			receivedEvent.setUser(receivedEventUser);
			receivedEvent.setSpecimen(reqSpecimen);
			specimenEventCollection.add(receivedEvent);
		}

		reqSpecimen.setSpecimenEventCollection(specimenEventCollection);

	}

	/**
	 * creates specimen domain object from given specimen requirement bean.
	 *
	 * @param specimenRequirementBean the specimen requirement bean
	 *
	 * @return the specimen domain object
	 */
	private static SpecimenRequirement getSpecimenDomainObject(SpecimenRequirementBean specimenRequirementBean)
	{
		NewSpecimenForm form = new NewSpecimenForm();
		form.setClassName(specimenRequirementBean.getClassName());
		SpecimenRequirement reqSpecimen=null;
		try
		{
			if(form.getClassName().equals("Tissue"))
        	{
				reqSpecimen = new TissueSpecimenRequirement();
        	}
        	else if(form.getClassName().equals("Fluid"))
        	{
        		reqSpecimen = new FluidSpecimenRequirement();
        	}
        	else if(form.getClassName().equals("Cell"))
        	{
        		reqSpecimen = new CellSpecimenRequirement();
        	}
        	else if(form.getClassName().equals("Molecular"))
        	{
        		reqSpecimen = new MolecularSpecimenRequirement();
        	}
		}
		catch (Exception e1)
		{
			CollectionProtocolUtil.LOGGER.error("Error in setting Section " +
					"header Priorities"+e1.getMessage(),e1);
			return null;
		}

		if (specimenRequirementBean.getId()==-1)
		{
			reqSpecimen.setId(null);
		}
		else
		{
			reqSpecimen.setId(Long.valueOf(specimenRequirementBean.getId()));
		}
		reqSpecimen.setActivityStatus(Status.ACTIVITY_STATUS_ACTIVE.toString());
		reqSpecimen.setInitialQuantity(new Double(specimenRequirementBean.getQuantity()));
		reqSpecimen.setLineage(specimenRequirementBean.getLineage());
		reqSpecimen.setPathologicalStatus(specimenRequirementBean.getPathologicalStatus());
		reqSpecimen.setSpecimenType(specimenRequirementBean.getType());
		String storageType = specimenRequirementBean.getStorageContainerForSpecimen();
		if(specimenRequirementBean.getClassName().equalsIgnoreCase(Constants.MOLECULAR))
		{
			((MolecularSpecimenRequirement)reqSpecimen).setConcentrationInMicrogramPerMicroliter(new Double(specimenRequirementBean.getConcentration()));
		}
		reqSpecimen.setStorageType(storageType);
		reqSpecimen.setSpecimenClass(specimenRequirementBean.getClassName());
		reqSpecimen.setLabelFormat(specimenRequirementBean.getLabelFormat());
		return reqSpecimen;
	}

	/**
	 * Gets the collection protocol for scg.
	 *
	 * @param identifier the identifier
	 *
	 * @return the collection protocol for scg
	 *
	 * @throws ApplicationException the application exception
	 */
	public static CollectionProtocol getCollectionProtocolForSCG(String identifier) throws ApplicationException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		CollectionProtocolBizLogic collectionProtocolBizLogic = (CollectionProtocolBizLogic)factory.getBizLogic(Constants.COLLECTION_PROTOCOL_FORM_ID);
		String sourceObjectName =  SpecimenCollectionGroup.class.getName();
		String[] whereColName = {"id"};
		String[] whereColCond = {"="};
		Object[] whereColVal = {Long.parseLong(identifier)};
		String [] selectColumnName = {"collectionProtocolRegistration.collectionProtocol"};
		List list = collectionProtocolBizLogic.retrieve(sourceObjectName,selectColumnName,whereColName,whereColCond,whereColVal,Constants.AND_JOIN_CONDITION);
		CollectionProtocol returnVal=null;
		if(list != null && !list.isEmpty())
		{
			CollectionProtocol collectionProtocol = (CollectionProtocol) list.get(0);
			returnVal= collectionProtocol;

		}
		return returnVal ;
	}
	//bug 8905
	/**
	 * This method is used to sort consents according to id.
	 *
	 * @param consentsMap the consents map
	 *
	 * @return sorted map
	 */
	public static Map sortConsentMap(Map consentsMap)
	{
		Set keys = consentsMap.keySet();
		List<String> idList = new ArrayList<String>();
		Iterator iterator = keys.iterator();
		while(iterator.hasNext())
		{
			String key = (String)iterator.next();
			idList.add(key);
		}
		Collections.sort(idList,new IdComparator());
		Map idMap = new LinkedHashMap();
		Iterator idIterator = idList.iterator();
		while(idIterator.hasNext())
		{
			String identifier = (String)idIterator.next();
			idMap.put(identifier,consentsMap.get(identifier));
		}
		return idMap;
	}

	/**
	 * Update the Clinical Diagnosis value.
	 *
	 * @param request request
	 * @param collectionProtocolBean collectionProtocolBean
	 */
	public static  void updateClinicalDiagnosis(HttpServletRequest request,
			final CollectionProtocolBean collectionProtocolBean)
	{
		if(collectionProtocolBean != null)
		{
			Collection<NameValueBean> clinicalDiagnosisBean = new LinkedHashSet<NameValueBean>();
			Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();

			Object[] clinicalDiagnosis =  collectionProtocolBean.getClinicalDiagnosis();
			if(clinicalDiagnosis != null)
			{
				for (int i=0 ;i<clinicalDiagnosis.length;i++)
				{
					NameValueBean nvb = new NameValueBean(clinicalDiagnosis[i],clinicalDiagnosis[i]) ;

					nvb.getName().toLowerCase(locale);

					clinicalDiagnosisBean.add(nvb);

				}
			}
			Collection<NameValueBean> clinicalDiagnosisBeans = new ArrayList<NameValueBean>();
			clinicalDiagnosisBeans.addAll(clinicalDiagnosisBean);
			request.setAttribute(edu.common.dynamicextensions.ui.util.Constants.SELECTED_VALUES, clinicalDiagnosisBeans);
		}

	}

	/**
	 * returns the errors.
	 *
	 * @param request gives the error key.
	 *
	 * @return errors.
	 */
	public static ActionErrors getActionErrors(HttpServletRequest request)
	{
		ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
		if (errors == null)
		{
			errors = new ActionErrors();
		}
		return errors;
	}

	/**
	 * This method will return collectionProtocolId.
	 *
	 * @param specimenId - specimen id
	 * @param dao - DAO obj
	 *
	 * @return collectionProtocolId - collectionProtocolId
	 *
	 * @throws BizLogicException - BizLogicException
	 */
    public static String getCPIdFromSpecimen(String specimenId,DAO dao) throws BizLogicException
    {
    	String collectionProtocolId = "";
		if (specimenId != null && !specimenId.trim().equals(""))
		{
			final Specimen specimen = new Specimen();
			specimen.setId(Long.parseLong(specimenId));
			try
			{
				final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
				final NewSpecimenBizLogic newSpecimenBizLogic = (NewSpecimenBizLogic) factory
				.getBizLogic(edu.wustl.catissuecore.util.global.Constants.NEW_SPECIMEN_FORM_ID);
				collectionProtocolId = newSpecimenBizLogic.getObjectId(dao, specimen);
				if (collectionProtocolId != null && !collectionProtocolId.trim().equals(""))
				{
					collectionProtocolId = collectionProtocolId.split("_")[1];
				}
			}
			catch (final ApplicationException appEx)
			{
				collectionProtocolId = "";
				throw new BizLogicException(appEx.getErrorKey(), appEx, appEx.getMsgValues());
			}
		}
		return collectionProtocolId;
    }

}
