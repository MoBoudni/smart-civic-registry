package de.behoerde.smartcivicregistry.unit.person.model;

import de.behoerde.smartcivicregistry.person.domain.model.PersonName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit-Tests für die {@link PersonName}-Klasse des Smart Civic Registry Systems.

 * Diese Testklasse verifiziert die Geschäftslogik vom PersonName Value Objects,
 * insbesondere die Namensformatierungsmethoden. Die Tests folgen dem Domain-Driven
 * Design Ansatz und pruefen sowohl die korrekte Formatierung als auch die
 * Behandlung von optionalen Feldern und Randfaellen.

 * Teststrategie:
 * - Isolierte Tests: Jeder Test prueft eine spezifische Formatierungslogik
 * - Vollstaendige Abdeckung: Alle Kombinationen von optionalen Pflichtfeldern
 * - Randfall-Tests: Behandlung von null-Werten, leeren Strings und Sonderzeichen
 * - Assertions: Lesbare Fehlermeldungen durch AssertJ

 * Getestete Funktionalitaet:
 * - getFullName(): Vollstaendiger Name fuer allgemeine Kommunikation
 * - getOfficialName(): Behoerdliches Format "Nachname, Vorname"
 * - maidenName: Speicherung des Geburtsnamens ohne Verwendung in Formatierung
 * - Optionale Felder: Titel und Zweitnamen werden korrekt behandelt
 *
 * @author Smart Civic Registry Team
 * @version 2.0
 * @since Phase 2 (Person Domain Module)
 * @see PersonName
 */
@DisplayName("PersonName Tests")
class PersonNameTest {

    /**
     * Testsuite fuer die {@link PersonName#getFullName()}-Methode.

     * Diese geschachtelte Klasse enthaelt alle Tests zur Überprüfung der
     * vollstaendigen Namensformatierung. Die Methode getFullName() formatiert
     * alle verfuegbaren Namensteile in einer natuerlichen Lesereihenfolge
     * fuer die allgemeine Kommunikation.

     * Getestete Aspekte:
     * - Korrekte Zusammenfuehrung aller Namensteile
     * - Behandlung von optionalen Feldern (Titel, Zweitname)
     * - Robustheit gegenueber null-Werten und leeren Strings
     * - Beibehaltung der korrekten Reihenfolge
     */
    @Nested
    @DisplayName("getFullName() Tests")
    class GetFullNameTests {

        /**
         * Testet die vollstaendige Namensformatierung mit allen Feldern.

         * Dieser Test verifiziert, dass ein Name mit Titel, Vorname, Zweitname
         * und Nachname korrekt formatiert wird. Alle Felder sind ausgefuellt
         * und muessen in der richtigen Reihenfolge kombiniert werden.

         * Erwartetes Ergebnis: "Dr. Max Johann Mustermann"
         */
        @Test
        @DisplayName("Vollstaendiger Name mit allen Feldern")
        void fullNameWithAllFields() {
            PersonName name = PersonName.builder()
                    .title("Dr.")
                    .firstName("Max")
                    .middleName("Johann")
                    .lastName("Mustermann")
                    .build();

            assertThat(name.getFullName()).isEqualTo("Dr. Max Johann Mustermann");
        }

        /**
         * Testet die Formatierung mit nur den Pflichtfeldern.

         * Dieser Test verifiziert, dass ein Name korrekt formatiert wird,
         * wenn nur die Pflichtfelder (Vorname und Nachname) ausgefuellt sind.
         * Optionale Felder duerfen das Ergebnis nicht beeinflussen.

         * Erwartetes Ergebnis: "Max Mustermann"
         */
        @Test
        @DisplayName("Nur Pflichtfelder")
        void fullNameWithRequiredFieldsOnly() {
            PersonName name = PersonName.builder()
                    .firstName("Max")
                    .lastName("Mustermann")
                    .build();

            assertThat(name.getFullName()).isEqualTo("Max Mustermann");
        }

        /**
         * Testet die Formatierung mit Titel aber ohne Zweitname.

         * Dieser Test verifiziert, dass der Titel korrekt eingefuegt wird,
         * auch wenn kein Zweitname vorhanden ist. Der Titel wird mit einem
         * Leerzeichen vom Vornamen getrennt.

         * Erwartetes Ergebnis: "Prof. Dr. Anna Schmidt"
         */
        @Test
        @DisplayName("Mit Titel aber ohne Zweitname")
        void fullNameWithTitleOnly() {
            PersonName name = PersonName.builder()
                    .title("Prof. Dr.")
                    .firstName("Anna")
                    .lastName("Schmidt")
                    .build();

            assertThat(name.getFullName()).isEqualTo("Prof. Dr. Anna Schmidt");
        }

