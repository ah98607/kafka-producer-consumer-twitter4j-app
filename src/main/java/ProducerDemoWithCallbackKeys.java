import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallbackKeys {
    public static void main(String[] args) {

        // bootstrap server
        String bootstrapServer = "127.0.0.1:9092";

        // create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // logger
        final Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallbackKeys.class.getName());

        // initialize producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // send some data
        for (int i = 0; i < 10; i++) {
            String topic = "first_topic";
            String value = "Hello World #" + i;
            // if you run main again, message with the same key will go to
            // the same partition where the message went previously
            String key = "id_" + i;
            //logger.info(("key = " + key));

            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);
            //producer.send(record);
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // this function is called every time a record is sent
                    if (e == null) {
                        logger.info("Received meta data \n" +
                                "Topic: " + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    }
                    else {
                        logger.error("Exception while producing", e);
                    }
                }
            });
        }
        producer.flush();
        producer.close();

        // how to test
        // suppose topic "first_topic" alerady exists
        // run kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic
        // click run main

    }
}
