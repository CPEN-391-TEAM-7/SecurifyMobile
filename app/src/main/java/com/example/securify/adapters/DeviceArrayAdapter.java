package com.example.securify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.securify.R;

import java.util.ArrayList;

public class DeviceArrayAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> stringArray;
    private final int numRows = 500;
    private boolean [] RowValidity = new boolean[numRows];

    public DeviceArrayAdapter (Context _context, int textViewResourceId, ArrayList<String> _stringArray) {
        super(_context, textViewResourceId, _stringArray);
        context = _context;
        stringArray = _stringArray;
        clearValidity();
    }


    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row =  inflater.inflate(R.layout.bluetooth_row, parent, false);

        ImageView icon = row.findViewById(R.id.BTicon);
        icon.setImageResource(R.drawable.ic_bluetooth);
        icon.setVisibility(View.VISIBLE);

        TextView label = row.findViewById(R.id.BTDeviceText);
        label.setText(stringArray.get(position));

        icon = row.findViewById(R.id.selected);
        icon.setImageResource(R.drawable.ic_clear);
        icon.setVisibility(View.VISIBLE);

        if(!RowValidity[position])
            icon.setImageResource(R.drawable.ic_clear);
        else
            icon.setImageResource(R.drawable.ic_check);
        return row;
    }

    public void setValid(int position) {RowValidity[position] = true;}
    public void setInValid(int position) {RowValidity[position] = false;}
    public void clearValidity() {
        for (int i = 0; i < numRows; i++) {
            RowValidity[i] = false;
        }
    }
}
