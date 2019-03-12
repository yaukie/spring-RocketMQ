package org.yaukie.common.rocketmq.config;

import java.util.LinkedList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 构造rocketmq配置属性信息,并读取配置文件
 * @author yaukie
 *
 */
@Component
@PropertySource("classpath:rocketmq.properties")
@ConfigurationProperties(prefix="org.rocketmq")
@Setter
@Getter
@ToString
@Accessors(chain=true)
public class RocketMQConstant {

	/*生产者,消费者注册中心*/
	private String namesrvAddr;
	
	/*生产者组名*/
	private String producerGrName;
	/*事务生产者组名*/
	private String transactionProducerGrName;
	/*消费者组名*/
	private String consumerGrName;
	/*生产者实例名*/
	private String producerInstanceName;
	/*消费者实例名*/
	private String consumerInstanceName;
	/*事务生产者实例名*/
	private String transactionProInstanceName;
	/*消费者最大并发量*/
	private int counsumerBatchSize;
	
	/*是否启动广播消费*/
	private boolean 	consumerBroadcasting; 
	/*是否消费历史记录*/
	private boolean enableHistoryConsumer;
	/*是否顺序消费*/
	private boolean enableOrderConsumer;
	/*判断消息是否首次消费*/
	private boolean isFirstSub;
	
	/*订阅容量*/
	private List<String> subscribe = new LinkedList<String>();
	
}
