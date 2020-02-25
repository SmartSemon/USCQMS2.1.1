package com.usc.obj.api.type.file;

import java.io.File;

import com.usc.conf.cf.file.FileDataBean;
import com.usc.obj.api.impl.AppFileContext;
import com.usc.util.SpringContextUtil;

public interface IFile
{
	public boolean downLoadFile(AppFileContext context);

	public boolean upLoadFile(AppFileContext context);

	public boolean replaceLocationFile(AppFileContext context);

	public File getServerLocationFile();

	public boolean hasFile();

	default String getLocation()
	{
		FileDataBean filedata = SpringContextUtil.getBean(FileDataBean.class);
		return filedata.getPath();
	}

}
