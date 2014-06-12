
package edu.wustl.common.dynamicextensions.domain.integration;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import edu.wustl.common.domain.AbstractDomainObject;

/**
 * Host application must have a concrete implementation for this abstract class
 * @author deepali_ahirrao
 * @hibernate.class table="DYEXTN_ABSTRACT_RECORD_ENTRY"
 */
@Entity
@Table(name="DYEXTN_ABSTRACT_RECORD_ENTRY")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class AbstractRecordEntry extends AbstractDomainObject
{

	/**
	 * Serial Version Unique Identifier
	 */
	protected static final long serialVersionUID = 1235468709L;

	protected Long id;
	protected String activityStatus;
	protected AbstractFormContext formContext;
	protected Date modifiedDate;
	protected String modifiedBy;

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_ABSTRACT_RE_SEQ"
	 */
	@Id
	@GeneratedValue(generator="abstractRecordGenerator")
	@SequenceGenerator(name="abstractRecordGenerator", sequenceName="DYEXTN_ABSTRACT_RE_SEQ")
	@Column(name="IDENTIFIER")
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @return Returns the activityStatus.
	 *  @hibernate.property name="activityStatus" column="ACTIVITY_STATUS" type="string" length="10"
	 */
	@Column(name="ACTIVITY_STATUS")
	public String getActivityStatus()
	{
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 *
	 * @return
	 * @hibernate.many-to-one column="ABSTRACT_FORM_CONTEXT_ID" class="edu.common.dynamicextensions.domain.integration.AbstractFormContext" constrained="true"
	 */
	@ManyToOne
	@JoinColumn(name="ABSTRACT_FORM_CONTEXT_ID")
	public AbstractFormContext getFormContext()
	{
		return formContext;
	}

	public void setFormContext(AbstractFormContext formContext)
	{
		this.formContext = formContext;
	}

	/**
	 * @return Returns the modifiedDate.
	 *  @hibernate.property name="modifiedDate" column="MODIFIED_DATE" type="date"
	 */
	@Temporal(TemporalType.DATE)
	@Column(name="MODIFIED_DATE")
	public Date getModifiedDate()
	{
		return modifiedDate;
	}


	public void setModifiedDate(Date modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return Returns the modifiedBy.
	 *  @hibernate.property name="modifiedBy" column="MODIFIED_BY" type="string" length="255"
	 */
	@Column(name="MODIFIED_BY")
	public String getModifiedBy()
	{
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

}
