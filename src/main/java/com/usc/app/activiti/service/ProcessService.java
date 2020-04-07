package com.usc.app.activiti.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface ProcessService {

	Object getProcdefProcess();

	Object getProcdefProcessByProcdefId(String queryParam);

	Object deleteByDeploymentId(String deploymentId);

	Object suspension(String id);

	Object activation(String id);

	void getProcessPicture(String id, HttpServletResponse response) throws IOException;

	Object startProcess(String queryParam) throws IOException;

	Object getRunProcess();

	void getActivityPng(String processInstanceId, HttpServletResponse response) throws IOException;

	Object getProcessReverseList(String queryParam) throws IOException;

	Object getProcessSubList(String queryParam) throws Exception;

	Object endProcess(String queryParam) throws IOException;

	Object getEndProcess();

	Object deleteProcess(String queryParam) throws IOException;

}
