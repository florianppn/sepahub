package fr.univrouen.sepa26.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.univrouen.sepa26.model.Document;
import fr.univrouen.sepa26.model.DrctDbtTxInf;
import fr.univrouen.sepa26.model.PmtInf;
import fr.univrouen.sepa26.repository.DocumentRepository;

/**
 * Service métier pour la gestion des documents SEPA.
 * Concentre la logique métier et la persistance en déléguant la manipulation XML
 * au SepaXmlService (respect des principes SOLID : SRP et DIP).
 */
@Service
public class SepaService implements ISepaService {

    private final DocumentRepository repository;
    private final SepaXmlService xmlService;

    private static final SepaXmlService STATIC_XML_SERVICE = new SepaXmlService();

    /**
     * Classe interne pour encapsuler le résultat de validation XSD.
     * Étend SepaXmlService.ValidationResult pour préserver la rétrocompatibilité.
     */
    public static class ValidationResult extends SepaXmlService.ValidationResult {
        public ValidationResult(boolean valid, String errorMessage) {
            super(valid, errorMessage);
        }

        public ValidationResult(boolean valid) {
            super(valid, null);
        }
    }

    /**
     * Classe interne pour le résultat de parsing XML.
     * Étend SepaXmlService.ParseResult pour préserver la rétrocompatibilité.
     */
    public static class ParseResult extends SepaXmlService.ParseResult {
        public ParseResult(Document document) {
            super(document);
        }

        public ParseResult(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * Injection par constructeur (recommandation Spring, DIP et immutabilité).
     */
    @Autowired
    public SepaService(DocumentRepository repository, SepaXmlService xmlService) {
        this.repository = repository;
        this.xmlService = (xmlService != null) ? xmlService : new SepaXmlService();
    }

    /**
     * Constructeur pour les tests unitaires isolés mockant uniquement le repository.
     */
    public SepaService(DocumentRepository repository) {
        this(repository, new SepaXmlService());
    }

    /**
     * Sauvegarde un document en base de données.
     * Réalise une vérification de l'unicité du PmtId (contrainte métier SEPA).
     * @param doc Le document à enregistrer.
     * @return Le document sauvegardé avec son ID généré, ou null si un doublon de PmtId est détecté.
     */
    @Override
    public Document save(Document doc) {
        try {
            if (doc == null || doc.getCstmrDrctDbtInitn() == null) {
                return null;
            }
            List<PmtInf> pmtInfs = doc.getCstmrDrctDbtInitn().getPmtInfs();
            if (pmtInfs != null) {
                for (PmtInf pmt : pmtInfs) {
                    if (pmt.getDrctDbtTxInfs() == null) continue;
                    for (DrctDbtTxInf tx : pmt.getDrctDbtTxInfs()) {
                        String pmtId = tx.getPmtId();
                        if (pmtId != null && exists(pmtId)) {
                            return null;
                        }
                    }
                }
            }
            return repository.save(doc);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Vérifie si un identifiant de paiement existe déjà en base.
     * @param pmtId L'identifiant à vérifier.
     * @return true s'il existe déjà, false sinon.
     */
    @Override
    public boolean exists(String pmtId) {
        return repository.findByPmtId(pmtId).isPresent();
    }

    /**
     * Récupère un document par son identifiant technique.
     * @param id L'ID du document.
     * @return Un Optional contenant le document.
     */
    @Override
    public Optional<Document> getById(long id) {
        return repository.findById(id);
    }

    /**
     * Récupère la liste des 10 derniers documents.
     * @return Liste de documents.
     */
    @Override
    public List<Document> getLast10() {
        return repository.findLast10();
    }

    /**
     * Supprime un document par son ID.
     * @param id L'ID du document à supprimer.
     * @return true si supprimé, false si le document n'existe pas.
     */
    @Override
    public boolean delete(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Transforme un objet Document en chaîne XML formatée via le service XML dédié.
     * @param doc Le document à transformer.
     * @return La chaîne XML.
     */
    @Override
    public String convertToXml(Document doc) {
        return xmlService.convertToXml(doc);
    }

    /**
     * Valide un flux XML brut en le comparant au schéma XSD.
     * @param xmlContent contenu XML.
     * @return true si le XML est conforme, false sinon.
     */
    @Override
    public boolean validateXSDRaw(String xmlContent) {
        return validateXSDRawWithDetails(xmlContent).isValid();
    }

    /**
     * Valide un flux XML brut en retournant un résultat détaillé.
     * @param xmlContent contenu XML.
     * @return ValidationResult avec le statut et le message d'erreur.
     */
    @Override
    public ValidationResult validateXSDRawWithDetails(String xmlContent) {
        SepaXmlService.ValidationResult res = xmlService.validateXsd(xmlContent);
        return new ValidationResult(res.isValid(), res.getErrorMessage());
    }

    /**
     * Désérialise un flux XML brut en un objet Document.
     * @param xmlContent contenu XML représentant un Document.
     * @return l'objet Document désérialisé, ou null en cas d'échec.
     */
    public static Document parseXml(String xmlContent) {
        return parseXmlWithDetails(xmlContent).document;
    }

    /**
     * Désérialise un flux XML brut avec capture du message d'erreur.
     * @param xmlContent contenu XML représentant un Document.
     * @return ParseResult avec le document ou le message d'erreur.
     */
    public static ParseResult parseXmlWithDetails(String xmlContent) {
        SepaXmlService.ParseResult res = STATIC_XML_SERVICE.parseXml(xmlContent);
        return res.isSuccess() ? new ParseResult(res.getDocument()) : new ParseResult(res.getErrorMessage());
    }

    /**
     * Effectue la recherche des documents dans la base selon une date et/ou un montant.
     * @param date date minimale pour la balise creDtTm, ou null si pas utilisé.
     * @param sum montant minimal pour la balise ctrlSum, ou null si pas utilisé.
     * @return Liste de documents correspondants.
     */
    @Override
    public List<Document> search(LocalDateTime date, Double sum) {
        return repository.search(date, sum);
    }
}