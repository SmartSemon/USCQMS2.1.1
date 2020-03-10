package com.usc.app.action.a;

import com.usc.app.action.i.AppAction;
import com.usc.app.query.rt.QueryReturnRequest;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;

public abstract class AbstractAction implements AppAction, QueryReturnRequest {

	protected ApplicationContext context = null;
	private String actionParam = null;

	@Override
	public Object action() throws Exception {
		if (CheckUserConnections() && isCommandEnvironmentEnable(getApplicationContext()))
		{ return executeAction(); }
		return null;
	}

	@Override
	public boolean isEnabled() throws Exception {
		return disable();
	}

	/**
	 * <p>
	 * Note: Specific transaction operations
	 *
	 * @return
	 * @throws Exception
	 */
	public abstract Object executeAction() throws Exception;

	/**
	 * <p>
	 * Note: Controlling the validity of transactions
	 *
	 * @return
	 * @throws Exception
	 */
	public abstract boolean disable() throws Exception;

	@Override
	public void setActionParam(String param) throws Exception {
		this.actionParam = param;
	}

	@Override
	public String getActionParam() throws Exception {
		return actionParam;
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return this.context;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

	public USCObject getSelectedObj() {
		return context.getSelectedObj();
	}

	@Override
	public boolean isCommandEnvironmentEnable(ApplicationContext context) {
		return true;
	}

	public boolean CheckUserConnections() {
		return true;
	}

}
