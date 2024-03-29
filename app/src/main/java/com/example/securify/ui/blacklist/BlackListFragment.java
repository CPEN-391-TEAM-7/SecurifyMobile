package com.example.securify.ui.blacklist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.securify.domain.DomainInfo;
import com.example.securify.domain.DomainLists;
import com.example.securify.domain.DomainMatcher;
import com.example.securify.R;
import com.example.securify.adapters.DomainListAdapter;
import com.example.securify.model.User;
import com.example.securify.ui.volley.VolleyRequest;
import com.example.securify.ui.volley.VolleyResponseListener;
import com.example.securify.ui.volley.VolleySingleton;

import org.apache.commons.net.whois.WhoisClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BlackListFragment extends Fragment {

    private BlackListViewModel blackListViewModel;
    private ArrayList<String> blackList;
    private DomainListAdapter blackListArrayAdapter;

    private WhoisClient whoisClient;
    private Boolean validDomain = true;
    private final String TAG = "BlackListFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        blackListViewModel =
                new ViewModelProvider(this).get(BlackListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_blacklist, container, false);
        final TextView textView = root.findViewById(R.id.text_blacklist);
        blackListViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // Fetch blacklist from DomainLists singleton
        blackList = DomainLists.getInstance().getBlackList();
        blackListArrayAdapter = new DomainListAdapter(getContext(), blackList, false);
        ExpandableListView blackListDomains = root.findViewById(R.id.blacklist_domains);
        blackListDomains.setAdapter(blackListArrayAdapter);
        blackListDomains.setGroupIndicator(null);


        EditText addBlackList = root.findViewById(R.id.add_blacklist_text);

        whoisClient = new WhoisClient();

        // Collect the whitelist of the user in the DataBase. (HTTP CALL)
        clearList();
        fetchBlacklist();

        Button addBlackListDomain = root.findViewById(R.id.add_blacklist_domain_button);
        addBlackListDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain_to_add = addBlackList.getText().toString().toLowerCase();
                if (blackList.contains(domain_to_add)) {
                    Toast.makeText(getContext(), "Domain already in whitelist", Toast.LENGTH_LONG).show();
                    addBlackList.getText().clear();
                    return;
                }
                if (DomainLists.getInstance().whiteListContains(domain_to_add)) {

                    DomainLists.getInstance().removeFromWhiteList(domain_to_add);
                } else {

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                StringBuilder server = new StringBuilder("");

                                whoisClient.connect("whois.iana.org");
                                server.append(whoisClient.query(domain_to_add));
                                whoisClient.disconnect();

                                String whoIsServer = DomainMatcher.getMatch(server.toString(), DomainMatcher.WHOIS_SERVER).trim();
                                if (whoIsServer.equals("")) {
                                    validDomain = false;
                                    return;
                                }

                                Log.i(TAG,  whoIsServer);
                                whoisClient.connect(whoIsServer);
                                StringBuilder result = new StringBuilder("");
                                result.append(whoisClient.query(domain_to_add));
                                Log.i(TAG,  result.toString());
                                String whoIsInfo = result.toString();

                                HashMap<String, String> domainInfo = new HashMap<>();
                                domainInfo.put(DomainInfo.DOMAIN_NAME, domain_to_add);

                                String domainID = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_DOMAIN_ID).trim();

                                Log.i(TAG, "registrar domain id:" + domainID);
                                domainInfo.put(DomainInfo.REGISTRAR_DOMAIN_ID, domainID);

                                String registrarName = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_NAME).trim();

                                Log.i(TAG, "registrar name:" + registrarName);
                                domainInfo.put(DomainInfo.REGISTRAR_NAME, registrarName);

                                String registrarExpiryDate = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_EXPIRY_DATE).trim();

                                Log.i(TAG, "expiry date:" + registrarExpiryDate);
                                domainInfo.put(DomainInfo.REGISTRAR_EXPIRY_DATE, registrarExpiryDate);

                                DomainInfo.getInstance().addDomain(domain_to_add, domainInfo);
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
                
                addBlackList.getText().clear();
                if (validDomain) {
                    addBlacklist(domain_to_add);
                    Collections.sort(blackList);
                    blackListArrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Invalid Domain", Toast.LENGTH_LONG).show();
                    validDomain = true;
                }

            }
        });

        return root;
    }

    /**
     * Collects the whitelisted domains from the Backend API.
     */
    private void fetchBlacklist() {

        // Build request body
        JSONObject postData = new JSONObject();

        try {
            postData.put("userID", User.getInstance().getUserID());
            postData.put("listType", "Blacklist");
        } catch (JSONException e){
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }


        VolleyRequest.addRequest(
                getContext(),
                VolleyRequest.GET_BLACKLIST,
                User.getInstance().getUserID(),
                "",
                postData,
                new VolleyResponseListener() {
                    @Override
                    public void onError(Object response) {
                        Log.e(TAG, response.toString());
                    }

                    @Override
                    public void onResponse(Object response) {
                        try {
                            JSONObject json = new JSONObject(response.toString());

                            if(json.getString("status").equals("Success")) {
                                JSONArray list = json.getJSONArray("list");

                                for(int i = 0; i < list.length(); i++) {
                                    Log.i(TAG, "Adding a domain to the list...");
                                    JSONObject domain = list.getJSONObject(i);
                                    addListAdapter(domain.getString("domainName"));
                                }
                            } else {
                                Log.e(TAG, "HTTP REQUEST return Failed...: " + json.getString("msg"));
                            }

                            Collections.sort(blackList);
                            blackListArrayAdapter.notifyDataSetChanged();
                        } catch (JSONException e){
                            e.printStackTrace();
                            Log.e(TAG, e.toString());
                        }
                    }
                }
        );
    }

    /**
     * makes a HTTP Request to the Backend API adding the domain name to the BlackList.
     * @param domainName name of domain added to BlackList
     */
    private void addBlacklist(String domainName) {

        // This is the request body.
        JSONObject postData = new JSONObject();

        try {
            postData.put("userID", User.getInstance().getUserID());
            postData.put(VolleySingleton.listType, VolleySingleton.Blacklist);
            postData.put(VolleySingleton.domainName, domainName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Sending the request...
        VolleyRequest.addRequest(getContext(),
                VolleyRequest.PUT_LIST,
                User.getInstance().getUserID(),
                domainName,
                postData,
                new VolleyResponseListener() {
                    @Override
                    public void onError(Object response) {
                        Log.i(TAG, response.toString());
                    }

                    @Override
                    public void onResponse(Object response) {
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject json = new JSONObject(response.toString());

                            // Something went wrong
                            if(!json.getString("status").equals("Success")) {
                                Toast.makeText(getContext(), json.getString("msg"), Toast.LENGTH_LONG).show();
                            }
                            // Successfully added.
                            else {

                                Log.i(TAG, "Successfully added the domain to Blacklist");

                                Toast.makeText(getContext(), json.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e){
                            Log.d(TAG, e.toString());
                        }
                    }
        });

        clearList();
        fetchBlacklist();
    }

    /**
     * Clears the elements (Adapters) in the List
     */
    private void clearList() {
        blackList.clear();
    }

    /**
     * Adds the domain to the list in the UI. This function should only be called
     * once the domain is properly registered in the Backend API.
     * @param domainName name of domain added to adapter
     */
    private void addListAdapter(String domainName) {
        if(!blackList.contains(domainName)) blackList.add(domainName);
        if (!DomainInfo.getInstance().contains(domainName)) {
            HashMap<String, String> info = new HashMap<>();
            info.put(DomainInfo.DOMAIN_NAME, domainName);
            info.put(DomainInfo.REGISTRAR_DOMAIN_ID, "");
            info.put(DomainInfo.REGISTRAR_NAME, "");
            info.put(DomainInfo.REGISTRAR_EXPIRY_DATE, "");
            DomainInfo.getInstance().addDomain(domainName, info);
        }
        blackListArrayAdapter.notifyDataSetChanged();
    }
}