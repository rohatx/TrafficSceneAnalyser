# Projektname

Noch hinzuzufügen

## Voraussetzungen

Stellen Sie sicher, dass Python und Pip auf Ihrem System installiert sind, um dieses Projekt ausführen zu können.

## Einrichtung

Folgen Sie diesen Schritten, um das Projekt lokal einzurichten:

### Erstellen und Aktivieren einer virtuellen Umgebung

Erstellen Sie eine virtuelle Umgebung, um die Abhängigkeiten isoliert vom Rest Ihres Systems zu verwalten:

```bash
python3 -m venv venv
source venv/bin/activate  # Unter Linux/MacOS
venv\Scripts\activate  # Unter Windows
```

### Installation der Abhängigkeiten

Installieren Sie alle erforderlichen Abhängigkeiten:

```bash
pip install -r requirements.txt
```

### Anwenden von Migrationen

Vor dem Starten der Anwendung müssen die Datenbank-Migrationen angewendet werden:

```bash
python manage.py migrate
```

### Starten des Entwicklungsservers
Zunächst muss .env angepasst werden. Es muss ein API key sowie ALLOWED_HOSTS exportiert werden.
```env
export OPENAI_APIKEY="Hier dein API Key"
export OPENROUTER_APIKEY="Hier dein API Key"
export SECRET_KEY = "Your Production Key"
export ALLOWED_HOSTS="Hier deine ip Adresse",localhost,127.0.0.1,[::1]
```
Jetzt können Sie den Entwicklungsserver starten:

```bash
python manage.py runserver 0.0.0.0:8000 #auf allen Netzwerk-Interfaces verfügbar
```

Der Server ist jetzt unter `http://127.0.0.1:8000/` erreichbar.
Um den Server aus der android app zu erreichen, fügen Sie Ihre IP-Adresse ALLOWED_HOSTS in src/settings.py hinzu

## Verwendung

Beispiel Post Anfrage: `http://127.0.0.1:8000/api/v1/picture-explain/`<br>
Body:
```json
{
    "_input": "Hier dein base64 encoded Bild",
    "model": "gpt-4o"
}
```

## Hinzufügen von LLaVa-Unterstützung (Keine Api Keys notwendig)
Um auch LLaVa nutzen zu können, muss dieses zunächst auf dem System installiert werden.
- Ein Tool zur Vereinfachung, welches hier verwendet wird ist [Ollama](https://ollama.com/)
- Aktuell ist Ollama nur für Linux und MacOS verfügbar. Unter WSL2 sollte es ebenfalls funktionieren.
- Nun muss noch unser verwendetes Modell gepullt werden `ollama pull llava:7b-v1.6`

## Lizenz

Dieses Projekt ist unter der MIT-Lizenz lizenziert - siehe die [LICENSE](LICENSE) Datei für Details.
