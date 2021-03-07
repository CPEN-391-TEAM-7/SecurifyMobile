package com.example.securify.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
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

public class DomainListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> domainList;
    private HashMap<String, String> domainInfo;
    private final int numRows = 500;
    private boolean isWhiteList;
    private boolean isActivityFragment;
    private final String BLACKLIST_DOMAIN = "Blacklist Domain";
    private final String WHITELIST_DOMAIN = "Whitelist Domain";


    public DomainListAdapter (Context _context, ArrayList<String> dList, boolean _isWhiteList, boolean _isActivityFragment) {
        context = _context;
        domainList = dList;
        isWhiteList = _isWhiteList;
        isActivityFragment = _isActivityFragment;
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


    if (this.isActivityFragment) {
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

    } else {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.domain_row, null);
        }

        TextView domainName = convertView.findViewById(R.id.domain_name);
        domainName.setText(domainList.get(groupPosition));

        Button changeDomainListButton = convertView.findViewById(R.id.change_domain_list_button);

        if (isWhiteList) {
            changeDomainListButton.setText(BLACKLIST_DOMAIN);
        } else {
            changeDomainListButton.setText(WHITELIST_DOMAIN);
        }

        changeDomainListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain = domainList.get(groupPosition);
                domainList.remove(groupPosition);

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

        ImageButton domainInfoButton = convertView.findViewById(R.id.domain_info_button);
        domainInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpanded) {
                    ((ExpandableListView) parent).collapseGroup(groupPosition);
                    domainInfoButton.setImageResource(R.drawable.ic_drop_down);
                }
                else {
                    ((ExpandableListView) parent).expandGroup(groupPosition, true);
                    domainInfoButton.setImageResource(R.drawable.ic_up);
                }
            }
        });
    }
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        domainInfo = DomainInfo.getInstance().getInfo(domainList.get(groupPosition));

        if (this.isActivityFragment) {

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.activity_domain_children, null);
            }

            Button activityWhiteListButton = convertView.findViewById(R.id.activity_whitelist_button);
            activityWhiteListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String domain = domainList.get(groupPosition);
                    DomainLists.getInstance().removeFromBlackList(domain);
                    DomainLists.getInstance().addToWhiteList(domain);
                    notifyDataSetChanged();
                }
            });


            Button activityBlackListButton = convertView.findViewById(R.id.activity_blacklist_button);
            activityBlackListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String domain = domainList.get(groupPosition);
                    DomainLists.getInstance().removeFromWhiteList(domain);
                    DomainLists.getInstance().addToBlackList(domain);
                    notifyDataSetChanged();
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

        } else {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.domain_children, null);
            }

            TextView domainNameText = convertView.findViewById(R.id.domain_name_text);
            domainNameText.setText(domainInfo.get(DomainInfo.DOMAIN_NAME));
            TextView registryDomainIDText = convertView.findViewById(R.id.registry_domain_id_text);
            registryDomainIDText.setText(domainInfo.get(DomainInfo.REGISTRAR_DOMAIN_ID));
            TextView registrarNameText = convertView.findViewById(R.id.registrar_name_text);
            registrarNameText.setText(domainInfo.get(DomainInfo.REGISTRAR_NAME));
            TextView registrarExpirationDateText = convertView.findViewById(R.id.registrar_expiration_date_text);
            registrarExpirationDateText.setText(domainInfo.get(DomainInfo.REGISTRAR_EXPIRY_DATE));
        }


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
}
