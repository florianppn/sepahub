package fr.univrouen.sepa26.builder;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.univrouen.sepa26.model.*;

/**
 * Tests unitaires pour le patron de conception GoF "Abstract Factory" :
 * {@link ISepaElementFactory} et {@link SepaElementFactory}.
 *
 * @author Florian Pépin
 * @version 1.0
 */
public class SepaElementFactoryTest {

    private ISepaElementFactory factory;

    @BeforeEach
    void setUp() {
        factory = new SepaElementFactory();
    }

    @Test
    void testCreateParty() {
        Party p1 = factory.createParty("Acme Corp");
        assertNotNull(p1);
        assertEquals("Acme Corp", p1.getNm());

        Party pDefault = factory.createParty(null);
        assertNotNull(pDefault);
        assertEquals("Inconnu", pDefault.getNm());
    }

    @Test
    void testCreateAccount() {
        Account acc = factory.createAccount("FR7630001007941234567890185");
        assertNotNull(acc);
        assertNotNull(acc.getId());
        assertEquals("FR7630001007941234567890185", acc.getId().getIban());

        Account accDef = factory.createAccount(null);
        assertNotNull(accDef.getId().getIban());
    }

    @Test
    void testCreateAgent() {
        Agent agt = factory.createAgent("BANKFRPPXXX");
        assertNotNull(agt);
        assertNotNull(agt.getFinInstnId());
        assertEquals("BANKFRPPXXX", agt.getFinInstnId().getBic());

        Agent agtDef = factory.createAgent(null);
        assertNotNull(agtDef.getFinInstnId().getBic());
    }

    @Test
    void testCreateCreditorScheme() {
        AccountSchemeId scheme = factory.createCreditorScheme("FR00ZZZ123456");
        assertNotNull(scheme);
        assertNotNull(scheme.getId());
        assertNotNull(scheme.getId().getPrvtId());
        assertNotNull(scheme.getId().getPrvtId().getOthr());
        assertEquals("FR00ZZZ123456", scheme.getId().getPrvtId().getOthr().getId());
        assertEquals("SEPA", scheme.getId().getPrvtId().getOthr().getSchemeName().getPrtry());
    }

    @Test
    void testCreateDefaultPaymentTypeInfo() {
        PaymentTypeInfo info = factory.createDefaultPaymentTypeInfo();
        assertNotNull(info);
        assertEquals("SEPA", info.getSvcLvl().getCd());
        assertEquals("SEPA", info.getLclInstrm().getCd());
        assertEquals("RCUR", info.getSeqTp());
    }

    @Test
    void testCreateEuroAmount() {
        InstdAmt amt = factory.createEuroAmount(250.75);
        assertNotNull(amt);
        assertEquals(250.75, amt.getValue(), 0.001);
        assertEquals("EUR", amt.getCcy());
    }

    @Test
    void testCreateMandateInfo() {
        LocalDate date = LocalDate.of(2025, 6, 15);
        MndtRltdInf mandate = factory.createMandateInfo("MANDAT-42", date);
        assertNotNull(mandate);
        assertEquals("MANDAT-42", mandate.getMndtId());
        assertEquals(date, mandate.getDtOfSgntr());
    }
}
