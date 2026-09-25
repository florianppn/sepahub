package fr.univrouen.sepa26.builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import fr.univrouen.sepa26.model.Document;

/**
 * Interface abstraite du patron de conception GoF "Builder" (Monteur).
 * Définit les étapes abstraites nécessaires à la construction par étapes
 * d'un document SEPA complexe (pain.008.001.02).
 * La construction d'une transaction se fait par étapes : {@code beginTransaction} -> setters -> {@link #buildTransaction()}.
 * Le produit final complet est restitué via {@link #getResult()}.
 *
 * @author Florian Pépin
 * @version 2.0
 */
public interface ISepaDocumentBuilder {

    /**
     * Réinitialise le monteur pour démarrer une nouvelle construction à partir de zéro.
     */
    void reset();

    /**
     * Renseigne l'en-tête de groupe (GrpHdr).
     * @param msgId Identifiant unique du message.
     * @param creationDateTime Date et heure de création.
     * @param initiatingPartyName Nom de l'initiateur du prélèvement.
     */
    void setGroupHeader(String msgId, LocalDateTime creationDateTime, String initiatingPartyName);

    /**
     * Renseigne les métadonnées du lot de paiement (PmtInf).
     * @param pmtInfId Identifiant unique du lot de paiement.
     * @param collectionDate Date d'échéance souhaitée pour le prélèvement.
     */
    void setPaymentInformation(String pmtInfId, LocalDate collectionDate);

    /**
     * Renseigne les coordonnées bancaires et légales du créancier (Cdtr).
     * @param name Nom ou raison sociale du créancier.
     * @param iban Compte IBAN du créancier.
     * @param bic Code BIC de l'établissement bancaire du créancier.
     * @param creditorSchemeId Identifiant créancier SEPA (ICS).
     */
    void setCreditor(String name, String iban, String bic, String creditorSchemeId);

    /**
     * Débute la construction unitaire d'une nouvelle transaction de prélèvement (DrctDbtTxInf).
     */
    void beginTransaction();

    /**
     * Renseigne l'identifiant de la transaction de paiement en cours (PmtId / EndToEndId).
     * @param pmtId Identifiant unique de la transaction.
     */
    void setPmtId(String pmtId);

    /**
     * Renseigne le montant en euros de la transaction en cours.
     * @param amount Montant prélevé.
     */
    void setAmount(double amount);

    /**
     * Renseigne les informations du débiteur pour la transaction en cours.
     * @param debtorName Nom du débiteur prélevé.
     * @param debtorIban IBAN du compte bancaire débité.
     * @param debtorBic BIC de la banque du débiteur.
     */
    void setDebtor(String debtorName, String debtorIban, String debtorBic);

    /**
     * Renseigne le mandat de prélèvement SEPA pour la transaction en cours.
     * @param mandateId Référence Unique de Mandat (RUM).
     * @param signatureDate Date de signature du mandat par le débiteur.
     */
    void setMandate(String mandateId, LocalDate signatureDate);

    /**
     * Renseigne le libellé / motif de paiement pour la transaction en cours (Remittance Information).
     * @param remittanceInfo Motif (ex: Facture N°123).
     */
    void setRemittanceInfo(String remittanceInfo);

    /**
     * Finalise la transaction unitaire en cours d'assemblage et l'ajoute au lot.
     */
    void buildTransaction();

    /**
     * Méthode de commodité permettant d'ajouter une transaction complète en un seul appel.
     */
    void addTransaction(
            String pmtId,
            double amount,
            String debtorName,
            String debtorIban,
            String debtorBic,
            String mandateId,
            LocalDate signatureDate,
            String remittanceInfo);

    /**
     * Surcharge simplifiée pour ajouter une transaction avec date de mandat et BIC par défaut.
     * @param pmtId Identifiant unique de la transaction.
     * @param amount Montant prélevé.
     * @param debtorName Nom du débiteur.
     * @param debtorIban IBAN du débiteur.
     * @param mandateId Référence du mandat SEPA.
     */
    void addTransaction(
            String pmtId,
            double amount,
            String debtorName,
            String debtorIban,
            String mandateId);

    /**
     * Finalise le document global, calcule automatiquement le nombre de transactions (nbOfTxs)
     * et la somme arithmétique de contrôle (ctrlSum), et retourne le produit Document fini.
     * @return Le Document SEPA assemblé et prêt à l'emploi.
     */
    Document getResult();
}
