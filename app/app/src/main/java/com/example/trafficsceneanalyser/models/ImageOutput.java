package com.example.trafficsceneanalyser.models;

import static android.provider.Settings.System.getString;

import java.io.InputStream;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageOutput {
    private String id;
    private InputStream imageStream;
    private String outputText;
    private Uri imageUri;
    private String markedImage;
    // Keys for the json Parcing
    private String lmmOutputKey = "Ausgabe LLM";
    private String probabilityKey = "Berechnete Wahrscheinlichkeit";
    private String reasoningKey = "Begründung";
    private String sanityCheckKey = "Sanity Check";
    private String imageQualityKey = "Bild Qualität";
    // Strings for the json Outputs
    private String lmmOutput = "Waiting for server response...";
    private String probability;
    private String reasoning;
    private String sanityCheck;
    private String imageQuality;

    public ImageOutput(String id, InputStream imageStream, String outputText, Uri imageUri, String markedImage) {
        this.id = id;
        this.imageStream = imageStream;
        this.outputText = outputText;
        this.imageUri = imageUri;
        this.markedImage = markedImage;
    }

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

    //Getter for all the json values
    public String getLmmOutput() {
        return lmmOutput;
    }
    public String getProbability() {
        return probability;
    }
    public String getReasoning() { return reasoning; }
    public String getSanityCheck() {
        return sanityCheck;
    }
    public String getImageQuality() {
        return imageQuality;
    }
    public int isUnderrideable() {
        if (lmmOutput.equals("false")) {
            return 0;
        } else if (lmmOutput.equals("true")) {
            return 1;
        } else {
            return -1;
        }
    }

    // if the output returns the json then begin parsing
    public void setOutputJSON() {
        try{
            JSONObject jsonObject = new JSONObject(outputText);
            lmmOutput = jsonObject.getString(lmmOutputKey);
            probability = jsonObject.getString(probabilityKey);
            reasoning = jsonObject.getString(reasoningKey);
            sanityCheck = jsonObject.getString(sanityCheckKey);
            imageQuality = jsonObject.getString(imageQualityKey);
        } catch (JSONException e) {
            Log.e("ImageOutput", "Error parsing JSON: " + e.getMessage());
        }

        try {
            float floatValue = Float.parseFloat(probability);
            floatValue = floatValue*100;
            int intValue = (int) floatValue;
            probability = intValue + "%";
        } catch (NumberFormatException e) {
            Log.e("ImageOutput", "Error parsing float value: " + e.getMessage());
        }
    }

    // TODO: Set string values in xml

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
    } // add isUnderrideable ?

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

    public boolean hasImageQualityIssues() {
        if (getImageQualityText().equals("")) {
            return false;
        } else { return true;}
    }
}
