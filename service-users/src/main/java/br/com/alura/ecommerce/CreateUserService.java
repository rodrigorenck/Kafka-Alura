package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private static final String topic = "ECOMMERCE_NEW_ORDER";
    private static final String groupId = CreateUserService.class.getSimpleName();
    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users (" +
                    "uuid varchar(200) primary key, " +
                    "email varchar(200))");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        //como nao queremos colocar configs extras nos criamos um mapa vazio, soh pra o compilador nao incomodar
        try (var kafkaService = new KafkaService<>(groupId, topic,
                createUserService::parse,
                Order.class,
                Map.of())) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("-----------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        Order order = record.value();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
            return;
        }
        System.out.println("User already exists in the database");
    }

    private void insertNewUser(String email) throws SQLException {
        var insert = connection.prepareStatement("insert into Users (uuid, email)" +
                "values(?,?)");
        //ao criar o usuario vamos gerar o id dele -> desse modo caso o usuario ja existaa nao vamos criar outro id
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();
        System.out.println("User uuid and " + email + " added");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("select uuid from Users " +
                "where email = ? limit 1");
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }

}
