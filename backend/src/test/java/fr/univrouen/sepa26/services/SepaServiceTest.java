package fr.univrouen.sepa26.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.univrouen.sepa26.model.*;
import fr.univrouen.sepa26.repository.DocumentRepository;

/**
 * Tests unitaires pour le service SepaService.
 * Utilise Mockito pour isoler la logique métier du repository.
 */
@ExtendWith(MockitoExtension.class)
public class SepaServiceTest {

    @Mock
    private DocumentRepository repository;

    @InjectMocks
    private SepaService sepaService;

    private Document validDoc;    
    
    @BeforeEach
    void setUp() {
        validDoc = new Document();
        
        CstmrDrctDbtInitn initn = new CstmrDrctDbtInitn();
        validDoc.setCstmrDrctDbtInitn(initn);

        GrpHdr grpHdr = new GrpHdr();
        grpHdr.setMsgId("MSG-UNIT-TEST");
        grpHdr.setCreDtTm(LocalDateTime.parse("2026-03-01T10:00:00"));
        grpHdr.setNbOfTxs(1);
        grpHdr.setCtrlSum(100.0);
        Party initgPty = new Party();
        initgPty.setNm("Test Company");
        grpHdr.setInitgPty(initgPty);
        initn.setGrpHdr(grpHdr);

        PmtInf pmtInf = new PmtInf();
        pmtInf.setPmtInfId("PMT-UNIT-1");
        pmtInf.setNbOfTxs(1);
        pmtInf.setCtrlSum(100.0);
        pmtInf.setReqdColltnDt(LocalDate.parse("2026-03-10"));
        
        PaymentTypeInfo pmtTpInf = new PaymentTypeInfo();
        ServiceLevel sl = new ServiceLevel();
        LocalInstrument li = new LocalInstrument();
        li.setCd("SEPA");
        sl.setCd("SEPA");
        pmtTpInf.setLclInstrm(li);
        pmtTpInf.setSvcLvl(sl);
        pmtTpInf.setSeqTp("RCUR");
        pmtInf.setPmtTpInf(pmtTpInf);
        

        Party cdtr = new Party();
        cdtr.setNm("Creditor Company");
        pmtInf.setCdtr(cdtr);

        Account cdtrAcct = new Account();
        AccountId cdtrAcctId = new AccountId();
        cdtrAcctId.setIban("FR7612345678901234567890123");
        cdtrAcct.setId(cdtrAcctId);
        pmtInf.setCdtrAcct(cdtrAcct);

        Agent cdtrAgt = new Agent();
        FinInstnId finCdtr = new FinInstnId();
        finCdtr.setBic("BANKFRPPXXX");
        cdtrAgt.setFinInstnId(finCdtr);
        pmtInf.setCdtrAgt(cdtrAgt);
        
        
        AccountSchemeId cdtrSchmeId = new AccountSchemeId();
        AccountId prvtIdWrapper = new AccountId();
        PrivateId prvtId = new PrivateId();
        OtherIdentification othr = new OtherIdentification();
        othr.setId("FR00ZZZ123456");
        SchemeName schmeNm = new SchemeName();
        schmeNm.setPrtry("SEPA");
        othr.setSchemeName(schmeNm);
        prvtId.setOthr(othr);
        prvtIdWrapper.setPrvtId(prvtId);
        cdtrSchmeId.setId(prvtIdWrapper);
        pmtInf.setCdtrSchmeId(cdtrSchmeId);
        

        DrctDbtTxInf txInf = new DrctDbtTxInf();
        txInf.setPmtId("REF-UNIT-TEST");

        InstdAmt amt = new InstdAmt();
        amt.setValue(100.0);
        amt.setCcy("EUR");
        txInf.setInstdAmt(amt);

        DrctDbtTx tx = new DrctDbtTx();
        MndtRltdInf mndt = new MndtRltdInf();
        mndt.setMndtId("MANDAT-UNIT");
        mndt.setDtOfSgntr(LocalDate.parse("2026-03-01"));
        tx.setMndtRltdInf(mndt);
        txInf.setDrctDbtTx(tx);

        Agent dbtrAgt = new Agent();
        FinInstnId finDbtr = new FinInstnId();
        finDbtr.setBic("BANKDEFFXXX");
        dbtrAgt.setFinInstnId(finDbtr);
        txInf.setDbtrAgt(dbtrAgt);

        Party dbtr = new Party();
        dbtr.setNm("Client Unitaire");
        txInf.setDbtr(dbtr);

        Account acct = new Account();
        AccountId acctId = new AccountId();
        acctId.setIban("FR7612345678901234567890123");
        acct.setId(acctId);
        txInf.setDbtrAcct(acct);
        
        txInf.setRmtInf("Facture Unitaire");
        
        pmtInf.getDrctDbtTxInfs().add(txInf);

        initn.getPmtInfs().add(pmtInf);
    }
    
    @Test
    void testValidateXSDRaw_Success() {
        String xml = sepaService.convertToXml(validDoc);
        assertTrue(sepaService.validateXSDRaw(xml));
    }
    
    @Test
    void testValidateXSDRaw_Failure() {
        String invalidXml = "<Document xmlns=\"http://univ.fr/sepa26\"></Document>";
        assertFalse(sepaService.validateXSDRaw(invalidXml));
    }
    
    @Test
    void testSave_Success() {
        when(repository.findByPmtId("REF-UNIT-TEST")).thenReturn(Optional.empty());
        when(repository.save(any(Document.class))).thenReturn(validDoc);

        Document saved = sepaService.save(validDoc);

        assertNotNull(saved, "Le document sauvegardé ne devrait pas être null");
        verify(repository, times(1)).save(validDoc);
    }
    
    @Test
    void testSave_DuplicateError() {
        when(repository.findByPmtId("REF-UNIT-TEST"))
            .thenReturn(Optional.of(new Document()));

        Document result = sepaService.save(validDoc);

        assertNull(result, "Le service devrait retourner null en cas de doublon");
        verify(repository, never()).save(any());
    }

    @Test
    void testDelete_Success() {
        long id = 123L;
        when(repository.existsById(id)).thenReturn(true);

        boolean deleted = sepaService.delete(id);

        assertTrue(deleted);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void testDelete_NotFound() {
        long id = 999L;
        when(repository.existsById(id)).thenReturn(false);

        boolean deleted = sepaService.delete(id);

        assertFalse(deleted);
        verify(repository, never()).deleteById(any());
    }
}

