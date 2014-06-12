
package edu.wustl.catissuecore.namegenerator;

import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.catissuecore.util.global.Variables;
import edu.wustl.common.util.logger.Logger;

/**
 * This class initialize  label and bar code generator depending upon configuration.
 * @author Falguni_Sachde
 *
 */
public final class LabelAndBarcodeGeneratorInitializer
{

	/**
	 * Logger object.
	 */
	private static final Logger LOGGER = Logger
			.getCommonLogger(LabelAndBarcodeGeneratorInitializer.class);

	/**
	 * private constructor.
	 */
	private LabelAndBarcodeGeneratorInitializer()
	{

	}

	/**
	 * This method reads configuration file and set the conditions
	 * whether automatic label ,bar code generation configured or not.
	 *
	 */
	public static void init()
	{
		try
		{
			setSpcimenLabelBarcodeGentorInstances();
			setStorageContainerGeneratorInstance();
		}
		catch (final Exception e)
		{
			LabelAndBarcodeGeneratorInitializer.LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * This method set Storage Container GeneratorInstance.
	 * @throws NameGeneratorException Name Generator Exception.
	 */
	private static void setStorageContainerGeneratorInstance() throws NameGeneratorException
	{
		LabelGenerator sCGeneratorInstance;

		BarcodeGenerator sCBarcodeGeneratorInstance;
		sCGeneratorInstance = LabelGeneratorFactory
				.getInstance(Constants.STORAGECONTAINER_LABEL_GENERATOR_PROPERTY_NAME);
		if (sCGeneratorInstance != null)
		{
			Variables.isStorageContainerLabelGeneratorAvl = true;
		}

		sCBarcodeGeneratorInstance = BarcodeGeneratorFactory
				.getInstance(Constants.STORAGECONTAINER_BARCODE_GENERATOR_PROPERTY_NAME);
		if (sCBarcodeGeneratorInstance != null)
		{
			Variables.isStorageContainerBarcodeGeneratorAvl = true;
		}
	}

	/**
	 * This method set Specimen Label BarcodeGentor Instances.
	 * @throws NameGeneratorException Name Generator Exception.
	 */
	private static void setSpcimenLabelBarcodeGentorInstances() throws NameGeneratorException
	{
		LabelGenerator specimenGeneratorInstance;
		LabelGenerator ppiLabelGeneratorInstance;
		BarcodeGenerator specBarcodeGeneratorInstance;
		BarcodeGenerator cprBarcodeGeneratorInstance;


		specBarcodeGeneratorInstance = BarcodeGeneratorFactory
		.getInstance(Constants.SPECIMEN_BARCODE_GENERATOR_PROPERTY_NAME);
		if (specBarcodeGeneratorInstance != null)
		{
			Variables.isSpecimenBarcodeGeneratorAvl = true;
		}


		specimenGeneratorInstance = LabelGeneratorFactory.getInstance(Constants.SPECIMEN_LABEL_GENERATOR_PROPERTY_NAME);
		if (specimenGeneratorInstance != null)
		{
			Variables.isSpecimenLabelGeneratorAvl = true;
		}
		if(specimenGeneratorInstance instanceof TemplateBasedLabelGenerator)
		{
			Variables.isTemplateBasedLblGeneratorAvl = true;
		}

		ppiLabelGeneratorInstance = LabelGeneratorFactory
						.getInstance(Constants.PROTOCOL_PARTICIPANT_IDENTIFIER_LABEL_GENERATOR_PROPERTY_NAME);
		if (ppiLabelGeneratorInstance != null)
		{
			Variables.isProtocolParticipantIdentifierLabelGeneratorAvl = true;
		}

		setSCGGenratorVar();
		cprBarcodeGeneratorInstance = BarcodeGeneratorFactory
				.getInstance(Constants.COLL_PROT_REG_BARCODE_GENERATOR_PROPERTY_NAME);
		if (cprBarcodeGeneratorInstance != null)
		{
			Variables.isCollectionProtocolRegistrationBarcodeGeneratorAvl = true;
		}




	}

	/**
	 * sets the global variable for SCG label n barcode.
	 * @param scgLableGeneratorInstance
	 * @throws NameGeneratorException
	 */
	private static void setSCGGenratorVar()
			throws NameGeneratorException
	{
		BarcodeGenerator scgBarcodeGeneratorInstance;
		LabelGenerator scgLableGeneratorInstance = LabelGeneratorFactory
		.getInstance(Constants.SPECIMEN_COLL_GROUP_LABEL_GENERATOR_PROPERTY_NAME);
		if (scgLableGeneratorInstance != null)
		{
			Variables.isSpecimenCollGroupLabelGeneratorAvl = true;
		}
		scgBarcodeGeneratorInstance = BarcodeGeneratorFactory
				.getInstance(Constants.SPECIMEN_COLL_GROUP_BARCODE_GENERATOR_PROPERTY_NAME);
		if (scgBarcodeGeneratorInstance != null)
		{
			Variables.isSpecimenCollGroupBarcodeGeneratorAvl = true;
		}
	}

}
