package com.usc.test.mate.action.service;

import javax.servlet.http.HttpServletRequest;

public interface ModelServer {
	boolean isModelingUser(String userName);

	Object openModel(String param);

	Object closeModel(String param, HttpServletRequest httpServletRequest);

	Object upgradeModel(String param, HttpServletRequest httpServletRequest);

	Object cancelUpgradeModel(String param, HttpServletRequest httpServletRequest);
}
