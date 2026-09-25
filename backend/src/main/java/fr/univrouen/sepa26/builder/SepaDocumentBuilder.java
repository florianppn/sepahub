package fr.univrouen.sepa26.builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import fr.univrouen.sepa26.model.CstmrDrctDbtInitn;
import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.model.DrctDbtTx;
import fr.univrouen.sepa26.model.DrctDbtTxInf;
import fr.univrouen.sepa26.model.GrpHdr;
import fr.univrouen.sepa26.model.PmtInf;

/**
 * Implémentation concrète du patron GoF "Builder" : {@link ISepaDocumentBuilder}.
 * Construit un document SEPA (pain.008) par étapes successives avec contrôle strict d'état,
 * en déléguant la création des sous-composants XML à une {@link ISepaElementFactory} (Abstract Factory).
 *
 * @author Florian Pépin
 * @version 2.0
 */
public class SepaDocumentBuilder implements ISepaDocumentBuilder {

    private final ISepaElementFactory factory;
    private boolean transactionInProgress = false;

    // Attributs de la transaction en cours de fabrication
    private String txPmtId;
    private double txAmount;
    private String txDebtorName;
    private String txDebtorIban;
    private String txDebtorBic;
    private String txMandateId;
    private LocalDate txSignatureDate;
    private String txRemittanceInfo;

    // Document en cours de construction
    private Document document;
    private CstmrDrctDbtInitn cstmrInitn;
    private GrpHdr grpHdr;
    private PmtInf pmtInf;

    /**
     * Initialise le monteur avec la fabrique par défaut.
     */
    public SepaDocumentBuilder() {
        this(new SepaElementFactory());
    }

    /**
     * Initialise le monteur avec une fabrique d'éléments personnalisée.
     * @param factory Fabrique d'éléments XML ISO 20022.
     */
    public SepaDocumentBuilder(ISepaElementFactory factory) {
        this.factory = factory;
        reset();
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        transactionInProgress = false;
        resetTx();

        document = new Document();
        cstmrInitn = new CstmrDrctDbtInitn();
        document.setCstmrDrctDbtInitn(cstmrInitn);

        grpHdr = new GrpHdr();
        cstmrInitn.setGrpHdr(grpHdr);
        setGroupHeader(null, null, null);

        pmtInf = new PmtInf();
        pmtInf.setPmtTpInf(factory.createDefaultPaymentTypeInfo());
        cstmrInitn.getPmtInfs().add(pmtInf);
        setPaymentInformation(null, null);
        setCreditor(null, null, null, null);
    }

    private void resetTx() {
        txPmtId = null;
        txAmount = 0.0;
        txDebtorName = txDebtorIban = txDebtorBic = txMandateId = txRemittanceInfo = null;
        txSignatureDate = null;
    }

    private void ensureNoTransactionInProgress() {
        if (transactionInProgress) {
            throw new IllegalStateException("Transaction en cours de construction non finalisée : appelez buildTransaction() avant");
        }
    }

