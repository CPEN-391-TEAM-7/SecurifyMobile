package com.example.securify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.securify.R;
import com.example.securify.comparators.AscendingDomainNameComparator;
import com.example.securify.comparators.DescendingDomainNameComparator;
import com.example.securify.comparators.AscendingCountComparator;
import com.example.securify.comparators.DescendingCountComparator;
import com.example.securify.comparators.StatisticsListComparator;
import com.example.securify.domain.TopDomainsInfo;
import com.example.securify.ui.volley.VolleySingleton;

import java.util.ArrayList;
import java.util.Collections;

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
        }
        if (TopDomainsInfo.getInstance().getInfo(domainString).get(VolleySingleton.listType).equals(VolleySingleton.Whitelist)) {
            domainList.setImageResource(R.drawable.ic_whitelist_icon);
            domainList.setColorFilter(context.getColor(R.color.main3));
        }

        return row;
    }

    public void sortDomainNameAscending() {
        Collections.sort(domainList, new AscendingDomainNameComparator());
        notifyDataSetChanged();
    }

    public void sortDomainNameDescending() {
        Collections.sort(domainList, new DescendingDomainNameComparator());
        notifyDataSetChanged();
    }

    public void sortCountAscending() {
        Collections.sort(domainList, new AscendingCountComparator());
        notifyDataSetChanged();
    }

    public void sortCountDescending() {
        Collections.sort(domainList, new DescendingCountComparator());
        notifyDataSetChanged();
    }


    public void sortList(int listPriority) {
        Collections.sort(domainList, new StatisticsListComparator(listPriority));
        notifyDataSetChanged();
    }
}
