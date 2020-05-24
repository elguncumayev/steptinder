package controller;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.*;

public class TinderApp {
  public void start() throws Exception {
    Server server = new Server(8080);
    ServletContextHandler handler = new ServletContextHandler();

    TemplateEngine engine = TemplateEngine.folder("E:\\0Elgun\\BACKEND\\step-project-tinder\\content");

    handler.addServlet(new ServletHolder(new StaticServlet("js")), "/js/*");
    handler.addServlet(new ServletHolder(new StaticServlet("css")), "/css/*");
    handler.addServlet(new ServletHolder(new UsersServlet(engine)), "/users");
    handler.addServlet(new ServletHolder(new LikedServlet(engine)), "/liked");
    handler.addServlet(new ServletHolder(new MessagesServlet(engine)), "/messages/*");
    handler.addServlet(new ServletHolder(new LoginServlet(engine)), "/login");
    handler.addServlet(new ServletHolder(new RedirectServlet("/login")), "/*");
    server.setHandler(handler);

    server.start();
    server.join();

  }
}
