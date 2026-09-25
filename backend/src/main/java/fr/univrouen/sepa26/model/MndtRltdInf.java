package fr.univrouen.sepa26.model;

import fr.univrouen.sepa26.util.LocalDateAdapter;
import jakarta.persistence.Embeddable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class MndtRltdInf {

    @XmlElement(name = "MndtId")
    private String mndtId;

    @XmlElement(name = "DtOfSgntr")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate dtOfSgntr;

    public String getMndtId() {
        return mndtId;
    }

    public void setMndtId(String mndtId) {
        this.mndtId = mndtId;
    }

    public LocalDate getDtOfSgntr() {
        return dtOfSgntr;
    }

    public void setDtOfSgntr(LocalDate dtOfSgntr) {
        this.dtOfSgntr = dtOfSgntr;
    }
}
