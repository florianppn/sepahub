package fr.univrouen.sepa26.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "transaction_info")
@XmlAccessorType(XmlAccessType.FIELD)
public class DrctDbtTxInf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drct_dbt_tx_inf_id")
    @XmlTransient
    private Long idDrctDbt;

    @XmlElement(name = "PmtId")
    @JacksonXmlProperty(localName = "PmtId")
    private String pmtId;

    @Embedded
    @XmlElement(name = "InstdAmt")
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "instdamt_value")),
            @AttributeOverride(name = "ccy", column = @Column(name = "instdamt_ccy"))
    })
    private InstdAmt instdAmt;

    @Embedded
    @XmlElement(name = "DrctDbtTx")
    private DrctDbtTx drctDbtTx;

    @Embedded
    @XmlElement(name = "DbtrAgt")
    @AttributeOverrides({
            @AttributeOverride(name = "finInstnId.bic", column = @Column(name = "dbtragt_bic")),
            @AttributeOverride(name = "finInstnId.othr.id", column = @Column(name = "dbtragt_id")),
            @AttributeOverride(name = "finInstnId.othr.schemeName.prtry", column = @Column(name = "dbtragt_prtry"))
    })
    private Agent dbtrAgt;

    @Embedded
    @XmlElement(name = "Dbtr")
    @AttributeOverrides({
            @AttributeOverride(name = "nm", column = @Column(name = "dbtr_nm"))
    })
    private Party dbtr;

    @Embedded
    @XmlElement(name = "DbtrAcct")
    @AttributeOverrides({
            @AttributeOverride(name = "id.iban", column = @Column(name = "dbtracct_iban")),
            @AttributeOverride(name = "id.prvtId.othr.id", column = @Column(name = "dbtracct_prvtid")),
            @AttributeOverride(name = "id.prvtId.othr.schemeName.prtry", column = @Column(name = "dbtracct_prtry"))
    })
    private Account dbtrAcct;

    @XmlElement(name = "RmtInf")
    private String rmtInf;

    public Long getId() {
        return idDrctDbt;
    }

    public void setId(Long idDrctDbt) {
        this.idDrctDbt = idDrctDbt;
    }

    public String getPmtId() {
        return pmtId;
    }

    public void setPmtId(String pmtId) {
        this.pmtId = pmtId;
    }

    public InstdAmt getInstdAmt() {
        return instdAmt;
    }

    public void setInstdAmt(InstdAmt instdAmt) {
        this.instdAmt = instdAmt;
    }

    public DrctDbtTx getDrctDbtTx() {
        return drctDbtTx;
    }

    public void setDrctDbtTx(DrctDbtTx drctDbtTx) {
        this.drctDbtTx = drctDbtTx;
    }

    public Agent getDbtrAgt() {
        return dbtrAgt;
    }

    public void setDbtrAgt(Agent dbtrAgt) {
        this.dbtrAgt = dbtrAgt;
    }

    public Party getDbtr() {
        return dbtr;
    }

    public void setDbtr(Party dbtr) {
        this.dbtr = dbtr;
    }

    public Account getDbtrAcct() {
        return dbtrAcct;
    }

    public void setDbtrAcct(Account dbtrAcct) {
        this.dbtrAcct = dbtrAcct;
    }

    public String getRmtInf() {
        return rmtInf;
    }

    public void setRmtInf(String rmtInf) {
        this.rmtInf = rmtInf;
    }
}
