package com.usc.test.mate.resource;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usc.test.mate.action.service.ModelServer;

@RestController
@RequestMapping(value = "/model", produces = "application/json;charset=UTF-8")
public class ModelSetResource {
	@Autowired
	private ModelServer modelServer;

	@PostMapping("/openModel")
	public Object openModel(@RequestBody String queryParam, HttpServletRequest httpServletRequest) {
		return modelServer.openModel(httpServletRequest.getHeader("UserName"));
	}

	@PostMapping("/closeModel")
	public Object closeModel(@RequestBody String queryParam, HttpServletRequest httpServletRequest) {
		return modelServer.closeModel(queryParam, httpServletRequest);
	}

	@PostMapping("/upgradeModel")
	public Object upgradeModel(@RequestBody String queryParam, HttpServletRequest httpServletRequest) {
		return modelServer.upgradeModel(queryParam, httpServletRequest);
	}

	@PostMapping("/cancelUpgradeModel")
	public Object cancelUpgradeModel(@RequestBody String queryParam, HttpServletRequest httpServletRequest) {
		return modelServer.cancelUpgradeModel(queryParam, httpServletRequest);
	}
}
