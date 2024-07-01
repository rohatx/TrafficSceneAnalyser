import subprocess
import sys
import signal

def run_tests():
    result = subprocess.run(['pytest'], capture_output=True, text=True)
    print(result.stdout)
    if result.returncode != 0:
        print("Tests failed, not starting the server.")
        sys.exit(result.returncode)

def start_server():
    process = subprocess.Popen(['python3', 'manage.py', 'runserver', '0.0.0.0:8000'])
    return process

if __name__ == '__main__':
    run_tests()
    server_process = start_server()

    try:
        server_process.wait()
    except KeyboardInterrupt:
        print("\nShutting down server...")
        server_process.send_signal(signal.SIGINT)
        server_process.wait()
        print("Server stopped gracefully.")
