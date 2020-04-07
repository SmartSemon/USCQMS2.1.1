package com.usc.app.activiti.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.activiti.ActCommonUtil;
import com.usc.app.activiti.service.ProcessTaskService;
import com.usc.app.bs.service.impl.BaseService;
import com.usc.app.util.Utils;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("processTaskService")
public class ProcessTaskServiceImpl extends BaseService implements ProcessTaskService {

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Override
    public Object getTaskToDo(String queryParam) throws IOException {
        Map<String, Object> param = JSON.parseObject(queryParam);
        //获取登陆人任务列表根据创建时间降序排序
        List<Task> taskList = taskService.createTaskQuery().taskAssignee((String) param.get("userName"))
                .orderByTaskCreateTime().desc().list();
        List<Map<String, Object>> list = new ArrayList<>();
        if (taskList != null && taskList.size() > 0) {
            for (Task task : taskList) {
                Map<String, Object> dto = new HashMap<>();
                //id(act_run_task表)
                dto.put("id", task.getId());
                //根据proc_inst_id获取act_hi_procinst中的发起人和发起时间与流程实例名称
                HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(task.getProcessInstanceId()).singleResult();
                //流程实例id(方便获取进度)
                dto.put("processInstanceId", task.getProcessInstanceId());
                //流程标题（发起人+发起时间+流程实例名称）
                dto.put("title", hpi.getStartUserId() + "在" + Utils.dateToString(hpi.getStartTime()) + "发起" + hpi.getProcessDefinitionName());
                //流程名称
                dto.put("name", hpi.getProcessDefinitionName());
                //当前环节
                dto.put("link", task.getName());
                //发起人
                dto.put("startName", hpi.getStartUserId());
                //创建时间
                dto.put("createTime", Utils.dateToString(task.getCreateTime()));
                list.add(dto);
            }
        }
        return new ActionMessage(true, null, "查询成功", list);
    }

    @Override
    public Object handle(String taskId, String queryParam) throws IOException {
        Map<String, Object> param = JSON.parseObject(queryParam);
        //添加意见  （param.getString("processInstanceId"):流程实例ID，如果为空，则不保存任务提交意见）
        if (StringUtils.isNotBlank((String) param.get("processInstanceId")) && StringUtils.isNotBlank((String) param.get("options"))) {
            taskService.addComment(taskId, (String) param.get("processInstanceId"), (String) param.get("options"));
        }
        Boolean isEnd = ActCommonUtil.nextTaskIsEnd(taskId);
        Boolean isExclusiveGateway = ActCommonUtil.nextTaskIsExclusiveGateway(taskId);
        if (isEnd) {
            boolean isRestore = ActCommonUtil.restore((String) param.get("processInstanceId"), (String) param.get("userName"),
                    "F", (String) param.get("processInstanceId"), null);
            if (isRestore) {
                taskService.complete(taskId);
                return new ActionMessage(true, null, "办理成功");
            } else {
                return new ActionMessage(false, null, "办理失败");
            }
        } else if (isExclusiveGateway) {
            //对应排他网关判断输入建议值判断选择分支
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("options", param.get("options"));
            try {
                taskService.complete(taskId, variables);
            } catch (Exception e) {
                System.err.println("排他网关判断之输入不正确！");
                return new ActionMessage(false, null, "办理失败");
            }
            //执行排他网关监听选择分支
//            taskService.complete(taskId);
            return new ActionMessage(true, null, "办理成功");
        } else {
            //完成任务
            taskService.complete(taskId);
            return new ActionMessage(true, null, "办理成功");
        }
    }

    @Override
    public Object taskTransfer(String taskId, String queryParam) throws IOException {
        Map<String, Object> param = JSON.parseObject(queryParam);
        try {
            taskService.setAssignee(taskId, (String) param.get("name"));
            return new ActionMessage(true, null, "转办成功");
        } catch (Exception e) {
            return new ActionMessage(false, null, "转办失败");
        }
    }

