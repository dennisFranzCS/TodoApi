package com.portfolio.todolistapi.ModelDTO;


import org.springframework.http.HttpStatus;

public record ExceptionDTO (
        String msg,
        HttpStatus httpStatus
) {}
