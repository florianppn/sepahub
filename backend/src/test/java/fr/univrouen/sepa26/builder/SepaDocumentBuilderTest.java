package fr.univrouen.sepa26.builder;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.model.DrctDbtTxInf;
import fr.univrouen.sepa26.model.GrpHdr;
import fr.univrouen.sepa26.model.PmtInf;

/**
 * Tests unitaires complets pour le patron de conception GoF "Builder" :
 * {@link ISepaDocumentBuilder}, {@link SepaDocumentBuilder} et {@link SepaDocumentDirector}.
 *
 * @author Florian Pépin
 * @version 2.0
 */
public class SepaDocumentBuilderTest {

    private ISepaDocumentBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new SepaDocumentBuilder();
    }

    @Test
    void testStepByStepBuildingAndAutomaticCalculations() {
        builder.reset();
        builder.setGroupHeader("MSG-TEST-CALC", LocalDateTime.now(), "Ma Société");
        builder.setPaymentInformation("PMT-TEST-CALC", LocalDate.now().plusDays(10));
        builder.setCreditor("Créancier Test", "FR7612345678901234567890123", "BANKFRPPXXX", "FR00ZZZ123456");

        // Transaction 1 : construction pas-à-pas
        builder.beginTransaction();
        builder.setPmtId("TX-1");
        builder.setAmount(120.50);
        builder.setDebtor("Client 1", "FR7611111111111111111111111", "BANKDEFFXXX");
        builder.setMandate("MANDAT-1", LocalDate.now().minusMonths(1));
        builder.setRemittanceInfo("Facture Mars");
        builder.buildTransaction();

        // Transaction 2 : construction pas-à-pas
        builder.beginTransaction();
        builder.setPmtId("TX-2");
        builder.setAmount(79.50);
        builder.setDebtor("Client 2", "FR7622222222222222222222222", "BANKDEFFXXX");
        builder.setMandate("MANDAT-2", LocalDate.now().minusMonths(2));
        builder.setRemittanceInfo("Facture Avril");
        builder.buildTransaction();

        Document doc = builder.getResult();

        assertNotNull(doc);
        assertNotNull(doc.getCstmrDrctDbtInitn());

        // Vérification de l'en-tête de groupe (GrpHdr)
        GrpHdr grpHdr = doc.getCstmrDrctDbtInitn().getGrpHdr();
        assertEquals("MSG-TEST-CALC", grpHdr.getMsgId());
        assertEquals(2, grpHdr.getNbOfTxs(), "Le nombre de transactions doit être calculé automatiquement");
        assertEquals(200.00, grpHdr.getCtrlSum(), 0.001, "La somme de contrôle doit être 120.50 + 79.50 = 200.00");

        // Vérification du lot de paiement (PmtInf)
        PmtInf pmtInf = doc.getCstmrDrctDbtInitn().getPmtInfs().get(0);
        assertEquals("PMT-TEST-CALC", pmtInf.getPmtInfId());
        assertEquals(2, pmtInf.getNbOfTxs());
        assertEquals(200.00, pmtInf.getCtrlSum(), 0.001);
        assertEquals(2, pmtInf.getDrctDbtTxInfs().size());

        // Vérification du contenu d'une transaction
        DrctDbtTxInf tx1 = pmtInf.getDrctDbtTxInfs().get(0);
        assertEquals("TX-1", tx1.getPmtId());
        assertEquals(120.50, tx1.getInstdAmt().getValue(), 0.001);
        assertEquals("Client 1", tx1.getDbtr().getNm());
        assertEquals("MANDAT-1", tx1.getDrctDbtTx().getMndtRltdInf().getMndtId());
    }

    @Test
    void testAddTransactionConvenienceMethod() {
        builder.reset();
        builder.setGroupHeader("MSG-CONVENIENCE", LocalDateTime.now(), "Entreprise ABC");
        builder.setPaymentInformation("PMT-CONVENIENCE", LocalDate.now().plusDays(5));
        builder.setCreditor("Créancier ABC", "FR7612345678901234567890123", "BANKFRPPXXX", "FR00ZZZ123456");

        builder.addTransaction(
                "TX-CONV-1", 100.00, "Debiteur 1", "FR7611111111111111111111111",
                "BANKDEFFXXX", "MANDAT-C1", LocalDate.now().minusMonths(1), "Paiement 1"
        );
        builder.addTransaction(
                "TX-CONV-2", 200.00, "Debiteur 2", "FR7622222222222222222222222",
                "BANKDEFFXXX", "MANDAT-C2", LocalDate.now().minusMonths(2), "Paiement 2"
        );

        Document doc = builder.getResult();
        assertNotNull(doc);
        assertEquals(2, doc.getCstmrDrctDbtInitn().getGrpHdr().getNbOfTxs());
        assertEquals(300.00, doc.getCstmrDrctDbtInitn().getGrpHdr().getCtrlSum(), 0.001);
    }

    @Test
    void testStateGuardThrowsWhenSettingFieldWithoutBeginTransaction() {
        builder.reset();
        // Tenter d'affecter un montant sans avoir appelé beginTransaction() au préalable
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> builder.setAmount(150.0));
        assertTrue(ex.getMessage().contains("Aucune transaction en cours"));
    }

    @Test
    void testStateGuardThrowsWhenBeginningTransactionWhileAnotherIsInProgress() {
        builder.reset();
        builder.beginTransaction();
        builder.setPmtId("TX-IN-PROGRESS");

        // Tenter d'ouvrir une seconde transaction sans clore la première
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> builder.beginTransaction());
        assertTrue(ex.getMessage().contains("Transaction en cours de construction"));
    }

    @Test
    void testStateGuardThrowsWhenGettingResultWithTransactionInProgress() {
        builder.reset();
        builder.beginTransaction();
        builder.setPmtId("TX-UNFINISHED");
        builder.setAmount(50.0);

        // Tenter de récupérer le résultat sans avoir appelé buildTransaction()
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> builder.getResult());
        assertTrue(ex.getMessage().contains("Transaction en cours de construction"));
    }

    @Test
    void testResetRestoresCleanState() {
        builder.reset();
        builder.setGroupHeader("MSG-TO-RESET", LocalDateTime.now(), "Societe");
        builder.addTransaction(
                "TX-1", 50.0, "Client", "FR7611111111111111111111111",
                "BANKDEFFXXX", "MANDAT-1", LocalDate.now(), "Test"
        );

        // Réinitialisation
        builder.reset();

        Document doc = builder.getResult();
        assertEquals(0, doc.getCstmrDrctDbtInitn().getPmtInfs().get(0).getDrctDbtTxInfs().size());
        assertEquals(0, doc.getCstmrDrctDbtInitn().getGrpHdr().getNbOfTxs());
        assertEquals(0.0, doc.getCstmrDrctDbtInitn().getGrpHdr().getCtrlSum(), 0.001);
    }

    @Test
    void testDirectorConstructsStandardDocument() {
        SepaDocumentDirector director = new SepaDocumentDirector(builder);

        Document doc = director.constructTwoTransactionsDocument("MSG-DIRECTOR-01", "Acme Corp", 150.0, 350.0);

        assertNotNull(doc);
        GrpHdr grpHdr = doc.getCstmrDrctDbtInitn().getGrpHdr();
        assertEquals("MSG-DIRECTOR-01", grpHdr.getMsgId());
        assertEquals(2, grpHdr.getNbOfTxs());
        assertEquals(500.0, grpHdr.getCtrlSum(), 0.001);
    }

    @Test
    void testDirectorConstructsSingleTransactionDocument() {
        SepaDocumentDirector director = new SepaDocumentDirector(builder);

        Document doc = director.constructSingleTransactionDocument(
                "MSG-SINGLE-01", "Beta SARL", "TX-SINGLE-1", 99.99,
                "Debiteur Solo", "FR7612345678901234567890123", "MANDAT-SOLO"
        );

        assertNotNull(doc);
        GrpHdr grpHdr = doc.getCstmrDrctDbtInitn().getGrpHdr();
        assertEquals(1, grpHdr.getNbOfTxs());
        assertEquals(99.99, grpHdr.getCtrlSum(), 0.001);
    }
}
