package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;

//consumidor
public class ReadingReportService {

    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();


    public static void main(String[] args) {
        var reportService = new ReadingReportService();
        //como nao queremos colocar configs extras nos criamos um mapa vazio, soh pra o compilador nao incomodar
        try (var kafkaService = new KafkaService<>(ReadingReportService.class.getSimpleName(),
                "USER_GENERATE_READING_REPORT",
                reportService::parse,
                User.class,
                Map.of())) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        System.out.println("-----------------------------------------");
        System.out.println("Processing report for " + record.value());

        var message = record.value();
        var user = message.getPayload();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " +  user.getUuid());

        System.out.println("File created: " + target.getAbsolutePath());
    }
}