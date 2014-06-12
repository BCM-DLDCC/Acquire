/**
 * <p>Title: ShipmentRequest </p>
 * <p>Description: Shipment request details.</p>
 * Copyright:    Copyright (c) year
 * Company:
 * @author nilesh_ghone
 * @version 1.00
 */

package edu.wustl.catissuecore.domain.shippingtracking;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;

import edu.wustl.catissuecore.actionForm.shippingtracking.BaseShipmentForm;
import edu.wustl.catissuecore.actionForm.shippingtracking.ShipmentRequestForm;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.StorageContainer;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;

/**
 * Shipment details. Shipment contains specimen(s) and/or container(s). Shipment
 * shipped to sites. Shipment has status assigned.
 * @hibernate.joined-subclass table="CATISSUE_SHIPMENT_REQUEST"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */

/**
 * Represents shipment request.
 */
public class ShipmentRequest extends BaseShipment
{
	/**
	 * Logger instance.
	 */
	private final transient Logger logger = Logger.getCommonLogger(ShipmentRequest.class);
	/**
	 * represents the collection ofspecimens.
	 */
	private Collection<Specimen> specimenCollection = new HashSet<Specimen>();

	/**
	 * gets the specimen collection.
	 * @return specimenCollection collection of specimens.
	 */
	public Collection<Specimen> getSpecimenCollection()
	{
		return this.specimenCollection;
	}

	/**
	 * sets the specimen collection.
	 * @param specimenCollectionParam specimen collection to set.
	 */
	public void setSpecimenCollection(Collection<Specimen> specimenCollectionParam)
	{
		this.specimenCollection = specimenCollectionParam;
	}

	/**
	 * gets the message label.
	 * @return msgLabel the message label.
	 */
	@Override
	public String getMessageLabel()
	{
		String msgLabel = "";
		if (this.receiverSite != null && this.receiverSite.getName() != null)
		{
			msgLabel = "site " + this.receiverSite.getName();
		}
		else
		{
			msgLabel = "required sites.";
		}
		return msgLabel;
	}

	/**
	 *  Required field if implements Serializable.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Used to check whether the shipment created for the request.
	 */
	private boolean requestProcessed = false;

	/**
	 * Get whether the shipment created for the request (i.e. Request processed or not).
	 * @return requestProcessed true if shipment created for the request otherwise false.
	 */
	public boolean isRequestProcessed()
	{
		return this.requestProcessed;
	}

	/**
	 * Set the request processed or not.
	 * @param requestProcessedParam true if shipment created for the request otherwise false
	 */
	public void setRequestProcessed(boolean requestProcessedParam)
	{
		this.requestProcessed = requestProcessedParam;
	}

	/**
	 * default constructor.
	 */
	public ShipmentRequest()
	{

	}

	/**
	 * constructor.
	 * @param form to set all values.
	 * @throws AssignDataException if some assigning operation fails.
	 */
	public ShipmentRequest(AbstractActionForm form) throws AssignDataException
	{
		this();
		this.setAllValues(form);
	}

	/**
	 * sets all values to the object.
	 * @param arg0 the object representing the form.
	 * @throws AssignDataException if some assigning operation fails.
	 */
	@Override
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{
		if (arg0 instanceof ShipmentRequestForm)
		{
			final BaseShipmentForm shipmentForm = (ShipmentRequestForm) arg0;
			this.setBasicShipmentRequestProperties(shipmentForm);
			this.setShipmentContents(shipmentForm);
		}
	}

	/**
	 * sets the shipment contents.
	 * @param shipmentForm object of BaseShipmentForm class.
	 */
	@Override
	protected void setShipmentContents(BaseShipmentForm shipmentForm)
	{
		final Collection<StorageContainer> updatedContainerCollection = new HashSet<StorageContainer>();
		//Call to super class's method to set information related to populate container info
		this.populateContainerContents(shipmentForm, updatedContainerCollection);
		if (!shipmentForm.isAddOperation())
		{
			this.containerCollection.clear();
			this.containerCollection.addAll(updatedContainerCollection);
		}
		// Populate the specimenCollection
		this.populateSpecimenCollection(shipmentForm);
	}

	/**
	 * populates specimen collection.
	 * @param shipmentForm form containing all values.
	 */
	private void populateSpecimenCollection(BaseShipmentForm shipmentForm)
	{
		final int specimenCount = shipmentForm.getSpecimenCounter();
		String fieldValue = "";
		Specimen specimen = null;
		this.specimenCollection.clear();
		if (specimenCount > 0)
		{
			for (int specimenCounter = 1; specimenCounter <= specimenCount; specimenCounter++)
			{
				fieldValue = (String) shipmentForm.getSpecimenDetails("specimenLabel_"
						+ specimenCounter);
				if (fieldValue != null && !fieldValue.trim().equals(""))
				{
					specimen = new Specimen();
					if (shipmentForm.getSpecimenLabelChoice().equalsIgnoreCase("SpecimenLabel"))
					{
						specimen.setLabel(fieldValue);
					}
					else if (shipmentForm.getSpecimenLabelChoice().equals("SpecimenBarcode"))
					{
						specimen.setBarcode(fieldValue);
					}
					this.specimenCollection.add(specimen);
				}
			}
		}
	}

	/**
	 * sets the basic shipment request properties.
	 * @param shipmentForm form object containing all values.
	 * @throws AssignDataException if some assignment operation fails.
	 */
	protected void setBasicShipmentRequestProperties(BaseShipmentForm shipmentForm)
			throws AssignDataException
	{
		if (shipmentForm.getId() != 0L)
		{
			this.id = shipmentForm.getId();
		}
		this.senderComments = shipmentForm.getSenderComments();
		this.senderSite = this.createSitObject(shipmentForm.getSenderSiteId());
		this.label = shipmentForm.getLabel();
		if (shipmentForm.getActivityStatus() != null
				&& !shipmentForm.getActivityStatus().trim().equals(""))
		{
			this.activityStatus = shipmentForm.getActivityStatus();
		}
		try
		{
			this.setShipmentDateProperty(shipmentForm);
		}
		catch (final ParseException e)
		{
			this.logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new AssignDataException(ErrorKey.getErrorKey("errors.item"), e, "item missing");
		}
	}
}
