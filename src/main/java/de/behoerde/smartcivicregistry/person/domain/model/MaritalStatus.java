package de.behoerde.smartcivicregistry.person.domain.model;

/**
 * Enumeration für die Familienstandsangabe von Personen im Registrierungssystem.

 * Diese Enumeration entspricht den deutschen behördlichen Standards für
 * Familienstandsangaben gemäß Personenstandsgesetz (PStG) und den Vorgaben
 * der Meldebehörden. Die Werte decken alle relevanten Familienstandsarten
 * ab, die in deutschen Verwaltungsverfahren verwendet werden.

 * Verwendung: Diese Enumeration wird in Verwaltungssystemen
 * zur Erfassung und Verwaltung von Personenstandsdaten verwendet. Die
 * Auswahl des zutreffenden Werts erfolgt entsprechend den tatsächlichen
 * familienrechtlichen Verhältnissen der betroffenen Person.

 * Hinweis: Änderungen des Familienstands (z.B. Heirat, Scheidung)
 * erfordern eine entsprechende Dokumentation und ggf. eine Aktualisierung
 * weiterer Personenstandsdaten.
 *
 * @author MiniMax Agent
 * @version 1.0
 * @since 2025-01
 */
public enum MaritalStatus {

    /**
     * Ledig.

     * Personen, die noch nie eine Ehe oder eingetragene Lebenspartnerschaft
     * eingegangen waren. Dieser Status schließt verwitwete oder geschiedene
     * Personen nicht ein, da diese einen anderen Familienstand haben.
     */
    SINGLE("Ledig"),

    /**
     * Verheiratet.

     * Personen, die derzeit in einer gültigen Ehe leben. Dieser Status
     * umfasst sowohl Ehegemeinschaften als auch Fälle, in denen die
     * Ehegatten räumlich getrennt leben, sofern die Ehe nicht aufgelöst wurde.
     */
    MARRIED("Verheiratet"),

    /**
     * Geschieden.

     * Personen, deren Ehe durch rechtskräftiges Urteil oder Beschluss
     * aufgelöst wurde. Der Status "Geschieden" bleibt auch nach der Scheidung
     * bestehen, bis eine erneute Ehe geschlossen wird.
     */
    DIVORCED("geschieden"),

    /**
     * Verwitwet.

     * Personen, deren Ehegatte verstorben ist und die nicht wieder geheiratet
     * haben. Dieser Status bleibt dauerhaft bestehen, sofern keine erneute
     * Ehe oder Lebenspartnerschaft eingegangen wird.
     */
    WIDOWED("Verwitwet"),

    /**
     * Eingetragene Lebenspartnerschaft.

     * Personen, die in einer eingetragenen Lebenspartnerschaft nach dem
     * Lebenspartnerschaftsgesetz (LPartG) leben. Dieses umfasst sowohl
     * Begründung als auch Fortbestand der Lebenspartnerschaft.

     * <b>Hinweis: Seit der Einführung der Ehe für gleichgeschlechtliche
     * Paare (2017) können keine neuen Lebenspartnerschaften mehr begründet
     * werden. Bestehende Lebenspartnerschaften können in Ehen umgewandelt werden.
     */
    REGISTERED_PARTNERSHIP("Eingetragene Lebenspartnerschaft");

    /**
     * Der anzeigegerechte Name des Familienstands.
     */
    private final String displayName;

    /**
     * Konstruktor für die Initialisierung des Anzeigenamens.
     *
     * @param displayName der anzeigegerechte Name, der in Benutzeroberflächen
     *                    und behördlichen Dokumenten verwendet wird
     */
    MaritalStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gibt den lokalisierten Anzeigenamen des Familienstands zurück.

     * Diese Methode liefert eine menschenlesbare Darstellung des enum-Werts,
     * die direkt in Benutzeroberflächen, Formularen oder behördlichen
     * Dokumenten verwendet werden kann.

     * @return der Anzeigename des Familienstands als {@link String}
     */
    public String getDisplayName() {
        return displayName;
    }
}