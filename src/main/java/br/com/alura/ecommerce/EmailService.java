package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

//consumidor
public class EmailService {

    public static void main(String[] args) {
        var consumer = new KafkaConsumer<String, String>(properties());

        //definimos qual topico esse consumidor esta escutando
        //eh super raro termos um consumidor que escuta de varios topicos - na maior parte das vezes escuta apenas um!
        consumer.subscribe(Collections.singletonList("ECOMMERCE_SEND_EMAIL"));

        //eh comum deixarmos a chamada do poll em um laço infinito pois queremos que ele fique escutando ate chegar mensagem
        while(true) {
            //consumer pergunta se tem mensagem ai dentro por algum tempo -> recebo uma lista de registros
            var records = consumer.poll(Duration.ofMillis(100));

            if (!records.isEmpty()) {
                System.out.println("Encontrei "  + records.count() +  " registros");
                for (var record :
                        records) {
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
        }
    }

    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        //temos que informar qual o desserializador da chave valor - de bytes para String (no nosso caso)
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //precisamos criar um grupo para o consumer -> vai receber todas as mensagens do topico que ele esta ouvindo
        //se dois servicos tem o mesmo grupo as mensagens serao meio divididas
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, EmailService.class.getSimpleName());
        return properties;
    }
}
