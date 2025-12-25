# ADR-001: Hexagonale Architektur (Ports & Adapters)

## Status
✅ Angenommen am 25-12-2025

## Kontext
Wir entwickeln ein behördliches Stammdaten-System ("Smart Civic Registry") mit folgenden Anforderungen:

### Technische Anforderungen:
- Komplexe Geschäftslogik (Personen, Organisationen, Anträge verwalten)
- Mehrere externe Schnittstellen (REST API, Datenbank, Security, mögliche Integrationen)
- Hohe Anforderungen an Wartbarkeit und Testbarkeit
- Langlebige Wartungsphase (typisch für öffentlichen Sektor)

### Business Anforderungen:
- DSGVO-konforme Datenverarbeitung
- Revisionssichere Protokollierung
- Skalierbarkeit für zukünftige Erweiterungen
- Einfache Integration in bestehende Behörden-IT-Landschaften

### Problemstellung:
Traditionelle Layered-Architekturen (Controller → Service → Repository) führen oft zu:
- Starker Kopplung an Frameworks
- Schwieriger Testbarkeit der Geschäftslogik
- "Big Ball of Mud" bei wachsender Komplexität

## Entscheidung
Wir implementieren **hexagonale Architektur (Ports & Adapters)** mit drei klar getrennten Schichten:

### 1. Domain Layer (Kern)
- **Inhalt**: Reine Geschäftslogik, Domain Entities, Value Objects, Domain Services
- **Regel**: Keine Abhängigkeiten zu Frameworks, Libraries oder externen Systemen
- **Beispiele**: `Person.java`, `Organization.java`, `Application.java`, `Address.java` (Value Object)

### 2. Application Layer
- **Inhalt**: Use Cases, Application Services, DTOs, Domain Event Handlers
- **Aufgabe**: Koordiniert den Datenfluss zwischen Domain und Infrastructure
- **Regel**: Kennt die Domain, aber nicht die Infrastructure Details
- **Beispiele**: `PersonService.java`, `CreatePersonCommand.java`, `PersonCreatedEvent.java`

### 3. Infrastructure Layer
- **Inhalt**: Implementierung von Ports, Frameworks, Datenbank, REST API
- **Aufgabe**: Stellt konkrete Implementierungen für die benötigten Ports bereit
- **Regel**: Abhängigkeiten zu Frameworks sind hier erlaubt
- **Beispiele**: `PersonRepositoryImpl.java` (JPA), `PersonController.java` (Spring MVC), `JwtService.java`

### Ports & Adapters Konzept:
- **Primary Ports**: Wie die Anwendung angesprochen wird (REST API, CLI, Events)
- **Primary Adapters**: Implementierung der Primary Ports (Spring Controller, Message Listeners)
- **Secondary Ports**: Was die Anwendung benötigt (Datenbank, externe Services)
- **Secondary Adapters**: Implementierung der Secondary Ports (JPA Repository, REST Client)

## Konsequenzen

### ✅ Vorteile
1. **Framework Unabhängigkeit**: Domain Layer kann ohne Spring getestet werden
2. **Einfache Testbarkeit**: Geschäftslogik isoliert von Infrastructure testbar
3. **Klare Trennung der Verantwortlichkeiten**: Jede Schicht hat eindeutige Aufgabe
4. **Flexibilität**: Einfacher Austausch von Komponenten (z.B. Datenbank, UI)
5. **Wartbarkeit**: Änderungen in einem Layer beeinflussen andere minimal
6. **Onboarding**: Neue Entwickler verstehen Architektur schnell

### ⚠️ Nachteile
1. **Höhere initiale Komplexität**: Mehr Boilerplate Code notwendig
2. **Steilere Learning Curve**: Entwickler müssen Architektur verstehen
3. **Mehr Dateien/Verzeichnisse**: Größere Projektstruktur
4. **Performance Overhead**: Mehr Abstraktionsschichten können Performance beeinflussen
5. **Over-Engineering Risk**: Für sehr kleine Projekte möglicherweise Overkill

### 📊 Trade-offs akzeptiert:
- **Komplexität vs. Wartbarkeit**: Wir akzeptieren höhere initiale Komplexität für bessere langfristige Wartbarkeit
- **Development Speed vs. Quality**: Etwas langsamere initiale Entwicklung für höhere Codequalität
- **Team Size Consideration**: Auch als Solo-Entwickler profitieren wir von der klaren Struktur

