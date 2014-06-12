
package edu.wustl.common.dynamicextensions.domain.integration;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import edu.wustl.common.domain.AbstractDomainObject;

/**
 * Host application must have a concrete implementation for this abstract class
 * @author deepali_ahirrao
 * @hibernate.class table="DYEXTN_ABSTRACT_FORM_CONTEXT"
 */
@Entity
@Table(name="DYEXTN_ABSTRACT_FORM_CONTEXT")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class AbstractFormContext extends AbstractDomainObject
{

	/**
	 * Serial Version Unique Identifier
	 */
	protected static final long serialVersionUID = 1235468709L;

	protected Long id;
	protected String formLabel;
	protected Long containerId;
	protected String activityStatus;
	protected Collection<AbstractRecordEntry> recordEntryCollection = new HashSet<AbstractRecordEntry>();
	protected Boolean hideForm;

	/**
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="DYEXTN_ABSTRACT_FRM_CTXT_SEQ"
	 */
	@Id
	@GeneratedValue(generator="formContextGenerator")
	@SequenceGenerator(name="formContextGenerator", sequenceName="DYEXTN_ABSTRACT_FRM_CTXT_SEQ")
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
	 * @return Returns the formLabel.
	 *  @hibernate.property name="formLabel" column="FORM_LABEL" type="string" length="255"
	 */
	@Column(name="FORM_LABEL")
	public String getFormLabel()
	{
		return formLabel;
	}

	public void setFormLabel(String formLabel)
	{
		this.formLabel = formLabel;
	}

	/**
	 * @return Returns the containerId.
	 *  @hibernate.property name="containerId" column="CONTAINER_ID" type="long" length="30"
	 */
	@Column(name="CONTAINER_ID")
	public Long getContainerId()
	{
		return containerId;
	}

	public void setContainerId(Long containerId)
	{
		this.containerId = containerId;
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
	 * @return Returns the recordEntryCollection.
	 * @hibernate.set name="recordEntryCollection" table="DYEXTN_ABSTRACT_RECORD_ENTRY" cascade="save-update"
	 * inverse="true" lazy="false"
	 * @hibernate.collection-key column="ABSTRACT_FORM_CONTEXT_ID"
	 * @hibernate.collection-one-to-many class="edu.common.dynamicextensions.domain.integration.AbstractRecordEntry"
	 * @return
	 */
	@OneToMany(mappedBy="formContext", cascade={CascadeType.PERSIST, CascadeType.MERGE})
	public Collection<AbstractRecordEntry> getRecordEntryCollection()
	{
		return recordEntryCollection;
	}

	public void setRecordEntryCollection(Collection<AbstractRecordEntry> recordEntryColn)
	{
		this.recordEntryCollection = recordEntryColn;
	}

	/**
	 * Returns true if to hide form.
	 * @hibernate.property name="hideForm" type="boolean" column="HIDE_FORM"
	 */
	@Column(name="HIDE_FORM")
	public Boolean getHideForm()
	{
		return hideForm;
	}

	public void setHideForm(Boolean hideForm)
	{
		this.hideForm = hideForm;
	}

}
