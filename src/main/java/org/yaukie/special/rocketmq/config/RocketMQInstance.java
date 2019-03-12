package org.yaukie.special.rocketmq.config;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.yaukie.special.rocketmq.event.MessageEvent;
import org.yaukie.special.rocketmq.event.TransactionCheckListenerImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * rocketmq参数信息实例化
 * 通过读取前配置文件bean,实例化参数到指定字段
 * @author yaukie
 */
@Configuration
@EnableConfigurationProperties(value=RocketMQConstant.class)
@Slf4j
public class RocketMQInstance {
	
	@Autowired
	private  RocketMQConstant rocketMQConstant;
	
	/*时间监听器*/
	@Autowired
	private   ApplicationEventPublisher applicationEventPublisher ;
	
	 @Autowired 
	 TransactionCheckListenerImpl  transactionCheckListenerImpl ;
	
	/**
	 * 初始化参数配置
	 */
	@PostConstruct
	public void init (){
		log.info("开始初始化rocketmq基础参数........");
		log.info("rocketmq注册中心["+rocketMQConstant.getNamesrvAddr()+"]");
		log.info("rocketmq生产者组名["+rocketMQConstant.getProducerGrName()+"]");
		log.info("rocketmq事务生产者组名["+rocketMQConstant.getTransactionProducerGrName()+"]");
		log.info("rocketmq消费者组名["+rocketMQConstant.getConsumerGrName()+"]");
		log.info("rocketmq生产者实例名["+rocketMQConstant.getProducerInstanceName()+"]");
		log.info("rocketmq事务生产者实例名["+rocketMQConstant.getTransactionProInstanceName()+"]");
		log.info("rocketmq消费者实例名["+rocketMQConstant.getConsumerInstanceName()+"]");
		log.info("rocketmq消费者最大并发量["+rocketMQConstant.getCounsumerBatchSize()+"]");
		log.info("rocketmq是否启动广播消费["+rocketMQConstant.isConsumerBroadcasting()+"]");
		log.info("rocketmq是否启动历史消费["+rocketMQConstant.isEnableHistoryConsumer()+"]");
		log.info("rocketmq是否启动顺序消费["+rocketMQConstant.isEnableOrderConsumer()+"]");
		log.info("rocketmq获取到的默认订阅数["+rocketMQConstant.getSubscribe().get(0)+"]");
		log.info("rocketmq基础参数完成初始化........");
	}
	
	/**
	 * 启动普通生产者模式
	 * @return
	 * @throws MQClientException 
	 */
	@Bean
	@ConditionalOnClass(DefaultMQProducer.class)
	@ConditionalOnMissingBean(DefaultMQProducer.class)
	@ConditionalOnProperty(prefix="org.", value={"namesrvAddr"})
	public  DefaultMQProducer defaultMQProducer() throws MQClientException{
		DefaultMQProducer defaultMQProducer = new DefaultMQProducer(rocketMQConstant.getProducerGrName());
		defaultMQProducer.setNamesrvAddr(rocketMQConstant.getNamesrvAddr());
		defaultMQProducer.setInstanceName(rocketMQConstant.getProducerInstanceName());
		/*	向Broker发送心跳间隔时间，单位毫秒 默认30000*/
		defaultMQProducer.setHeartbeatBrokerInterval(25000);
		/*轮训Name Server 间隔时间，单位毫秒 默认30000*/
		defaultMQProducer.setPollNameServerInterval(25000);
		defaultMQProducer.setVipChannelEnabled(false);
		/*异步发送失败的话，rocketmq内部重试多少次*/
		defaultMQProducer.setRetryTimesWhenSendAsyncFailed(10);
		/*发送消息超时时间，单位毫秒 默认10000*/
		defaultMQProducer.setSendMsgTimeout(12000);
		defaultMQProducer.start();
		log.info("RocketMQInstance  getDefaultMQProInstance starts ......");
		return defaultMQProducer; 
		
	}
	
	/**
	 * 启动事务生产者模式
	 * @return
	 * @throws MQClientException 
	 */
	@Bean
	@ConditionalOnClass(TransactionMQProducer.class)
	@ConditionalOnMissingBean(TransactionMQProducer.class)
	@ConditionalOnProperty(prefix="org.rocketmq", value={"namesrvAddr"})
	public  TransactionMQProducer transactionMQProducer() throws MQClientException{
		TransactionMQProducer transactionMQProducer =new TransactionMQProducer(rocketMQConstant.getTransactionProducerGrName());
		transactionMQProducer.setNamesrvAddr(rocketMQConstant.getNamesrvAddr());
		transactionMQProducer.setInstanceName(rocketMQConstant.getTransactionProInstanceName());
		transactionMQProducer.setRetryTimesWhenSendAsyncFailed(10);
		/*事务回查最大并发数*/
		transactionMQProducer.setCheckThreadPoolMaxSize(2);
		/*事务回查最小并发数*/
		transactionMQProducer.setCheckThreadPoolMinSize(2);
		/*队列数*/
		transactionMQProducer.setCheckRequestHoldMax(2000);
		/*必须设为false否则连接broker10909端口*/
		transactionMQProducer.setVipChannelEnabled(false);
		transactionMQProducer.setTransactionCheckListener(transactionCheckListenerImpl);
		transactionMQProducer.start();
		log.info("RocketMQInstance getTranMQProInstance starts .....");
		return transactionMQProducer ;
	}
	
