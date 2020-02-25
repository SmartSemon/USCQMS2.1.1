package com.usc.conf.cf.file;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Component
@PropertySource(value =
{ "config/filedata.properties" })
public class FileDataBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	@Value("${file.rootpath}")
	public String path = null;
	@Value("${file.maxsize}")
	public String maxsize = null;
	@Value("${file.type}")
	public String type = null;
}
