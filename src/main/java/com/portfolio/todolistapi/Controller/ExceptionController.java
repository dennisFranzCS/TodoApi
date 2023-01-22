package com.portfolio.todolistapi.Controller;

import com.portfolio.todolistapi.ModelDTO.ExceptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;


@ControllerAdvice
@Slf4j
public class ExceptionController extends ResponseEntityExceptionHandler {
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public @ResponseBody ExceptionDTO handleEntityNotFoundException(EntityNotFoundException exception){
        log.debug("Handled an entity not found exception.");
        return new ExceptionDTO("Requested resources is not available.", HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public @ResponseBody ExceptionDTO handleAccessDeniedException(AccessDeniedException exception){
        log.debug("Handled an access denied exception.");
        return new ExceptionDTO("Access to the requested resource was denied.", HttpStatus.FORBIDDEN);
    }

    /**
     * Handle all exceptions that are not handled to avoid leaking relevant information to a user. Spring Security
     * exceptions are not handled.
     * @param exception Exception to be handled
     * @return ExceptionDTO based response object
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public @ResponseBody ExceptionDTO handleAllUnhandledExceptions(Exception exception){
        log.error("Encountered an unhandled error!", exception);
        return  new ExceptionDTO("An internal error occured.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
