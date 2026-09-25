package fr.univrouen.sepa26.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.univrouen.sepa26.dto.SepaResponse;
import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.services.ISepaService;
import fr.univrouen.sepa26.validation.SepaValidationChain;
import fr.univrouen.sepa26.validation.ValidationContext;

/**
 * Contrôleur REST gérant les modifications de données.
 * Traite les envois (POST) et les suppressions (DELETE) de documents.
 * Délègue l'évaluation des règles à la chaîne de responsabilité GoF (SepaValidationChain).
 */
@RestController
@RequestMapping("/sepa26")
public class PostController {

    private final ISepaService sepaService;
    private final SepaValidationChain validationChain;

    @Autowired
    public PostController(ISepaService sepaService, SepaValidationChain validationChain) {
        this.sepaService = sepaService;
        this.validationChain = validationChain;
    }

    /**
     * Ajoute un nouveau document SEPA.
     * Réalise la validation syntaxique et métier via la chaîne de responsabilité (Chain of Responsibility).
     *
     * @param xmlRaw Le flux XML envoyé dans le corps de la requête.
     * @param validate Booléen indiquant si la validation XSD doit être effectuée (défaut true).
     * @return Un objet SepaResponse (INSERTED ou ERROR avec description).
     */
    @PostMapping(value = "/insert",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public SepaResponse insert(
            @RequestBody String xmlRaw,
            @RequestParam(value = "validate", defaultValue = "true") boolean validate) {
        try {
            // Évaluation complète via la chaîne de responsabilité
            ValidationContext context = validationChain.process(xmlRaw, validate);
            if (!context.getResult().isValid()) {
                return new SepaResponse("ERROR", context.getResult().getErrorMessage());
            }

            // Persistance de l'objet Document assemblé par la chaîne
            Document saved = sepaService.save(context.getDocument());
            if (saved == null) {
                return new SepaResponse("ERROR", "Échec de l'enregistrement en base de données");
            }

            return new SepaResponse(saved.getId(), "INSERTED");
        } catch (Exception e) {
            return new SepaResponse("ERROR", "Erreur interne : " + e.getMessage());
        }
    }

    /**
     * Supprime un document par son identifiant.
     * @param id L'ID du document à supprimer.
     * @return Un objet SepaResponse (DELETED ou ERROR).
     */
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public SepaResponse delete(@PathVariable long id) {
        if (sepaService.delete(id)) {
            return new SepaResponse(id, "DELETED");
        } else {
            return new SepaResponse("ERROR");
        }
    }
}