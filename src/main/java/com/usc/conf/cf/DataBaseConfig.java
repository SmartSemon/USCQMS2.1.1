package com.usc.conf.cf;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

@Configuration
@PropertySource(value = "config/db_application.yml", encoding = "utf-8", factory = USCYamlPropertySourceFactory.class)
public class DataBaseConfig {
	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.master.druid")
	public DataSource masterDataSource() {
		return DruidDataSourceBuilder.create().build();
	}

	@Bean(name = "modelDataSource")
	@ConfigurationProperties("spring.datasource.model.druid")
	public DataSource modelDataSource() {
		return DruidDataSourceBuilder.create().build();
	}

	@Bean
	@Primary
	public JdbcTemplate masterJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean(name = "modelJdbcTemplate")
	public JdbcTemplate modelJdbcTemplate(@Qualifier("modelDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
