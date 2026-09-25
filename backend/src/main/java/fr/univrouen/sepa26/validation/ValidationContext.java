package fr.univrouen.sepa26.validation;

import fr.univrouen.sepa26.model.Document;

/**
 * Contexte partagé transmis à travers les maillons de la chaîne de responsabilité.
 * Maintient l'état de la requête entrante et le document désérialisé.
 */
public class ValidationContext {

    private final String rawXml;
    private final boolean validateXsd;
    private Document document;
    private ValidationResult result = ValidationResult.ok();

    public ValidationContext(String rawXml, boolean validateXsd) {
        this.rawXml = rawXml;
        this.validateXsd = validateXsd;
    }

    public String getRawXml() {
        return rawXml;
    }

    public boolean isValidateXsd() {
        return validateXsd;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public ValidationResult getResult() {
        return result;
    }

    public void setResult(ValidationResult result) {
        this.result = result;
    }
}
