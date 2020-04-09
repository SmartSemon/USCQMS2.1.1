package com.usc.server.jdbc.procedure;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.util.Assert;

import lombok.Data;

@Data
public class USCCallableStatementCreator implements CallableStatementCreator, SqlProvider {

	private String sql;
	private int paramLength = 0;
	private int inparamsLength = 0;
	private int outparamsLength = 0;
	private Object[] inputParams = null;
	private int[] outputParams = null;

	public USCCallableStatementCreator(String callString)
	{
		Assert.notNull(callString, "Call string must not be null");
		this.sql = callString;
	}

	/**
	 * 无返回值
	 * 
	 * @param proceduerName 存储过程名称
	 * @param inputParams   输入参数
	 */
	public USCCallableStatementCreator(String proceduerName, Object[] inputParams)
	{
		Assert.notNull(proceduerName, "Call string must not be null");
		Assert.notNull(inputParams, "Call inputParams must not be null");
		this.inputParams = inputParams;
		this.inparamsLength = inputParams.length;
		this.paramLength = this.inparamsLength;
		this.sql = dwInputAndOutPutParams(proceduerName);
	}

	/**
	 * 有返回值
	 * 
	 * @param proceduerName    存储过程名称
	 * @param inputParams      输入参数
	 * @param outputParamTypes 输出参数
	 */
	public USCCallableStatementCreator(String proceduerName, Object[] inputParams, int... outputParamTypes)
	{
		Assert.notNull(proceduerName, "Call string must not be null");
		Assert.notNull(inputParams, "Call inputParams must not be null");
		Assert.notNull(outputParamTypes, "Call outputParams must not be null");
		this.inputParams = inputParams;
		this.outputParams = outputParamTypes;
		this.inparamsLength = inputParams.length;
		this.outparamsLength = outputParamTypes == null ? 0 : outputParamTypes.length;
		this.paramLength = inparamsLength + outparamsLength;
		this.sql = dwInputAndOutPutParams(proceduerName);
	}

	@Override
	public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String sql = getSql();
		CallableStatement cs = con.prepareCall(sql);
		if (inputParams != null)
		{
			for (int i = 1; i <= inparamsLength; i++)
			{ cs.setInt(i, (int) inputParams[i - 1]); }
		}
		if (outputParams != null)
		{
			for (int i = 0; i < outputParams.length; i++)
			{ cs.registerOutParameter(inparamsLength + i + 1, outputParams[i]); }
		}
		return cs;
	}

	@Override
	public String getSql() {
		return this.sql;
	}

	private String dwInputAndOutPutParams(String proceduerName) {
		String ps = "";
		int len = inparamsLength + outparamsLength;
		for (int i = 0; i < len; i++)
		{ ps += ",?"; }
		ps = ps.substring(1);
		return "{call " + proceduerName + "(" + ps + ")}";
	}

}
