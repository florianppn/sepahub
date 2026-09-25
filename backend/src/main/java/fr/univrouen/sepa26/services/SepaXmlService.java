package fr.univrouen.sepa26.services;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import fr.univrouen.sepa26.model.Document;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

/**
 * Service dédié à la manipulation, validation et transformation XML (JAXB & XSD).
 * Respecte le principe de responsabilité unique (SRP) en isolant toutes les opérations XML.
 */
@Service
public class SepaXmlService {

    private final Schema schema;
    private final JAXBContext jaxbContext;

    /**
     * Résultat encapsulé d'une validation XSD.
     */
    public static class ValidationResult {
        public final boolean valid;
        public final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public ValidationResult(boolean valid) {
            this(valid, null);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Résultat encapsulé d'une désérialisation XML.
     */
    public static class ParseResult {
        public final Document document;
        public final String errorMessage;

        public ParseResult(Document document) {
            this.document = document;
            this.errorMessage = null;
        }

        public ParseResult(String errorMessage) {
            this.document = null;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return document != null;
        }

        public Document getDocument() {
            return document;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Injection par constructeur du schéma et du contexte JAXB (DIP & performance).
     * En cas d'appel direct hors Spring (ex: tests unitaires purs), des valeurs par défaut sont initialisées.
     */
    public SepaXmlService(Schema schema, JAXBContext jaxbContext) {
        this.schema = (schema != null) ? schema : initDefaultSchema();
        this.jaxbContext = (jaxbContext != null) ? jaxbContext : initDefaultJaxbContext();
    }

    /**
     * Constructeur sans argument pour faciliter les tests unitaires isolés.
     */
    public SepaXmlService() {
        this(null, null);
    }

    private static Schema initDefaultSchema() {
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            return sf.newSchema(new ClassPathResource("xml/sepa26.xsd").getURL());
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de charger le schéma XSD par défaut : " + e.getMessage(), e);
        }
    }

    private static JAXBContext initDefaultJaxbContext() {
        try {
            return JAXBContext.newInstance(Document.class);
        } catch (Exception e) {
            throw new IllegalStateException("Impossible d'initialiser JAXBContext par défaut : " + e.getMessage(), e);
        }
    }

    /**
     * Sérialise un objet Document en chaîne XML indentée.
     * Centralise la logique de marshalling pour respecter le principe DRY.
     *
     * @param doc Le document à convertir.
     * @return Le XML sous forme de chaîne, ou un bloc &lt;error&gt; en cas d'exception.
     */
    public String convertToXml(Document doc) {
        if (doc == null) {
            return "<error>Document is null</error>";
        }
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            marshaller.marshal(doc, sw);
            return sw.toString();
        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        }
    }

    /**
     * Valide un contenu XML brut par rapport au schéma XSD avec message d'erreur détaillé.
     *
     * @param xmlContent Contenu XML brut.
     * @return ValidationResult contenant le statut et l'éventuelle erreur.
     */
    public ValidationResult validateXsd(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            return new ValidationResult(false, "Le contenu XML est vide");
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            // Protection contre les vulnérabilités XXE
            try {
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (Exception ignored) {
                // Support partiel selon l'environnement XML
            }

            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(document));

            return new ValidationResult(true);
        } catch (Exception e) {
            String errorMsg = "Erreur de validation XSD : " + e.getMessage();
            return new ValidationResult(false, errorMsg);
        }
    }

    /**
     * Désérialise un XML brut en objet Document JAXB avec détails.
     *
     * @param xmlContent Contenu XML à parser.
     * @return ParseResult avec le Document ou l'erreur de désérialisation.
     */
    public ParseResult parseXml(String xmlContent) {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            return new ParseResult("Le contenu XML est vide");
        }
        try {
            Document doc = (Document) jaxbContext.createUnmarshaller()
                    .unmarshal(new StringReader(xmlContent));
            return new ParseResult(doc);
        } catch (Exception e) {
            String message = (e.getMessage() != null) ? e.getMessage() : e.toString();
            String errorMsg = "Erreur de parsing XML (JAXB) : " + message;
            return new ParseResult(errorMsg);
        }
    }
}
