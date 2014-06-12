
package edu.wustl.patientLookUp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class will provide the methods to load the property files and
 * get the property value.
 * @author geeta_jaggal
 *
 */
public class PropertyHandler
{

	private static Properties patientLookUpServiceProperties = null;;
	private static Document document = null;

	/**
	 * Load the property file.
	 * @throws PatientLookupException :PatientLookupException
	 */
	public static void init() throws PatientLookupException
	{
		String path = null;
		InputStream iStream = null;
		try
		{
			patientLookUpServiceProperties = new Properties();

			path = System.getProperty("patientMatchingPropertyFile");
			if (path != null)
			{
				InputStream inpurStream = new FileInputStream(new File(path));
			    patientLookUpServiceProperties.load(inpurStream);
			}
			else
			{
				path = "patientLookUpService.properties";
				iStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
				if (iStream != null)
				{
					patientLookUpServiceProperties.load(iStream);
				}
				else
				{
					throw new FileNotFoundException(
							"patientLookUpService.properties file not Found");
				}
			}

			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
			path = "med_lookup_view.xml";
			iStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			if (iStream != null)
			{
				document = dbuilder.parse(iStream);
				populateProperyFile(document);
			}
			else
			{
				throw new FileNotFoundException("med_lookup_view.xml file not Found");
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			Logger.out.info("Error while loading the property  files  \n ");
			throw new PatientLookupException(e.getMessage(), e);
		}

	}

	/**
	 * Populate the properties object with the values in the med_lookup_view.xml file.
	 * @param document :document
	 */
	private static void populateProperyFile(Document document)
	{
		Element root = document.getDocumentElement();
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (child instanceof Element)
			{
				NodeList subChildNodes = child.getChildNodes();
				boolean isNameFound = false;
				String pName = null;

				for (int j = 0; j < subChildNodes.getLength(); j++)
				{
					Node subchildNode = subChildNodes.item(j);
					String subNodeName = subchildNode.getNodeName();
					if (subNodeName.equals("name"))
					{
						pName = (String) subchildNode.getFirstChild().getNodeValue();
					}
					if (subNodeName.equals("value"))
					{
						String pValue = "";
						if (subchildNode != null && subchildNode.getFirstChild() != null)
						{
							pValue = (String) subchildNode.getFirstChild().getNodeValue();
						}
						patientLookUpServiceProperties.put(pName, pValue);
					}
				}
			}
		}

	}

	/**
	 * @param node : node
	 * @return node name 
	 */
	private static String getNodeName(Node node)
	{
		String subNodeName = node.getNodeName();
		String pName = null;
		if (subNodeName.equals("name"))
		{
			pName = (String) node.getFirstChild().getNodeValue();
		}
		return pName;
	}

	/**
	 * @param node :node.
	 * @return node value for the node name
	 */
	private static String getNodeValue(Node node)
	{
		String subNodeName = node.getNodeName();
		String pValue = "";
		if (subNodeName.equals("value"))
		{
			if (node != null && node.getFirstChild() != null)
			{
				pValue = (String) node.getFirstChild().getNodeValue();
			}
		}
		return pValue;
	}

	/**
	 * @param propertyName : propertyName
	 * @return property value for the propertyName
	 * @throws PatientLookupException :PatientLookupException
	 */
	public static String getValue(String propertyName) throws PatientLookupException
	{
		String value = null;
		try
		{
			if (patientLookUpServiceProperties == null)
			{
				init();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new PatientLookupException(e.getMessage(), e);

		}
		if (propertyName != null)
		{
			value = (String) patientLookUpServiceProperties.getProperty(propertyName);
		}
		return value;
	}

}
