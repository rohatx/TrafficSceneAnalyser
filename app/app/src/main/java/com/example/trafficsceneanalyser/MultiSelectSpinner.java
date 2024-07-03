package com.example.trafficsceneanalyser;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertDialog;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MultiSelectSpinner extends androidx.appcompat.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    private String[] _items = null;
    private boolean[] _selection = null;
    private ArrayAdapter<String> _proxyAdapter;
    private String _defaultText = "Select Models";
    private MultiSelectSpinnerListener _listener;

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

    public void setItems(String[] items) {
        _items = items;
        _selection = new boolean[_items.length];
        _proxyAdapter.clear();
        _proxyAdapter.add(_defaultText);
        Arrays.fill(_selection, false);
    }

    public void setListener(MultiSelectSpinnerListener listener) {
        _listener = listener;
    }

    @Override
    public boolean performClick() {
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
            _selection[which] = isChecked;
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    public List<String> getSelectedItems() {
        List<String> selectedItems = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (_selection[i]) {
                selectedItems.add(_items[i]);
            }
        }
        return selectedItems;
    }

    public interface MultiSelectSpinnerListener {
        void onItemsSelected(List<String> selectedItems);
    }
}
