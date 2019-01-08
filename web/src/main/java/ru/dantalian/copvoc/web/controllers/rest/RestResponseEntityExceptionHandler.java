package ru.dantalian.copvoc.web.controllers.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	@ExceptionHandler({ RestException.class })
  public ResponseEntity<Object> handleAccessDeniedException(
    final Exception ex, final WebRequest request) {
		final ResponseStatusException e = (ResponseStatusException) ex;
		logger.error("REST Error occured", e);
      return new ResponseEntity<>(
      	new	RestExceptionBody(e.getStatus(), e.getReason()), new HttpHeaders(), e.getStatus());
  }

}
