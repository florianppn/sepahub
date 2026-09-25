package fr.univrouen.sepa26.validation;

/**
 * Classe de base abstraite pour le patron de conception GoF "Chain of Responsibility" (Chaîne de responsabilité).
 * Permet à plusieurs gestionnaires de traiter une requête de validation séquentiellement.
 * Chaque maillon décide soit de traiter et passer au maillon suivant, soit d'interrompre la chaîne
 * en cas d'erreur de conformité ou de règle métier (court-circuit / fail-fast).
 */
public abstract class AbstractValidationHandler {

    private AbstractValidationHandler next;

    /**
     * Chaîne le maillon suivant.
     * @param next Le gestionnaire successeur dans la chaîne.
     * @return Le gestionnaire successeur configuré.
     */
    public AbstractValidationHandler setNext(AbstractValidationHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Traite la requête de validation courante.
     * @param context Contexte d'exécution partagé contenant les données et l'état.
     * @return Résultat de validation (succès ou échec).
     */
    public abstract ValidationResult handle(ValidationContext context);

    /**
     * Transmet l'exécution au maillon suivant si présent, sinon conclut avec succès.
     * @param context Contexte d'exécution partagé.
     * @return Résultat final de la chaîne.
     */
    protected ValidationResult checkNext(ValidationContext context) {
        if (next == null) {
            return ValidationResult.ok();
        }
        return next.handle(context);
    }
}
