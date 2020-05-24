package servlets;

import entity.User;
import services.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class UsersServlet extends HttpServlet {

  private final TemplateEngine engine;
  private final UserService userService = new UserService();
  private int choosesUserId;

  public UsersServlet(TemplateEngine engine) {
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
    try {
      User user = userService.randomUser(Integer.parseInt(sign.get().getValue()));
      HashMap<String, Object> data = new HashMap<>();
      this.choosesUserId = user.getId();
      String name = user.getFullName();
      String image = user.getPhoto();
      data.put("username", name);
      data.put("image", image);
      engine.render("like-page.ftl", data, resp);
    } catch (Exception e) {
      resp.sendRedirect("/liked");
    }
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
    String like = req.getParameter("like");
    if (!userService.relationInteraction(Integer.parseInt(sign.get().getValue()), this.choosesUserId, like != null)) {
      resp.sendRedirect("/liked");
    } else resp.sendRedirect("/users");
  }
}