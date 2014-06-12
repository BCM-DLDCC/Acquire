package edu.wustl.patientLookUp.util;


/**
 * @author geeta_jaggal
 * This is the exception class which will catch all the exceptions thrown by the algorithm
 */
public class PatientLookupException extends Exception
{

	/**serialVersionUID.*/
	private static final long serialVersionUID = 1L;

	/**
	 * @param message : Exception message
	 * @param cause : Cause for the exception
	 */
	public PatientLookupException(String message,Throwable cause)
	{
		super(message, cause);
	}
}
