package fr.univrouen.sepa26.validation;

import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.model.DrctDbtTxInf;
import fr.univrouen.sepa26.model.PmtInf;
import fr.univrouen.sepa26.repository.DocumentRepository;

/**
 * Maillon 3 : Règle métier bancaire d'unicité de l'identifiant de paiement (PmtId).
 * Interroge la base de données pour rejeter tout document contenant un PmtId déjà présent.
 */
public class PmtIdUniquenessHandler extends AbstractValidationHandler {

    private final DocumentRepository repository;

    public PmtIdUniquenessHandler(DocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public ValidationResult handle(ValidationContext context) {
        Document doc = context.getDocument();
        if (doc != null && doc.getCstmrDrctDbtInitn() != null && doc.getCstmrDrctDbtInitn().getPmtInfs() != null) {
            for (PmtInf pmt : doc.getCstmrDrctDbtInitn().getPmtInfs()) {
                if (pmt.getDrctDbtTxInfs() == null) continue;
                for (DrctDbtTxInf tx : pmt.getDrctDbtTxInfs()) {
                    String pmtId = tx.getPmtId();
                    if (pmtId != null && repository.findByPmtId(pmtId).isPresent()) {
                        ValidationResult result = ValidationResult.fail("Doublon détecté : un PmtId identique existe déjà en base");
                        context.setResult(result);
                        return result;
                    }
                }
            }
        }
        return checkNext(context);
    }
}
