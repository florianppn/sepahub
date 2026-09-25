package fr.univrouen.sepa26.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentTypeInfo {

    @XmlElement(name = "SvcLvl")
    @AttributeOverrides({
            @AttributeOverride(name = "cd", column = @Column(name = "svc_lvl_cd"))
    })
    @Embedded
    private ServiceLevel svcLvl;

    @XmlElement(name = "LclInstrm")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cd", column = @Column(name = "lcl_instrm_cd"))
    })
    private LocalInstrument lclInstrm;

    @XmlElement(name = "SeqTp")
    private String seqTp;

    public ServiceLevel getSvcLvl() {
        return svcLvl;
    }

    public void setSvcLvl(ServiceLevel svcLvl) {
        this.svcLvl = svcLvl;
    }

    public LocalInstrument getLclInstrm() {
        return lclInstrm;
    }

    public void setLclInstrm(LocalInstrument lclInstrm) {
        this.lclInstrm = lclInstrm;
    }

    public String getSeqTp() {
        return seqTp;
    }

    public void setSeqTp(String seqTp) {
        this.seqTp = seqTp;
    }
}
