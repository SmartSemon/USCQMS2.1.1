package com.usc.app.activiti.resource;

import com.usc.app.activiti.service.ProcessTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

/**
 * @author lwp
 */
@RestController
@RequestMapping(value = "/act/task", produces = "application/json;charset=UTF-8")
public class ProcessTaskResource {

    @Autowired
    ProcessTaskService processTaskService;

    /**
     * 获取登陆人待办理任务
     *
     * @param queryParam（userName） 用户名
     * @return 任务集合
     */
    @PostMapping("getTaskToDo")
    public Object getTaskToDo(@RequestBody String queryParam) throws IOException {
        return processTaskService.getTaskToDo(queryParam);
    }

    /**
     * 任务办理（同意）
     * @param taskId 任务id
     * @param queryParam (options:处理建议,processInstanceId:实例id)
     * @throws IOException
     */
    @PostMapping("handle/{taskId}")
    @CrossOrigin
    public Object handle(@PathVariable("taskId") String taskId, @RequestBody String queryParam) throws IOException {
        return processTaskService.handle(taskId, queryParam);
    }

    /**
     * 驳回
     * @param taskId 任务id
     * @param queryParam (options:驳回原因,processInstanceId:实例id)
     * @throws IOException
     */
    @PostMapping("reject/{taskId}")
    @CrossOrigin
    public Object reject(@PathVariable("taskId") String taskId, @RequestBody String queryParam) throws IOException {
        return processTaskService.reject(taskId, queryParam);
    }

    /**
     * 任务转办
     * @param taskId 任务id
     * @param queryParam (name:转办人)
     * @throws IOException
     */
    @PostMapping("taskTransfer/{taskId}")
    @CrossOrigin
    public Object taskTransfer(@PathVariable("taskId") String taskId, @RequestBody String queryParam) throws IOException {
        return processTaskService.taskTransfer(taskId, queryParam);
    }

    /**
     * 获取登陆人已办任务
     * @param queryParam
     * @return
     */
    @PostMapping("getTaskDone")
    public Object getTaskDone(@RequestBody String queryParam) throws IOException {
        return processTaskService.getTaskDone(queryParam);
    }

    /**
     * 获取我的申请流程
     * @return 获取我的申请流程集合
     */
    @PostMapping("getMyProcess")
    public Object getRunProcess(@RequestBody String queryParam) throws IOException{
        return processTaskService.getMyProcess(queryParam);
    }

    /**
     * 用户撤销流程
     * @param queryParam （processInstanceId） 流程实例id
     */
    @PostMapping("processRevoke")
    public Object processRevoke (@RequestBody String queryParam) throws IOException {
       return processTaskService.processRevoke(queryParam);
    }
}
