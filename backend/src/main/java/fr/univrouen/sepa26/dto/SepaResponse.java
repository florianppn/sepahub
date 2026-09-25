package fr.univrouen.sepa26.dto;

import jakarta.xml.bind.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.*;

/**
 * Objet de transfert de données (DTO) pour les réponses standard de l'API.
 * Utilisé pour informer l'utilisateur du succès ou de l'échec d'une opération.
 */
@XmlRootElement(name = "SepaResponse")
@JacksonXmlRootElement(localName = "SepaResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "status", "description"})
public class SepaResponse {

    /** Identifiant de la ressource concernée (optionnel) */
    @XmlElement
    @JacksonXmlProperty(localName = "id")
    private Long id;

    /** Statut de l'opération (ex: INSERTED, DELETED, ERROR) */
    @XmlElement
    @JacksonXmlProperty(localName = "status")
    private String status;

    /** Description détaillée en cas d'erreur (optionnel) */
    @XmlElement
    @JacksonXmlProperty(localName = "description")
    private String description;

    public SepaResponse() {}

    public SepaResponse(Long id, String status) {
        this.id = id;
        this.status = status;
    }

    public SepaResponse(String status) {
        this.status = status;
    }

    public SepaResponse(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}