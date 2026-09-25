package fr.univrouen.sepa26.repository;

import fr.univrouen.sepa26.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface d'accès aux données pour les entités Document.
 * Utilise Spring Data JPA pour générer automatiquement les requêtes SQL.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Recherche un document en fonction de l'identifiant de paiement (pmtId)
     * d'une de ses transactions.
     *
     * @param pmtId L'identifiant unique du paiement.
     * @return Un Optional contenant le Document s'il est trouvé.
     */
    @Query("SELECT d FROM Document d " +
            "JOIN d.cstmrDrctDbtInitn p " +
            "JOIN p.pmtInfs pi " +
            "JOIN pi.drctDbtTxInfs t " +
            "WHERE t.pmtId = :pmtId")
    Optional<Document> findByPmtId(String pmtId);

    /**
     * Récupère les 10 derniers documents enregistrés en base.
     *
     * @return Liste des 10 documents les plus récents.
     */
    @Query(value = "SELECT * FROM documents ORDER BY document_id DESC LIMIT 10", nativeQuery = true)
    List<Document> findLast10();
    
    /**
     * Recherche des documents en fonction de critères de date et de montant.
     * @param date Date minimale de recherche.
     * @param sum Montant minimal de recherche.
     * @return La liste des documents correspondant aux critères, vide sinon.
     */
    @Query("SELECT d FROM Document d " +
    		"JOIN d.cstmrDrctDbtInitn i " +
    		"JOIN i.grpHdr g " +
    		"WHERE (:date IS NULL OR g.creDtTm >= :date) " +
    		"AND (:sum IS NULL OR g.ctrlSum >= :sum)")
    List<Document> search(@Param("date") LocalDateTime date, @Param("sum") Double sum);
}