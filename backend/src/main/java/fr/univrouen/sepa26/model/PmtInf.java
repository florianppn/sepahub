package fr.univrouen.sepa26.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import fr.univrouen.sepa26.util.LocalDateAdapter;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "payment_info")
@XmlAccessorType(XmlAccessType.FIELD)
public class PmtInf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pmtinf_id")
    @XmlTransient
    private Long idPmtInf;

    @XmlElement(name = "PmtInfId")
    private String pmtInfId;

    @XmlElement(name = "NbOfTxs")
    private Integer nbOfTxs;

    @XmlElement(name = "CtrlSum")
    private double ctrlSum;

    @XmlElement(name = "PmtTpInf")
    private PaymentTypeInfo pmtTpInf;

    @XmlElement(name = "ReqdColltnDt")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate reqdColltnDt;

    @Embedded
    @XmlElement(name = "Cdtr")
    @AttributeOverrides({
        @AttributeOverride(name = "nm", column = @Column(name = "cdtr_nm"))
    })
    private Party cdtr;

    @Embedded
    @XmlElement(name = "CdtrAcct")
    @AttributeOverrides({
        @AttributeOverride(name = "id.iban", column = @Column(name = "cdtracct_iban")),
        @AttributeOverride(name = "id.prvtId.othr.id", column = @Column(name = "cdtracct_prvtid")),
        @AttributeOverride(name = "id.prvtId.othr.schemeName.prtry", column = @Column(name = "cdtracct_prtry"))
    })
    private Account cdtrAcct;

    @Embedded
    @XmlElement(name = "CdtrAgt")
    @AttributeOverrides({
        @AttributeOverride(name = "finInstnId.bic", column = @Column(name = "cdtragt_bic")),
        @AttributeOverride(name = "finInstnId.othr.id", column = @Column(name = "cdtragt_id")),
        @AttributeOverride(name = "finInstnId.othr.schemeName.prtry", column = @Column(name = "cdtragt_prtry"))
    })
    private Agent cdtrAgt;

    @Embedded
    @XmlElement(name = "CdtrSchmeId")
    @AttributeOverrides({
        @AttributeOverride(name = "id.iban", column = @Column(name = "cdtrschmeid_iban")),
        @AttributeOverride(name = "id.prvtId.othr.id", column = @Column(name = "cdtrschmeid_prvtid")),
        @AttributeOverride(name = "id.prvtId.othr.schemeName.prtry", column = @Column(name = "cdtrschmeid_prtry"))
    })
    private AccountSchemeId cdtrSchmeId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JacksonXmlProperty(localName = "DrctDbtTxInf")
    @JoinColumn(name = "pmtinf_id", referencedColumnName = "pmtinf_id")
    @XmlElement(name = "DrctDbtTxInf")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<DrctDbtTxInf> drctDbtTxInfs = new ArrayList<>();

    public Long getId() {
        return idPmtInf;
    }

    public void setId(Long idPmtInf) {
        this.idPmtInf = idPmtInf;
    }

    public String getPmtInfId() {
        return pmtInfId;
    }

    public void setPmtInfId(String pmtInfId) {
        this.pmtInfId = pmtInfId;
    }

    public Integer getNbOfTxs() {
        return nbOfTxs;
    }

    public void setNbOfTxs(Integer nbOfTxs) {
        this.nbOfTxs = nbOfTxs;
    }

    public Double getCtrlSum() {
        return ctrlSum;
    }

    public void setCtrlSum(Double ctrlSum) {
        this.ctrlSum = ctrlSum;
    }

    public PaymentTypeInfo getPmtTpInf() {
        return pmtTpInf;
    }

    public void setPmtTpInf(PaymentTypeInfo pmtTpInf) {
        this.pmtTpInf = pmtTpInf;
    }

    public LocalDate getReqdColltnDt() {
        return reqdColltnDt;
    }

    public void setReqdColltnDt(LocalDate reqdColltnDt) {
        this.reqdColltnDt = reqdColltnDt;
    }

    public Party getCdtr() {
        return cdtr;
    }

    public void setCdtr(Party cdtr) {
        this.cdtr = cdtr;
    }

    public Account getCdtrAcct() {
        return cdtrAcct;
    }

    public void setCdtrAcct(Account cdtrAcct) {
        this.cdtrAcct = cdtrAcct;
    }

    public Agent getCdtrAgt() {
        return cdtrAgt;
    }

    public void setCdtrAgt(Agent cdtrAgt) {
        this.cdtrAgt = cdtrAgt;
    }

    public AccountSchemeId getCdtrSchmeId() {
        return cdtrSchmeId;
    }

    public void setCdtrSchmeId(AccountSchemeId cdtrSchmeId) {
        this.cdtrSchmeId = cdtrSchmeId;
    }

    public List<DrctDbtTxInf> getDrctDbtTxInfs() {
        return drctDbtTxInfs;
    }

    public void setDrctDbtTxInfs(List<DrctDbtTxInf> drctDbtTxInfs) {
        this.drctDbtTxInfs = drctDbtTxInfs;
    }
}
