package br.com.alura.ecommerce;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

//cria novo pedido de compra
//produtor
//refatoramos o codigo - isolamos a interacao das libs de kafka no kafka dispatcher!
public class NewOrderMain {

    //queremos produzir uma mensagem no kafka
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //try para dar um close no final
        try (var kafkaDispatcher = new KafkaDispatcher()) {
            for (int i = 0; i < 10; i++) {
                //o kafka decide para qual particao vai mandar a mensagem com base na chave! - vamos fazer ser sempre uma chave diferente para vermos cair em particoes diferentes
                //vamos simular que nossas chaves serao o id do usuario
                var key = UUID.randomUUID().toString();
                var value = key + ",2918,10650";
                kafkaDispatcher.send("ECOMMERCE_NEW_ORDER", key, value));
                //vamos enviar outro record
                var email = "Thank you for your order! We are processing your order!";
                kafkaDispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
            }
        }
    }
}