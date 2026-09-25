package fr.univrouen.sepa26.builder;

import java.time.LocalDate;

import fr.univrouen.sepa26.model.Account;
import fr.univrouen.sepa26.model.AccountId;
import fr.univrouen.sepa26.model.AccountSchemeId;
import fr.univrouen.sepa26.model.Agent;
import fr.univrouen.sepa26.model.FinInstnId;
import fr.univrouen.sepa26.model.InstdAmt;
import fr.univrouen.sepa26.model.LocalInstrument;
import fr.univrouen.sepa26.model.MndtRltdInf;
import fr.univrouen.sepa26.model.OtherIdentification;
import fr.univrouen.sepa26.model.Party;
import fr.univrouen.sepa26.model.PaymentTypeInfo;
import fr.univrouen.sepa26.model.PrivateId;
import fr.univrouen.sepa26.model.SchemeName;
import fr.univrouen.sepa26.model.ServiceLevel;

/**
 * Implémentation concrète de la fabrique {@link ISepaElementFactory}.
 * Isole l'instanciation des structures XML imbriquées requises par SEPA ISO 20022.
 *
 * @author Florian Pépin
 * @version 1.0
 */
public class SepaElementFactory implements ISepaElementFactory {

    @Override
    public Party createParty(String name) {
        Party party = new Party();
        party.setNm(name != null ? name : "Inconnu");
        return party;
    }

    @Override
    public Account createAccount(String iban) {
        Account acct = new Account();
        AccountId id = new AccountId();
        id.setIban(iban != null ? iban : "FR7612345678901234567890123");
        acct.setId(id);
        return acct;
    }

    @Override
    public Agent createAgent(String bic) {
        Agent agt = new Agent();
        FinInstnId fin = new FinInstnId();
        fin.setBic(bic != null ? bic : "BANKFRPPXXX");
        agt.setFinInstnId(fin);
        return agt;
    }

    @Override
    public AccountSchemeId createCreditorScheme(String schemeId) {
        AccountSchemeId scheme = new AccountSchemeId();
        AccountId id = new AccountId();
        PrivateId prvt = new PrivateId();
        OtherIdentification othr = new OtherIdentification();
        SchemeName schmeNm = new SchemeName();
        schmeNm.setPrtry("SEPA");
        othr.setId(schemeId != null ? schemeId : "FR00ZZZ123456");
        othr.setSchemeName(schmeNm);
        prvt.setOthr(othr);
        id.setPrvtId(prvt);
        scheme.setId(id);
        return scheme;
    }

    @Override
    public PaymentTypeInfo createDefaultPaymentTypeInfo() {
        PaymentTypeInfo pmtTp = new PaymentTypeInfo();
        ServiceLevel sl = new ServiceLevel();
        sl.setCd("SEPA");
        LocalInstrument li = new LocalInstrument();
        li.setCd("SEPA");
        pmtTp.setSvcLvl(sl);
        pmtTp.setLclInstrm(li);
        pmtTp.setSeqTp("RCUR");
        return pmtTp;
    }

    @Override
    public InstdAmt createEuroAmount(double amount) {
        InstdAmt amt = new InstdAmt();
        amt.setValue(amount);
        amt.setCcy("EUR");
        return amt;
    }

    @Override
    public MndtRltdInf createMandateInfo(String mandateId, LocalDate signatureDate) {
        MndtRltdInf mndt = new MndtRltdInf();
        mndt.setMndtId(mandateId != null ? mandateId : "MANDAT-DEF");
        mndt.setDtOfSgntr(signatureDate != null ? signatureDate : LocalDate.now().minusMonths(1));
        return mndt;
    }
}
