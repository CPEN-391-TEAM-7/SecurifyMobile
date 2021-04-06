package com.example.securify.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.securify.domain.DomainInfo;
import com.example.securify.domain.DomainLists;
import com.example.securify.R;
import com.example.securify.domain.DomainMatcher;
import com.example.securify.model.User;
import com.example.securify.ui.volley.VolleyRequest;
import com.example.securify.ui.volley.VolleyResponseListener;
import com.example.securify.ui.volley.VolleySingleton;

import org.apache.commons.net.whois.WhoisClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapter class that displays blacklisted/whitelisted domains in BlackListFragment and WhiteListFragment respectively
 */
public class DomainListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private ArrayList<String> domainList;
    private HashMap<String, String> domainInfo;
    private boolean isWhiteList;

    private final String TAG = "DomainListAdapter";

    public DomainListAdapter (Context _context, ArrayList<String> dList, boolean _isWhiteList) {
        context = _context;
        domainList = dList;
        isWhiteList = _isWhiteList;
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
            convertView = layoutInflater.inflate(R.layout.domain_row, null);
        }

        TextView domainName = convertView.findViewById(R.id.domain_name);
        domainName.setText(domainList.get(groupPosition));

        // Set change domain button as appropriate (depending on which list the domain is initially in)
        Button changeDomainListButton = convertView.findViewById(R.id.change_domain_list_button);

        if (isWhiteList) {
            String BLACKLIST_DOMAIN = "Blacklist Domain";
            changeDomainListButton.setText(BLACKLIST_DOMAIN);
        } else {
            String WHITELIST_DOMAIN = "Whitelist Domain";
            changeDomainListButton.setText(WHITELIST_DOMAIN);
        }

        changeDomainListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain = domainList.get(groupPosition);
                domainList.remove(groupPosition);

                JSONObject putObject = new JSONObject();

                if (isWhiteList) {
                    try {
                        putObject.put(VolleySingleton.userID, User.getInstance().getUserID());
                        putObject.put(VolleySingleton.listType, VolleySingleton.Blacklist);
                        putObject.put(VolleySingleton.domainName, domain);
                        VolleyRequest.addRequest(context, VolleyRequest.PUT_LIST, "", "", putObject, new VolleyResponseListener() {
                            @Override
                            public void onError(Object response) {
                                Log.e(TAG, response.toString());
;                            }

                            @Override
                            public void onResponse(Object response) {
                                notifyDataSetChanged();
                            }
                        });

                        DomainLists.getInstance().removeFromWhiteList(domain);
                        DomainLists.getInstance().addToBlackList(domain);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    try {
                        putObject.put(VolleySingleton.userID, User.getInstance().getUserID());
                        putObject.put(VolleySingleton.listType, VolleySingleton.Whitelist);
                        putObject.put(VolleySingleton.domainName, domain);
                        VolleyRequest.addRequest(context, VolleyRequest.PUT_LIST, "", "", putObject, new VolleyResponseListener() {
                            @Override
                            public void onError(Object response) {
                                Log.e(TAG, response.toString());
                                ;                            }

                            @Override
                            public void onResponse(Object response) {
                                notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    DomainLists.getInstance().removeFromBlackList(domain);
                    DomainLists.getInstance().addToWhiteList(domain);
                }

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

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        domainInfo = DomainInfo.getInstance().getInfo(domainList.get(groupPosition));

        // Sets up WhoIs query if domain info is empty
        WhoisClient whoisClient = new WhoisClient();

        if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.domain_children, null);
        }

        String domainName = domainInfo.get(DomainInfo.DOMAIN_NAME);
        if (domainInfo.get(DomainInfo.REGISTRAR_DOMAIN_ID).equals("")) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        StringBuilder server = new StringBuilder("");

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
                        domainInfo.put(DomainInfo.DOMAIN_NAME, domainName);

                        String domainID = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_DOMAIN_ID).trim();

                        Log.i(TAG, "registrar domain id:" + domainID);
                        domainInfo.put(DomainInfo.REGISTRAR_DOMAIN_ID, domainID);

                        String registrarName = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_NAME).trim();

                        Log.i(TAG, "registrar name:" + registrarName);
                        domainInfo.put(DomainInfo.REGISTRAR_NAME, registrarName);

                        String registrarExpiryDate = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_EXPIRY_DATE).trim();

                        Log.i(TAG, "expiry date:" + registrarExpiryDate);
                        domainInfo.put(DomainInfo.REGISTRAR_EXPIRY_DATE, registrarExpiryDate);

                        DomainInfo.getInstance().addDomain(domainName, domainInfo);

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

        // Display domain
        TextView domainNameText = convertView.findViewById(R.id.domain_name_text);
        domainNameText.setText(domainInfo.get(DomainInfo.DOMAIN_NAME));

        TextView registryDomainIDText = convertView.findViewById(R.id.registry_domain_id_text);
        registryDomainIDText.setText(domainInfo.get(DomainInfo.REGISTRAR_DOMAIN_ID));

        TextView registrarNameText = convertView.findViewById(R.id.registrar_name_text);
        registrarNameText.setText(domainInfo.get(DomainInfo.REGISTRAR_NAME));

        TextView registrarExpirationDateText = convertView.findViewById(R.id.registrar_expiration_date_text);
        registrarExpirationDateText.setText(domainInfo.get(DomainInfo.REGISTRAR_EXPIRY_DATE));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}
