package fr.univrouen.sepa26.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.univrouen.sepa26.repository.DocumentRepository;
import fr.univrouen.sepa26.services.SepaXmlService;

/**
 * Coordinateur Spring du patron de conception GoF "Chain of Responsibility".
 * Instancie et assemble la chaîne des gestionnaires de validation dans l'ordre requis :
 * 1. XsdValidationHandler (Conformité de structure XSD)
 * 2. XmlParsingHandler (Désérialisation JAXB)
 * 3. PmtIdUniquenessHandler (Règle d'unicité PmtId en BDD)
 * 4. ControlSumValidationHandler (Cohérence arithmétique de CtrlSum)
 */
@Component
public class SepaValidationChain {

    private final AbstractValidationHandler firstHandler;

    @Autowired
    public SepaValidationChain(SepaXmlService xmlService, DocumentRepository repository) {
        XsdValidationHandler xsdHandler = new XsdValidationHandler(xmlService);
        XmlParsingHandler parsingHandler = new XmlParsingHandler(xmlService);
        PmtIdUniquenessHandler uniquenessHandler = new PmtIdUniquenessHandler(repository);
        ControlSumValidationHandler controlSumHandler = new ControlSumValidationHandler();

        // Assemblage séquentiel de la chaîne
        xsdHandler.setNext(parsingHandler)
                  .setNext(uniquenessHandler)
                  .setNext(controlSumHandler);

        this.firstHandler = xsdHandler;
    }

    /**
     * Exécute la chaîne de validation et retourne le contexte complet avec l'éventuel Document produit.
     * @param rawXml Flux XML brut.
     * @param validateXsd Indique si la validation XSD doit être effectuée.
     * @return Le ValidationContext complété.
     */
    public ValidationContext process(String rawXml, boolean validateXsd) {
        ValidationContext context = new ValidationContext(rawXml, validateXsd);
        ValidationResult result = firstHandler.handle(context);
        context.setResult(result);
        return context;
    }

    /**
     * Raccourci pour obtenir directement le résultat d'évaluation de la chaîne.
     */
    public ValidationResult validate(String rawXml, boolean validateXsd) {
        return process(rawXml, validateXsd).getResult();
    }
}
