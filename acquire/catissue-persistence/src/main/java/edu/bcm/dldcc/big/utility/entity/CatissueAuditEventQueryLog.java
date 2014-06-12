package edu.bcm.dldcc.big.utility.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the CATISSUE_AUDIT_EVENT_QUERY_LOG database table.
 * 
 */
@Entity
@Table(name="CATISSUE_AUDIT_EVENT_QUERY_LOG")
public class CatissueAuditEventQueryLog implements Serializable {
	private static final long serialVersionUID = 1L;
	private long identifier;
	private BigDecimal countOfRootRecords;
	private BigDecimal ifTempTableDeleted;
	private String queryDetails;
	private BigDecimal queryId;
	private String rootEntityName;
	private String tempTableName;
	private CatissueAuditEvent catissueAuditEvent;

    public CatissueAuditEventQueryLog() {
    }


	@Id
	@SequenceGenerator(name="CATISSUE_AUDIT_EVENT_QUERY_LOG_IDENTIFIER_GENERATOR", sequenceName="CATISSUE_AUDIT_EVENT_QUERY_LOG_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CATISSUE_AUDIT_EVENT_QUERY_LOG_IDENTIFIER_GENERATOR")
	public long getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(long identifier) {
		this.identifier = identifier;
	}


	@Column(name="COUNT_OF_ROOT_RECORDS")
	public BigDecimal getCountOfRootRecords() {
		return this.countOfRootRecords;
	}

	public void setCountOfRootRecords(BigDecimal countOfRootRecords) {
		this.countOfRootRecords = countOfRootRecords;
	}


	@Column(name="IF_TEMP_TABLE_DELETED")
	public BigDecimal getIfTempTableDeleted() {
		return this.ifTempTableDeleted;
	}

	public void setIfTempTableDeleted(BigDecimal ifTempTableDeleted) {
		this.ifTempTableDeleted = ifTempTableDeleted;
	}


    @Lob()
	@Column(name="QUERY_DETAILS")
	public String getQueryDetails() {
		return this.queryDetails;
	}

	public void setQueryDetails(String queryDetails) {
		this.queryDetails = queryDetails;
	}


	@Column(name="QUERY_ID")
	public BigDecimal getQueryId() {
		return this.queryId;
	}

	public void setQueryId(BigDecimal queryId) {
		this.queryId = queryId;
	}


	@Column(name="ROOT_ENTITY_NAME")
	public String getRootEntityName() {
		return this.rootEntityName;
	}

	public void setRootEntityName(String rootEntityName) {
		this.rootEntityName = rootEntityName;
	}


	@Column(name="TEMP_TABLE_NAME")
	public String getTempTableName() {
		return this.tempTableName;
	}

	public void setTempTableName(String tempTableName) {
		this.tempTableName = tempTableName;
	}


	//bi-directional many-to-one association to CatissueAuditEvent
    @ManyToOne
	@JoinColumn(name="AUDIT_EVENT_ID")
	public CatissueAuditEvent getCatissueAuditEvent() {
		return this.catissueAuditEvent;
	}

	public void setCatissueAuditEvent(CatissueAuditEvent catissueAuditEvent) {
		this.catissueAuditEvent = catissueAuditEvent;
	}
	
}