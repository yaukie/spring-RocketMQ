** spring-RocketMQ **
<br>
springboot+RocketMQ实现spring与rocketmq的集成,作为一个范例提供给大家研究
 <br>
**说明**
<br>
Apache RocketMQ是阿里开源的一款高性能、高吞吐量、队列模型的消息中间件的分布式消息中间件。RocketMQ具有以下特点： <br>
1）是一个队列模型的消息中间件，具有高性能、高可靠、高实时、分布式特点。 <br>
2）Producer、Consumer、队列都可以分布式。 <br>
3）Producer向一些队列轮流发送消息，队列集合称为Topic，Consumer如果做广播消费，则一个consumer实例消费这个Topic对应的所有队列， <br>
如果做集群消费，则多个Consumer实例平均消费这个topic对应的队列集合。 <br>
4）支持严格的消息顺序； <br>
5）提供丰富的消息拉取模式 <br>
6）高效的订阅者水平扩展能力 <br>
7）实时的消息订阅机制 <br>
8）亿级消息堆积能力 <br>
9）较少的依赖 <br>
10）支持Topic与Queue两种模式； <br>
11）同时支持Push与Pull方式消费消息； <br>
 **消息队列的应用场景** <br>
**1）异步处理 **<br>
将不是必须的业务逻辑，进行异步处理，比如注册之后短信、邮箱的发送 <br>

**2）应用解耦** <br>
订单系统：用户下单后，订单系统完成持久化处理，将消息写入消息队列，返回用户订单下单成功。 <br>
库存系统：订阅下单的消息，采用拉/推的方式，获取下单信息，库存系统根据下单信息，进行库存操作。 <br>
假如：在下单时库存系统不能正常使用。也不影响正常下单，因为下单后，订单系统写入消息队列就不再关心其他的后续操作了。实现订单系统与库存系统的应用解耦。 <br>

**3）流量削锋**，<br>也是消息队列中的常用场景，一般在秒杀或团抢活动中使用广泛。 <br>
应用场景：秒杀活动，一般会因为流量过大，导致流量暴增，应用挂掉。为解决这个问题，一般需要在应用前端加入消息队列。 <br>
a）可以控制活动的人数； <br>
b）可以缓解短时间内高流量压垮应用； <br>
c）用户的请求，服务器接收后，首先写入消息队列。假如消息队列长度超过最大数量，则直接抛弃用户请求或跳转到错误页面； <br>
d）秒杀业务根据消息队列中的请求信息，再做后续处理。 <br>

**4）日志处理** <br>

**5）消息通讯** <br>
消息通讯是指，消息队列一般都内置了高效的通信机制，因此也可以用在纯的消息通讯。比如实现点对点消息队列，或者聊天室等。 <br>

**6）性能**<br>
RocketMQ单机也可以支持亿级的消息堆积能力。单机写入TPS单实例约7万条/秒，单机部署3个Broker，可以跑到最高12万条/秒，消息大小10个字节 <br> <br>

**RocketMQ的集群部署**<br>

1）Name Server 可集群部署，节点之间无任何信息同步。<br>
2）Broker(消息中转角色，负责存储消息，转发消息) 部署相对复杂，Broker 分为Master 与Slave，一个Master 可以对应多个Slave，但是一个Slave 只能对应一个<br>
Master，Master 与Slave 的对应关系通过指定相同的BrokerName，不同的BrokerId来定 义，BrokerId为0 表示Master，非0 表示Slave。Master 也可以部署多个。<br>
3）Producer 与Name Server 集群中的其中一个节点（随机选择）建立长连接，定期从Name Server 取Topic 路由信息，并向提供Topic 服务的Master 建立长连接<br>
，且定时向Master 发送心跳。Producer 完全无状态，可集群部署。<br>
4）Consumer 与Name Server 集群中的其中一个节点（随机选择）建立长连接，定期从Name Server 取Topic 路由信息，并向提供Topic 服务的Master、Slave 建立<br>
长连接，且定时向Master、Slave 发送心跳。Consumer既可以从Master 订阅消息，也可以从Slave 订阅消息，订阅规则由Broker 配置决定。<br>
<br>
Broker：消息中转角色，负责存储消息，转发消息<br>
**Broker集群有多种配置方式：**<br>
**1）单Master<br>**
优点：除了配置简单没什么优点<br>
缺点：不可靠，该机器重启或宕机，将导致整个服务不可用<br>
**2）多Master**<br>
优点：配置简单，性能最高<br>
缺点：可能会有少量消息丢失（配置相关），单台机器重启或宕机期间，该机器下未被消费的消息在机器恢复前不可订阅，影响消息实时性<br>
**3）多Master多Slave**，每个Master配一个Slave，有多对Master-Slave，集群采用异步复制方式，主备有短暂消息延迟，毫秒级<br>
优点：性能同多Master几乎一样，实时性高，主备间切换对应用透明，不需人工干预<br>
缺点：Master宕机或磁盘损坏时会有少量消息丢失<br>
**4）多Master多Slave**，每个Master配一个Slave，有多对Master-Slave，集群采用同步双写方式，主备都写成功，向应用返回成功<br>
优点：服务可用性与数据可用性非常高<br>
缺点：性能比异步集群略低，当前版本主宕备不能自动切换为主<br>

Master和Slave的配置文件参考conf目录下的配置文件<br>
Master与Slave通过指定相同的brokerName参数来配对，Master的BrokerId必须是0，Slave的BrokerId必须是大于0的数。<br>
一个Master下面可以挂载多个Slave，同一Master下的多个Slave通过指定不同的BrokerId来区分。<br>

Rocketmq默认给出了三种建议配置模式: 
(1) 2m-2s-async(主从异步); 
(2) 2m-2s-sync(主从同步); 
(3) 2m-noslave(仅master)<br>

**功能**
	-- 
<br> 

**结构** 
springRocketMQ<br> 
├─common 普通实现方式<br> 
│  ├─bean bean实例<br> 
│  ├─config 配置信息<br> 
│  ├─consumer 消费者<br> 
│  └─producer 生产者<br> 
 ├─special 注解实现方式<br> 
│  ├─bean bean实例<br> 
│  ├─config 配置信息<br> 
│  ├─context 获取会话上下文<br> 
│  ├─event 时间监听器<br> 
│  ├─consumer 消费者<br> 
│  └─producer 生产者<br> 
 ├─resources<br> 
│  ├─application.properties(springboot默认配置)<br> 
│  ├─rocketmq.properties(rocketmq基础配置)<br> 
<br> 
**部署**<br> 
	--通过git命令检到本地之后,直接运行org\yaukie\RocketMQApplicationStart这个类,启动项目<br> 
**注意**<br> 
本项目采用了rocketmq 集群模式,需要读者自行搭建一个rocketmq的2m-2s-async的主从异步集群,并将集群信息配置到<br> 
rocketmq.properties中<br> 
具体的搭建过程请自行百度实现<br> 
完





