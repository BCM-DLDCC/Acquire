/**
 *
 */

package edu.wustl.catissuecore.domain.deintegration;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.dynamicextensions.domain.integration.AbstractRecordEntry;
import edu.wustl.common.exception.AssignDataException;

/**
 * @author deepali_ahirrao
 * @hibernate.class table="CATISSUE_PARTICIPANT_REC_NTRY"
 */
@Entity
@Table(name="CATISSUE_PARTICIPANT_REC_NTRY")
public class ParticipantRecordEntry extends AbstractRecordEntry
{

	/*
	 *
	 */
	private static final long serialVersionUID = 1234567890L;

	/**
	 *
	 */
	@ManyToOne
	@JoinColumn(name="PARTICIPANT_ID")
	protected Participant participant;

	public Participant getParticipant()
	{
		return participant;
	}

	public void setParticipant(Participant participant)
	{
		this.participant = participant;
	}

	@Override
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{
		// TODO Auto-generated method stub

	}

}
