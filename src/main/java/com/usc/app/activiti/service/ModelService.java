package com.usc.app.activiti.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public interface ModelService {

    ObjectNode getEditorJson(String modelId);

    String getStencilset();

    Object getModelList();

    Object create(String name, String key, String description);

    String saveModel(String modelId, String name, String description, String json_xml, String svg_xml);

    Object deploy(String modelId);

    Object delete(String modelId);

    List getAllRole();

    List getRoleUserList(String roleId);
}
