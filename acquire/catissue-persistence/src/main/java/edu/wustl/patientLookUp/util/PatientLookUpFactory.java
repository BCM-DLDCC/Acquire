
package edu.wustl.patientLookUp.util;

import edu.wustl.patientLookUp.lookUpServiceBizLogic.IPatientLookUp;

/**
 * @author geeta_jaggal
 * This is the factory class to retrieve singleton instance of IPatientLookUp class.
 */
public class PatientLookUpFactory
{

	/**
	 * @return object of class which implements the interface IPatientLookUp
	 * @throws Exception :Exception
	 */
	public static IPatientLookUp getPatientLookupServiceImpl() throws Exception
	{
		final String className = PropertyHandler.getValue(Constants.PATIENT_LOOKUP_SERVICE);

		return (IPatientLookUp) Class.forName(className).newInstance();
	}


}
