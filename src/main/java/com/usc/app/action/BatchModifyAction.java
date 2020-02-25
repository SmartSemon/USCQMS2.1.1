package com.usc.app.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.usc.app.action.a.AbstractAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.obj.api.USCObject;
import com.usc.obj.api.impl.AppFileContext;
import com.usc.obj.api.type.file.IFile;
import com.usc.server.md.field.FieldNameInitConst;
import com.usc.util.ObjectHelperUtils;

public class BatchModifyAction extends AbstractAction
{

	@Override
	public Object executeAction() throws Exception
	{
		USCObject[] objects = context.getSelectObjs();
		boolean b = false;
		List list = new ArrayList<Map>();
		if (!ObjectHelperUtils.isEmpty(objects))
		{
			Map newData = ActionParamParser.getFieldVaues(context.getItemInfo(), context.getItemPage(),
					context.getFormData());
			if (ObjectHelperUtils.isEmpty(newData))
			{
				return failedOperation();
			}
			if (objects.length == 1)
			{
				USCObject uscObject = context.getSelectedObj();
				uscObject.setObjectFieldValues(newData);
				if (uscObject instanceof IFile)
				{
					IFile file = (IFile) uscObject;
					b = file.replaceLocationFile((AppFileContext) context);
				} else
				{
					b = uscObject.save(context);
				}
				if (b)
				{
					return new ActionMessage(b, RetSignEnum.MODIFY,
							StandardResultTranslate.translate("Action_Update_1"), uscObject);
				}
			}
			if (objects.length > 1)
			{
				USCObject object = context.getSelectedObj();
				if (object instanceof IFile)
				{
					removeFileFields(newData);
				}
				for (USCObject uscObject : objects)
				{
					uscObject.setObjectFieldValues(newData);
					if (uscObject.save(context))
					{
						b = Boolean.TRUE;
					}
				}

			}
			if (b)
			{
				return new ActionMessage(b, RetSignEnum.MODIFY, StandardResultTranslate.translate("Action_Update_1"),
						objects);
			}

		}
		return modifyFailed();
	}

	private void removeFileFields(Map newData)
	{
		for (String field : FieldNameInitConst.getFileFields())
		{
			newData.remove(field);
		}
	}

	@Override
	public boolean disable() throws Exception
	{
		return false;
	}

}
