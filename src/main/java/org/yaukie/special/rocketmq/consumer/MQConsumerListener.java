package org.yaukie.special.rocketmq.consumer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.yaukie.special.rocketmq.event.MessageEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 监听消息消费
 * @author yaukie
 */
@Component
@Slf4j
public class MQConsumerListener {

	@EventListener
	public void rocketmqMsgListener(MessageEvent  event){
		DefaultMQPushConsumer consumer = event.getConsumer();
		 List<MessageExt> msgs = event.getMsgs();
		 for(MessageExt ext : msgs ){
			 try {
				log.info("监听到消费者实例["+consumer.getInstanceName()+"]...正在消费["+new String(ext.getBody(),"UTF-8")+"]");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		 }
	}
	
}
