package com.example.securify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.securify.R;
import com.example.securify.ui.volley.VolleySingleton;

import java.util.HashMap;

public class StatisticsListAdapter extends BaseAdapter {

    private HashMap<String, HashMap<String, Object>> mData;
    private String[] mKeys;
    private Context context;



    public StatisticsListAdapter(HashMap<String, HashMap<String, Object>> data, Context _context) {
        mData = data;
        mKeys = mData.keySet().toArray(new String[data.size()]);
        context = _context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String key = mKeys[position];


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.statistics_row, parent, false);

        TextView domainName = row.findViewById(R.id.statistics_domain_name);
        domainName.setText(mData.get(key).get(VolleySingleton.domainName).toString());

        TextView domainCount = row.findViewById(R.id.statistics_count);
        domainCount.setText(mData.get(key).get(VolleySingleton.num_of_accesses).toString());

        ImageView domainList = row.findViewById(R.id.statistics_list);

        if (mData.get(key).get(VolleySingleton.listType).toString().equals(VolleySingleton.Blacklist)) {
            domainList.setImageResource(R.drawable.ic_blacklist_icon);
        } else {
            domainList.setImageResource(R.drawable.ic_whitelist_icon);
        }

        return row;
    }
}
