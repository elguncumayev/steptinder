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
import java.util.List;
import java.util.Optional;

public class LikedServlet extends HttpServlet {
  private final TemplateEngine engine;
  private final UserService userService = new UserService();

  public LikedServlet(TemplateEngine engine) {
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
    HashMap<String, Object> data = new HashMap<>();
    List<User> users = userService.likedPeople(Integer.parseInt(sign.get().getValue()));
    data.put("users",users);
    engine.render("people-list.ftl", data, resp);
  }
}
