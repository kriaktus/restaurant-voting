package com.github.kriaktus.restaurantvoting.web;

import com.github.kriaktus.restaurantvoting.error.AppException;
import com.github.kriaktus.restaurantvoting.util.validation.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String EXCEPTION_USER_DUPLICATE_EMAIL = "User with this email already exists";
    public static final String EXCEPTION_MENU_ITEM_DUPLICATE_NAME = "Menu item with this name already exists in actual menu";
    public static final String EXCEPTION_MENU_ITEM_FROM_ANOTHER_RESTAURANT = "Menu item from another restaurant or does not exist";
    public static final String EXCEPTION_MENU_ACTUAL_DATE = "Actual menu must contain actual date";
    public static final String EXCEPTION_MENU_MENU_ITEM_HAS_ID = "Created menu must contain menu items without id";
    public static final String EXCEPTION_MENU_MENU_ITEM_HAS_SAME_ID = "Menu items has same id";
    public static final String EXCEPTION_MENU_MENU_ITEM_HAS_SAME_NAME = "Menu items has same name";
    public static final String EXCEPTION_RESTAURANT_DUPLICATE_NAME = "Restaurant with same name already exist";

    private final ErrorAttributes errorAttributes;

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        return handleBindingErrors(ex.getBindingResult(), request);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appException(WebRequest request, AppException ex) {
        log.error("ApplicationException: {}", ex.getMessage());
        return createResponseEntity(getDefaultBody(request, ex.getOptions(), null), ex.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> persistException(WebRequest request, EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage());
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(MESSAGE), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<Object> handleBindingErrors(BindingResult result, WebRequest request) {
        String msg = result.getFieldErrors().stream()
                .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.joining("\n"));
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.defaults(), msg), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private Map<String, Object> getDefaultBody(WebRequest request, ErrorAttributeOptions options, String msg) {
        Map<String, Object> body = errorAttributes.getErrorAttributes(request, options);
        if (msg != null) {
            body.put("message", msg);
        }
        return body;
    }

    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> createResponseEntity(Map<String, Object> body, HttpStatus status) {
        body.put("timestamp", ((Date) body.get("timestamp")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DATE_TIME_FORMATTER));
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        return (ResponseEntity<T>) ResponseEntity.status(status).body(body);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request) {
        log.error("Exception", ex);
        super.handleExceptionInternal(ex, body, headers, status, request);
        return createResponseEntity(getDefaultBody(request, ErrorAttributeOptions.of(), ValidationUtil.getRootCause(ex).getMessage()), status);
    }
}
