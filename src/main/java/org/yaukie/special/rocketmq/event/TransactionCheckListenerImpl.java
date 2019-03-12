package org.yaukie.special.rocketmq.event;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
/**
 * 本地事务回查,,
 * 回查是由broker发起,生产者调用事务状态检查机制,通知broker当前消息的事务状态
 * 
 * @author yaukie
 *
 */
@Component
@Slf4j
public class TransactionCheckListenerImpl implements TransactionCheckListener {

		private AtomicInteger transactionIndex =new AtomicInteger();
	
	@Override
	public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
		Integer value = transactionIndex.getAndDecrement();
		if(value==0){
			throw new RuntimeException("发送的消息不能为空!");
		}else if(value % 5==0){
			log.info("TransactionCheckListenerImpl====事务回查状态为["+LocalTransactionState.COMMIT_MESSAGE+"]");
			return LocalTransactionState.COMMIT_MESSAGE;
		}else if(value % 6==0 && value % 5 !=0){
			log.info("TransactionCheckListenerImpl====事务回查状态为["+LocalTransactionState.ROLLBACK_MESSAGE+"]");
			return LocalTransactionState.ROLLBACK_MESSAGE;
		}
		log.info("TransactionCheckListenerImpl====事务回查状态为["+LocalTransactionState.UNKNOW+"]");
		return LocalTransactionState.UNKNOW;
}

}
