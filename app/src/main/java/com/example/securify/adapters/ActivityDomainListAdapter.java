package com.example.securify.adapters;

import android.content.Context;
import android.util.Log;
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

import com.example.securify.comparators.ActivityListComparator;
import com.example.securify.domain.DomainInfo;
import com.example.securify.domain.DomainLists;
import com.example.securify.R;
import com.example.securify.comparators.AscendingDomainNameComparator;
import com.example.securify.comparators.AscendingTimeStampComparator;
import com.example.securify.comparators.DescendingDomainNameComparator;
import com.example.securify.comparators.DescendingTimeStampComparator;
import com.example.securify.domain.DomainMatcher;
import com.example.securify.model.User;
import com.example.securify.ui.volley.VolleyRequest;
import com.example.securify.ui.volley.VolleyResponseListener;
import com.example.securify.ui.volley.VolleySingleton;

import org.apache.commons.net.whois.WhoisClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 *  Adapter class for the list of domains displayed in ActivityFragment
 */
public class ActivityDomainListAdapter extends BaseExpandableListAdapter implements Filterable {
    
    private final Context context;
    private ArrayList<String> domainList;
    public final String DOMAIN_NAME_FILTER = "Domain Name";
    public final String START_DATE_FILTER = "Start Date";
    public final String START_TIME_FILTER = "Start Time";
    public final String END_DATE_FILTER = "End Date";
    public final String END_TIME_FILTER = "End Time";
    public final String LIST_FILTER = "List";
    public HashMap<String, String> constraints = new HashMap<>();
    private DomainFilter domainFilter;
    private final String TAG = "ACTIVITY_DOMAIN_LIST_ADAPTER";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");


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

            // Get appropriate image associated with a domain's list type
            if (blacklist.contains(domainList.get(groupPosition))) {
                listIndicator.setImageResource(R.drawable.ic_blacklist_icon);
                listIndicator.setColorFilter(context.getColor(R.color.main7));
            } else {
                if (whitelist.contains(domainList.get(groupPosition))) {
                    listIndicator.setImageResource(R.drawable.ic_whitelist_icon);
                    listIndicator.setColorFilter(context.getColor(R.color.main3));
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

        HashMap<String, String> domainInfo = DomainInfo.getInstance().getInfo(domainList.get(groupPosition));

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

                        JSONObject postData = new JSONObject();

                        try {
                            postData.put("userID", User.getInstance().getUserID());
                            postData.put(VolleySingleton.listType, VolleySingleton.Whitelist);
                            postData.put(VolleySingleton.domainName, domain);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        VolleyRequest.addRequest(context, VolleyRequest.PUT_LIST, User.getInstance().getUserID(), domain, postData, new VolleyResponseListener() {
                            @Override
                            public void onError(Object response) {
                                Log.i(TAG, response.toString());
                            }

                            @Override
                            public void onResponse(Object response) {
                                Log.i(TAG, response.toString());
                            }
                        });

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

                        JSONObject postData = new JSONObject();

                        try {
                            postData.put("userID", User.getInstance().getUserID());
                            postData.put(VolleySingleton.listType, VolleySingleton.Blacklist);
                            postData.put(VolleySingleton.domainName, domain);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        VolleyRequest.addRequest(context, VolleyRequest.PUT_LIST, User.getInstance().getUserID(), domain, postData, new VolleyResponseListener() {
                            @Override
                            public void onError(Object response) {
                                Log.i(TAG, response.toString());
                            }

                            @Override
                            public void onResponse(Object response) {
                                Log.i(TAG, response.toString());
                            }
                        });

                        DomainLists.getInstance().removeFromWhiteList(domain);
                        DomainLists.getInstance().addToBlackList(domain);
                        notifyDataSetChanged();
                    }
                }
            });

            // Sets up Whois query if domain info is empty
            WhoisClient whoisClient = new WhoisClient();

            if (domainInfo.get(DomainInfo.REGISTRAR_DOMAIN_ID).equals("")) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            StringBuilder server = new StringBuilder("");
                            String domainName = domainList.get(groupPosition);
                            whoisClient.connect("whois.iana.org");
                            server.append(whoisClient.query(domainName));
                            whoisClient.disconnect();

                            String whoIsServer = DomainMatcher.getMatch(server.toString(), DomainMatcher.WHOIS_SERVER).trim();
                            if (whoIsServer.equals("")) {
                                return;
                            }

                            Log.i(TAG,  whoIsServer);
                            whoisClient.connect(whoIsServer);
                            StringBuilder result = new StringBuilder("");
                            result.append(whoisClient.query(domainName));
                            Log.i(TAG,  result.toString());
                            String whoIsInfo = result.toString();

