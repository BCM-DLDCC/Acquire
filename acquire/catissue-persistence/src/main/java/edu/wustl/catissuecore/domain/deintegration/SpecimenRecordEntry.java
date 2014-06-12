/**
 *
 */

package edu.wustl.catissuecore.domain.deintegration;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.dynamicextensions.domain.integration.AbstractRecordEntry;
import edu.wustl.common.exception.AssignDataException;

/**
 * @author deepali_ahirrao
 * @hibernate.class table="CATISSUE_SPECIMEN_REC_NTRY"
 */
@Entity
@Table(name="CATISSUE_SPECIMEN_REC_NTRY")
public class SpecimenRecordEntry extends AbstractRecordEntry

{

	/*
	 *
	 */
	private static final long serialVersionUID = 1234567890L;
	/**
	 *
	 */
	protected Specimen specimen;

	@ManyToOne
	@JoinColumn(name="SPECIMEN_ID")
	public Specimen getSpecimen()
	{
		return specimen;
	}

	public void setSpecimen(Specimen specimen)
	{
		this.specimen = specimen;
	}

	@Override
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{
		// TODO Auto-generated method stub

	}

}
