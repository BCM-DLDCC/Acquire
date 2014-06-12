
package edu.wustl.common.participant.utility;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import edu.wustl.patientLookUp.util.Logger;
import edu.wustl.patientLookUp.util.PatientLookupException;

public class PropertyHandler
{

	private static Properties participantManagerProperties = null;;

	/**
	 * Load the property file.
	 * @throws ParticipantManagerException :ParticipantManagerException
	 */
	public static void init(String path) throws ParticipantManagerException
	{
		try
		{
			participantManagerProperties = new Properties();

			InputStream iStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(path);
			if (iStream != null)
			{
				participantManagerProperties.load(iStream);
			}
			else
			{
				throw new FileNotFoundException("participantManager.properties file not Found");
			}
			/*
						path = System.getProperty("patientMatchingPropertyFile");
						if (path != null)
						{
							InputStream inpurStream = new FileInputStream(new File(path));
							if (inpurStream != null)
							{
								participantManagerProperties.load(inpurStream);
							}
							else
							{
								throw new FileNotFoundException(
										"participantManager.properties file not Found");
							}
						}
						*/
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			Logger.out.info("Error while loading the property  files  \n ");
			throw new ParticipantManagerException(e.getMessage(), e);
		}

	}

	/**
	 * @param propertyName : propertyName
	 * @return property value for the propertyName
	 * @throws PatientLookupException :PatientLookupException
	 */
	public static String getValue(String propertyName) throws ParticipantManagerException
	{
		String value = null;
		try
		{
			if (participantManagerProperties == null)
			{
				init("participantManager.properties");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new ParticipantManagerException(e.getMessage(), e);

		}
		if (propertyName != null)
		{
			value = (String) participantManagerProperties.getProperty(propertyName);
		}
		return value;
	}

}
