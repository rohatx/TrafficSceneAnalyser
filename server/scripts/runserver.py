# scripts/runserver.py
import subprocess
import sys
import signal
import os

# Wechseln zum Root-Verzeichnis des Projekts
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
os.chdir(project_root)

# Funktion zum Ausführen von Tests mit pytest
def run_tests():
    result = subprocess.run(['pytest'], capture_output=True, text=True)
    print(result.stdout)
    if result.returncode != 0:
        print("Tests failed, not starting the server.")
        sys.exit(result.returncode)

# Funktion zum Starten des Django-Servers mit SSL-Unterstützung
def start_server():
    process = subprocess.Popen([
        'python3', 'scripts/manage.py', 'runserver_plus',
        '--cert-file', 'certificates/django-selfsigned.crt',
        '--key-file', 'certificates/django-selfsigned.key', '0.0.0.0:8000'
    ], preexec_fn=os.setsid)
    return process

# Funktion zum Stoppen des Servers und zum sauberen Beenden des Prozesses
def stop_server(process):
    os.killpg(os.getpgid(process.pid), signal.SIGINT)
    process.wait()
    print("Server stopped gracefully.")

# Hauptfunktion, die die Tests ausführt und den Server startet
if __name__ == '__main__':
    run_tests()  # Tests ausführen
    server_process = start_server()  # Server starten

    try:
        server_process.wait()  # Warten auf das Ende des Serverprozesses
    except KeyboardInterrupt:
        print("\nShutting down server...")
        stop_server(server_process)  # Server stoppen bei KeyboardInterrupt
