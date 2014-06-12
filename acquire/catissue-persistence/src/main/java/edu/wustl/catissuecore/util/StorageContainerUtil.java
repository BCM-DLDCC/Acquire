
package edu.wustl.catissuecore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import edu.wustl.catissuecore.actionForm.ISpecimenType;
import edu.wustl.catissuecore.actionForm.SpecimenArrayForm;
import edu.wustl.catissuecore.bizlogic.SiteBizLogic;
import edu.wustl.catissuecore.bizlogic.SpecimenArrayBizLogic;
import edu.wustl.catissuecore.bizlogic.StorageContainerBizLogic;
import edu.wustl.catissuecore.bizlogic.UserBizLogic;
import edu.wustl.catissuecore.domain.Capacity;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.Container;
import edu.wustl.catissuecore.domain.ContainerPosition;
import edu.wustl.catissuecore.domain.ISpecimenTypeDomain;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenArray;
import edu.wustl.catissuecore.domain.SpecimenArrayType;
import edu.wustl.catissuecore.domain.SpecimenPosition;
import edu.wustl.catissuecore.domain.StorageContainer;
import edu.wustl.catissuecore.domain.StorageType;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.QueryWhereClause;
import edu.wustl.dao.condition.EqualClause;
import edu.wustl.dao.condition.INClause;
import edu.wustl.dao.condition.NullClause;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;

public final class StorageContainerUtil
{

	private static Logger logger = Logger.getCommonLogger(StorageContainerUtil.class);
	/**
	 * creates a singleton object
	 */
	private static StorageContainerUtil storcontUtil = new StorageContainerUtil();

	/**
	 * Private constructor
	 */
	private StorageContainerUtil()
	{

	}

	/**
	 * returns single object
	 */

	public static StorageContainerUtil getInstance()
	{
		return storcontUtil;
	}

	/**
	 * This functions returns a list of disabled containers from the cache.
	 * @return Returns a list of disabled.
	 * @throws DAOException
	 */
	public static List getListOfDisabledContainersFromCache() throws ApplicationException
	{
		// TODO if map is null
		// TODO move all code to common utility

		// getting instance of catissueCoreCacheManager and getting participantMap from cache
		final CatissueCoreCacheManager catCoreCacheMngr = CatissueCoreCacheManager
				.getInstance();
		final List disabledconts = (ArrayList) catCoreCacheMngr
				.getObjectFromCache(Constants.MAP_OF_DISABLED_CONTAINERS);
		return disabledconts;
	}

	/**
	 * This functions returns a map of available rows vs. available columns.
	 * @param container The container.
	 * @return Returns a map of available rows vs. available columns.
	 * @throws DAOException
	 */

