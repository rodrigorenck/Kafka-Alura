package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;

//consumidor
//sistema de log generico que vai ouvir todos os topicos de ecommerce
public class LogService {

    public static void main(String[] args) {
        var consumer = new KafkaConsumer<String, String>(properties());

        //definimos qual topico esse consumidor esta escutando
        //eh super raro termos um consumidor que escuta de varios topicos porem nesse caso temos!
        consumer.subscribe(Pattern.compile("ECOMMERCE.*"));

        //eh comum deixarmos a chamada do poll em um laço infinito pois queremos que ele fique escutando ate chegar mensagem
        while(true) {
            //consumer pergunta se tem mensagem ai dentro por algum tempo -> recebo uma lista de registros
            var records = consumer.poll(Duration.ofMillis(100));

            if (!records.isEmpty()) {
                System.out.println("Encontrei "  + records.count() +  " registros");
                for (var record :
                        records) {
                    System.out.println("-----------------------------------------");
                    System.out.println("LOG: " + record.topic());
                    System.out.println(record.key());
                    System.out.println(record.value());
                    System.out.println(record.partition());
                    System.out.println(record.offset());
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
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, LogService.class.getSimpleName());
        return properties;
    }
}
