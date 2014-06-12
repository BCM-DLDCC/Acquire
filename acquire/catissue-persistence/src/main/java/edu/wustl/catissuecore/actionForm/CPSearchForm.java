
package edu.wustl.catissuecore.actionForm;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author renuka_bajpai
 *
 */
public class CPSearchForm extends AbstractActionForm
{

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;
	Long cpId = null;
	Long participantId = null;

	/**
	 * Getting Participant id
	 * @return participantId
	 */
	public Long getParticipantId()
	{
		return this.participantId;
	}

	/**
	 * @param participantId Setting Participant ID
	 */
	public void setParticipantId(Long participantId)
	{
		this.participantId = participantId;
	}

	/**
	 * Returns the identifier assigned to form bean.
	 * @return The identifier assigned to form bean.
	 */
	@Override
	public int getFormId()
	{
		return 0;
	}

	/**
	 * This method Copies the data from an Specimen object to a AliquotForm object.
	 * @param abstractDomain An object of Specimen class.  
	 */
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
	}

	/**
	 * This method resets the form fields.
	 */
	@Override
	public void reset()
	{
	}

	/**
	 * getting CP Id
	 * @return cpId
	 */
	public Long getCpId()
	{
		return this.cpId;
	}

	/**
	 * @param cpId Setting CP Id
	 */
	public void setCpId(Long cpId)
	{
		this.cpId = cpId;
	}

	@Override
	public void setAddNewObjectIdentifier(String arg0, Long arg1)
	{
		// TODO Auto-generated method stub

	}
}
