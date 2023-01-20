package com.portfolio.todolistapi.Repository;

import com.portfolio.todolistapi.Model.Todolist;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodolistRepository extends JpaRepository<Todolist, Long> {
    List<Todolist> findAllByOwner(String owner, Sort sort);
}