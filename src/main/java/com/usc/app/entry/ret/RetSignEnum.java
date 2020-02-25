package com.usc.app.entry.ret;

public enum RetSignEnum
{
	NEW("N"),

	MODIFY("M"),

	DELETE("D"),

	ADD("A"),

	REMOVE("R"),

	QUERY("Q"),

	NOT_DEAL_WITH("NDW"),

	EXCEPTION("EXCEPTION");

	public String code;

	RetSignEnum(String code)
	{
		this.code = code;
	}
}
