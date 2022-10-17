package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//consumidor
public class FraudDetectorService {

    private static final String topic = "ECOMMERCE_NEW_ORDER";
    private static final String groupId = FraudDetectorService.class.getSimpleName();

    //alem de consumir mensagens esse nosso servico vai enviar mensagens tambem
    private final KafkaDispatcher<Order> kafkaDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        var fraudDetectorService = new FraudDetectorService();
        //como nao queremos colocar configs extras nos criamos um mapa vazio, soh pra o compilador nao incomodar
        try (var kafkaService = new KafkaService<>(groupId, topic, fraudDetectorService::parse, Order.class, Map.of())) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
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
            //ignoring
            throw new RuntimeException(e);
        }

        Order order = record.value();
        if (isFraud(order)) {
            //pretending that the fraud happens when the amount is >= 4500
            System.out.println("Order is a fraud!!!");
            kafkaDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), order);
        } else {
            System.out.println("Approved: " + order);
            kafkaDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), order);
        }
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("3500")) >= 0;
    }
}