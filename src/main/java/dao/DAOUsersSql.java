package dao;

import entity.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class DAOUsersSql implements DAO<User> {
  private final String defaultPhoto = "https://www.logolynx.com/images/logolynx/s_cb/cbd29542455b9e0cc175289ff24cecaa.jpeg";
  private final static String URL = "jdbc:postgresql://localhost:5432/postgres";
  private final static String UNAME = "postgres";
  private final static String PWD = "cumayev_99";

//  @Override
//  public Collection<User> getAll() {
//    try {
//      Connection conn = DriverManager.getConnection(URL, UNAME, PWD);
//      String SQL = "select * from users;";
//      return getUsers(conn, SQL);
//    } catch (SQLException e) {
//      return new ArrayList<>();
//    }
//  }

  @Override
  public boolean check(String query) {
    try {
      Connection conn = DriverManager.getConnection(URL, UNAME, PWD);
      PreparedStatement stmt = conn.prepareStatement(query);
      ResultSet rSet = stmt.executeQuery();
      return rSet.next();
    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public Collection<User> getBySQLQuery(String query) {
    try {
      Connection conn = DriverManager.getConnection(URL, UNAME, PWD);
      return getUsers(conn, query);
    } catch (SQLException e) {
      return new ArrayList<>();
    }
  }

  @Override
  public int getID(String query) {
    try {
      Connection conn = DriverManager.getConnection(URL, UNAME, PWD);
      PreparedStatement stmt = conn.prepareStatement(query);
      ResultSet rSet = stmt.executeQuery();
      rSet.next();
      return rSet.getInt("id");
    } catch (SQLException e) {
      return -1;
    }
  }

  @Override
  public Optional<User> get(int id) {
    try {
      Connection conn = DriverManager.getConnection(URL, UNAME, PWD);
      String SQL = String.format("select * from users u where u.id = %s;", id);
      PreparedStatement stmt = conn.prepareStatement(SQL);
      ResultSet rSet = stmt.executeQuery();
      rSet.next();
      int ID = rSet.getInt("id");
      String username = rSet.getString("e-mail");
      String fullName = rSet.getString("fullName");
      String lastLogin = rSet.getString("lastLogin");
      Optional<String> workInfo = Optional.ofNullable(rSet.getString("workInfo"));
      Optional<String> prof_photo = Optional.ofNullable(rSet.getString("prof_photo"));
      return Optional.of(new User(
              ID,
              username,
              fullName,
              workInfo.orElse(""),
              LocalDateTime.parse(lastLogin),
              prof_photo.orElse(defaultPhoto)
      ));
    } catch (SQLException e) {
      return Optional.empty();
    }
  }

  @Override
  public boolean executeSQL(String query) {
    try {
      Connection conn = DriverManager.getConnection(URL, UNAME, PWD);
      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.execute();
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  private Collection<User> getUsers(Connection conn, String SQL) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(SQL);
    ResultSet rSet = stmt.executeQuery();
    ArrayList<User> users = new ArrayList<>();
    while (rSet.next()) {
      int ID = rSet.getInt("id");
      String username = rSet.getString("e-mail");
      String fullName = rSet.getString("fullName");
      String lastLogin = rSet.getString("lastLogin");
      Optional<String> workInfo = Optional.ofNullable(rSet.getString("workInfo"));
      Optional<String> prof_photo = Optional.ofNullable(rSet.getString("prof_photo"));
      users.add(new User(
              ID,
              username,
              fullName,
              workInfo.orElse(" "),
              LocalDateTime.parse(lastLogin),
              prof_photo.orElse(defaultPhoto)
      ));
    }
    return users;
  }
}
