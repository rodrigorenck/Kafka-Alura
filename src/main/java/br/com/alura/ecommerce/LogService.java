package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;

//consumidor
//sistema de log generico que vai ouvir todos os topicos de ecommerce
public class LogService {

    //eh super raro termos um consumidor que escuta de varios topicos porem nesse caso temos!
    private static final Pattern topic = Pattern.compile("ECOMMERCE.*");
    private static final String groupdId = LogService.class.getName();

    public static void main(String[] args) {
        var logService = new LogService();
        try (var kafkaService = new KafkaService(groupdId, topic, logService::parse)) {
            kafkaService.run();
        }
    }
    private void parse(ConsumerRecord<String, String> record) {
        System.out.println("-----------------------------------------");
        System.out.println("LOG: " + record.topic());
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
    }
}
