package fr.univrouen.sepa26.dto;

/**
 * DTO immuable représentant la documentation d'un point d'accès (endpoint) de l'API.
 *
 * @param url Chemin d'accès de l'endpoint (ex: /sepa26/xml/{id}).
 * @param method Méthode HTTP associée (GET, POST, DELETE).
 * @param format Format de retour attendu (XML, HTML, JSON).
 * @param operation Résumé succinct de l'opération.
 * @param description Description détaillée de l'opération et de ses paramètres.
 *
 * @author Florian Pépin
 * @version 2.0
 */
public record EndpointDoc(
        String url,
        String method,
        String format,
        String operation,
        String description
) {

    /**
     * Accesseur synonyme de format().
     * @return Le format de retour.
     */
    public String getReturn() {
        return format;
    }
}
