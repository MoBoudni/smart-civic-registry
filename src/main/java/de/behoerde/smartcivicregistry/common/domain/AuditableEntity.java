package de.behoerde.smartcivicregistry.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Abstrakte Basisklasse für revisionssichere Domain-Entitäten im Smart Civic Registry.
 * <p>
 * Diese Klasse implementiert die zentrale Audit-Funktionalität für alle Stammdaten-Entitäten
 * gemäß den Anforderungen der DSGVO-konformen Datenverarbeitung. Sie stellt sicher, dass
 * alle Änderungen an Entitäten nachvollziehbar protokolliert werden.
 * </p>
 * <p>
 * Als {@link MappedSuperclass} werden die Audit-Felder automatisch in die Tabellen
 * aller erbenden Entitäten (z.B. Person, Organisation) integriert, ohne eine separate
 * Tabelle für die Basisklasse zu erstellen.
 * </p>
 *
 * <strong>Verwendung:</strong>
 * <pre>{@code
 * @Entity
 * public class Person extends AuditableEntity {
 *     // Domain-spezifische Felder
 * }
 * }</pre>
 *
 * <strong>Revisionssicherheit:</strong>
 * <ul>
 *   <li>Automatische Zeitstempel bei Erstellung und Änderung</li>
 *   <li>Benutzerkennung für Nachvollziehbarkeit (Integration mit Security Module)</li>
 *   <li>Soft-Delete für Datenschutz-konforme Löschung</li>
 * </ul>
 *
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
     * <p>
     * Wird automatisch durch Hibernate's {@link CreationTimestamp} beim ersten
     * Speichern gesetzt und ist danach unveränderlich ({@code updatable = false}).
     * Essentiell für revisionssichere Protokollierung.
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Zeitstempel der letzten Änderung der Entität.
     * <p>
     * Wird automatisch durch Hibernate's {@link UpdateTimestamp} bei jeder
     * Aktualisierung der Entität neu gesetzt. Ermöglicht Nachvollziehbarkeit
     * von Änderungen im Rahmen der DSGVO-Anforderungen.
     * </p>
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Benutzerkennung des Erstellers der Entität.
     * <p>
     * Sollte beim Erstellen einer Entität automatisch aus dem Security Context
     * gesetzt werden (z.B. über EntityListener oder Service Layer).
     * Speichert typischerweise den Username aus dem JWT-Token.
     * </p>
     *
     * @see: de.behoerde.smartcivicregistry.security.jwt.JwtUtils
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * Benutzerkennung des letzten Bearbeiters der Entität.
     * <p>
     * Sollte bei jeder Änderung automatisch aus dem Security Context aktualisiert
     * werden. Ermöglicht die Nachvollziehbarkeit von Änderungen für Audit-Zwecke
     * und DSGVO-Compliance.
     * </p>
     *
     * @see: de.behoerde.smartcivicregistry.security.jwt.JwtUtils
     */
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * Soft-Delete-Flag für DSGVO-konforme Datenlöschung.
     * <p>
     * Wenn {@code true}, gilt die Entität als gelöscht, wird aber physisch in der
     * Datenbank beibehalten. Dies erfüllt die Anforderungen für revisionssichere
     * Protokollierung bei gleichzeitiger logischer Löschung von Personendaten.
     * </p>
     * <p>
     * <strong>Hinweis:</strong> Repository-Methoden müssen dieses Flag explizit
     * berücksichtigen. Alternativ kann {@code @Where(clause = "deleted = false")}
     * auf Entitätsebene verwendet werden.
     * </p>
     *
     * <strong>Standard:</strong> {@code false} (nicht gelöscht)
     */
    @Column(name = "deleted")
    private boolean deleted = false;
}
