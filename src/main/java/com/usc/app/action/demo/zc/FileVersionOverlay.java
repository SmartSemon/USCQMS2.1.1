package com.usc.app.action.demo.zc;

import com.usc.app.activiti.ActCommonUtil;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.type.GeneralObject;
import com.usc.obj.api.type.file.IFile;
import com.usc.obj.util.USCObjectQueryHelper;
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

    private static final long serialVersionUID = -1400269214834170502L;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
//        String itemNo = ActCommonUtil.getItemNo(execution.getProcessInstanceId());
//        List<HashMap<String, Object>> list = null;
//        try {
//            // 获取此次流程的业务数据
//            HashMap listDto = new HashMap();
//            listDto.put("itemNo", itemNo);
//            listDto.put("facetype", 2);
//            listDto.put("itemGridNo", "default");
//            listDto.put("itemPropertyNo", "default");
//            listDto.put("itemRelationPageNo", "default");
//            listDto.put("condition", "DSNO=" + execution.getProcessInstanceId() + " ORDER BY id DESC");
//            listDto.put("page", 1);
//            String params = ActCommonUtil.toJsonString(listDto);
//            ServiceToWbeClientResource serviceToWbeClientResource = new ServiceToWbeClientResource();
//            HashMap<String, Object> object = (HashMap<String, Object>) serviceToWbeClientResource
//                    .getDataListLimit(params);
//            list = (List<HashMap<String, Object>>) object.get("dataList");
//        } catch (Exception e) {
//            System.err.println(e);
//        }
        String conditions = "DSNO = " + "'" + execution.getProcessInstanceId() + "'";
        //申请单的中的数据集
        USCObject[] objects = USCObjectQueryHelper.getObjectsByCondition("QCDOCUMENTMANAGEMENT", conditions);
        HashMap restore = new HashMap();
        if (objects != null && objects.length > 0) {
            for (USCObject object : objects) {
                restore.put("VER", object.getFieldValue("VER") != null ? object.getFieldValueToInteger("VER") + 1 : 1);
                ApplicationContext applicationContext = new ApplicationContext(null, object);
                object.setObjectFieldValues(restore);
                object.save(applicationContext);
            }
        }
    }
}
