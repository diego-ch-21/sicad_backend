package com.sicad.sicad_backend.exception;

import com.sicad.sicad_backend.dto.base.CustomErrorResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    //global
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseListReponse<CustomErrorResponse>> handleDefaultException(Exception ex, WebRequest request) {
        CustomErrorResponse cer = new CustomErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(new BaseListReponse<>(
                500,
                "Error interno del servidor",
                List.of(cer)
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //especifico
    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<BaseListReponse<CustomErrorResponse>> handleModelNotFoundException(ModelNotFoundException ex, WebRequest request) {
        CustomErrorResponse cer = new CustomErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(new BaseListReponse<>(
                404,
                "Recurso no encontrado",
                List.of(cer)
        ), HttpStatus.NOT_FOUND);
    }
    /*
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<CustomErrorResponse> handleArithmeticException(ArithmeticException ex, WebRequest request) {
        CustomErrorResponse cer = new CustomErrorResponse(
                LocalDateTime.now(),
                "Error de cálculo: " + ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(cer, HttpStatus.NOT_ACCEPTABLE);
    }

     */
    //otra forma de hacer una excepcion de validacion usando un metodo heredado ResponseEntityExceptionHandler
    /*@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        CustomErrorResponse cer = new CustomErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(cer, HttpStatus.BAD_REQUEST);
    }*/
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<CustomErrorResponse> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new CustomErrorResponse(
                        LocalDateTime.now(),
                        error.getDefaultMessage(), // Este es el mensaje personalizado de @NotBlank
                        request.getDescription(false)
                ))
                .toList();

        return new ResponseEntity<>(
                new BaseListReponse<>(400, "Error de validación", errores),
                HttpStatus.BAD_REQUEST
        );
    }


    // Manejo de excepciones de validación
    /*@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        CustomErrorResponse cer = new CustomErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(cer, HttpStatus.BAD_REQUEST);
    }*/


    /*
    @ExceptionHandler(ModelNotFoundException.class)
    public ProblemDetail handleModelNotFoundException(ModelNotFoundException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        pd.setTitle("Recurso no encontrado");
        pd.setType(URI.create(request.getDescription(false)));
        pd.setProperty("code", 404);
        pd.setProperty("message", "NO ENCONTRADO");
        return pd;
    }
     */

}
