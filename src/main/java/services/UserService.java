package services;

import dao.DAO;
import dao.DAOMessagesSql;
import dao.DAOUsersSql;
import entity.Message;
import entity.User;

import java.time.LocalDateTime;
import java.util.*;

public class UserService {

  private final DAO<User> userDAO = new DAOUsersSql();
  private final DAO<Message> messageDAO = new DAOMessagesSql();

  public ArrayList<User> likedPeople(int userID) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.id, u.\"e-mail\", u.\"fullName\", u.\"workInfo\" ,u.\"lastLogin\", u.prof_photo ")
            .append("from relations r ")
            .append("inner join users u on r.\"to\" = u.id ")
            .append(String.format("where r.\"from\"= %s AND r.rel = true;", userID));
    return (ArrayList<User>) userDAO.getBySQLQuery(sb.toString());
  }


  public User randomUser(int userID) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.id, u.\"e-mail\", u.\"fullName\", u.\"workInfo\" ,u.\"lastLogin\", u.prof_photo from users u ")
            .append("left join relations r on u.id = r.\"from\" ")
            .append(String.format("where u.id != %s AND ", userID))
            .append("u.id NOT IN ")
            .append(String.format("(select r.\"to\" from relations r where r.\"from\"= %s);", userID));
    Random random = new Random();
    ArrayList<User> noActionUsers = (ArrayList<User>) userDAO.getBySQLQuery(sb.toString());
    return noActionUsers.get(random.nextInt(noActionUsers.size()));
  }

  public boolean containsMail(String email) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.\"e-mail\" ")
            .append("from users u ")
            .append(String.format("where u.\"e-mail\" = '%s';", email));
    return userDAO.check(sb.toString());
  }

  public boolean checkPass(String email, String password) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.\"e-mail\" ")
            .append("from users u ")
            .append(String.format("where \"e-mail\" = '%s' AND pass = '%s';", email, password));
    return userDAO.check(sb.toString());
  }

  public int getUserID(String email) {
    String sb = String.format("select u.id from users u where u.\"e-mail\" = '%s';", email);
    return userDAO.getID(sb);
  }

  public boolean relationInteraction(int currUserId, int choosesUserId, boolean b) {
    String sb = String.format("INSERT INTO public.relations (\"from\", \"to\", rel) VALUES (%s, %s, %s);",
            currUserId,
            choosesUserId,
            b ? "true" : "false");
    return userDAO.executeSQL(sb);
  }

  public boolean setLoginTime(int id) {
    LocalDateTime now = LocalDateTime.now();
    String sb = String.format("UPDATE public.users SET \"lastLogin\" = '%s' WHERE id = %s", now, id);
    return userDAO.executeSQL(sb);
  }

  public User getUserName(String id) {
    Optional<User> user = userDAO.get(Integer.parseInt(id));
    return user.orElse(null);
  }

  public ArrayList<Message> getMessages(int logUser, int partner) {
    StringBuilder sb = new StringBuilder();
    sb.append("select * from messages m ")
            .append(String.format("where (m.\"from\" = %s and m.\"to\" = %s) or (m.\"from\" = %s and m.\"to\" = %s)",
                    logUser,
                    partner,
                    partner,
                    logUser))
            .append("order by time DESC limit 10;");
    ArrayList<Message> bySQLQuery = (ArrayList<Message>) messageDAO.getBySQLQuery(sb.toString());
    bySQLQuery.sort(Comparator.comparing(Message::getSentTime));
    return bySQLQuery;
  }

  public boolean sendMessage(String logUser, String partner, String message) {
    StringBuilder sb =new StringBuilder();
    sb.append("INSERT INTO public.messages (text, \"from\", \"to\", time)")
            .append(String.format("VALUES ('%s', %s, %s, '%s')",message,logUser,partner,LocalDateTime.now()));
    return userDAO.executeSQL(sb.toString());
  }
}
