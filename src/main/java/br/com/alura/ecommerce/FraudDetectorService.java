package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.Map;

//consumidor
public class FraudDetectorService {

    private static final String topic = "ECOMMERCE_NEW_ORDER";
    private static final String groupId = FraudDetectorService.class.getSimpleName();

    public static void main(String[] args) {
        var fraudDetectorService = new FraudDetectorService();
        //como nao queremos colocar configs extras nos criamos um mapa vazio, soh pra o compilador nao incomodar
        try(var kafkaService = new KafkaService(groupId, topic, fraudDetectorService::parse, Order.class, Map.of())){
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) {
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