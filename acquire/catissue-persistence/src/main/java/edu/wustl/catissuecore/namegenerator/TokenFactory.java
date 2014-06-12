package edu.wustl.catissuecore.namegenerator;

import java.util.HashMap;
import java.util.Map;

import edu.wustl.common.util.logger.Logger;


// TODO: Auto-generated Javadoc
/**
 * A factory for creating Token objects.
 */
/**
 * @author nitesh_marwaha
 *
 */
public final class TokenFactory
{

	/** Logger object. */
	private static final Logger LOGGER = Logger.getCommonLogger(LabelGeneratorFactory.class);

	/**
	 * Private constructor.
	 */
	private TokenFactory()
	{

	}

	/** Singleton instance of SpecimenLabelGenerator. */
	private static Map<String, Object> tokenMap = new HashMap<String, Object>();

	/**
	 * Get singleton instance of SpecimenLabelGenerator.
	 * The class name of an instance is picked up from properties file
	 *
	 * @param tokenKey Property key name for specific Object's
	 * Label generator class (eg.specimenLabelGeneratorClass)
	 *
	 * @return LabelGenerator
	 *
	 * @throws NameGeneratorException NameGeneratorException
	 */
	public static LabelTokens getInstance(String tokenKey) throws NameGeneratorException
	{
		try
		{
			LabelTokens labelToken;
			if (tokenMap.get(tokenKey) == null)
			{
				String className = PropertyHandler.getTokenValue(tokenKey);

				if (className == null || "".equals(className))
				{
					throw new TokenNotFoundException("");
				}
				else
				{
					tokenMap.put(tokenKey, Class.forName(className).newInstance());
					labelToken = (LabelTokens)tokenMap.get(tokenKey);
				}
			}
			else
			{
				labelToken = (LabelTokens)tokenMap.get(tokenKey);
			}
			return labelToken;
		}
		catch(final TokenNotFoundException exp)
		{
			LOGGER.info(exp.getMessage(), exp);
			throw new TokenNotFoundException("Could not create LabelGenerator instance: "
					+ exp.getMessage());
		}
		catch (final IllegalAccessException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw new NameGeneratorException("Could not create LabelGenerator instance: "
					+ e.getMessage());
		}
		catch (final InstantiationException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw new NameGeneratorException("Could not create LabelGenerator instance: "
					+ e.getMessage());
		}
		catch (final ClassNotFoundException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw new NameGeneratorException("Could not create LabelGenerator instance: "
					+ e.getMessage());
		}
		catch (final Exception ex)
		{
			LOGGER.error(ex.getMessage(), ex);
			throw new NameGeneratorException(ex.getMessage(), ex);
		}
	}
}
