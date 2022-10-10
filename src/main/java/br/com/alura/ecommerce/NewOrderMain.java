package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

//cria novo pedido de compra
//produtor
public class NewOrderMain {

    //queremos produzir uma mensagem no kafka
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //ele precisa da chave e do valor (mensagem)
        var producer = new KafkaProducer<String, String>(properties());

        //vamos simular 100 pedidos para ver em quais particoes vai cair
        for (int i = 0; i < 100; i++) {
            //o kafka decide para qual particao vai mandar a mensagem com base na chave! - vamos fazer ser sempre uma chave diferente para vermos cair em particoes diferentes
            //vamos simular que nossas chaves serao o id do usuario
            var key = UUID.randomUUID().toString();
            var value = key + ",2918,10650";

            //recebe o topico como parametro -> depois ele recebe a chave e o valor -> nesse exemplo usaremos a mesma coisa para as duas
            var record = new ProducerRecord<>("ECOMMERCE_NEW_ORDER", key, value);

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

            //vamos enviar outro record
            var email = "Thank you for your order! We are processing your order!";
            var emailRecord = new ProducerRecord<>("ECOMMERCE_SEND_EMAIL", email, email);
            producer.send(emailRecord, callback).get();
        }
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
}
