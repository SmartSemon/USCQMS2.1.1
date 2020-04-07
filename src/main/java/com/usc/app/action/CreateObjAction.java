package com.usc.app.action;

import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.AppFileContext;
import com.usc.obj.api.type.file.FileObject;
import com.usc.obj.api.type.file.IFile;

public class CreateObjAction extends AbstractAction {

	@Override
	public Object executeAction() throws Exception {
		if (!disable())
		{
			Map<String, Object> newData = ActionParamParser.getFieldVaues(context.getItemInfo(), context.getItemPage(),
					context.getFormData());
			context.setFormData(newData);
			USCObject object = context.createObj(context.getItemNo());
			if (object == null)
			{
				if (context.getExtendInfo("CreateResult") != null)
				{ return context.getExtendInfo("CreateResult"); }
			} else
			{
				if (object instanceof IFile)
				{ doFileAction(object); }
			}
			return new ActionMessage(true, RetSignEnum.NEW, StandardResultTranslate.translate("Action_Create_1"),
					object);
		}
		return failedOperation();

	}

	private void doFileAction(USCObject object) {
		AppFileContext fileContext = (AppFileContext) context;
		FileObject fileObject = (FileObject) object;
		fileObject.upLoadFile(fileContext);
	}

	@Override
	public boolean disable() throws Exception {
		return false;
	}

}
