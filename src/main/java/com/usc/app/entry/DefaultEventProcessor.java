package com.usc.app.entry;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.usc.app.action.AppActionFactory;
import com.usc.app.action.i.AppAction;
import com.usc.app.action.utils.ActionMessage;
import com.usc.app.action.utils.ResultMessage;
import com.usc.app.entry.ret.RetSignEnum;
import com.usc.app.exception.ApplicationException;
import com.usc.app.exception.GetExceptionDetails;
import com.usc.app.util.RabbitMqUtils;
import com.usc.app.util.RabbitMqUtilsBuilder;
import com.usc.app.util.tran.StandardResultTranslate;
import com.usc.cache.redis.RedisUtil;
import com.usc.cache.redis.RedisUtilBuilder;
import com.usc.obj.api.BeanFactoryConverter;
import com.usc.obj.api.impl.AppFileContext;
import com.usc.obj.api.impl.ApplicationContext;
import com.usc.obj.jsonbean.ActionRequestJSONBean;
import com.usc.server.md.ItemInfo;
import com.usc.server.md.ItemMenu;
import com.usc.server.md.MenuLibrary;
import com.usc.server.mq.ItemMQMenu;
import com.usc.server.util.LoggerFactory;

public class DefaultEventProcessor {
	private Object resultJson = new Object();

	private MultipartFile file;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public MultipartFile getFile() {
		return this.file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Object getResultJson() {
		return resultJson;
	}

	public void setResultJson(Object obj) {
//		if (obj == null)
//		{
//			this.resultJson = StandardResultTranslate.successfulOperation();
//			return;
//		}
		if (obj instanceof ResultMessage)
		{
			this.resultJson = obj;
			return;
		}
		if (obj instanceof Map)
		{
			this.resultJson = obj;
			return;
		}

		if (obj instanceof Optional)
		{
			Optional object = (Optional) obj;
			if (object.isPresent())
			{
				obj = object.get();
				setResultJson(obj);
			}
			return;
		}

		this.resultJson = ActionMessage.creator(true, RetSignEnum.NOT_DEAL_WITH,
				StandardResultTranslate.translate("Action_Default_1"), obj);
	}

	public DefaultEventProcessor(MultipartFile file, HttpServletRequest request, HttpServletResponse response)
	{
		String queryParam = request.getParameter("values");
		ActionRequestJSONBean jsonBean = null;
		try
		{
			jsonBean = BeanFactoryConverter.getJsonBean(ActionRequestJSONBean.class, queryParam);

			if (jsonBean != null)
			{
				String impl = jsonBean.getImplclass();
				boolean implType = jsonBean.getImplType() != null && jsonBean.getImplType() == true;
				if (implType)
				{
					doMq(impl);
					return;
				}
				jsonBean.init();
				setFile(file);
				setRequest(request);
				setResponse(response);
				ApplicationContext context = createContext(jsonBean);
				action(context, jsonBean);
			}
		} catch (Exception e)
		{
			setResultJson(StandardResultTranslate.getResult(false, GetExceptionDetails.details(e)));
			return;
		}

	}

	private void action(ApplicationContext context, ActionRequestJSONBean jsonBean) throws Exception {
		String impl = jsonBean.getImplclass();
		Object clazz = null;
		if (impl != null)
		{
			clazz = AppActionFactory.getAction(impl);
			if (clazz == null)
			{
				LoggerFactory.logError("未找到实现类：" + impl, new Throwable("No menu found : " + impl));
				setResultJson(StandardResultTranslate.getResult(false, "未找到实现类：：" + impl));
				return;
			}
			InspectionImplClass.init(clazz, jsonBean);

			execute((AppAction) clazz, context);
		} else
		{
			LoggerFactory.logError("未找到实现类：" + impl, new Throwable("No menu found : " + impl));
		}
	}

	private void doMq(String impl) throws Exception {
		RedisUtil redisUtil = RedisUtilBuilder.builder();
		ItemMQMenu menu = redisUtil.hget("MODEL_MQAFFAIRS", impl, ItemMQMenu.class);
		if (menu != null)
		{
			RabbitMqUtils mqUtils = RabbitMqUtilsBuilder.build();
			mqUtils.sendToFanoutExchange(menu.getChannelname(), menu.getMcontent());
			setResultJson(null);
		}
	}

	private void execute(AppAction action, ApplicationContext context) {
		Object object = null;
		try
		{
			if (doBeforeAction(context, action))
			{
				object = executeAction(action, context);
				doAfterAction(context, action);
				setResultJson(object);
			}
		} catch (Exception e)
		{
			handException(e);
		}
	}

	private Object executeAction(AppAction action, ApplicationContext context) throws Exception {
		action.setApplicationContext(context);
		return action.action();
	}

	private boolean doBeforeAction(ApplicationContext context, AppAction action) {
		String implString = action.getClass().getName();
		ItemMenu menu = context.getItemInfo().getItemMenu(implString);
		if (menu != null)
		{
			List<MenuLibrary> beforeActionsLibraries = menu.getBeforeActionList();
			if (beforeActionsLibraries != null)
			{
				for (MenuLibrary menuLibrary : beforeActionsLibraries)
				{
					String beforeImplclass = menuLibrary.getImplclass();
					try
					{
						doAction(context, beforeImplclass);
					} catch (Exception e)
					{
						handException(e);
						return false;
					}
				}
			}
		}
		return true;
	}

	private void doAfterAction(ApplicationContext context, AppAction action) throws Exception {
		String implString = action.getClass().getName();
		ItemMenu menu = context.getItemInfo().getItemMenu(implString);
		if (menu != null)
		{
			List<MenuLibrary> beforeActionsLibraries = menu.getBeforeActionList();
			if (beforeActionsLibraries != null)
			{
				for (MenuLibrary menuLibrary : beforeActionsLibraries)
				{
					String beforeImplclass = menuLibrary.getImplclass();
					doAction(context, beforeImplclass);
				}
			}
		}
	}

	private void doAction(ApplicationContext context, String impl) throws Exception {
		AppAction action = AppActionFactory.getAction(impl);
		if (action != null)
		{ executeAction(action, context); }
	}

	private ApplicationContext createContext(ActionRequestJSONBean jsonBean) {
		ApplicationContext applicationContext = new ApplicationContext(jsonBean);
		applicationContext.setContext();
		ItemInfo info = applicationContext.getItemInfo();
		if (info == null)
		{
			return applicationContext;
		} else
		{
			if (info.getType() == 1)
			{ applicationContext = new AppFileContext(jsonBean, getFile()); }
		}
		applicationContext.setServletRequest(getRequest());
		applicationContext.setServletResponse(getResponse());

		return applicationContext;
	}

	private void handException(Exception e) {
		Object object = null;
		if (!(e instanceof ApplicationException))
		{ e.printStackTrace(); }
		if (e instanceof ApplicationException)
		{ object = ((ApplicationException) e).toActionMessage(); }
		if (e instanceof RuntimeException)
		{
			object = (new ApplicationException(e)).toActionMessage();
		} else
		{
			object = StandardResultTranslate.getResult(e.getMessage(), false);
		}
		setResultJson(object);
	}

}
