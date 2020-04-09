package com.usc.app.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.usc.app.action.retmsg.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.FormatPromptInformation;

public class ApplicationException extends Exception
{

	private static final long serialVersionUID = 983353008854057946L;
	public static String ExceptionClass = "ExceptionClass";
	public static String ExceptionMessage = "ExceptionMessage";
	public static String ExceptionKey = "ExceptionKey";
	public static String MessageArguments = "MessageArguments";
	public static String PrintStackTrace = "PrintStackTrace";
	public static String ExceptionProcess = "ExceptionProcess";

	private Object errObj = null;

	protected String exceptionProcess = null;
	protected String info = null;
	protected Exception throwException = null;
	protected String infoTranslateKey = null;
	protected String[] errCode = null;
	protected String[] infoPath = null;

	public ApplicationException(String msg)
	{
		super(msg);
		this.info = msg;
	}

	public ApplicationException(String msg, Object errBindObj)
	{
		super(msg);
		this.info = msg;
		this.errObj = errBindObj;
	}

	public ApplicationException(Exception e)
	{
		super(e.getMessage());
		this.setInfo(e.getMessage());
		this.setThrowException(e);
	}

	public void setTranlateInfo(String infoTranslateKey, String[] infoPath)
	{
		this.infoTranslateKey = infoTranslateKey;
		this.infoPath = infoPath;
	}

	public void setThrowException(Exception throwException)
	{
		this.throwException = throwException;
		this.setStackTrace(throwException.getStackTrace());
	}

	public Exception getThrowException()
	{
		return this.throwException;
	}

	public String getMessage()
	{
		if (this.infoTranslateKey != null && this.infoTranslateKey.length() > 0)
		{
			return FormatPromptInformation.getFormatrMsg(this.infoTranslateKey, this.infoPath);
		} else
		{
			return getInfo();
		}
	}

	public String getInfo()
	{
		return this.info;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public String getInfoTranslateKey()
	{
		return this.infoTranslateKey;
	}

	public void setErrCode(String... errCode)
	{
		this.errCode = errCode;
	}

	public String[] getErrCode()
	{
		return this.errCode;
	}

	public Object getErrObj()
	{
		return this.errObj;
	}

	public void setErrObj(Object errObj)
	{
		this.errObj = errObj;
	}

	public void setExceptionProcess(String exceptionProcess)
	{
		this.exceptionProcess = exceptionProcess;
	}

	public String getExceptionProcess()
	{
		return this.exceptionProcess;
	}

	public String getDetails()
	{
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		this.printStackTrace(printWriter);
		String es = writer.toString();
		try
		{
			return es;
		} finally
		{
			this.printStackTrace();
		}
	}

	public ActionMessage toActionMessage()
	{
		ActionMessage jsonObject = new ActionMessage(false, RetSignEnum.EXCEPTION, this.getInfo(), this.errObj,
				this.errCode);
		return jsonObject;
	}
}
