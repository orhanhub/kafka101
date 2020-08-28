package com.github.orhanhub.kafka.tutorial;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoKeys {
    public static void main(String[] args) {

        //Create a Logger for my Class
        final Logger logger = LoggerFactory.getLogger(ProducerDemoKeys.class);

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
        for (int i = 0; i < 50; i++ ) {
            //Create a producer record to send;
            final ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic", "hw_" , "Hello World " + Integer.toString(i));
            //send data // async
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //execute everytime a record is successfully sent or exception thrown
                    if (e == null) {
                        //the record was successfully sent
                        logger.info("Received new metedata: \n" +
                                "Topic: " + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp() + "\n"
                        );
                    } else {
                        logger.error("Error while producing: ", e);
                    }
                }
            });
        }
        //But the above send is done async so we need to flush and close
        producer.flush();
        producer.close();
    }
}
