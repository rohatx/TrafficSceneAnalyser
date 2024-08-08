import sqlite3
import csv
import argparse
import os
import base64
import sys

def fetch_data(calculated_probability_threshold, model, llm_output, image_dir):
    # Change to the project root directory
    project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
    db_path = os.path.join(project_root, 'db.sqlite3')

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    query = """
    SELECT id, user_id, _output, model, latitude, longitude, calculated_probability, llm_output, marked_image
    FROM t_picture_analyser
    WHERE 1=1
    """
    params = []

    if calculated_probability_threshold is not None:
        query += " AND calculated_probability > ?"
        params.append(calculated_probability_threshold)

    if model:
        query += " AND model = ?"
        params.append(model)

    if llm_output is not None:
        query += " AND llm_output = ?"
        params.append(llm_output)

    cursor.execute(query, params)
    rows = cursor.fetchall()

    if not os.path.exists(image_dir):
        os.makedirs(image_dir)

    processed_rows = []
    for row in rows:
        id, user_id, _output, model, latitude, longitude, calculated_probability, llm_output, marked_image = row

        if marked_image:
            image_filename = f"{id}_marked_image.png"
            image_path = os.path.join(image_dir, image_filename)
            
            with open(image_path, "wb") as image_file:
                image_file.write(base64.b64decode(marked_image))
            
            marked_image = image_path
        
        processed_rows.append((id, user_id, _output, model, latitude, longitude, calculated_probability, llm_output, marked_image))
    
    conn.close()
    return processed_rows

def write_to_csv(data, filename):
    with open(filename, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(["ID", "User ID", "Output", "Model", "Latitude", "Longitude", "Calculated Probability", "LLM Output", "Marked Image Path"])
        writer.writerows(data)

def main():
    parser = argparse.ArgumentParser(description="Export data from the database to a CSV file.")
    parser.add_argument('-c', '--calculated_probability', type=float, help="The threshold for calculated probability.")
    parser.add_argument('-o', '--output_file', type=str, required=True, help="The output CSV file.")
    parser.add_argument('-i', '--image_dir', type=str, required=True, help="The directory to save images.")
    parser.add_argument('-m', '--model', type=str, help="Filter by model type.")
    parser.add_argument('-l', '--llm_output', type=int, choices=[0, 1], help="Filter by LLM output (0 or 1).")

    args = parser.parse_args()

    if args.calculated_probability is not None and (args.calculated_probability < 0 or args.calculated_probability > 1):
        print("Error: calculated_probability must be between 0 and 1.")
        sys.exit(1)

    data = fetch_data(args.calculated_probability, args.model, args.llm_output, args.image_dir)
    write_to_csv(data, args.output_file)

if __name__ == "__main__":
    main()
