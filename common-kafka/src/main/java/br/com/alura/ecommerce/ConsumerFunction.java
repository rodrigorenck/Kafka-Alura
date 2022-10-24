package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public interface ConsumerFunction<T> {
    //recebe uma mensagem e faz alguma coisa com ela
    //eh raro colocarmos o throw Exception -> soh quando  a gente quer tratar qualquer tipo de excecao
    void consume(ConsumerRecord<String, Message<T>> record) throws Exception;
}