        /**
         * Testet die Behandlung von Adelstiteln.

         * Dieser Test verifiziert, dass Praepositionen wie "von" korrekt
         * als Teil des Nachnamens behandelt werden. Diese gehoeren zum
         * Familiennamen und werden nicht als Titel interpretiert.

         * Erwartetes Ergebnis: "Johann Wolfgang von Goethe"
         */
        @Test
        @DisplayName("Mit Adelstitel")
        void fullNameWithNobleTitle() {
            PersonName name = PersonName.builder()
                    .firstName("Johann Wolfgang")
                    .lastName("von Goethe")
                    .build();

            assertThat(name.getFullName()).isEqualTo("Johann Wolfgang von Goethe");
        }

        /**
         * Testet die Behandlung von null-Werten.

         * Dieser Test verifiziert, dass null-Werte fuer optionale Felder
         * ignoriert werden und nicht zu einer "null"-Zeichenkette fuehren.
         * Die Methode muss robust gegenueber fehlenden Daten sein.

         * Erwartetes Ergebnis: "Max Mustermann" (ohne "null")
         */
        @Test
        @DisplayName("Null-Werte werden ignoriert")
        void fullNameWithNullValues() {
            PersonName name = PersonName.builder()
                    .title(null)
                    .firstName("Max")
                    .middleName(null)
                    .lastName("Mustermann")
                    .build();

            assertThat(name.getFullName()).isEqualTo("Max Mustermann");
        }

        /**
         * Testet die Behandlung von leeren Strings.

         * Dieser Test verifiziert, dass leere Strings fuer optionale Felder
         * ignoriert werden. Ein leerer Titel oder Zweitname fuehrt nicht zu
         * doppelten Leerzeichen im Ergebnis.

         * Erwartetes Ergebnis: "Max Mustermann" (keine führende/trailing Leerzeichen)
         */
        @Test
        @DisplayName("Leere Strings werden ignoriert")
        void fullNameWithEmptyStrings() {
            PersonName name = PersonName.builder()
                    .title("")
                    .firstName("Max")
                    .middleName("")
                    .lastName("Mustermann")
                    .build();

            assertThat(name.getFullName()).isEqualTo("Max Mustermann");
        }
    }

    /**
     * Testsuite fuer die {@link PersonName#getOfficialName()}-Methode.

     * Diese geschachtelte Klasse enthaelt alle Tests zur Überprüfung des
     * behoerdlichen Namensformats. Die Methode getOfficialName() formatiert
     * den Namen im Standardformat "Nachname, Vorname", das in deutschen
     * Behoerden, Archiven und amtlichen Dokumenten ueblich ist.

     * Getestete Aspekte:
     * - Korrekte Reihenfolge "Nachname, Vorname"
     * - Weglassen von Titeln fuer einheitliche Darstellung
     * - Ignorieren von Zweitnamen zur Vermeidung von Unklarheiten
     * - Behandlung von Adelstiteln als Teil des Nachnamens
     */
    @Nested
    @DisplayName("getOfficialName() Tests")
    class GetOfficialNameTests {

        /**
         * Testet das behoerdliche Format ohne Titel.

         * Dieser Test verifiziert, dass Titel bei der behoerdlichen
         * Formatierung weggelassen werden. Dies gewaehrleistet eine
         * einheitliche Darstellung in Registern und Dokumenten.

         * Erwartetes Ergebnis: "Mustermann, Max"
         */
        @Test
        @DisplayName("Offizielles Format ohne Titel")
        void officialNameWithoutTitle() {
            PersonName name = PersonName.builder()
                    .title("Dr.")
                    .firstName("Max")
                    .lastName("Mustermann")
                    .build();

            assertThat(name.getOfficialName()).isEqualTo("Mustermann, Max");
        }

        /**
         * Testet das behoerdliche Format mit Adelstitel.

         * Dieser Test verifiziert, dass Adelstitel wie "von" als Teil
         * des Nachnamens behandelt und nicht weggelassen werden. Die
         * vollstaendige Namensidentitaet wird bewahrt.

         * Erwartetes Ergebnis: "von Goethe, Johann Wolfgang"
         */
        @Test
        @DisplayName("Offizielles Format mit Adelstitel")
        void officialNameWithNobleTitle() {
            PersonName name = PersonName.builder()
                    .firstName("Johann Wolfgang")
                    .lastName("von Goethe")
                    .build();

            assertThat(name.getOfficialName()).isEqualTo("von Goethe, Johann Wolfgang");
        }

        /**
         * Testet das behoerdliche Format ohne Zweitname.

         * Dieser Test verifiziert, dass Zweitnamen bei der behoerdlichen
         * Formatierung weggelassen werden. Dies dient der Vermeidung von
         * Unklarheiten bei der alphabetischen Sortierung.

         * Erwartetes Ergebnis: "Schmidt, Anna" (Zweitname "Maria" wird ignoriert)
         */
        @Test
        @DisplayName("Offizielles Format ohne Zweitname")
        void officialNameWithoutMiddleName() {
            PersonName name = PersonName.builder()
                    .firstName("Anna")
                    .middleName("Maria")
                    .lastName("Schmidt")
                    .build();

            // Zweitname wird NICHT verwendet
            assertThat(name.getOfficialName()).isEqualTo("Schmidt, Anna");
        }
    }

