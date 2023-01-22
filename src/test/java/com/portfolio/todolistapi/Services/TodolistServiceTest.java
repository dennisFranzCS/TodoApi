package com.portfolio.todolistapi.Services;

import com.portfolio.todolistapi.DTOMapper.TodolistDTOMapper;
import com.portfolio.todolistapi.Model.Todolist;
import com.portfolio.todolistapi.ModelDTO.TodolistDTO;
import com.portfolio.todolistapi.Repository.TodolistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
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
class TodolistServiceTest {

    @Mock
    private TodolistRepository todolistRepository;
    private TodolistService underTest;
    private final TodolistDTOMapper todolistDTOMapper = new TodolistDTOMapper();

    @BeforeEach
    void setUp() {
        this.underTest = new TodolistService(todolistRepository, todolistDTOMapper);
    }

    @Test
    void getTodolist() {
        // given
        String todolistOwner = "todolist_owner";
        Todolist todolist = new Todolist(1L, "todolist_name", "todolist_description", todolistOwner);
        TodolistDTO todolistExpected = todolistDTOMapper.apply(todolist);

        when(this.todolistRepository.findById(todolist.getId())).thenReturn(Optional.of(todolist));

        // when
        TodolistDTO todolistResult = underTest.getTodolist(todolist.getId(), todolistOwner);

        // then
        assertThat(todolistResult).isEqualTo(todolistExpected);
        verify(todolistRepository).findById(todolist.getId());
    }

    @Test
    void getTodolist_NotExistingTodolistId() {
        // given
        String todolistOwner = "todolist_owner";
        when(this.todolistRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> underTest.getTodolist(1L, todolistOwner));
        verify(todolistRepository).findById(1L);
    }

    @Test
    void getTodolist_WrongOwner() {
        // given
        String todolistOwnerCorrect = "todolist_owner_correct";
        String todolistOwnerWrong = "todolist_owner_wrong";
        Todolist todolist = new Todolist(1L, "todolist_name", "todolist_description", todolistOwnerCorrect);

        when(this.todolistRepository.findById(todolist.getId())).thenReturn(Optional.of(todolist));

        // when & then
        assertThrows(AccessDeniedException.class, () -> underTest.getTodolist(todolist.getId(), todolistOwnerWrong));
        verify(todolistRepository).findById(todolist.getId());
    }

    @Test
    void getTodolistRef() {
        // given
        String todolistOwner = "todolist_owner";
        Todolist todolist = new Todolist(1L, "todolist_name", "todolist_description", todolistOwner);

        when(todolistRepository.findById(todolist.getId())).thenReturn(Optional.of(todolist));

        // when
        Todolist todolistResult = underTest.getTodolistRef(todolist.getId(), todolistOwner);

        //then
        assertThat(todolistResult).isEqualTo(todolist);
        verify(todolistRepository).findById(todolist.getId());
    }

    @Test
    void getTodolistRef_NotExistingTodolistId() {
        // given
        when(todolistRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> underTest.getTodolistRef(1L, null));
        verify(todolistRepository).findById(1L);
    }

    @Test
    void getTodolistRef_WrongOwner() {
        // given
        String todolistOwnerCorrect = "todolist_owner_correct";
        String todolistOwnerWrong = "todolist_owner_wrong";
        Todolist todolist = new Todolist(1L, "todolist_name", "todolist_description", todolistOwnerCorrect);
        when(todolistRepository.findById(1L)).thenReturn(Optional.of(todolist));

        // when & then
        assertThrows(AccessDeniedException.class, () -> underTest.getTodolistRef(todolist.getId(), todolistOwnerWrong));
        verify(todolistRepository).findById(todolist.getId());
    }

    @Test
    void getAllTodolists() {
        // given
        String todolistOwner = "todolist_owner";
        List<Todolist> todolistsReturn = new ArrayList<>();
        todolistsReturn.add(new Todolist(1L, todolistOwner));
        todolistsReturn.add(new Todolist(2L, todolistOwner));
        todolistsReturn.add(new Todolist(3L, todolistOwner));

        when(todolistRepository.findAllByOwner(todolistOwner, Sort.unsorted())).thenReturn(todolistsReturn);

        // when
        List<TodolistDTO> result = underTest.getAllTodoslists(todolistOwner);

        // then
        assertThat(result).isEqualTo(todolistsReturn.stream().map(todolistDTOMapper).toList());
        verify(todolistRepository).findAllByOwner(todolistOwner, Sort.unsorted());
    }

