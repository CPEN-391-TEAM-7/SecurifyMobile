package com.example.securify.ui.whitelist;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.whois.WhoisClient;
import org.json.JSONException;
import org.json.JSONObject;

public class WhiteListFragment extends Fragment {

    private WhiteListViewModel whiteListViewModel;
    private ArrayList<String> whiteList;
    private ArrayList<String> allDomainsList;
    private DomainListAdapter whiteListArrayAdapter;

    private WhoisClient whoisClient;
    private Boolean validDomain = true;
    private final String TAG = "WhiteListFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        whiteListViewModel =
                new ViewModelProvider(this).get(WhiteListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_whitelist, container, false);
        final TextView textView = root.findViewById(R.id.text_whitelist);
        whiteListViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        whiteList = DomainLists.getInstance().getWhiteList();
        whiteListArrayAdapter =  new DomainListAdapter(getContext(), whiteList, true, false);
        ExpandableListView whiteListDomains = root.findViewById(R.id.whitelist_domains);
        whiteListDomains.setAdapter(whiteListArrayAdapter);
        whiteListDomains.setGroupIndicator(null);

        allDomainsList = DomainLists.getInstance().getAllDomainsList();
        EditText addWhiteList = root.findViewById(R.id.add_whitelist_text);

        whoisClient = new WhoisClient();

        Button addWhiteListDomain =  root.findViewById(R.id.add_whitelist_domain_button);
        addWhiteListDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whitelist = addWhiteList.getText().toString();
                if (whiteList.contains(whitelist)) {
                    Toast.makeText(getContext(), "Domain already in whitelist", Toast.LENGTH_LONG).show();
                    addWhiteList.getText().clear();
                    return;
                }

                if (DomainLists.getInstance().blackListContains(whitelist)) {

                    DomainLists.getInstance().removeFromBlackList(whitelist);

                } else {

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                StringBuilder server = new StringBuilder("");

                                whoisClient.connect("whois.iana.org");
                                server.append(whoisClient.query(whitelist));
                                whoisClient.disconnect();

                                String whoIsServer = DomainMatcher.getMatch(server.toString(), DomainMatcher.WHOIS_SERVER).trim();
                                if (whoIsServer.equals("")) {
                                    validDomain = false;
                                    return;
                                }

                                Log.i(TAG,  whoIsServer);
                                whoisClient.connect(whoIsServer);
                                StringBuilder result = new StringBuilder("");
                                result.append(whoisClient.query(whitelist));
                                Log.i(TAG,  result.toString());
                                String whoIsInfo = result.toString();

                                HashMap<String, String> domainInfo = new HashMap<>();
                                domainInfo.put(DomainInfo.DOMAIN_NAME, whitelist);

                                String domainID = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_DOMAIN_ID).trim();

                                Log.i(TAG, "registrar domain id:" + domainID);
                                domainInfo.put(DomainInfo.REGISTRAR_DOMAIN_ID, domainID);

                                String registrarName = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_NAME).trim();

                                Log.i(TAG, "registrar name:" + registrarName);
                                domainInfo.put(DomainInfo.REGISTRAR_NAME, registrarName);

                                String registrarExpiryDate = DomainMatcher.getMatch(whoIsInfo, DomainMatcher.REGISTRAR_EXPIRY_DATE).trim();

                                Log.i(TAG, "expiry date:" + registrarExpiryDate);
                                domainInfo.put(DomainInfo.REGISTRAR_EXPIRY_DATE, registrarExpiryDate);

                                // TODO: implement proper timestamping
                                domainInfo.put(DomainInfo.DOMAIN_TIMESTAMP, "2021-01-23-13:31:24");
                                DomainInfo.getInstance().addDomain(whitelist, domainInfo);
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

                addWhiteList.getText().clear();

                if (validDomain) {
                    addWhiteList(whitelist);
                } else {
                    Toast.makeText(getContext(), "Invalid Domain", Toast.LENGTH_LONG).show();
                    validDomain = true;
                }

            }
        });

        return root;
    }

    private void addWhiteList(String domainName) {
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
        VolleyRequest.addRequest(getContext(), VolleyRequest.PUT_LIST, User.getInstance().getUserID(), domainName, "", postData, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.i(TAG, message);
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
                        Toast.makeText(getContext(), json.getString("msg"), Toast.LENGTH_SHORT).show();

                            whiteListArrayAdapter.notifyDataSetChanged();
                            whiteList.add(domainName);
                            allDomainsList.add(domainName);

                    }
                } catch (JSONException e){
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

}

