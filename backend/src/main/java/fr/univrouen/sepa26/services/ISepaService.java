package fr.univrouen.sepa26.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import fr.univrouen.sepa26.model.Document;

/**
 * Interface d'abstraction pour le service métier SEPA.
 * Respecte le principe d'inversion des dépendances (DIP) et d'ouverture/fermeture (OCP)
 * en découplant les contrôleurs de l'implémentation concrète.
 */
public interface ISepaService {

    /**
     * Sauvegarde un document en base après vérification métier de l'unicité du PmtId.
     * @param doc Le document à persister.
     * @return Le document sauvegardé ou null en cas de doublon.
     */
    Document save(Document doc);

    /**
     * Vérifie si un identifiant de paiement existe déjà en base de données.
     * @param pmtId Identifiant à vérifier.
     * @return true si présent, false sinon.
     */
    boolean exists(String pmtId);

    /**
     * Recherche un document par son identifiant technique unique.
     * @param id Identifiant numérique.
     * @return Un Optional contenant le document si trouvé.
     */
    Optional<Document> getById(long id);

    /**
     * Récupère la liste des 10 derniers documents enregistrés.
     * @return Liste de documents (au maximum 10).
     */
    List<Document> getLast10();

    /**
     * Supprime un document par son identifiant technique.
     * @param id Identifiant du document à supprimer.
     * @return true si suppression effectuée, false si introuvable.
     */
    boolean delete(long id);

    /**
     * Convertit un objet Document en chaîne XML formatée (JAXB).
     * @param doc Le document à convertir.
     * @return Le flux XML formatté.
     */
    String convertToXml(Document doc);

    /**
     * Valide un flux XML par rapport au schéma XSD officiel.
     * @param xmlContent Contenu XML brut.
     * @return true si valide, false sinon.
     */
    boolean validateXSDRaw(String xmlContent);

    /**
     * Valide un flux XML par rapport au schéma XSD avec message d'erreur détaillé.
     * @param xmlContent Contenu XML brut.
     * @return ValidationResult avec statut et message.
     */
    SepaService.ValidationResult validateXSDRawWithDetails(String xmlContent);

    /**
     * Recherche des documents par date minimale et/ou montant minimal.
     * @param date Date minimale (ou null si aucun filtre de date).
     * @param sum Montant minimal (ou null si aucun filtre de montant).
     * @return Liste des documents correspondants.
     */
    List<Document> search(LocalDateTime date, Double sum);
}
