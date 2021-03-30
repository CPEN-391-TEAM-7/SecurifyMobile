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
import com.example.securify.model.User;
import com.example.securify.ui.volley.VolleyRequest;
import com.example.securify.ui.volley.VolleyResponseListener;
import com.example.securify.ui.volley.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DomainListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> domainList;
    private HashMap<String, String> domainInfo;
    private final int numRows = 500;
    private boolean isWhiteList;
    private boolean isActivityFragment;
    private final String BLACKLIST_DOMAIN = "Blacklist Domain";
    private final String WHITELIST_DOMAIN = "Whitelist Domain";

    private final String TAG = "DomainListAdapter";

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

                JSONObject putObject = new JSONObject();

                if (isWhiteList) {
                    try {
                        putObject.put(VolleySingleton.userID, User.getInstance().getUserID());
                        putObject.put(VolleySingleton.listType, VolleySingleton.Blacklist);
                        putObject.put(VolleySingleton.domainName, domain);
                        VolleyRequest.addRequest(context, VolleyRequest.PUT_LIST, "", "", "", putObject, new VolleyResponseListener() {
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
                        VolleyRequest.addRequest(context, VolleyRequest.PUT_LIST, "", "", "", putObject, new VolleyResponseListener() {
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

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}
