package de.behoerde.smartcivicregistry.person.domain.model;

/**
 * Enumeration für die Geschlechtsangabe von Personen im Registrierungssystem.

 * Diese Enumeration unterstützt DSGVO-konforme Geschlechtsangaben gemäß
 * der指示 (Empfehlung) des IT-Planungsrates und internationaler Standards.
 * Die Werte entsprechen den Anforderungen an eine inklusive und rechtskonforme
 * Personenverwaltung.

 * Verwendung: Diese Enumeration wird primär in Kontexten eingesetzt,
 * in denen eine differenzierte Geschlechtsangabe erforderlich oder zulässig ist.
 * Der Wert {@link #UNKNOWN} sollte verwendet werden, wenn die Angabe nicht
 * bekannt ist oder bewusst nicht angegeben wird.

 * Hinweis zur DSGVO: Die Speicherung und Verarbeitung dieser Daten
 * unterliegt den Datenschutzanforderungen der DSGVO. Eine Erhebung ist nur
 * zulässig, wenn eine Rechtsgrundlage oder Einwilligung vorliegt.
 *
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-01
 */
public enum Gender {

    /**
     * Männliches Geschlecht.

     * Entspricht der traditionellen Geschlechtsangabe für männliche Personen.
     */
    MALE("Männlich"),

    /**
     * Weibliches Geschlecht.

     * Entspricht der traditionellen Geschlechtsangabe für weibliche Personen.
     */
    FEMALE("Weiblich"),

    /**
     * Geschlechtseintrag "Divers".

     * Entspricht dem dritten Geschlechtseintrag gemäß § 22 Abs. 3 PStG
     * (Personenstandsgesetz) für Personen, die sich dauerhaft weder dem
     * männlichen noch dem weiblichen Geschlecht zuordnen.
     */
    DIVERSE("Divers"),

    /**
     * Unbekannt oder nicht angegeben.

     * Verwendet, wenn das Geschlecht nicht bekannt ist oder bewusst nicht
     * angegeben werden soll. Dies dient der Datenqualität und vermeidet
     * ungenaue oder erzwungene Angaben.
     */
    UNKNOWN("Unbekannt");

    /**
     * Der anzeigegerechte Name des Geschlechts.
     */
    private final String displayName;

    /**
     * Konstruktor für die Initialisierung des Anzeigenamens.
     *
     * @param displayName der anzeigegerechte Name, der in Benutzeroberflächen
     * verwendet wird
     */
    Gender(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gibt den lokalisierten Anzeigenamen des Geschlechts zurück.

     * Diese Methode liefert eine menschenlesbare Darstellung des enum-Werts,
     * die direkt in Benutzeroberflächen oder Dokumenten verwendet werden kann.

     * @return der Anzeigename des Geschlechts als {@link String}
     */
    public String getDisplayName() {
        return displayName;
    }
}