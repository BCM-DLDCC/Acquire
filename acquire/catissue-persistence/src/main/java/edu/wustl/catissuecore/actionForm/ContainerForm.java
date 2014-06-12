/*
 * <p>Title: ContainerForm Class </p>
 * <p>Description:This class initializes the fields of container form which is super class of all
 * forms which have relationship with container domain objects. 
 *  </p>
 * Copyright: Copyright (c) year 2006
 * Company: Washington University, School of Medicine, St. Louis.
 * @version 1.1
 * Created on August 29,2006
 */

package edu.wustl.catissuecore.actionForm;

import edu.wustl.catissuecore.domain.Container;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * <p>This class initializes the fields of ContainerForm</p>
 * @author Ashwin Gupta
 * @version 1.1
 */
public class ContainerForm extends AbstractActionForm
{

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Specify the name field 
	 */
	protected String name;

	/**
	 * Specify the barcode field 
	 */
	protected String barcode;

	/**
	 * Specify the activityStatus field 
	 */
	protected String activityStatus = "Active";

	/**
	 * Specify the positionDimensionOne field 
	 */
	protected int positionDimensionOne;

	/**
	 * Specify the positionDimensionTwo field 
	 */
	protected int positionDimensionTwo;

	/**
	 * Specify the positionInStorageContainer field 
	 */
	protected String positionInStorageContainer;

	/**
	 * Specify the isFull field 
	 */
	protected String isFull = "False";

	/**
	 * Specify the oneDimensionCapacity field 
	 */
	protected int oneDimensionCapacity;

	/**
	 * Specify the twoDimensionCapacity field 
	 */
	protected int twoDimensionCapacity;

	/**
	 * Specify the comment field 
	 */
	private String comment;

	/** 
	 * @see edu.wustl.common.actionForm.AbstractActionForm#getFormId()
	 * @return 0
	 */
	@Override
	public int getFormId()
	{
		return 0;
	}

	/** 
	 * @see edu.wustl.common.actionForm.AbstractActionForm#setAllValues(edu.wustl.common.domain.AbstractDomainObject)
	 * @param domainObject An object of Container
	 */
	public void setAllValues(AbstractDomainObject domainObject)
	{
		if (domainObject instanceof Container)
		{
			final Container container = (Container) domainObject;
			this.setId(container.getId().longValue());
			this.barcode = container.getBarcode();
			this.activityStatus = container.getActivityStatus();
			if (container.getFull() != null)
			{
				this.isFull = String.valueOf(container.getFull().booleanValue());
			}
			if (container != null && container.getLocatedAtPosition() != null)
			{
				this.positionDimensionOne = container.getLocatedAtPosition()
						.getPositionDimensionOne().intValue();
				this.positionDimensionTwo = container.getLocatedAtPosition()
						.getPositionDimensionTwo().intValue();
			}
			this.comment = container.getComment();
			this.name = container.getName();

			if (container.getCapacity().getOneDimensionCapacity() != null)
			{
				this.oneDimensionCapacity = container.getCapacity().getOneDimensionCapacity()
						.intValue();
			}
			if (container.getCapacity().getTwoDimensionCapacity() != null)
			{
				this.twoDimensionCapacity = container.getCapacity().getTwoDimensionCapacity()
						.intValue();
			}

		}
	}

	/**
	 * @see edu.wustl.common.actionForm.AbstractActionForm#reset()
	 */
	@Override
	protected void reset()
	{
	}

	/**
	 * @return Returns the activityStatus.
	 */
	@Override
	public String getActivityStatus()
	{
		return this.activityStatus;
	}

	/**
	 * @param activityStatus The activityStatus to set.
	 */
	@Override
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
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
	 * @return Returns the comment.
	 */
	public String getComment()
	{
		return this.comment;
	}

	/**
	 * @param comment The comment to set.
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
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
	 * @return Returns the name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
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
		this.positionDimensionTwo = positionDimensionTwo;
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
	 * @return Returns the positionInStorageContainer.
	 */
	public String getPositionInStorageContainer()
	{
		return this.positionInStorageContainer;
	}

	/**
	 * @param positionInStorageContainer The positionInStorageContainer to set.
	 */
	public void setPositionInStorageContainer(String positionInStorageContainer)
	{
		this.positionInStorageContainer = positionInStorageContainer;
	}

	@Override
	public void setAddNewObjectIdentifier(String arg0, Long arg1)
	{
		// TODO Auto-generated method stub

	}

}
