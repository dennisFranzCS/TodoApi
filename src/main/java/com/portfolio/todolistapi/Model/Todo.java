package com.portfolio.todolistapi.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "todos")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Todo implements Serializable {
    @Id
    @GeneratedValue(strategy = SEQUENCE)
    private Long id;
    @Column(nullable = false)
    private String owner;
    @Column(nullable = false)
    private String name = "";
    @Column(nullable = false)
    private String description = "";
    @Column(nullable = false)
    private Boolean favorite = false;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    private Instant completedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = "todolist_id")
    @JsonIgnore
    private Todolist todolist;

    public Todo(String name, String description, Boolean favorite, String owner, Todolist todolist) {
        this.name = name;
        this.description = description;
        this.favorite = favorite;
        this.owner = owner;
        this.todolist = todolist;
    }

    public Todo(Long id, String name, String description, Boolean favorite, String owner, Todolist todolist) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.favorite = favorite;
        this.owner = owner;
        this.todolist = todolist;
    }
}