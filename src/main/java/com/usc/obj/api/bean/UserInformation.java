package com.usc.obj.api.bean;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserInformation implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String userID;
	private String userName;
	private String userRemark;
	private String avatar;
	private String sign;
	private String status;
	private String onDuty;
	private String employeeID;
	private String employeeNo;
	private String employeeName;
	private String employeeRemark;
	private Integer age;
	private String sax;
	private String tel;
	private String mail;
	private String idCard;
	private String wkSate;
	private String departMentNo;
	private String departMentName;
	private String clientID;

	public HashMap<String, Object> toSystemUserMap()
	{
		HashMap<String, Object> suser = new HashMap<String, Object>();
		suser.put("SNAME", getUserName());
		suser.put("ID", getUserID());
		suser.put("REMARK", getUserName());
		suser.put("ONDUTY", getOnDuty());
		suser.put("AVATAR", getAvatar());
		suser.put("SIGN", getSign());
		suser.put("STATUS", getStatus());
		return suser;

	}

}
