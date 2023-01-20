package com.portfolio.todolistapi.DTOMapper;

import com.portfolio.todolistapi.Model.Todo;
import com.portfolio.todolistapi.ModelDTO.TodoDTO;

import java.util.function.Function;

public class TodoDTOMapper implements Function<Todo, TodoDTO> {
    @Override
    public TodoDTO apply(Todo todo) {
        return new TodoDTO(
                todo.getId(),
                todo.getName(),
                todo.getDescription(),
                todo.getFavorite(),
                todo.getCreatedAt(),
                todo.getCompletedAt()
        );
    }
}