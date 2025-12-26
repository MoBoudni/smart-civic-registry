_ADR-003: JWT-basierte Anmeldung (Authentifizierung) und Rechteprüfung (Autorisierung)_

**Status**
Genehmigt am 25.12.2025


**Kontext**
Das System "Smart Civic Registry" speichert persönliche Daten. Diese Daten müssen nach DSGVO 
geschützt werden. Wir brauchen eine sichere Anmeldung für:

Anwendungsfälle:
1. Beamte und Sachbearbeiter brauchen Zugang zu Personen- und Organisationsdaten.
2. Systeme kommunizieren über APIs.
3. Bürger sollen später ein eigenes Portal nutzen können.

Sicherheitsanforderungen:
- Wir schützen persönliche Daten nach DSGVO.
- Alle Zugriffe werden protokolliert und können geprüft werden.
- Der Zugang hängt von der Rolle des Nutzers ab.
- Das System arbeitet ohne Zustand, um gut zu skalieren.

Technische Bedingungen:
- Wir nutzen Spring Boot 3.2 als Basis.
- Die API ist im REST-Stil gebaut.
- Das Frontend kann verschieden sein.
- Die Lösung muss leicht in bestehende IT-Systeme passen.

**Entscheidung**
Wir verwenden JWT (JSON Web Tokens) für die Anmeldung. Die Architektur ist wie folgt:

1. Token-Plan (Zwei Token)

- Access Token: Gültig für 15 bis 30 Minuten, für API-Zugriff.
- Refresh Token: Gültig für 7 Tage, wird sicher gespeichert und erneuert das Access Token.
- Logout: Tokens werden ungültig gemacht durch Blacklist oder Whitelist.

2. JWT Implementation Details

 Token Struktur (Claims):

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