package fr.univrouen.sepa26.validation;

import fr.univrouen.sepa26.services.SepaXmlService;

/**
 * Maillon 1 : Validation syntaxique par rapport au schéma XSD officiel (sepa26.xsd).
 * Activé lorsque le paramètre validate est vrai.
 */
public class XsdValidationHandler extends AbstractValidationHandler {

    private final SepaXmlService xmlService;

    public XsdValidationHandler(SepaXmlService xmlService) {
        this.xmlService = xmlService;
    }

    @Override
    public ValidationResult handle(ValidationContext context) {
        if (context.isValidateXsd()) {
            SepaXmlService.ValidationResult xsdResult = xmlService.validateXsd(context.getRawXml());
            if (!xsdResult.isValid()) {
                ValidationResult result = ValidationResult.fail(xsdResult.getErrorMessage());
                context.setResult(result);
                return result;
            }
        }
        return checkNext(context);
    }
}
