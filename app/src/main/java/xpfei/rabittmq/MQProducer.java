package xpfei.rabittmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * 描 述:RabbitMQ生产者
 * 作 者: xpfei
 * 时 间: 2016/12/9
 */
public class MQProducer {
    private Channel channel;
    private Connection connection;
    private ConnectionFactory factory;
    private final static String QUEUE_SIMULATOR = "xpfei";

    public MQProducer() {
        factory = new ConnectionFactory();
        factory.setHost("172.16.0.217");
        factory.setPort(5672);
        factory.setUsername("xpfei");
        factory.setPassword("123456");
        factory.setRequestedHeartbeat(10);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setConnectionTimeout(15000);// 15秒
        // 关键所在，指定线程池
        ExecutorService service = Executors.newFixedThreadPool(10);
        factory.setSharedExecutor(service);
    }

    public void Init() throws IOException, TimeoutException {
        connection = factory.newConnection();
        //创建一个渠道
        channel = connection.createChannel();
    }

    public void send(String msg) {
        try {
            //为channel定义queue的属性，queueName为Queue名称
            channel.queueDeclare(QUEUE_SIMULATOR, true, false, false, null);
            //发送消息
            channel.basicPublish("", QUEUE_SIMULATOR, null, msg.getBytes());
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
