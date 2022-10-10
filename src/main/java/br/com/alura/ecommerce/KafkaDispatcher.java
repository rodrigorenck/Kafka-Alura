package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Classe com a logica de criar um produtor e enviar uma mensagem para algum topico
 * Usamos o nome KafkaDispatcher pois KafkaProducer ja existe!
 * Boa pratica implementar o closeable para fecharmos o servico quando terminarmos
 */
public class KafkaDispatcher implements Closeable {

    private final KafkaProducer<String, String > producer;

    public KafkaDispatcher(){
        this.producer = new KafkaProducer<String, String>(properties());

    }

    //kafka producer recebe como parametro uma properties - pode ser de um arquivo, mas aqui vamos criar na mao
    private static Properties properties() {
        var properties = new Properties();
        //temos que falar onde vamos nos conectar
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        //transformador do tipo da chave e valor (que no nosso caso eh String) para bytes - Serializadores de String para Bytes
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

    //vai receber o topico a chave e o valor
    public void send(String topic, String key, String value) throws ExecutionException, InterruptedException {
        //recebe o topico como parametro -> depois ele recebe a chave e o valor
        var record = new ProducerRecord<>(topic, key, value);

        //send devolve um Future - ou seja algo que vai executar daqui a pouco - ele eh Assincrono
        //entao se eu quiser esperar eu coloco um get que dai ele passa a ser Sincrono - com o get eu espero o Future terminar - joga uma exception pq enquanto tu ta esperando pode dar alguma erro
        Callback callback = (data, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
                return;
            }
            System.out.println("sucesso enviando " + data.topic() + ":::" + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
        };
        producer.send(record, callback).get();
    }

    @Override
    public void close() {
        producer.close();
    }
}