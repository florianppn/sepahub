package fr.univrouen.sepa26.model;

import jakarta.persistence.Embeddable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class InstdAmt {

    @XmlValue
    private Double value;

    @XmlAttribute(name = "Ccy")
    private String ccy;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }
}
