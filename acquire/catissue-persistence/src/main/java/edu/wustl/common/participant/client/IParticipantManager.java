
package edu.wustl.common.participant.client;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.wustl.common.exception.ApplicationException;

// TODO: Auto-generated Javadoc
/**
 * The Interface IParticipantManager.
 *
 * @author geeta_jaggal
 */
public interface IParticipantManager
{

	/**
	 * Gets the pI cordinators first name and last name.
	 *
	 * @return the pI cordinatorsof protocol
	 */
	public String getPICordinatorsofProtocol();

	/**
	 * This method will return all the protocol ids
	 * associated with MISC id.
	 *
	 * This method first fetch the MICS id to which the protocol id is associated with.
	 * And then fetch all the protocol ids associated with the MICS id.
	 *
	 * Suppose  protocolId associated with MICSId and MICSId enabled for participant
	 * match withing mics then this method will return all the protocol ids associated with the MICSid.
	 *
	 * @param cpId the cp id
	 *
	 * @return the associted mutli inst protocol id list
	 *
	 * @throws ApplicationException the application exception
	 */
	public Set<Long> getProtocolIdLstForMICSEnabledForMatching(Long protocolId)
			throws ApplicationException;

	/**
	 * Gets the last name query.
	 *
	 * @param protocolIdSet the protocol id set
	 *
	 * @return the last name query
	 */
	public String getLastNameQuery(Set<Long> protocolIdSet);

	/**
	 * Gets the meta phone code query.
	 *
	 * @param protocolIdSet the protocol id set
	 *
	 * @return the meta phone code query
	 */
	public String getMetaPhoneCodeQuery(Set<Long> protocolIdSet);

	/**
	 * Gets the mRN query.
	 *
	 * @param protocolIdSet the protocol id set
	 *
	 * @return the mRN query
	 */
	public String getMRNQuery(Set<Long> protocolIdSet);

	/**
	 * Gets the sSN query.
	 *
	 * @param protocolIdSet the protocol id set
	 *
	 * @return the sSN query
	 */
	public String getSSNQuery(Set<Long> protocolIdSet);

	/**
	 * Fetch the PI and co-ordinators ids.
	 * @param participantId
	 * @return
	 * @throws ApplicationException
	 */
	public LinkedHashSet<Long> getParticipantPICordinators(long participantId)
			throws ApplicationException;

}
