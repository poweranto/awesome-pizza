package com.awesomepizza.orderservice.exceptions;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice()
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	ErrorResponse handleResourceNotFound(Exception ex, WebRequest request) {
		
		ErrorResponseException errEx = new ErrorResponseException(HttpStatus.NOT_FOUND);
		errEx.setDetail(ex.getMessage());
		
		return errEx;
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	ErrorResponse handleMethodArgumentTypeMismatch(Exception ex, WebRequest request) {
		
		ErrorResponseException errEx = new ErrorResponseException(HttpStatus.BAD_REQUEST);
		errEx.setDetail("id has to be a valid number");
		
		return errEx;
	}

	@ExceptionHandler({OrderAlreadyInProgressException.class, IllegalOrderStateException.class})
	ErrorResponse handleOrderAlreadyInProgress(Exception ex, WebRequest request) {
		
		ErrorResponseException errEx = new ErrorResponseException(HttpStatus.CONFLICT);
		errEx.setDetail(ex.getMessage());
		
		return errEx;
	}
}