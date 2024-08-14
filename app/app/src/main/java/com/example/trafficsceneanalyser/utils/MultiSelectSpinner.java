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

package com.example.trafficsceneanalyser.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertDialog;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// Diese Klasse implementiert einen Spinner (Dropdown), der mehrere Auswahlmöglichkeiten erlaubt
public class MultiSelectSpinner extends androidx.appcompat.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    private String[] _items = null; // Liste der auswählbaren Elemente
    private boolean[] _selection = null; // Auswahlinformationen
    private ArrayAdapter<String> _proxyAdapter; // Adapter für die Anzeige im Spinner
    private String _defaultText = "Select Models"; // Standardtext, wenn keine Auswahl getroffen wurde
    private MultiSelectSpinnerListener _listener; // Listener, der über Änderungen benachrichtigt wird

    public MultiSelectSpinner(Context context) {
        super(context);
        _proxyAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(_proxyAdapter);
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        _proxyAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(_proxyAdapter);
    }

    // Methode zum Setzen der Elemente im Spinner
    public void setItems(String[] items) {
        _items = items;
        _selection = new boolean[_items.length];
        _proxyAdapter.clear();
        _proxyAdapter.add(_defaultText);
        Arrays.fill(_selection, false); // Initialisieren aller Auswahlen mit false
    }

    // Methode zum Setzen des Listeners
    public void setListener(MultiSelectSpinnerListener listener) {
        _listener = listener;
    }

    @Override
    public boolean performClick() {
        // Erstellen und Anzeigen eines Dialogs zur Auswahl mehrerer Elemente
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Models");
        builder.setMultiChoiceItems(_items, _selection, this);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            _listener.onItemsSelected(getSelectedItems());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (_selection != null && which < _selection.length) {
            _selection[which] = isChecked; // Auswahl entsprechend setzen
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    // Methode zum Abrufen der ausgewählten Elemente
    public List<String> getSelectedItems() {
        List<String> selectedItems = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (_selection[i]) {
                selectedItems.add(_items[i]);
            }
        }
        return selectedItems;
    }

    // Schnittstelle für den Listener, der benachrichtigt wird, wenn Elemente ausgewählt wurden
    public interface MultiSelectSpinnerListener {
        void onItemsSelected(List<String> selectedItems);
    }
}
