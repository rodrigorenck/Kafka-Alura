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
public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    var email = req.getParameter("email");

                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(req.getParameter("amount"));

                    var order = new Order(orderId, amount, email);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

                    var emailCode = "Thank you for your order";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

                    System.out.println("New order sent successfully");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("New order sent");
                } catch (ExecutionException e) {
                    throw new ServletException(e);
                } catch (InterruptedException e) {
                    throw new ServletException(e);
                }
            }
        }

