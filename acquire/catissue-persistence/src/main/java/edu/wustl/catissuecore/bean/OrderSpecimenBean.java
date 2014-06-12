
package edu.wustl.catissuecore.bean;

import java.io.Serializable;

/**
 * @author deepti_phadnis
 *
 */
public class OrderSpecimenBean implements Serializable
{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * String containg specimenId.
	 */
	private String specimenId;

	/**
	 * String containing specimen name.
	 */
	private String specimenName;

	/**
	 * String containing requested quantity.
	 */
	private String requestedQuantity;

	/**
	 * String containing available quantity.
	 */
	private String availableQuantity;

	/**
	 * String containing description.
	 */
	private String description;

	/**
	 * String containing unit of requested quantity.
	 */
	private String unitRequestedQuantity;

	/**
	 * String containing whether specimen is derived.
	 */
	private String isDerived;

	/**
	 * String containing whether item is to be removed.
	 */
	private String checkedToRemove = "off";

	/**
	 * String containing type of specimen.
	 */
	private String specimenType;

	/**
	 * String containing class of specimen.
	 */
	private String specimenClass;

	/**
	 * String containing pathological status os the item.
	 */
	private String pathologicalStatus;

	/**
	 * String containing tissue site of item.
	 */
	private String tissueSite;

	/**
	 * String containing specimenCollectionGroup.
	 */
	private String specimenCollectionGroup;

	/**
	 * This indicates whether item to be added is an Array ,Specimen or
	 * Pathalogical case.
	 */

	private String typeOfItem;

	/**
	 * This indicates whether biospecimens are to be added to Array or to
	 * OrderList.
	 */
	private String arrayName;

	/**
	 * This indicates biospecimens Distribution Site.
	 *
	 */
	private String distributionSite;

	/**
	 * This indicates SPR Collection Protocol.
	 *
	 */
	private String collectionProtocol;
	/**
	 * This indicates biospecimens collection status.
	 *
	 */
	private String collectionStatus;

	/**
	 * This checks the availablity of specimen.
	 *
	 */
	private String isAvailable;

	/**
	 * @return isDerived-whether specimen is existing or derived.
	 */
	public String getIsDerived()
	{
		return this.isDerived;
	}

	/**
	 * @param isDerived String containing whether item is derived or existing.
	 */
	public void setIsDerived(String isDerived)
	{
		this.isDerived = isDerived;
	}

	/**
	 * @return unitRequestedQuantity-unit of requested quantity.
	 */
	public String getUnitRequestedQuantity()
	{
		return this.unitRequestedQuantity;
	}

	/**
	 * @param unitRequestedQuantity String containing the unit of requested quantity.
	 */
	public void setUnitRequestedQuantity(String unitRequestedQuantity)
	{
		this.unitRequestedQuantity = unitRequestedQuantity;
	}

	/**
	 * @return availableQuantity.
	 */
	public String getAvailableQuantity()
	{
		return this.availableQuantity;
	}

	/**
	 * @param availableQuantity String containing the available quantity.
	 */
	public void setAvailableQuantity(String availableQuantity)
	{
		this.availableQuantity = availableQuantity;
	}

	/**
	 * @return description
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @param description String containing the description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return requestedQuantity.
	 */
	public String getRequestedQuantity()
	{
		return this.requestedQuantity;
	}

	/**
	 * @param requestedQuantity String containing the requested Quantity.
	 */
	public void setRequestedQuantity(String requestedQuantity)
	{
		this.requestedQuantity = requestedQuantity;
	}

	/**
	 * @return specimenName
	 */
	public String getSpecimenName()
	{
		return this.specimenName;
	}

	/**
	 * @param specimenName String containing the specimen name
	 */
	public void setSpecimenName(String specimenName)
	{
		this.specimenName = specimenName;
	}

	/**
	 * @return specimenId
	 */
	public String getSpecimenId()
	{
		return this.specimenId;
	}

