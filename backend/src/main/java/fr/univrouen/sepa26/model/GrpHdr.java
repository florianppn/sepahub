package fr.univrouen.sepa26.model;

import java.time.LocalDateTime;

import fr.univrouen.sepa26.util.LocalDateTimeAdapter;
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
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "grp_hdr")
@XmlAccessorType(XmlAccessType.FIELD)
public class GrpHdr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grp_hdr_id")
    @XmlTransient
    private Long idGrp;

    @XmlElement(name = "MsgId")
    private String msgId;

    @XmlElement(name = "CreDtTm")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime creDtTm;

    @XmlElement(name = "NbOfTxs")
    private int nbOfTxs;

    @XmlElement(name = "CtrlSum")
    private double ctrlSum;

    @Embedded
    @XmlElement(name = "InitgPty")
    @AttributeOverrides({
        @AttributeOverride(name = "nm", column = @Column(name = "initgpty_nm"))
    })
    private Party initgPty;

    public Long getId() {
        return idGrp;
    }

    public void setId(Long idGrp) {
        this.idGrp = idGrp;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public LocalDateTime getCreDtTm() {
        return creDtTm;
    }

    public void setCreDtTm(LocalDateTime creDtTm) {
        this.creDtTm = creDtTm;
    }

    public int getNbOfTxs() {
        return nbOfTxs;
    }

    public void setNbOfTxs(int nbOfTxs) {
        this.nbOfTxs = nbOfTxs;
    }

    public double getCtrlSum() {
        return ctrlSum;
    }

    public void setCtrlSum(double ctrlSum) {
        this.ctrlSum = ctrlSum;
    }

    public Party getInitgPty() {
        return initgPty;
    }

    public void setInitgPty(Party initgPty) {
        this.initgPty = initgPty;
    }
}
