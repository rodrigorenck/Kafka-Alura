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
                    var orderId = UUID.randomUUID().toString();
                    //nossa chave sera o email do usuario -> quem gera o id do usuario eh o banco de dados!
                    var email = Math.random() + "@email.com";

                    var amount = BigDecimal.valueOf(Math.random() * 5000 + 1);
                    var order = new Order(orderId, amount, email);

                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);
                    //vamos enviar outro record
                    var emailCode = new Email("Thank you for your order", "We are processing your order");
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, "Thank you for your order");
                }
            }
        }
    }
}