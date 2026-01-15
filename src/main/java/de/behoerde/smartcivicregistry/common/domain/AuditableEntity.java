package de.behoerde.smartcivicregistry.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Abstrakte Basisklasse für revisionssichere Domain-Entitäten im Smart Civic Registry.

 * Diese Klasse implementiert die zentrale Audit-Funktionalität für alle Stammdaten-Entitäten
 * gemäß den Anforderungen der DSGVO-konformen Datenverarbeitung. Sie stellt sicher, dass
 * alle Änderungen an Entitäten nachvollziehbar protokolliert werden.

 * Als {@link MappedSuperclass} werden die Audit-Felder automatisch in die Tabellen
 * aller erbenden Entitäten (z.B. Person, Organisation) integriert, ohne eine separate
 * Tabelle für die Basisklasse zu erstellen.

 * Verwendung:
 * {@code
 * @Entity
 * public class Person extends AuditableEntity {
 *     // Domain-spezifische Felder
 * }
 * }

 * Revisionssicherheit:

 *   Automatische Zeitstempel bei Erstellung und Änderung</li>
 *   Benutzerkennung für Nachvollziehbarkeit (Integration mit Security Module)</li>
 *   Soft-Delete für Datenschutz-konforme Löschung</li>

 * @author Smart Civic Registry Team
 * @version 1.0
 * @since Phase 2 (Person Domain Module)
 * @see de.behoerde.smartcivicregistry.person.domain.model.Person
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity {

    /**
     * Zeitstempel der erstmaligen Persistierung der Entität.

     * Wird automatisch durch Hibernate's {@link CreationTimestamp} beim ersten
     * Speichern gesetzt und ist danach unveränderlich ({@code updatable = false}).
     * Essentiell für revisionssichere Protokollierung.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Zeitstempel der letzten Änderung der Entität.

     * Wird automatisch durch Hibernate's {@link UpdateTimestamp} bei jeder
     * Aktualisierung der Entität neu gesetzt. Ermöglicht Nachvollziehbarkeit
     * von Änderungen im Rahmen der DSGVO-Anforderungen.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Benutzerkennung des Erstellers der Entität.

     * Sollte beim Erstellen einer Entität automatisch aus dem Security Context
     * gesetzt werden (z.B. über EntityListener oder Service Layer).
     * Speichert typischerweise den Username aus dem JWT-Token.

     * @see: de.behoerde.smartcivicregistry.security.jwt.JwtUtils
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * Benutzerkennung des letzten Bearbeiters der Entität.

     * Sollte bei jeder Änderung automatisch aus dem Security Context aktualisiert
     * werden. Ermöglicht die Nachvollziehbarkeit von Änderungen für Audit-Zwecke
     * und DSGVO-Compliance.

     * @see: de.behoerde.smartcivicregistry.security.jwt.JwtUtils
     */
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * Soft-Delete-Flag für DSGVO-konforme Datenlöschung.

     * Wenn {@code true}, gilt die Entität als gelöscht, wird aber physisch in der
     * Datenbank beibehalten. Dies erfüllt die Anforderungen für revisionssichere
     * Protokollierung bei gleichzeitiger logischer Löschung von Personendaten.

     * Hinweis: Repository-Methoden müssen dieses Flag explizit
     * berücksichtigen. Alternativ kann {@code @Where(clause = "deleted = false")}
     * auf Entitätsebene verwendet werden.

     * Standard:{@code false} (nicht gelöscht)
     */
    @Column(name = "deleted")
    private boolean deleted = false;
}
