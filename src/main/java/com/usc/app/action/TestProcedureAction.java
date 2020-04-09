package com.usc.app.action;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.server.DBConnecter;
import com.usc.server.jdbc.procedure.USCCallableStatementCreator;

public class TestProcedureAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception {
		USCCallableStatementCreator csc = new USCCallableStatementCreator("TEST_PRO", new Object[] { 1234567 },
				Types.INTEGER);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> bathes = (List<Map<String, Object>>) DBConnecter.getJdbcTemplate().execute(csc,
				new CallableStatementCallback<Object>() {

					@Override
					public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
						cs.execute();
						return cs.getInt(2);
					}
				});
		if (bathes != null)
		{
			for (Map<String, Object> map : bathes)
			{ System.out.println(map.get("ID") + ":" + map.get("NO")); }
		}
		return new ActionMessage(true, RetSignEnum.NOT_DEAL_WITH, String.valueOf(bathes), null);
	}

	@Override
	public boolean disable() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
