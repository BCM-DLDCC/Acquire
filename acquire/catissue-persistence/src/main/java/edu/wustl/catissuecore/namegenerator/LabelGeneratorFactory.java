
package edu.wustl.catissuecore.namegenerator;

import java.util.HashMap;
import java.util.Map;

import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.util.logger.Logger;

/**
 * This is the factory Class to retrieve singleton instance of LabelGenerator.
 * @author Falguni_Sachde
 */
public final class LabelGeneratorFactory
{

	/**
	 * Logger object.
	 */
	private static final Logger LOGGER = Logger.getCommonLogger(LabelGeneratorFactory.class);

	/**
	 * Private constructor.
	 */
	private LabelGeneratorFactory()
	{

	}

	/**
	 * Singleton instance of SpecimenLabelGenerator.
	 */
	private static Map<String, Object> labelgeneratorMap = new HashMap<String, Object>();

	/**
	 * Get singleton instance of SpecimenLabelGenerator.
	 * The class name of an instance is picked up from properties file
	 * @param generatorType Property key name for specific Object's
	 * Label generator class (eg.specimenLabelGeneratorClass)
	 * @return LabelGenerator
	 * @throws NameGeneratorException NameGeneratorException
	 */
	public static LabelGenerator getInstance(String generatorType) throws NameGeneratorException
	{
		try
		{
			if (labelgeneratorMap.get(generatorType) == null)
			{
				String className = PropertyHandler.getValue(generatorType);


				/*if(Constants.SPECIMEN_LABEL_GENERATOR_PROPERTY_NAME.equals(generatorType))
				{
					className="edu.wustl.catissuecore.namegenerator.DefaultSpecimenLabelGenerator";
				}*/

//				if(Constants.CUSTOM_SPECIMEN_LABEL_GENERATOR_PROPERTY_NAME.equals(generatorType))
//				{
//					className="edu.wustl.catissuecore.namegenerator.DefaultTemplateBasedLabelGenerator";
//				}

				if (className != null && !"".equals(className))
				{
					labelgeneratorMap.put(generatorType, Class.forName(className).newInstance());
				}
				else
				{
					return null;
				}
			}
			return (LabelGenerator) labelgeneratorMap.get(generatorType);
		}
		catch (final IllegalAccessException e)
		{
			LabelGeneratorFactory.LOGGER.error(e.getMessage(), e);
			throw new NameGeneratorException("Could not create LabelGenerator instance: "
					+ e.getMessage());
		}
		catch (final InstantiationException e)
		{
			LabelGeneratorFactory.LOGGER.error(e.getMessage(), e);
			throw new NameGeneratorException("Could not create LabelGenerator instance: "
					+ e.getMessage());
		}
		catch (final ClassNotFoundException e)
		{
			LabelGeneratorFactory.LOGGER.error(e.getMessage(), e);
			throw new NameGeneratorException("Could not create LabelGenerator instance: "
					+ e.getMessage());
		}
		catch (final Exception ex)
		{
			LabelGeneratorFactory.LOGGER.error(ex.getMessage(), ex);
			throw new NameGeneratorException(ex.getMessage(), ex);
		}
	}

	public void init()
	{

	}
}
