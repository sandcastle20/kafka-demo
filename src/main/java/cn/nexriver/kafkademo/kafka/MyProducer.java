//package cn.nexriver.kafkademo.kafka;
//
//import cn.nexriver.kafkademo.entity.Order;
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.producer.*;
//import org.apache.kafka.common.serialization.StringSerializer;
//
//import java.util.Properties;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutionException;
//
///**
// * @ClassName MyProdicer
// * @Description TODO
// * @Author shi.cq
// * @Date 2022/2/20
// */
//@Slf4j
//public class MyProducer {
//
//    private final static String TOPIC_NAME = "my-replicated-topic";
//
//    public static <i> void main(String[] args) throws ExecutionException, InterruptedException {
//        Properties properties = new Properties();
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.6:9092,192.168.1.6:9093,192.168.1.6:9094");
//        /**
//         * 发送消息持久化机制参数
//         * (1) acks=0 表示producer不需要等待任何broker确认收到消息的回复，就可以继续发送下一条消息。性能最高，
//         * 但是容易丢消息
//         * (2) acks=1 至少要等待leader已经成功将数据写入本地log,但是不需要等待所有follower是否成功写入。就可以继续发送下一条消息。
//         * 这种情况下，如果follower没有成功备份数据，而此时leader又挂掉，则消息会丢失
//         * (3) acks=-1或all:需要等待min insync replicas(默认为1，推荐配置大于2)这个参数配置的副本个数都成功写入日志，这种策略会保证
//         * 只要有一个备份存活就不会丢失数据。这是最强的数据保证。一般除非是金融级别，或是跟钱打交道的场景才会使用这种配置
//         * @param args
//         * @return
//         **/
//        properties.put(ProducerConfig.ACKS_CONFIG, "1");
//        /**
//         * 发送失败会重试，默认重试间隔100ms,重试能保证消息发送的可高性，但是也可能造成消息重复发送，比如网络抖动，所以需要在接受
//         * 者那边做好消息接受的幂等性处理
//         * @return
//         **/
//        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
//        // 重试时间间隔
//        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 300);
//        // 设置发送消息的本地缓存区，如果设置了该缓冲区，消息会先发送到本地缓冲区，可以提供消息发送性能，默认是33554432，即32MB
//        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
//        /**
//         * kafka本地线程会从缓存区取数据，批量发送到broker
//         * 设置批量发送消息的大小，默认实16384，即16kb.就是说一个batch满了16kb就发送出去
//         **/
//        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
//        /**
//         * 默认值是0，意思就是消息必须立即被发送，但这样会影响性能
//         * 一般设置10毫秒左右，就是说这个消息发送完后会进入本地的一个batch,如果10毫秒内，这个batch满了16kb就会
//         * 随batch一起被发送出去，如果10毫秒内，batch没满，那么也必须把消息发送出去，不能让消息发送延时队列时间太长
//         **/
//        properties.put(ProducerConfig.LINGER_MS_CONFIG, 10);
//        // 把发送的key从字符串序列化为字节数组
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        // 把发送的value从字符串序列化为字节数组
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//
//        // 发消息的客户端
//        Producer<String, String> producer = new KafkaProducer<String, String>(properties) {
//            // 要发送5条消息
//            int msgNum = 5;
//            final CountDownLatch countDownLatch = new CountDownLatch(msgNum);
//
//            for (int i = 1; j < 100000000000000L; j++) {
//                Order order = new Order((long) i, i);
//                // 指定发送分区
////          ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC_NAME, 0, order.getOrderId().toString(), JSON.toJSONString(order));‘
//                // 未指定发送分区，
//                // 具体发送的分区计算公式：hash(key) % patitionNum
//                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC_NAME, order.getOrderId().toString(), JSON.toJSONString(order));
////            try {
////                // 等待消息发送成功后的同步阻塞方法
////                RecordMetadata metadata = producer.send(producerRecord).get();
////                // 阻塞
////                log.info("[同步方式发送消息结果] topic-{} | partition-{} | metadata-{} | offset-{}", TOPIC_NAME, metadata.topic(), metadata.partition(), metadata.offset());
////            }catch (InterruptedException exception){
////                exception.printStackTrace();
////                // 1.记录日志 预警系统+1
////                // 2.设置时间间隔1s 同步的方式再次发送，如果还是不行 日志预警 人工介入
////                Thread.sleep(1000);
////                try {
////                    //等待消息发送成功的同步阻塞方法
////                    RecordMetadata metadata = producer.send(producerRecord).get();
////                }catch (ExecutionException){
////                    //TODO 人工介入
////                }
////            }catch (ExecutionException e){
////                e.printStackTrace();
////            }
//
//                // 异步回调方式发送消息
//                producer.send(producerRecord, new Callback() {
//                    @Override
//                    public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
//                        if (exception != null) {
//                            log.info("[发送消息失败]", exception.getStackTrace());
//                        }
//
//                        if (recordMetadata != null) {
//                            log.info("[异步反思发送消息结果] topic-{} | partition-{} | offset-{}", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
//                        }
//                        countDownLatch.countDown(); //-1 = 4  ==0 5个回复全部都执行完毕
//                    }
//                });
//            }
//
//        };
//    }
//
//}