    @Test
    void createTodolist() {
        Instant now = Instant.now();
        try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class)) {
            // snapshot current timestamp for all objects.
            mockedStatic.when(Instant::now).thenReturn(now);

            // given
            String todolistOwner = "todolist_owner";
            TodolistDTO todolistDTO = new TodolistDTO(null, "todolist_name", "todolist_description", null);
            Todolist todolistExpected = new Todolist(null, todolistOwner, "todolist_name",
                    "todolist_description", Instant.now(), new ArrayList<>());
            TodolistDTO todolistDTOExpected = todolistDTOMapper.apply(todolistExpected);

            when(todolistRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

            // when
            TodolistDTO todolistReturn = underTest.createTodolist(todolistDTO, todolistOwner);

            // then
            verify(todolistRepository).save(todolistExpected);
            assertThat(todolistReturn).isEqualTo(todolistDTOExpected);
        }
    }

    @Test
    void updateTodolist() {
        // given
        String todolistOwner = "todolist_owner";
        TodolistDTO todolistNewValues = new TodolistDTO(null,"todolist_name_updated",
                "todolist_description_updated", null);
        Todolist todolistFindById = new Todolist(1L, todolistOwner, "todolist_name",
                "todolist_description", Instant.now(), new ArrayList<>());
        Todolist todolistExpected = new Todolist(1L, todolistOwner, todolistNewValues.name(),
                todolistNewValues.description(), todolistFindById.getCreatedAt(), todolistFindById.getTodos());

        when(this.todolistRepository.findById(todolistFindById.getId())).thenReturn(Optional.of(todolistFindById));
        when(this.todolistRepository.save(any(Todolist.class))).thenAnswer(i -> i.getArguments()[0]);

        // when
        TodolistDTO todolistResult = underTest.updateTodolist(todolistFindById.getId(), todolistNewValues, todolistOwner);

        // then
        verify(todolistRepository).findById(todolistFindById.getId());
        verify(todolistRepository).save(todolistExpected);
        assertThat(todolistResult).isEqualTo(todolistDTOMapper.apply(todolistExpected));
    }

    @Test
    void updateTodolist_NotExistingTodolistId() {
        // given
        String todolistOwner = "todolist_owner";
        when(this.todolistRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> underTest.updateTodolist(1L, null, todolistOwner));
        verify(todolistRepository).findById(1L);
        verify(todolistRepository, never()).save(any());
    }

    @Test
    void updateTodolist_WrongOwner() {
        // given
        String todolistOwnerCorrect = "todolist_owner_correct";
        String todolistOwnerWrong = "todolist_owner_wrong";
        TodolistDTO todolistNewValues = new TodolistDTO(null, null, null, null);
        Todolist todolistFindById = new Todolist(1L, "todolist_name", "todolist_description", todolistOwnerCorrect);
        when(this.todolistRepository.findById(todolistFindById.getId())).thenReturn(Optional.of(todolistFindById));

        // when & then
        assertThrows(AccessDeniedException.class, () -> underTest.updateTodolist(todolistFindById.getId(), todolistNewValues, todolistOwnerWrong));
        verify(todolistRepository).findById(todolistFindById.getId());
        verify(todolistRepository, never()).save(any());
    }

    @Test
    void deleteTodolist() {
        // given
        String todolistOwner = "todolist_owner";
        Todolist todolistRepoReturn = new Todolist(1L,"test todolist name", "test todolist description", todolistOwner);
        when(todolistRepository.findById(todolistRepoReturn.getId())).thenReturn(Optional.of(todolistRepoReturn));

        // when
        TodolistDTO todolistReturn = underTest.deleteTodolist(todolistRepoReturn.getId(), todolistOwner);

        // then
        assertThat(todolistReturn).isEqualTo(todolistDTOMapper.apply(todolistRepoReturn));
        verify(todolistRepository).findById(todolistRepoReturn.getId());
        verify(todolistRepository).deleteById(todolistRepoReturn.getId());
    }

    @Test
    void deleteTodolist_NotExistingTodolistId() {
        // given
        String todolistOwner = "todolist_owner";
        when(todolistRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> underTest.deleteTodolist(1L, todolistOwner));
        verify(todolistRepository).findById(1L);
        verify(todolistRepository, never()).delete(any());
    }

    @Test
    void deleteTodolist_WrongOwner() {
        // given
        String todolistOwnerCorrect = "todolist_owner_correct";
        String todolistOwnerWrong = "todolist_owner_wrong";
        Todolist todolistRepoReturn = new Todolist(1L,"test todolist name", "test todolist description", todolistOwnerCorrect);
        when(todolistRepository.findById(todolistRepoReturn.getId())).thenReturn(Optional.of(todolistRepoReturn));

        // when & then
        assertThrows(AccessDeniedException.class, () -> underTest.deleteTodolist(todolistRepoReturn.getId(), todolistOwnerWrong));
        verify(todolistRepository).findById(todolistRepoReturn.getId());
        verify(todolistRepository, never()).delete(any());
    }
}