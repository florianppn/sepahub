package fr.univrouen.sepa26.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.univrouen.sepa26.TestDocumentBuilder;
import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.model.DrctDbtTxInf;
import fr.univrouen.sepa26.model.GrpHdr;
import fr.univrouen.sepa26.model.PmtInf;
import fr.univrouen.sepa26.repository.DocumentRepository;

/**
 * Tests unitaires pour SepaService utilisant des données mockées avec 2 transactions.
 * Démontre l'utilisation du TestDocumentBuilder pour générer des documents de test complets.
 */
@ExtendWith(MockitoExtension.class)
public class SepaServiceWithMockTransactionsTest {

    @Mock
    private DocumentRepository repository;

    @InjectMocks
    private SepaService sepaService;

    private Document docWithTwoTxs;

    @BeforeEach
    void setUp() {
        // Crée un document avec 2 transactions mockées
        docWithTwoTxs = TestDocumentBuilder.buildDocumentWithTwoTransactions();
    }

    @Test
    void testDocumentHasTwoTransactions() {
        assertNotNull(docWithTwoTxs, "Le document ne devrait pas être null");
        assertNotNull(docWithTwoTxs.getCstmrDrctDbtInitn(), "L'initialisation ne devrait pas être null");
        assertEquals(1, docWithTwoTxs.getCstmrDrctDbtInitn().getPmtInfs().size(), "Devrait avoir 1 paiement");

        PmtInf pmtInf = docWithTwoTxs.getCstmrDrctDbtInitn().getPmtInfs().get(0);
        assertEquals(2, pmtInf.getDrctDbtTxInfs().size(), "Devrait avoir 2 transactions");
    }

    @Test
    void testFirstTransactionData() {
        PmtInf pmtInf = docWithTwoTxs.getCstmrDrctDbtInitn().getPmtInfs().get(0);
        DrctDbtTxInf tx1 = pmtInf.getDrctDbtTxInfs().get(0);

        assertEquals("REF-MOCK-TX-001", tx1.getPmtId(), "ID de paiement 1");
        assertEquals(250.0, tx1.getInstdAmt().getValue(), "Montant TX1");
        assertEquals("EUR", tx1.getInstdAmt().getCcy(), "Devise TX1");
        assertEquals("Client A", tx1.getDbtr().getNm(), "Nom du débiteur TX1");
        assertEquals("MANDAT-MOCK-001", tx1.getDrctDbtTx().getMndtRltdInf().getMndtId(), "Mandat TX1");
    }

    @Test
    void testSecondTransactionData() {
        PmtInf pmtInf = docWithTwoTxs.getCstmrDrctDbtInitn().getPmtInfs().get(0);
        DrctDbtTxInf tx2 = pmtInf.getDrctDbtTxInfs().get(1);

        assertEquals("REF-MOCK-TX-002", tx2.getPmtId(), "ID de paiement 2");
        assertEquals(250.0, tx2.getInstdAmt().getValue(), "Montant TX2");
        assertEquals("EUR", tx2.getInstdAmt().getCcy(), "Devise TX2");
        assertEquals("Client B", tx2.getDbtr().getNm(), "Nom du débiteur TX2");
        assertEquals("MANDAT-MOCK-002", tx2.getDrctDbtTx().getMndtRltdInf().getMndtId(), "Mandat TX2");
    }

    @Test
    void testHeaderSummary() {
        GrpHdr grpHdr = docWithTwoTxs.getCstmrDrctDbtInitn().getGrpHdr();
        assertEquals(2, grpHdr.getNbOfTxs(), "Nombre de transactions dans le header");
        assertEquals(500.0, grpHdr.getCtrlSum(), "Somme de contrôle totale (250 + 250)");
    }

    @Test
    void testSaveWithTwoTransactions_Success() {
        // Mockage : pas de duplicat
        when(repository.findByPmtId("REF-MOCK-TX-001")).thenReturn(Optional.empty());
        when(repository.findByPmtId("REF-MOCK-TX-002")).thenReturn(Optional.empty());
        when(repository.save(any(Document.class))).thenReturn(docWithTwoTxs);

        Document saved = sepaService.save(docWithTwoTxs);

        assertNotNull(saved, "Le document sauvegardé ne devrait pas être null");
        verify(repository, times(1)).save(docWithTwoTxs);
    }

    @Test
    void testSaveWithDuplicate_FirstTransactionFails() {
        // Mockage : TX-001 est un doublon
        when(repository.findByPmtId("REF-MOCK-TX-001")).thenReturn(Optional.of(new Document()));

        Document result = sepaService.save(docWithTwoTxs);

        assertNull(result, "Le service devrait retourner null si une transaction est dupliquée");
        verify(repository, never()).save(any());
    }

    @Test
    void testConvertToXml() {
        String xml = sepaService.convertToXml(docWithTwoTxs);

        assertNotNull(xml, "Le XML ne devrait pas être null");
        assertFalse(xml.isEmpty(), "Le XML ne devrait pas être vide");
        assertTrue(xml.contains("REF-MOCK-TX-001"), "Le XML devrait contenir la TX1");
        assertTrue(xml.contains("REF-MOCK-TX-002"), "Le XML devrait contenir la TX2");
        assertTrue(xml.contains("Client A"), "Le XML devrait contenir le client A");
        assertTrue(xml.contains("Client B"), "Le XML devrait contenir le client B");
    }

    @Test
    void testXmlValidation() {
        String xml = sepaService.convertToXml(docWithTwoTxs);
        assertTrue(sepaService.validateXSDRaw(xml), "Le XML généré devrait être valide XSD");
    }
}
