package org.yaukie.special.rocketmq.event;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * 编写消息监听器
 * @author yaukie
 */
@Slf4j
@Accessors(chain=true)
@ToString
public class MessageEvent extends ApplicationEvent{

	@Getter
	@Setter
	private DefaultMQPushConsumer consumer ;
	
	@Getter
	@Setter
	private List<MessageExt> msgs;
	
	public MessageEvent(List<MessageExt> msgs ,DefaultMQPushConsumer consumer) {
		super(msgs);
		this.consumer=consumer;
		this.setMsgs(msgs);
	}

}
