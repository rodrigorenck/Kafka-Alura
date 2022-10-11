package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {
    //recebe uma mensagem e faz alguma coisa com ela
    void consume(ConsumerRecord<String, T> record);
}