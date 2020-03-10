package com.usc.app._log.service.impl;

import java.util.List;
import java.util.Map;

import com.usc.autho.UserAuthority;
import com.usc.server.DBConnecter;
import com.usc.server.jdbc.SDBUtils;
import com.usc.util.ObjectHelperUtils;

public class Navigation {

	public static List<Map<String, Object>> getAuthorized(String userName) {
		StringBuffer sql = new StringBuffer("del=0 AND state='F' ");
		Object[] objects = null;
		if (!UserAuthority.getSuperUsers().contains(userName))
		{
			List<String> authIDS = SDBUtils.getModuleAuthed(userName);
			if (!ObjectHelperUtils.isEmpty(authIDS))
			{
				sql.append(" AND id IN(");
				objects = new Object[authIDS.size()];
				for (int i = 0; i < authIDS.size(); i++)
				{
					if (i > 0)
					{ sql.append(","); }
					sql.append("?");
					Object id = authIDS.get(i);
					objects[i] = id;
				}
				sql.append(")");
			} else
			{
				return null;
			}
		}
		sql.append(" ORDER BY sort,ctime");
		return DBConnecter.getJdbcTemplate().queryForList("SELECT * FROM USC_MODEL_NAVIGATION WHERE " + sql.toString(),
				objects);
	}

}
