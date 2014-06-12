
package edu.wustl.common.participant.utility;

import java.io.FileNotFoundException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.util.logger.Logger;

/**
 * @author geeta_jaggal.
 * The Class RaceGenderCodesProperyHandler.
 */
public class RaceGenderCodesProperyHandler
{

	/** The race gender codes prop. */
	private static Properties raceGenderCodesProp = null;

	/** The document. */
	private static Document document = null;
	private static final Logger LOGGER = Logger
			.getCommonLogger(RaceGenderCodesProperyHandler.class);

	/**
	 * Inits the.
	 *
	 * @param path the path
	 *
	 * @throws Exception the exception
	 */
	public static void init(final String path) throws ApplicationException
	{

		try
		{
			final java.io.InputStream iStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(path);
			if (iStream != null)
			{
				raceGenderCodesProp = new Properties();
				final DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
				final DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
				document = dbuilder.parse(iStream);
				populateProperyFile(document);
			}
			else
			{
				throw new FileNotFoundException("HL7MesRaceGenderCodes.xml file not Found");
			}
		}
		catch (FileNotFoundException e)
		{
			LOGGER.info(e.getMessage());
			throw new ApplicationException(null, e, " HL7MesRaceGenderCodes.xml fiel not found \n");
		}
		catch (Exception e)
		{
			LOGGER.info(e.getMessage());
			throw new ApplicationException(null, e,
					" Error in initialising RaceGenderCodesProperyHandler class");
		}

	}

	/**
	 * Populate propery file.
	 *
	 * @param document the document
	 */
	private static void populateProperyFile(final Document document)
	{
		final Element root = document.getDocumentElement();
		final NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			final Node child = children.item(i);
			if (!(child instanceof Element))
			{
				continue;
			}
			final NodeList subChildNodes = child.getChildNodes();
			String pName = null;
			for (int j = 0; j < subChildNodes.getLength(); j++)
			{
				final Node subchildNode = subChildNodes.item(j);
				final String subNodeName = subchildNode.getNodeName();
				if ("name".equals(subNodeName))
				{
					pName = subchildNode.getFirstChild().getNodeValue();
				}
				if (!("value".equals(subNodeName)))
				{
					continue;
				}
				String pValue = "";
				if (subchildNode != null && subchildNode.getFirstChild() != null)
				{
					pValue = subchildNode.getFirstChild().getNodeValue();
				}
				raceGenderCodesProp.put(pName, pValue);
			}

		}

	}

	/**
	 * Gets the value.
	 *
	 * @param propertyName the property name
	 *
	 * @return the value
	 *
	 * @throws Exception the exception
	 */
	public static String getValue(final String propertyName) throws ApplicationException
	{
		String value = null;

		if (raceGenderCodesProp == null)
		{
			init("HL7MesRaceGenderCodes.xml");
		}

		if (propertyName != null)
		{
			value = raceGenderCodesProp.getProperty(propertyName);
		}
		return value;
	}

}
