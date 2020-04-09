package com.usc.conf.cf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;

public class FastJsonSerializerConfig {
	public static FastJsonConfig getFastJsonConfig() {
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(getSerializerFeatures());
		fastJsonConfig.setSerializeFilters(getPropertyPreFilter());
		return fastJsonConfig;
	}

	public static SerializeFilter getPropertyPreFilter() {
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
		filter.getExcludes().add("name");
		filter.getExcludes().add("ename");
		filter.getExcludes().add("itemColor");
		filter.getExcludes().add("modelClassView");
		filter.getExcludes().add("modelQueryView");
		filter.getExcludes().add("modelRelationShip");
		return filter;
	}

	public static SerializerFeature[] getSerializerFeatures() {
		List<SerializerFeature> features = new ArrayList<SerializerFeature>();
		features.add(SerializerFeature.PrettyFormat); // 格式化数据
		features.add(SerializerFeature.WriteMapNullValue); // 开启输出值为null的字段
		features.add(SerializerFeature.WriteNullListAsEmpty);// 将Collection类型字段的字段空值输出为[]
		features.add(SerializerFeature.WriteNullBooleanAsFalse);// Boolean字段如果为null,输出为false,而非null
		features.add(SerializerFeature.WriteSlashAsSpecial);// 对斜杠’/’进行转义
		features.add(SerializerFeature.WriteDateUseDateFormat);// 全局序列化时间格式
		features.add(SerializerFeature.DisableCircularReferenceDetect);// 禁用循环引用
		features.add(SerializerFeature.WriteNullStringAsEmpty);// 将字符串类型字段的空值输出为空字符串
		features.add(SerializerFeature.WriteNullNumberAsZero);// 将数值类型字段的空值输出为0
		return features.toArray(new SerializerFeature[] {});
	}

	public static List<MediaType> getMediaTypes() {
		List<MediaType> fastMediaTypes = new ArrayList<MediaType>();
		fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		fastMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
		fastMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
		fastMediaTypes.add(MediaType.parseMediaType("text/plain;charset=utf-8"));
		fastMediaTypes.add(MediaType.parseMediaType("text/html;charset=utf-8"));
		fastMediaTypes.add(MediaType.parseMediaType("text/json;charset=utf-8"));

		return fastMediaTypes;
	}
}