    /**
     * Testsuite fuer die maidenName-Funktionalitaet.

     * Diese geschachtelte Klasse enthaelt alle Tests zur Überprüfung
     * der Geburtsname-Speicherung. Der maidenName dient der historischen
     * Nachvollziehbarkeit und Identitätsprüfung, wird aber nicht in
     * den Namensformatierungen verwendet.

     * Getestete Aspekte:
     * - Korrekte Speicherung des Geburtsnamens
     * - Keine Verwendung in getFullName()
     * - Keine Verwendung in getOfficialName()
     */
    @Nested
    @DisplayName("Geburtsname (maidenName) Tests")
    class MaidenNameTests {

        /**
         * Testet die Speicherung des Geburtsnamens.

         * Dieser Test verifiziert, dass der maidenName korrekt gespeichert
         * und ueber den Getter abgerufen werden kann. Der Geburtsname wird
         * fuer historische Zwecke aufbewahrt.

         * Erwartetes Ergebnis: "Schmidt"
         */
        @Test
        @DisplayName("Geburtsname wird gespeichert")
        void maidenNameIsStored() {
            PersonName name = PersonName.builder()
                    .firstName("Max")
                    .lastName("Mustermann")
                    .maidenName("Schmidt")
                    .build();

            assertThat(name.getMaidenName()).isEqualTo("Schmidt");
        }

        /**
         * Testet, dass der Geburtsname nicht in Formatierungen verwendet wird.

         * Dieser Test verifiziert, dass der maidenName ausschliesslich
         * der historischen Dokumentation dient und nicht in die aktuellen
         * Namensformatierungen einfliesst. Dies entspricht den Anforderungen
         */
        @Test
        @DisplayName("Geburtsname wird nicht in Formatierung verwendet")
        void maidenNameNotInFormat() {
            PersonName name = PersonName.builder()
                    .firstName("Max")
                    .lastName("Mustermann")
                    .maidenName("Schmidt")
                    .build();

            assertThat(name.getFullName()).isEqualTo("Max Mustermann");
            assertThat(name.getOfficialName()).isEqualTo("Mustermann, Max");
        }
    }

    /**
     * Testsuite fuer Randfaelle und spezielle Saetze.

     * Diese geschachtelte Klasse enthaelt Tests fuer ungewoehnliche
     * aber gueltige Eingabedaten. Diese Tests stellen sicher, dass
     * die Formatierungsmethoden robust gegenueber verschiedenen
     * Eingaben sind.

     * Getestete Saetze:
     * - Sehr lange Namen mit vielen Bestandteilen
     * - Sonderzeichen und internationale Zeichen
     * - Bindestriche und andere spezielle Zeichen
     */
    @Nested
    @DisplayName("Randfall-Tests")
    class EdgeCaseTests {

        /**
         * Testet die Behandlung sehr langer Namen.

         * Dieser Test verifiziert, dass die Formatierung auch bei
         * langen Namen mit mehreren Titeln und Vornamen korrekt
         * funktioniert. Alle Bestandteile werden in der richtigen
         * Reihenfolge zusammengefuehrt.

         * Erwartetes Ergebnis: Beginn mit Titel, Ende mit Nachname
         */
        @Test
        @DisplayName("Sehr langer Name")
        void veryLongName() {
            PersonName name = PersonName.builder()
                    .title("Prof. Dr. med.")
                    .firstName("Johann Wolfgang")
                    .middleName("Alexander")
                    .lastName("Mustermann-Schmidt")
                    .build();

            String fullName = name.getFullName();
            assertThat(fullName).startsWith("Prof. Dr. med.");
            assertThat(fullName).contains("Johann Wolfgang");
            assertThat(fullName).contains("Alexander");
            assertThat(fullName).endsWith("Mustermann-Schmidt");
        }

        /**
         * Testet die Behandlung von Sonderzeichen.

         * Dieser Test verifiziert, dass internationale Zeichen und
         * Sonderzeichen korrekt behandelt werden. Die Formatierung
         * darf keine Umlaute oder Sonderzeichen veraendern oder
         * entfernen.

         * Erwartetes Ergebnis: "José Muñoz-García" in beiden Formaten
         */
        @Test
        @DisplayName("Sonderzeichen im Namen")
        void specialCharactersInName() {
            PersonName name = PersonName.builder()
                    .firstName("José")
                    .lastName("Muñoz-García")
                    .build();

            assertThat(name.getFullName()).isEqualTo("José Muñoz-García");
            assertThat(name.getOfficialName()).isEqualTo("Muñoz-García, José");
        }
    }
}