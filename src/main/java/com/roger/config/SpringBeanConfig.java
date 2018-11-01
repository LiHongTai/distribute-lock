package com.roger.config;

import com.roger.constant.SystemConstant;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(SystemConstant.COMPONENT_SCAN_PACKAGE)
public class SpringBeanConfig {
}
