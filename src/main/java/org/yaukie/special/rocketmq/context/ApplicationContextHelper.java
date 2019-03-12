package org.yaukie.special.rocketmq.context;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 获取context上下文
 * @author yaukie
 *
 */
public class ApplicationContextHelper {
		
	@Getter
	@Setter
	@Accessors(chain=true)
	private static ApplicationContext applicationContext;
 

		@SuppressWarnings("unchecked")
		public <T> T getBean(String name){
			return (T) applicationContext.getBean(name);
		}
		
		public <T> T getBean(Class<T> cls ){
			return applicationContext.getBean(cls);
		}
}
