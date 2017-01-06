package xpfei.rabittmq;


import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ消费者
 */
public class MQConsumer {
    private Channel channel;
    private Connection connection;
    private ConnectionFactory factory;
    private final static String QUEUE_SIMULATOR = "M00";
    private OnReceiveMessageHandler mOnReceiveMessageHandler;

    public void setOnReceiveMessageHandler(OnReceiveMessageHandler handler) {
        mOnReceiveMessageHandler = handler;
    }

    public MQConsumer() {
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

    /**
     * @throws IOException
     * @throws TimeoutException
     */
    public void Init() throws IOException, TimeoutException {
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public void setConfig() {
        try {
            // 设置一条条的应答
            channel.basicQos(1);
            channel.queueDeclare(QUEUE_SIMULATOR, true, false, false, null);
            channel.basicConsume(QUEUE_SIMULATOR, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    super.handleDelivery(consumerTag, envelope, properties, body);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    mOnReceiveMessageHandler.onReceiveMessage(body);
                    Log.e("Tag", "消息：" + new String(body));
                }
            });
        } catch (IOException e) {
            Log.e("Tag", "消息接收失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void dispush() {
        try {
            connection.close();
            channel.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface OnReceiveMessageHandler {
        void onReceiveMessage(byte[] message);
    }
}
