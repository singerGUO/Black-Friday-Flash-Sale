package com.zhenghao.seckill.mq;


import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RocketMQService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     *  发送消息
     * @param topic
     * @param body
     * @throws Exception
     */
    public void sendMessage(String topic,String body) throws Exception{
        Message message = new Message(topic,body.getBytes());
        DefaultMQProducer myproducer = rocketMQTemplate.getProducer();
        myproducer.setSendMsgTimeout(60000);
        myproducer.send(message);
    }

    /**
     * 发送延时消息
     *
     * @param topic
     * @param body
     * @param delayTimeLevel
     * @throws Exception
     */
    public void sendDelayMessage(String topic, String body, int delayTimeLevel) throws Exception {
        Message message = new Message(topic, body.getBytes());
        message.setDelayTimeLevel(delayTimeLevel);
        rocketMQTemplate.getProducer().send(message);
    }
}