	public static Map<NameValueBean, List<NameValueBean>> getAvailablePositionMapForNewContainer(StorageContainer container)
			throws DAOException
	{
		final Map<NameValueBean, List<NameValueBean>> map = new TreeMap<NameValueBean, List<NameValueBean>>();

		if (!container.getFull().booleanValue())
		{
			final int dimX = container.getCapacity().getOneDimensionCapacity().intValue() + 1;
			final int dimY = container.getCapacity().getTwoDimensionCapacity().intValue() + 1;

			for (int x = 1; x < dimX; x++)
			{
				final List<NameValueBean> list = new ArrayList<NameValueBean>();
				for (int y = 1; y < dimY; y++)
				{
					list.add(new NameValueBean(Integer.valueOf(y), Integer.valueOf(y)));
				}
				if (!list.isEmpty())
				{
					final Integer xObj = Integer.valueOf(x);
					final NameValueBean nvb = new NameValueBean(xObj, xObj);
					map.put(nvb, list);
				}
			}
		}
		return map;
	}
   /**
    *
    * @param storageContainer - StorageContainer
    * @param allocatedPositions - allocatedPositions
    * @param dao - DAO object
    * @param pos1 - position one
    * @param pos2 - position two
    * @return Position object
    * @throws ApplicationException - ApplicationException
    */
   //Bug 15392
	public static Position getFirstAvailablePositionsInContainer(final StorageContainer storageContainer,
			HashSet<String> allocatedPositions, DAO dao,Integer pos1,Integer pos2) throws ApplicationException
	{
		final Position position = getFirstAvailablePositionInContainer(storageContainer,
				dao, allocatedPositions,pos1,pos2);
		if (position == null)
		{
			throw AppUtility.getApplicationException(null, "storage.full", "");
		}
		return position;
	}
	/**
	 *
	 * @param containerId - containerId
	 * @param aliquotCount - aliquotCount
	 * @param posDimOne -  Dimension X
	 * @param posDimTwo - Dimension Y
	 * @param dao - DAO object
	 * @param pos1 - position one
	 * @param pos2 - position two
	 * @return Map
	 * @throws BizLogicException - BizLogicException
	 */
	//Bug 15392
	public static Map<NameValueBean, List<NameValueBean>> getAvailablePositionMapForContainer(String containerId, int aliquotCount,
			Integer posDimOne, Integer posDimTwo, DAO dao,Integer pos1,Integer pos2)
			throws BizLogicException
			{
		final Map<NameValueBean, List<NameValueBean>> map = new TreeMap<NameValueBean, List<NameValueBean>>();
		int count = 0;
		final int dimX = posDimOne + 1;
		final int dimY = posDimTwo + 1;

		final boolean[][] availPositions = getAvailablePositionsForContainer(containerId,
				dimX, dimY, dao);

		for (int x = 1; x < availPositions.length; x++)
		{
			if(x >= pos1)
			{
				final List<NameValueBean> list = new ArrayList<NameValueBean>();
                if(pos2 == (availPositions[x].length-1))
                {
                	pos2 = 0;
                	continue;
                }
				for (int y = 1; y < availPositions[x].length; y++)
				{
					if(x == pos1)//bug 15412
					{
						if(y <= pos2)
						{
						  continue;
						}
					}
					if (availPositions[x][y])
					{
						list.add(new NameValueBean(new Integer(y), new Integer(y)));
						count++;
					}
				}
				if (!list.isEmpty())
				{
					final Integer xObj = new Integer(x);
					final NameValueBean nvb = new NameValueBean(xObj, xObj);
					map.put(nvb, list);
				}
			}

		}
		if (count < aliquotCount)
		{
			return new TreeMap<NameValueBean, List<NameValueBean>>();
		}
		return map;
	}
	/**
	 * This function returns the first available position in a container which can be allocated.
	 * @param container Container object
	 * @param dao DAO object
	 * @param allocatedPositions Set of allocatedPositions
	 * @param pos1 - position one
	 * @param pos2 - position two
	 * @return Positions
	 * @throws BizLogicException BizLogicException
	 */
	//bug 15260
	private static Position getFirstAvailablePositionInContainer(Container container, DAO dao,
			HashSet<String> allocatedPositions,Integer pos1,Integer pos2) throws BizLogicException
	{
		Position position = null;
		try
		{
			Integer xPos;
			Integer yPos;
			String containerValue = null;
			final Capacity capacity = getContainerCapacity(container, dao);
			if(pos1 == null && pos2 == null)
			{
				pos1 = 0;
				pos2 = 0;
			}
			final Map positionMap = getAvailablePositionMapForContainer(String
					.valueOf(container.getId()), 0, capacity.getOneDimensionCapacity(), capacity
					.getTwoDimensionCapacity(), dao,pos1,pos2);
			if (positionMap != null)
			{
				final Iterator containerPosIterator = positionMap.keySet().iterator();
				boolean positionAllottedFlag = false;
				while (containerPosIterator.hasNext() && !positionAllottedFlag)
				{
					NameValueBean nvb = (NameValueBean) containerPosIterator.next();
					xPos = Integer.valueOf(nvb.getValue());
					final List yposValues = (List) positionMap.get(nvb);
					final Iterator yposIterator = yposValues.iterator();

					while (yposIterator.hasNext())
					{
						nvb = (NameValueBean) yposIterator.next();
						yPos = Integer.valueOf(nvb.getValue());
						final Long containerId = container.getId();

						if (container.getName() != null)
						{
							containerValue = StorageContainerUtil.getStorageValueKey(container
									.getName(), null, xPos, yPos);
						}
						else
						{
							containerValue = StorageContainerUtil.getStorageValueKey(null,
									containerId.toString(), xPos, yPos);
						}
						if (!allocatedPositions.contains(containerValue))
						{
							positionAllottedFlag = true;
							position = new Position();
							position.setXPos(xPos);
							position.setYPos(yPos);
							break;
						}

					}
				}

			}
	    }
		catch (final DAOException daoEx)
		{
			logger.error(daoEx.getMessage(),daoEx);
			throw new BizLogicException(daoEx);
		}
		return position;
	}
	/**
	 * This functions returns a double dimensional boolean array which tells the
	 * availablity of storage positions of particular container. True -
	 * Available. False - Not Available.
	 * @param containerId - The container id.
	 * @param dimX - int
	 * @param dimY - int
	 * @param JDBCDAO dao  to be used for fetching data
	 * @return Returns a double dimensional boolean array of position
	 *         availablity.
	 * @throws BizLogicException throws BizLogicException
	 */
	public static boolean[][] getAvailablePositionsForContainer(String containerId, int dimX, int dimY,
			DAO dao) throws BizLogicException
	{
		final boolean[][] positions = new boolean[dimX][dimY];
		try
		{

			for (int i = 0; i < dimX; i++)
			{
				for (int j = 0; j < dimY; j++)
				{
					positions[i][j] = true;
				}
			}
			final String[] selectColumnName = new String[]{"positionDimensionOne",
					"positionDimensionTwo"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(SpecimenPosition.class
					.getName());
			queryWhereClause.addCondition(new EqualClause("storageContainer.id", containerId));
			final List list = dao.executeQuery("select positionDimensionOne,positionDimensionTwo from "+SpecimenPosition.class.getName()+" where storageContainer.id="+containerId);
			setPositions(positions, list);
			final QueryWhereClause queryWhereClause2 = new QueryWhereClause(ContainerPosition.class
					.getName());
			queryWhereClause2.addCondition(new EqualClause("parentContainer.id", containerId));
			final List list2 = dao.retrieve(ContainerPosition.class.getName(), selectColumnName,
					queryWhereClause2);
			setPositions(positions, list2);
		}
		catch (final DAOException daoEx)
		{
			logger.error(daoEx.getMessage(),daoEx);

			throw new BizLogicException(daoEx);
		}
		return positions;
	}
	/**
	 * @param positions - boolean array of position.
	 * @param list - list of objects
	 */
	private static void setPositions(boolean[][] positions, List resultSet)
	{
		if (resultSet != null)
		{
			int countX, countY;
			for (int i = 0; i < resultSet.size(); i++)
			{
				final Object[] columnList = (Object[]) resultSet.get(i);
				if ((columnList != null) && (columnList.length == 2))
				{
					countX = (Integer) columnList[0];
					countY = (Integer) columnList[1];
					positions[countX][countY] = false;
				}
			}
		}
	}

	/**
	 * @param containerId
	 * @param aliquotCount
	 * @param positionDimensionOne
	 * @param positionDimensionTwo
	 * @param dao
	 * @return Map
	 */
	 //added from p4
	public static Map getAvailablePositionMapForContainer(String containerId, int aliquotCount,
			Integer positionDimensionOne, Integer positionDimensionTwo, DAO dao)
			throws BizLogicException
	{
		final Map map = new TreeMap();
		int count = 0;
		// Logger.out.debug("dimX:"+positionDimensionOne+":dimY:"+positionDimensionTwo);
		// if (!container.isFull().booleanValue())
		// {
		final int dimX = positionDimensionOne + 1;
		final int dimY = positionDimensionTwo + 1;

		final boolean[][] availablePosistions = getAvailablePositionsForContainer(containerId,
				dimX, dimY, dao);

		for (int x = 1; x < availablePosistions.length; x++)
		{

			final List list = new ArrayList();

			for (int y = 1; y < availablePosistions[x].length; y++)
			{
				if (availablePosistions[x][y])
				{
					list.add(new NameValueBean(new Integer(y), new Integer(y)));
					count++;
				}
			}

			if (!list.isEmpty())
			{
				final Integer xObj = new Integer(x);
				final NameValueBean nvb = new NameValueBean(xObj, xObj);
				map.put(nvb, list);
			}
		}
		// }
		// Logger.out.info("Map :"+map);
		if (count < aliquotCount)
		{
			return new TreeMap();
		}
		return map;
	}

	/**
	 * This function returns the first available position in a container which can be allocated.
	 * If container is full, returns null
	 * @param container : Container for which available position is to be searched
	 * @return Position
	 * @throws BizLogicException
	 */
	public static Position getFirstAvailablePositionInContainer(Container container, DAO dao)
			throws BizLogicException
	{
		Position position = null;
		try
		{
			Integer xPos;
			Integer yPos;
			final Capacity scCapacity = getContainerCapacity(container, dao);
			final Map positionMap = getAvailablePositionMapForContainer(String
					.valueOf(container.getId()), 0, scCapacity.getOneDimensionCapacity(),
					scCapacity.getTwoDimensionCapacity(), dao);
			if (positionMap != null)
			{
				final Iterator containerPosIterator = positionMap.keySet().iterator();
				if (containerPosIterator.hasNext())
				{
					NameValueBean nvb = (NameValueBean) containerPosIterator.next();
					xPos = Integer.valueOf(nvb.getValue());
					final List yposValues = (List) positionMap.get(nvb);
					final Iterator yposIterator = yposValues.iterator();

					if (yposIterator.hasNext())
					{
						nvb = (NameValueBean) yposIterator.next();
						yPos = Integer.valueOf(nvb.getValue());
						position = new Position();
						position.setXPos(xPos);
						position.setYPos(yPos);
					}
				}
			}
		}
		catch (final DAOException daoEx)
		{
			logger.error(daoEx.getMessage(),daoEx);
			throw new BizLogicException(daoEx);
		}
		return position;
	}

	private static Capacity getContainerCapacity(Container container, DAO dao) throws DAOException
	{
		Capacity scCapacity = container.getCapacity();
		if (scCapacity == null)
		{
			scCapacity = new Capacity();
			final String[] selectColumnName = new String[]{"capacity.oneDimensionCapacity",
					"capacity.twoDimensionCapacity"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(StorageContainer.class
					.getName());
			queryWhereClause.addCondition(new EqualClause("id", container.getId()));
			final List list = dao.retrieve(StorageContainer.class.getName(), selectColumnName,
					queryWhereClause);
			final Object[] returnValues = (Object[]) list.get(0);
			scCapacity.setOneDimensionCapacity((Integer) returnValues[0]);
			scCapacity.setTwoDimensionCapacity((Integer) returnValues[1]);
		}
		return scCapacity;

	}

	/**
	 *  This method gives first valid storage position to a specimen if it is not given.
	 *  If storage position is given it validates the storage position
	 * @param specimen
	 * @throws ApplicationException
	 */
	public static void validateStorageLocationForSpecimen(Specimen specimen, DAO dao,
			HashSet<String> allocatedPositions) throws ApplicationException
	{
		Integer xPos = null;
		Integer yPos = null;
		if (specimen.getSpecimenPosition() != null
				&& specimen.getSpecimenPosition().getStorageContainer() != null)
		{
			final StorageContainer storageContainer = specimen.getSpecimenPosition()
					.getStorageContainer();
			String containerValue = null;
			if (specimen.getSpecimenPosition() != null)
			{
				xPos = specimen.getSpecimenPosition().getPositionDimensionOne();
				yPos = specimen.getSpecimenPosition().getPositionDimensionTwo();
			}
			/**
			 *  Following code is added to set the x and y dimension in case only storage container is given
			 *  and x and y positions are not given
			 */

			if (xPos == null || yPos == null)
			{
				final Position position = getFirstAvailablePositionInContainer(
						storageContainer, dao);
				if (position != null)
				{
					xPos = position.getXPos();
					yPos = position.getYPos();
					final Long containerId = storageContainer.getId();
					if (containerId != null)
					{
						containerValue = StorageContainerUtil.getStorageValueKey(null, containerId
								.toString(), xPos, yPos);
					}
					else
					{
						containerValue = StorageContainerUtil.getStorageValueKey(storageContainer
								.getName(), null, xPos, yPos);
					}
					if (!allocatedPositions.contains(containerValue))
					{
						final SpecimenPosition specPos = specimen.getSpecimenPosition();
						specPos.setPositionDimensionOne(xPos);
						specPos.setPositionDimensionTwo(yPos);
						specPos.setSpecimen(specimen);
						allocatedPositions.add(containerValue);
						//	break;
					}
				}
				else
				{
					throw AppUtility.getApplicationException(null, "storage.full", "");
				}
			}
			if (containerValue == null)
			{
				if (storageContainer.getId() == null)
				{
					containerValue = StorageContainerUtil.getStorageValueKey(storageContainer
							.getName(), null, xPos, yPos);
				}
				else
				{
					containerValue = StorageContainerUtil.getStorageValueKey(null, storageContainer
							.getId().toString(), xPos, yPos);
				}
				if (!allocatedPositions.contains(containerValue))
				{
					allocatedPositions.add(containerValue);
				}
				else
				{
					throw AppUtility.getApplicationException(null,
							"errors.storageContainer.Multiple.inUse", "StorageContainerUtil.java");
				}
			}

			if (xPos == null || yPos == null || xPos.intValue() < 0 || yPos.intValue() < 0)
			{
				throw AppUtility.getApplicationException(null, "errors.item.format",
						"StorageContainerUtil.java :"
								+ ApplicationProperties
										.getValue("specimen.positionInStorageContainer"));
			}
		}

	}

	/**
	* @param dao
	* @param similarContainerMap
	* @param containerPrefixKey
	* @param positionsToBeAllocatedList
	* @param usedPositionsList
	* @param iCount
	* @throws ApplicationException
	*/
	public static void prepareContainerMap(DAO dao, Map similarContainerMap,
			String containerPrefixKey, List positionsToBeAllocatedList, List usedPositionsList,
			int iCount, String contId) throws ApplicationException
	{
		final String radioButonKey = "radio_" + iCount;
		final String containerIdKey = containerPrefixKey + iCount + contId;
		final String containerNameKey = containerPrefixKey + iCount + "_StorageContainer_name";
		final String posDim1Key = containerPrefixKey + iCount + "_positionDimensionOne";
		final String posDim2Key = containerPrefixKey + iCount + "_positionDimensionTwo";

		String containerName = null;
		String containerId = null;
		String posDim1 = null;
		String posDim2 = null;
		//get the container values based on user selection from dropdown or map
		if (similarContainerMap.get(radioButonKey) != null
				&& similarContainerMap.get(radioButonKey).equals("1"))
		{
			containerId = (String) similarContainerMap.get(containerIdKey);
			posDim1 = (String) similarContainerMap.get(posDim1Key);
			posDim2 = (String) similarContainerMap.get(posDim2Key);
			String position = containerId + Constants.STORAGE_LOCATION_SAPERATOR + posDim1
				+ Constants.STORAGE_LOCATION_SAPERATOR + posDim2;
			/**
			 * bug 12881
			 * If same positions are applied to more than one aliquot.
			 */
			 if(!usedPositionsList.contains( position ))
             {
			   usedPositionsList.add(position);
             }
             else
             {
            	throw AppUtility.getApplicationException(null,
             			"errors.storageContainer.inUse", "StorageContainerUtil.java");
             }

		}
		else if (similarContainerMap.get(radioButonKey) != null
				&& similarContainerMap.get(radioButonKey).equals("2"))
		{
			containerName = (String) similarContainerMap.get(containerNameKey + "_fromMap");
			if(containerName.trim().equals( "" ))
			{
				final String message = ApplicationProperties.getValue("specimen.storageContainer");
				throw AppUtility.getApplicationException(null, "errors.invalid",
						message + ":StorageContainerUtil.java ");
			}

			final String sourceObjectName = StorageContainer.class.getName();
			final String[] selectColumnName = {"id"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new EqualClause("name", containerName));
			final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);

			if (!list.isEmpty())
			{
				containerId = list.get(0).toString();
			}
			else
			{
				final String message = ApplicationProperties.getValue("specimen.storageContainer");
				throw AppUtility.getApplicationException(null, "errors.invalid",
						message + ":StorageContainerUtil.java ");
			}

			posDim1 = (String) similarContainerMap.get(posDim1Key + "_fromMap");
			posDim2 = (String) similarContainerMap.get(posDim2Key + "_fromMap");

			if (posDim1 == null || posDim1.trim().equals("") || posDim2 == null
					|| posDim2.trim().equals(""))
			{
				positionsToBeAllocatedList.add(Integer.valueOf(iCount));
			}
			else
			{
                String position = containerId + Constants.STORAGE_LOCATION_SAPERATOR + posDim1
				+ Constants.STORAGE_LOCATION_SAPERATOR + posDim2;
               // bug 12881
                if(!usedPositionsList.contains( position ))
                {
                	usedPositionsList.add(position);
                	similarContainerMap.put(containerIdKey, containerId);
                	similarContainerMap.put(posDim1Key, posDim1);
                	similarContainerMap.put(posDim2Key, posDim2);
                	similarContainerMap.remove(containerIdKey + "_fromMap");
                	similarContainerMap.remove(posDim1Key + "_fromMap");
                	similarContainerMap.remove(posDim2Key + "_fromMap");
                }
                else
                {
                	throw AppUtility.getApplicationException(null,
                			"errors.storageContainer.inUse", "StorageContainerUtil.java");
                }
			}

		}
	}

	/**
	 * check for initial values for storage container.
	 * @param containerMap container map
	 * @return list of initial values
	 */
	public static List checkForInitialValues(Map containerMap)
	{
		List initialValues = null;

		if ((containerMap != null) && (containerMap.size() > 0))
		{
			final String[] startingPoints = new String[3];

			Set keySet = containerMap.keySet();
			Iterator itr = keySet.iterator();
			NameValueBean nvb = (NameValueBean) itr.next();
			startingPoints[0] = nvb.getValue();

			final Map map1 = (Map) containerMap.get(nvb);
			keySet = map1.keySet();
			itr = keySet.iterator();
			nvb = (NameValueBean) itr.next();
			startingPoints[1] = nvb.getValue();

			final List list = (List) map1.get(nvb);
			nvb = (NameValueBean) list.get(0);
			startingPoints[2] = nvb.getValue();

			logger.info("Starting points[0]" + startingPoints[0]);
			logger.info("Starting points[1]" + startingPoints[1]);
			logger.info("Starting points[2]" + startingPoints[2]);
			initialValues = new ArrayList();
			initialValues.add(startingPoints);
		}
		return initialValues;
	}

	/**
	 * @param containerMap
	 * @param aliquotCount
	 * @param counter
	 * @return
	 */
	public static int checkForLocation(Map containerMap, int aliquotCount, int counter)
	{
		final Object[] containerId = containerMap.keySet().toArray();
		for (int i = 0; i < containerId.length; i++)
		{
			final Map xDimMap = (Map) containerMap.get(containerId[i]);
			if (!xDimMap.isEmpty())
			{
				final Object[] xDim = xDimMap.keySet().toArray();
				for (final Object element : xDim)
				{
					final List yDimList = (List) xDimMap.get(element);
					counter = counter + yDimList.size();
					if (counter >= aliquotCount)
					{
						i = containerId.length;
						break;
					}
				}
			}
		}
		return counter;
	}

	/**
	 * @param form
	 * @param containerMap
	 * @param aliquotMap
	 */

	public static void populateAliquotMap(String objectKey, Map containerMap, Map aliquotMap,
			String noOfAliquots)
	{
		int counter = 1;
		if (!containerMap.isEmpty())
		{
			final Object[] containerId = containerMap.keySet().toArray();
			for (int i = 0; i < containerId.length; i++)
			{
				final Map xDimMap = (Map) containerMap.get(containerId[i]);
				counter = setAliquotMap(objectKey, xDimMap, containerId, noOfAliquots, counter,
						aliquotMap, i);
				//bug 15085
				/**
				 * changed condition from <= to == as:
				 * If the first container contains only 1 free position and aliquot count is 2,
				 * then 1st specimen occupies 1st position and counter increments and becomes 2.
				 * Which is equal to aliquot count thus the 2nd specimen didn't get any position.
				 */
				if (counter -1  == Integer.parseInt(noOfAliquots))
				{
					i = containerId.length;
				}
			}
		}
	}

	public static int setAliquotMap(String objectKey, Map xDimMap, Object[] containerId,
			String noOfAliquots, int counter, Map aliquotMap, int count1)
	{
		objectKey = objectKey + ":";
		if (!xDimMap.isEmpty())
		{
			final Object[] xDim = xDimMap.keySet().toArray();
			for (int j = 0; j < xDim.length; j++)
			{
				final List yDimList = (List) xDimMap.get(xDim[j]);
		//  bug commeneted as it was looking for container having positions for all aliquots in one row only
				//bug 15085
//				if(!(yDimList.size()>=Integer.parseInt(noOfAliquots)))
//				{
//					continue;
//				}
				for (int k = 0; k < yDimList.size(); k++)
				{
					if (counter - 1 <= Integer.parseInt(noOfAliquots))
					{
						final String containerKey = objectKey + counter + "_StorageContainer_id";
						final String pos1Key = objectKey + counter + "_positionDimensionOne";
						final String pos2Key = objectKey + counter + "_positionDimensionTwo";

						aliquotMap.put(containerKey, ((NameValueBean) containerId[count1]).getValue());
						aliquotMap.put(pos1Key, ((NameValueBean) xDim[j]).getValue());
						aliquotMap.put(pos2Key, ((NameValueBean) yDimList.get(k)).getValue());
                        counter++;

					}
					else
					{
						j = xDim.length;
						//i = containerId.length;
						break;
					}
				}
			}
		}
		//counter = new Integer(intCounter);
		return counter;
	}

	/**
	 * @param specimenArrayBizLogic
	 * @param specimenArrayForm
	 * @param containerMap
	 * @return
	 * @throws DAOException
	 */
	public static List setInitialValue(SpecimenArrayBizLogic specimenArrayBizLogic,
			SpecimenArrayForm specimenArrayForm, TreeMap containerMap, DAO dao)
			throws ApplicationException
	{
		List initialValues = null;
		final String[] startingPoints = new String[]{"-1", "-1", "-1"};
		if (specimenArrayForm.getStorageContainer() != null
				&& !specimenArrayForm.getStorageContainer().equals("-1"))
		{
			startingPoints[0] = specimenArrayForm.getStorageContainer();
		}
		if (specimenArrayForm.getPositionDimensionOne() != -1)
		{
			startingPoints[1] = String.valueOf(specimenArrayForm.getPositionDimensionOne());
		}

		if (specimenArrayForm.getPositionDimensionTwo() != -1)
		{
			startingPoints[2] = String.valueOf(specimenArrayForm.getPositionDimensionTwo());
		}
		initialValues = new ArrayList();
		initialValues.add(startingPoints);

		return initialValues;
	}

	/**
	 *  This method allocates available position to single specimen
	 * @param object
	 * @param aliquotMap
	 * @param usedPositionsList
	 * @throws ApplicationException
	 */
	public static void allocatePositionToSingleContainerOrSpecimen(Object object, Map aliquotMap,
			HashSet<String> allocatedPositions, String spKey, String scId, DAO dao) throws ApplicationException
	{
		final int specimenNumber = ((Integer) object).intValue();
		final String specimenKey = spKey;
		final String containerIDKey = specimenKey + specimenNumber + "_StorageContainer_id";
		final String containerIdKey = specimenKey + specimenNumber + scId;
		final String posDim1Key = specimenKey + specimenNumber + "_positionDimensionOne";
		final String posDim2Key = specimenKey + specimenNumber + "_positionDimensionTwo";

		final String containerID = (String) aliquotMap.get(containerIDKey);
		final Container container = new Container();
		container.setId(Long.valueOf(containerID));
		final Position position = getFirstAvailablePositionInContainer(container, dao,allocatedPositions,null,null);
		String containerValue = null;
		if (position != null)
		{
		  	containerValue = StorageContainerUtil.getStorageValueKey(null,containerID, position.getXPos(), position.getYPos());
			allocatedPositions.add( containerValue );
			aliquotMap.put(containerIdKey, containerID);
			aliquotMap.put(posDim1Key, ""+position.getXPos());
			aliquotMap.put(posDim2Key, ""+position.getYPos());
			aliquotMap.remove(containerIdKey + "_fromMap");
			aliquotMap.remove(posDim1Key + "_fromMap");
			aliquotMap.remove(posDim2Key + "_fromMap");
		}
		else
		{
			throw AppUtility.getApplicationException(null, "storage.container.has.nt.enough.space",
					Long.valueOf(specimenNumber).toString());
		}
	}

	/**
	 *
	 * @param pos1 - position one
	 * @param pos2 - position two
	 * @return - boolean
	 */
	public static boolean checkPos1AndPos2(String pos1, String pos2)
	{
		boolean flag = false;
		if (pos1 != null && !pos1.trim().equals(""))
		{
			long longNumber = 1;
			try
			{
				longNumber = Long.parseLong(pos1);
			}
			catch (final Exception e)
			{
				StorageContainerUtil.logger.error(e.getMessage(), e);
				e.printStackTrace();
				flag = true;

			}
			if (longNumber <= 0)
			{
				flag = true;
			}
		}
		if (pos2 != null && !pos2.trim().equals(""))
		{
			long longNumber = 1;
			try
			{
				longNumber = Long.parseLong(pos2);
			}
			catch (final Exception e)
			{
				StorageContainerUtil.logger.error(e.getMessage(), e);
				e.printStackTrace();
				flag = true;

			}
			if (longNumber <= 0)
			{
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * Description: This method is used to create storage location key value.
	 * Used while updating or inserting Specimen
	 * @param containerName - storage container name
	 * @param containerID - storage container id
	 * @param containerPos1 - storage container Position 1
	 * @param containerPos2 - storage container Position 2
	 * @return storageValue : container name or container id:container Position 1,container Position 2
	 */
	//bug 8294
	public static String getStorageValueKey(String containerName, String containerID,
			Integer containerPos1, Integer containerPos2)
	{
		final StringBuffer storageValue = new StringBuffer();
		if (containerName != null)
		{
			storageValue.append(containerName);
			storageValue.append(':');
			storageValue.append(containerPos1);
			storageValue.append(" ,");
			storageValue.append(containerPos2);
		}
		else if (containerID != null)
		{
			storageValue.append(containerID);
			storageValue.append(':');
			storageValue.append(containerPos1);
			storageValue.append(" ,");
			storageValue.append(containerPos2);
		}
		return storageValue.toString();
	}

	/**
	 *
	 * @param containerMap - containerMap
	 * @param containerId - containerId
	 * @param xPos - xPos
	 * @param yPos - yPos
	 * @param dao - dao
	 * @throws BizLogicException - BizLogicException
	 */
	public static void addAllocatedPositionToMap(TreeMap containerMap, long containerId, int xPos,
			int yPos, DAO dao) throws BizLogicException
	{
	    //bug 14826
		long relevanceCnt = 1;
		if ((containerId != 0) && (xPos != 0) && (yPos != 0))
		{
			List parentContainerNameList;
			try
			{
				parentContainerNameList = dao.retrieveAttribute(StorageContainer.class, "id",
						containerId, "name");
				if ((parentContainerNameList != null) && (parentContainerNameList.size() > 0))
				{
					final String parentContainerName = (String) parentContainerNameList.get(0);
					Set keySet = containerMap.keySet();
					for (final Iterator itr = keySet.iterator(); itr.hasNext();)
					{
						NameValueBean containerName = (NameValueBean) itr.next();
						if(containerName.getName().equals( parentContainerName ))
						{
							relevanceCnt = containerName.getRelevanceCounter();
							break;
						}
					}
					NameValueBean nvBean = new NameValueBean(parentContainerName,Long.valueOf( containerId ),relevanceCnt);
					Map positionMap = (Map) containerMap.get(nvBean);
					if (positionMap == null)
					{
						positionMap = new TreeMap();
					}
					List list = (List) positionMap.get(new NameValueBean(xPos, xPos));
					if (list == null)
					{
						list = new ArrayList();
					}
					list.add(0,new NameValueBean(yPos, yPos));
					positionMap.put(new NameValueBean(xPos, xPos), list);
					containerMap.put(new NameValueBean(parentContainerName, Long.valueOf( containerId ),relevanceCnt), positionMap);

				}
			}
			catch (final DAOException e)
			{
				StorageContainerUtil.logger.error(e.getMessage(), e);
				throw new BizLogicException(e);
			}
		}
	}

	/**
	 * Checking for available position.
	 * @param jdbcDao JDBCDAO object
	 * @param containerId container Identifier
	 * @param containerName container name
	 * @param pos1 position dim 1
	 * @param pos2 position dim 2
	 * @return Boolean
	 * @throws ApplicationException ApplicationException
	 */
	public static boolean isPositionAvailable(JDBCDAO jdbcDao, String containerId, String containerName,
			String pos1, String pos2) throws ApplicationException
	{
		if (!isSpecimenAssigned(containerId, containerName, pos1, pos2))
		{
			if (!isContainerAssigned(containerId, containerName, pos1, pos2))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * Checking container.
	 * @param containerId container Identifier
	 * @param containerName container name
	 * @param pos1 position dim 1
	 * @param pos2 position dim 2
	 * @return Boolean
	 * @throws ApplicationException ApplicationException
	 */
	private static  boolean isContainerAssigned(String containerId, String containerName,
			String pos1, String pos2) throws ApplicationException
	{
		final StringBuilder query = new StringBuilder();
		if ((containerId == null) || (containerId.trim().equals("")))
		{
			query.append("select id from " + ContainerPosition.class.getName()
					+ " contPos where contPos.parentContainer.name='" + containerName + "'");
		}
		else
		{
			query.append("select id from " + ContainerPosition.class.getName()
					+ " contPos where contPos.parentContainer.id=" + containerId);
		}

		query.append(" and contPos.positionDimensionOne=" + pos1
				+ " and contPos.positionDimensionTwo=" + pos2);
		final List allocatedList = AppUtility.executeQuery(query.toString());

		if ((allocatedList != null) && (!allocatedList.isEmpty()))
		{
			return true;
		}
		return false;
	}

	/**
	 * Checking specimen.
	 * @param containerId container Identifier
	 * @param containerName container name
	 * @param pos1 position dim 1
	 * @param pos2 position dim 2
	 * @return Boolean
	 * @throws ApplicationException
	 */
	private static boolean isSpecimenAssigned(String containerId, String containerName,
			String pos1, String pos2) throws ApplicationException
	{
		final StringBuilder query = new StringBuilder();
		if ((containerId == null) || (containerId.trim().equals("")))
		{
			query.append("select id from " + SpecimenPosition.class.getName()
					+ " specPos where specPos.storageContainer.name='" + containerName + "'");
		}
		else
		{
			query.append("select id from " + SpecimenPosition.class.getName()
					+ " specPos where specPos.storageContainer.id=" + containerId);
		}
		query.append(" and specPos.positionDimensionOne=" + pos1
				+ "  and specPos.positionDimensionTwo=" + pos2);
		final List allocatedList = AppUtility.executeQuery(query.toString());
		if ((allocatedList != null) && (!allocatedList.isEmpty()))
		{
			return true;
		}
		return false;
	}

	/**
	 * Get count of container's free location.
	 * @param jdbcDAO JDBCDAO object
	 * @param containerId container identifier
	 * @param storageContainerName StorageContainer Name
	 * @return Long value
	 * @throws BizLogicException
	 */
	public static Long getCountofFreeLocationOfContainer(JDBCDAO jdbcDAO, String containerId,
			String storageContainerName) throws BizLogicException
	{
		long freeLocations = 0;
		final StringBuilder query = new StringBuilder();
		if ((containerId == null) || (containerId.trim().equals("")))
		{
			containerId = getContainerId(jdbcDAO, storageContainerName);
		}
		query
				.append("select (capacity.one_dimension_capacity * capacity.two_dimension_capacity -view1.specLocations-view2.contLocns)");
		query
				.append(" from catissue_container  cont join catissue_capacity  capacity on cont.capacity_id=capacity.identifier,");
		query
				.append(" (select count(*)  specLocations from catissue_specimen_position sp where container_id = ? ) view1,");
		query
				.append(" (select count(*)  contLocns from catissue_container_position cp where cp.parent_container_id=? ) view2 ");
		query.append(" where cont.identifier = ?");
		ColumnValueBean columnValueBean = new ColumnValueBean(containerId);
		List<ColumnValueBean> columnValueBeanList = new ArrayList<ColumnValueBean>();
		columnValueBeanList.add(columnValueBean);
		columnValueBeanList.add(columnValueBean);
		columnValueBeanList.add(columnValueBean);
		try
		{
			final List results = jdbcDAO.executeQuery(query.toString(),null, columnValueBeanList);
			final Object result = getResultSetData(results, 0, 0);
			if ((result != null) && (result instanceof String))
			{
				try
				{
					freeLocations = Long.valueOf((String) result);
				}
				catch (final NumberFormatException numFormatEx)
				{
					logger.error(numFormatEx.getMessage(),numFormatEx);
					numFormatEx.printStackTrace();
					freeLocations = 0;
				}
			}
		}
		catch (final DAOException e1)
		{
			logger.error(e1.getMessage(),e1);
			throw new BizLogicException(e1);
		}

		return freeLocations;
	}
	/**
	 *
	 * @param jdbcDAO jdbcDAO Object
	 * @param containerName Container name
	 * @return String container Identifier
	 * @throws BizLogicException BizLogicException
	 */
	private static String getContainerId(JDBCDAO jdbcDAO, String containerName) throws BizLogicException
	{
		final StringBuilder query = new StringBuilder();
		query.append("select identifier from catissue_container cont ");
		query.append("where cont.name=?");
		ColumnValueBean columnValueBean = new ColumnValueBean(containerName);
		List<ColumnValueBean> columnValueBeanList = new ArrayList<ColumnValueBean>();
		columnValueBeanList.add(columnValueBean);
		if (jdbcDAO != null)
		{
			try
			{
				final List results = jdbcDAO.executeQuery(query.toString(),null,columnValueBeanList);
				final Object result = getResultSetData(results, 0, 0);
				if ((result != null) && (result instanceof String))
				{
					return (String) result;
				}
			}
			catch (final DAOException daoExp)
			{
				logger.error(daoExp.getMessage(),daoExp);
				throw new BizLogicException(daoExp);
			}
		}
		return null;
	}

	/**
	 * Get getResultSetData
	 * @param resultSet List of result
	 * @param rowNumber integer row number
	 * @param columnNumber integer column number
	 * @return Object
	 */
	private static Object getResultSetData(List resultSet, int rowNumber, int columnNumber)
	{
		if ((resultSet != null) && (resultSet.size() > rowNumber))
		{
			final List columnList = (List) resultSet.get(rowNumber);
			if ((columnList != null) && (columnList.size() > columnNumber))
			{
				return columnList.get(columnNumber);
			}
		}
		return null;
	}

	/**
	 * @param siteidSet - siteidSet.
	 * @param siteId - siteId
	 * @return boolean value
	 */
	public static boolean hasPrivilegeonSite(Set<Long> siteidSet, Long siteId)
	{
		boolean hasPrivilege = true;
		if (siteidSet != null)
		{
			if (!siteidSet.contains(siteId))
			{
				hasPrivilege = false;
			}
		}
		return hasPrivilege;
	}


	/**
	 * @param storageContainer - StorageContainer object.
	 * @param parentContainerID - StorageContainer
	 * @param dao - DAO object.
	 * @return boolean value.
	 * @throws BizLogicException throws BizLogicException
	 */
	public static boolean isUnderSubContainer(StorageContainer storageContainer, Long parentContainerID,
			DAO dao) throws BizLogicException
	{
		try
		{
			if (storageContainer != null)
			{
				final Collection childrenColl = new StorageContainerBizLogic().getChildren(dao,
						storageContainer.getId());
				final Iterator iterator = childrenColl.iterator();
				while (iterator.hasNext())
				{
					final StorageContainer container = (StorageContainer) iterator.next();
					if (parentContainerID.longValue() == container.getId().longValue())
					{
						return true;
					}
					if (isUnderSubContainer(container, parentContainerID, dao))
					{
						return true;
					}
				}
			}
		}
		catch (final ApplicationException exp)
		{
			logger.error(exp.getMessage(), exp);
			ErrorKey errorKey = ErrorKey.getErrorKey(exp.getErrorKeyName());
			throw new BizLogicException(errorKey, exp, exp.getMsgValues());
		}

		return false;
	}

	/**
	 * @return Long value next container number.
	 * @throws ApplicationException
	 */
	public static long getNextContainerNumber() throws ApplicationException
	{
		DAO dao = null;
		try
		{
			final String sourceObjectName = "CATISSUE_STORAGE_CONTAINER";
			final String[] selectColumnName = {"max(IDENTIFIER) as MAX_NAME"};
			dao = AppUtility.openDAOSession(null);
			final List list = dao.retrieve(sourceObjectName, selectColumnName);

			if (!list.isEmpty())
			{
				final List columnList = (List) list.get(0);
				if (!columnList.isEmpty())
				{
					final String str = (String) columnList.get(0);
					if (!"".equals(str))
					{
						final long longNumber = Long.parseLong(str);
						return longNumber + 1;
					}
				}
			}

			return 1;
		}
		catch (final DAOException daoExp)
		{
			logger.error(daoExp.getMessage(), daoExp);
			ErrorKey errorKey = ErrorKey.getErrorKey(daoExp.getErrorKeyName());
			throw new ApplicationException(errorKey, daoExp, daoExp.getMsgValues());
		}
		finally
		{
			AppUtility.closeDAOSession(dao);
		}

	}
	/**
	 * @param siteName - Site name.
	 * @param typeName - site type.
	 * @param operation - operation
	 * @param longIdentifier - id.
	 * @return String container name.
	 * @throws BizLogicException throws BizLogicException
	 */
	public String getContainerName(String siteName, String typeName, String operation, long longIdentifier)
			throws ApplicationException
	{
		String containerName = "";
		if (typeName != null && siteName != null && !typeName.equals("") && !siteName.equals(""))
		{
			// Poornima:Max length of site name is 50 and Max length of
			// container type name is 100, in Oracle the name does not truncate
			// and it is giving error. So these fields are truncated in case it
			// is longer than 40.
			// It also solves Bug 2829:System fails to create a default unique
			// storage container name
			String maxSiteName = siteName;
			String maxTypeName = typeName;
			if (siteName.length() > 40)
			{
				maxSiteName = siteName.substring(0, 39);
			}
			if (typeName.length() > 40)
			{
				maxTypeName = typeName.substring(0, 39);
			}
			if (operation.equals(Constants.ADD))
			{
				containerName = maxSiteName + "_" + maxTypeName + "_"
						+ String.valueOf(getNextContainerNumber());
			}
			else
			{
				containerName = maxSiteName + "_" + maxTypeName + "_" + String.valueOf(longIdentifier);
			}
		}
		return containerName;
	}

	/**
	 * @param parentID - parentID.
	 * @param typeID - typeID
	 * @param isInSite - isInSite
	 * @return next container number
	 * @throws BizLogicException throws BizLogicException
	 */
	public static int getNextContainerNumber(long parentID, long typeID, boolean isInSite)
			throws ApplicationException
	{
		try
		{
			final String sourceObjectName = "CATISSUE_STORAGE_CONTAINER";
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);

			final String[] selectColumnName = {"max(IDENTIFIER) as MAX_NAME"};
			queryWhereClause.addCondition(new EqualClause("STORAGE_TYPE_ID", Long.valueOf(typeID)))
					.andOpr().addCondition(
							new EqualClause("PARENT_CONTAINER_ID", Long.valueOf(parentID)));

			if (isInSite)
			{
				queryWhereClause.addCondition(
						new EqualClause("STORAGE_TYPE_ID", Long.valueOf(typeID))).andOpr()
						.addCondition(new EqualClause("SITE_ID", Long.valueOf(parentID))).andOpr()
						.addCondition(new NullClause("PARENT_CONTAINER_ID"));
			}
			final JDBCDAO jdbcDAO = DAOConfigFactory.getInstance().getDAOFactory(
					Constants.APPLICATION_NAME).getJDBCDAO();

			jdbcDAO.openSession(null);

			final List list = jdbcDAO
					.retrieve(sourceObjectName, selectColumnName, queryWhereClause);

			jdbcDAO.closeSession();

			if (!list.isEmpty())
			{
				final List columnList = (List) list.get(0);
				if (!columnList.isEmpty())
				{
					final String str = (String) columnList.get(0);
					logger.info("str---------------:" + str);
					if (!"".equals(str))
					{
						final int intNumber = Integer.parseInt(str);
						return intNumber + 1;
					}
				}
			}

			return 1;
		}
		catch (final DAOException daoExp)
		{
			logger.error(daoExp.getMessage(), daoExp);
			ErrorKey errorKey = ErrorKey.getErrorKey(daoExp.getErrorKeyName());
			throw new ApplicationException(errorKey, daoExp, daoExp.getMsgValues());
		}
	}

	/**
	 * @param dao - DAO object.
	 * @param container - StorageContainer object.
	 * @return boolean value.
	 * @throws BizLogicException throws BizLogicException
	 */
	public static boolean isContainerEmpty(DAO dao, StorageContainer container) throws BizLogicException
	{
		try
		{
			String sourceObjectName = StorageContainer.class.getName();
			final String[] selectColumnName = {"locatedAtPosition.positionDimensionOne",
					"locatedAtPosition.positionDimensionTwo"};
			final String[] whereColumnName = {"locatedAtPosition.parentContainer.id"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new EqualClause("locatedAtPosition.parentContainer.id",
					container.getId()));
			List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
			if (!list.isEmpty())
			{
				return false;
			}
			else
			{
				sourceObjectName = Specimen.class.getName();
				whereColumnName[0] = "specimenPosition.storageContainer.id";
				selectColumnName[0] = "specimenPosition.positionDimensionOne";
				selectColumnName[1] = "specimenPosition.positionDimensionTwo";
				final QueryWhereClause queryWhereClausenew = new QueryWhereClause(sourceObjectName);
				queryWhereClausenew.addCondition(new EqualClause(
						"specimenPosition.storageContainer.id", container.getId()));
				list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClausenew);
				if (!list.isEmpty())
				{
					return false;
				}
				else
				{
					sourceObjectName = SpecimenArray.class.getName();
					whereColumnName[0] = "locatedAtPosition.parentContainer.id";
					selectColumnName[0] = "locatedAtPosition.positionDimensionOne";
					selectColumnName[1] = "locatedAtPosition.positionDimensionTwo";
					final QueryWhereClause queryWhereClauseinner = new QueryWhereClause(
							sourceObjectName);
					queryWhereClauseinner.addCondition(new EqualClause(
							"locatedAtPosition.parentContainer.id", container.getId()));
					list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClauseinner);

					if (!list.isEmpty())
					{
						return false;
					}
				}
			}
			return true;
		}
		catch (final DAOException daoExp)
		{
			logger.error(daoExp.getMessage(), daoExp);
			ErrorKey errorKey = ErrorKey.getErrorKey(daoExp.getErrorKeyName());
			throw new BizLogicException(errorKey, daoExp, daoExp.getMsgValues());
		}

	}

	/**
	 * @param dao - DAO object.
	 * @param parentContainer - StorageContainer object
	 * @param children - children
	 * @return boolean array
	 * @throws BizLogicException throws BizLogicException
	 */
	public static boolean[][] getStorageContainerFullStatus(DAO dao, StorageContainer parentContainer,
			Collection children) throws BizLogicException
	{
		boolean[][] fullStatus = null;
		final Integer oneDimensionCapacity = parentContainer.getCapacity()
				.getOneDimensionCapacity();
		final Integer twoDimensionCapacity = parentContainer.getCapacity()
				.getTwoDimensionCapacity();
		fullStatus = new boolean[oneDimensionCapacity.intValue() + 1][twoDimensionCapacity
				.intValue() + 1];
		if (children != null)
		{
			final Iterator iterator = children.iterator();
			logger.debug("storageContainer.getChildrenContainerCollection().size(): "
					+ children.size());
			while (iterator.hasNext())
			{
				final StorageContainer childStorageContainer = (StorageContainer) iterator.next();
				if (childStorageContainer.getLocatedAtPosition() != null)
				{
					final Integer positionDimensionOne = childStorageContainer
							.getLocatedAtPosition().getPositionDimensionOne();
					final Integer positionDimensionTwo = childStorageContainer
							.getLocatedAtPosition().getPositionDimensionTwo();
					logger.debug("positionDimensionOne : " + positionDimensionOne.intValue());
					logger.debug("positionDimensionTwo : " + positionDimensionTwo.intValue());
					fullStatus[positionDimensionOne.intValue()][positionDimensionTwo.intValue()] = true;
				}
			}
		}
		return fullStatus;
	}

	/**
	 * @param containerId - containerId.
	 * @return collection of container childrens
	 * @throws BizLogicException
	 */
	public static Collection getContainerChildren(Long containerId) throws ApplicationException
	{
		DAO dao = null;
		Collection<Container> children = null;
		try
		{
			dao = AppUtility.openDAOSession(null);
			children = new StorageContainerBizLogic().getChildren(dao, containerId);
		}
		catch (final ApplicationException e)
		{
			logger.error(e.getMessage(), e);
			ErrorKey errorKey = ErrorKey.getErrorKey(e.getErrorKeyName());
			throw new ApplicationException(errorKey, e, e.getMsgValues());
		}
		finally
		{
			AppUtility.closeDAOSession(dao);
		}
		return children;
	}


	/**
	 * @param newContainer - StorageContainer object.
	 * @param oldContainer -  StorageContainer object
	 * @return boolean value
	 */
	public static boolean checkForRestrictionsChanged(StorageContainer newContainer,
			StorageContainer oldContainer)
	{
		int flag = 0;
		final Collection cpCollNew = newContainer.getCollectionProtocolCollection();
		final Collection cpCollOld = oldContainer.getCollectionProtocolCollection();

		final Collection storTypeCollNew = newContainer.getHoldsStorageTypeCollection();
		final Collection storTypeCollOld = oldContainer.getHoldsStorageTypeCollection();

		final Collection spClassCollNew = newContainer.getHoldsSpecimenClassCollection();
		final Collection spClassCollOld = oldContainer.getHoldsSpecimenClassCollection();

		final Collection spArrayTypeCollNew = newContainer.getHoldsSpecimenArrayTypeCollection();
		final Collection spArrayTypeCollOld = oldContainer.getHoldsSpecimenArrayTypeCollection();
		/**
		 * Bug 3612 - User should be able to change the restrictions if he
		 * specifies the superset of the old restrictions if container is not
		 * empty.
		 */
		Iterator itrOld = cpCollOld.iterator();
		while (itrOld.hasNext())
		{
			flag = 0;
			final CollectionProtocol cpOld = (CollectionProtocol) itrOld.next();
			final Iterator itrNew = cpCollNew.iterator();
			if (cpCollNew.isEmpty())
			{
				break;
			}
			while (itrNew.hasNext())
			{
				final CollectionProtocol cpNew = (CollectionProtocol) itrNew.next();
				if (cpOld.getId().longValue() == cpNew.getId().longValue())
				{
					flag = 1;
					break;
				}
			}
			if (flag != 1)
			{
				return true;
			}
		}
		itrOld = storTypeCollOld.iterator();
		while (itrOld.hasNext())
		{
			flag = 0;
			final StorageType storOld = (StorageType) itrOld.next();
			final Iterator itrNew = storTypeCollNew.iterator();
			while (itrNew.hasNext())
			{
				final StorageType storNew = (StorageType) itrNew.next();
				if (storNew.getId().longValue() == storOld.getId().longValue()
						|| storNew.getId().longValue() == 1)
				{
					flag = 1;
					break;
				}
			}
			if (flag != 1)
			{
				return true;
			}

		}
		itrOld = spClassCollOld.iterator();
		while (itrOld.hasNext())
		{
			flag = 0;
			final String specimenOld = (String) itrOld.next();
			final Iterator itrNew = spClassCollNew.iterator();
			while (itrNew.hasNext())
			{
				final String specimenNew = (String) itrNew.next();
				if (specimenNew.equals(specimenOld))
				{
					flag = 1;
					break;
				}
			}
			if (flag != 1)
			{
				return true;
			}
		}
		itrOld = spArrayTypeCollOld.iterator();
		while (itrOld.hasNext())
		{
			flag = 0;
			final SpecimenArrayType spArrayTypeOld = (SpecimenArrayType) itrOld.next();

			final Iterator itrNew = spArrayTypeCollNew.iterator();
			while (itrNew.hasNext())
			{
				final SpecimenArrayType spArrayTypeNew = (SpecimenArrayType) itrNew.next();

				if (spArrayTypeNew.getId().longValue() == spArrayTypeOld.getId().longValue()
						|| spArrayTypeNew.getId().longValue() == 1)
				{
					flag = 1;
					break;
				}
			}
			if (flag != 1)
			{
				return true;
			}
		}

		return false;
	}
	/**
	 * this function checks weather parent of the container is valid or not
	 * according to restriction provided for the containers.
	 *
	 * @param container -
	 *            Container
	 * @param parent -
	 *            Parent Container
	 * @return boolean true indicating valid to use , false indicating not valid
	 *         to use.
	 * @throws BizLogicException throws BizLogicException
	 */
	public static boolean isParentContainerValidToUSe(StorageContainer container,
			StorageContainer parent) throws BizLogicException
	{

		final StorageType storageTypeAny = new StorageType();
		storageTypeAny.setId(Long.valueOf("1"));
		storageTypeAny.setName("All");
		if (parent.getHoldsStorageTypeCollection().contains(storageTypeAny))
		{
			return true;
		}
		if (!parent.getHoldsStorageTypeCollection().contains(container.getStorageType()))
		{
			return false;
		}
		return true;
	}
	/**
	 * @param dao - DAO object.
	 * @param container - StorageContainer object
	 * @param sessionDataBean - SessionDataBean object
	 * @return boolean value
	 * @throws BizLogicException throws BizLogicException
	 */
	public static boolean validateContainerAccess(DAO dao, StorageContainer container,
			SessionDataBean sessionDataBean) throws BizLogicException
	{
		boolean flag = true;
		logger.debug("validateContainerAccess..................");
		if (sessionDataBean != null && sessionDataBean.isAdmin())
		{
			return flag;
		}
		final Long userId = sessionDataBean.getUserId();
		Site site = null;
		Set<Long> loggedInUserSiteIdSet = null;
		site = new SiteBizLogic().getSite(dao, container.getId());
		loggedInUserSiteIdSet = new UserBizLogic().getRelatedSiteIds(userId);
		if (loggedInUserSiteIdSet != null && loggedInUserSiteIdSet.contains(Long.valueOf(site.getId())))
		{
			return flag;
		}
		else
		{
			flag = false;
			return false;
		}
	}
	/**
	 * @param parent - StorageContainer object.
	 * @param current - StorageContainer object
	 * @return boolean value
	 */
	public static boolean validatePosition(StorageContainer parent, StorageContainer current)
	{
		final int posOneCapacity = parent.getCapacity().getOneDimensionCapacity().intValue();
		final int posTwoCapacity = parent.getCapacity().getTwoDimensionCapacity().intValue();
		return validatePosition(posOneCapacity, posTwoCapacity, current);
	}

	/**
	 * @param posOneCapacity - integer.
	 * @param posTwoCapacity - integer
	 * @param current - StorageContainer object
	 * @return boolean value
	 */
	public static boolean validatePosition(int posOneCapacity, int posTwoCapacity,
			StorageContainer current)
	{
		final int positionDimensionOne = current.getLocatedAtPosition().getPositionDimensionOne()
				.intValue();
		final int positionDimensionTwo = current.getLocatedAtPosition().getPositionDimensionTwo()
				.intValue();
		logger.debug("validatePosition C : " + positionDimensionOne + " : "
				+ positionDimensionTwo);
		logger.debug("validatePosition P : " + posOneCapacity + " : " + posTwoCapacity);
		if ((positionDimensionOne > posOneCapacity) || (positionDimensionTwo > posTwoCapacity))
		{
			logger.debug("validatePosition false");
			return false;
		}
		logger.debug("validatePosition true");
		return true;
	}
	/**
	 * This method is to validate position based on parent container id.
	 * @param dao
	 *            Object DAO
	 * @param container
	 *            current container
	 * @return boolean value based on validation
	 * @throws BizLogicException
	 *             exception occure while DB handling
	 */
	public static boolean validatePosition(DAO dao, StorageContainer container) throws BizLogicException
	{
		try
		{
			final String sourceObjectName = StorageContainer.class.getName();
			final String[] selectColumnName = {"id", "capacity.oneDimensionCapacity",
					"capacity.twoDimensionCapacity"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(new EqualClause("id", container.getLocatedAtPosition()
					.getParentContainer().getId()));
			final List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
			Integer pcCapacityOne = 0;
			Integer pcCapacityTwo = 0;
			if (!list.isEmpty())
			{
				final Object[] obj1 = (Object[]) list.get(0);
				pcCapacityOne = (Integer) obj1[1];
				pcCapacityTwo = (Integer) obj1[2];
			}
			final int positionDimensionOne = container.getLocatedAtPosition()
					.getPositionDimensionOne().intValue();
			final int positionDimensionTwo = container.getLocatedAtPosition()
					.getPositionDimensionTwo().intValue();
			logger.debug("validatePosition C : " + positionDimensionOne + " : "
					+ positionDimensionTwo);
			logger.debug("validatePosition P : " + pcCapacityOne + " : " + pcCapacityTwo);
			if ((positionDimensionOne > pcCapacityOne) || (positionDimensionTwo > pcCapacityTwo))
			{
				logger.debug("validatePosition false");
				return false;
			}
			logger.debug("validatePosition true");
			return true;
		}
		catch (final DAOException daoExp)
		{
			logger.error(daoExp.getMessage(), daoExp);
			ErrorKey errorKey = ErrorKey.getErrorKey(daoExp.getErrorKeyName());
			throw new BizLogicException(errorKey, daoExp, daoExp.getMsgValues());
		}
	}

	/**
	 * @param dao - DAO object.
	 * @param containerIds - Long type array of containerIds
	 * @return boolean value based on container available for disabled
	 */
	public static boolean isContainerAvailableForDisabled(DAO dao, Long[] containerIds)
	{
		final List containerList = new ArrayList();
		if (containerIds.length != 0)
		{
			try
			{
				String sourceObjectName = Specimen.class.getName();
				final String[] selectColumnName = {"id"};
				final String[] whereColumnName1 = {"specimenPosition.storageContainer.id"};
				final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
				queryWhereClause.addCondition(new INClause("specimenPosition.storageContainer.id",
						containerIds));
				List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
				if (list.size() != 0)
				{
					return false;
				}
				else
				{
					sourceObjectName = SpecimenArray.class.getName();
					whereColumnName1[0] = "locatedAtPosition.parentContainer.id";

					final QueryWhereClause queryWhereClauseNew = new QueryWhereClause(
							sourceObjectName);
					queryWhereClauseNew.addCondition(new INClause(
							"locatedAtPosition.parentContainer.id", containerIds));
					list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClauseNew);
					if (list.size() != 0)
					{
						return false;
					}
				}
			}
			catch (final Exception e)
			{
				logger.error("Error in isContainerAvailable : " + e.getMessage(),e);
				return false;
			}
		}
		else
		{
			return true;
		}

		return isContainerAvailableForDisabled(dao, edu.wustl.common.util.Utility
				.toLongArray(containerList));
	}

	/**
	 * @param dao -DAO object.
	 * @param current - StorageContainer object
	 * @return boolean value  based on Container available for positions
	 */
	public static boolean isContainerAvailableForPositions(DAO dao, StorageContainer current)
	{
		try
		{
			String sourceObjectName = StorageContainer.class.getName();
			final String[] selectColumnName = {"id"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(
					new EqualClause("locatedAtPosition.positionDimensionOne", current
							.getLocatedAtPosition().getPositionDimensionOne())).andOpr()
					.addCondition(
							new EqualClause("locatedAtPosition.positionDimensionTwo", current
									.getLocatedAtPosition().getPositionDimensionTwo())).andOpr()
					.addCondition(
							new EqualClause("locatedAtPosition.parentContainer.id", current
									.getLocatedAtPosition().getParentContainer().getId()));

			List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
			logger.debug("current.getParentContainer() :"
					+ current.getLocatedAtPosition().getParentContainer());
			if (list.size() != 0)
			{
				final Object obj = list.get(0);
				logger
						.debug("**********IN isContainerAvailable : obj::::::: --------- " + obj);
				return false;
			}
			else
			{
				sourceObjectName = Specimen.class.getName();
				final QueryWhereClause queryWhereClauseNew = new QueryWhereClause(sourceObjectName);
				queryWhereClauseNew.addCondition(
						new EqualClause("specimenPosition.positionDimensionOne", current
								.getLocatedAtPosition().getPositionDimensionOne())).andOpr()
						.addCondition(
								new EqualClause("specimenPosition.positionDimensionTwo", current
										.getLocatedAtPosition().getPositionDimensionTwo()))
						.andOpr().addCondition(
								new EqualClause("specimenPosition.storageContainer.id", current
										.getLocatedAtPosition().getParentContainer().getId()));

				list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClauseNew);
				// check if Specimen exists with the given storageContainer
				// information
				if (list.size() != 0)
				{
					final Object obj = list.get(0);
					logger
							.debug("**************IN isPositionAvailable : obj::::::: --------------- "
									+ obj);
					return false;
				}
				else
				{
					sourceObjectName = SpecimenArray.class.getName();
					final QueryWhereClause queryWhereClauseInner = new QueryWhereClause(
							sourceObjectName);
					queryWhereClauseInner.addCondition(
							new EqualClause("locatedAtPosition.positionDimensionOne", current
									.getLocatedAtPosition().getPositionDimensionOne())).andOpr()
							.addCondition(
									new EqualClause("locatedAtPosition.positionDimensionTwo",
											current.getLocatedAtPosition()
													.getPositionDimensionTwo())).andOpr()
							.addCondition(
									new EqualClause("locatedAtPosition.parentContainer.id", current
											.getLocatedAtPosition().getParentContainer().getId()));

					list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClauseInner);
					if (list.size() != 0)
					{
						final Object obj = list.get(0);
						logger
								.debug("**************IN isPositionAvailable : obj::::::: --------------- "
										+ obj);
						return false;
					}
				}

			}
			return true;
		}
		catch (final Exception e)
		{
			logger.error("Error in isContainerAvailable : " + e.getMessage(),e);
			return false;
		}
	}

	/**
	 * @param storageContainer - StorageContainer object.
	 * @param posOne - String
	 * @param posTwo - String
	 * @return boolean value based on position validation
	 */
	public static boolean validatePosition(StorageContainer storageContainer, String posOne,
			String posTwo)
	{
		try
		{
			logger.debug("storageContainer.getCapacity().getOneDimensionCapacity() : "
					+ storageContainer.getCapacity().getOneDimensionCapacity());
			logger.debug("storageContainer.getCapacity().getTwoDimensionCapacity() : "
					+ storageContainer.getCapacity().getTwoDimensionCapacity());
			final int oneDimensionCapacity = (storageContainer.getCapacity()
					.getOneDimensionCapacity() != null ? storageContainer.getCapacity()
					.getOneDimensionCapacity().intValue() : -1);
			final int twoDimensionCapacity = (storageContainer.getCapacity()
					.getTwoDimensionCapacity() != null ? storageContainer.getCapacity()
					.getTwoDimensionCapacity().intValue() : -1);
			if (((oneDimensionCapacity) < Integer.parseInt(posOne))
					|| ((twoDimensionCapacity) < Integer.parseInt(posTwo)))
			{
				return false;
			}
			return true;
		}
		catch (final Exception e)
		{
			logger.error("Error in validatePosition : " + e.getMessage(),e);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param dao - DAO object.
	 * @param storageContainer - StorageContainer object
	 * @param posOne - String
	 * @param posTwo - String
	 * @param specimen - Specimen object
	 * @return boolean value based on position available.
	 */
	public static boolean isPositionAvailable(DAO dao, StorageContainer storageContainer,
			String posOne, String posTwo, Specimen specimen)
	{
		try
		{
			String sourceObjectName = Specimen.class.getName();
			final String[] selectColumnName = {"id"};
			final QueryWhereClause queryWhereClause = new QueryWhereClause(sourceObjectName);
			queryWhereClause.addCondition(
					new EqualClause("specimenPosition.positionDimensionOne", Integer
							.valueOf(posOne))).andOpr().addCondition(
					new EqualClause("specimenPosition.positionDimensionTwo", Integer
							.valueOf(posTwo))).andOpr().addCondition(
					new EqualClause("specimenPosition.storageContainer.id", storageContainer
							.getId()));

			List list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClause);
			logger.debug("storageContainer.getId() :" + storageContainer.getId());
			if (list.size() != 0)
			{
				final Object obj = list.get(0);
				boolean isPosAvail = false;
				if (specimen != null)
				{
					if (!(specimen.getLineage().equalsIgnoreCase("New"))
							&& ((Long) obj).longValue() == specimen.getId().longValue())
					{
						isPosAvail = true;
					}
				}
				logger
						.debug("**************IN isPositionAvailable : obj::::::: --------------- "
								+ obj);
				return isPosAvail;
			}
			else
			{
				sourceObjectName = StorageContainer.class.getName();

				final QueryWhereClause queryWhereClauseNew = new QueryWhereClause(sourceObjectName);
				queryWhereClauseNew.addCondition(
						new EqualClause("locatedAtPosition.positionDimensionOne", Integer
								.valueOf(posOne))).andOpr().addCondition(
						new EqualClause("locatedAtPosition.positionDimensionTwo", Integer
								.valueOf(posTwo))).andOpr().addCondition(
						new EqualClause("locatedAtPosition.parentContainer.id", storageContainer
								.getId()));

				list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClauseNew);

				logger.debug("storageContainer.getId() :" + storageContainer.getId());
				if (list.size() != 0)
				{
					final Object obj = list.get(0);
					logger.debug("**********IN isPositionAvailable : obj::::: --------- "
							+ obj);
					return false;
				}
				else
				{
					sourceObjectName = SpecimenArray.class.getName();
					final QueryWhereClause queryWhereClauseInner = new QueryWhereClause(
							sourceObjectName);
					queryWhereClauseInner.addCondition(
							new EqualClause("locatedAtPosition.positionDimensionOne", Integer
									.valueOf(posOne))).andOpr().addCondition(
							new EqualClause("locatedAtPosition.positionDimensionTwo", Integer
									.valueOf(posTwo))).andOpr().addCondition(
							new EqualClause("locatedAtPosition.parentContainer.id",
									storageContainer.getId()));

					list = dao.retrieve(sourceObjectName, selectColumnName, queryWhereClauseInner);

					logger.debug("storageContainer.getId() :" + storageContainer.getId());
					if (list.size() != 0)
					{
						final Object obj = list.get(0);
						logger.debug("**********IN isPositionAvailable : obj::::: --------- "
								+ obj);
						return false;
					}
				}
			}

			return true;

		}
		catch (final Exception e)
		{
			logger.error("Error in isPositionAvailable : " + e.getMessage(),e);
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * @param containerId Container Identifier
	 * @param containerName Container Name
	 * @return Boolean is Full
	 * @throws ApplicationException ApplicationException
	 */
	public static boolean isContainerFull(String containerId, String containerName)
		throws ApplicationException
	{
		final JDBCDAO jdbcDAO = AppUtility.openJDBCSession();
		final Long freePositions = getCountofFreeLocationOfContainer(jdbcDAO, containerId,
				containerName);
		AppUtility.closeJDBCSession(jdbcDAO);
		if (freePositions == 0)
		{
			return true;
		}
		else
		{
			return false;
		}

	}
	/**
	 * @param dao DAO Object
	 * @param storageContainerId Container identifier
	 * @param dimOne Position  one
	 * @param dimTwo Position  two
	 * @return Boolean
	 * @throws DAOException DAOException
	 */
	public static boolean canReduceDimension(DAO dao, Long storageContainerId, Integer dimOne,
		Integer dimTwo) throws DAOException
	{
		if (!isSpecimenAssignedWithinDimensions(dao, storageContainerId, dimOne))
		{
			if (!isContainerAssignedWithinDimensions(dao, storageContainerId, dimOne, dimTwo))
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * @param dao DAO Object
	 * @param storageContainerId Container identifier
	 * @param dimOne Position  one
	 * @param dimTwo Position  two
	 * @return Boolean
	 * @throws DAOException DAOException
	 */
	private static boolean isContainerAssignedWithinDimensions(DAO dao, Long storageContainerId,
		Integer dimOne, Integer dimTwo) throws DAOException
	{
		final StringBuilder contQuery = new StringBuilder();
		contQuery.append("from ContainerPosition cp ");
		contQuery.append("where cp.parentContainer.id =");
		contQuery.append(storageContainerId);
		contQuery.append(" and (cp.positionDimensionOne>");
		contQuery.append(dimOne);
		contQuery.append(" or cp.positionDimensionTwo>");
		contQuery.append(dimTwo);
		contQuery.append(")");
		final List contList = dao.executeQuery(contQuery.toString());
		if ((contList != null) && (contList.size() > 0))
		{
			return true;
		}
		return false;
	}
	/**
	 * @param dao DAO Object
	 * @param storageContainerId Container identifier
	 * @param dimOne Position  one
	 * @return Boolean
	 * @throws DAOException DAOException
	 */
	private static boolean isSpecimenAssignedWithinDimensions(DAO dao, Long storageContainerId,
		Integer dimOne) throws DAOException
	{
		final StringBuilder specQuery = new StringBuilder();
		specQuery.append("from SpecimenPosition sp ");
		specQuery.append("where sp.storageContainer.id =");
		specQuery.append(storageContainerId);
		specQuery.append(" and (sp.positionDimensionOne>");
		specQuery.append(dimOne);
		specQuery.append(" or sp.positionDimensionTwo>");
		specQuery.append(dimOne);
		specQuery.append(")");
		final List list = dao.executeQuery(specQuery.toString());
		if ((list != null) && (list.size() > 0))
		{
			return true;
		}
		return false;
	}

	/**
	 * @param contId container Id
	 * @param pos1 position 1
	 * @param pos2 position 2
	 * @param multipleSpecimen true/false
	 * @return List of objects
	 */
	public static List<Object> setparameterList(
	String contId, String pos1, String pos2, boolean multipleSpecimen)
	{
		List<Object> parameterList = new LinkedList<Object>();
		parameterList.add(contId);
		parameterList.add(pos1);
		parameterList.add(pos2);
		parameterList.add(multipleSpecimen);
		return parameterList;
	}
	 /**
	 * This method will populate Specimen Type collection.
	 * @param specimenClassTypeCollection Specimen Class Type Collection
	 * @param specimenTypeCollection Specimen Type Collection
	 * @param form StorageType/StorageContainer form
	 */
	public static void populateSpType(final Collection specimenClassTypeCollection,
			final Collection specimenTypeCollection, ISpecimenType form)
	{
		try
		{
			List<String> tissurList= AppUtility.getAllTissueSpType();
			List<String> cellList= AppUtility.getAllCellType();
			List<String> fluidList= AppUtility.getAllFluidSpType();
			List<String> molList= AppUtility.getAllMolecularType();

			String[] holdsTissueSpType = new String[tissurList.size()];
			String[] holdsCellSpType = new String[cellList.size()];
			String[] holdsFluidSpType = new String[fluidList.size()];
			String[] holdsMolSpType = new String[molList.size()];
			int tissueCount = 0;
			int fluidCount = 0;
			int cellCount = 0;
			int molCount = 0;
			final Iterator<String> iterator = specimenTypeCollection.iterator();
			while (iterator.hasNext())
			{
				final String specimenType = (String) iterator.next();
				if(Constants.NOT_SPECIFIED.equals(specimenType))
				{
					final Iterator<String> itera = specimenClassTypeCollection.iterator();
					while (itera.hasNext())
					{
						final String specimenClass = (String) itera.next();
						if(Constants.TISSUE.equals(specimenClass))
						{
							holdsTissueSpType[tissueCount] = specimenType;
							tissueCount++;
						}
						else if(Constants.FLUID.equals(specimenClass))
						{
							holdsFluidSpType[fluidCount] = specimenType;
							fluidCount++;
						}
						else if(Constants.CELL.equals(specimenClass))
						{
							holdsCellSpType[cellCount] = specimenType;
							cellCount++;
						}
						else if(Constants.MOLECULAR.equals(specimenClass))
						{
							holdsMolSpType[molCount] = specimenType;
							molCount++;
						}
					}
				}
				else if(tissurList.contains(specimenType))
				{
					holdsTissueSpType[tissueCount] = specimenType;
					tissueCount++;
				}
				else if(cellList.contains(specimenType))
				{
					holdsCellSpType[cellCount] = specimenType;
					cellCount++;
				}
				else if(fluidList.contains(specimenType))
				{
					holdsFluidSpType[fluidCount] = specimenType;
					fluidCount++;
				}
				else if(molList.contains(specimenType))
				{
					holdsMolSpType[molCount] = specimenType;
					molCount++;
				}
			}
			if(tissueCount==0)
			{
				holdsTissueSpType = null;
			}
			if(cellCount==0)
			{
				holdsCellSpType=null;
			}
			if(fluidCount==0)
			{
				holdsFluidSpType=null;
			}
			if (molCount==0)
			{
				holdsMolSpType = null;
			}
			form.setHoldsTissueSpType(holdsTissueSpType);
			form.setHoldsCellSpType(holdsCellSpType);
			form.setHoldsFluidSpType(holdsFluidSpType);
			form.setHoldsMolSpType(holdsMolSpType);
			form.setSpecimenOrArrayType("Specimen");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	 /**
	 * This method will populate Specimen Type collection based on classes selected by user.
	 * @param specimenClassTypeCollection Specimen Class Type Collection
	 * @param specimenTypeCollection Specimen Type Collection
	 * @param form StorageType/StorageContainer form
	 */
	public static void populateSpecimenType(StorageContainer container)
	{
		try
		{
			final Collection specimenClassTypeCollection = container.getHoldsSpecimenClassCollection();
			final Collection specimenTypeCollection = container.getHoldsSpecimenTypeCollection();
			Collection<String> spType = new HashSet<String>();
			if(specimenClassTypeCollection != null || !specimenClassTypeCollection.isEmpty())
			{
				final Iterator<String> itrSpClass = specimenClassTypeCollection.iterator();
				while (itrSpClass.hasNext())
				{
					final String specimenClass = (String) itrSpClass.next();
					if(Constants.TISSUE.equals(specimenClass))
					{
						List<String> tissueList= AppUtility.getAllTissueSpType();
						spType = setSpecimenType(tissueList, specimenTypeCollection, spType, AppUtility.getAllTissueSpType());
					}
					else if(Constants.CELL.equals(specimenClass))
					{
						List<String> cellList= AppUtility.getAllCellType();
						spType = setSpecimenType(cellList, specimenTypeCollection, spType, AppUtility.getAllCellType());
					}
					else if(Constants.FLUID.equals(specimenClass))
					{
						List<String> fluidList= AppUtility.getAllFluidSpType();
						spType = setSpecimenType(fluidList, specimenTypeCollection, spType, AppUtility.getAllFluidSpType());
					}
					else if(Constants.MOLECULAR.equals(specimenClass))
					{
						List<String> molList= AppUtility.getAllMolecularType();
						spType = setSpecimenType(molList, specimenTypeCollection, spType, AppUtility.getAllMolecularType());
					}
				}
				if(!spType.isEmpty())
				{
					spType.addAll(container.getHoldsSpecimenTypeCollection());
					container.setHoldsSpecimenTypeCollection(spType);
				}
			}
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	private static Collection<String> setSpecimenType(List<String> specimenTypeList, Collection specimenTypeCollection,
			Collection<String> spType, List<String> newSpecimenTypeList)
	{
		specimenTypeList.retainAll(specimenTypeCollection);
		if(specimenTypeList.isEmpty())
		{
			spType.addAll(newSpecimenTypeList);
		}
		return spType;
	}

	/**
	 * Set Specimen Type List.
	 * @param request HttpServletRequest
	 * @param spType ISpecimenType
	 */
	public static void setSpTypeList(HttpServletRequest request,
			final ISpecimenType spType)
	{
		List<NameValueBean> tissueList = setTissueList(spType);
		List<NameValueBean> cellList = setCellList(spType);
		List<NameValueBean> fluidList = setFluidList(spType);
		List<NameValueBean> molList = setMolList(spType);
		request.setAttribute(Constants.TISSUE_SPECIMEN, tissueList);
		request.setAttribute(Constants.CELL_SPECIMEN, cellList);
		request.setAttribute(Constants.FLUID_SPECIMEN, fluidList);
		request.setAttribute(Constants.MOLECULAR_SPECIMEN, molList);
	}
	/**
	 * @param spType ISpecimenType
	 * @return Molecular list
	 */
	private static List<NameValueBean> setMolList(final ISpecimenType spType)
	{
		List<NameValueBean> molList = new ArrayList<NameValueBean>();
		if(spType.getHoldsMolSpType()!=null)
		{
			final String[] molType = spType.getHoldsMolSpType();
			for (final String element : molType)
			{
				if(element!=null && !element.equals(""))
				{
					molList.add(new NameValueBean(element,element));
				}
			}
		}
		return molList;
	}
	/**
	 * @param spType ISpecimenType
	 * @return FluidList
	 */
	private static List<NameValueBean> setFluidList(final ISpecimenType spType)
	{
		List<NameValueBean> fluidList = new ArrayList<NameValueBean>();
		if(spType.getHoldsFluidSpType()!=null)
		{
			final String[] fluidType = spType.getHoldsFluidSpType();
			for (final String element : fluidType)
			{
				if(element!=null && !element.equals(""))
				{
					fluidList.add(new NameValueBean(element,element));
				}
			}
		}
		return fluidList;
	}
	/**
	 * @param spType ISpecimenType
	 * @return CellList
	 */
	private static List<NameValueBean> setCellList(final ISpecimenType spType)
	{
		List<NameValueBean> cellList = new ArrayList<NameValueBean>();
		if(spType.getHoldsCellSpType()!=null)
		{
			final String[] cellType = spType.getHoldsCellSpType();
			for (final String element : cellType)
			{
				if(element!=null && !element.equals(""))
				{
					cellList.add(new NameValueBean(element,element));
				}
			}
		}
		return cellList;
	}
	/**
	 * @param spType ISpecimenType
	 * @return TissueList
	 */
	private static List<NameValueBean> setTissueList(final ISpecimenType spType)
	{
		List<NameValueBean> tissueList = new ArrayList<NameValueBean>();
		if(spType.getHoldsTissueSpType()!=null)
		{
			final String[] tissueType = spType.getHoldsTissueSpType();
			for (final String element : tissueType)
			{
				if(element!=null && !element.equals(""))
				{
					tissueList.add(new NameValueBean(element,element));
				}
			}
		}
		return tissueList;
	}

	/**
	 * @param spForm Storage Type Form
	 * @param spDomain ISpecimenTypeDomain
	 * @throws ApplicationException ApplicationException
	 */
	public static void setSpTypeColl(final ISpecimenType spForm, final ISpecimenTypeDomain spDomain)
	throws ApplicationException
	{
		List<String[]> spTypeList = new ArrayList<String[]>();
		String[] tissueSpeTypeArr = spForm.getHoldsTissueSpType();
		String[] cellSpTypeArr = spForm.getHoldsCellSpType();
		String[] fluidSpeTypeArr = spForm.getHoldsFluidSpType();
		String[] molSpTypeArr = spForm.getHoldsMolSpType();
		Collection<String> spType = new HashSet<String>();
		Collection<String> spClassCollection = spDomain.getHoldsSpecimenClassCollection();
		setTissueSp(tissueSpeTypeArr, spType, spClassCollection);
		setCellSp(cellSpTypeArr, spType, spClassCollection);
		setFluidSp(fluidSpeTypeArr, spType, spClassCollection);
		setMolSp(molSpTypeArr, spType, spClassCollection);
		spTypeList.add(tissueSpeTypeArr);
		spTypeList.add(cellSpTypeArr);
		spTypeList.add(fluidSpeTypeArr);
		spTypeList.add(molSpTypeArr);
		setAllSpType(spClassCollection, spType, spTypeList);
		spDomain.setHoldsSpecimenTypeCollection(spType);
		spDomain.setHoldsSpecimenClassCollection(spClassCollection);
	}

	/**
	 * @param molSpTypeArr molSpTypeArr
	 * @param spDomain ISpecimenTypeDomain
	 */
	private static void setMolSp(final String[] molSpTypeArr,
			Collection<String> molType,
			Collection<String> spClassCollection)
	{
		if (molSpTypeArr != null)
		{
			for (final String element : molSpTypeArr)
			{
				logger.debug("type Id :" + element);
				molType.add(element);
			}
			spClassCollection.add(Constants.MOLECULAR);
		}
	}
	/**
	 * @param fluidSpeTypeArr fluidSpeTypeArr
	 * @param spDomain ISpecimenTypeDomain
	 */
	private static void setFluidSp(final String[] fluidSpeTypeArr,
			Collection<String> fluidSpType,
			Collection<String> spClassCollection)
	{
		if (fluidSpeTypeArr != null)
		{
			for (final String element : fluidSpeTypeArr)
			{
				logger.debug("type Id :" + element);
				fluidSpType.add(element);
			}
			spClassCollection.add(Constants.FLUID);
		}
	}
	/**
	 * @param cellSpTypeArr cellSpTypeArr
	 * @param spDomain ISpecimenTypeDomain
	 */
	private static void setCellSp(final String[] cellSpTypeArr,
			Collection<String> cellType,
			Collection<String> spClassCollection)
	{
		if (cellSpTypeArr != null)
		{
			for (final String element : cellSpTypeArr)
			{
				logger.debug("type Id :" + element);
				cellType.add(element);
			}
			spClassCollection.add(Constants.CELL);
		}
	}
	/**
	 * @param tissueSpeTypeArr tissueSpeTypeArr
	 * @param spDomain ISpecimenTypeDomain
	 */
	private static void setTissueSp(final String[] tissueSpeTypeArr,
			Collection<String> tissueSpType,
			Collection<String> spClassCollection)
	{
		if (tissueSpeTypeArr != null)
		{
			for (final String element : tissueSpeTypeArr)
			{
				logger.debug("type Id :" + element);
				tissueSpType.add(element);
			}
			spClassCollection.add(Constants.TISSUE);
		}
	}
	/**
	 * @param spDomain ISpecimenTypeDomain
	 * @param spForm ISpecimenType
	 * @throws ApplicationException ApplicationException
	 */
	private static void setAllSpType(Collection<String> spClassCollection,
			Collection<String> spType, List<String[]> spTypeList)
			throws ApplicationException
	{
		final String[] tissueSpeTypeArr = spTypeList.get(0);
		final String[] cellSpTypeArr 	= spTypeList.get(1);
		final String[] fluidSpeTypeArr 	= spTypeList.get(2);
		final String[] molSpTypeArr 	= spTypeList.get(3);
		Iterator<String> classItr= spClassCollection.iterator();
		while(classItr.hasNext())
		{
			String className = classItr.next();
			setAllTissueSp(tissueSpeTypeArr, className, spType);
			setAllCellSp(cellSpTypeArr, className, spType);
			setAllFluidSp(fluidSpeTypeArr, className, spType);
			setAllMolSp(molSpTypeArr, className, spType);
		}
	}
	/**
	 * @param molSpTypeArr molSpTypeArr
	 * @param className Tissue, Cell, Fluid and Molecular
	 * @param spDomain ISpecimenTypeDomain
	 * @throws ApplicationException ApplicationException
	 */
	private static void setAllMolSp(final String[] molSpTypeArr,
			String className, Collection<String> spType)
			throws ApplicationException
	{
		if(Constants.MOLECULAR.equals(className) && molSpTypeArr==null)
		{
			spType.addAll(AppUtility.getAllMolecularType());
		}
	}
	/**
	 * @param fluidSpeTypeArr fluidSpeTypeArr
	 * @param className Tissue, Cell, Fluid and Molecular
	 * @param spDomain ISpecimenTypeDomain
	 * @throws ApplicationException ApplicationException
	 */
	private static void setAllFluidSp(final String[] fluidSpeTypeArr,
			String className, Collection<String> spType)
			throws ApplicationException
	{
		if(Constants.FLUID.equals(className) && fluidSpeTypeArr==null)
		{
			spType.addAll(AppUtility.getAllFluidSpType());
		}
	}
	/**
	 * @param cellSpTypeArr cellSpTypeArr
	 * @param className Tissue, Cell, Fluid and Molecular
	 * @param spDomain ISpecimenTypeDomain
	 * @throws ApplicationException ApplicationException
	 */
	private static void setAllCellSp(final String[] cellSpTypeArr,
			String className, Collection<String> spType)
			throws ApplicationException
	{
		if(Constants.CELL.equals(className)&& cellSpTypeArr==null)
		{
			spType.addAll(AppUtility.getAllCellType());
		}
	}
	/**
	 * @param tissueSpeTypeArr tissueSpeTypeArr
	 * @param className Tissue, Cell, Fluid and Molecular
	 * @param spDomain ISpecimenTypeDomain
	 * @throws ApplicationException ApplicationException
	 */
	private static void setAllTissueSp(final String[] tissueSpeTypeArr,
			String className, Collection<String> spType)
			throws ApplicationException
	{
		if(Constants.TISSUE.equals(className) && tissueSpeTypeArr==null)
		{
			spType.addAll(AppUtility.getAllTissueSpType());
		}
	}
}