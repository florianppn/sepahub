package fr.univrouen.sepa26.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SearchResults")
@JacksonXmlRootElement(localName = "SearchResults")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResponse {
	@XmlElement
	@JacksonXmlProperty(localName = "status")
	private String status;
	
	@XmlElement(name = "DocumentList")
	@JacksonXmlProperty(localName = "DocumentList")
	private DocumentList documentList;
	
	public SearchResponse() {
		
	}
	
	public SearchResponse(String status) {
		this.status = status;
	}
	
	public SearchResponse(String status, DocumentList documentList) {
		this.status = status;
		this.documentList = documentList;
	}
	
	public String getStatus() {
		return status;
	}
	
    public void setStatus(String status) {
    	this.status = status;
    }

    public DocumentList getDocumentList() {
    	return documentList;
    }
    
    public void setDocumentList(DocumentList documentList) {
    	this.documentList = documentList;
    }
}
