package fr.univrouen.sepa26.validation;

/**
 * Encapsule le résultat d'évaluation d'un maillon ou de l'ensemble
 * de la chaîne de responsabilité (Chain of Responsibility).
 */
public class ValidationResult {

    private final boolean valid;
    private final String errorMessage;

    public ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult fail(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