    private void requireTransactionInProgress() {
        if (!transactionInProgress) {
            throw new IllegalStateException("Aucune transaction en cours : appelez beginTransaction() au préalable");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setGroupHeader(String msgId, LocalDateTime creationDateTime, String initiatingPartyName) {
        ensureNoTransactionInProgress();
        grpHdr.setMsgId(msgId != null ? msgId : "MSG-" + System.currentTimeMillis());
        grpHdr.setCreDtTm(creationDateTime != null ? creationDateTime : LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        grpHdr.setInitgPty(factory.createParty(initiatingPartyName != null ? initiatingPartyName : "Initiateur par defaut"));
    }

    /** {@inheritDoc} */
    @Override
    public void setPaymentInformation(String pmtInfId, LocalDate collectionDate) {
        ensureNoTransactionInProgress();
        pmtInf.setPmtInfId(pmtInfId != null ? pmtInfId : "PMT-" + System.currentTimeMillis());
        pmtInf.setReqdColltnDt(collectionDate != null ? collectionDate : LocalDate.now().plusDays(7));
    }

    /** {@inheritDoc} */
    @Override
    public void setCreditor(String name, String iban, String bic, String creditorSchemeId) {
        ensureNoTransactionInProgress();
        pmtInf.setCdtr(factory.createParty(name != null ? name : "Creancier"));
        pmtInf.setCdtrAcct(factory.createAccount(iban != null ? iban : "FR7612345678901234567890123"));
        pmtInf.setCdtrAgt(factory.createAgent(bic != null ? bic : "BANKFRPPXXX"));
        pmtInf.setCdtrSchmeId(factory.createCreditorScheme(creditorSchemeId != null ? creditorSchemeId : "FR00ZZZ123456"));
    }

    /** {@inheritDoc} */
    @Override
    public void beginTransaction() {
        ensureNoTransactionInProgress();
        transactionInProgress = true;
        resetTx();
    }

    /** {@inheritDoc} */
    @Override
    public void setPmtId(String pmtId) {
        requireTransactionInProgress();
        this.txPmtId = pmtId;
    }

    /** {@inheritDoc} */
    @Override
    public void setAmount(double amount) {
        requireTransactionInProgress();
        this.txAmount = amount;
    }

    /** {@inheritDoc} */
    @Override
    public void setDebtor(String name, String iban, String bic) {
        requireTransactionInProgress();
        this.txDebtorName = name;
        this.txDebtorIban = iban;
        this.txDebtorBic = bic;
    }

    /** {@inheritDoc} */
    @Override
    public void setMandate(String mandateId, LocalDate signatureDate) {
        requireTransactionInProgress();
        this.txMandateId = mandateId;
        this.txSignatureDate = signatureDate;
    }

    /** {@inheritDoc} */
    @Override
    public void setRemittanceInfo(String remittanceInfo) {
        requireTransactionInProgress();
        this.txRemittanceInfo = remittanceInfo;
    }

    /** {@inheritDoc} */
    @Override
    public void buildTransaction() {
        requireTransactionInProgress();

        DrctDbtTxInf tx = new DrctDbtTxInf();
        tx.setPmtId(txPmtId != null ? txPmtId : "TX-" + System.currentTimeMillis());
        tx.setInstdAmt(factory.createEuroAmount(txAmount));

        DrctDbtTx dbtTx = new DrctDbtTx();
        dbtTx.setMndtRltdInf(factory.createMandateInfo(txMandateId, txSignatureDate));
        tx.setDrctDbtTx(dbtTx);

        tx.setDbtrAgt(factory.createAgent(txDebtorBic != null ? txDebtorBic : "BANKDEFFXXX"));
        tx.setDbtr(factory.createParty(txDebtorName != null ? txDebtorName : "Debiteur Inconnu"));
        tx.setDbtrAcct(factory.createAccount(txDebtorIban != null ? txDebtorIban : "FR7611111111111111111111111"));
        tx.setRmtInf(txRemittanceInfo != null ? txRemittanceInfo : "Facture");

        pmtInf.getDrctDbtTxInfs().add(tx);
        transactionInProgress = false;
        resetTx();
    }

    /** {@inheritDoc} */
    @Override
    public void addTransaction(String pmtId, double amount, String debtorName, String debtorIban,
                               String debtorBic, String mandateId, LocalDate signatureDate, String remittanceInfo) {
        beginTransaction();
        setPmtId(pmtId);
        setAmount(amount);
        setDebtor(debtorName, debtorIban, debtorBic);
        setMandate(mandateId, signatureDate);
        setRemittanceInfo(remittanceInfo);
        buildTransaction();
    }

    /** {@inheritDoc} */
    @Override
    public void addTransaction(String pmtId, double amount, String debtorName, String debtorIban, String mandateId) {
        addTransaction(pmtId, amount, debtorName, debtorIban, "BANKDEFFXXX", mandateId, null, "Facture");
    }

    /** {@inheritDoc} */
    @Override
    public Document getResult() {
        ensureNoTransactionInProgress();

        List<DrctDbtTxInf> txs = pmtInf.getDrctDbtTxInfs();
        int count = txs.size();
        double totalSum = Math.round(txs.stream()
                .mapToDouble(t -> t.getInstdAmt() != null ? t.getInstdAmt().getValue() : 0.0)
                .sum() * 100.0) / 100.0;

        grpHdr.setNbOfTxs(count);
        grpHdr.setCtrlSum(totalSum);
        pmtInf.setNbOfTxs(count);
        pmtInf.setCtrlSum(totalSum);

        return document;
    }
}
