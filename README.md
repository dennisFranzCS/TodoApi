# Todolist API

Dies ist ein Java Spring Boot basiertes CRUD Backend, das für das Verwalten von Todolisten und Todos entwickelt wurde. Das Projekt nutzt Technologien wie JPA, SpringBoot, SpringSecurity, Postgres, OAuth2, KeyCloak, Docker und Postman. Mit der Integrierung von OAuth2, SpringSecurity und KeyCloak als SSO Verwalter wird die einfache Anmeldung von neuen Benutzern und Anwendungen gewährleistet. Dank Docker und Postman können die API-Endpunkte einfach manuell getestet und überprüft werden. Dank Unit Tests der Service Ebene können diese Services automatisiert überprüft werden.
Dieses Projekt wurde von mir geschrieben, um meine Grundkenntnisse im Bereich der Java Spring Boot Entwicklung zu demonstrieren. Es dient hauptsächlich zu Lernzwecken und ist nicht für den Einsatz in einer Produktionsumgebung vorgesehen. Obwohl Sorgfalt darauf verwendet wurde, dass das Projekt ordnungsgemäß funktioniert, sollte es nicht als vollständig geprüft oder produktionsbereit betrachtet werden.

## How to get started

Das Projekt besteht aus den 3 Komponenten Java Spring Boot Backend, Docker Container und eine Postman Collection. Das Backend verwaltet den eigentlichen Webserver. Die Docker Container verwalten eine Möglichkeit eine KeyCloak Instanz mit dazugehöriger Datenbank und eine Datenbank für das Backend zu erstellen. Die Postman Collection soll exemplarisch zeigen, welche Anfragen beim Backend  gemacht werden können. Die Postman Collection enthält jedoch keine Tests.

### Backend

Um das Backend Projekt zu starten, müssen Sie einige Schritte befolgen. Zunächst müssen Sie sicherstellen, dass Sie über die notwendige Software, wie die Java Development Kit (JDK) und das Apache Maven Build-Tool (zur Vereinfachung liegt dem Projekt die `mvnw` und `mvnw.cmd` bei), verfügen. Sobald Sie alle Voraussetzungen erfüllt haben, können Sie mit den folgenden Schritten fortfahren:

1. Klonen Sie das Projekt von Github auf Ihren lokalen Computer oder laden Sie es als ZIP-Datei herunter und entpacken Sie es.
2. Öffnen Sie eine Kommandozeile in dem Verzeichnis, in dem das Projekt geklont wurde, und führen Sie den folgenden Befehl aus: `mvn clean install`
3. Sobald der Build erfolgreich abgeschlossen ist, können Sie das Projekt mit dem folgenden Befehl starten (Umgebungsvariablen müssen mit `-Dspring-boot.run.arguments="--VARIABLE1=VALUE1 --VARIABLE2=VALUE2"` etc. gesetzt werden): Postgres: `mvn spring-boot:run -Dspring-boot.run.profiles=postgres` H2: `mvn spring-boot:run -Dspring-boot.run.profiles=h2`
4. Sobald das Projekt gestartet ist, können Sie es eine REST-Client-Anwendung wie Postman aufrufen.

Bei der Verwendung von `mvnw` sollten bei allen Befehlen `mvn` mit `mvnw` ausgetauscht werden. Mit diesen Schritten sollte das Projekt erfolgreich gestartet werden. Vereinfacht kann dies durch IDEs wie Eclipse oder IntelliJ werden.
Das Backend benötigt folgende Umgebungsvariablen:

| Name                       | Beschreibung                                                          |
|----------------------------|-----------------------------------------------------------------------|
| DB_URL                     | Datasource URL zur Todo Datenbank.                                    |
| DB_USERNAME                | Nutzername der zur Anmeldung bei der Todo Datenbank genutzt wird.     |
| DB_PASSWORD                | Passwort der zur Anmeldung bei der Todo Datenbank genutzt wird.       |
| SERVER_PORT                | Port, über welchen das Java Spring Boot Backend erreicht werden kann. |
| RESOURCE_SERVER_ISSUER_URI | URI von der KeyCloak Server Umgebung.                                 |

