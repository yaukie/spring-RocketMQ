package org.yaukie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * 构造springboot启动类
 * @author yaukie
 */
@SpringBootApplication
@ComponentScan(basePackages="org.yaukie.special")
@Slf4j
public class RocketMQApplicationStart {
	
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(RocketMQApplicationStart.class,args);
//		ApplicationContextHelper.setApplicationContext(context);
	}

}
