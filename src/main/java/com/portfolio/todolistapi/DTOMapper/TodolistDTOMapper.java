package com.portfolio.todolistapi.DTOMapper;

import com.portfolio.todolistapi.Model.Todolist;
import com.portfolio.todolistapi.ModelDTO.TodolistDTO;

import java.util.function.Function;

public class TodolistDTOMapper implements Function<Todolist, TodolistDTO> {
    @Override
    public TodolistDTO apply(Todolist todolist) {
        return new TodolistDTO(
                todolist.getId(),
                todolist.getName(),
                todolist.getDescription(),
                todolist.getCreatedAt()
        );
    }
}
