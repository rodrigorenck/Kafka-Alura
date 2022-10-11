package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

//consumidor
//refatoramos - deixamos a maior parte da interacao com as libs de kafka na KafkaService
public class EmailService {

    private static final String topic = "ECOMMERCE_SEND_EMAIL";
    private static final String groupId = EmailService.class.getSimpleName();

    public static void main(String[] args) {
        var emailService = new EmailService();
        try (var kafkaService = new KafkaService(groupId, topic, emailService::parse, String.class, Map.of())) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record) {
        System.out.println("-----------------------------------------");
        System.out.println("Sending email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        //esse thread sleep eh soh pra simular um sistema de verdade que nao seria instantaneo
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //ignorando
            throw new RuntimeException(e);
        }
        System.out.println("Email sent");
    }
}