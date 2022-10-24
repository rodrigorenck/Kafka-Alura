package br.com.alura.ecommerce;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Fast Delegate -> ele passa a mensagem rapido
 * Ele deve fazer poucas coisas - quanto mais codigo aqui maior a chance de dar um erro e eu perder a compra
 * Se eu envio a mensagem e da erro depois eu ja tenho um ponto de partida, por isso devemos enviar a mensagem antes que de algum erro
 */
public class GenerateAllReportsServlet extends HttpServlet {

    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();


    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    //batch -> envia uma mensagem para todos usuarios  -> para cada usuario envia o user_generate_reading_report
                    //vou notificar que deve ser feito para todos usuarios
                    batchDispatcher.send("SEND_MESSAGE_TO_ALL_USERS", "USER_GENERATE_READING_REPORT", "USER_GENERATE_READING_REPORT");

                    System.out.println("Sent generate reports to all users");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("Report request generated");
                } catch (ExecutionException e) {
                    throw new ServletException(e);
                } catch (InterruptedException e) {
                    throw new ServletException(e);
                }
            }
        }

