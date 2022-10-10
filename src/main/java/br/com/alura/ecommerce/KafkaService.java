package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Classe com a logica de criar e executar um consumidor
 */
public class KafkaService implements Closeable {

    private final KafkaConsumer<String, String> consumer;
    private final ConsumerFunction parse;

    /**
     * Vai receber o groupId - pois ele eh diferente dependendo do consumidor
     * Vai receber o TOPICO
     * Vai receber tambem a FUNCAO que ele vai executar para cada mensagem recebida - parse
     */
    public KafkaService(String groupId, String topic, ConsumerFunction parse) {
        this.consumer = new KafkaConsumer<>(properties(groupId));
        this.parse = parse;
        //definimos qual topico esse consumidor esta escutando
        //eh super raro termos um consumidor que escuta de varios topicos - na maior parte das vezes escuta apenas um!
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void run() {
        //eh comum deixarmos a chamada do poll em um laço infinito pois queremos que ele fique escutando ate chegar mensagem
        while (true) {
            //consumer pergunta se tem mensagem ai dentro por algum tempo -> recebo uma lista de registros
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("Encontrei " + records.count() + " registros");
                for (var record :
                        records) {
                    parse.consume(record);
                }
            }
        }
    }


    //recebemos o groupId pois precisamos de um grupo diferente para cada consumidor
    private static Properties properties(String groupId) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        //temos que informar qual o desserializador da chave valor - de bytes para String (no nosso caso)
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //precisamos criar um grupo para o consumer -> vai receber todas as mensagens do topico que ele esta ouvindo
        //se dois servicos tem o mesmo grupo as mensagens serao meio divididas
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}