package dao;

import java.util.Collection;
import java.util.Optional;

public interface DAO<T> {

//  Collection<T> getAll();

  Collection<T> getBySQLQuery(String query);

  int getID(String query);

  boolean check(String query);

  Optional<T> get(int id);

  boolean executeSQL(String sb);
}