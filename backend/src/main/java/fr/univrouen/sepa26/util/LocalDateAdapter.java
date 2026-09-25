package fr.univrouen.sepa26.util;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String v) {
        return (v == null) ? null : LocalDate.parse(v);
    }

    @Override
    public String marshal(LocalDate v) {
        return (v == null) ? null : v.toString();
    }
}