### Docker Container

Docker Container beinhalteten einen Großteil der Entwicklungsumgebung. Die Docker Compose Datei ist zu finden unter `/dev/docker-compose.yml`. Um die Container zu starten, müssen folgende Umgebungsvariablen gesetzt sein:

| Name              | Beschreibung                                                       |
|-------------------|--------------------------------------------------------------------|
| KC_DB_VOLUME_PATH | Pfad, wo die KeyCloak Datenbank permanent gespeichert werden soll. |
| KC_PORT           | Port, über welchen KeyCloak erreicht werden kann.                  |
| DB_USERNAME       | Nutzername, der zur Anmeldung bei der Todo Datenbank genutzt wird. |
| DB_PASSWORD       | Passwort, der zur Anmeldung bei der Todo Datenbank genutzt wird.   |
| TODO_DB_PORT      | Port, über welchen die Todo Datenbank erreicht werden kann.        |

Um die Container mithilfe von Docker Compose zu starten, muss folgender Befehl ausgeführt werden: `docker-compose --env-file=relativer-pfad up`. Mit relativer Pfad ist dabei der Pfad zur Datei relativ zum Ausführverzeichnis gemeint.

#### KeyCloak

KeyCloak benötigt für den Authentifizierungsprozess auch noch ein Setup. Dafür sind folgende Schritte notwendig:
1. Nach dem Start der Docker Container, besuche die Seite `http://localhost:8080/`.
2. Login mit Nutzerdaten Username `kc_admin` und Passwort `kc_password`. Diese können auch in Docker Compose Datei geändert werden.
3. Links oben beim master drop down Menü, einen neuen Realm erstellen.
4. Erstelle einen Clienten und User in diesem Realm. Beim Clienten sollten im Reiter `Capability Config` die Optionen Client Authentication und Authorization an sein. Des Weiteren sollte die OAuth 2.0 Device Authorization Grant aktiviert sein.
5. Für die Postman Konfiguration notwendige Information sind Client Id, Client Secret, Username und sein Passwort und die Issuer URI zu finden unter `http://localhost:8080/realms/{{YOUR_REALM}}/.well-known/openid-configuration`.

### Postman

Es wird eine Postman Installation vorausgesetzt. Wenn dies erfüllt ist, fahren Sie mit den folgenden Schritten fort:

1. Öffnen Sie Postman.
2. Im Tab Collections wählen Sie über der Liste Import und wählen Sie die `/dev/TodoApi.postman_collection.json` aus.
3. Setzen Sie die für die Authentifikation notwendigen Umgebungsvariablen unter dem Tab Environments. Dann wählen Sie diese Umgebung für die importierte Collection aus.

Die benötigten Umgebungsvariablen sind:

| Name             | Beschreibung                                                                                                                                 |
|------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| ACCESS_TOKEN_URL | URL die benutzt wird, um den Token von KeyCloak anzufragen. `Bsp: http://localhost:8080/realms/{{YOUR_REALM}}/protocol/openid-connect/token` |
| CLIENT_ID        | ID des in KeyCloak erstellten Clients.                                                                                                       |
| CLIENT_SECRET    | Secret des in KeyCloak erstellten Clients.                                                                                                   |
| OAUTH_USERNAME   | Username des in KeyCloak erstellten Users.                                                                                                   |
| OAUTH_PASSWORD   | Username des in KeyCloak erstellten Users.                                                                                                   |

## Disclamer

Disclaimer: Das vorliegende GitHub-Projekt wurde aus meinem aktuellen Wissen erstellt. Obwohl ich alle Anstrengungen unternommen habe, um sicherzustellen, dass das Projekt korrekt und funktionsfähig ist, kann es dennoch vorkommen, dass ich Fehler oder ungünstige Entscheidungen getroffen habe. Des Weiteren enthält dieses Projekt keine Konfiguration, die so zu kommerziellen Zwecken laufen sollten. Beispielhaft benutzt die KeyCloak Instanz im Docker Container die Development Instanz und auch für die Konfiguration von SpringSecurity ist Fehlerfreiheit nicht gewährleistet. 