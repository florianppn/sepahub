package fr.univrouen.sepa26.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class DrctDbtTx {

    @XmlElement(name = "MndtRltdInf")
    @Embedded
    private MndtRltdInf mndtRltdInf;

    public MndtRltdInf getMndtRltdInf() {
        return mndtRltdInf;
    }

    public void setMndtRltdInf(MndtRltdInf mndtRltdInf) {
        this.mndtRltdInf = mndtRltdInf;
    }
}
