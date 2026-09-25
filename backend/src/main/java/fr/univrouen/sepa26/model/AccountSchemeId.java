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
public class AccountSchemeId {

    @XmlElement(name = "Id")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "iban", column = @Column(name = "cdtrschmeid_iban")),
            @AttributeOverride(name = "prvtId.othr.id", column = @Column(name = "cdtrschmeid_prvtid")),
            @AttributeOverride(name = "prvtId.othr.schemeName.prtry", column = @Column(name = "cdtrschmeid_prtry"))
    })
    private AccountId id;

    public AccountId getId() {
        return id;
    }

    public void setId(AccountId id) {
        this.id = id;
    }
}
