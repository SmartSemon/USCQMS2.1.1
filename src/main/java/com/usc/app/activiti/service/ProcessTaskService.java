package com.usc.app.activiti.service;

import java.io.IOException;

public interface ProcessTaskService {

    Object getTaskToDo(String queryParam) throws IOException;

    Object handle(String taskId, String queryParam) throws IOException;

    Object taskTransfer(String taskId, String queryParam) throws IOException;

    Object reject(String taskId, String queryParam) throws IOException;

    Object getTaskDone(String queryParam) throws IOException;

    Object getMyProcess(String queryParam) throws IOException;

    Object processRevoke(String queryParam) throws IOException;
}
