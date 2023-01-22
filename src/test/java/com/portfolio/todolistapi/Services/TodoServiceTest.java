package com.portfolio.todolistapi.Services;

import com.portfolio.todolistapi.DTOMapper.TodoDTOMapper;
import com.portfolio.todolistapi.Model.Todo;
import com.portfolio.todolistapi.Model.Todolist;
import com.portfolio.todolistapi.ModelDTO.TodoDTO;
import com.portfolio.todolistapi.Repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private TodolistService todolistService;
    private TodoService underTest;
    private final TodoDTOMapper todoDTOMapper = new TodoDTOMapper();

    @BeforeEach
    void setUp() {
        this.underTest = new TodoService(this.todoRepository, this.todolistService);
    }

    @Test
    void getTodo() {
        // given
        String todoOwner = "todo_owner";
        Todolist todolist = new Todolist(1L, todoOwner);
        Todo todo = new Todo(2L, todoOwner, "todo_name", "todo_description", true,
                Instant.now(), null, todolist);

        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

        // when
        TodoDTO result = underTest.getTodo(todo.getId(), todoOwner);

        // then
        assertThat(result).isEqualTo(todoDTOMapper.apply(todo));
        verify(todoRepository).findById(todo.getId());
    }

    @Test
    void getTodo_NotExistingTodoId() {
        // given
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> underTest.getTodo(1L, null));
        verify(todoRepository).findById(1L);
    }

    @Test
    void getTodo_WrongOwner() {
        // given
        String todoOwnerCorrect = "todo_owner_correct";
        String todoOwnerWrong = "todo_owner_wrong";
        Todolist todolist = new Todolist(1L, todoOwnerCorrect);
        Todo todo = new Todo(2L, todoOwnerCorrect, "todo_name", "todo_description", true,
                Instant.now(), null, todolist);

        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

        // when & then
        assertThrows(AccessDeniedException.class, () -> underTest.getTodo(todo.getId(), todoOwnerWrong));
        verify(todoRepository).findById(todo.getId());
    }

    @Test
    void getAllTodos() {
        // given
        String todoOwner = "todo_owner";
        Todolist todolist = new Todolist(1L, "todolist_name", "todolist_name", todoOwner);
        List<Todo> todos = new ArrayList<>();
        todos.add(new Todo(2L, "todo_name_1", "todo_description_1", false, todoOwner,todolist));
        todos.add(new Todo(3L, "todo_name_2", "todo_description_2", true, todoOwner, todolist));
        todos.add(new Todo(4L, "todo_name_3", "todo_description_3", false, todoOwner, todolist));
        todolist.setTodos(todos);
        when(this.todolistService.getTodolistRef(todolist.getId(), todoOwner)).thenReturn(todolist);

        // when
        List<TodoDTO> todosResult = underTest.getAllTodos(1L, todoOwner);

        // then
        verify(todolistService).getTodolistRef(todolist.getId(), todoOwner);
        assertThat(todosResult).isEqualTo(todos.stream().map(todoDTOMapper).toList());
    }

    @Test
    void getAllTodos_NotExistingTodolistId() {
        // given
        String todoOwner = "todo_owner";

        when(this.todolistService.getTodolistRef(1L, todoOwner)).thenThrow(new EntityNotFoundException());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> underTest.getAllTodos(1L, todoOwner));
        verify(todolistService).getTodolistRef(1L, todoOwner);
        verify(todoRepository, never()).save(any());
    }

    @Test
    void getAllTodos_WrongOwner() {
        // given
        String todoOwner = "todo_owner";

        when(this.todolistService.getTodolistRef(1L, todoOwner)).thenThrow(new AccessDeniedException(""));

        // when & then
        assertThrows(AccessDeniedException.class, () -> underTest.getAllTodos(1L, todoOwner));
        verify(todolistService).getTodolistRef(1L, todoOwner);
        verify(todoRepository, never()).save(any());
    }

    @Test
    void createTodo() {
        Instant now = Instant.now();
        try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class)) {
            // mock to always use a snapshot timestamp for all objects
            mockedStatic.when(Instant::now).thenReturn(now);

            // given
            String todoOwner = "todo_owner";
            TodoDTO todo = new TodoDTO(null,"todo_name", "todo_description",
                    true, null, null);

            Todolist todolistReturned = new Todolist(1L, todoOwner);

            Todo todoExpected = new Todo(null, todoOwner, todo.name(), todo.description(),
                    true, Instant.now(), null, todolistReturned);
            TodoDTO todoDTOExpected = todoDTOMapper.apply(todoExpected);

            when(this.todolistService.getTodolistRef(1L, todoOwner)).thenReturn(todolistReturned);
            when(this.todoRepository.save(any(Todo.class))).thenAnswer(i -> i.getArguments()[0]);

            // when
            TodoDTO todoResult = underTest.createTodo(todolistReturned.getId(), todo, todoOwner);

            // then
            verify(todolistService).getTodolistRef(todolistReturned.getId(), todoOwner);
            verify(todoRepository).save(todoExpected);
            assertThat(todoResult).isEqualTo(todoDTOExpected);
        }
    }

    @Test
    void createTodo_NotExistingTodolistId() {
        // given
        String todoOwner = "todo_owner";
        TodoDTO todo = new TodoDTO(null, "todo_name", "todo_description", false, null, null);
        when(this.todolistService.getTodolistRef(1L, todoOwner)).thenThrow(new EntityNotFoundException());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> underTest.createTodo(1L, todo, todoOwner));
        verify(todolistService).getTodolistRef(1L, todoOwner);
        verify(todoRepository, never()).save(any());
    }

    @Test
    void createTodo_WrongOwner() {
        // given
        String todoOwner = "todo_owner";
        TodoDTO todo = new TodoDTO(null, "todo_name", "todo_description", false, null, null);
        when(this.todolistService.getTodolistRef(1L, todoOwner)).thenThrow(new AccessDeniedException(""));

        // when & then
        assertThrows(AccessDeniedException.class, () -> underTest.createTodo(1L, todo, todoOwner));
        verify(todolistService).getTodolistRef(1L, todoOwner);
        verify(todoRepository, never()).save(any());
    }

    @Test
    void updateTodo() {
        // given
        TodoDTO todoNew = new TodoDTO(null,"todolist_name", "todo_description_new",
                false, null, Instant.now().minusSeconds(1));

        String todoOwner = "todo_owner";
        Todolist todolistReturned = new Todolist(1L, todoOwner);

        Todo todoFindById = new Todo(2L, todoOwner, "todo_name", "todo_description",
                true, Instant.now(), null, todolistReturned);

        // combine todoFindById and todoNew as expected by updateTodo
        Todo todoExpected = new Todo(todoFindById.getId(), todoOwner, todoNew.name(), todoNew.description(),
                todoNew.favorite(), todoFindById.getCreatedAt(), todoNew.completedAt(), todolistReturned);
        TodoDTO todoDTOExpected = todoDTOMapper.apply(todoExpected);

        when(this.todoRepository.findById(todoFindById.getId())).thenReturn(Optional.of(todoFindById));
        when(this.todoRepository.save(any(Todo.class))).thenAnswer(i -> i.getArguments()[0]);

        // when
        TodoDTO todoResult = underTest.updateTodo(todoFindById.getId(), todoNew, todoOwner);

        // then
        verify(todoRepository).findById(todoFindById.getId());
        verify(todoRepository).save(todoExpected);
        assertThat(todoResult).isEqualTo(todoDTOExpected);
    }

    @Test
    void updateTodo_NotExistingTodoId() {
        // given
        String todoOwner = "todo_owner";
        TodoDTO todoNewValues = new TodoDTO(null, "todo_name_new", "todo_description_new", false, null, null);
        when(this.todoRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> underTest.updateTodo(1L, todoNewValues, todoOwner));
        verify(todoRepository).findById(1L);
        verify(todoRepository, never()).save(any());
    }

    @Test
    void updateTodo_WrongTodo() {
        // given
        String todoOwnerWrong = "todo_owner_wrong";
        String todoOwnerCorrect = "todo_owner_correct";
        Todolist todolist = new Todolist(1L, "todolist_name", "todolist_description", todoOwnerCorrect);
        TodoDTO todoNewValues = new TodoDTO(null, "todo_name_new", "todo_description_new", false, null, null);
        Todo todoFindById = new Todo(2L, "todo_name", "todo_description", false, todoOwnerCorrect, todolist);
        when(this.todoRepository.findById(2L)).thenReturn(Optional.of(todoFindById));

        // when & then
        assertThrows(AccessDeniedException.class, () -> underTest.updateTodo(2L, todoNewValues, todoOwnerWrong));
        verify(todoRepository).findById(2L);
        verify(todoRepository, never()).save(any());
    }

    @Test
    void deleteTodo() {
        // given
        String todoOwner = "todo_owner";
        Todolist todolist = new Todolist(1L, "todolist_name", "todolist_description", todoOwner);
        Todo todoToDelete = new Todo(2L, "todo_name", "todo_description", false, todoOwner, todolist);
        TodoDTO expected = todoDTOMapper.apply(todoToDelete);
        when(this.todoRepository.findById(todoToDelete.getId())).thenReturn(Optional.of(todoToDelete));

        // when
        TodoDTO todoResult = underTest.deleteTodo(todoToDelete.getId(), todoOwner);

        // then
        verify(todoRepository).findById(todoToDelete.getId());
        verify(todoRepository).deleteById(todoToDelete.getId());
        assertThat(todoResult).isEqualTo(expected);
    }

    @Test
    void deleteTodo_NotExistingTodoId() {
        // given
        String todoOwner = "todo_owner";
        when(this.todoRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> underTest.deleteTodo(1L, todoOwner));
        verify(todoRepository).findById(1L);
        verify(todoRepository, never()).deleteById(any());
    }

    @Test
    void deleteTodo_WrongOwner() {
        // given
        String todoOwnerWrong = "todo_owner_wrong";
        String todoOwnerCorrect = "todo_owner_correct";
        Todolist todolist = new Todolist(1L, "todolist_name", "todolist_description", todoOwnerCorrect);
        Todo todoToDelete = new Todo(2L, "todo_name", "todo_description", false, todoOwnerCorrect, todolist);
        when(this.todoRepository.findById(todoToDelete.getId())).thenReturn(Optional.of(todoToDelete));

        // when & then
        assertThrows(AccessDeniedException.class, () -> underTest.deleteTodo(todoToDelete.getId(), todoOwnerWrong));
        verify(todoRepository).findById(todoToDelete.getId());
        verify(todoRepository, never()).deleteById(any());
    }
}