                            HashMap<String, String> domainInfo = DomainInfo.getInstance().getInfo(domainName);
                            String domainID = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_DOMAIN_ID).trim();

                            Log.i(TAG, "registrar domain id:" + domainID);
                            domainInfo.put(DomainInfo.REGISTRAR_DOMAIN_ID, domainID);

                            String registrarName = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_NAME).trim();

                            Log.i(TAG, "registrar name:" + registrarName);
                            domainInfo.put(DomainInfo.REGISTRAR_NAME, registrarName);

                            String registrarExpiryDate = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_EXPIRY_DATE).trim();

                            Log.i(TAG, "expiry date:" + registrarExpiryDate);
                            domainInfo.put(DomainInfo.REGISTRAR_EXPIRY_DATE, registrarExpiryDate);

                            notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // displays domain info to user
            TextView activityDomainNameText = convertView.findViewById(R.id.activity_domain_name_text);
            activityDomainNameText.setText(domainInfo.get(DomainInfo.DOMAIN_NAME));

            TextView activityRegistryDomainIDText = convertView.findViewById(R.id.activity_registry_domain_id_text);
            activityRegistryDomainIDText.setText(domainInfo.get(DomainInfo.REGISTRAR_DOMAIN_ID));

            TextView activityRegistrarNameText = convertView.findViewById(R.id.activity_registrar_name_text);
            activityRegistrarNameText.setText(domainInfo.get(DomainInfo.REGISTRAR_NAME));

            TextView activityRegistrarExpirationDateText = convertView.findViewById(R.id.activity_registrar_expiration_date_text);
            activityRegistrarExpirationDateText.setText(domainInfo.get(DomainInfo.REGISTRAR_EXPIRY_DATE));

            TextView activityDeviceIPText = convertView.findViewById(R.id.activity_device_ip_address_text);
            activityDeviceIPText.setText(domainInfo.get(DomainInfo.DEVICE_IP));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void sortDomainNameAscending() {
        domainList.sort(new AscendingDomainNameComparator());
        notifyDataSetChanged();
    }

    public void sortDomainNameDescending() {
        domainList.sort(new DescendingDomainNameComparator());
        notifyDataSetChanged();
    }

    public void sortTimeStampAscending() {
        domainList.sort(new AscendingTimeStampComparator());
        notifyDataSetChanged();
    }

    public void sortTimeStampDescending() {
        domainList.sort(new DescendingTimeStampComparator());
        notifyDataSetChanged();
    }

    public void sortList(int listPriority) {
        domainList.sort(new ActivityListComparator(listPriority));
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

            ArrayList<String> mDomainFilterList = DomainLists.getInstance().getActivityDomainsList();

            // Filters domains by name
            String nameConstraint = constraints.get(DOMAIN_NAME_FILTER);

            if (nameConstraint != null) {
                if (!nameConstraint.equals("")) {
                    for (String domain: mDomainFilterList) {
                        if (domain.contains(nameConstraint)) {
                            filteredDomainList.add(domain);
                        }
                    }
                } else {
                    filteredDomainList.addAll(mDomainFilterList);
                }
            }

            // Filters domains by list type
            String listConstraint = constraints.get(LIST_FILTER);
            DomainLists domainLists = DomainLists.getInstance();
            ArrayList<String> filteredDomainListIterate = new ArrayList<>(filteredDomainList);

            if (listConstraint != null) {
                switch (listConstraint) {
                    case "Blacklist Domains Only":
                        for (String domain:filteredDomainListIterate) {
                            if(!domainLists.blackListContains(domain)) {
                                filteredDomainList.remove(domain);
                            }
                        }
                        break;

                    case "Whitelist Domains Only":
                        for (String domain: filteredDomainListIterate) {
                            if(!domainLists.whiteListContains(domain)) {
                                filteredDomainList.remove(domain);
                            }
                        }
                        break;
                }
            }

            filteredDomainListIterate.clear();
            filteredDomainListIterate.addAll(filteredDomainList);

            // format starting date constraint
            String startDate = constraints.get(START_DATE_FILTER);
            Date startDateTime = null;

            if (startDate != null && !startDate.matches("")) {
                String startTime = constraints.get(START_TIME_FILTER);
                if (startTime != null) {
                    if (startTime.matches("")) {
                        try {
                            startDateTime = dateFormat.parse(startDate + "-" + "00:00:00");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            startDateTime = dateFormat.parse(startDate + "-" + startTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // format starting date constraint
            String endDate = constraints.get(END_DATE_FILTER);
            Date endDateTime = null;

            if (startDate != null && !startDate.matches("")) {

                String endTime = constraints.get(END_TIME_FILTER);
                if (endTime != null) {
                    if (endTime.matches("")) {
                        try {
                            endDateTime = dateFormat.parse(endDate + "-" + "00:00:00");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            endDateTime = dateFormat.parse(endDate + "-" + endTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            DomainInfo domainInformation = DomainInfo.getInstance();

            // filter domains by date
            for (String domain:filteredDomainListIterate) {

                Date timeStamp = null;

                try {
                    timeStamp = dateFormat.parse(Objects.requireNonNull(domainInformation.getInfo(domain).get(DomainInfo.DOMAIN_TIMESTAMP)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (timeStamp == null) {
                    Log.e(TAG, domain + "'s timestamp is null");
                    continue;
                }

                if (startDateTime != null) {

                    if (startDateTime.after(timeStamp)) {
                        filteredDomainList.remove(domain);
                        continue;
                    }

                }

                if (endDateTime != null) {

                    if (endDateTime.before(timeStamp)) {
                        filteredDomainList.remove(domain);
                    }

                }
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
