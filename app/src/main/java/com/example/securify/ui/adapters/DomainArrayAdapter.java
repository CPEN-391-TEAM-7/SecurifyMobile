package com.example.securify.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.example.securify.DomainLists;
import com.example.securify.R;

import java.util.ArrayList;

public class DomainArrayAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> stringArray;
    private final int numRows = 500;
    private boolean isWhiteList;
    private final String BLACKLIST_DOMAIN = "Blacklist Domain";
    private final String WHITELIST_DOMAIN = "Whitelist Domain";

    public DomainArrayAdapter (Context _context, int textViewResourceId, ArrayList<String> _stringArray, boolean _isWhiteList) {
        super(_context, textViewResourceId, _stringArray);
        context = _context;
        stringArray = _stringArray;
        isWhiteList = _isWhiteList;
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row =  inflater.inflate(R.layout.domain_row, parent, false);

        TextView domainName = row.findViewById(R.id.domain_name);
        domainName.setText(stringArray.get(position));

        Button changeDomainListButton = row.findViewById(R.id.change_domain_list_button);

        if (isWhiteList) {
            changeDomainListButton.setText(BLACKLIST_DOMAIN);
        } else {
            changeDomainListButton.setText(WHITELIST_DOMAIN);
        }

        changeDomainListButton.setOnClickListener(new View.OnClickListener() {
            String domain;
            @Override
            public void onClick(View v) {
                domain = stringArray.get(position);
                stringArray.remove(position);

                if (isWhiteList) {
                    DomainLists.getInstance().removeFromWhiteList(domain);
                    DomainLists.getInstance().addToBlackList(domain);
                } else {
                    DomainLists.getInstance().removeFromBlackList(domain);
                    DomainLists.getInstance().addToWhiteList(domain);
                }

                notifyDataSetChanged();


            }
        });

        return row;
    }

}
