/**
 * <p>Title: Concept Class>
 * <p>Description:  Concept domain object.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Ashish Gupta
 * @version 1.00
 * Created on March 07,2007
 */

package edu.wustl.catissuecore.domain.pathology;

import java.util.Collection;
import java.util.HashSet;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * @hibernate.class table="CATISSUE_CONCEPT"
 * Represents Concept associated with pathology report.
 */
public class Concept extends AbstractDomainObject
{

	/**
	 *
	 */
	private static final long serialVersionUID = 982000479650093510L;

	/**
	 *
	 */
	protected String conceptUniqueIdentifier;

	/**
	 * System generated unique ID.
	 */
	protected Long id;

	/**
	 * Name of the concept.
	 */
	protected String name;

	/**
	 * semantic type of the concept.
	 */
	protected SemanticType semanticType;

	/**
	 * Concept referent collection.
	 */
	protected Collection conceptReferentCollection = new HashSet();

	/**
	 * Constructor.
	 */
	public Concept()
	{
		super();
	}

	/**
	 * @return collection of concept referent collection.
	 * @hibernate.set cascade="save-update" table="CATISSUE_CONCEPT_REFERENT" lazy="false"
	 * @hibernate.collection-one-to-many class="edu.wustl.catissuecore.domain.pathology.ConceptReferent"
	 * @hibernate.collection-key column="CONCEPT_ID"
	 */
	public Collection getConceptReferentCollection()
	{
		return this.conceptReferentCollection;
	}

	/**
	 * @param conceptReferentCollection sets collection of concept referent
	 */
	public void setConceptReferentCollection(Collection conceptReferentCollection)
	{
		this.conceptReferentCollection = conceptReferentCollection;
	}

	/**
	 * @return concept unique identifier.
	 * @hibernate.property type="string" length="30" column="CONCEPT_UNIQUE_ID"
	 */
	public String getConceptUniqueIdentifier()
	{
		return this.conceptUniqueIdentifier;
	}

	/**
	 * @param conceptUniqueIdentifier sets concepts unique identifier
	 */
	public void setConceptUniqueIdentifier(String conceptUniqueIdentifier)
	{
		this.conceptUniqueIdentifier = conceptUniqueIdentifier;
	}

	/**
	 * @return system generated id for the concept
	 * @hibernate.id type="long" length="30" column="IDENTIFIER" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_CONCEPT_SEQ"
	 */
	@Override
	public Long getId()
	{
		return this.id;
	}

	/**
	 * @param identifier sets system generated id
	 */
	@Override
	public void setId(Long identifier)
	{
		this.id = identifier;
	}

	/**
	 * @return name of the concept
	 * @hibernate.property type="string" length="500" column="NAME"
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name sets name of the concept
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return symantice type for the concept
	 * @hibernate.many-to-one
	 *  class="edu.wustl.catissuecore.domain.pathology.SemanticType"
	 *   column="SEMANTIC_TYPE_ID" cascade="save-update"
	 */
	public SemanticType getSemanticType()
	{
		return this.semanticType;
	}

	/**
	 * @param semanticType sets semantic type
	 */
	public void setSemanticType(SemanticType semanticType)
	{
		this.semanticType = semanticType;
	}

	/**
	 * @param abstractForm : abstractForm
	 * @throws AssignDataException : AssignDataException
	 */
	@Override
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{
		// TODO Auto-generated method stub

	}

}