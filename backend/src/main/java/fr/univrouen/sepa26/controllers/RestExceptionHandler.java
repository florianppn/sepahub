package fr.univrouen.sepa26.controllers;

import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import fr.univrouen.sepa26.dto.SepaResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestionnaire d'exceptions global pour l'API REST SEPA26.
 * Fournit des réponses d'erreur normalisées selon la négociation de contenu :
 * - JSON : ProblemDetail conforme à la RFC 7807 (standard Spring Boot).
 * - XML : DTO SepaResponse avec statut ERROR et description détaillée.
 *
 * @author Florian Pépin
 * @version 2.0
 */
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        String message = "La ressource demandée n'existe pas : " + ex.getResourcePath();
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Ressource non trouvée", message, request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ResponseEntity<?> handleBadRequest(Exception ex, HttpServletRequest request) {
        String message = "Requête invalide : " + ex.getMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Requête invalide", message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex, HttpServletRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Une erreur interne est survenue.";
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne", message, request);
    }

    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String title, String detail, HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains(MediaType.APPLICATION_XML_VALUE)) {
            SepaResponse sepaResponse = new SepaResponse("ERROR", detail);
            return ResponseEntity.status(status).contentType(MediaType.APPLICATION_XML).body(sepaResponse);
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problemDetail);
    }
}
