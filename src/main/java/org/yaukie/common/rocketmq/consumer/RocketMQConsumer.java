package org.yaukie.common.rocketmq.consumer;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.yaukie.common.rocketmq.config.RocketMQConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * rocketmq消息消费者
 * 采用另一种方式实现
 * @author yaukie
 */
@Component
@Slf4j
public class RocketMQConsumer {

	@Autowired
	private RocketMQConstant 	constant;
	
	@PostConstruct
	public void consumer(){
		log.info("RocketMQConsumer 消费者初始化......");
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
		consumer.setNamesrvAddr(constant.getNamesrvAddr());
		consumer.setConsumerGroup(constant.getConsumerGrName());
		consumer.setInstanceName(constant.getConsumerInstanceName());
		
		if(constant.isConsumerBroadcasting()){
			consumer.setMessageModel(MessageModel.BROADCASTING);
		}
		
		/*设置批量消费*/
		consumer.setConsumeMessageBatchMaxSize(constant.getCounsumerBatchSize()==0?
				1:constant.getCounsumerBatchSize());
		/*消费者与broker保持长连接心跳监测时间间隔 */
		consumer.setHeartbeatBrokerInterval(15000);
			
		List<String> subscribe = constant.getSubscribe();
		if(subscribe !=null && subscribe.size()>0){
			for(String sub : subscribe ){
				try {
					consumer.subscribe(sub.split(":")[0],sub.split(":")[1]);
				} catch (MQClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		/*消息监听开启,并发消费*/
	 consumer.registerMessageListener((MessageListenerConcurrently)(List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
		 	
		 	if(CollectionUtils.isEmpty(msgs)){
		 		log.info("消息不能为空!");
		 		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		 	}
			 msgs=filter(msgs);
			 
		 	for(MessageExt ext : msgs ){
		 		try {
					log.info("当前正在消费的消息为["+new String(ext.getBody(),"utf-8")+"]");
				} catch (Exception e) {
					log.error("消息消费出现异常"+e.getLocalizedMessage());
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;
				}
		 	}
		 	
		 	return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		 
	});
	 
	 	new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					log.info("当前消费者线程为:["+Thread.currentThread().getName()+"]");
					Thread.sleep(2000);
					consumer.start();
				} catch (MQClientException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	 		
	 	}).start();
		
	}
	
	/**
	 * 消息去重处理
	 * @param msgs
	 * @return
	 */
	private  List<MessageExt> filter( List<MessageExt> msgs ){
		if(constant.isFirstSub() && !constant.isEnableHistoryConsumer()){
				 msgs.parallelStream().distinct();
		}
		if(constant.isFirstSub() && msgs.size()>0){
			constant.setFirstSub(false);
		}
		
		return msgs;
	}
	
}
