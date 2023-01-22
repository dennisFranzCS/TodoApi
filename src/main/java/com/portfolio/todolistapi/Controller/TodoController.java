package com.portfolio.todolistapi.Controller;

import com.portfolio.todolistapi.ModelDTO.TodoDTO;
import com.portfolio.todolistapi.Services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class TodoController {
    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping(path = "todolist/{todolistId}/todo/{todoId}")
    public ResponseEntity<TodoDTO> getTodo(@PathVariable Long todoId, Principal principal){
        return ResponseEntity.ok(todoService.getTodo(todoId, principal.getName()));
    }

    @GetMapping(path = "todolist/{todolistId}/todo/")
    public ResponseEntity<List<TodoDTO>> getAllTodos(@PathVariable Long todolistId, Principal principal){
        return ResponseEntity.ok(todoService.getAllTodos(todolistId, principal.getName()));
    }

    @PostMapping(path = "todolist/{todolistId}/todo/")
    public ResponseEntity<TodoDTO> createTodo(@PathVariable Long todolistId, @RequestBody TodoDTO todo,
                                              Principal principal){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(todoService.createTodo(todolistId, todo, principal.getName()));
    }

    @PutMapping(path = "todolist/{todolistId}/todo/{todoId}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable Long todolistId, @PathVariable Long todoId,
                                              @RequestBody TodoDTO todo, Principal principal) {
        // todolistId can be used in the future if needed
        return ResponseEntity.ok(todoService.updateTodo(todoId, todo, principal.getName()));
    }

    @DeleteMapping(path = "todolist/{todolistId}/todo/{todoId}")
    public ResponseEntity<TodoDTO> deleteTodo(@PathVariable Long todolistId, @PathVariable Long todoId,
                                              Principal principal) {
        // todolistId can be used in the future if needed
        return ResponseEntity.ok(todoService.deleteTodo(todoId, principal.getName()));
    }
}

