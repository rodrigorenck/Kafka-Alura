package br.com.alura.ecommerce;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

//agora ele soh deserializa mensagens
public class GsonDeserializer implements Deserializer<Message> {

    public static final String TYPE_CONFIG = "br.com.alura.ecommerce.type_config";
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

    //agora vamos deserializar sempre uma message
    @Override
    public Message deserialize(String s, byte[] bytes) {
        return gson.fromJson(new String(bytes), Message.class);
    }
}
