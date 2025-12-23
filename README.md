#  Smart Civic Registry - Zentrale Registerverwaltung

## Projektbeschreibung

* Behördentaugliches Stammdaten-System zur Verwaltung von Personen, Organisationen und Anträgen.  
* Entwickelt mit Java 17, Spring Boot 3, PostgreSQL und hexagonaler Architektur.

## Zielsetzung

Demonstration moderner Java-Entwicklung für den öffentlichen Dienst mit Fokus auf:
- DSGVO-Konforme Datenverarbeitung
- JWT-basierte Authentifizierung & Autorisierung
- Revisionssichere Protokollierung
- RESTful API mit OpenAPI Dokumentation

## Technologie-Stack

- Backend: Java 17, Spring Boot 3.2, Spring Security, JWT
- Datenbank: PostgreSQL, Flyway Migrations
- Architektur: Hexagonale Architektur (Ports & Adapters)
- Testing: JUnit 5, Testcontainers, Mockito
- DevOps: Docker, GitHub Actions, Maven
- Dokumentation: OpenAPI 3, Markdown, ADRs

## 📁 Projektstruktur

smart-civic-registry/
├── docs/ # Projektdokumentation

├── src/ # Quellcode (hexagonale Architektur)

├── docker/ # Container-Konfiguration

└── scripts/ # Entwicklungsskripte
