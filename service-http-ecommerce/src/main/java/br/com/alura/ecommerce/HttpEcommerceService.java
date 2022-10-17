package br.com.alura.ecommerce;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpEcommerceService {

    //vai rodar um servidor
    public static void main(String[] args) throws Exception {
        var server = new Server(8080);

        var context = new ServletContextHandler();
        //localhost:8080/
        context.setContextPath("/");
        //localhost:8080/new
        context.addServlet(new ServletHolder(new NewOrderServlet()), "/new");

        server.setHandler(context);

        server.start();
        server.join();
    }
}
