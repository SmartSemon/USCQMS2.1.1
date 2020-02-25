package com.usc.app._log.service;

import java.util.Map;

public interface LogInOrOutService
{

	public Map<String, Object> Login(Map<String, Object> parameter);

	public Map<String, Object> Logout(Map<String, Object> parameter);
}
