package com.portfolio.todolistapi.Services;

import com.portfolio.todolistapi.DTOMapper.TodolistDTOMapper;
import com.portfolio.todolistapi.Model.Todolist;
import com.portfolio.todolistapi.ModelDTO.TodolistDTO;
import com.portfolio.todolistapi.Repository.TodolistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TodolistService {
    TodolistRepository todolistRepository;
    TodolistDTOMapper todolistDTOMapper;

    @Autowired
    public TodolistService(TodolistRepository todolistRepository) {
        this.todolistRepository = todolistRepository;
        this.todolistDTOMapper = new TodolistDTOMapper();
    }

    public TodolistService(TodolistRepository todolistRepository, TodolistDTOMapper todolistDTOMapper) {
        this.todolistRepository = todolistRepository;
        this.todolistDTOMapper = todolistDTOMapper;
    }

    public TodolistDTO getTodolist(Long todolistId, String owner) throws EntityNotFoundException, AccessDeniedException {
        Todolist todolist = todolistRepository.findById(todolistId).orElseThrow(EntityNotFoundException::new);

        if (!todolist.getOwner().equals(owner)) {
            throw new AccessDeniedException("");
        }

        return todolistDTOMapper.apply(todolist);
    }

    public Todolist getTodolistRef(Long todolistId, String owner) throws EntityNotFoundException, AccessDeniedException {
        Todolist todolist = todolistRepository.findById(todolistId).orElseThrow(EntityNotFoundException::new);

        if (!todolist.getOwner().equals(owner)) {
            throw new AccessDeniedException("");
        }
        return todolist;
    }

    public List<TodolistDTO> getAllTodoslists(String owner) {
        return todolistRepository
                .findAllByOwner(owner, Sort.unsorted())
                .stream()
                .map(todolistDTOMapper)
                .toList();
    }

    public TodolistDTO createTodolist(TodolistDTO todolistInfo, String owner) {
        Todolist todolist = new Todolist();

        if (todolistInfo.name() != null) {
            todolist.setName(todolistInfo.name());
        }
        if (todolistInfo.description() != null) {
            todolist.setDescription(todolistInfo.description());
        }
        todolist.setOwner(owner);

        return todolistDTOMapper.apply(todolistRepository.save(todolist));
    }

    public TodolistDTO updateTodolist(Long todolistId, TodolistDTO todolist, String owner) throws AccessDeniedException {
        Todolist existingTodolist = todolistRepository.findById(todolistId).orElseThrow(EntityNotFoundException::new);

        if (!existingTodolist.getOwner().equals(owner)) {
            throw new AccessDeniedException("");
        }

        if (todolist.name() != null) {
            existingTodolist.setName(todolist.name());
        }
        if (todolist.description() != null) {
            existingTodolist.setDescription(todolist.description());
        }

        todolistRepository.save(existingTodolist);
        return todolistDTOMapper.apply(existingTodolist);
    }

    public TodolistDTO deleteTodolist(Long todolistId, String owner) throws EntityNotFoundException, AccessDeniedException {
        Todolist todolist = todolistRepository.findById(todolistId).orElseThrow(EntityNotFoundException::new);

        if (!todolist.getOwner().equals(owner)) {
            throw new AccessDeniedException("");
        }

        todolistRepository.deleteById(todolist.getId());
        return todolistDTOMapper.apply(todolist);
    }
}
