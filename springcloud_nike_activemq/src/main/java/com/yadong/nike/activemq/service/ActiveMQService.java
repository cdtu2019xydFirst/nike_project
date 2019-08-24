package com.yadong.nike.activemq.service;

import com.yadong.nike.activemq.mq.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/24 | 15:19
 **/
@Service
@Transactional
public class ActiveMQService {

    @Autowired
    private ActiveMQUtil activeMQUtil;

    public void sendPayment_checked_queue(String outTradeNumber, Integer count) {
        /*获取mq连接, 在连接中创建会话*/
        Connection connection = null;
        Session session = null;
        /*调用ActiveMQ发送主动检查支付宝回调结果的消息*/
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            Queue payment_checked_queue = session.createQueue("PAYMENT_CHECKED_QUEUE");/*创建队列模式消息*/
            MessageProducer producer = session.createProducer(payment_checked_queue);
            //ActiveMQTextMessage textMessage = new ActiveMQTextMessage();/*字符串形式结构的消息*/
            ActiveMQMapMessage mapMessage = new ActiveMQMapMessage();/*hash形式结构的消息（K   V）结构*/
            mapMessage.setString("out_trade_no", outTradeNumber);
            mapMessage.setInt("count", count);
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000 * 30);/*加入30秒的延迟时间*/
            producer.send(mapMessage);
            session.commit();
        } catch (JMSException e) {
            /*更新失败，回滚*/
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            /*关闭连接*/
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPayment_success_queue(String outTradeNumber) {
        Connection connection = null;
        Session session = null;
        try {
            connection = activeMQUtil.getConnectionFactory().createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue payment_success_queue = session.createQueue("PAYMENT_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(payment_success_queue);
            ActiveMQMapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("out_trade_no", outTradeNumber);
            producer.send(mapMessage);/*发送消息*/
            session.commit();
        } catch (JMSException e) {
            try {
                session.rollback();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
