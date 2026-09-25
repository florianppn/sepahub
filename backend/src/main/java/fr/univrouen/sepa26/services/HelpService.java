package fr.univrouen.sepa26.services;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.univrouen.sepa26.dto.EndpointDoc;

/**
 * Implémentation du service de documentation {@link IHelpService}.
 * Fournit la liste immuable et typée des endpoints exposés par SEPAHub.
 *
 * @author Florian Pépin
 * @version 2.0
 */
@Service
public class HelpService implements IHelpService {

    private static final List<EndpointDoc> ENDPOINTS = List.of(
            new EndpointDoc(
                    "/",
                    "GET",
                    "JSON",
                    "Métadonnées de l'application",
                    "Retourne les informations d'identification de l'application et son état de santé :\n"
                            + "• Nom du projet\n"
                            + "• Numéro de version\n"
                            + "• Développeur\n"
                            + "• Statut de santé (UP)\n"
                            + "• Lien vers la documentation"
            ),
            new EndpointDoc(
                    "/help",
                    "GET",
                    "JSON",
                    "Documentation de l'API REST",
                    "Catalogue exhaustif des opérations exposées par le service REST.\n"
                            + "Fournit pour chaque route : URL, méthode HTTP, format attendu/retourné, résumé et description détaillée."
            ),
            new EndpointDoc(
                    "/sepa26/resume/xml",
                    "GET",
                    "XML",
                    "Liste des transactions stockées",
                    "Liste simplifiée des 10 dernières transactions présentes dans la base.\n"
                            + "Pour chaque transaction, ne seront affichées que les informations suivantes :\n"
                            + "• id\n"
                            + "• date\n"
                            + "• montant"
            ),
            new EndpointDoc(
                    "/sepa26/xml/{id}",
                    "GET",
                    "XML",
                    "Affiche le contenu complet du document SEPA dont l'identifiant est {id}",
                    "Intégralité du document SEPA dont l'identifiant est fourni par son {id}.\n"
                            + "Flux XML conforme au schéma XSD ISO 20022.\n"
                            + "Si l'identifiant est incorrect, retour d'un message d'erreur au format XML contenant :\n"
                            + "• id : numéro de l'identifiant demandé\n"
                            + "• status : ERROR"
            ),
            new EndpointDoc(
                    "/sepa26/html/{id}",
                    "GET",
                    "HTML",
                    "Affiche le contenu complet du document SEPA transformé en HTML",
                    "Intégralité du document SEPA dont l'identifiant est fourni par son {id}, transformé en HTML via XSLT (Saxon-HE).\n"
                            + "Si l'identifiant est incorrect, retour d'un message d'erreur au format XML contenant :\n"
                            + "• id : numéro de l'identifiant demandé\n"
                            + "• status : ERROR"
            ),
            new EndpointDoc(
                    "/sepa26/insert",
                    "POST",
                    "XML",
                    "Ajoute un document SEPA en base",
                    "Flux XML décrivant un document SEPA à ajouter, conforme au schéma XSD.\n"
                            + "Le flux reçu est validé par la chaîne de validation (XSD, syntaxe XML, unicité pmtId, somme de contrôle).\n"
                            + "Paramètre optionnel : validate=true|false (activation de la validation stricte XSD).\n"
                            + "Si le flux est déjà présent (même pmtId) ou invalide, une indication d'erreur est retournée.\n"
                            + "Si l'opération est réussie, le flux est persisté et le retour contient :\n"
                            + "• id : identifiant unique généré en base\n"
                            + "• status : INSERTED\n"
                            + "En cas d'échec de l'opération :\n"
                            + "• status : ERROR"
            ),
            new EndpointDoc(
                    "/sepa26/delete/{id}",
                    "DELETE",
                    "XML",
                    "Supprime le document SEPA dont l'identifiant est {id}",
                    "Suppression du document en base.\n"
                            + "Si l'opération réussit, retour des informations suivantes :\n"
                            + "• id : numéro d'identifiant du document retiré\n"
                            + "• status : DELETED\n"
                            + "En cas d'échec de l'opération :\n"
                            + "• status : ERROR"
            ),
            new EndpointDoc(
                    "/sepa26/search",
                    "GET",
                    "XML",
                    "Recherche multicritère de documents SEPA",
                    "Recherche la liste des documents répondant aux contraintes exprimées dans la requête :\n"
                            + "• date : liste des documents dont la date CreDtTm est postérieure ou égale à celle indiquée\n"
                            + "• sum : liste des documents dont le montant ctrlSum est supérieur ou égal à celui indiqué\n"
                            + "Si l'opération réussit : flux XML de résultats (status OK)\n"
                            + "Si l'opération réussit mais sans résultat : status NONE\n"
                            + "Si la requête est invalide : status ERROR"
            )
    );

    @Override
    public List<EndpointDoc> getEndpoints() {
        return ENDPOINTS;
    }
}
