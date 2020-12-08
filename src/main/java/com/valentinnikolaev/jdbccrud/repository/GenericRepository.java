package com.valentinnikolaev.jdbccrud.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    Optional<T> add(T entity);

    Optional<T> get(ID id);

    Optional<T> change(T entity);

    boolean remove(ID id);

    List<T> getAll();

    boolean removeAll();

    boolean isContains(ID id);

}
