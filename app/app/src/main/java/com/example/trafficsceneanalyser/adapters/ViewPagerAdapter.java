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

package com.example.trafficsceneanalyser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trafficsceneanalyser.models.ImageSource;
import com.example.trafficsceneanalyser.R;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private Context context;
    private List<ImageSource> imageSources;
    private LayoutInflater layoutInflater;

    // Konstruktor zum Initialisieren der Adapterklasse
    public ViewPagerAdapter(Context context, List<ImageSource> imageSources) {
        this.context = context;
        this.imageSources = imageSources;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating des Layouts für die einzelnen Elemente
        View view = layoutInflater.inflate(R.layout.showimageslayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Binden der Bilddaten an die UI-Elemente im ViewHolder
        ImageSource imageSource = imageSources.get(position);
        if (imageSource.isUri()) {
            Glide.with(context)
                    .load(imageSource.getUri())
                    .fitCenter()
                    .into(holder.imageView); // Ladebild von URI
        } else if (imageSource.isUrl()) {
            Glide.with(context)
                    .load(imageSource.getUrl())
                    .fitCenter()
                    .into(holder.imageView); // Ladebild von URL
        }
    }

    @Override
    public int getItemCount() {
        return imageSources.size(); // Gibt die Größe der Bildquellenliste zurück
    }

    // ViewHolder-Klasse zur Verwaltung der UI-Elemente jedes Listenelements
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.UploadImage); // Initialisierung der ImageView
        }
    }
}
