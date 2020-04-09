package com.usc.conf.cf;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.util.IOUtils;

@ControllerAdvice
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (int i = converters.size() - 1; i >= 0; i--)
		{
			if (converters.get(i) instanceof MappingJackson2HttpMessageConverter)
			{ converters.remove(i); }
		}

		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		converter.setFastJsonConfig(FastJsonSerializerConfig.getFastJsonConfig());
		converter.setSupportedMediaTypes(FastJsonSerializerConfig.getMediaTypes());
		converter.setDefaultCharset(IOUtils.UTF8);

		converters.add(converter);
	}

}
