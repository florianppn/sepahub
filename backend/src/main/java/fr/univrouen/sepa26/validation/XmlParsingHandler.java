package fr.univrouen.sepa26.validation;

import fr.univrouen.sepa26.services.SepaXmlService;

/**
 * Maillon 2 : Désérialisation JAXB du flux XML en objet Document.
 * Place le document parsé dans le contexte pour les maillons suivants.
 */
public class XmlParsingHandler extends AbstractValidationHandler {

    private final SepaXmlService xmlService;

    public XmlParsingHandler(SepaXmlService xmlService) {
        this.xmlService = xmlService;
    }

    @Override
    public ValidationResult handle(ValidationContext context) {
        if (context.getDocument() == null) {
            SepaXmlService.ParseResult parseResult = xmlService.parseXml(context.getRawXml());
            if (!parseResult.isSuccess()) {
                ValidationResult result = ValidationResult.fail(parseResult.getErrorMessage());
                context.setResult(result);
                return result;
            }
            context.setDocument(parseResult.getDocument());
        }
        return checkNext(context);
    }
}
