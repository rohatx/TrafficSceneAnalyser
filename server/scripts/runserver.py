"""
MIT License

Copyright (c) 2024 RUB-SE-LAB-2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
"""

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
