package fr.univrouen.sepa26.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import fr.univrouen.sepa26.builder.ISepaDocumentBuilder;
import fr.univrouen.sepa26.builder.SepaDocumentBuilder;
import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.services.ISepaService;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Initialise la base de données avec 2 documents de test au démarrage de l'application.
 * Utilise le patron de conception GoF "Builder" (SepaDocumentBuilder) pour une construction
 * déclarative, sûre et hautement lisible des documents SEPA.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ISepaService sepaService;

    @Autowired
    public DataInitializer(ISepaService sepaService) {
        this.sepaService = sepaService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialiser uniquement si la base est vide
        if (sepaService.getLast10().isEmpty()) {
            System.out.println("Initialisation de la base de données avec 2 documents de test via SepaDocumentBuilder...");

            sepaService.save(createDocument1());
            sepaService.save(createDocument2());

            System.out.println("✓ Données de test créées avec succès via le Builder GoF !");
        }
    }

    /**
     * Crée le premier document de test (2 transactions de 300€).
     */
    private Document createDocument1() {
        ISepaDocumentBuilder builder = new SepaDocumentBuilder();
        builder.setGroupHeader("MSG-INIT-001", LocalDateTime.now(), "Societe A");
        builder.setPaymentInformation("PMT-INIT-001", LocalDate.now().plusDays(7));
        builder.setCreditor("Creancier INIT", "FR7612345678901234567890123", "BANKFRPPXXX", "FR00ZZZ123456");
        builder.addTransaction(
                "REF-INIT-001-A",
                300.00,
                "Client 1A",
                "FR7630001007941234567890185",
                "BANKDEFFXXX",
                "MANDAT-001-A",
                LocalDate.now().minusMonths(1),
                "Facture initiale 1A"
        );
        builder.addTransaction(
                "REF-INIT-001-B",
                300.00,
                "Client 1B",
                "FR7620041010050500013M02606",
                "BANKDEFFXXX",
                "MANDAT-001-B",
                LocalDate.now().minusMonths(1),
                "Facture initiale 1B"
        );
        return builder.getResult();
    }

    /**
     * Crée le second document de test (2 transactions de 500€).
     */
    private Document createDocument2() {
        ISepaDocumentBuilder builder = new SepaDocumentBuilder();
        builder.setGroupHeader("MSG-INIT-002", LocalDateTime.now(), "Societe B");
        builder.setPaymentInformation("PMT-INIT-002", LocalDate.now().plusDays(7));
        builder.setCreditor("Creancier INIT", "FR7612345678901234567890123", "BANKFRPPXXX", "FR00ZZZ123456");
        builder.addTransaction(
                "REF-INIT-002-A",
                500.00,
                "Client 2A",
                "FR7612548017150001234567890",
                "BANKDEFFXXX",
                "MANDAT-002-A",
                LocalDate.now().minusMonths(1),
                "Facture initiale 2A"
        );
        builder.addTransaction(
                "REF-INIT-002-B",
                500.00,
                "Client 2B",
                "FR7614508000505917721779613",
                "BANKDEFFXXX",
                "MANDAT-002-B",
                LocalDate.now().minusMonths(1),
                "Facture initiale 2B"
        );
        return builder.getResult();
    }
}
