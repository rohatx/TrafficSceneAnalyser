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

### Erstelle die notwendigen Certificates für HTTPS
```bash
mkdir certificates
openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout django-selfsigned.key -out certificates/django-selfsigned.crt
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

## Nutzung des Skripts zum Exportieren von Daten in eine CSV-Datei

Dieses Projekt enthält ein Skript zum Exportieren von Daten aus der Datenbank in eine CSV-Datei. Das Skript kann mit verschiedenen Parametern verwendet werden, um die exportierten Daten zu filtern.

### Parameter

- `-c`, `--calculated_probability`: (optional) Der Schwellenwert für die berechnete Wahrscheinlichkeit. Nur Einträge mit einer berechneten Wahrscheinlichkeit größer als dieser Wert werden exportiert.
- `-o`, `--output_file`: (erforderlich) Der Name der Ausgabedatei (CSV).
- `-i`, `--image_dir`: (erforderlich) Das Verzeichnis, in dem die Bilder gespeichert werden sollen.
- `-m`, `--model`: (optional) Filter nach Modelltyp.
- `-l`, `--llm_output`: (optional) Filter nach LLM-Ausgabe (0 oder 1).

### Beispiel

So verwenden Sie das Skript:

1. Stellen Sie sicher, dass Ihre virtuelle Umgebung aktiviert ist.

2. Führen Sie das Skript mit den gewünschten Parametern aus:

```bash
python export_to_csv.py -o output.csv -i images/ -c 0.5 -m "gpt-4o" -l 1
```

Dieser Befehl exportiert alle Datensätze, bei denen die calculated_probability größer als 0.5 ist, das Modell gpt-4o verwendet wurde und der LLM-Ausgang 1 ist, in eine Datei namens output.csv. Die base64-codierten Bilder werden im Verzeichnis images/ gespeichert und in der CSV-Datei als Pfade referenziert.

## Lizenz

Dieses Projekt ist unter der MIT-Lizenz lizenziert - siehe die [LICENSE](LICENSE) Datei für Details.
