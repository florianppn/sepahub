package fr.univrouen.sepa26.builder;

import java.time.LocalDate;

import fr.univrouen.sepa26.model.Account;
import fr.univrouen.sepa26.model.AccountSchemeId;
import fr.univrouen.sepa26.model.Agent;
import fr.univrouen.sepa26.model.InstdAmt;
import fr.univrouen.sepa26.model.MndtRltdInf;
import fr.univrouen.sepa26.model.Party;
import fr.univrouen.sepa26.model.PaymentTypeInfo;

/**
 * Interface formelle du patron de conception GoF "Abstract Factory" (Fabrique Abstraite).
 * Définit la famille de méthodes pour instancier les sous-arborescences XML/JAXB
 * requises par la norme SEPA ISO 20022 (pain.008.001.02).
 *
 * @author Florian Pépin
 * @version 1.0
 */
public interface ISepaElementFactory {

    /**
     * Crée une entité Party (initiateur, créancier ou débiteur).
     * @param name Nom de l'entité.
     * @return L'objet Party instancié.
     */
    Party createParty(String name);

    /**
     * Crée un compte bancaire Account encapsulant un AccountId avec son IBAN.
     * @param iban Code IBAN du compte.
     * @return L'objet Account instancié.
     */
    Account createAccount(String iban);

    /**
     * Crée un agent bancaire Agent encapsulant un FinInstnId avec son BIC.
     * @param bic Code BIC de l'établissement.
     * @return L'objet Agent instancié.
     */
    Agent createAgent(String bic);

    /**
     * Crée l'arborescence complète de l'identifiant créancier SEPA (ICS / AccountSchemeId).
     * @param schemeId Identifiant créancier SEPA.
     * @return L'objet AccountSchemeId instancié.
     */
    AccountSchemeId createCreditorScheme(String schemeId);

    /**
     * Crée le bloc de spécifications SEPA par défaut (SvcLvl=SEPA, LclInstrm=SEPA, SeqTp=RCUR).
     * @return L'objet PaymentTypeInfo instancié.
     */
    PaymentTypeInfo createDefaultPaymentTypeInfo();

    /**
     * Crée un montant monétaire InstdAmt libellé en euros (EUR).
     * @param amount Valeur numérique du montant.
     * @return L'objet InstdAmt instancié.
     */
    InstdAmt createEuroAmount(double amount);

    /**
     * Crée le bloc de mandat SEPA avec sa référence unique (RUM) et sa date de signature.
     * @param mandateId Référence unique du mandat.
     * @param signatureDate Date de signature du mandat.
     * @return L'objet MndtRltdInf instancié.
     */
    MndtRltdInf createMandateInfo(String mandateId, LocalDate signatureDate);
}
