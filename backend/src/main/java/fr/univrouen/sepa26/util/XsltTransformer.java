package fr.univrouen.sepa26.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class XsltTransformer {
	
	private final TransformerFactory transformerFactory;
	
	public XsltTransformer() {
		this.transformerFactory = new net.sf.saxon.TransformerFactoryImpl();
	}
	
	/**
	 * Transforme un XML en HTML en utilisant un fichier XSLT correspondant.
	 * @param xml
	 * @param xsltPath
	 * @return le HTML
	 * @throws TransformerException
	 * @throws IOException
	 */
	public String transform(String xml, String xsltPath) throws TransformerException, IOException {
		StreamSource xsltSource = new StreamSource(new ClassPathResource(xsltPath).getInputStream());
		Transformer transformer = transformerFactory.newTransformer(xsltSource);
		StringReader reader = new StringReader(xml);
		StringWriter writer = new StringWriter();
		transformer.transform(new StreamSource(reader), new StreamResult(writer));
		return writer.toString();
	}
	
	
	public String transformSepa(String xml) throws TransformerException, IOException {
		return transform(xml, "xml/sepa26.xslt");
	}
}
