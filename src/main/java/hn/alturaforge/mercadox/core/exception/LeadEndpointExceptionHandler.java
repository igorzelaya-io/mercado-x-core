package hn.alturaforge.mercadox.core.exception;

import hn.alturaforge.mercadox.core.controller.LeadController;
import hn.alturaforge.mercadox.library.entity.response.BaseResponseDto;
import hn.alturaforge.mercadox.library.entity.response.Response;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = LeadController.class)
public class LeadEndpointExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<? extends Response<String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return new BaseResponseDto<String>()
                .buildResponseEntity(HttpStatus.BAD_REQUEST, "Invalid request.", message);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<? extends Response<String>> handleEntityNotFound(EntityNotFoundException ex) {
        return new BaseResponseDto<String>()
                .buildResponseEntity(HttpStatus.BAD_REQUEST, "Invalid request.", "Invalid request.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<? extends Response<String>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return new BaseResponseDto<String>()
                .buildResponseEntity(HttpStatus.BAD_REQUEST, "Malformed request body.", "Malformed request body.");
    }
}
