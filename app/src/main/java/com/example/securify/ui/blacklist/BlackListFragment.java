package com.example.securify.ui.blacklist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.securify.DomainInfo;
import com.example.securify.DomainLists;
import com.example.securify.R;
import com.example.securify.ui.adapters.DomainListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class BlackListFragment extends Fragment {

    private BlackListViewModel blackListViewModel;
    private ArrayList<String> blackList;
    private ArrayList<String> allDomainsList;
    private DomainListAdapter blackListArrayAdapter;

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
        // TODO: check if domain is valid
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

                blackList.add(blacklist);
                allDomainsList.add(blacklist);

                if (DomainLists.getInstance().whiteListContains(blacklist)) {

                    DomainLists.getInstance().removeFromWhiteList(blacklist);

                } else {

                    /* placeholder code */
                    HashMap<String, String> domainInfo = new HashMap<>();
                    domainInfo.put(DomainInfo.DOMAIN_NAME, DomainInfo.DOMAIN_NAME);
                    domainInfo.put(DomainInfo.REGISTRAR_DOMAIN_ID, DomainInfo.REGISTRAR_DOMAIN_ID);
                    domainInfo.put(DomainInfo.REGISTRAR_NAME, DomainInfo.REGISTRAR_NAME);
                    domainInfo.put(DomainInfo.REGISTRAR_EXPIRY_DATE, DomainInfo.REGISTRAR_EXPIRY_DATE);
                    domainInfo.put(DomainInfo.DOMAIN_TIMESTAMP, "2021-01-23-13:31:24");
                    DomainInfo.getInstance().addDomain(blacklist, domainInfo);
                }


                blackListArrayAdapter.notifyDataSetChanged();
                addBlackList.getText().clear();
            }
        });

        return root;
    }
}