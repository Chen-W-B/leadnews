package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * 生产者
 */
public class ProducerQuickStart {

    public static void main(String[] args) throws Exception {
        //1.kafka链接配置信息
        Properties prop = new Properties();
        //kafka链接地址
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.200.130:9092");
        //key和value的序列化
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        //ack配置  消息确认机制
        // acks=0           | 生产者在成功写入消息之前不会等待任何来自服务器的响应,消息有丢失的风险，但是速度最快 |
        // acks=1（默认值）   | 只要集群首领节点收到消息，生产者就会收到一个来自服务器的成功响应 |
        // acks=all         | 只有当所有参与赋值的节点全部收到消息时，生产者才会收到一个来自服务器的成功响应 |
        prop.put(ProducerConfig.ACKS_CONFIG,"all");

        //设置重试次数 -- 10次
        //生产者从服务器收到的错误有可能是临时性错误，在这种情况下，
        // retries参数的值决定了生产者可以重发消息的次数，如果达到这个次数，生产者会放弃重试返回错误，
        // 默认情况下，生产者会在每次重试之间等待100ms
        prop.put(ProducerConfig.RETRIES_CONFIG,10);

        //消息压缩 -- 默认情况下， 消息发送时不会被压缩。
        //使用压缩可以降低网络传输开销和存储开销，而这往往是向 Kafka 发送消息的瓶颈所在。
        // snappy | 占用较少的  CPU，  却能提供较好的性能和相当可观的压缩比， 如果看重性能和网络带宽，建议采用 |
        // lz4    | 占用较少的 CPU， 压缩和解压缩速度较快，压缩比也很客观        |
        // gzip   | 占用较多的  CPU，但会提供更高的压缩比，网络带宽有限，可以使用这种算法 |
        prop.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"gzip");

        //2.创建kafka生产者对象
        KafkaProducer<String,String> producer = new KafkaProducer<String,String>(prop);

        //3.发送消息
        /**
         * 第一个参数：topic
         * 第二个参数：消息的key
         * 第三个参数：消息的value
         */
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String,String> kvProducerRecord = new ProducerRecord<String,String>("itcast-topic-input","hello kafka");
            producer.send(kvProducerRecord);
        }

        /*ProducerRecord<String,String> kvProducerRecord = new ProducerRecord<String,String>("topic-first","key--01","hello kafka");
        //同步发送消息  -- 数据量大时，耗时长，可能会产生阻塞
        //RecordMetadata recordMetadata = producer.send(kvProducerRecord).get();
        //System.out.println(recordMetadata.offset());//偏移量

        //异步消息发送
        producer.send(kvProducerRecord, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e != null){
                    System.out.println("记录异常信息到日志表中");
                }
                System.out.println(recordMetadata.offset());
            }
        });*/

        //4.关闭消息通道  必须要关闭，否则消息发送不成功
        producer.close();

    }

}
