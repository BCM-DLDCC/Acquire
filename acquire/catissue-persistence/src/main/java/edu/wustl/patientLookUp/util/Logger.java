
package edu.wustl.patientLookUp.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;


/**
 * @author geeta_jaggal
 * This class will provides the utility method for participant matching algorithm
 */
public class Logger
{

	/**
	 * default logger object, to be used if logger configuration is not available.
	 */
	public static org.apache.log4j.Logger out = null;
	public static org.apache.log4j.Logger getLogger(Class className)
	{
		try
		{
			if (out == null)
			{
				ConsoleAppender appender = new ConsoleAppender(new PatternLayout());
				out = org.apache.log4j.Logger.getLogger(Logger.class);
			    out.addAppender(appender);
			    out.setLevel((Level) Level.INFO);

			}
		}
		catch (Exception malformedURLEx)
		{
			out.fatal("Logger not configured. Invalid config file log4j.properties");
		}
		return out;
	}
}