	/**
	 * 创建消费者实例
	 * @return
	 * @throws MQClientException 
	 */
	@Bean
	@ConditionalOnClass(DefaultMQPushConsumer.class)
	@ConditionalOnMissingBean(DefaultMQPushConsumer.class)
	@ConditionalOnProperty(prefix="org.rocketmq",value={"namesrvAddr"})
	public  DefaultMQPushConsumer defaultMQPushConsumer() throws MQClientException{
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketMQConstant.getConsumerGrName());
		consumer.setNamesrvAddr(rocketMQConstant.getNamesrvAddr());
		consumer.setInstanceName(rocketMQConstant.getConsumerInstanceName());
		/*启用广播消费模式*/
		if(rocketMQConstant.isConsumerBroadcasting()){
			consumer.setMessageModel(MessageModel.BROADCASTING);
		}
		
		/*设置批量消费*/
		consumer.setConsumeMessageBatchMaxSize(rocketMQConstant.getCounsumerBatchSize()==0?
				1:rocketMQConstant.getCounsumerBatchSize());
		/*消费者与broker保持长连接心跳监测时间间隔 */
		consumer.setHeartbeatBrokerInterval(15000);
			
		List<String> subscribe = rocketMQConstant.getSubscribe();
		if(subscribe !=null && subscribe.size()>0){
			for(String sub : subscribe ){
				consumer.subscribe(sub.split(":")[0],sub.split(":")[1]);
			}
		}
		/*如果启动顺序消费*/
		if(rocketMQConstant.isEnableOrderConsumer()){
			/*给消费者注册消息监听器*/
			consumer.registerMessageListener(new MessageListenerOrderly() {
				
				@Override
				public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
					try
					{
						context.setAutoCommit(true);
						if(CollectionUtils.isEmpty(msgs)){
							log.info("接收到消息为空则消息不用做处理");
							return ConsumeOrderlyStatus.SUCCESS;
						}
						/*消息去重处理*/
						msgs=filterMsg(msgs);
						for(MessageExt ext : msgs ){
							log.info("当前正在处理的消息为["+ext.toString()+"]:"
									+ "消息主题为:"+ "["+ext.getTopic()+"]:"
									+ "消息内容为["+ext.getTags()+"]:"
									+ "该消息重复消费的次数为["+ext.getReconsumeTimes()+"]");
						}
						applicationEventPublisher.publishEvent(new MessageEvent(msgs, consumer));
					}catch (Exception e ){
							log.error("消息消费出现异常"+e.getMessage());
							return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
					}
					return ConsumeOrderlyStatus.SUCCESS;
				}
			});
			
		}else/*如果启动的是并发消费*/
		{
			consumer.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
					try
					{
						if(CollectionUtils.isEmpty(msgs)){
							log.info("接收到消息为空则消息不用做处理");
							return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
						}
						/*消息去重处理*/
						msgs=filterMsg(msgs);
						for(MessageExt ext : msgs ){
							log.info("当前正在处理的消息为["+ext.toString()+"]:"
									+ "消息主题为:"+ "["+ext.getTopic()+"]:"
									+ "消息内容为["+ext.getTags()+"]:"
									+ "该消息重复消费的次数为["+ext.getReconsumeTimes()+"]");
						}
						applicationEventPublisher.publishEvent(new MessageEvent(msgs, consumer));
					}catch (Exception e ){
							log.error("消息消费出现异常"+e.getMessage());
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
		}
		/*开启消费者消费模式*/
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					consumer.start();
				} catch (InterruptedException e) {
					log.error("消费者线程启动出异常"+e.getMessage());
				} catch (MQClientException e) {
					log.error("rocketmq消费者启动出现异常"+e.getErrorMessage());
				}
			}
			
		}).start();
		
		return consumer;
	}
	/**
	 * 消息去重处理
	 * @param msgs
	 * @return
	 */
	private  List<MessageExt> filterMsg( List<MessageExt> msgs ){
		if(rocketMQConstant.isFirstSub() && !rocketMQConstant.isEnableHistoryConsumer()){
			if(msgs !=null && msgs.size()>0){
					msgs=msgs.stream().filter(a ->System.currentTimeMillis()-a.getBornTimestamp()<0)
							.collect(Collectors.toList());
			}
		}
		if(rocketMQConstant.isFirstSub() && msgs.size() >0 ){
			rocketMQConstant.setFirstSub(false);
		}
		
		return msgs;
	}
	
}
