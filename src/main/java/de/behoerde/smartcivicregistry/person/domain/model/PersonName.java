package de.behoerde.smartcivicregistry.person.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Value Object fuer die Verwaltung von Personennamen im Smart Civic Registry System.
 *
 * Diese Embeddable-Klasse kapselt alle namensrelevanten Informationen einer Person
 * und stellt Methoden zur Formatierung und Darstellung von Namen bereit. Als Value Object
 * wird sie direkt in die Person-Entity eingebettet, ohne eine eigene Datenbanktabelle zu erstellen.
 * Dies entspricht dem Domain-Driven Design (DDD) Paradigma, bei dem Namen als unveränderliche
 * Wertobjekte ohne eigene Identität behandelt werden.
 *
 * Value Object Charakteristiken:
 * - Unveraenderlichkeit: Nach der Erstellung nicht mehr aenderbar
 * - Wertbasierte Gleichheit: Gleichheit basiert auf Attributwerten, nicht auf Identitaet
 * - Keine eigene ID: Kein eigener Lebenszyklus, eingebettet in Parent-Entity
 * - Selbstvalidierend: Implementiert Geschaeftslogik fuer Namensformatierung
 *
 * Verwendungsbeispiel:
 * // Einbettung in Person-Entity
 * @Embedded
 * private PersonName name;
 *
 * // Erstellung mittels Builder
 * PersonName name = PersonName.builder()
 *     .title("Dr.")
 *     .firstName("Max")
 *     .middleName("Johann")
 *     .lastName("Mustermann")
 *     .maidenName("Schmidt")
 *     .build();
 *
 * // Namensformate abrufen
 * String fullName = name.getFullName();      // "Dr. Max Johann Mustermann"
 * String official = name.getOfficialName();  // "Mustermann, Max"
 *
 * Namenskonventionen und Validierung:
 * - Alle Namensteile werden bei der Ausgabe korrekt mit Leerzeichen formatiert
 * - Leere oder null-Werte fuer optionale Felder werden automatisch ignoriert
 * - Der maidenName speichert den Geburtsnamen fuer historische Nachvollziehbarkeit
 * - Die Methoden getFullName() und getOfficialName() geben niemals null zurueck
 *
 * @author Smart Civic Registry Team
 * @version 1.1
 * @since Phase 2 (Person Domain Module)
 * @see Person
 * @see jakarta.persistence.Embeddable
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonName {

    /**
     * Akademischer oder Adelstitel der Person.
     *
     * Enthaelt akademische Grade, Adelstitel und Anredeformen, die vor dem Namen gefuehrt werden.
     *
     * Uebliche Werte: "Dr.", "Prof.", "Dr. med.", "Prof. Dr.", "von", "zu"
     *
     * Technische Details:
     * - Optionales Feld, maximal 50 Zeichen
     * - Wird in getFullName() mit einem Leerzeichen vor dem Vornamen eingefuegt
     * - Wird in getOfficialName() bewusst weggelassen
     *
     * @since 1.0
     * @see #getFullName()
     */
    @Column(name = "title", length = 50)
    private String title;

    /**
     * Vorname der Person.
     *
     * Der primaere Rufname (Rufnamen), unter dem die Person im taeglichen Gebrauch bekannt ist.
     * Bei mehreren Vornamen wird der erste eingetragen.
     *
     * Pflichtfeld: Dieses Feld darf nicht null oder leer sein.
     *
     * Beispiele: "Max", "Maria", "Anna", "Johann Wolfgang"
     *
     * Verwendung:
     * - Wird in getFullName() zwischen Titel und Nachname eingefuegt
     * - Wird in getOfficialName() nach dem Komma ausgegeben
     *
     * @since 1.0
     * @see #middleName
     * @see #getFullName()
     * @see #getOfficialName()
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Zweiter Vorname oder Mittelname der Person.
     *
     * Optionaler weiterer Vorname, der zwischen Erst- und Nachname steht.
     * Bei der Namensanzeige wird dieser zwischen Vorname und Nachname eingefuegt.
     *
     * Hinweis: Dieses Feld ist optional und wird nur in getFullName()
     * verwendet. In getOfficialName() wird der Mittelname bewusst weggelassen,
     * um eine standardisierte behördliche Darstellung zu gewaehrleisten.
     *
     * Beispiele: "Maria", "Wolfgang", "Alexander", "Josef"
     *
     * @since 1.0
     * @see #firstName
     * @see #getFullName()
     */
    @Column(name = "middle_name", length = 100)
    private String middleName;

    /**
     * Nachname (Familienname) der Person.
     *
     * Der aktuelle rechtsgueltige Familienname der Person. Bei Namensaenderungen
     * durch Heirat, Scheidung oder behördliche Entscheidung wird der neue Name hier gespeichert.
     *
     * Pflichtfeld: Dieses Feld darf nicht null oder leer sein.
     *
     * Beispiele: "Mustermann", "Schmidt", "von Goethe", "Müller-Schmidt"
     *
     * Namensaenderungen: Bei Aenderungen des Familienstands (z.B. Heirat)
     * wird der neue Name in diesem Feld gespeichert. Der urspruengliche Name kann
     * in maidenName fuer historische Zwecke aufbewahrt werden.
     *
     * @since 1.0
     * @see #maidenName
     * @see #getFullName()
     * @see #getOfficialName()
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Geburtsname der Person vor Namensaenderung.
     *
     * Speichert den urspruenglichen Nachnamen vor einer Namensaenderung, typischerweise
     * verursacht durch Heirat, Scheidung oder behördliche Entscheidung. Dieses Feld
     * dient der historischen Nachvollziehbarkeit und Identitaetspruefung.
     *
     * Beispiel: Eine Person heißt nach Heirat "Mustermann",
     * ihr Geburtsname (maidenName) war "Schmidt".
     *
     * Verwendungszwecke:
     * - Historische Nachvollziehbarkeit in Behördendokumenten und Melderegistern
     * - Identitaetspruefung bei alten Ausweisdokumenten und Zertifikaten
     * - Genealogische Recherchen und Familienforschung
     * - Rechtssichere Dokumentation von Namensaenderungen
     *
     * @since 1.0
     * @see #lastName
     */
    @Column(name = "maiden_name", length = 100)
    private String maidenName;

    // ==================== Geschaeftslogik (Domain Logic) ====================

    /**
     * Gibt den vollständigen Namen der Person in lesbarer Form zurueck.
     *
     * Formatiert alle verfuegbaren Namensteile in einer natuerlichen Lesereihenfolge,
     * die fuer die allgemeine Kommunikation und Anzeige geeignet ist. Optionale
     * Bestandteile (Titel, Zweitname) werden nur eingefuegt, wenn sie vorhanden sind.
     *
     * Format: [Titel] Vorname [Zweitname] Nachname
     *
     * Beispiele:
     * - Nur Pflichtfelder: "Max Mustermann"
     * - Mit Titel: "Dr. Max Mustermann"
     * - Mit Zweitname: "Anna Maria Schmidt"
     * - Mit Titel und Zweitname: "Prof. Johann Wolfgang von Goethe"
     *
     * Verwendungsempfehlung:
     * Diese Methode ist geeignet fuer:
     * - Anzeige in Benutzeroberflaechen und Dashboards
     * - Begruessungen in Briefen und E-Mails
     * - Allgemeine Kommunikation mit Buergern
     * - Informelle Listen und Kontaktverzeichnisse
     *
     * Garantie: Die Methode gibt niemals null zurueck.
     * Bei fehlenden optionalen Feldern wird der Name entsprechend gekuerzt.
     *
     * @return Vollstaendiger Name als formatierte Zeichenkette, nie null
     * @since 1.0
     * @see #getOfficialName()
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();

        if (title != null && !title.isEmpty()) {
            fullName.append(title).append(" ");
        }

        fullName.append(firstName);

        if (middleName != null && !middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
        }

        fullName.append(" ").append(lastName);

        return fullName.toString().trim();
    }

    /**
     * Gibt den Namen im offiziellen Behoerdenformat zurueck.
     *
     * Verwendet nur die Pflichtfelder (Vorname und Nachname) in standardisierter
     * Reihenfolge, die in deutschen Behoerden, Archiven und amtlichen Dokumenten ueblich ist.
     * Titel und Zweitnamen werden bewusst weggelassen, um eine einheitliche
     * und vergleichbare Darstellung zu gewaehrleisten.
     *
     * Format: Nachname, Vorname
     *
     * Beispiele:
     * - "Mustermann, Max"
     * - "Schmidt, Anna"
     * - "von Goethe, Johann"
     *
     * Verwendungsempfehlung:
     * Diese Methode ist geeignet fuer:
     * - Amtliche Dokumente und behoerdliche Formulare
     * - Alphabetische Sortierung in Listen und Registern
     * - Behoerdliche Korrespondenz und Bescheide
     * - Melderegister und Datenbankausgaben
     * - Archivierung und Dokumentenmanagement
     *
     * Technische Hinweise:
     * - Titel wird weggelassen fuer einheitliche behoerdliche Darstellung
     * - Zweitname wird weggelassen zur Vermeidung von Unklarheiten
     * - Geburtsname (maidenName) wird nicht verwendet
     *
     * Garantie: Die Methode gibt niemals null zurueck.
     *
     * @return Offizieller Name im Format "Nachname, Vorname", nie null
     * @since 1.0
     * @see #getFullName()
     */
    public String getOfficialName() {
        return lastName + ", " + firstName;
    }
}