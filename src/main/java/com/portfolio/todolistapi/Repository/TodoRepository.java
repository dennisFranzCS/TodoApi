package com.portfolio.todolistapi.Repository;

import com.portfolio.todolistapi.Model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    void deleteAllByTodolistIdAndOwner(Long todolistId, String owner);
}
