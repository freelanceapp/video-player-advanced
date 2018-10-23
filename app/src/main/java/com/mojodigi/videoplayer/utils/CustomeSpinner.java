package com.mojodigi.videoplayer.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.mojodigi.videoplayer.R;

public class CustomeSpinner extends ArrayAdapter<String> {
    private List<String> objects;

    public CustomeSpinner(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_spinner, parent, false);
        final TextView label =  row.findViewById(R.id.tv_spinnervalue);
//        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "corbel.ttf");
        label.setTypeface(Helper.typeFace_adobe_caslonpro_Regular(getContext()));


        label.setText(objects.get(position));
        return row;
    }
}