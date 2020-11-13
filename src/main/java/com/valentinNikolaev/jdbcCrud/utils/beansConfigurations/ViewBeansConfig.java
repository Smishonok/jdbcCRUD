package com.valentinNikolaev.jdbcCrud.utils.beansConfigurations;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.valentinNikolaev.jdbcCrud.view")
@Import(ControllersBeansConfig.class)
public class ViewBeansConfig {}
