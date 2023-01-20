package com.portfolio.todolistapi.ModelDTO;

import java.time.Instant;

public record TodolistDTO (
        Long id,
        String name,
        String description,
        Instant createdAt
) {}