	/**
	 * @param specimenId String containing the specimen id
	 */
	public void setSpecimenId(String specimenId)
	{
		this.specimenId = specimenId;
	}

	/**
	 * @return checkedToRemove
	 */
	public String getCheckedToRemove()
	{
		return this.checkedToRemove;
	}

	/**
	 * @param checkedToRemove String containing if the item is to be removed
	 */
	public void setCheckedToRemove(String checkedToRemove)
	{
		this.checkedToRemove = checkedToRemove;
	}

	/**
	 * @return specimenClass
	 */
	public String getSpecimenClass()
	{
		return this.specimenClass;
	}

	/**
	 * @param specimenClass String containing if the class of specimen
	 */
	public void setSpecimenClass(String specimenClass)
	{
		this.specimenClass = specimenClass;
	}

	/**
	 * @return specimenType
	 */
	public String getSpecimenType()
	{
		return this.specimenType;
	}

	/**
	 * @param specimenType String containing if the type of specimen
	 */
	public void setSpecimenType(String specimenType)
	{
		this.specimenType = specimenType;
	}

	/**
	 * @return arrayName
	 */
	public String getArrayName()
	{
		return this.arrayName;
	}

	/**
	 *
	 * @param arrayName String containing name of array
	 */
	public void setArrayName(String arrayName)
	{
		this.arrayName = arrayName;
	}

	/**
	 * @return typeOfItem
	 */
	public String getTypeOfItem()
	{
		return this.typeOfItem;
	}

	/**
	 * @param typeOfItem String containing type of item whether it is specimen,array or pathological case
	 */
	public void setTypeOfItem(String typeOfItem)
	{
		this.typeOfItem = typeOfItem;
	}

	/**
	 * @return pathologicalStatus
	 */
	public String getPathologicalStatus()
	{
		return this.pathologicalStatus;
	}

	/**
	 * @param pathologicalStatus String containing pathological status
	 */
	public void setPathologicalStatus(String pathologicalStatus)
	{
		this.pathologicalStatus = pathologicalStatus;
	}

	/**
	 * @return tissueSite
	 */
	public String getTissueSite()
	{
		return this.tissueSite;
	}

	/**
	 * @param tissueSite String containing tissue site
	 */
	public void setTissueSite(String tissueSite)
	{
		this.tissueSite = tissueSite;
	}

	/**
	 * @return specimenCollectionGroup
	 */
	public String getSpecimenCollectionGroup()
	{
		return this.specimenCollectionGroup;
	}

	/**
	 * @param specimenCollectionGroup String containing the specimenCollectionGroup
	 */
	public void setSpecimenCollectionGroup(String specimenCollectionGroup)
	{
		this.specimenCollectionGroup = specimenCollectionGroup;
	}
	/**
	 *
	 * @return distributionSite distributionSite
	 */
	public String getDistributionSite()
	{
		return this.distributionSite;
	}

	/**
	 *
	 * @param distributionSite distributionSite
	 */
	public void setDistributionSite(String distributionSite)
	{
		this.distributionSite = distributionSite;
	}
	/**
	 *
	 * @return collectionStatus
	 */
	public String getCollectionStatus()
	{
		return this.collectionStatus;
	}
	/**
	 *
	 * @param collectionStatus collectionStatus
	 */
	public void setCollectionStatus(String collectionStatus)
	{
		this.collectionStatus = collectionStatus;
	}
	/**
	 *
	 * @return isAvailable
	 */
	public String getIsAvailable()
	{
		return this.isAvailable;
	}
	/**
	 *
	 * @param isAvailable isAvailable
	 */
	public void setIsAvailable(String isAvailable)
	{
		this.isAvailable = isAvailable;
	}
	/**
	 *
	 * @return collectionProtocol
	 */
	public String getCollectionProtocol()
	{
		return this.collectionProtocol;
	}
	/**
	 *
	 * @param collectionProtocol collectionProtocol
	 */
	public void setCollectionProtocol(String collectionProtocol)
	{
		this.collectionProtocol = collectionProtocol;
	}

}
