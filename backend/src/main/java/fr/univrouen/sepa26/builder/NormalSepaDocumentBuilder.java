package fr.univrouen.sepa26.builder;

/**
 * Ancien nom du monteur concret de document SEPA.
 *
 * @deprecated Utilisez directement {@link SepaDocumentBuilder}.
 */
@Deprecated(since = "2.1", forRemoval = true)
public class NormalSepaDocumentBuilder extends SepaDocumentBuilder {

    public NormalSepaDocumentBuilder() {
        super();
    }

    public NormalSepaDocumentBuilder(ISepaElementFactory factory) {
        super(factory);
    }
}
