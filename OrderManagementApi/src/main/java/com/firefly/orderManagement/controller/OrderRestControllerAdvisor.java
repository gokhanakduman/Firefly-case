package com.firefly.orderManagement.controller;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.firefly.orderManagement.controller.dto.ErrorResponseDTO;
import com.firefly.orderManagement.exception.NoOrderWithIdException;
import com.firefly.orderManagement.util.Constants;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class OrderRestControllerAdvisor extends ResponseEntityExceptionHandler {
	
	@ApiResponse(responseCode = "400")
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
			ConstraintViolationException ex, ServletWebRequest servletWebRequest) {
		logErrorWithRequestPath(servletWebRequest, ex);
		return getErrorResponse(Constants.ERROR_MESSAGE_REQUEST_PARAMETERS_NOT_VALID, HttpStatus.BAD_REQUEST);
	}
	
	@ApiResponse(responseCode = "404")
	@ExceptionHandler(NoOrderWithIdException.class)
	public ResponseEntity<ErrorResponseDTO> handleNoSuchElementExceptions(
			NoOrderWithIdException ex, ServletWebRequest servletWebRequest) {
		logErrorWithRequestPath(servletWebRequest, ex);
		return getErrorResponse(Constants.ERROR_MESSAGE_REQUESTED_ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
		// NO_CONTENT can be used also?
	}
	
	@ApiResponse(responseCode = "500")
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO>  handleOtherExceptions(Exception ex, ServletWebRequest servletWebRequest) {
		logErrorWithRequestPath(servletWebRequest, ex);
		return getErrorResponse(Constants.ERROR_MESSAGE_UNKNOWN_ERROR_OCCURED, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private void logErrorWithRequestPath(ServletWebRequest servletWebRequest, Exception e) {
		log.error(
				"Error occured at endpoint: {}", 
				servletWebRequest.getRequest().getRequestURI().toString(),
				e
				);
	}

	private ResponseEntity<ErrorResponseDTO> getErrorResponse(String message, HttpStatus httpStatus) {
		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(message);
		return new ResponseEntity<>(errorResponseDTO, httpStatus);
	}
}
