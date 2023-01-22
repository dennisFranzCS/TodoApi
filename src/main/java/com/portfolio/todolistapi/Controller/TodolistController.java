package com.portfolio.todolistapi.Controller;

import com.portfolio.todolistapi.ModelDTO.TodolistDTO;
import com.portfolio.todolistapi.Services.TodolistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping(path = "/api/v1")
public class TodolistController {
    private final TodolistService todolistService;

    @Autowired
    public TodolistController(TodolistService todolistService) {
        this.todolistService = todolistService;
    }

    @GetMapping(path = "/todolist/{todolistId}")
    public ResponseEntity<TodolistDTO> getTodolist(@PathVariable Long todolistId, Principal principal) {
        return ResponseEntity.ok(todolistService.getTodolist(todolistId, principal.getName()));
    }

    @GetMapping(path = "/todolist/")
    public ResponseEntity<Collection<TodolistDTO>> getAllTodolists(Principal principal) {
        return ResponseEntity.ok(todolistService.getAllTodoslists(principal.getName()));
    }

    @PostMapping(path = "/todolist/")
    public ResponseEntity<TodolistDTO> createTodolist(@RequestBody TodolistDTO todolist, Principal principal) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(todolistService.createTodolist(todolist, principal.getName()));
    }

    @PutMapping(path = "/todolist/{todolistId}")
    public ResponseEntity<TodolistDTO> updateTodolist(@PathVariable Long todolistId, @RequestBody TodolistDTO todolist, Principal principal) {
        return ResponseEntity.ok(todolistService.updateTodolist(todolistId, todolist, principal.getName()));
    }

    @DeleteMapping(path = "/todolist/{todolistId}")
    public ResponseEntity<TodolistDTO> deleteTodolist(@PathVariable Long todolistId, Principal principal) {
        return ResponseEntity.ok(todolistService.deleteTodolist(todolistId, principal.getName()));
    }
}
