package com.portfolio.todolistapi.Services;

import com.portfolio.todolistapi.DTOMapper.TodoDTOMapper;
import com.portfolio.todolistapi.Model.Todo;
import com.portfolio.todolistapi.Model.Todolist;
import com.portfolio.todolistapi.ModelDTO.TodoDTO;
import com.portfolio.todolistapi.Repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodolistService todolistService;
    private final TodoDTOMapper todoDTOMapper;

    @Autowired
    public TodoService(TodoRepository todoRepository, TodolistService todolistService) {
        this.todoRepository = todoRepository;
        this.todolistService = todolistService;
        this.todoDTOMapper = new TodoDTOMapper();
    }

    public TodoService(TodoRepository todoRepository, TodolistService todolistService, TodoDTOMapper todoDTOMapper) {
        this.todoRepository = todoRepository;
        this.todolistService = todolistService;
        this.todoDTOMapper = todoDTOMapper;
    }

    public TodoDTO getTodo(Long todoId, String owner) throws EntityNotFoundException, AccessDeniedException {
        Todo todo = todoRepository.findById(todoId).orElseThrow(EntityNotFoundException::new);

        if(!todo.getOwner().equals(owner)) {
            throw new AccessDeniedException("");
        }

        return todoDTOMapper.apply(todo);
    }

    public List<TodoDTO> getAllTodos(Long todolistId, String owner) throws EntityNotFoundException, AccessDeniedException {
        Todolist todolist = todolistService.getTodolistRef(todolistId, owner);

        return todolist.getTodos().stream().map(todoDTOMapper).toList();
    }

    public TodoDTO createTodo(Long todolistId, TodoDTO todoInfo, String owner) throws EntityNotFoundException, AccessDeniedException {
        Todolist todolist = todolistService.getTodolistRef(todolistId, owner);

        Todo todo = new Todo();
        if (todoInfo.name() != null) {
            todo.setName(todoInfo.name());
        }
        if (todoInfo.description() != null) {
            todo.setDescription(todoInfo.description());
        }
        if (todoInfo.completedAt() != null) {
            todo.setCompletedAt(todoInfo.completedAt());
        }
        if (todoInfo.favorite() != null) {
            todo.setFavorite(todoInfo.favorite());
        }
        todo.setOwner(owner);
        todo.setTodolist(todolist);

        return todoDTOMapper.apply(todoRepository.save(todo));
    }

    public TodoDTO updateTodo(Long todoId, TodoDTO todo, String owner) throws EntityNotFoundException, AccessDeniedException {
        Todo todoToUpdate = todoRepository.findById(todoId).orElseThrow(EntityNotFoundException::new);

        if(!todoToUpdate.getOwner().equals(owner)) {
            throw new AccessDeniedException("");
        }

        if (todo.name() != null) {
            todoToUpdate.setName(todo.name());
        }
        if (todo.description() != null) {
            todoToUpdate.setDescription(todo.description());
        }
        if (todo.favorite() != null) {
            todoToUpdate.setFavorite(todo.favorite());
        }
        if (todo.completedAt() != null) {
            todoToUpdate.setCompletedAt(todo.completedAt());
        }

        return todoDTOMapper.apply(todoRepository.save(todoToUpdate));
    }

    public TodoDTO deleteTodo(Long todoId, String owner) throws EntityNotFoundException, AccessDeniedException {
        Todo todo = todoRepository.findById(todoId).orElseThrow(EntityNotFoundException::new);

        if(!todo.getOwner().equals(owner)) {
            throw new AccessDeniedException("");
        }

        todoRepository.deleteById(todoId);

        return todoDTOMapper.apply(todo);
    }
}
