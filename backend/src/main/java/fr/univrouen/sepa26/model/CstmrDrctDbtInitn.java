package fr.univrouen.sepa26.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "cstmr_direct_debit_initiation")
@XmlAccessorType(XmlAccessType.FIELD)
public class CstmrDrctDbtInitn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cstmr_direct_debit_initiation_id")
    @XmlTransient
    private Long idCstmr;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "grp_hdr_id", referencedColumnName = "grp_hdr_id")
    @XmlElement(name = "GrpHdr")
    private GrpHdr grpHdr;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JacksonXmlProperty(localName = "PmtInf")
    @JoinColumn(name = "cstmr_direct_debit_initiation_id", referencedColumnName = "cstmr_direct_debit_initiation_id")
    @XmlElement(name = "PmtInf")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PmtInf> pmtInfs = new ArrayList<>();

    public Long getId() {
        return idCstmr;
    }

    public void setId(Long idCstmr) {
        this.idCstmr = idCstmr;
    }

    public GrpHdr getGrpHdr() {
        return grpHdr;
    }

    public void setGrpHdr(GrpHdr grpHdr) {
        this.grpHdr = grpHdr;
    }

    public List<PmtInf> getPmtInfs() {
        return pmtInfs;
    }

    public void setPmtInfs(List<PmtInf> pmtInfs) {
        this.pmtInfs = pmtInfs;
    }
}
