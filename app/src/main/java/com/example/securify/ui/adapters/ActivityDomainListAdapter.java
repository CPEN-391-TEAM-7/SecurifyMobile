package com.example.securify.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.securify.DomainInfo;
import com.example.securify.DomainLists;
import com.example.securify.R;
import com.example.securify.model.Domain;
import com.example.securify.ui.comparators.AscendingDomainNameComparator;
import com.example.securify.ui.comparators.AscendingListComparator;
import com.example.securify.ui.comparators.AscendingTimeStampComparator;
import com.example.securify.ui.comparators.DescendingDomainNameComparator;
import com.example.securify.ui.comparators.DescendingListComparator;
import com.example.securify.ui.comparators.DescendingTimeStampComparator;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActivityDomainListAdapter extends BaseExpandableListAdapter implements Filterable {
    private Context context;
    private ArrayList<String> domainList;
    private ArrayList<String> mDomainFilterList;
    private HashMap<String, String> domainInfo;
    private final int numRows = 500;
    public final String DOMAIN_NAME_FILTER = "Domain Name";
    public final String START_DATE_FILTER = "Start Date";
    public final String START_TIME_FILTER = "Start Time";
    public final String END_DATE_FILTER = "End Date";
    public final String END_TIME_FILTER = "End Time";
    public final String LIST_FILTER = "List";
    public HashMap<String, String> constraints = new HashMap<>();
    private ArrayList<String> filteredList =  new ArrayList<>();
    private DomainFilter domainFilter;

    public ActivityDomainListAdapter(Context _context, ArrayList<String> dList) {
        context = _context;
        domainList = dList;
        getFilter();
    }

    @Override
    public int getGroupCount() {
        return domainList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.activity_domain_row, null);
            }

            TextView activityDomainName = convertView.findViewById(R.id.activity_domain_name);
            activityDomainName.setText(domainList.get(groupPosition));

            TextView activityDomainTimeStamp = convertView.findViewById(R.id.activity_domain_timestamp);
            activityDomainTimeStamp.setText(DomainInfo.getInstance().getInfo(domainList.get(groupPosition)).get(DomainInfo.DOMAIN_TIMESTAMP));

            DomainLists domainLists = DomainLists.getInstance();
            ArrayList<String> blacklist = domainLists.getBlackList();
            ArrayList<String> whitelist = domainLists.getWhiteList();

            ImageView listIndicator = convertView.findViewById(R.id.list_indicator);

            if (blacklist.contains(domainList.get(groupPosition))) {
                listIndicator.setImageResource(R.drawable.ic_blacklist_icon);
            } else {
                if (whitelist.contains(domainList.get(groupPosition))) {
                    listIndicator.setImageResource(R.drawable.ic_whitelist_icon);
                } else {
                    listIndicator.setImageResource(0);
                }
            }

            ImageButton activityDomainInfoButton = convertView.findViewById(R.id.activity_domain_info_button);
            activityDomainInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isExpanded) {
                        ((ExpandableListView) parent).collapseGroup(groupPosition);
                        activityDomainInfoButton.setImageResource(R.drawable.ic_drop_down);
                    }
                    else {
                        ((ExpandableListView) parent).expandGroup(groupPosition, true);
                        activityDomainInfoButton.setImageResource(R.drawable.ic_up);
                    }
                }
            });

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        domainInfo = DomainInfo.getInstance().getInfo(domainList.get(groupPosition));

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.activity_domain_children, null);
            }

            Button activityWhiteListButton = convertView.findViewById(R.id.activity_whitelist_button);
            activityWhiteListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String domain = domainList.get(groupPosition);
                    if (!DomainLists.getInstance().whiteListContains(domain)) {
                        DomainLists.getInstance().removeFromBlackList(domain);
                        DomainLists.getInstance().addToWhiteList(domain);
                        notifyDataSetChanged();
                    }
                }
            });


            Button activityBlackListButton = convertView.findViewById(R.id.activity_blacklist_button);
            activityBlackListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String domain = domainList.get(groupPosition);
                    if (!DomainLists.getInstance().blackListContains(domain)) {
                        DomainLists.getInstance().removeFromWhiteList(domain);
                        DomainLists.getInstance().addToBlackList(domain);
                        notifyDataSetChanged();
                    }
                }
            });

            TextView activityDomainNameText = convertView.findViewById(R.id.activity_domain_name_text);
            activityDomainNameText.setText(domainInfo.get(DomainInfo.DOMAIN_NAME));
            TextView activityRegistryDomainIDText = convertView.findViewById(R.id.activity_registry_domain_id_text);
            activityRegistryDomainIDText.setText(domainInfo.get(DomainInfo.REGISTRAR_DOMAIN_ID));
            TextView activityRegistrarNameText = convertView.findViewById(R.id.activity_registrar_name_text);
            activityRegistrarNameText.setText(domainInfo.get(DomainInfo.REGISTRAR_NAME));
            TextView activityRegistrarExpirationDateText = convertView.findViewById(R.id.activity_registrar_expiration_date_text);
            activityRegistrarExpirationDateText.setText(domainInfo.get(DomainInfo.REGISTRAR_EXPIRY_DATE));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void sortDomainNameAscending() {
        Collections.sort(domainList, new AscendingDomainNameComparator());
        notifyDataSetChanged();
    }

    public void sortDomainNameDescending() {
        Collections.sort(domainList, new DescendingDomainNameComparator());
        notifyDataSetChanged();
    }

    public void sortTimeStampAscending() {
        Collections.sort(domainList, new AscendingTimeStampComparator());
        notifyDataSetChanged();
    }

    public void sortTimeStampDescending() {
        Collections.sort(domainList, new DescendingTimeStampComparator());
        notifyDataSetChanged();
    }

    public void sortListAscending() {
        Collections.sort(domainList, new AscendingListComparator());
        notifyDataSetChanged();
    }

    public void sortListDescending() {
        Collections.sort(domainList, new DescendingListComparator());
        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        if (domainFilter == null) {
            domainFilter = new DomainFilter();
        }

        return domainFilter;
    }

    private class DomainFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            ArrayList<String> filteredDomainList = new ArrayList<>();

            mDomainFilterList = DomainLists.getInstance().getAllDomainsList();

            String nameConstraint = constraints.get(DOMAIN_NAME_FILTER);

            if (!nameConstraint.equals("")) {
                for (String domain:mDomainFilterList) {
                    if (domain.contains(nameConstraint)) {
                        filteredDomainList.add(domain);
                    }
                }
            } else {
                filteredDomainList.addAll(mDomainFilterList);
            }

            String listConstraint = constraints.get(LIST_FILTER);
            DomainLists domainLists = DomainLists.getInstance();

            // TODO: add filtering for dates
            switch (listConstraint) {
                case "Blacklist Domains Only":
                    for (String domain: filteredDomainList) {
                        if(!domainLists.blackListContains(domain)) {
                            filteredDomainList.remove(domain);
                        }
                    }
                    break;

                case "Whitelist Domains Only":
                    for (String domain: filteredDomainList) {
                        if(!domainLists.whiteListContains(domain)) {
                            filteredDomainList.remove(domain);
                        }
                    }
                    break;
            }

            results.count = filteredDomainList.size();
            results.values = filteredDomainList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values == null) {
                domainList = new ArrayList<>();
            } else {
                domainList = (ArrayList<String>) results.values;
            }
            notifyDataSetChanged();
        }
    }
}
