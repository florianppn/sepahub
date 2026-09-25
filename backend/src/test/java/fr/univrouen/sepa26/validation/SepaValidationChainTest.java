package fr.univrouen.sepa26.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.univrouen.sepa26.builder.ISepaDocumentBuilder;
import fr.univrouen.sepa26.builder.SepaDocumentBuilder;
import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.repository.DocumentRepository;
import fr.univrouen.sepa26.services.SepaXmlService;

/**
 * Tests unitaires pour le patron de conception GoF "Chain of Responsibility" (SepaValidationChain).
 */
@ExtendWith(MockitoExtension.class)
public class SepaValidationChainTest {

    @Mock
    private DocumentRepository repository;

    private SepaXmlService xmlService;
    private SepaValidationChain validationChain;
    private String sampleValidXml;

    @BeforeEach
    void setUp() {
        xmlService = new SepaXmlService();
        validationChain = new SepaValidationChain(xmlService, repository);

        ISepaDocumentBuilder builder = new SepaDocumentBuilder();
        builder.setGroupHeader("MSG-CHAIN-TEST", null, "Initiateur Test");
        builder.setCreditor("Entreprise Test", "FR7612345678901234567890123", "BANKFRPPXXX", "FR00ZZZ123456");
        builder.addTransaction("REF-CHAIN-01", 150.0, "Client A", "FR7611111111111111111111111", "MANDAT-01");
        Document sampleDoc = builder.getResult();
        sampleValidXml = xmlService.convertToXml(sampleDoc);
    }

    @Test
    void testValidXmlPassesEntireChain() {
        when(repository.findByPmtId("REF-CHAIN-01")).thenReturn(Optional.empty());

        ValidationContext context = validationChain.process(sampleValidXml, true);

        assertTrue(context.getResult().isValid(), "La chaîne devrait accepter un document parfaitement conforme");
        assertNotNull(context.getDocument(), "Le document parsé doit être présent dans le contexte");
        assertEquals("MSG-CHAIN-TEST", context.getDocument().getCstmrDrctDbtInitn().getGrpHdr().getMsgId());
    }

    @Test
    void testXsdFailureShortCircuitsAtFirstHandler() {
        String invalidXml = "<Document xmlns=\"http://univ.fr/sepa26\"><InvalidTag/></Document>";

        ValidationContext context = validationChain.process(invalidXml, true);

        assertFalse(context.getResult().isValid(), "La validation XSD doit rejeter le XML");
        assertTrue(context.getResult().getErrorMessage().contains("Erreur de validation XSD"));
        assertNull(context.getDocument(), "Le parsing ne doit pas être exécuté après un échec XSD");
        verifyNoInteractions(repository);
    }

    @Test
    void testMalformedXmlFailsAtParsingHandler() {
        // validateXsd = false pour tester directement le maillon de parsing JAXB
        String malformedXml = "<Document xmlns=\"http://univ.fr/sepa26\"><NotClosed>";

        ValidationContext context = validationChain.process(malformedXml, false);

        assertFalse(context.getResult().isValid());
        assertTrue(context.getResult().getErrorMessage().contains("Erreur de parsing XML"));
        assertNull(context.getDocument());
        verifyNoInteractions(repository);
    }

    @Test
    void testDuplicatePmtIdFailsAtUniquenessHandler() {
        // Mockage : le PmtId existe déjà en base
        when(repository.findByPmtId("REF-CHAIN-01")).thenReturn(Optional.of(new Document()));

        ValidationContext context = validationChain.process(sampleValidXml, true);

        assertFalse(context.getResult().isValid(), "Le maillon d'unicité doit rejeter le doublon");
        assertTrue(context.getResult().getErrorMessage().contains("Doublon détecté"));
        assertNotNull(context.getDocument(), "Le document avait bien été parsé avant le rejet par l'unicité");
    }

    @Test
    void testControlSumDiscrepancyFailsAtControlSumHandler() {
        when(repository.findByPmtId(anyString())).thenReturn(Optional.empty());

        // Document altéré : CtrlSum déclaré à 999.00 alors que la transaction est de 150.00
        String tamperedXml = sampleValidXml.replace("<CtrlSum>150.0</CtrlSum>", "<CtrlSum>999.00</CtrlSum>");

        // On saute la validation XSD pour tester le comportement du maillon métier ControlSum
        ValidationContext context = validationChain.process(tamperedXml, false);

        assertFalse(context.getResult().isValid(), "Le maillon de contrôle financier doit détecter l'écart");
        assertTrue(context.getResult().getErrorMessage().contains("Incohérence de contrôle financier"));
    }
}
