package com.usc.conf.cf.json;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@PropertySource(value =
{ "config/application.properties" })
public class FastJSONAutoTypeAccept
{
	@Value("${fastjson.parser.autoTypeAccept}")
	public String AutoTypeAccept;
}
