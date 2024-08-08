# scripts/runserver.py
import subprocess
import sys
import signal
import os

# Change to the root directory of the project
project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
os.chdir(project_root)

def run_tests():
    result = subprocess.run(['pytest'], capture_output=True, text=True)
    print(result.stdout)
    if result.returncode != 0:
        print("Tests failed, not starting the server.")
        sys.exit(result.returncode)

def start_server():
    process = subprocess.Popen([
        'python3', 'manage.py', 'runserver_plus',
        '--cert-file', 'certificates/django-selfsigned.crt',
        '--key-file', 'certificates/django-selfsigned.key', '0.0.0.0:8000'
    ], preexec_fn=os.setsid)
    return process

def stop_server(process):
    os.killpg(os.getpgid(process.pid), signal.SIGINT)
    process.wait()
    print("Server stopped gracefully.")

if __name__ == '__main__':
    run_tests()
    server_process = start_server()

    try:
        server_process.wait()
    except KeyboardInterrupt:
        print("\nShutting down server...")
        stop_server(server_process)
