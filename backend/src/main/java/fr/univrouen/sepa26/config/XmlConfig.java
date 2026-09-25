package fr.univrouen.sepa26.config;

import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

import fr.univrouen.sepa26.model.Document;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

/**
 * Configuration Spring pour la gestion optimisée des ressources XML.
 * Centralise et met en cache le schéma XSD et le contexte JAXB (thread-safe)
 * pour éviter leur recréation coûteuse à chaque requête.
 */
@Configuration
public class XmlConfig {

    private static final String XSD_PATH = "xml/sepa26.xsd";

    /**
     * Schéma XSD précompilé une seule fois au démarrage de l'application.
     */
    @Bean
    public Schema sepaSchema() throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // Protection contre les failles XXE
        try {
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        } catch (Exception ignored) {
            // Certaines implémentations de SchemaFactory ne supportent pas ces propriétés
        }
        return factory.newSchema(new ClassPathResource(XSD_PATH).getURL());
    }

    /**
     * Contexte JAXB réutilisable et thread-safe pour la classe Document.
     */
    @Bean
    public JAXBContext documentJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(Document.class);
    }
}
