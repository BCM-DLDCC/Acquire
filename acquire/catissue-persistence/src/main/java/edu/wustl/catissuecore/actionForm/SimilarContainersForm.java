/*
 * Created on Jul 3, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package edu.wustl.catissuecore.actionForm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * @author chetan_bh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimilarContainersForm extends AbstractActionForm
{

	private static final long serialVersionUID = 1L;

	/**
	 * logger Logger - Generic logger.
	 */
	private static Logger logger = Logger.getCommonLogger(SimilarContainersForm.class);

	private String storageContainerId;

	private String noOfContainers;

	private String storageContainerType;

	private String siteName;

	private String defaultTemperature;

	private String collectionProtocolTitle;

	private String holds;

	/**
	 * Text label for dimension one.
	 */
	private String oneDimensionLabel;

	/**
	 * Text label for dimension two.
	 */
	private String twoDimensionLabel;

	/**
	 * An id which refers to Parent Container of this container.
	 */
	private long parentContainerId;

	/** 
	 * Positon for dimension 1
	 */
	private int positionDimensionOne;

	/**
	 * Position for dimension 2
	 */
	private int positionDimensionTwo;

	/**
	 * collectionIds contains Ids of collection Protocols that this container can hold
	 */
	protected long[] collectionIds = new long[]{-1};

	/**
	 * holdStorageTypeIds contains Ids of Storage Types that this container can hold
	 */
	protected long[] holdsStorageTypeIds;

	/**
	 * holdSpecimenClassTypeIds contains Ids of Specimen Types that this container can hold
	 */
	protected long[] holdsSpecimenClassTypeIds;

	/**
	 * holdSpecimenTypeIds contains Ids of Specimen Types that this container can hold
	 */
	protected long[] holdsSpecimenTypeIds;

	/**
	 * Tells whether this container is full or not.
	 */
	private String isFull = "False";

	/**
	 * An id which refers to the type of the storage.
	 */
	private long typeId = -1;

	private long siteId = -1;

	/**
	 * Radio button to choose site/parentContainer.
	 */
	private int checkedButton = 1;

	/**
	 * Capacity in dimension one.
	 */
	private int oneDimensionCapacity;

	/**
	 * Capacity in dimension two.
	 */
	private int twoDimensionCapacity = 1;

	private String barcode;

	private String containerName;

	/**
	 * A map that contains distinguished fields (container name,barcode,parent location) per container.
	 */
	private Map similarContainersMap = new HashMap();

	/**
	 * Returns the map that contains distinguished fields per aliquots.
	 * @return The map that contains distinguished fields per aliquots.
	 * @see #setAliquotMap(Map)
	 */
	public Map getSimilarContainersMap()
	{
		//System.out.println("AliquotForm : getAliquotMap "+similarContainersMap);
		return this.similarContainersMap;
	}

	/**
	 * Sets the map of distinguished fields of aliquots.
	 * @param similarContainersMap A map of distinguished fields of aliquots.
	 * @see #getAliquotMap()
	 */
	public void setSimilarContainersMap(Map similarContainersMap)
	{
		//System.out.println("AliquotForm : setAliquotMap "+similarContainersMap);
		this.similarContainersMap = similarContainersMap;
	}

	/**
	 * Associates the specified object with the specified key in the map.
	 * @param key the key to which the object is mapped.
	 * @param value the object which is to be mapped.
	 */
	public void setValue(String key, Object value)
	{
		//System.out.println("simCont: setValue -> "+key+" "+value);
		this.similarContainersMap.put(key, value);
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * @param key the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getValue(String key)
	{
		//System.out.println("simCont: getValue <- "+key+" "+similarContainersMap.get(key));
		return this.similarContainersMap.get(key);
	}

	/**
	 * @return Returns the storageContainerType.
	 */
	public String getStorageContainerType()
	{
		return this.storageContainerType;
	}

	/**
	 * @param storageContainerType The storageContainerType to set.
	 */
	public void setStorageContainerType(String storageContainerType)
	{
		this.storageContainerType = storageContainerType;
	}

	/**
	 * @return SIMILAR_CONTAINERS_FORM_ID
	 */
	@Override
	public int getFormId()
	{
		// TODO Auto-generated method stub
		return Constants.SIMILAR_CONTAINERS_FORM_ID;
	}

	/**
	 * @see edu.wustl.common.actionForm.AbstractActionForm#setAllValues(edu.wustl.common.domain.AbstractDomainObject)
	 * @param abstractDomain An AbstractDomain Object
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Resets the values of all the fields.
	 */
	@Override
	protected void reset()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @return Returns the noOfContainers.
	 */
	public String getNoOfContainers()
	{
		return this.noOfContainers;
	}

	/**
	 * @param noOfContainers The noOfContainers to set.
	 */
	public void setNoOfContainers(String noOfContainers)
	{
		this.noOfContainers = noOfContainers;

		//System.out.println("noOfContainers "+noOfContainers);
	}

	/**
	 * @return Returns the storageContainerId.
	 */
	public String getStorageContainerId()
	{
		return this.storageContainerId;
	}

	/**
	 * @param storageContainerId The storageContainerId to set.
	 */
	public void setStorageContainerId(String storageContainerId)
	{
		this.storageContainerId = storageContainerId;
	}

	/**
	 * @return Returns the defaultTemperature.
	 */
	public String getDefaultTemperature()
	{
		return this.defaultTemperature;
	}

	/**
	 * @param defaultTemperature The defaultTemperature to set.
	 */
	public void setDefaultTemperature(String defaultTemperature)
	{
		this.defaultTemperature = defaultTemperature;
	}

	/**
	 * @return Returns the oneDimensionCapacity.
	 */
	public int getOneDimensionCapacity()
	{
		return this.oneDimensionCapacity;
	}

	/**
	 * @param oneDimensionCapacity The oneDimensionCapacity to set.
	 */
	public void setOneDimensionCapacity(int oneDimensionCapacity)
	{
		this.oneDimensionCapacity = oneDimensionCapacity;
	}

	/**
	 * @return Returns the twoDimensionCapacity.
	 */
	public int getTwoDimensionCapacity()
	{
		return this.twoDimensionCapacity;
	}

	/**
	 * @param twoDimensionCapacity The twoDimensionCapacity to set.
	 */
	public void setTwoDimensionCapacity(int twoDimensionCapacity)
	{
		this.twoDimensionCapacity = twoDimensionCapacity;
	}

	/**
	 * @return Returns the typeId.
	 */
	public long getTypeId()
	{
		return this.typeId;
	}

	/**
	 * @param typeId The typeId to set.
	 */
	public void setTypeId(long typeId)
	{
		this.typeId = typeId;

	}

	/**
	 * @return Returns the siteId.
	 */
	public long getSiteId()
	{
		return this.siteId;
	}

	/**
	 * @param siteId The siteId to set.
	 */
	public void setSiteId(long siteId)
	{
		this.siteId = siteId;
	}

	/**
	 * @return Returns the siteName.
	 */
	public String getSiteName()
	{
		return this.siteName;
	}

	/**
	 * @param siteName The siteName to set.
	 */
	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	/**
	 * @return Returns the collectionProtocolTitle.
	 */
	public String getCollectionProtocolTitle()
	{
		return this.collectionProtocolTitle;
	}

	/**
	 * @param collectionProtocolTitle The collectionProtocolTitle to set.
	 */
	public void setCollectionProtocolTitle(String collectionProtocolTitle)
	{
		this.collectionProtocolTitle = collectionProtocolTitle;
	}

	/**
	 * @return Returns the holds.
	 */
	public String getHolds()
	{
		return this.holds;
	}

	/**
	 * @param holds The holds to set.
	 */
	public void setHolds(String holds)
	{
		this.holds = holds;
	}

	/**
	 * getitng collection Ids that this container can hold
	 * @return collection Id's array
	 */
	public long[] getCollectionIds()
	{
		return this.collectionIds;
	}

	/**
	 * setitng the Collection Id array
	 * @param collectionIds - array of collection Ids to set
	 */
	public void setCollectionIds(long[] collectionIds)
	{
		this.collectionIds = collectionIds;
	}

	/**
	 * getitng StorageType Id's that this container can hold
	 * @return StorageType Id' array
	 */
	public long[] getHoldsStorageTypeIds()
	{
		return this.holdsStorageTypeIds;
	}

	/**
	 * setting the StorageType Id array
	 * @param holdsStorageTypeIds - array of StorageType id's to set
	 */
	public void setHoldsStorageTypeIds(long[] holdsStorageTypeIds)
	{
		this.holdsStorageTypeIds = holdsStorageTypeIds;
	}

	/**
	 * 
	 * @return holdsSpecimenTypeIds
	 */
	public long[] getHoldsSpecimenTypeIds()
	{
		return holdsSpecimenTypeIds;
	}
	
	/**
	 * Setting the SpecimenType.
	 * @param holdsSpecimenTypeIds array of Specimen Type
	 */
	public void setHoldsSpecimenTypeIds(long[] holdsSpecimenTypeIds)
	{
		this.holdsSpecimenTypeIds = holdsSpecimenTypeIds;
	}

	/**
	 * @return Returns the checkedButton.
	 */
	public int getCheckedButton()
	{
		return this.checkedButton;
	}

	/**
	 * @param checkedButton The checkedButton to set.
	 */
	public void setCheckedButton(int checkedButton)
	{
		logger.debug("setCheckedButton ** " + checkedButton);
		this.checkedButton = checkedButton;
	}

	/**
	 * @return Returns the isFull.
	 */
	public String getIsFull()
	{
		return this.isFull;
	}

	/**
	 * @param isFull The isFull to set.
	 */
	public void setIsFull(String isFull)
	{
		this.isFull = isFull;
	}

	/**
	 * Returns the label of dimension one.
	 * @return String the label of dimension one.
	 * @see #setOneDimensionLabel(String)
	 */
	public String getOneDimensionLabel()
	{
		return this.oneDimensionLabel;
	}

	/**
	 * Sets the label of dimension one.
	 * @param oneDimensionLabel the label of dimension one to be set.
	 * @see #getOneDimensionLabel()
	 */
	public void setOneDimensionLabel(String oneDimensionLabel)
	{
		this.oneDimensionLabel = oneDimensionLabel;
	}

	/**
	 * Returns the label of dimension two.
	 * @return String the label of dimension two.
	 * @see #setTwoDimensionLabel(String)
	 */
	public String getTwoDimensionLabel()
	{
		return this.twoDimensionLabel;
	}

	/**
	 * Sets the label of dimension two.
	 * @param twoDimensionLabel the label of dimension two to be set.
	 * @see #getTwoDimensionLabel()
	 */
	public void setTwoDimensionLabel(String twoDimensionLabel)
	{
		this.twoDimensionLabel = twoDimensionLabel;
	}

	/**
	 * @return Returns the holdsSpecimenClassTypeIds.
	 */
	public long[] getHoldsSpecimenClassTypeIds()
	{
		return this.holdsSpecimenClassTypeIds;
	}

	/**
	 * @param holdsSpecimenClassTypeIds The holdsSpecimenClassTypeIds to set.
	 */
	public void setHoldsSpecimenClassTypeIds(long[] holdsSpecimenClassTypeIds)
	{
		this.holdsSpecimenClassTypeIds = holdsSpecimenClassTypeIds;
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @return error ActionErrors instance
	 * @param mapping Actionmapping instance
	 * @param request HttpServletRequest instance
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		logger.debug("SimilarContainersForm :: validate()");
		final ActionErrors errors = new ActionErrors();
		final Validator validator = new Validator();

		try
		{
			if (this.typeId == -1)
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("storageContainer.type")));
			}
			final int noOfCont = Integer.parseInt(this.noOfContainers);
			logger.debug("simMap ^^--?? " + this.similarContainersMap);
			for (int i = 1; i <= noOfCont; i++)
			{
				final String iBarcode = (String) this.getValue("simCont:" + i + "_barcode"); //simCont:1_barcode
				if (iBarcode != null && iBarcode.equals("")) // this is done because barcode is empty string set by struts
				{ // but barcode in DB is unique but can be null.
					this.setValue("simCont:" + i + "_barcode", null);
				}
				final int checkedButtonStatus = Integer.parseInt((String) this
						.getValue("checkedButton"));
				final String siteId = (String) this.getValue("simCont:" + i + "_siteId");
				if (checkedButtonStatus == 2 || siteId == null)
				{
					final String parentContId = (String) this.getValue("simCont:" + i
							+ "_parentContainerId");
					final String positionDimensionOne = (String) this.getValue("simCont:" + i
							+ "_positionDimensionOne");
					final String positionDimensionTwo = (String) this.getValue("simCont:" + i
							+ "_positionDimensionTwo");
					logger.debug(i + " parentContId " + parentContId + " positionDimensionOne "
							+ positionDimensionOne + " positionDimensionTwo "
							+ positionDimensionOne);
					if (parentContId.equals("-1") || positionDimensionOne.equals("-1")
							|| positionDimensionTwo.equals("-1"))
					{
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
								"errors.item.required", ApplicationProperties
										.getValue("similarcontainers.location")));
						this.setValue("checkedButton", "2");
					}
				}
				else
				{
					//String siteId = (String)getValue("simCont:"+i+"_siteId");
					//System.out.println("site Id *** "+siteId);
					if (siteId.equals("-1"))
					{
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
								"errors.item.required", ApplicationProperties
										.getValue("storageContainer.site")));
					}
				}
			}

			//			VALIDATIONS FOR DIMENSION 1.
			if (Validator.isEmpty(String.valueOf(this.oneDimensionCapacity)))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("storageContainer.oneDimension")));
			}
			else
			{
				if (!validator.isNumeric(String.valueOf(this.oneDimensionCapacity)))
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.format",
							ApplicationProperties.getValue("storageContainer.oneDimension")));
				}
			}

			//Validations for dimension 2
			if (!Validator.isEmpty(String.valueOf(this.twoDimensionCapacity))
					&& (!validator.isNumeric(String.valueOf(this.twoDimensionCapacity))))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.format",
						ApplicationProperties.getValue("storageContainer.twoDimension")));
			}

			//			validations for temperature
			if (!Validator.isEmpty(this.defaultTemperature)
					&& (!validator.isDouble(this.defaultTemperature, false)))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.format",
						ApplicationProperties.getValue("storageContainer.temperature")));
			}

		}
		catch (final Exception excp)
		{
			SimilarContainersForm.logger.error(excp.getMessage(), excp);
			excp.printStackTrace();
		}
		return errors;
	}

	/**
	 * @return Returns the parentContainerId.
	 */
	public long getParentContainerId()
	{
		return this.parentContainerId;
	}

	/**
	 * @param parentContainerId The parentContainerId to set.
	 */
	public void setParentContainerId(long parentContainerId)
	{
		logger.debug("calling ... parentContainerId " + parentContainerId);
		this.parentContainerId = parentContainerId;
	}

	/**
	 * @return Returns the positionDimensionOne.
	 */
	public int getPositionDimensionOne()
	{
		return this.positionDimensionOne;
	}

	/**
	 * @param positionDimensionOne The positionDimensionOne to set.
	 */
	public void setPositionDimensionOne(int positionDimensionOne)
	{
		logger.debug("calling ... positionDimensionOne " + positionDimensionOne);
		this.positionDimensionOne = positionDimensionOne;
	}

	/**
	 * @return Returns the positionDimensionTwo.
	 */
	public int getPositionDimensionTwo()
	{
		return this.positionDimensionTwo;
	}

	/**
	 * @param positionDimensionTwo The positionDimensionTwo to set.
	 */
	public void setPositionDimensionTwo(int positionDimensionTwo)
	{
		logger.debug("calling ... positionDimensionTwo " + positionDimensionTwo);
		this.positionDimensionTwo = positionDimensionTwo;
	}

	/**
	 * @return Returns the barcode.
	 */
	public String getBarcode()
	{
		return this.barcode;
	}

	/**
	 * @param barcode The barcode to set.
	 */
	public void setBarcode(String barcode)
	{
		this.barcode = barcode;
	}

	/**
	 * @return Returns the containerName.
	 */
	public String getContainerName()
	{
		return this.containerName;
	}

	/**
	 * @param containerName The containerName to set.
	 */
	public void setContainerName(String containerName)
	{
		this.containerName = containerName;
	}

	@Override
	public void setAddNewObjectIdentifier(String arg0, Long arg1)
	{
		// TODO Auto-generated method stub

	}
}
