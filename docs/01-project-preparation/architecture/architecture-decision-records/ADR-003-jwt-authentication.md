# ADR-003: JWT-basierte Authentifizierung und Autorisierung

## Status
✅ Angenommen am 25.12.2025

## Kontext
Das "Smart Civic Registry" System verwaltet sensible personenbezogene Daten gemäß DSGVO. Es benötigt eine sichere Authentifizierungslösung für:

### Anwendungsfälle:
1. **Beamte/Sachbearbeiter**: Zugriff auf Personen- und Organisationsdaten
2. **System-zu-System Kommunikation**: API Zugriff für Integrationen
3. **Bürger-Selbstservice**: Geplante Erweiterung für Bürgerportal

### Sicherheitsanforderungen:
- **DSGVO Compliance**: Schutz personenbezogener Daten
- **Revisionssicherheit**: Nachvollziehbare Zugriffsprotokolle
- **Rollenbasierte Zugriffskontrolle**: Unterschiedliche Berechtigungen je Rolle
- **Stateless Architektur**: Skalierbarkeit für mögliche hohe Last

### Technische Constraints:
- **Spring Boot 3.2** als Basis-Framework
- **RESTful API** als primäre Schnittstelle
- **Frontend-Agnostisch**: Muss mit verschiedenen Frontends kompatibel sein
- **Einfache Integration**: In bestehende Behörden-IT-Landschaften

## Entscheidung
Wir implementieren **JWT (JSON Web Tokens) basierte Authentifizierung** mit folgender Architektur:

### 1. Token Strategie (Dual-Token Approach)
- **Access Token**: Kurzlebig (15-30 Minuten), für API-Zugriff
- **Refresh Token**: Langlebig (7 Tage), gesichert gespeichert, für Token-Erneuerung
- **Logout Mechanismus**: Token Invalidation durch Blacklisting/Whitelisting

### 2. JWT Implementation Details
```yaml
# Token Struktur (Claims)
access_token:
  typ: "JWT"
  alg: "HS256"  # HMAC mit SHA-256
  claims:
    sub: "user@behoerde.de"           # Subject (Username/Email)
    roles: ["ROLE_OFFICER", "ROLE_USER"] # Benutzerrollen
    iss: "smart-civic-registry"       # Issuer
    aud: "registry-api"              # Audience
    exp: 1700000000                  # Expiration Time
    iat: 1699999400                  # Issued At
    jti: "unique-token-id"           # Token ID für Revocation

refresh_token:
  storage: "database"                # Gesichert in DB gespeichert
  expiration: "7 days"
  one-time-use: true                 # Single Use pro Refresh