package com.portfolio.todolistapi.ModelDTO;

import java.time.Instant;

public record TodoDTO (
        Long id,
        String name,
        String description,
        Boolean favorite,
        Instant createdAt,
        Instant completedAt
) {}
