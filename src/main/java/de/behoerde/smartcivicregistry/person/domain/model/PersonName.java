package de.behoerde.smartcivicregistry.person.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Value Object für die Verwaltung von Personennamen im Smart Civic Registry System.

 * Diese {@link Embeddable}-Klasse kapselt alle namensrelevanten Informationen einer Person
 * und stellt Methoden zur Formatierung und Darstellung von Namen bereit. Als Value Object
 * wird sie direkt in die Person-Entity eingebettet, ohne eine eigene Datenbanktabelle zu erstellen.
 * Dies entspricht dem Domain-Driven Design (DDD) Paradigma, bei dem Namen als unveränderliche
 * Wertobjekte ohne eigene Identität behandelt werden.

 * Value Object Charakteristiken:

 *   Unveränderlichkeit: Nach der Erstellung nicht mehr änderbar
 *   Wertbasierte Gleichheit: Gleichheit basiert auf Attributwerten, nicht auf Identität
 *   Keine eigene ID: Kein eigener Lebenszyklus, eingebettet in Parent-Entity
 *   Selbstvalidierend: Implementiert Geschäftslogik für Namensformatierung

 * Verwendungsbeispiel:
 * {@code
 * // Einbettung in Person-Entity
 * @Embedded
 * private PersonName name;

 * // Erstellung mittels Builder
 * PersonName name = PersonName.builder()
 *     .title("Dr.")
 *     .firstName("Max")
 *     .middleName("Johann")
 *     .lastName("Mustermann")
 *     .maidenName("Schmidt")
 *     .build();

 * // Namensformate abrufen
 * String fullName = name.getFullName();      // "Dr. Max Johann Mustermann"
 * String official = name.getOfficialName();  // "Mustermann, Max"
 * }
 *
 * Namenskonventionen und Validierung:

 *   Alle Namensteile werden bei der Ausgabe korrekt mit Leerzeichen formatiert
 *   Leere oder null-Werte für optionale Felder werden automatisch ignoriert
 *   Der {@link #maidenName} speichert den Geburtsnamen für historische Nachvollziehbarkeit
 *   Die Methoden {@link #getFullName()} und {@link #getOfficialName()} geben nie {@code null} zurück
 * 
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

     * Enthält akademische Grade, Adelstitel und Anredeformen, die vor dem Namen geführt werden.

     * Übliche Werte: "Dr.", "Prof.", "Dr. med.", "Prof. Dr.", "von", "zu"

     * Technische Details:

     *   Optionales Feld, maximal 50 Zeichen
     *   Wird in {@link #getFullName()} mit einem Leerzeichen vor dem Vornamen eingefügt
     *   Wird in {@link #getOfficialName()} bewusst weggelassen
     * 
     *
     * @since 1.0
     * @see #getFullName()
     */
    @Column(name = "title", length = 50)
    private String title;

    /**
     * Vorname der Person.

     * Der primäre Rufname (Rufnamen), unter dem die Person im täglichen Gebrauch bekannt ist.
     * Bei mehreren Vornamen wird der erste eingetragen.

     * Pflichtfeld: Dieses Feld darf nicht {@code null} oder leer sein.

     * Beispiele: "Max", "Maria", "Anna", "Johann Wolfgang"

     * Verwendung:

     *   Wird in {@link #getFullName()} zwischen Titel und Nachname eingefügt
     *   Wird in {@link #getOfficialName()} nach dem Komma ausgegeben

     * @since 1.0
     * @see #middleName
     * @see #getFullName()
     * @see #getOfficialName()
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Zweiter Vorname oder Mittelname der Person.

     * Optionaler weiterer Vorname, der zwischen Erst- und Nachname steht.
     * Bei der Namensanzeige wird dieser zwischen Vorname und Nachname eingefügt.

     * Hinweis: Dieses Feld ist optional und wird nur in {@link #getFullName()}
     * verwendet. In {@link #getOfficialName()} wird der Mittelname bewusst weggelassen,
     * um eine standardisierte behördliche Darstellung zu gewährleisten.

     * Beispiele: "Maria", "Wolfgang", "Alexander", "Josef"

     * @since 1.0
     * @see #firstName
     * @see #getFullName()
     */
    @Column(name = "middle_name", length = 100)
    private String middleName;

    /**
     * Nachname (Familienname) der Person.

     * Der aktuelle rechtsgültige Familienname der Person. Bei Namensänderungen
     * durch Heirat, Scheidung oder behördliche Entscheidung wird der neue Name hier gespeichert.

     * Pflichtfeld: Dieses Feld darf nicht {@code null} oder leer sein.

     * Beispiele: "Mustermann", "Schmidt", "von Goethe", "Müller-Schmidt"

     * Namensänderungen: Bei Änderungen des Familienstands (z.B. Heirat)
     * wird der neue Name in diesem Feld gespeichert. Der ursprüngliche Name kann
     * in {@link #maidenName} für historische Zwecke aufbewahrt werden.

     * @since 1.0
     * @see #maidenName
     * @see #getFullName()
     * @see #getOfficialName()
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Geburtsname der Person vor Namensänderung.

     * Speichert den ursprünglichen Nachnamen vor einer Namensänderung, typischerweise
     * verursacht durch Heirat, Scheidung oder behördliche Entscheidung. Dieses Feld
     * dient der historischen Nachvollziehbarkeit und Identitätsprüfung.

     * Beispiel: Eine Person heißt nach Heirat "Mustermann",
     * ihr Geburtsname (maidenName) war "Schmidt".

     * Verwendungszwecke:

     *   Historische Nachvollziehbarkeit in Behördendokumenten und Melderegistern
     *   Identitätsprüfung bei alten Ausweisdokumenten und Zertifikaten
     *   Genealogische Recherchen und Familienforschung
     *   Rechtssichere Dokumentation von Namensänderungen

     * @since 1.0
     * @see #lastName
     */
    @Column(name = "maiden_name", length = 100)
    private String maidenName;

    // ==================== GESCHÄFTSLOGIK (DOMAIN LOGIC) ====================

    /**
     * Gibt den vollständigen Namen der Person in lesbarer Form zurück.

     * Formatiert alle verfügbaren Namensteile in einer natürlichen Lesereihenfolge,
     * die für die allgemeine Kommunikation und Anzeige geeignet ist. Optionale
     * Bestandteile (Titel, Zweitname) werden nur eingefügt, wenn sie vorhanden sind.

     * Format:

     * [Titel] Vorname [Zweitname] Nachname

     * Beispiele:

     *   Nur Pflichtfelder: "Max Mustermann"
     *   Mit Titel: "Dr. Max Mustermann"
     *   Mit Zweitname: "Anna Maria Schmidt"
     *   Mit Titel und Zweitname: "Prof. Johann Wolfgang von Goethe"

     * Verwendungsempfehlung:

     * Diese Methode ist geeignet für:

     *   Anzeige in Benutzeroberflächen und Dashboards
     *   Begrüßungen in Briefen und E-Mails
     *   Allgemeine Kommunikation mit Bürgern
     *   Informelle Listen und Kontaktverzeichnisse

     * Garantie: Die Methode gibt niemals {@code null} zurück.
     * Bei fehlenden optionalen Feldern wird der Name entsprechend gekürzt.

     * @return Vollständiger Name als formatierte Zeichenkette, nie {@code null}
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
     * Gibt den Namen im offiziellen Behördenformat zurück.

     * Verwendet nur die Pflichtfelder (Vorname und Nachname) in standardisierter
     * Reihenfolge, die in deutschen Behörden, Archiven und amtlichen Dokumenten üblich ist.
     * Titel und Zweitnamen werden bewusst weggelassen, um eine einheitliche
     * und vergleichbare Darstellung zu gewährleisten.

     * Format:

     * Nachname, Vorname

     * Beispiele:

     *   "Mustermann, Max"
     *   "Schmidt, Anna"
     *   "von Goethe, Johann"

     * Verwendungsempfehlung:

     * Diese Methode ist geeignet für:

     *   Amtliche Dokumente und behördliche Formulare
     *   Alphabetische Sortierung in Listen und Registern
     *   Behördliche Korrespondenz und Bescheide
     *   Melderegister und Datenbankausgaben
     *   Archivierung und Dokumentenmanagement

     * Technische Hinweise:

     *   Titel wird weggelassen für einheitliche behördliche Darstellung
     *   Zweitname wird weggelassen zur Vermeidung von Unklarheiten
     *   Geburtsname ({@link #maidenName}) wird nicht verwendet
     
     * Garantie: Die Methode gibt niemals {@code null} zurück.
     
     * @return Offizieller Name im Format "Nachname, Vorname", nie {@code null}
     * @since 1.0
     * @see #getFullName()
     */
    public String getOfficialName() {
        return lastName + ", " + firstName;
    }
}