package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * Classe com a logica de criar e executar um consumidor
 */
public class KafkaService<T> implements Closeable {

    private final KafkaConsumer<String, T> consumer;
    private final ConsumerFunction<T> parse;

    /**
     * Vai receber o groupId - pois ele eh diferente dependendo do consumidor
     * Vai receber o TOPICO
     * Vai receber tambem a FUNCAO que ele vai executar para cada mensagem recebida - parse
     */
    //properties - propriedades extras
    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
        this(groupId, parse, type, properties);
        //definimos qual topico esse consumidor esta escutando
        //eh super raro termos um consumidor que escuta de varios topicos - na maior parte das vezes escuta apenas um!
        consumer.subscribe(Collections.singletonList(topic));
    }

    //construtor para o logService - que recebe uma regex - pattern
    public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
        this(groupId, parse, type, properties);
        consumer.subscribe(topic);
    }

    private KafkaService(String groupId, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
        this.consumer = new KafkaConsumer<>(getProperties(type, groupId, properties));
        this.parse = parse;
    }


    public void run() {
        //eh comum deixarmos a chamada do poll em um laço infinito pois queremos que ele fique escutando ate chegar mensagem
        while (true) {
            //consumer pergunta se tem mensagem ai dentro por algum tempo -> recebo uma lista de registros
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println();
                System.out.println("Found " + records.count() + " records");
                for (var record :
                        records) {
                    try {
                        parse.consume(record);
                    } catch (Exception e) {
                        //only catches Exception because no matter which Exception I want to recover and parse the next one
                        //so far, just logging the exception for this message(record)
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    //recebemos o groupId pois precisamos de um grupo diferente para cada consumidor
    //com o override properties nos permitimos que quem chamar o metodo faca configuracoes extras, diferentes da padrao
    private Properties getProperties(Class<T> type, String groupId, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        //temos que informar qual o desserializador da chave valor - de bytes para String (no nosso caso)
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //queremos que ele seja capaz de deserializar qualquer objeto - GsonDeserializer
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        //precisamos criar um grupo para o consumer -> vai receber todas as mensagens do topico que ele esta ouvindo
        //se dois servicos tem o mesmo grupo as mensagens serao meio divididas
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
        //sobrescrevemos as configs padroes com as configs que o cara que chamou queira
        properties.putAll(overrideProperties);
        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}