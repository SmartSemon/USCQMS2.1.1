package com.usc.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AppRunner implements ApplicationRunner {
	@Autowired
	@Qualifier("modelJdbcTemplate")
	protected JdbcTemplate jdbcTemplate;

}
