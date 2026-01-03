# 🏛️ Smart Civic Registry - Zentrale Registerverwaltung

## 📋 Projektbeschreibung
Behördentaugliches Stammdaten-System zur Verwaltung von Personen, Organisationen und Anträgen.  
Entwickelt mit **Java 17, Spring Boot 3, PostgreSQL** und **hexagonaler Architektur**.

## 🎯 Zielsetzung
Demonstration moderner Java-Entwicklung für den öffentlichen Dienst mit Fokus auf:
- ✅ **DSGVO-Konforme Datenverarbeitung**
- ✅ **JWT-basierte Authentifizierung & Autorisierung**
- ✅ **Revisionssichere Protokollierung**
- ✅ **RESTful API mit OpenAPI Dokumentation**

## 🚀 Aktueller Projektstatus (Januar 2026)

### **✅ Abgeschlossene Phasen:**
- **Phase 0:** Repository & Dokumentation Setup **100%**
- **Phase 1:** Spring Boot Grundgerüst **100%**
- **Phase 2:** Person Domain Module **85%**
- **Phase 6:** Security Module **70%**

### **📈 Implementierte Features:**
1. **Person Domain Module** (Phase 2)
    - ✅ Person Entity mit Geschäftslogik (Altersberechnung, Adressvalidierung)
    - ✅ PersonRepository mit Custom Queries und Pagination
    - ✅ PersonService mit Validierung und Business Logic
    - ✅ Unit Test Suite (6 Tests ✅ grün)

2. **Security Module** (Phase 6)
    - ✅ JWT-basierte Authentifizierung
    - ✅ User Entity und Rollen-basierte Autorisierung
    - ✅ Spring Security Konfiguration
    - ✅ Password Encoding (BCrypt)

3. **Infrastruktur**
    - ✅ H2 Database für Entwicklung
    - ✅ PostgreSQL für Produktion (vorbereitet)
    - ✅ Hexagonale Architektur implementiert
    - ✅ Docker Setup (vorbereitet)

## 🛠️ Technologie-Stack

### **Backend:**
- Java 17
- Spring Boot 3.5.9
- Spring Security 6
- Spring Data JPA
- JWT (JSON Web Tokens)

### **Datenbank:**
- H2 (Entwicklung & Tests)
- PostgreSQL (Produktion)
- Flyway Migrations (geplant)

### **Architektur & Testing:**
- Hexagonale Architektur (Ports & Adapters)
- JUnit 5, Mockito
- Testcontainers (vorbereitet)
- Lombok, MapStruct

### **DevOps & Dokumentation:**
- Maven Wrapper
- Docker & Docker Compose
- GitHub Actions (CI/CD vorbereitet)
- OpenAPI 3 / Swagger UI
- ADR (Architecture Decision Records)

## 📁 Projektstruktur (Hexagonale Architektur)

smart-civic-registry/

├── src/main/java/de/behoerde/smartcivicregistry/

│ ├── person/ # Person Domain Module (Phase 2)

│ │ ├── domain/ # Domain Layer (Kern)

│ │ │ ├── model/ # Entities & Value Objects

│ │ │ └── repository/ # Repository Interfaces

│ │ ├── application/ # Application Layer

│ │ │ └── service/ # Application Services

│ │ └── infrastructure/ # Infrastructure Layer (geplant)

│ ├── security/ # Security Module (Phase 6)

│ │ ├── auth/ # Authentifizierung

│ │ ├── jwt/ # JWT Implementation

│ │ ├── config/ # Security Konfiguration

│ │ └── filter/ # Security Filter

│ ├── common/ # Gemeinsame Komponenten

│ │ ├── domain/ # Basisklassen (AuditableEntity)

│ │ └── response/ # API Response Klassen

│ ├── config/ # Spring Konfigurationen

│ └── SmartCivicRegistryApplication.java

├── src/test/java/ # Test Suite

├── src/main/resources/

│ ├── application.yml # Hauptkonfiguration

│ └── application-test.yml # Test-Konfiguration

├── docs/ # Projektdokumentation

├── docker/ # Container-Konfiguration

└── scripts/ # Entwicklungsskripte


## 🚀 Lokale Entwicklung

### **Voraussetzungen:**
- Java 17
- Maven 3.9+
- Docker (optional für PostgreSQL)
