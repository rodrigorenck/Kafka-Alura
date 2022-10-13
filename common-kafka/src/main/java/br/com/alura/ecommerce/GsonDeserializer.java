package br.com.alura.ecommerce;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class GsonDeserializer<T> implements Deserializer<T> {

    public static final String TYPE_CONFIG = "br.com.alura.ecommerce.type_config";
    private final Gson gson = new GsonBuilder().create();
    private Class<T> type;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        String typeName = String.valueOf(configs.get(TYPE_CONFIG));
        try {
            this.type = (Class<T>) Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Type for deserialization does not exist in classpath. " + e);
        }
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        //quando voce for deserializar voce tem que saber qual o tipo que voce ta deserializando
        // -> portanto temos que passar para ele qual a classe de origem que estamos deserializando
        return gson.fromJson(new String(bytes), type);
    }
}
