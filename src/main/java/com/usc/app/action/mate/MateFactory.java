package com.usc.app.action.mate;

import java.util.concurrent.ConcurrentHashMap;

import com.usc.server.md.GlobalGrid;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.ModelClassView;
import com.usc.server.md.ModelQueryView;
import com.usc.server.md.ModelRelationShip;
import com.usc.server.md.USCModelMate;

public class MateFactory
{
	private static ConcurrentHashMap<String, ItemInfo> itemInfoMap = new ConcurrentHashMap<String, ItemInfo>();
	private static ConcurrentHashMap<String, ModelRelationShip> shipMap = new ConcurrentHashMap<String, ModelRelationShip>();
	private static ConcurrentHashMap<String, ModelQueryView> queryView = new ConcurrentHashMap<String, ModelQueryView>();
	private static ConcurrentHashMap<String, ModelClassView> classView = new ConcurrentHashMap<String, ModelClassView>();
	private static ConcurrentHashMap<String, GlobalGrid> globalGrid = new ConcurrentHashMap<String, GlobalGrid>();

	public static ItemInfo getItemInfo(String itemNo)
	{
		if (itemNo == null)
		{
			return null;
		}
		ItemInfo info = itemInfoMap.get(itemNo.toUpperCase());
		if (info != null)
		{
			return info;
		} else
		{
			info = USCModelMate.getItemInfo(itemNo.toUpperCase());
		}
		if (info != null)
		{
			putItemInfo(info);
		}
		return info;
	}

	public static void putItemInfo(ItemInfo info)
	{
		itemInfoMap.putIfAbsent(info.getItemNo(), info);
	}

	public static ModelRelationShip getRelationShip(String shipNo)
	{
		ModelRelationShip ship = shipMap.get(shipNo.toUpperCase());
		if (ship != null)
		{
			return ship;
		} else
		{
			ship = USCModelMate.getRelationShipInfo(shipNo.toUpperCase());
		}
		if (ship != null)
		{
			putRelationShip(ship);
		}
		return ship;
	}

	public static void putRelationShip(ModelRelationShip ship)
	{
		shipMap.putIfAbsent(ship.getNo(), ship);
	}

	public static ModelQueryView getQueryView(String viewNo)
	{
		ModelQueryView view = queryView.get(viewNo.toUpperCase());
		if (view != null)
		{
			return view;
		} else
		{
			view = USCModelMate.getModelQueryViewInfo(viewNo.toUpperCase());

		}
		if (view != null)
		{
			putQueryView(view);
		}
		return view;
	}

	public static void putQueryView(ModelQueryView view)
	{
		queryView.putIfAbsent(view.getNo(), view);
	}

	public static ModelClassView getClassView(String viewNo)
	{
		ModelClassView view = classView.get(viewNo.toUpperCase());
		if (view != null)
		{
			return view;
		} else
		{
			view = USCModelMate.getModelClassViewInfo(viewNo.toUpperCase());
		}
		if (view != null)
		{
			putClassView(view);
		}
		return view;
	}

	public static void putClassView(ModelClassView view)
	{
		classView.putIfAbsent(view.getNo(), view);
	}

	public static GlobalGrid getGlobalGrid(String gridNo)
	{
		GlobalGrid grid = globalGrid.get(gridNo);
		if (grid != null)
		{
			return grid;
		} else
		{
			grid = USCModelMate.getGlobalGrid(gridNo);
		}
		if (grid != null)
		{
			putGlobalGrid(grid);
		}
		return grid;
	}

	private static void putGlobalGrid(GlobalGrid grid)
	{
		globalGrid.putIfAbsent(grid.getNo(), grid);
	}

	public static void clear()
	{
		itemInfoMap.clear();
		shipMap.clear();
		queryView.clear();
		classView.clear();
	}

	public static void clearItemInfo()
	{
		itemInfoMap.clear();
	}

	public static void clearRelationShip()
	{
		shipMap.clear();
	}

	public static void clearQueryView()
	{
		queryView.clear();
	}

	public static void clearClassView()
	{
		classView.clear();
	}

	public static void remove(String key)
	{
		itemInfoMap.remove(key);
		shipMap.remove(key);
		queryView.remove(key);
		classView.remove(key);
	}

	public static void removeItemInfoCache(String key)
	{
		itemInfoMap.remove(key);
	}

	public static void removeRelationShipCache(String key)
	{
		shipMap.remove(key);
	}

	public static void removClassViewCache(String key)
	{
		queryView.remove(key);
	}

	public static void removQueryViewCache(String key)
	{
		classView.remove(key);
	}

	public static void remoGlobalGridCache(String key)
	{
		globalGrid.remove(key);
	}
}
