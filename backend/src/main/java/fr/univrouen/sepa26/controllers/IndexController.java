package fr.univrouen.sepa26.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.univrouen.sepa26.dto.EndpointDoc;
import fr.univrouen.sepa26.services.IHelpService;

/**
 * Contrôleur REST moderne fournissant les métadonnées de l'application et la documentation des points d'accès.
 *
 * @author Florian Pépin
 * @version 2.0
 */
@RestController
public class IndexController {

    private final IHelpService helpService;

    @Value("${app.name:Projet SEPA26}")
    private String projectName;

    @Value("${app.version:0.0.1-SNAPSHOT}")
    private String version;

    @Value("${app.developer:Florian Pépin}")
    private String developer;

    @Autowired
    public IndexController(IHelpService helpService) {
        this.helpService = helpService;
    }

    /**
     * Point d'accès racine fournissant les métadonnées et le statut de l'API en JSON.
     * @return Réponse JSON avec le statut et les métadonnées.
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> index() {
        return ResponseEntity.ok(Map.of(
                "message", "Bienvenue sur le service REST SEPA26",
                "name", projectName,
                "version", version,
                "developer", developer,
                "status", "UP",
                "documentation", "/help"
        ));
    }

    /**
     * Documentation exhaustive de l'API REST sous forme de flux JSON.
     * Consommée dynamiquement par l'application frontend Angular.
     * @return Liste typée des descripteurs d'endpoints.
     */
    @GetMapping(value = {"/help", "/sepa26/help"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EndpointDoc> help() {
        return helpService.getEndpoints();
    }
}