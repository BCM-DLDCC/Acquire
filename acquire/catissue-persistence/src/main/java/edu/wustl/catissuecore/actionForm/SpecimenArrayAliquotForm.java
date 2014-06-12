/*
 * Created on Sep 21, 2006
 */

package edu.wustl.catissuecore.actionForm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Validator;

/**
 * This Class is used to encapsulate all the request parameters passed from SpecimenArrayAliquots.jsp page.
 * @author jitendra_agrawal
 */
public class SpecimenArrayAliquotForm extends AbstractActionForm
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5307623948521795045L;

	/**
	 * Label of the ParentSpecimenArray
	 */
	private String parentSpecimenArrayLabel = "";

	/**
	* Barcode of the ParentSpecimenArray.
	*/
	private String barcode = "";

	/**
	 * A number that tells how many aliquots to be created.
	 */
	private String aliquotCount = "";

	/**
	* Initial quantity per aliquot.
	*/
	private String quantityPerAliquot = "1";

	/**
	 * Radio button to choose barcode/label of parentSpecimen
	 */
	private String checkedButton = "1";

	/**
	 * Submit/Create Button Clicked
	 */
	private String buttonClicked = "";

	/**
	 * specimenClass of the ParentSpecimenArray.
	 */
	private String specimenClass;

	/**
	 * specimenType of of the ParentSpecimenArray.
	 */
	private String[] specimenTypes;

	/**
	 * specimenArrayType of the ParentSpecimenArray.
	 */
	private String specimenArrayType;

	/**
	 * specimenArrayId of the ParentSpecimenArray
	 */
	private String specimenArrayId;

	/**
	 * A map that contains distinguished fields (barcode,location) per aliquot.
	 */
	private Map specimenArrayAliquotMap = new HashMap();

	/**
	 * Returns the identifier assigned to form bean.
	 * @return The identifier assigned to form bean.
	 */
	@Override
	public int getFormId()
	{
		return Constants.SPECIMEN_ARRAY_ALIQUOT_FORM_ID;
	}

	/**
	 * This method resets the form fields.
	 */
	@Override
	protected void reset()
	{

	}

	/**
	 * This method Copies the data from an SpecimenArrayAliquot object to a SpecimenArrayAliquotForm object.
	 * @param arg0 An object of Specimen class.  
	 */
	public void setAllValues(AbstractDomainObject arg0)
	{

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
		final ActionErrors errors = new ActionErrors();
		final Validator validator = new Validator();

		if (this.checkedButton.equals("1"))
		{
			if (Validator.isEmpty(this.parentSpecimenArrayLabel))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("specimenArrayAliquots.parentLabel")));
			}
		}
		else
		{
			if (this.barcode == null || this.barcode.trim().length() == 0)
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("specimenArrayAliquots.barcode")));
			}
		}

		this.aliquotCount = AppUtility.isValidCount(this.aliquotCount, errors);
		if (!validator.isNumeric(this.aliquotCount))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.format",
					ApplicationProperties.getValue("specimenArrayAliquots.noOfAliquots")));
		}

		if (request.getParameter(Constants.PAGE_OF).equals(
				Constants.PAGE_OF_SPECIMEN_ARRAY_ALIQUOT_SUMMARY))
		{
			final Iterator keyIterator = this.specimenArrayAliquotMap.keySet().iterator();
			while (keyIterator.hasNext())
			{
				final String key = (String) keyIterator.next();
				if (key.endsWith("_label"))
				{
					final String value = (String) this.specimenArrayAliquotMap.get(key);

					if (Validator.isEmpty(value))
					{
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
								"errors.item.required", ApplicationProperties
										.getValue("specimenArrayAliquots.label")));
					}
				}
				else if (key.indexOf("_positionDimension") != -1)
				{
					final String value = (String) this.specimenArrayAliquotMap.get(key);
					if (value != null && !value.trim().equals("") && !validator.isDouble(value))
					{
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.format",
								ApplicationProperties
										.getValue("specimen.positionInStorageContainer")));
						break;
					}
				}
			}
		}

		return errors;
	}

	/**
	 * @return Returns the aliquotCount.
	 */
	public String getAliquotCount()
	{
		return this.aliquotCount;
	}

	/**
	 * @param aliquotCount The aliquotCount to set.
	 */
	public void setAliquotCount(String aliquotCount)
	{
		this.aliquotCount = aliquotCount;
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
	 * @return Returns the checkedButton.
	 */
	public String getCheckedButton()
	{
		return this.checkedButton;
	}

	/**
	 * @param checkedButton The checkedButton to set.
	 */
	public void setCheckedButton(String checkedButton)
	{
		this.checkedButton = checkedButton;
	}

	/**
	 * @return Returns the parentSpecimenArrayLabel.
	 */
	public String getParentSpecimenArrayLabel()
	{
		return this.parentSpecimenArrayLabel;
	}

	/**
	 * @param parentSpecimenArrayLabel The parentSpecimenArrayLabel to set.
	 */
	public void setParentSpecimenArrayLabel(String parentSpecimenArrayLabel)
	{
		this.parentSpecimenArrayLabel = parentSpecimenArrayLabel;
	}

	/**
	 * @return Returns the quantityPerAliquot.
	 */
	public String getQuantityPerAliquot()
	{
		return this.quantityPerAliquot;
	}

	/**
	 * @param quantityPerAliquot The quantityPerAliquot to set.
	 */
	public void setQuantityPerAliquot(String quantityPerAliquot)
	{
		this.quantityPerAliquot = quantityPerAliquot;
	}

	/**
	 * @return Returns the buttonClicked.
	 */
	public String getButtonClicked()
	{
		return this.buttonClicked;
	}

	/**
	 * @param buttonClicked The buttonClicked to set.
	 */
	public void setButtonClicked(String buttonClicked)
	{
		this.buttonClicked = buttonClicked;
	}

	/**
	 * @return Returns the specimenArrayType.
	 */
	public String getSpecimenArrayType()
	{
		return this.specimenArrayType;
	}

	/**
	 * @param specimenArrayType The specimenArrayType to set.
	 */
	public void setSpecimenArrayType(String specimenArrayType)
	{
		this.specimenArrayType = specimenArrayType;
	}

	/**
	 * @return Returns the specimenClass.
	 */
	public String getSpecimenClass()
	{
		return this.specimenClass;
	}

	/**
	 * @param specimenClass The specimenClass to set.
	 */
	public void setSpecimenClass(String specimenClass)
	{
		this.specimenClass = specimenClass;
	}

	/**
	 * @return Returns the specimenType.
	 */
	public String[] getSpecimenTypes()
	{
		return this.specimenTypes;
	}

	/**
	 * @param specimenTypes The specimenType to set.
	 */
	public void setSpecimenTypes(String[] specimenTypes)
	{
		this.specimenTypes = specimenTypes;
	}

	/**
	 * @return Returns the specimenArrayAliquotMap.
	 */
	public Map getSpecimenArrayAliquotMap()
	{
		return this.specimenArrayAliquotMap;
	}

	/**
	 * @param specimenArrayAliquotMap The specimenArrayAliquotMap to set.
	 */
	public void setSpecimenArrayAliquotMap(Map specimenArrayAliquotMap)
	{
		this.specimenArrayAliquotMap = specimenArrayAliquotMap;
	}

	/**
	 * Associates the specified object with the specified key in the map.
	 * @param key the key to which the object is mapped.
	 * @param value the object which is to be mapped.
	 */
	public void setValue(String key, Object value)
	{
		this.specimenArrayAliquotMap.put(key, value);
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * @param key the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getValue(String key)
	{
		return this.specimenArrayAliquotMap.get(key);
	}

	/**
	 * @return Returns the specimenArrayId.
	 */
	public String getSpecimenArrayId()
	{
		return this.specimenArrayId;
	}

	/**
	 * @param specimenArrayId The specimenArrayId to set.
	 */
	public void setSpecimenArrayId(String specimenArrayId)
	{
		this.specimenArrayId = specimenArrayId;
	}

	@Override
	public void setAddNewObjectIdentifier(String arg0, Long arg1)
	{
		// TODO Auto-generated method stub

	}
}