    @Override
    public Object reject(String taskId, String queryParam) throws IOException {
        Map<String, Object> param = JSON.parseObject(queryParam);
        //获取当前任务
        HistoricTaskInstance currTask = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();
        //获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(currTask.getProcessInstanceId())
                .singleResult();
        //获取流程定义
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(currTask.getProcessDefinitionId());
        if (processDefinitionEntity == null) {
            return new ActionMessage(false, null, "驳回失败");
        }
        //获取当前activity
        ActivityImpl currActivity = ((ProcessDefinitionImpl) processDefinitionEntity)
                .findActivity(currTask.getTaskDefinitionKey());
        //获取当前任务流入
        List<PvmTransition> histTransitionList = currActivity
                .getIncomingTransitions();
        //清除当前活动出口
        List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();
        List<PvmTransition> originPvmTransitionList = new ArrayList<PvmTransition>(pvmTransitionList);
        pvmTransitionList.clear();
        //查找上一个user task节点
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId())
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc().list();
        TransitionImpl transitionImpl = null;
        if (historicActivityInstances.size() > 0) {
            ActivityImpl lastActivity = ((ProcessDefinitionImpl) processDefinitionEntity)
                    .findActivity(historicActivityInstances.get(0).getActivityId());
            //创建当前任务的新出口
            transitionImpl = currActivity.createOutgoingTransition(lastActivity.getId());
            transitionImpl.setDestination(lastActivity);
        } else {
            return new ActionMessage(false, null, "驳回失败");
        }
        // 完成任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
        for (Task task : tasks) {
            //添加意见  （param.getString("processInstanceId"):流程实例ID，如果为空，则不保存任务提交意见）
            if (StringUtils.isNotBlank((String) param.get("processInstanceId")) && StringUtils.isNotBlank((String) param.get("options"))) {
                taskService.addComment(taskId, (String) param.get("processInstanceId"), (String) param.get("options"));
            }
            taskService.complete(task.getId());
        }
        // 恢复方向
        currActivity.getOutgoingTransitions().remove(transitionImpl);
        pvmTransitionList.addAll(originPvmTransitionList);
        return new ActionMessage(true, null, "驳回成功");
    }

    @Override
    public Object getTaskDone(String queryParam) throws IOException {
        Map<String, Object> param = JSON.parseObject(queryParam);
        List<HistoricTaskInstance> histTaskList = historyService.createHistoricTaskInstanceQuery().taskAssignee((String) param.get("userName")).finished()
                .orderByHistoricTaskInstanceEndTime().desc().list();
        List<Map<String, Object>> list = new ArrayList<>();
        if (histTaskList != null && histTaskList.size() > 0) {
            for (HistoricTaskInstance historicTask : histTaskList) {
                Map<String, Object> dto = new HashMap<>();
                //id(act_run_task表)
                dto.put("id", historicTask.getId());
                //根据proc_inst_id获取act_hi_procinst中的发起人和发起时间与流程实例名称
                HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(historicTask.getProcessInstanceId()).singleResult();
                //流程实例id(方便获取进度)
                dto.put("processInstanceId", historicTask.getProcessInstanceId());
                //流程标题（发起人+发起时间+流程实例名称）
                dto.put("title", hpi.getStartUserId() + "在" + Utils.dateToString(hpi.getStartTime()) + "发起" + hpi.getProcessDefinitionName());
                //流程名称
                dto.put("name", hpi.getProcessDefinitionName());
                //当前环节
                dto.put("link", historicTask.getName());
                //发起人
                dto.put("startName", hpi.getStartUserId());
                //办理时间
                dto.put("endTime", Utils.dateToString(historicTask.getEndTime()));
                list.add(dto);
            }
        }
        return new ActionMessage(true, null, "查询成功", list);
    }

    @Override
    public Object getMyProcess(String queryParam) throws IOException {
        Map<String, Object> param = JSON.parseObject(queryParam);
        // 根据用户查询所有流程实例，(act_hi_procinst表)，根据开始时间降序排序
        List<HistoricProcessInstance> hpiList = historyService.createHistoricProcessInstanceQuery().startedBy((String) param.get("userName"))
                .orderByProcessInstanceStartTime().desc().list();
        List<Map<String, Object>> list = new ArrayList<>();
        if (hpiList != null && hpiList.size() > 0) {
            //遍历流程实例根据流程实例获取最新流程任务(未完成的任务)
            for (HistoricProcessInstance hp : hpiList) {
                ///先判断流程是否结束
                ProcessInstance singleResult = runtimeService.createProcessInstanceQuery().processInstanceId(hp.getId()).singleResult();
                if (singleResult == null) {
                    // 没结束根据流程实例ID获取任务
                    List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
                            .processInstanceId(hp.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                    Map<String, Object> endDto = new HashMap<>();
                    //id(act_hi_actinst表)
                    endDto.put("id", htiList.get(0).getId());
                    //流程实例ID(方便对活动节点进行操作)
                    endDto.put("processInstanceId", htiList.get(0).getProcessInstanceId());
                    //流程标题（发起人+发起时间+流程实例名称）
                    endDto.put("title", hp.getStartUserId() + "在" + Utils.dateToString(hp.getStartTime()) + "发起" + hp.getProcessDefinitionName());
                    //流程名称
                    endDto.put("name", hp.getProcessDefinitionName());
                    endDto.put("state", htiList.get(0).getDeleteReason());
                    endDto.put("startName", hp.getStartUserId());
                    list.add(endDto);
                } else {
                    // 已结束根据流程实例ID获取任务
                    HistoricTaskInstance hti = historyService.createHistoricTaskInstanceQuery().processInstanceId(singleResult.getId()).unfinished().singleResult();
                    Map<String, Object> runDto = new HashMap<>();
                    //id(act_hi_actinst表)
                    runDto.put("id", hti.getId());
                    //流程实例ID(方便对活动节点进行操作)
                    runDto.put("processInstanceId", hti.getProcessInstanceId());
                    //流程标题（发起人+发起时间+流程实例名称）
                    runDto.put("title", hp.getStartUserId() + "在" + Utils.dateToString(hp.getStartTime()) + "发起" + hp.getProcessDefinitionName());
                    //流程名称
                    runDto.put("name", hp.getProcessDefinitionName());
                    HistoricActivityInstance hai = historyService.createHistoricActivityInstanceQuery()
                            .processInstanceId(hti.getProcessInstanceId()).unfinished().singleResult();
                    runDto.put("state", "[进行中]:" + hai.getActivityName());
                    //发起人
                    runDto.put("startName", hp.getStartUserId());
                    list.add(runDto);
                }
            }
        }
        return new ActionMessage(true, null, "查询成功", list);
    }

    @Override
    public Object processRevoke(String queryParam) throws IOException {
        Map<String, Object> param = JSON.parseObject(queryParam);
        //还原流程绑定的数据是否成功
        boolean isRestore = ActCommonUtil.restore((String) param.get("processInstanceId"), (String) param.get("userName"), "C", "", null);
        if (isRestore) {
            try {
                runtimeService.deleteProcessInstance((String) param.get("processInstanceId"), "[用户撤销]");
                return new ActionMessage(true, null, "撤销成功");
            } catch (Exception e) {
                return new ActionMessage(false, null, "撤销失败");
            }
        } else {
            return new ActionMessage(false, null, "撤销失败");
        }

    }
}
