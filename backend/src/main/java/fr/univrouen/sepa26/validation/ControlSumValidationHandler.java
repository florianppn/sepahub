package fr.univrouen.sepa26.validation;

import java.util.List;

import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.model.DrctDbtTxInf;
import fr.univrouen.sepa26.model.GrpHdr;
import fr.univrouen.sepa26.model.PmtInf;

/**
 * Maillon 4 : Règle métier de contrôle de cohérence arithmétique.
 * Vérifie que la somme de contrôle déclarée dans l'en-tête (GrpHdr.ctrlSum)
 * correspond rigoureusement à la somme des montants individuels des transactions (InstdAmt).
 */
public class ControlSumValidationHandler extends AbstractValidationHandler {

    private static final double EPSILON = 0.01;

    @Override
    public ValidationResult handle(ValidationContext context) {
        Document doc = context.getDocument();
        if (doc != null && doc.getCstmrDrctDbtInitn() != null) {
            GrpHdr grpHdr = doc.getCstmrDrctDbtInitn().getGrpHdr();
            if (grpHdr != null) {
                double declaredSum = grpHdr.getCtrlSum();
                double computedSum = 0.0;

                List<PmtInf> pmtInfs = doc.getCstmrDrctDbtInitn().getPmtInfs();
                if (pmtInfs != null) {
                    for (PmtInf pmt : pmtInfs) {
                        if (pmt.getDrctDbtTxInfs() != null) {
                            for (DrctDbtTxInf tx : pmt.getDrctDbtTxInfs()) {
                                if (tx.getInstdAmt() != null) {
                                    computedSum += tx.getInstdAmt().getValue();
                                }
                            }
                        }
                    }
                }

                computedSum = Math.round(computedSum * 100.0) / 100.0;
                if (Math.abs(declaredSum - computedSum) > EPSILON) {
                    ValidationResult result = ValidationResult.fail(
                            String.format("Incohérence de contrôle financier : somme déclarée (%s) ≠ somme des transactions (%s)",
                                    declaredSum, computedSum)
                    );
                    context.setResult(result);
                    return result;
                }
            }
        }
        return checkNext(context);
    }
}
