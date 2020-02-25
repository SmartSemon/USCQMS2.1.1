package com.usc.app.action;

import java.util.Map;

import com.usc.app.action.a.AbstractRelationAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.AppFileContext;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.api.impl.USCServerBeanProvider;
import com.usc.obj.api.type.file.FileObject;
import com.usc.obj.api.type.file.IFile;

public class CreateRelationObjAction extends AbstractRelationAction
{

	@Override
	public Object executeAction() throws Exception
	{
		Map newData = ActionParamParser.getFieldVaues(context.getItemInfo(), context.getItemPage(),
				context.getFormData());
		context.setFormData(newData);
		USCObject newObj = context.createObj(context.getItemNo());
		if (newObj != null)
		{
			Map relationData = GetRelationData.getData(getRoot(), newObj);
			ApplicationContext applicationContext = (ApplicationContext) USCServerBeanProvider
					.getContext(getRelationShip().getRelationItem(), context.getUserName());
			applicationContext.setFormData(relationData);
			applicationContext.createObj(applicationContext.getItemNo());
			if (newObj instanceof IFile)
			{
				doFileAction(newObj);
			}
		}
		return new ActionMessage(flagTrue, RetSignEnum.NEW, StandardResultTranslate.translate("Action_Create_1"),
				newObj);
	}

	private boolean doFileAction(USCObject object)
	{
		AppFileContext fileContext = (AppFileContext) context;
		FileObject fileObject = (FileObject) object;
		return fileObject.upLoadFile(fileContext);
	}

}
