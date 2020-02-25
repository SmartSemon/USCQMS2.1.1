package com.usc.app.exception;

import java.rmi.RemoteException;

public class ThrowException
{
	public static ApplicationException throwException(Throwable e)
	{
		return new ApplicationException(e.getMessage(), e);
	}

	public static ApplicationException throwNullPointerException(String message)
	{
		return throwException(new NullPointerException(message));
	}

	public static RemoteException throwRemoteExceptionDetails(Throwable e)
	{
		return new RemoteException(GetExceptionDetails.details(e), e);
	}
}
