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

import sqlite3
import csv
import argparse
import os
import base64
import sys

# Funktion zum Abrufen und Verarbeiten von Daten aus der Datenbank basierend auf den gegebenen Filtern
def fetch_data(calculated_probability_threshold, model, llm_output, image_dir):
    # Wechseln in das Projekt-Root-Verzeichnis
    project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
    db_path = os.path.join(project_root, 'db.sqlite3')

    # Verbindung zur SQLite-Datenbank herstellen
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # SQL-Abfrage zum Abrufen der Daten
    query = """
    SELECT id, user_id, _output, model, latitude, longitude, calculated_probability, llm_output, marked_image
    FROM t_picture_analyser
    WHERE 1=1
    """
    params = []

    # Filter auf Basis des berechneten Wahrscheinlichkeitsschwellenwerts
    if calculated_probability_threshold is not None:
        query += " AND calculated_probability > ?"
        params.append(calculated_probability_threshold)

    # Filter auf Basis des Modells
    if model:
        query += " AND model = ?"
        params.append(model)

    # Filter auf Basis des LLM-Outputs
    if llm_output is not None:
        query += " AND llm_output = ?"
        params.append(llm_output)

    # Ausführen der Abfrage mit den angegebenen Parametern
    cursor.execute(query, params)
    rows = cursor.fetchall()

    # Erstellen des Verzeichnisses zum Speichern der Bilder, falls nicht vorhanden
    if not os.path.exists(image_dir):
        os.makedirs(image_dir)

    processed_rows = []
    for row in rows:
        id, user_id, _output, model, latitude, longitude, calculated_probability, llm_output, marked_image = row

        # Wenn ein markiertes Bild vorhanden ist, wird es im angegebenen Verzeichnis gespeichert
        if marked_image:
            image_filename = f"{id}_marked_image.png"
            image_path = os.path.join(image_dir, image_filename)
            
            with open(image_path, "wb") as image_file:
                image_file.write(base64.b64decode(marked_image))
            
            marked_image = image_path
        
        # Verarbeiten der Zeilen und Hinzufügen zur Ergebnisliste
        processed_rows.append((id, user_id, _output, model, latitude, longitude, calculated_probability, llm_output, marked_image))
    
    conn.close()  # Schließen der Datenbankverbindung
    return processed_rows  # Rückgabe der verarbeiteten Daten

# Funktion zum Schreiben der abgerufenen Daten in eine CSV-Datei
def write_to_csv(data, filename):
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        # Schreiben der CSV-Headerzeile
        writer.writerow(["ID", "User ID", "Output", "Model", "Latitude", "Longitude", "Calculated Probability", "LLM Output", "Marked Image Path"])
        writer.writerows(data)  # Schreiben der Datenzeilen

# Hauptfunktion zum Parsen von Argumenten und Ausführen der Exportfunktionen
def main():
    parser = argparse.ArgumentParser(description="Export data from the database to a CSV file.")
    parser.add_argument('-c', '--calculated_probability', type=float, help="The threshold for calculated probability.")
    parser.add_argument('-o', '--output_file', type=str, required=True, help="The output CSV file.")
    parser.add_argument('-i', '--image_dir', type=str, required=True, help="The directory to save images.")
    parser.add_argument('-m', '--model', type=str, help="Filter by model type.")
    parser.add_argument('-l', '--llm_output', type=int, choices=[0, 1], help="Filter by LLM output (0 or 1).")

    args = parser.parse_args()

    # Validierung des Wahrscheinlichkeitsschwellenwerts
    if args.calculated_probability is not None and (args.calculated_probability < 0 or args.calculated_probability > 1):
        print("Error: calculated_probability must be between 0 and 1.")
        sys.exit(1)

    # Abrufen der Daten und Schreiben in die CSV-Datei
    data = fetch_data(args.calculated_probability, args.model, args.llm_output, args.image_dir)
    write_to_csv(data, args.output_file)

# Ausführung der Hauptfunktion, wenn das Skript direkt ausgeführt wird
if __name__ == "__main__":
    main()
