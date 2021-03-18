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

import org.apache.commons.net.whois.WhoisClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BlackListFragment extends Fragment {

    private BlackListViewModel blackListViewModel;
    private ArrayList<String> blackList;
    private ArrayList<String> allDomainsList;
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

        blackList = DomainLists.getInstance().getBlackList();
        blackListArrayAdapter = new DomainListAdapter(getContext(), blackList, false, false);
        ExpandableListView blackListDomains = root.findViewById(R.id.blacklist_domains);
        blackListDomains.setAdapter(blackListArrayAdapter);
        blackListDomains.setGroupIndicator(null);

        allDomainsList = DomainLists.getInstance().getAllDomainsList();
        EditText addBlackList = root.findViewById(R.id.add_blacklist_text);

        whoisClient = new WhoisClient();

        Button addBlackListDomain = root.findViewById(R.id.add_blacklist_domain_button);
        addBlackListDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blacklist = addBlackList.getText().toString();
                if (blackList.contains(blacklist)) {
                    Toast.makeText(getContext(), "Domain already in whitelist", Toast.LENGTH_LONG).show();
                    addBlackList.getText().clear();
                    return;
                }
                if (DomainLists.getInstance().whiteListContains(blacklist)) {

                    DomainLists.getInstance().removeFromWhiteList(blacklist);
                    //TODO: update lists in the backend

                } else {

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                StringBuilder server = new StringBuilder("");

                                whoisClient.connect("whois.iana.org");
                                server.append(whoisClient.query(blacklist));
                                whoisClient.disconnect();

                                String whoIsServer = DomainMatcher.getMatch(server.toString(), DomainMatcher.WHOIS_SERVER).trim();
                                if (whoIsServer.equals("")) {
                                    validDomain = false;
                                    return;
                                }

                                Log.i(TAG,  whoIsServer);
                                whoisClient.connect(whoIsServer);
                                StringBuilder result = new StringBuilder("");
                                result.append(whoisClient.query(blacklist));
                                Log.i(TAG,  result.toString());
                                String whoIsInfo = result.toString();

                                HashMap<String, String> domainInfo = new HashMap<>();
                                domainInfo.put(DomainInfo.DOMAIN_NAME, blacklist);

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
                                DomainInfo.getInstance().addDomain(blacklist, domainInfo);
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

                if (validDomain) {
                    blackListArrayAdapter.notifyDataSetChanged();
                    addBlackList.getText().clear();
                    blackList.add(blacklist);
                    allDomainsList.add(blacklist);
                    //TODO: update lists in the backend

                } else {
                    Toast.makeText(getContext(), "Invalid Domain", Toast.LENGTH_LONG).show();
                    validDomain = true;
                    addBlackList.getText().clear();
                }

            }
        });

        return root;
    }
}