package org.yaukie.common.rocketmq.producer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaukie.common.rocketmq.bean.UserBean;
import org.yaukie.common.rocketmq.config.RocketMQConstant;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * 采用另一种方式实现
 * rocketmq消息生产者
 * @author yaukie
 *
 */
@Component
@Slf4j
public class RocketMQProducer {

	@Autowired
	private RocketMQConstant 	constant;
	
	@PostConstruct
	public void producer(){
		log.info("RocketMQProducer producer 初始化...");
		DefaultMQProducer producer = new DefaultMQProducer();
		producer.setNamesrvAddr(constant.getNamesrvAddr());
		producer.setProducerGroup(constant.getProducerGrName());
		producer.setInstanceName(constant.getProducerInstanceName());
		try {
			log.info("RocketMQProducer producer 准备启动!");
			producer.start();
		} catch (MQClientException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		List<Message> msgs = new LinkedList<Message>();
		/*定义1000个消息进行发送*/
		for(int i=1;i<=1000;i++){
			UserBean user = new UserBean();
			user.setId(i+"");
			user.setUserName("yebn_"+i);
			user.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			String json = JSON.toJSONString(user);
			Message msg = new Message(constant.getSubscribe().get(0).split(":")[0],
					constant.getSubscribe().get(0).split(":")[1],json.getBytes());
			msgs.add(msg);
		}	
		
		try {
			SendResult result = producer.send(msgs);
			log.info("RocketMQProducer producer 消息发送成功!");
		} catch (MQClientException e) {
			e.printStackTrace();
		} catch (RemotingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MQBrokerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
