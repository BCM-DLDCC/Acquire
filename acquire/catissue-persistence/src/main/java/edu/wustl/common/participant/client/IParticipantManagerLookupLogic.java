package edu.wustl.common.participant.client;

import java.util.List;
import java.util.Set;

import edu.wustl.common.lookup.LookupLogic;
import edu.wustl.common.lookup.LookupParameters;
import edu.wustl.common.participant.domain.IParticipant;


public interface IParticipantManagerLookupLogic extends LookupLogic
{
	/**
	 * @param params Lookup Parameters.
	 * @return List
	 * @throws Exception Exception.
	 */
	List lookup(LookupParameters params,Set<Long> csList) throws Exception;

	void initParticipantCache() throws Exception;

	void updatePartiicpantCache(IParticipant participant);
}
