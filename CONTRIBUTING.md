# Beitragsrichtlinien

Vielen Dank für Ihr Interesse am Smart Civic Registry Projekt!

## 🎯 Wie Sie beitragen können

### 1. Fehler melden
- Nutzen Sie die GitHub Issues
- Beschreiben Sie den Fehler genau
- Fügen Sie Screenshots oder Logs hinzu wenn möglich

### 2. Neue Features vorschlagen
- Erstellen Sie ein Issue mit dem Label "enhancement"
- Beschreiben Sie den Use Case
- Skizzieren Sie mögliche Lösungsansätze

### 3. Code beitragen
1. Forken Sie das Repository
2. Erstellen Sie einen Feature-Branch
3. Committen Sie Ihre Änderungen
4. Erstellen Sie einen Pull Request

## 📋 Entwicklungsumgebung einrichten

### Voraussetzungen
- JDK 17 oder höher
- Maven 3.8+
- Docker & Docker Compose
- Git

### Lokales Setup
```bash
# Repository klonen
git clone https://github.com/MoBoudni/smart-civic-registry.git
cd smart-civic-registry

# Services starten
docker-compose up -d

# Anwendung starten
./mvnw spring-boot:run