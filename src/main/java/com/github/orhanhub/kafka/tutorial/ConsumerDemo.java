package com.github.orhanhub.kafka.tutorial;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ConsumerDemo.class);

        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my-fourth-application";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //options: read the first is earliest, or the last is latest
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        //Create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        //Subscribe consumer to our topic(s)
        //To subscribe to a single topic use this:
//            consumer.subscribe(Collections.singleton("first_topic"));
        //To subscribe to mulltiple topics
        //consumer.subscribe(Arrays.asList("first_topic", "second_topic"));
        //Or just get single topic as array
        consumer.subscribe(Arrays.asList("first_topic"));

        //poll new data
            while(true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for(ConsumerRecord<String, String> record: records) {
                    logger.info("Key: "+ record.key() + " Value: " + record.value() );
                    logger.info("Partition: " + record.partition() + " Offset: " + record.offset());
                }
            }
        }
}
