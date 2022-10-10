package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

//consumidor
public class FraudDetectorService {

    private static final String topic = "ECOMMERCE_NEW_ORDER";
    private static final String groupId = FraudDetectorService.class.getSimpleName();

    public static void main(String[] args) {
        var fraudDetectorService = new FraudDetectorService();
        try(var kafkaService = new KafkaService(groupId, topic, fraudDetectorService::parse)){
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record) {
        System.out.println("-----------------------------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        //esse thread sleep eh soh pra simular um sistema de verdade que nao seria instantaneo
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            //ignorando
            throw new RuntimeException(e);
        }
        System.out.println("Order processed");
    }
}