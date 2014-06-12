
package edu.wustl.catissuecore.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author deepti_phadnis
 *
 */
public class DefineArrayForm extends AbstractActionForm
{

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * A String containing name of Array
	 */
	private String arrayName;

	/**
	 * A String containing type of array
	 */
	private String arraytype;

	/**
	 * A String containing dimensionX of the array container
	 */
	private String dimenmsionX = "";

	/**
	 * A String containing dimensionY of the array container
	 */
	private String dimenmsionY = "";

	/**
	 * A String containing class of array
	 */
	private String arrayClass;

	/**
	 * A String containing name of arrayType
	 */
	private String arrayTypeName;

	/**
	 * @return arrayTypeName
	 */
	public String getArrayTypeName()
	{
		return this.arrayTypeName;
	}

	/**
	 * @param arrayTypeName String containing the name of array type
	 */
	public void setArrayTypeName(String arrayTypeName)
	{
		this.arrayTypeName = arrayTypeName;
	}

	/**
	 * @param abstractDomain AbstractDomainObject
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
	}

	/**
	 * @return 0
	 */
	@Override
	public int getFormId()
	{
		return 0;
	}

	/**
	 * Resets the values of all the fields.
	 * Is called by the overridden reset method defined in ActionForm.  
	 * */
	@Override
	protected void reset()
	{
		this.arrayName = null;
		this.arraytype = null;
		this.dimenmsionX = null;
		this.dimenmsionY = null;
		this.arrayClass = null;
		this.arrayTypeName = null;
	}

	/**
	* Overrides the validate method of ActionForm.
	* @param mapping ActionMapping
	* @param request HttpServletRequest
	* @return ActionErrors 
	*/
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		final ActionErrors errors = new ActionErrors();
		if ((this.arrayName == null) || (this.arrayName.length() == 0))
		{
			errors.add("orderRequestName", new ActionError("errors.arrayname.required"));
		}
		if ((this.arraytype == null) || (this.arraytype.equals("-1")))
		{
			errors.add("arraytype", new ActionError("errors.arraytype.required"));
		}
		return errors;
	}

	/**
	 * @return arrayName
	 */
	public String getArrayName()
	{
		return this.arrayName;
	}

	/**
	 * @param arrayName String containing the name of array
	 */
	public void setArrayName(String arrayName)
	{
		this.arrayName = arrayName;
	}

	/**
	 * @return arraytype 
	 */
	public String getArraytype()
	{
		return this.arraytype;
	}

	/**
	 * @param arraytype String containing the array type
	 */
	public void setArraytype(String arraytype)
	{
		this.arraytype = arraytype;
	}

	/**
	 * @return dimenmsionX 
	 */
	public String getDimenmsionX()
	{
		return this.dimenmsionX;
	}

	/**
	 * @param dimenmsionX String containing the dimensionX of the array
	 */
	public void setDimenmsionX(String dimenmsionX)
	{
		this.dimenmsionX = dimenmsionX;
	}

	/**
	 * @return dimenmsionY
	 */
	public String getDimenmsionY()
	{
		return this.dimenmsionY;
	}

	/**
	 * @param dimenmsionY String containing the dimensionY of the array
	 */
	public void setDimenmsionY(String dimenmsionY)
	{
		this.dimenmsionY = dimenmsionY;
	}

	/**
	 * @return String 
	 */
	public String getArrayClass()
	{
		return this.arrayClass;
	}

	/**
	 * @param arrayClass String containing the class of array
	 */
	public void setArrayClass(String arrayClass)
	{
		this.arrayClass = arrayClass;
	}

	@Override
	public void setAddNewObjectIdentifier(String arg0, Long arg1)
	{
		// TODO Auto-generated method stub

	}
}
