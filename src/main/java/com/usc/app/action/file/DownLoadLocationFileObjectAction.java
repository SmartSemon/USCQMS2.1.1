package com.usc.app.action.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.usc.app.util.file.FileUtil;
import com.usc.app.util.file.ZipUtil;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.AppFileContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.obj.api.type.file.FileObjUtil;
import com.usc.obj.api.type.file.FileObject;
import com.usc.obj.api.type.file.IFile;
import com.usc.server.syslog.LOGActionEnum;
import com.usc.util.ObjectHelperUtils;

public class DownLoadLocationFileObjectAction extends AbstractFileObjAction {

	@Override
	public Object executeAction() throws Exception {
		AppFileContext fileContext = (AppFileContext) context;
		USCObject[] objects = context.getSelectObjs();
		if (objects.length == 1)
		{
			IFile file = (IFile) objects[0];
			FileObject fileObject = (FileObject) file;
			if (!fileObject.hasFile())
			{ return false; }
			if (!fileObject.downLoadFile(fileContext))
			{ return false; }
		} else
		{
			bathDownloadFile(objects, null, fileContext.getServletRequest(), fileContext.getServletResponse());
		}

		return true;
	}

	@Override
	public boolean disable() throws Exception {

		return false;
	}

	public boolean bathDownloadFile(USCObject[] fileObjects, String locaton, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		File tmpFile = FileObjUtil.getTmpFile();
		String tmpFileName = tmpFile.getName();
		String outFileName = tmpFileName + "." + "zip";
		String outPath = tmpFile.getPath() + File.separator + outFileName;
		List<File> files = new ArrayList<File>();
		int cont = fileObjects.length;
		FileObject[] fileObjects2 = new FileObject[cont];
		try
		{
			List<String> ofns = new ArrayList<String>(cont);
			for (int i = 0; i < cont; i++)
			{
				if (fileObjects[i] instanceof FileObject)
				{
					FileObject fileObject = (FileObject) fileObjects[i];
					if (fileObject.hasFile())
					{
						String id = fileObject.getID();
						String fileName = (String) fileObject.getFieldValue("FNAME");
						String flocation = (String) fileObject.getFieldValue("FLOCATION");
						String ftype = fileObject.getFieldValueToString("FTYPE");
						String serverFilePath = fileObject.getLocation() + flocation + File.separator + id + ftype;
						File serFile = new File(serverFilePath);
						if (serFile.exists())
						{
							FileInputStream tmpserFile = new FileInputStream(serFile);
							String ofn = fileName + ftype;
							if (ofns.contains(ofn))
							{ ofn = fileName + "(" + i + ")" + ftype; }
							ofns.add(ofn);
							File tf = new File(tmpFile.getPath() + File.separator + ofn);
							Path tpPath = tf.toPath();
							if (FileObjUtil.copyFile(tmpserFile, tpPath) != 0L)
							{ fileObjects2[i] = fileObject; }
							if (tf.exists())
							{ files.add(tf); }
						}

					}
				}
			}
			if (ObjectHelperUtils.isEmpty(files))
			{ return false; }

			FileOutputStream outputStream = new FileOutputStream(outPath);
			ZipUtil.toZip(files, outputStream);
			File zipFile = new File(outPath);
			if (FileObjUtil.doDownLoad(zipFile, outFileName, request, response))
			{
				USCServerBeanProvider.getSystemLogger().writeFileLog(context, LOGActionEnum.DL, fileObjects2);
				return true;
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (tmpFile.exists())
			{ FileUtil.deleteRecursivelyFolder(tmpFile); }
		}
		return false;
	}

}
