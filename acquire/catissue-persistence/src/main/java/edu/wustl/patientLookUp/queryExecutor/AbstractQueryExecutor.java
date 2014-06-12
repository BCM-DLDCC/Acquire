
package edu.wustl.patientLookUp.queryExecutor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.patientLookUp.domain.PatientInformation;
import edu.wustl.patientLookUp.util.Logger;
import edu.wustl.patientLookUp.util.PatientLookupException;

/**
 * This call will provide the implementation of the generic database related methods.
 * @author geeta_jaggal
 */
public abstract class AbstractQueryExecutor implements IQueryExecutor
{

	protected Connection connection = null;
	protected JDBCDAO jdbcDAO = null;
	protected static IQueryExecutor xQueryExecutorImpl = null;
//	protected String empiDBUrl;
//	protected String empiDBUser;
//	protected String empiDBPassword;
//	protected String empiDBdriver;
//	protected String empiDBSchema;

	/* (non-Javadoc)
	 * @see edu.wustl.patientLookUp.queryExecutor.IQueryExecutor#setDBParameteres(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setDBParameteres(String dbURL, String dbUser, String dbPassword, String dbDriver,
			String dbSchema) throws PatientLookupException
	{
//		empiDBUrl = dbURL;
//		empiDBUser = dbUser;
//		empiDBPassword = dbPassword;
//		empiDBdriver = dbDriver;
//		empiDBSchema = dbSchema;
//		QueryGenerator.setDBSchema(empiDBSchema);
	}

	/**
	 * This method will provide the implementation for opening the database connection.
	 * @throws PatientLookupException : PatientLookupException
	 */
	protected void openConnection() throws PatientLookupException
	{
		try
		{
//			Class.forName(empiDBdriver);
//			connection = DriverManager.getConnection(empiDBUrl, empiDBUser, empiDBPassword);

		}
		catch (Exception e)
		{
			Logger.out.info(e.getMessage(), e);
			Logger.out.info("--------------- ERROR WHILE GETTING THE CIDER DATABASE CONNECTION .\n ---------- ");
			Logger.out.info("--------------- SPECIFY THE CORRECT DB VALUES IN clinportalInstall.properties -------------- .\n");

			throw new PatientLookupException(e.getMessage(), e);
		}
	}

	/**
	 * @return returns the connection object
	 * @throws PatientLookupException
	 */
	protected Connection getConnection() throws PatientLookupException
	{
		try
		{
			if (connection == null || connection.isClosed())
			{
				openConnection();
			}
		}
		catch (Exception e)
		{
			Logger.out.info(e.getMessage(), e);
			Logger.out.info("Error in getting the database connetion");
			throw new PatientLookupException(e.getMessage(), e);
		}
		return connection;
	}

	/**
	 * This method will close database connection
	 * @throws PatientLookupException : PatientLookupException
	 */
	public void closeConnection() throws PatientLookupException
	{
		try
		{
			if (connection == null || connection.isClosed())
			{
				connection.close();
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			Logger.out.debug(e.getMessage(), e);
			Logger.out.debug("Error in closing the database connetion");
			throw new PatientLookupException(e.getMessage(), e);
		}
	}
}
