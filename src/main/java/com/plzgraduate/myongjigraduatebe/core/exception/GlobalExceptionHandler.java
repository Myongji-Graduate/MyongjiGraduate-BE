package com.plzgraduate.myongjigraduatebe.core.exception;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({
		IllegalArgumentException.class,
		IllegalStateException.class,
		HttpMessageNotReadableException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleBadRequestException(Exception e) {
		log.debug("Bad request exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.of(HttpStatus.BAD_REQUEST, getMessage(e));
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ExceptionResponse handleNotFoundException(Exception e) {
		log.debug("Not Found exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.of(HttpStatus.NOT_FOUND, getMessage(e));
	}

	@ExceptionHandler(UnAuthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ExceptionResponse handleUnAuthorizedException(Exception e) {
		log.debug("unauthorized exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.of(HttpStatus.BAD_REQUEST, getMessage(e));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleValidationException(ConstraintViolationException e) {
		log.info("validated exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.of(HttpStatus.BAD_REQUEST, getViolationErrorMessage(e));
	}



	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.debug("validation exception occurred: {}",  e.getMessage(), e);
		return ExceptionResponse.of(HttpStatus.BAD_REQUEST, getBindingErrorMessage(e));
	}

	@ExceptionHandler({PdfParsingException.class, InvalidPdfException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handlePdfException(Exception e) {
		log.warn("pdf exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.of(HttpStatus.BAD_REQUEST, getMessage(e));
	}

	@ExceptionHandler({RuntimeException.class, Exception.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleRuntimeException(RuntimeException e) {
		log.error("Unexpected exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, getMessage(e));
	}

	private String getMessage(Exception e) {
		return Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(e.getMessage());
	}

	private String getViolationErrorMessage(ConstraintViolationException e) {
		return e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).findFirst().orElse(null);
	}

	private String getBindingErrorMessage(MethodArgumentNotValidException e) {
		Optional<ObjectError> objectError = e.getBindingResult().getAllErrors().stream().findFirst();
		return objectError.map(DefaultMessageSourceResolvable::getDefaultMessage).orElse(null);
	}

}
