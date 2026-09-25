package fr.univrouen.sepa26.builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import fr.univrouen.sepa26.model.Document;

/**
 * Directeur (Director) du patron de conception GoF "Builder".
 * Responsable de l'orchestration des étapes de construction pour fabriquer
 * des configurations types de documents SEPA (pain.008.001.02).
 * Sépare l'algorithme d'assemblage de la structure interne des objets.
 *
 * @author Florian Pépin
 * @version 2.0
 */
public class SepaDocumentDirector {

    private final ISepaDocumentBuilder builder;

    /**
     * Initialise le directeur avec une instance de monteur.
     * @param builder Implémentation de ISepaDocumentBuilder à orchestrer.
     */
    public SepaDocumentDirector(ISepaDocumentBuilder builder) {
        this.builder = builder;
    }

    /**
     * Construit un document de démonstration standard composé de 2 transactions.
     * Démontre à la fois la construction étape par étape (transaction 1)
     * et l'utilisation de la méthode de commodité (transaction 2).
     *
     * @param msgId Identifiant unique du message.
     * @param companyName Raison sociale de l'émetteur.
     * @param tx1Amount Montant de la 1ère transaction.
     * @param tx2Amount Montant de la 2ème transaction.
     * @return Le Document SEPA complètement assemblé.
     */
    public Document constructTwoTransactionsDocument(
            String msgId,
            String companyName,
            double tx1Amount,
            double tx2Amount) {

        builder.reset();
        builder.setGroupHeader(msgId, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), companyName);
        builder.setPaymentInformation("PMT-" + msgId, LocalDate.now().plusDays(7));
        builder.setCreditor("Societe " + companyName, "FR7612345678901234567890123", "BANKFRPPXXX", "FR00ZZZ123456");

        // Transaction 1 : construction étape par étape avec begin/set/build
        builder.beginTransaction();
        builder.setPmtId("REF-" + msgId + "-01");
        builder.setAmount(tx1Amount);
        builder.setDebtor("Client 1 (" + companyName + ")", "FR7630001007941234567890185", "BANKDEFFXXX");
        builder.setMandate("MANDAT-" + msgId + "-01", LocalDate.now().minusMonths(1));
        builder.setRemittanceInfo("Facture service 1");
        builder.buildTransaction();

        // Transaction 2 : ajout via la méthode de commodité
        builder.addTransaction(
                "REF-" + msgId + "-02",
                tx2Amount,
                "Client 2 (" + companyName + ")",
                "FR7620041010050500013M02606",
                "BANKDEFFXXX",
                "MANDAT-" + msgId + "-02",
                LocalDate.now().minusMonths(2),
                "Facture service 2"
        );

        return builder.getResult();
    }

    /**
     * Construit un document unitaire avec une seule transaction.
     *
     * @param msgId Identifiant du message.
     * @param companyName Raison sociale de l'émetteur.
     * @param pmtId Identifiant unique de la transaction.
     * @param amount Montant prélevé.
     * @param debtorName Nom du débiteur.
     * @param debtorIban IBAN du débiteur.
     * @param mandateId Référence du mandat SEPA.
     * @return Le Document SEPA unitaire assemblé.
     */
    public Document constructSingleTransactionDocument(
            String msgId,
            String companyName,
            String pmtId,
            double amount,
            String debtorName,
            String debtorIban,
            String mandateId) {

        builder.reset();
        builder.setGroupHeader(msgId, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), companyName);
        builder.setPaymentInformation("PMT-" + msgId, LocalDate.now().plusDays(5));
        builder.setCreditor("Societe " + companyName, "FR7612345678901234567890123", "BANKFRPPXXX", "FR00ZZZ123456");

        builder.beginTransaction();
        builder.setPmtId(pmtId);
        builder.setAmount(amount);
        builder.setDebtor(debtorName, debtorIban, "BANKDEFFXXX");
        builder.setMandate(mandateId, LocalDate.now().minusMonths(1));
        builder.setRemittanceInfo("Prelevement ponctuel");
        builder.buildTransaction();

        return builder.getResult();
    }
}