## Alternativen erwogen

### 1. Traditionelle Layered Architecture (abgelehnt)
- **Vorteile**: Einfacher, weniger Boilerplate, schnellere Entwicklung initial
- **Nachteile**: Starke Kopplung, schwerer zu testen, "Big Ball of Mud" Risk
- **Entscheidung**: Abgelehnt wegen schlechterer Langzeitwartbarkeit

### 2. Clean Architecture (abgelehnt)
- **Vorteile**: Ähnlich wie hexagonal, sehr populär
- **Nachteile**: Komplexere Regeln, mehr Abstraktionen
- **Entscheidung**: Hexagonal ist einfacher zu verstehen bei ähnlichen Vorteilen

### 3. Microservices (abgelehnt)
- **Vorteile**: Höhere Skalierbarkeit, unabhängige Deployment
- **Nachteile**: Viel zu komplex für MVP, Operational Overhead
- **Entscheidung**: Zu Over-engineered für initiales Projekt

### 4. MVC (Model-View-Controller) für Backend (abgelehnt)
- **Vorteile**: Sehr einfach, gut dokumentiert
- **Nachteile**: Vermischt Concerns, schlecht für komplexe Business Logic
- **Entscheidung**: Nicht geeignet für Domain-driven Design

## Implementierungsdetails

### Package Struktur für Person Domain:

src/main/java/de/behoerde/smartcivicregistry/person/
├── domain/ # Domain Layer
│ ├── model/
│ │ ├── Person.java # Aggregate Root
│ │ ├── Name.java # Value Object
│ │ └── Address.java # Value Object
│ ├── repository/ # Repository Interface (Port)
│ │ └── PersonRepository.java
│ └── service/
│ └── PersonDomainService.java # Domain Service
├── application/ # Application Layer
│ ├── service/
│ │ ├── PersonService.java # Application Service
│ │ └── PersonQueryService.java
│ ├── dto/
│ │ ├── request/
│ │ │ └── CreatePersonRequest.java
│ │ └── response/
│ │ └── PersonResponse.java
│ └── event/
│ └── PersonCreatedEvent.java
└── infrastructure/ # Infrastructure Layer
├── persistence/
│ ├── PersonRepositoryImpl.java # JPA Implementation (Adapter)
│ └── mapper/
│ └── PersonMapper.java # MapStruct Mapper
└── controller/
└── PersonController.java # REST Controller (Adapter)


### Abhängigkeitsregeln:
1. **Domain Layer**: Keine Abhängigkeiten zu anderen Layern
2. **Application Layer**: Abhängig von Domain Layer
3. **Infrastructure Layer**: Abhängig von Domain und Application Layer
4. **Inward Dependency**: Abhängigkeiten zeigen immer zur Domain (nach innen)

### Testing Strategie:
- **Domain Layer**: Unit Tests ohne Spring Context
- **Application Layer**: Unit Tests mit Mockito für Abhängigkeiten
- **Infrastructure Layer**: Integration Tests mit Testcontainers

## Referenzen

### Bücher & Artikel:
- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- "Clean Architecture" by Robert C. Martin
- "Domain-Driven Design" by Eric Evans

### Technische Ressourcen:
- [Spring Boot Hexagonal Architecture Example](https://github.com/ivangfr/springboot-react-hexagonal)
- [Baeldung: Hexagonal Architecture in Java](https://www.baeldung.com/hexagonal-architecture-ddd-spring)
- [Martin Fowler: Ports and Adapters](https://martinfowler.com/bliki/HexagonalArchitecture.html)

### Behördenrelevante Referenzen:
- [Architekturen in der öffentlichen Verwaltung - BMI](https://www.cio.bund.de/Web/DE/Architekturen/architekturen_node.html)
- [Öffentliche IT: Nachhaltige Architekturen](https://www.oeffentliche-it.de/documents/10181/14412/Studie+Nachhaltige+IT-Architekturen)

## Verwandte ADRs
- [ADR-002: PostgreSQL als Datenbank](./ADR-002-postgresql-database.md)
- [ADR-003: JWT-basierte Authentifizierung](./ADR-003-jwt-authentication.md)
- [ADR-008: Test-Driven Development Strategie](./ADR-008-testing-strategy.md)

---

**Autor**: Mo Boudni  
**Review**: Self-reviewed  
**Aktualisiert**: 25.12.2025 
**Gültig für**: Smart Civic Registry v1.0+