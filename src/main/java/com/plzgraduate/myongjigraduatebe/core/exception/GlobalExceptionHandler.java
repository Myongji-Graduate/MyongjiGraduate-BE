package com.plzgraduate.myongjigraduatebe.core.exception;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
		return ExceptionResponse.from(e.getMessage());
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ExceptionResponse handleNotFoundException(Exception e) {
		log.debug("Not Found exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.from(e.getMessage());
	}

	@ExceptionHandler(UnAuthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ExceptionResponse handleUnAuthorizedException(Exception e) {
		log.debug("unauthorized exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.from(e.getMessage());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleValidationException(ConstraintViolationException e) {
		log.info("validated exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.from(getViolationErrorMessage(e));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		log.debug("validation exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.from(getBindingErrorMessage(e));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		log.debug("graduation category mismatch exception occurred: {}",
			getMethodArgumentTypeMismatchErrorMessage(e));
		return ExceptionResponse.from(ErrorCode.INVALIDATED_GRADUATION_CATEGORY.toString());
	}

	@ExceptionHandler({PdfParsingException.class, InvalidPdfException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionResponse handlePdfException(Exception e) {
		log.warn("pdf exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.from(e.getMessage());
	}

	@ExceptionHandler({RuntimeException.class, Exception.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionResponse handleRuntimeException(RuntimeException e) {
		log.error("Unexpected exception occurred: {}", e.getMessage(), e);
		return ExceptionResponse.from(ErrorCode.INTERNAL_SEVER_ERROR.toString());
	}

	private String getViolationErrorMessage(ConstraintViolationException e) {
		return e.getConstraintViolations()
			.stream()
			.map(ConstraintViolation::getMessage)
			.findFirst()
			.orElse(null);
	}

	private String getBindingErrorMessage(MethodArgumentNotValidException e) {
		Optional<ObjectError> objectError = e.getBindingResult()
			.getAllErrors()
			.stream()
			.findFirst();
		return objectError.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.orElse(null);
	}

	private String getMethodArgumentTypeMismatchErrorMessage(
		MethodArgumentTypeMismatchException e) {
		String errorMessage = Objects.requireNonNull(e.getMessage())
			.split("value '")[1].split(
			"'")[0];
		return "Failed to convert value: " + errorMessage;
	}

}
