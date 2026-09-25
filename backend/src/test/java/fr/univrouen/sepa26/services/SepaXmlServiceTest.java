package fr.univrouen.sepa26.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.univrouen.sepa26.TestDocumentBuilder;
import fr.univrouen.sepa26.model.Document;

/**
 * Tests unitaires pour le service SepaXmlService.
 * Vérifie l'isolation et la conformité des opérations XML (marshalling, unmarshalling, validation XSD).
 */
public class SepaXmlServiceTest {

    private SepaXmlService xmlService;
    private Document sampleDoc;

    @BeforeEach
    void setUp() {
        xmlService = new SepaXmlService();
        sampleDoc = TestDocumentBuilder.buildDocumentWithTwoTransactions();
    }

    @Test
    void testConvertToXml_Success() {
        String xml = xmlService.convertToXml(sampleDoc);
        assertNotNull(xml);
        assertTrue(xml.contains("<Document xmlns=\"http://univ.fr/sepa26\">"));
        assertTrue(xml.contains("REF-MOCK-TX-001"));
        assertTrue(xml.contains("REF-MOCK-TX-002"));
    }

    @Test
    void testConvertToXml_Null() {
        String xml = xmlService.convertToXml(null);
        assertTrue(xml.contains("<error>"));
    }

    @Test
    void testValidateXsd_ValidXml() {
        String xml = xmlService.convertToXml(sampleDoc);
        SepaXmlService.ValidationResult result = xmlService.validateXsd(xml);
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testValidateXsd_InvalidXml() {
        String invalidXml = "<Document xmlns=\"http://univ.fr/sepa26\"><InvalidTag/></Document>";
        SepaXmlService.ValidationResult result = xmlService.validateXsd(invalidXml);
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testValidateXsd_EmptyXml() {
        SepaXmlService.ValidationResult result = xmlService.validateXsd("");
        assertFalse(result.isValid());
        assertEquals("Le contenu XML est vide", result.getErrorMessage());
    }

    @Test
    void testParseXml_Success() {
        String xml = xmlService.convertToXml(sampleDoc);
        SepaXmlService.ParseResult result = xmlService.parseXml(xml);
        assertTrue(result.isSuccess());
        assertNotNull(result.getDocument());
        assertEquals("MSG-MOCK-001", result.getDocument().getCstmrDrctDbtInitn().getGrpHdr().getMsgId());
    }

    @Test
    void testParseXml_MalformedXml() {
        String malformedXml = "<Document xmlns=\"http://univ.fr/sepa26\"><UnclosedTag>";
        SepaXmlService.ParseResult result = xmlService.parseXml(malformedXml);
        assertFalse(result.isSuccess());
        assertNull(result.getDocument());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testParseXml_Empty() {
        SepaXmlService.ParseResult result = xmlService.parseXml(null);
        assertFalse(result.isSuccess());
        assertEquals("Le contenu XML est vide", result.getErrorMessage());
    }
}
