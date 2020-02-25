package com.usc.app.ims.config.action;

public enum EndpointEnum
{
	RefreshUnread("@NT_RUR"),

	RefreshRead("@NT_RR"),

	EXCEPTION("@NT_EXCEPTION");
	public String code;

	EndpointEnum(String code)
	{
		this.code = code;
	}
}
