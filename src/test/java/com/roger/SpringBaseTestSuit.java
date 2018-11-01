package com.roger;

import com.roger.config.DruidDataSourceConfig;
import com.roger.config.SpringBeanConfig;
import com.roger.config.SpringTXConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DruidDataSourceConfig.class,  SpringTXConfig.class, SpringBeanConfig.class})
public class SpringBaseTestSuit {
}
