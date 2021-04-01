package com.example.securify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.securify.R;
import com.example.securify.comparators.ActivityAscendingDomainNameComparator;
import com.example.securify.comparators.ActivityDescendingDomainNameComparator;
import com.example.securify.comparators.StatisticsAscendingCountComparator;
import com.example.securify.comparators.StatisticsAscendingListComparator;
import com.example.securify.comparators.StatisticsDescendingCountComparator;
import com.example.securify.comparators.StatisticsDescendingListComparator;
import com.example.securify.domain.TopDomainsInfo;
import com.example.securify.ui.volley.VolleySingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class StatisticsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> domainList;

    public StatisticsListAdapter(ArrayList<String> domainList, Context _context) {
        this.domainList = domainList;
        context = _context;
    }

    @Override
    public int getCount() {
        return domainList.size();
    }

    @Override
    public Object getItem(int position) {
        return domainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.statistics_row, parent, false);

        String domainString = domainList.get(position);
        TextView domainName = row.findViewById(R.id.statistics_domain_name);
        domainName.setText(domainList.get(position));

        TextView domainCount = row.findViewById(R.id.statistics_count);
        domainCount.setText(TopDomainsInfo.getInstance().getInfo(domainString).get(VolleySingleton.num_of_accesses));

        ImageView domainList = row.findViewById(R.id.statistics_list);

        if (TopDomainsInfo.getInstance().getInfo(domainString).get(VolleySingleton.listType).equals(VolleySingleton.Blacklist)) {
            domainList.setImageResource(R.drawable.ic_blacklist_icon);
            domainList.setColorFilter(context.getColor(R.color.main7));
        } else {
            domainList.setImageResource(R.drawable.ic_whitelist_icon);
            domainList.setColorFilter(context.getColor(R.color.main3));
        }

        return row;
    }

    public void sortDomainNameAscending() {
        Collections.sort(domainList, new ActivityAscendingDomainNameComparator());
        notifyDataSetChanged();
    }

    public void sortDomainNameDescending() {
        Collections.sort(domainList, new ActivityDescendingDomainNameComparator());
        notifyDataSetChanged();
    }

    public void sortCountAscending() {
        Collections.sort(domainList, new StatisticsAscendingCountComparator());
        notifyDataSetChanged();
    }

    public void sortCountDescending() {
        Collections.sort(domainList, new StatisticsDescendingCountComparator());
        notifyDataSetChanged();
    }

    public void sortListAscending() {
        Collections.sort(domainList, new StatisticsAscendingListComparator());
        notifyDataSetChanged();
    }

    public void sortListDescending() {
        Collections.sort(domainList, new StatisticsDescendingListComparator());
        notifyDataSetChanged();
    }


}
