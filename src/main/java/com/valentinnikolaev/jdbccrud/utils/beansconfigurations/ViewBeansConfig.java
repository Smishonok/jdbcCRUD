package com.valentinnikolaev.jdbccrud.utils.beansconfigurations;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.valentinnikolaev.jdbccrud.view")
@Import(ControllersBeansConfig.class)
public class ViewBeansConfig {}
