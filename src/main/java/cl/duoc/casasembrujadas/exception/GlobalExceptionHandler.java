package cl.duoc.casasembrujadas.exception;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import cl.duoc.casasembrujadas.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(this::describir)
                .sorted()
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "La solicitud tiene datos inválidos", request, errores);
    }

    private String describir(FieldError error) {
        if (error.isBindingFailure()) {
            return error.getField() + ": el valor '" + error.getRejectedValue() + "' no tiene un formato válido";
        }
        return error.getField() + ": " + error.getDefaultMessage();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> cuerpoIlegible(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no es un JSON válido o tiene un valor con formato incorrecto", request, List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> tipoIncorrecto(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un valor inválido: '" + ex.getValue() + "'", request, List.of());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> parametroFaltante(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "Falta el parámetro '" + ex.getParameterName() + "'", request, List.of());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaInexistente(NoResourceFoundException ex, HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, "No existe la ruta " + request.getRequestURI(), request, List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> metodoNoPermitido(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED,
                "La ruta no acepta el método " + ex.getMethod(), request, List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> tipoDeContenido(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return responder(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "El cuerpo debe enviarse como application/json", request, List.of());
    }

    @ExceptionHandler({RecursoDuplicadoException.class, ReglaNegocioException.class})
    public ResponseEntity<ErrorResponse> conflicto(RuntimeException ex, HttpServletRequest request) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> claveDuplicada(DuplicateKeyException ex, HttpServletRequest request) {
        return responder(HttpStatus.CONFLICT, "Ya existe un registro con ese dato único", request, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        return responder(HttpStatus.CONFLICT,
                "La operación no se puede realizar porque otros datos dependen de este registro", request, List.of());
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<ErrorResponse> accesoDenegado(AccesoDenegadoException ex, HttpServletRequest request) {
        return responder(HttpStatus.FORBIDDEN, ex.getMessage(), request, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado", request, List.of());
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus estado, String mensaje, HttpServletRequest request, List<String> errores) {
        ErrorResponse cuerpo = ErrorResponse.builder()
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(estado.value())
                .error(estado.getReasonPhrase())
                .mensaje(mensaje)
                .ruta(request.getRequestURI())
                .errores(errores)
                .build();
        return ResponseEntity.status(estado).body(cuerpo);
    }
}