package fr.univrouen.sepa26;

import java.time.LocalDate;
import java.time.LocalDateTime;

import fr.univrouen.sepa26.builder.ISepaDocumentBuilder;
import fr.univrouen.sepa26.builder.SepaDocumentBuilder;
import fr.univrouen.sepa26.model.Document;

/**
 * Fabrique de test pour créer des documents SEPA réalistes.
 * Délègue au patron de conception GoF "SepaDocumentBuilder".
 */
public class TestDocumentBuilder {

    /**
     * Crée un document avec 2 transactions par défaut.
     * @return Un document complètement peuplé avec 2 transactions de test (total 500€).
     */
    public static Document buildDocumentWithTwoTransactions() {
        ISepaDocumentBuilder builder = new SepaDocumentBuilder();
        builder.setGroupHeader("MSG-MOCK-001", LocalDateTime.parse("2026-04-09T14:00:00"), "Entreprise Mockee");
        builder.setPaymentInformation("PMT-MOCK-001", LocalDate.parse("2026-04-15"));
        builder.setCreditor("Creancier Mocke SARL", "FR7612345678901234567890123", "BANKFRPPXXX", "FR00ZZZ123456");
        builder.addTransaction(
                "REF-MOCK-TX-001",
                250.00,
                "Client A",
                "FR7612345678901234567890123",
                "BANKDEFFXXX",
                "MANDAT-MOCK-001",
                LocalDate.parse("2025-01-01"),
                "Facture automatique"
        );
        builder.addTransaction(
                "REF-MOCK-TX-002",
                250.00,
                "Client B",
                "FR7687654321098765432109876",
                "BANKDEFFXXX",
                "MANDAT-MOCK-002",
                LocalDate.parse("2025-01-01"),
                "Facture automatique"
        );
        return builder.getResult();
    }
}
