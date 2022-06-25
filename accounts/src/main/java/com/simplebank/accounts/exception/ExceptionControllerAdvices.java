package com.simplebank.accounts.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * A controller advice used to provide handling behaviour for the possible Exceptions around the controllers used.
 */
@ControllerAdvice
public class ExceptionControllerAdvices {
    private final Log log = LogFactory.getLog(getClass());

    /**
     * Handler for ResourceNotFoundException's
     * @param ex the concrete exception
     * @return A response body as a String
     */
    @ResponseBody
    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String resourceNotFoundHandler(ResourceNotFoundException ex) {
        return ex.getMessage();
    }

    /**
     * Handler for exceptions connected to parsing the request payload
     * @param ex the concrete exception instance
     * @return A response body as a String
     */
    @ResponseBody
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badMessageFormat(HttpMessageNotReadableException ex) {
        return "Message not readable. Make sure it's a valid JSON!";
    }

    /**
     * Handler for exceptions connected to validating requests
     * @param ex the concrete exception instance
     * @return A response body as a String
     */
    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Handler for generic axceptions, not handled by the other handlers.
     * @param ex the concrete exception instance
     * @return A response body as a String
     */
    @ResponseBody
    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String genericErrorHandler(Exception ex) {
        log.error("Generic server error caught. See error message for details.", ex);
        return "Generic server error: " + ex.getMessage();
    }
}