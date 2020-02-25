package com.usc.obj.api.type.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.usc.obj.api.InvokeContext;
import com.usc.obj.api.bean.UserInformation;
import com.usc.obj.api.type.AbstractDBObj;
import com.usc.server.DBConnecter;
import com.usc.server.util.BeanConverter;
import com.usc.util.ObjectHelperUtils;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class UserInfoObject extends AbstractDBObj implements EmployeeInfo, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 6968093484780401270L;
	@JSONField(ordinal = 4)
	private HashMap<String, Object> userInfo;
	@JSONField(ordinal = 5)
	private UserInformation userInformation;

	public UserInfoObject(String objType, HashMap<String, Object> map)
	{
		super("SUSER", map);
		infoIsNullSet();
	}

	public UserInfoObject(String objType, UserInformation info)
	{
		super("SUSER", info.toSystemUserMap());
		setUserInfomation(info);
	}

	public HashMap<String, Object> getUserInfo()
	{
		return this.userInfo;
	}

	public void setUserInfo(HashMap<String, Object> map)
	{
		this.userInfo = map;
		if (ObjectHelperUtils.isNotEmpty(map))
		{
			this.userInformation = (UserInformation) BeanConverter.toJavaBean(new UserInformation(), map);
		}

	}

	private void setUserInfomation(UserInformation info)
	{
		this.userInformation = info;
		if (ObjectHelperUtils.isNotEmpty(info))
		{
			this.userInfo = (HashMap<String, Object>) BeanConverter.toMap(info);
		}

	}

	public UserInformation getUserInfomation()
	{
		if (this.userInformation != null)
		{
			return this.userInformation;
		}
		return (UserInformation) BeanConverter.toJavaBean(new UserInformation(), this.userInfo);
	}

	public String getUserID()
	{
		return this.getID();
	}

	public String getUserName()
	{
		return this.getFieldValueToString("SNAME");
	}

	public String getOnDuty()
	{
		return this.getFieldValueToString("ONDUTY");
	}

	public String getUserAvatar()
	{
		return this.getFieldValueToString("AVATAR");
	}

	public String getUserSign()
	{
		return this.getFieldValueToString("SIGN");
	}

	public String getUserStatus()
	{
		super.getFieldValuesJSON(true);
		return this.getFieldValueToString("STATUS");
	}

	@Override
	public Map<String, Object> getFieldValuesJSON(boolean b)
	{
		JSONObject jsonObject = new JSONObject(getFieldValues().size());
		this.fieldValues.forEach((f, v) -> {
			if (!f.equals("PASSWORD"))
			{
				jsonObject.put(f, getFieldValueJson(f, v));
			}
		});
		return jsonObject;
	}

	@Override
	public boolean canClone(InvokeContext context) throws Exception
	{
		return false;
	}

	@Override
	public String getEmployeeID()
	{
		return this.userInformation.getEmployeeID();
	}

	@Override
	public String getEmployeeNo()
	{
		return this.userInformation.getEmployeeNo();
	}

	@Override
	public String getEmployeeName()
	{
		return this.userInformation.getEmployeeName();
	}

	@Override
	public String getEmployeeAge()
	{
		return this.userInformation.getEmployeeRemark();
	}

	@Override
	public String getEmployeeSax()
	{
		return this.userInformation.getSax();
	}

	@Override
	public String getEmployeeTel()
	{
		return this.userInformation.getTel();
	}

	@Override
	public String getEmployeeEMail()
	{
		return this.userInformation.getMail();
	}

	@Override
	public String getEmployeeIDCard()
	{
		return this.userInformation.getIdCard();
	}

	@Override
	public String getEmployeeWkSate()
	{
		return this.userInformation.getWkSate();
	}

	@Override
	public String getEmployeeDepartMentNo()
	{
		return this.userInformation.getDepartMentNo();
	}

	@Override
	public String getEmployeeDepartMentName()
	{
		return this.userInformation.getDepartMentName();
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void infoIsNullSet()
	{
		String sql = "SELECT U.id AS userID,U.onduty AS onDuty,U.name AS userName,U.remark AS userRemark,P.id AS employeeID,"
				+ "P.no AS employeeNo,P.name AS employeeName,P.sax AS sax,P.tel AS tel,P.mail AS mail,P.remark AS employeeRemark,P.age AS age,"
				+ "P.idcard AS idCard,P.wk_state AS wkSate,D.no AS departMentNo,D.name AS departMentName "
				+ "FROM suser U,spersonnel P,sdepartment D WHERE U.del=0 AND p.del=0 AND D.del=0 "
				+ "AND EXISTS(SELECT 1 FROM suser_relobj A,crl_spersonnel_obj B WHERE A.del=0 AND B.del=0  "
				+ "AND A.itembid=U.id AND A.itemaid=b.itemid AND B.itemid=P.id AND B.nodeid=D.id) AND U.name='"
				+ this.getUserName() + "'";
		try
		{

			List<UserInformation> list = (new JdbcTemplate(DBConnecter.getDataSource())).query(sql,
					new BeanPropertyRowMapper(UserInformation.class));
			if (!ObjectHelperUtils.isEmpty(list))
			{
				setUserInfomation(list.get(0));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
