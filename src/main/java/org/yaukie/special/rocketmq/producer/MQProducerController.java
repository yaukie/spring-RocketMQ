package org.yaukie.special.rocketmq.producer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaukie.special.rocketmq.bean.UserBean;
import org.yaukie.special.rocketmq.config.RocketMQConstant;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MQProducerController {
	
	@Autowired
	private RocketMQConstant rocketMQConstant;

	@Autowired
	private  DefaultMQProducer defaultMQProducer ;
	
	@Autowired
	private TransactionMQProducer transactionMQProducer ;
	
	@Autowired
	private ApplicationContext context; 
	
	
	@Autowired
	private HttpServletRequest request;
	 
	@Autowired
	private HttpServletResponse response;
	
	/**
	 * 发送普通消息
	 */
	@RequestMapping(path="/sendMsg")
	public  void sendMsg(){
		List<Message> msgs = new LinkedList<Message>();
		/*构造消息体*/
		for(int i =1;i<=60000;i++){
		UserBean user = new UserBean();
		user.setId(String.valueOf(i));
		user.setUserName("yaukie_"+String.valueOf(i));
		user.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		String json = JSON.toJSONString(user);
		Message msg;
		try {
			msg = new Message(rocketMQConstant.getSubscribe().get(0).split(":")[0],
						rocketMQConstant.getSubscribe().get(0).split(":")[1],json.getBytes("UTF-8"));
			msgs.add(msg);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
			try {
				SendResult sendResult = defaultMQProducer.send(msgs,20000);
				log.info("MQProducerController sendMsg 消息发送成功,消息ID为"+sendResult.getMsgId()+",消息状态"+sendResult.getSendStatus());
				response.setContentType("text/plain;charset=utf-8");
				PrintWriter pw = response.getWriter();
				pw.print("{\"result\":\""+sendResult.getSendStatus()+"\",\"MsgId\":\""+sendResult.getMsgId()+"\"}");
			} catch (MQClientException e) {
				e.printStackTrace();
			} catch (RemotingException e) {
				e.printStackTrace();
			} catch (MQBrokerException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	/**
	 * 发送事务消息
	 */
	@RequestMapping(path="/sendTranMsg")
	public  void sendTranMsg(){
		try {
			/*准备发送100个事务消息*/
			for(int i=1;i<100;i++){
			UserBean user = new UserBean();
			user.setId(String.valueOf(i));
			user.setUserName("yebn_trans_"+String.valueOf(i));
			user.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			String json = JSON.toJSONString(user);
			Message msg = new Message(rocketMQConstant.getSubscribe().get(0).split(":")[0],
					rocketMQConstant.getSubscribe().get(0).split(":")[1],json.getBytes());
			SendResult sendResult = transactionMQProducer.sendMessageInTransaction(msg,(Message msg1,Object arg)->{
						Integer value = 0;
						if(arg instanceof Integer ){
								value = (Integer)arg;
						}
						if(value==0){
							throw new RuntimeException("发送的消息不能为空!");
						}else if(value % 5==0){
							log.info("MQProducerControllerxxx事务回查状态为["+LocalTransactionState.COMMIT_MESSAGE+"],消息内容为["+new String(msg1.getBody()));
							return LocalTransactionState.COMMIT_MESSAGE;
						}else if(value % 6==0 && value % 5 !=0){
							log.info("MQProducerControllerxxx事务回查状态为["+LocalTransactionState.ROLLBACK_MESSAGE+"],消息内容为["+new String(msg1.getBody()));
							return LocalTransactionState.ROLLBACK_MESSAGE;
						}
						log.info("MQProducerControllerxxx事务回查状态为["+LocalTransactionState.UNKNOW+"]");
						return LocalTransactionState.UNKNOW;
			},i);
			response.setContentType("text/plain;charset=utf-8");
			PrintWriter pw = response.getWriter();
			pw.print("{\"result\":\""+sendResult.getSendStatus()+"\",\"MsgId\":\""+sendResult.getMsgId()+"\"}");
			log.info("MQProducerController sendTranMsg 消息发送成功,消息ID为"+sendResult.getMsgId()+",消息状态"+sendResult.getSendStatus());
		}
		} catch (MQClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 顺序发送消息
	 */
	@RequestMapping(path="/sendMsgOrder")
	public void sendMsgOrder(){
		List<Message> msgs = new LinkedList<Message>();
		/*构造消息体*/
		for(int i =1;i<=10;i++){
		UserBean user = new UserBean();
		user.setId(String.valueOf(i));
		user.setUserName("yaukie_"+String.valueOf(i));
		user.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		String json = JSON.toJSONString(user);
		Message msg;
		try {
			msg = new Message(rocketMQConstant.getSubscribe().get(0).split(":")[0],
						rocketMQConstant.getSubscribe().get(0).split(":")[1],json.getBytes("UTF-8"));
			SendResult sendResult = defaultMQProducer.send(msg,(List<MessageQueue> mqs, Message msg1, Object arg) ->{
				int 	index = ((Integer)arg) % mqs.size();
				return mqs.get(index);
			},i);
			log.info("MQProducerController sendMsgOrder 消息发送成功,消息ID为"+sendResult.getMsgId()+",消息状态"+sendResult.getSendStatus());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MQClientException e) {
			e.printStackTrace();
		} catch (RemotingException e) {
			e.printStackTrace();
		} catch (MQBrokerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
	
	}
}
