package cn.nexriver.kafkademo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName MySimpleProducer
 * @Description TODO
 * @Author shi.cq
 * @Date 2022/2/21
 */
@Slf4j
public class MySimpleProducer {

    private final static String TOPIC_NAME = "my-replicated-topic01";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //1.设置参数
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.64.12:9092,192.168.64.12:9093,192.168.64.12:9094");

        //把发送的key从字符串序列化为字节数组
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //把发送消息value从字符串数组序列化为字节数组
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //2.创建生产消息的客户端，传入参数
        Producer<String, String> producer = new KafkaProducer<String, String>(properties);

        //3.创建消息
        //key: 作用是决定了往哪个分区上发, value:具体要发送的消息内容
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC_NAME, 1,"nice3", "helllokafka");

        //发送消息
        RecordMetadata metadata = producer.send(producerRecord).get();
        log.info("[同步方式发送消息结果] topic-{} | partition-{} | offset-{}", metadata.topic(), metadata.partition(), metadata.offset());

    }
}
