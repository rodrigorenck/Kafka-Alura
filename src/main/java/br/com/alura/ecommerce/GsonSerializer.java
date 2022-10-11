package br.com.alura.ecommerce;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serializer;

//temos que implementar o serializer do kafka -> ele vai transormar qualquer objeto nosso em bytes
public class GsonSerializer<T> implements Serializer<T> {
    private final Gson gson = new GsonBuilder().create();

    @Override
    public byte[] serialize(String s, T t) {
        return gson.toJson(t).getBytes();
    }
}
