package fr.univrouen.sepa26.services;

import java.util.List;

import fr.univrouen.sepa26.dto.EndpointDoc;

/**
 * Service fournissant le catalogue de documentation des points d'accès de l'API SEPA26.
 *
 * @author Florian Pépin
 * @version 2.0
 */
public interface IHelpService {

    /**
     * Retourne la liste exhaustive et ordonnée de la documentation des routes exposées.
     * @return Liste immuable des descripteurs d'endpoints.
     */
    List<EndpointDoc> getEndpoints();
}
