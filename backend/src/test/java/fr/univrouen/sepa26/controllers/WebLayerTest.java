package fr.univrouen.sepa26.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class WebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testIndexPage() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bienvenue sur le service REST SEPA26")))
                .andExpect(content().string(containsString("Florian")));
    }

    @Test
    public void testHelpEndpoint() throws Exception {
        this.mockMvc.perform(get("/help"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].url").exists())
                .andExpect(jsonPath("$[0].method").value("GET"));
    }

    @Test
    public void testSepaHelpRestJson() throws Exception {
        this.mockMvc.perform(get("/sepa26/help"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].url").exists());
    }

    @Test
    public void testGetResumeXml() throws Exception {
        this.mockMvc.perform(get("/sepa26/resume/xml").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/DocumentList").exists());
    }

    @Test
    public void testNotFoundReturnsProblemDetailJson() throws Exception {
        this.mockMvc.perform(get("/api/unknown-endpoint").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Ressource non trouvée"));
    }

    @Test
    public void testNotFoundReturnsSepaResponseXml() throws Exception {
        this.mockMvc.perform(get("/api/unknown-endpoint").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/SepaResponse/status").string("ERROR"));
    }

    @Test
    public void testInsertAndGetDetailAndDelete() throws Exception {
    	String validXml =
    		    "<Document xmlns=\"http://univ.fr/sepa26\">" +
    		    "  <CstmrDrctDbtInitn>" +
    		    "    <GrpHdr>" +
    		    "      <MsgId>MSG-TEST</MsgId>" +
    		    "      <CreDtTm>2026-03-01T10:00:00</CreDtTm>" +
    		    "      <NbOfTxs>1</NbOfTxs>" +
    		    "      <CtrlSum>500.00</CtrlSum>" +
    		    "      <InitgPty>" +
    		    "        <Nm>Test Company</Nm>" +
    		    "      </InitgPty>" +
    		    "    </GrpHdr>" +
    		    "    <PmtInf>" +
    		    "      <PmtInfId>PMT-TEST-1</PmtInfId>" +
    		    "      <NbOfTxs>1</NbOfTxs>" +
    		    "      <CtrlSum>500.00</CtrlSum>" +
    		    "      <PmtTpInf>" +
    		    "        <SvcLvl><Cd>SEPA</Cd></SvcLvl>" +
    		    "        <LclInstrm><Cd>CORE</Cd></LclInstrm>" +
    		    "        <SeqTp>RCUR</SeqTp>" +
    		    "      </PmtTpInf>" +
    		    "      <ReqdColltnDt>2026-03-10</ReqdColltnDt>" +
    		    "      <Cdtr>" +
    		    "        <Nm>Test Company</Nm>" +
    		    "      </Cdtr>" +
    		    "      <CdtrAcct>" +
    		    "        <Id>" +
    		    "          <IBAN>FR7612345678901234567890123</IBAN>" +
    		    "        </Id>" +
    		    "      </CdtrAcct>" +
    		    "      <CdtrAgt>" +
    		    "        <FinInstnId>" +
    		    "          <BIC>ROUENSWNXXX</BIC>" +
    		    "        </FinInstnId>" +
    		    "      </CdtrAgt>" +
    		    "      <CdtrSchmeId>" +
    		    "        <Id>" +
    		    "          <PrvtId>" +
    		    "            <Othr>" +
    		    "              <Id>FR00ZZZ123456</Id>" +
    		    "              <SchmeNm>" +
    		    "                <Prtry>SEPA</Prtry>" +
    		    "              </SchmeNm>" +
    		    "            </Othr>" +
    		    "          </PrvtId>" +
    		    "        </Id>" +
    		    "      </CdtrSchmeId>" +
    		    "      <DrctDbtTxInf>" +
    		    "        <PmtId>REF-TEST-INSERT</PmtId>" +
    		    "        <InstdAmt Ccy=\"EUR\">500.00</InstdAmt>" +
    		    "        <DrctDbtTx>" +
    		    "          <MndtRltdInf>" +
    		    "            <MndtId>MANDAT-TEST</MndtId>" +
    		    "            <DtOfSgntr>2026-03-01</DtOfSgntr>" +
    		    "          </MndtRltdInf>" +
    		    "        </DrctDbtTx>" +
    		    "        <DbtrAgt>" +
    		    "          <FinInstnId>" +
    		    "            <BIC>ROUENSWNXXX</BIC>" +
    		    "          </FinInstnId>" +
    		    "        </DbtrAgt>" +
    		    "        <Dbtr>" +
    		    "          <Nm>Client Test</Nm>" +
    		    "        </Dbtr>" +
    		    "        <DbtrAcct>" +
    		    "          <Id>" +
    		    "            <IBAN>FR7612345678901234567890123</IBAN>" +
    		    "          </Id>" +
    		    "        </DbtrAcct>" +
    		    "        <RmtInf>Facture Test</RmtInf>" +
    		    "      </DrctDbtTxInf>" +
    		    "    </PmtInf>" +
    		    "  </CstmrDrctDbtInitn>" +
    		    "</Document>";

    	
        // 1. Insert
        MvcResult insertResult = this.mockMvc.perform(post("/sepa26/insert")
                .contentType(MediaType.APPLICATION_XML)
                .content(validXml)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/SepaResponse/status").string("INSERTED"))
                .andReturn();
        
        String responseBody = insertResult.getResponse().getContentAsString();
        String idStr = responseBody.replaceAll(".*<id>(\\d+)</id>.*", "$1");
        long id = Long.parseLong(idStr);

        // 2. Get Detail XML
        this.mockMvc.perform(get("/sepa26/xml/" + id).accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/Document/CstmrDrctDbtInitn/PmtInf/DrctDbtTxInf/PmtId").string("REF-TEST-INSERT"));

        // 3. Get Detail HTML
        this.mockMvc.perform(get("/sepa26/html/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("REF-TEST-INSERT")));

        // 4. Delete
        this.mockMvc.perform(delete("/sepa26/delete/" + id).accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/SepaResponse/status").string("DELETED"));

        // 5. Verify deleted
        this.mockMvc.perform(get("/sepa26/xml/" + id).accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/SepaResponse/status").string("ERROR"));
    }

    @Test
    public void testInsertInvalidXml() throws Exception {
        String invalidXml = "<Document xmlns=\"http://univ.fr/sepa26\"><Invalid/></Document>";
        this.mockMvc.perform(post("/sepa26/insert")
                .contentType(MediaType.APPLICATION_XML)
                .content(invalidXml)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/SepaResponse/status").string("ERROR"))
                .andExpect(xpath("/SepaResponse/description").exists());
    }

    @Test
    public void testInsertMalformedXml() throws Exception {
        String malformedXml = "<Document xmlns=\"http://univ.fr/sepa26\"><NotClosed>";
        this.mockMvc.perform(post("/sepa26/insert")
                .contentType(MediaType.APPLICATION_XML)
                .content(malformedXml)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/SepaResponse/status").string("ERROR"))
                .andExpect(xpath("/SepaResponse/description").exists());
    }
}
