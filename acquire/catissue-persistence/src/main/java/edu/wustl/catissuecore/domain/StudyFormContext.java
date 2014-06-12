/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

package edu.wustl.catissuecore.domain;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.dynamicextensions.domain.integration.AbstractFormContext;
import edu.wustl.common.exception.AssignDataException;

/**
 *
 * @author shital_lawhale
 * @hibernate.class table="CATISSUE_STUDY_FORM_CONTEXT"
 */
@Entity
@Table(name="CATISSUE_STUDY_FORM_CONTEXT")
public class StudyFormContext extends AbstractFormContext
{

	private static final long serialVersionUID = 1L;

	protected Integer noOfEntries;

	protected Collection<CollectionProtocol> collectionProtocolCollection = new HashSet<CollectionProtocol>();

	@ManyToMany
	@JoinTable(name="CATISSUE_CP_STUDYFORMCONTEXT",
	joinColumns=@JoinColumn(name="COLLECTION_PROTOCOL_ID"),
	inverseJoinColumns=@JoinColumn(name="STUDY_FORM_CONTEXT_ID")
	)
	public Collection<CollectionProtocol> getCollectionProtocolCollection()
	{
		return collectionProtocolCollection;
	}

	public void setCollectionProtocolCollection(
			Collection<CollectionProtocol> collectionProtocolCollection)
	{
		this.collectionProtocolCollection = collectionProtocolCollection;
	}

	@Column(name="NO_OF_ENTRIES")
	public Integer getNoOfEntries()
	{
		return noOfEntries;
	}

	public void setNoOfEntries(Integer noOfEntries)
	{
		this.noOfEntries = noOfEntries;
	}

	/**
	 *
	 */
	public void setAllValues(IValueObject arg0) throws AssignDataException
	{

	}

}
