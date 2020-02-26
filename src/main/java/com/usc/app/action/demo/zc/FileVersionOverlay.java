package com.usc.app.action.demo.zc;

import com.usc.app.activiti.ActCommonUtil;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.type.GeneralObject;
import com.usc.test.mate.resource.ServiceToWbeClientResource;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: lwp
 * @DATE: 2020/2/25 19:10
 * @Description: 文件版本叠加
 **/
public class FileVersionOverlay implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String itemNo = ActCommonUtil.getItemNo(execution.getProcessInstanceId());
        List<HashMap<String, Object>> list = null;
        try {
            // 获取此次流程的业务数据
            HashMap listDto = new HashMap();
            listDto.put("itemNo", itemNo);
            listDto.put("facetype", 2);
            listDto.put("itemGridNo", "default");
            listDto.put("itemPropertyNo", "default");
            listDto.put("itemRelationPageNo", "default");
            listDto.put("condition", "DSNO=" + execution.getProcessInstanceId() + " ORDER BY id DESC");
            listDto.put("page", 1);
            String params = ActCommonUtil.toJsonString(listDto);
            ServiceToWbeClientResource serviceToWbeClientResource = new ServiceToWbeClientResource();
            HashMap<String, Object> object = (HashMap<String, Object>) serviceToWbeClientResource
                    .getDataListLimit(params);
            list = (List<HashMap<String, Object>>) object.get("dataList");
        } catch (Exception e) {
            System.err.println(e);
        }
        HashMap restore = new HashMap();
        for (HashMap<String, Object> objectMap : list) {
            USCObject uscObject = new GeneralObject(itemNo, objectMap);
            restore.put("VER", uscObject.getFieldValueToInteger("VER") + 1);
            ApplicationContext applicationContext = new ApplicationContext(null, uscObject);
            uscObject.setObjectFieldValues(restore);
            uscObject.save(applicationContext);
        }
    }
}
