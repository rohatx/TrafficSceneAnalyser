/*
 * MIT License
 *
 * Copyright (c) 2024 RUB-SE-LAB-2024
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.example.trafficsceneanalyser.models;

import static android.provider.Settings.System.getString;

import java.io.InputStream;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageOutput {
    // Variablen für Bilddaten und JSON-Parsing-Schlüssel
    private String id;
    private InputStream imageStream;
    private String outputText;
    private Uri imageUri;
    private String markedImage;

    // Keys for JSON Parsing
    private String lmmOutputKey = "Ausgabe LLM";
    private String probabilityKey = "Berechnete Wahrscheinlichkeit";
    private String reasoningKey = "Begründung";
    private String sanityCheckKey = "Sanity Check";
    private String imageQualityKey = "Bild Qualität";

    // Variablen für die aus dem JSON geparsten Werte
    private String lmmOutput = "Waiting for server response...";
    private String probability;
    private String reasoning;
    private String sanityCheck;
    private String imageQuality;

    // Konstruktor zum Initialisieren der ImageOutput-Objekte
    public ImageOutput(String id, InputStream imageStream, String outputText, Uri imageUri, String markedImage) {
        this.id = id;
        this.imageStream = imageStream;
        this.outputText = outputText;
        this.imageUri = imageUri;
        this.markedImage = markedImage;
    }

    // Getter-Methoden für die verschiedenen Eigenschaften
    public String getId() {
        return id;
    }

    public InputStream getImageStream() {
        return imageStream;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getMarkedImage() {
        return markedImage;
    }

    public void setMarkedImage(String markedImage) {
        this.markedImage = markedImage;
    }

    public void setImageStream(InputStream imageStream) {
        this.imageStream = imageStream;
    }

    // Getter für die aus dem JSON geparsten Werte
    public String getLmmOutput() {
        return lmmOutput;
    }

    public String getProbability() {
        return probability;
    }

    public String getReasoning() {
        return reasoning;
    }

    public String getSanityCheck() {
        return sanityCheck;
    }

    public String getImageQuality() {
        return imageQuality;
    }

    // Methode zur Überprüfung, ob das Objekt unterfahrbar ist
    public int isUnderrideable() {
        if (lmmOutput.equals("false")) {
            return 0;
        } else if (lmmOutput.equals("true")) {
            return 1;
        } else {
            return -1;
        }
    }

    // Methode zum Parsen der JSON-Daten
    public void setOutputJSON() {
        try {
            JSONObject jsonObject = new JSONObject(outputText);
            lmmOutput = jsonObject.getString(lmmOutputKey);
            probability = jsonObject.getString(probabilityKey);
            reasoning = jsonObject.getString(reasoningKey);
            sanityCheck = jsonObject.getString(sanityCheckKey);
            imageQuality = jsonObject.getString(imageQualityKey);
        } catch (JSONException e) {
            Log.e("ImageOutput", "Error parsing JSON: " + e.getMessage());
        }

        // Wahrscheinlichkeit in Prozent umrechnen
        try {
            float floatValue = Float.parseFloat(probability);
            floatValue = floatValue * 100;
            int intValue = (int) floatValue;
            probability = intValue + "%";
        } catch (NumberFormatException e) {
            Log.e("ImageOutput", "Error parsing float value: " + e.getMessage());
        }
    }

    // Methoden zur Erstellung von Texten für die Anzeige
    public String getInfoText() {
        return "<b>Berechnung:</b> Das Objekt ist mit " + getProbability() + " unterfahrbar.";
    }

    public String getReasoningText() {
        String underride = " ";
        if (isUnderrideable() == 0) {
            underride = " nicht ";
        }
        String boilerplate = "<b>Laut LMM:</b> Das Objekt kann" + underride + "unterfahren werden.<br/><br/>";
        return boilerplate + "<b>LMM Begründung:</b> " + reasoning;
    }

    public String getSanityCheckText() {
        if (getSanityCheck().equals("Keine Probleme")) {
            return "<b>Sanity Check:</b> Keine Probleme.";
        } else {
            return "<b>Sanity Check: </b>" + getSanityCheck();
        }
    }

    public String getImageQualityText() {
        if (getImageQuality().equals("Keine Probleme")) {
            return "";
        } else {
            return "<u>Bildprobleme: " + getImageQuality() + "</u>";
        }
    }

    // Überprüfung, ob es Bildqualitätsprobleme gibt
    public boolean hasImageQualityIssues() {
        return !getImageQualityText().equals("");
    }
}
