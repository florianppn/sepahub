package fr.univrouen.sepa26.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.univrouen.sepa26.dto.DocumentList;
import fr.univrouen.sepa26.dto.SearchResponse;
import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.services.ISepaService;

/**
 * Contrôleur REST pour la recherche multicritères de documents SEPA.
 * Utilise l'injection par constructeur et dépend de l'interface ISepaService (DIP).
 */
@RestController
@RequestMapping("/sepa26")
public class SearchController {

    private final ISepaService sepaService;

    @Autowired
    public SearchController(ISepaService sepaService) {
        this.sepaService = sepaService;
    }

    /**
     * Recherche les documents selon une date minimale et/ou un montant minimal.
     * @param date paramètre facultatif de date minimale format YYYY-MM-DD.
     * @param sum paramètre facultatif de montant minimal de la transaction.
     * @return ResponseEntity contenant une SearchResponse:
     *   - status : OK, NONE, ERROR
     *   - DocumentList : liste des documents trouvés, vide si aucun document
     */
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<SearchResponse> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Double sum) {
        try {
            LocalDateTime dateTime = (date != null) ? date.atStartOfDay() : null;
            List<Document> results = sepaService.search(dateTime, sum);
            if (results.isEmpty()) {
                return ResponseEntity.ok(new SearchResponse("NONE"));
            } else {
                DocumentList doc = new DocumentList(results);
                return ResponseEntity.ok(new SearchResponse("OK", doc));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new SearchResponse("ERROR"));
        }
    }
}
