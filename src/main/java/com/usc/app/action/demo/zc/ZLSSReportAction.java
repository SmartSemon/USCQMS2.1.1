package com.usc.app.action.demo.zc;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.usc.app.action.a.AbstractAction;
import com.usc.server.DBConnecter;

public class ZLSSReportAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		String sql = "SELECT A.QPDESCRIPTION,A.REPUNIT,A.REPORTER_TIME,A.REPORTER,A.CARMODEL,A.PARTNAME,A.DIESELCODE,A.DIESELMODELS,A.NUM,A.PRODUCTNO,A.CTCODE,A.PJUSER,B.TRANCOST,B.MANAGECOST,B.CONSCOST,B.APSCOST,B.TESTFEE,B.MEETCOST,B.RECIEWCOST,B.TRAVELCOST,\r\n"
				+ "	B.SAMPLECOST,B.OTHERCOST,B.SEGMENTCLASS,b.RECORDNO,B.LOSSWKHOURS,B.LABORCOST,B.MATECOST,B.FUELCOST,B.TESTCOST,B.REPUNIT,B.TOTALCOST,B.SCOPE,B.TYPECODE,B.RCODE\r\n"
				+ "FROM UNQUALIFIED A,MASSLOSS B ,REL_MASSLOSS_OBJ C WHERE  A.ID = C.ITEMAID AND B.ID = C.ITEMBID";
		List<Map<String, Object>> dataList = DBConnecter.getJdbcTemplate().queryForList(sql);
		Map<String, Object> ret = new JSONObject();
		ret.put("flag", true);
		ret.put("dataList", dataList);
		return ret;
	}

	@Override
	public boolean disable() throws Exception
	{
		return false;
	}

}
