package fr.univrouen.sepa26.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.univrouen.sepa26.dto.DocumentList;
import fr.univrouen.sepa26.dto.SepaResponse;
import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.services.ISepaService;
import fr.univrouen.sepa26.util.XsltTransformer;

/**
 * Contrôleur REST gérant les requêtes de consultation (GET).
 * Fournit des données au format XML (JAXB) ou HTML transformé via XSLT (Saxon-HE).
 * Utilise l'injection par constructeur et l'interface ISepaService (DIP, SRP, DRY).
 *
 * @author Florian Pépin
 * @version 2.0
 */
@RestController
@RequestMapping("/sepa26")
public class GetController {

    private final ISepaService sepaService;
    private final XsltTransformer xsltTransformer;

    @Autowired
    public GetController(ISepaService sepaService, XsltTransformer xsltTransformer) {
        this.sepaService = sepaService;
        this.xsltTransformer = xsltTransformer;
    }

    /**
     * Retourne la liste des 10 derniers documents au format XML.
     * @return Un objet DocumentList sérialisé en XML.
     */
    @GetMapping(value = "/resume/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public DocumentList getResumeXml() {
        return new DocumentList(sepaService.getLast10());
    }

    /**
     * Retourne le détail d'un document au format XML.
     * @param id L'identifiant technique du document.
     * @return Le document en XML ou une réponse d'erreur.
     */
    @GetMapping(value = "/xml/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getXmlDetail(@PathVariable long id) {
        Optional<Document> doc = sepaService.getById(id);
        if (doc.isPresent()) {
            return ResponseEntity.ok(doc.get());
        }
        return ResponseEntity.ok(new SepaResponse(id, "ERROR"));
    }

    /**
     * Récupère le détail d'un document en HTML via transformation XSLT.
     * Réutilise le service centralisé pour la sérialisation XML (DRY).
     *
     * @param id Identifiant du document à récupérer.
     * @return Chaîne HTML contenant le détail, ou flux d'erreur.
     */
    @GetMapping(value = "/html/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String getHtmlDetail(@PathVariable long id) {
        Optional<Document> doc = sepaService.getById(id);
        if (doc.isPresent()) {
            try {
                String xml = sepaService.convertToXml(doc.get());
                return xsltTransformer.transformSepa(xml);
            } catch (Exception e) {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<error>\n"
                        + "  <status>id</status>\n"
                        + "  <message>" + e.getMessage() + "</message>\n"
                        + "</error>";
            }
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<error>\n"
                + "  <status>id</status>\n"
                + "  <message>DOC NOT FOUND</message>\n"
                + "</error>";
    }
}