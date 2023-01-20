package com.portfolio.todolistapi.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "todolists")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class Todolist implements Serializable {
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
    private Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "todolist", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<Todo> todos = new ArrayList<>();

    public Todolist(Long id, String owner) {
        this.id = id;
        this.owner = owner;
    }

    public Todolist(String name, String description, String owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public Todolist(Long id, String name, String description, String owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
    }
}