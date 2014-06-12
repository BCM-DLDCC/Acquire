package edu.wustl.common.participant.utility;


public class ParticipantManagerException extends Exception
{
	/**serialVersionUID.*/
	private static final long serialVersionUID = 1L;

	/**
	 * @param message : Exception message
	 * @param cause : Cause for the exception
	 */
	public ParticipantManagerException(String message,Throwable cause)
	{
		super(message, cause);
	}
}
