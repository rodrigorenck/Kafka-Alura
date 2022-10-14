package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

//cria novo pedido de compra
//produtor
//refatoramos o codigo - isolamos a interacao das libs de kafka no kafka dispatcher!
public class NewOrderMain {

    //queremos produzir uma mensagem no kafka
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //try para dar um close no final
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                for (int i = 0; i < 10; i++) {
                    //o kafka decide para qual particao vai mandar a mensagem com base na chave! - vamos fazer ser sempre uma chave diferente para vermos cair em particoes diferentes
                    //vamos simular que nossas chaves serao o id do usuario
                    var userId = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();

                    var amount = BigDecimal.valueOf(Math.random() * 5000 + 1);
                    var order = new Order(userId, orderId, amount);

                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", userId, order);
                    //vamos enviar outro record
                    var email = new Email("Thank you for your order", "We are processing your order");
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userId, "Thank you for your order");
                }
            }
        }
    }
}