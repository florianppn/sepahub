package fr.univrouen.sepa26.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountId {

    @XmlElement(name = "IBAN")
    private String iban;

    @XmlElement(name = "PrvtId")
    @Embedded
    private PrivateId prvtId;

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public PrivateId getPrvtId() {
        return prvtId;
    }

    public void setPrvtId(PrivateId prvtId) {
        this.prvtId = prvtId;
    }
}
