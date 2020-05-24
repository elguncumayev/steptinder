package servlets;

import entity.Message;
import entity.User;
import services.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class MessagesServlet extends HttpServlet {
  private final UserService userService = new UserService();
  private final TemplateEngine engine;

  public MessagesServlet(TemplateEngine engine) {
    this.engine = engine;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Optional<Cookie> sign = Arrays.stream(req.getCookies())
            .filter(cookie -> cookie.getName().equals("sign"))
            .findFirst();
    if (!sign.isPresent()) {
      resp.sendRedirect("/login");
      return;
    }
    String idS = req.getParameter("nf");
    if (idS == null) {
      resp.sendRedirect("/liked");
      return;
    }
    HashMap<String, Object> data = new HashMap<>();
    User partner = userService.getUserName(idS);
    if (partner == null) {
      resp.sendError(404);
      return;
    }
    int id = Integer.parseInt(idS);
    ArrayList<Message> messages = userService.getMessages(Integer.parseInt(sign.get().getValue()), id);
    StringBuilder sb = new StringBuilder();
    for (Message m : messages) {
      if(m.getFrom() == id){
        sb.append("<li class=\"receive-msg float-left mb-2\">")
                .append(String.format("<div class=\"sender-img\"><img src=\"%s\" class=\"float-left\"></div>",
                        partner.getPhoto()))
                .append("<div class=\"receive-msg-desc float-left ml-2\">")
                .append(String.format("<p class=\"bg-white m-0 pt-1 pb-1 pl-2 pr-2 rounded\">%s<br></p>",
                        m.getText()))
                .append(String.format("<span class=\"receive-msg-time\">%s %s, %s</span>",
                        m.getSentTime().getMonth(),
                        m.getSentTime().getDayOfMonth(),
                        m.getSentTime().toLocalTime()))
                .append("</div> </li>");
      }
      else {
        sb.append("<li class=\"send-msg float-right mb-2\">")
                .append("<p class=\"pt-1 pb-1 pl-2 pr-2 m-0 rounded\">")
                .append(String.format("%s</p>",m.getText()))
                .append(String.format("<span class=\"send-msg-time\">%s %s %s</span></li>",
                        m.getSentTime().getMonth(),
                        m.getSentTime().getDayOfMonth(),
                        m.getSentTime().toLocalTime()));
      }
    }

    data.put("partnerName", partner.getFullName());
    data.put("partnerPhoto", partner.getPhoto());
    data.put("messages", sb.toString());
    engine.render("chat.ftl", data, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Optional<Cookie> sign = Arrays.stream(req.getCookies())
            .filter(cookie -> cookie.getName().equals("sign"))
            .findFirst();
    if (!sign.isPresent()) {
      resp.sendRedirect("/login");
      return;
    }
    String id = req.getParameter("nf");
    String text = req.getParameter("message");
    if(text == null || text.matches("[\\s]") || text.isEmpty()) {
      resp.sendRedirect(String.format("/messages?nf=%s",id));
      return;
    }
    userService.sendMessage(sign.get().getValue(),id,text);
    resp.sendRedirect(String.format("/messages?nf=%s",id));
  }
}
/*
                        <li class="receive-msg float-left mb-2">
                            <div class="sender-img">
                                <img src="${partnerPhoto}" class="float-left">
                            </div>
                            <div class="receive-msg-desc float-left ml-2">
                                <p class="bg-white m-0 pt-1 pb-1 pl-2 pr-2 rounded">
                                    How are you ?<br>
                                </p>
                                <span class="receive-msg-time">ketty, Jan 25, 6:20 PM</span>
                            </div>
                        </li>

                        <li class="send-msg float-right mb-2">
                            <p class="pt-1 pb-1 pl-2 pr-2 m-0 rounded">
                                Byy
                            </p>
                            <span class="send-msg-time">1 min</span>
                        </li>
*/