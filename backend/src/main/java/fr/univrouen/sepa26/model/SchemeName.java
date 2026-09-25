package fr.univrouen.sepa26.model;

import jakarta.persistence.Embeddable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class SchemeName {

    @XmlElement(name = "Prtry")
    private String prtry;

    public String getPrtry() {
        return prtry;
    }

    public void setPrtry(String prtry) {
        this.prtry = prtry;
    }
}
