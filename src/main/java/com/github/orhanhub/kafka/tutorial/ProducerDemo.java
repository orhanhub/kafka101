package com.github.orhanhub.kafka.tutorial;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerDemo {
    public static void main(String[] args) {

        String bootstrapServers = "127.0.0.1:9092";
        //create Producer properties
        Properties properties = new Properties();
        /*
         * old way
        properties.setProperty("bootstrap.servers", bootstrapServers);
        //Kafka converts info to bytes, so we need to serialize them
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer",StringSerializer.class.getName());
        */
         properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
         properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
         properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create the producer
        //Key to be String and Value to be String
        KafkaProducer<String, String > producer = new KafkaProducer<String ,String >(properties);
            //Create a producer record to send;
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic", "Hello World");
        //send data
        producer.send(record);

        //But the above send is done async so we need to flush and close
        producer.flush();
        producer.close();
    }
}
