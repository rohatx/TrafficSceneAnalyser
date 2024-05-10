# Projektname

Noch hinzuzufügen

## Voraussetzungen

Stellen Sie sicher, dass Python und Pip auf Ihrem System installiert sind, um dieses Projekt ausführen zu können.

## Einrichtung

Folgen Sie diesen Schritten, um das Projekt lokal einzurichten:

### Klonen des Repositories

Zuerst müssen Sie das Repository klonen:

```bash
git clone https://github.com/deinUsername/deinRepository.git
cd deinRepository
```

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

Jetzt können Sie den Entwicklungsserver starten:

```bash
python manage.py runserver
```

Der Server ist jetzt unter `http://127.0.0.1:8000/` erreichbar.

## Verwendung

Noch hinzuzufügen

## Lizenz

Dieses Projekt ist unter der MIT-Lizenz lizenziert - siehe die [LICENSE](LICENSE) Datei für Details.
