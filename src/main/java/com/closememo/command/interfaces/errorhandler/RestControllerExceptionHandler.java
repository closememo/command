package com.closememo.command.interfaces.errorhandler;

import com.closememo.command.domain.BusinessException;
import jakarta.persistence.NoResultException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = {
    "com.closememo.command.interfaces"
})
public class RestControllerExceptionHandler {

  @ExceptionHandler({
      BusinessException.class
  })
  protected ResponseEntity<ErrorResponse> handle(BusinessException exception) {
    if (exception.isNecessaryToLog()) {
      String message = Optional.ofNullable(exception.getMessage())
          .orElse(exception.getClass().getSimpleName());
      log.error(message, exception);
    }

    Error error = new Error(exception.getClass().getSimpleName(), exception.getMessage());
    return new ResponseEntity<>(new ErrorResponse(error), new HttpHeaders(),
        exception.getHttpStatus());
  }

  @ExceptionHandler({
      NoResultException.class
  })
  protected ResponseEntity<ErrorResponse> handle(Exception exception) {
    Error error = new Error(exception.getClass().getSimpleName(), exception.getMessage());
    return new ResponseEntity<>(new ErrorResponse(error), new HttpHeaders(), HttpStatus.NOT_FOUND);
  }
}
