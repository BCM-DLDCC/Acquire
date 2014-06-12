
package edu.wustl.catissuecore.querysuite.metadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;

/**
 * This class is used to update the constraintProperties of the Associations according to
 * the new model of DE & also update the primaryKey Attribute Collection of the entity.
 * @author pavan_kalantri
 *
 */
public class UpdateSchemaForConstraintProperties
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	/**
	 * Generic logger.
	 */
	private static Logger logger = Logger
			.getCommonLogger(UpdateSchemaForConstraintProperties.class);

	/**
	 * Specify entity Id Vs Primary Attribute Id map.
	 */
	private static Map<Long, Long> entityIdVsPrimaryAttrId = new HashMap<Long, Long>();

	/**
	 * main method.
	 * @param args arguments.
	 * @throws SQLException SQL Exception
	 * @throws DAOException DAO Exception
	 */
	public static void main(String[] args) throws SQLException, DAOException
	{
		System.out.println("UpdateSchemaForConstraintProperties.main()=====START");
		try
		{
			updateSchema();

		}
		catch (final Exception e)
		{
			UpdateSchemaForConstraintProperties.logger.error("Could not update the metadata" +
					" For constraint properties"+e.getMessage(), e);
			e.printStackTrace();

		}
		System.out.println("UpdateSchemaForConstraintProperties.main()=====END");
	}

	/**
	 * It will update the data in the database.
	 * @throws SQLException SQL Exception
	 * @throws DAOException DAO Exception
	 */
	private static void updateSchema() throws SQLException, DAOException
	{
		updateCompositeKeyRelationTable();
		updateConstraintPropertiesForAssociation();
		updateConstraintPropertiesForInheritance();

	}

	/**
	 * It will update the Constraint properties of the entity in case of inheritance 
	 * @throws SQLException
	 * @throws DAOException
	 */
	private static void updateConstraintPropertiesForInheritance() throws SQLException,
			DAOException
	{
		System.out
				.println("UpdateSchemaForConstraintProperties.updateConstraintPropertiesForInheritance()");
		JDBCDAO jdbcdao = null;
		final String appName = CommonServiceLocator.getInstance().getAppName();
		final IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		jdbcdao = daoFactory.getJDBCDAO();
		jdbcdao.openSession(null);
		final String getEntityIdQuery = "select IDENTIFIER, PARENT_ENTITY_ID from DYEXTN_ENTITY where PARENT_ENTITY_ID is not null";
		final ResultSet resultSet = jdbcdao.getQueryResultSet(getEntityIdQuery);
		final String getTablePropertiesQuery = "select CONSTRAINT_NAME from DYEXTN_TABLE_PROPERTIES where ABSTRACT_ENTITY_ID = ";

		final List<Vector> list = new ArrayList<Vector>();
		while (resultSet.next())
		{
			long entityId = 0;
			long parentEntityId = 0;
			String cnstrName = null;
			final Vector vList = new Vector<List>();
			entityId = resultSet.getLong(1);
			parentEntityId = resultSet.getLong(2);

			final ResultSet rsltSet = jdbcdao.getQueryResultSet(getTablePropertiesQuery + entityId);
			if (rsltSet.next())
			{
				cnstrName = rsltSet.getString(1);
			}
			jdbcdao.closeStatement(rsltSet);
			vList.add(Long.toString(entityId));
			vList.add(Long.toString(parentEntityId));
			vList.add(cnstrName);
			list.add(vList);
		}

		jdbcdao.closeStatement(resultSet);
		jdbcdao.closeSession();

		for (final Vector v : list)
		{
			jdbcdao = daoFactory.getJDBCDAO();
			jdbcdao.openSession(null);
			final long entityId = Long.parseLong(v.get(0).toString());
			final long parentEntityId = Long.parseLong(v.get(1).toString());
			String cnstrName = null;

			if (v.get(2) != null)
			{
				cnstrName = v.get(2).toString();
			}
			addConstraintProperties(entityId, parentEntityId, cnstrName, jdbcdao);
			jdbcdao.commit();
			jdbcdao.closeSession();

		}

	}

	/**
	 * It will add the new constraintProperties record in the case of inheritance
	 * @param entityId
	 * @param parentEntityId
	 * @param cnstrName
	 * @throws SQLException
	 * @throws DAOException
	 */
	private static void addConstraintProperties(long entityId, long parentEntityId,
			String cnstrName, JDBCDAO jdbcdao) throws SQLException, DAOException
	{
		final StringBuffer tableName = new StringBuffer("DYEXTN_CONSTRAINT_PROPERTIES");
		final long cnstrPropId = getNextUniqeId("DYEXTN_DATABASE_PROPERTIES", jdbcdao);
		final String databasePropQuery = "insert into DYEXTN_DATABASE_PROPERTIES(IDENTIFIER) values ("
				+ cnstrPropId + ")";

		jdbcdao.executeUpdate(databasePropQuery);
		final String insertCnstrPropQuery = "insert into " + tableName
				+ " (IDENTIFIER, CONSTRAINT_NAME, ABSTRACT_ENTITY_ID ) values (" + cnstrPropId
				+ ", '" + cnstrName + "', " + entityId + ")";
		jdbcdao.executeUpdate(insertCnstrPropQuery);

		final long cnstrKeyPropId = getNextUniqeId("DYEXTN_CONSTRAINTKEY_PROP", jdbcdao);
		final long primaryAttributeId = entityIdVsPrimaryAttrId.get(parentEntityId);
		final String cnstrKeyPropQuery = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER,PRIMARY_ATTRIBUTE_ID ,SRC_CONSTRAINT_KEY_ID) values("
				+ cnstrKeyPropId + "," + primaryAttributeId + "," + cnstrPropId + ")";

		jdbcdao.executeUpdate(cnstrKeyPropQuery);
		addSrcColumnProperties("IDENTIFIER", cnstrKeyPropId, jdbcdao);

	}

	/**
	 * It will search the id attributes in the abstract_metadata & then search there entities and update 
	 * the entity_composite_key_rel table
	 * @throws SQLException
	 * @throws DAOException
	 */
	private static void updateCompositeKeyRelationTable() throws SQLException, DAOException
	{
		final String query = "select IDENTIFIER from DYEXTN_ABSTRACT_METADATA where NAME like 'id'";
		JDBCDAO jdbcdao = null;
		final List<Long> lstAttribute = new ArrayList<Long>();
		final String appName = CommonServiceLocator.getInstance().getAppName();
		final IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		jdbcdao = daoFactory.getJDBCDAO();
		jdbcdao.openSession(null);
		final ResultSet resultSet = jdbcdao.getQueryResultSet(query);
		while (resultSet.next())
		{
			final Long attributeId = resultSet.getLong(1);
			lstAttribute.add(attributeId);
		}
		jdbcdao.closeStatement(resultSet);
		jdbcdao.closeSession();

		Long entityId;
		try
		{
			JDBCDAO jdbcdao1 = null;
			final IDAOFactory daoFactory1 = DAOConfigFactory.getInstance().getDAOFactory(appName);
			jdbcdao1 = daoFactory1.getJDBCDAO();
			jdbcdao1.openSession(null);
			for (final Long attributeId : lstAttribute)
			{
				entityId = getSrcEntityIdOfAttribute(attributeId, jdbcdao1);
				entityIdVsPrimaryAttrId.put(entityId, attributeId);
				addEntityCompositeKey(entityId, attributeId, jdbcdao1);

			}
			jdbcdao1.commit();
			jdbcdao1.closeSession();
		}
		catch (final Exception e)
		{
			UpdateSchemaForConstraintProperties.logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		System.out.println("UpdateSchemaForConstraintProperties.updateCompositeKeyRelationTable()");
	}

	/**
	 * It will add the primary key attributes of the entity in the Entity_COMPOSITE_KEY_REL table
	 * @param entityId
	 * @param attributeId
	 * @throws DAOException
	 */
	private static void addEntityCompositeKey(long entityId, long attributeId, JDBCDAO jdbcdao)
			throws DAOException
	{
		final String query = "insert into dyextn_entiy_composite_key_rel values (" + entityId + ","
				+ attributeId + ",0)";
		jdbcdao.executeUpdate(query);
	}

	/**
	 * It will update the constraintProperties of the association according to new DE Model
	 * @throws SQLException
	 * @throws DAOException
	 */
	private static void updateConstraintPropertiesForAssociation() throws SQLException,
			DAOException
	{

		Long cnstrPropId;
		String srcEntityKey = null;
		String tgtEntityKey = null;
		String srcCnstrName = null;
		Long associationId;
		Long categoryAssonId;
		Long cnstrKeyPropId;
		Long entityId;
		Long primaryAttributeId;
		String cnstrKeyPropQuery;

		final List<Vector> vlist = new ArrayList<Vector>();
		JDBCDAO jdbcdao = null;
		try
		{

			final String appName = CommonServiceLocator.getInstance().getAppName();
			final IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
			jdbcdao = daoFactory.getJDBCDAO();
			jdbcdao.openSession(null);
			final String query = "select * from DYEXTN_CONSTRAINT_PROPERTIES";

			final ResultSet resultSet = jdbcdao.getQueryResultSet(query);
			while (resultSet.next())
			{
				cnstrPropId = resultSet.getLong("IDENTIFIER");
				srcEntityKey = resultSet.getString("SOURCE_ENTITY_KEY");
				tgtEntityKey = resultSet.getString("TARGET_ENTITY_KEY");
				srcCnstrName = resultSet.getString("SRC_CONSTRAINT_NAME");
				associationId = resultSet.getLong("ASSOCIATION_ID");
				categoryAssonId = resultSet.getLong("CATEGORY_ASSOCIATION_ID");
				final Vector v = new Vector();
				v.add(cnstrPropId);
				v.add(srcEntityKey);
				v.add(tgtEntityKey);
				v.add(srcCnstrName);
				v.add(associationId);
				v.add(categoryAssonId);
				vlist.add(v);
			}
			jdbcdao.closeStatement(resultSet);
		}
		finally
		{
			jdbcdao.closeSession();
		}
		System.out
				.println("UpdateSchemaForConstraintProperties.updateConstraintPropertiesForAssociation()"
						+ vlist.size());
		JDBCDAO jdbcdao1 = null;
		try
		{

			final StringBuffer cnstrKeyPropTableName = new StringBuffer("DYEXTN_CONSTRAINTKEY_PROP");
			final String appName = CommonServiceLocator.getInstance().getAppName();
			final IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);

			for (final Vector v : vlist)
			{
				jdbcdao1 = daoFactory.getJDBCDAO();
				jdbcdao1.openSession(null);
				cnstrPropId = (Long) v.get(0);
				srcEntityKey = (String) v.get(1);
				tgtEntityKey = (String) v.get(2);
				srcCnstrName = (String) v.get(3);
				associationId = (Long) v.get(4);
				categoryAssonId = (Long) v.get(5);

				if (associationId == 0)
				{
					forCategoryAssocation(jdbcdao1, categoryAssonId, srcCnstrName, srcEntityKey,
							tgtEntityKey, cnstrPropId);
					jdbcdao1.commit();
					jdbcdao1.closeSession();
					continue;
				}
				final String cnstrQuery = "update DYEXTN_CONSTRAINT_PROPERTIES set CONSTRAINT_NAME = '"
						+ srcCnstrName + "' where IDENTIFIER = " + cnstrPropId;
				jdbcdao1.executeUpdate(cnstrQuery);
				if (srcEntityKey != null && tgtEntityKey != null)
				{
					//update srcCnstrKey Property collection

					entityId = getSrcEntityIdOfAttribute(associationId, jdbcdao1);
					primaryAttributeId = entityIdVsPrimaryAttrId.get(entityId);
					cnstrKeyPropId = getNextUniqeId(cnstrKeyPropTableName.toString(), jdbcdao1);
					cnstrKeyPropQuery = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER,PRIMARY_ATTRIBUTE_ID ,SRC_CONSTRAINT_KEY_ID) values("
							+ cnstrKeyPropId + "," + primaryAttributeId + "," + cnstrPropId + ")";
					jdbcdao1.executeUpdate(cnstrKeyPropQuery);
					addSrcColumnProperties(srcEntityKey, cnstrKeyPropId, jdbcdao1);
					// update target Constraint Key property collection 
					entityId = getTgtEntityIdOfAttribute(associationId, jdbcdao1);
					primaryAttributeId = entityIdVsPrimaryAttrId.get(entityId);
					cnstrKeyPropId = getNextUniqeId(cnstrKeyPropTableName.toString(), jdbcdao1);
					cnstrKeyPropQuery = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER , PRIMARY_ATTRIBUTE_ID ,TGT_CONSTRAINT_KEY_ID) values("
							+ cnstrKeyPropId + "," + primaryAttributeId + "," + cnstrPropId + ")";
					jdbcdao1.executeUpdate(cnstrKeyPropQuery);
					addSrcColumnProperties(tgtEntityKey, cnstrKeyPropId, jdbcdao1);
				}
				else
				{
					if (srcEntityKey == null)
					{

						//one to many case
						entityId = getTgtEntityIdOfAttribute(associationId, jdbcdao1);
						primaryAttributeId = entityIdVsPrimaryAttrId.get(entityId);
						cnstrKeyPropId = getNextUniqeId(cnstrKeyPropTableName.toString(), jdbcdao1);
						cnstrKeyPropQuery = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER , PRIMARY_ATTRIBUTE_ID ,TGT_CONSTRAINT_KEY_ID) values("
								+ cnstrKeyPropId
								+ ","
								+ primaryAttributeId
								+ ","
								+ cnstrPropId
								+ ")";
						jdbcdao1.executeUpdate(cnstrKeyPropQuery);
						addSrcColumnProperties(tgtEntityKey, cnstrKeyPropId, jdbcdao1);

					}
					else
					{
						//many to one case
						entityId = getSrcEntityIdOfAttribute(associationId, jdbcdao1);
						primaryAttributeId = entityIdVsPrimaryAttrId.get(entityId);
						cnstrKeyPropId = getNextUniqeId(cnstrKeyPropTableName.toString(), jdbcdao1);
						cnstrKeyPropQuery = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER ,PRIMARY_ATTRIBUTE_ID ,SRC_CONSTRAINT_KEY_ID) values("
								+ cnstrKeyPropId
								+ ","
								+ primaryAttributeId
								+ ","
								+ cnstrPropId
								+ ")";
						jdbcdao1.executeUpdate(cnstrKeyPropQuery);
						addSrcColumnProperties(srcEntityKey, cnstrKeyPropId, jdbcdao1);

					}
				}

				jdbcdao1.commit();
				jdbcdao1.closeSession();

			}//FOR loop

		}
		catch (final Exception e)
		{
			UpdateSchemaForConstraintProperties.logger.error(e.getMessage(),e);
			e.printStackTrace();
		}

	}

	/**
	 * @param jdbcdao1
	 * @param categoryAssonId
	 * @param srcCnstrName
	 * @param srcEntityKey
	 * @param tgtEntityKey
	 * @param cnstrPropId
	 * @throws DAOException
	 * @throws SQLException
	 */
	private static void forCategoryAssocation(JDBCDAO jdbcdao1, Long categoryAssonId,
			String srcCnstrName, String srcEntityKey, String tgtEntityKey, Long cnstrPropId)
			throws DAOException, SQLException
	{
		final StringBuffer cnstrKeyPropTableName = new StringBuffer("DYEXTN_CONSTRAINTKEY_PROP");
		Long cnstrKeyPropId;
		Long entityId;
		Long primaryAttributeId;
		String cnstrKeyPropQuery;
		final String cnstrQuery = "update DYEXTN_CONSTRAINT_PROPERTIES set CONSTRAINT_NAME = '"
				+ srcCnstrName + "' where IDENTIFIER = " + cnstrPropId;
		jdbcdao1.executeUpdate(cnstrQuery);
		if (srcEntityKey != null && tgtEntityKey != null)
		{
			entityId = getSrcEntityIdOfAttributeForCatAssn(categoryAssonId, jdbcdao1);
			primaryAttributeId = entityIdVsPrimaryAttrId.get(entityId);
			cnstrKeyPropId = getNextUniqeId(cnstrKeyPropTableName.toString(), jdbcdao1);

			cnstrKeyPropQuery = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER,PRIMARY_ATTRIBUTE_ID ,SRC_CONSTRAINT_KEY_ID) values("
					+ cnstrKeyPropId + "," + primaryAttributeId + "," + cnstrPropId + ")";
			jdbcdao1.executeUpdate(cnstrKeyPropQuery);

			addSrcColumnProperties(srcEntityKey, cnstrKeyPropId, jdbcdao1);

			// update target Constraint Key property collection 
			entityId = getTgtEntityIdOfAttributeForCatAssn(categoryAssonId, jdbcdao1);
			primaryAttributeId = entityIdVsPrimaryAttrId.get(entityId);
			cnstrKeyPropId = getNextUniqeId(cnstrKeyPropTableName.toString(), jdbcdao1);
			cnstrKeyPropQuery = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER , PRIMARY_ATTRIBUTE_ID ,TGT_CONSTRAINT_KEY_ID) values("
					+ cnstrKeyPropId + "," + primaryAttributeId + "," + cnstrPropId + ")";
			jdbcdao1.executeUpdate(cnstrKeyPropQuery);
			addSrcColumnProperties(tgtEntityKey, cnstrKeyPropId, jdbcdao1);

		}
		else
		{
			if (srcEntityKey == null)
			{
				//one to many case
				entityId = getTgtEntityIdOfAttributeForCatAssn(categoryAssonId, jdbcdao1);
				primaryAttributeId = entityIdVsPrimaryAttrId.get(entityId);
				cnstrKeyPropId = getNextUniqeId(cnstrKeyPropTableName.toString(), jdbcdao1);

				cnstrKeyPropQuery = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER , PRIMARY_ATTRIBUTE_ID ,TGT_CONSTRAINT_KEY_ID) values("
						+ cnstrKeyPropId + "," + primaryAttributeId + "," + cnstrPropId + ")";
				jdbcdao1.executeUpdate(cnstrKeyPropQuery);
				addSrcColumnProperties(tgtEntityKey, cnstrKeyPropId, jdbcdao1);
			}
			else
			{
				//many to one case
				entityId = getSrcEntityIdOfAttributeForCatAssn(categoryAssonId, jdbcdao1);
				primaryAttributeId = entityIdVsPrimaryAttrId.get(entityId);
				cnstrKeyPropId = getNextUniqeId(cnstrKeyPropTableName.toString(), jdbcdao1);
				cnstrKeyPropQuery = "insert into DYEXTN_CONSTRAINTKEY_PROP(IDENTIFIER ,PRIMARY_ATTRIBUTE_ID ,SRC_CONSTRAINT_KEY_ID) values("
						+ cnstrKeyPropId + "," + primaryAttributeId + "," + cnstrPropId + ")";

				jdbcdao1.executeUpdate(cnstrKeyPropQuery);
				addSrcColumnProperties(srcEntityKey, cnstrKeyPropId, jdbcdao1);

			}
		}

	}

	/**
	 * It will retrieve the TargetEntity Id of the given association Id
	 * @param associationId whose target entity id is to be searched
	 * @return target entity Id of the given association Id
	 * @throws SQLException
	 * @throws DAOException 
	 */
	private static long getTgtEntityIdOfAttribute(long associationId, JDBCDAO jdbcdao)
			throws SQLException, DAOException
	{
		Long entityId = null;
		final String getEntityIdQuery = "select TARGET_ENTITY_ID from DYEXTN_ASSOCIATION where IDENTIFIER = "
				+ associationId;
		final ResultSet resultSet = jdbcdao.getQueryResultSet(getEntityIdQuery);
		if (resultSet.next())
		{
			entityId = resultSet.getLong(1);
		}
		jdbcdao.closeStatement(resultSet);
		return entityId;
	}

	/**
	 * @param catAssnId
	 * @param jdbcdao
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	private static long getTgtEntityIdOfAttributeForCatAssn(long catAssnId, JDBCDAO jdbcdao)
			throws SQLException, DAOException
	{
		Long tgtentityId = null;
		final String getEntityIdQuery = "select ENTITY_ID from dyextn_category_entity where IDENTIFIER = (select IDENTIFIER from dyextn_category_entity where CATEGORY_ASSOCIATION_ID ="
				+ catAssnId + ")";
		final ResultSet resultSet = jdbcdao.getQueryResultSet(getEntityIdQuery);
		if (resultSet.next())
		{
			tgtentityId = resultSet.getLong(1);
		}
		jdbcdao.closeStatement(resultSet);

		return tgtentityId;
	}

	/**
	 * It will retrieve the SourceEntity Id of the given association Id
	 * @param associationId whose source entity id is to be searched
	 * @return source entity Id of the given association Id
	 * @throws SQLException
	 * @throws DAOException 
	 */
	private static long getSrcEntityIdOfAttribute(long attributeId, JDBCDAO jdbcdao)
			throws SQLException, DAOException
	{
		Long entityId = null;
		final String getEntityIdQuery = "select ENTIY_ID from DYEXTN_ATTRIBUTE where IDENTIFIER = "
				+ attributeId;
		final ResultSet resultSet = jdbcdao.getQueryResultSet(getEntityIdQuery);
		if (resultSet.next())
		{
			entityId = resultSet.getLong(1);
		}
		jdbcdao.closeStatement(resultSet);

		return entityId;
	}

	private static long getSrcEntityIdOfAttributeForCatAssn(long catAssnId, JDBCDAO jdbcdao)
			throws SQLException, DAOException
	{
		Long srcentityId = null;
		final String getEntityIdQuery = "select ENTITY_ID from dyextn_category_entity		where IDENTIFIER = (select CATEGORY_ENTIY_ID from dyextn_category_association where IDENTIFIER = "
				+ catAssnId + ")";
		final ResultSet resultSet = jdbcdao.getQueryResultSet(getEntityIdQuery);
		if (resultSet.next())
		{
			srcentityId = resultSet.getLong(1);
		}
		jdbcdao.closeStatement(resultSet);
		return srcentityId;
	}

	/**
	 * @param columnName
	 * @param cnstrPropId
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	private static long addSrcColumnProperties(String columnName, long cnstrPropId, JDBCDAO jdbcdao)
			throws SQLException, DAOException
	{

		final String tableName = "DYEXTN_DATABASE_PROPERTIES";
		final Long columnPropId = getNextUniqeId(tableName, jdbcdao);
		final String query = "insert into " + tableName + " values (" + columnPropId + ",'"
				+ columnName + "')";
		jdbcdao.executeUpdate(query);

		final String cnstrQuery = "insert into DYEXTN_COLUMN_PROPERTIES(IDENTIFIER,CNSTR_KEY_PROP_ID) values ("
				+ columnPropId + "," + cnstrPropId + ")";
		jdbcdao.executeUpdate(cnstrQuery);

		return columnPropId;
	}

	/**
	 * @param tableName
	 * @param jdbcdao
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public static Long getNextUniqeId(String tableName, JDBCDAO jdbcdao) throws SQLException,
			DAOException
	{
		Long nextAvailableId = null;
		final String query = "select max(IDENTIFIER) from " + tableName;
		final ResultSet resultSet = jdbcdao.getQueryResultSet(query);
		if (resultSet.next())
		{
			nextAvailableId = resultSet.getLong(1);
			nextAvailableId++;
			if (nextAvailableId == null || nextAvailableId == 0)
			{
				nextAvailableId = Long.valueOf(1);
			}
		}

		jdbcdao.closeStatement(resultSet);
		return nextAvailableId;
	}

}