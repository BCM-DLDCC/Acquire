
package edu.wustl.common.participant.utility;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.language.Metaphone;

import edu.wustl.common.exception.ApplicationException;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

/**
 * @author geeta_jaggal.
 * This call is used for updating all the existing
 * paticipants with last name metaphone code.
 * The Class UpdateParticipantMetaPhoneInfo.
 */
public final class UpdateParticipantMetaPhoneInfo
{

	 private UpdateParticipantMetaPhoneInfo(){

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            : db related configuration values
	 *
	 * @throws SQLException
	 *             : SQLException
	 * @throws IOException
	 *             : IOException
	 * @throws ClassNotFoundException
	 *             : ClassNotFoundException
	 * @throws ApplicationException
	 *             : ApplicationException
	 */
	public static void main(final String[] args) throws SQLException, IOException,
			ClassNotFoundException, ApplicationException
	{
		insertMetaPhoneCodeForLastName();

	}

	/**
	 * This method will insert metaPhonic codes for last name in the participant
	 * table.
	 *
	 * @throws DAOException
	 *             the DAO exception
	 * @throws ApplicationException
	 *             : ApplicationException
	 * @throws SQLException
	 *             : SQLException
	 */
	public static void insertMetaPhoneCodeForLastName() throws DAOException
	{

		String lNameMetaPhone = null;
		String sql = null;
		sql="select identifier,last_name from catissue_participant";
		final Metaphone metaPhoneObj = new Metaphone();
		String identifier= null;
		JDBCDAO dao = null;
		List idNameList = null;
		String lastName = null;
		try
		{
			dao = ParticipantManagerUtility.getJDBCDAO();

			final List list = dao.executeQuery(sql,null,null);
			if (list != null && !list.isEmpty())
			{
				for (int i = 0; i < list.size(); i++)
				{
					idNameList = (List) list.get(i);
					identifier = (String) idNameList.get(0);
					lastName = (String) idNameList.get(1);
					lNameMetaPhone = metaPhoneObj.metaphone(lastName);
					updateMetaPhone(dao, identifier, lNameMetaPhone);
				}
			}
			dao.commit();
		}
		catch (DAOException e)
		{
			dao.rollback();
			throw new DAOException(e.getErrorKey(), e, e.getMessage());
		}
		finally
		{
			dao.closeSession();
		}
	}

	/**
	 * Update meta phone.
	 *
	 * @param dao
	 *            the dao
	 * @param identifier
	 *            the identifier
	 * @param lNameMetaPhone
	 *            the l name meta phone
	 *
	 * @throws DAOException
	 *             the DAO exception
	 */
	private static void updateMetaPhone(final JDBCDAO dao, final String identifier,final  String lNameMetaPhone)
			throws DAOException
	{
		String query = null;
		final LinkedList<LinkedList<ColumnValueBean>> colValBeans = new LinkedList<LinkedList<ColumnValueBean>>();
		final LinkedList<ColumnValueBean> colValBeanLst = new LinkedList<ColumnValueBean>();
		colValBeanLst.add(new ColumnValueBean(lNameMetaPhone));
		colValBeanLst.add(new ColumnValueBean(identifier));
		colValBeans.add(colValBeanLst);
		query = "update catissue_participant set lName_metaPhone=? where identifier=?";
		dao.executeUpdate(query, colValBeans);
	}
}
