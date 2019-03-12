package org.yaukie.special.rocketmq.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 定义消息发送者
 * @author yaukie
 */
@NoArgsConstructor
@Accessors(chain=true)
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class UserBean {
	private String id;
	/*消息生产者*/
	private String userName;
	/*消息生产时间*/
	private String date;
	
}
