package com.example.securify.ui.whitelist;

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

public class WhiteListFragment extends Fragment {

    private WhiteListViewModel whiteListViewModel;
    private ArrayList<String> whiteList;
    private ArrayList<String> allDomainsList;
    private DomainListAdapter whiteListArrayAdapter;

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
        // TODO: check if domain is valid
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

                whiteList.add(whitelist);
                allDomainsList.add(whitelist);

                if (DomainLists.getInstance().blackListContains(whitelist)) {

                    DomainLists.getInstance().removeFromBlackList(whitelist);

                } else {

                    /* placeholder code */
                    HashMap<String, String> domainInfo = new HashMap<>();
                    domainInfo.put(DomainInfo.DOMAIN_NAME, DomainInfo.DOMAIN_NAME);
                    domainInfo.put(DomainInfo.REGISTRAR_DOMAIN_ID, DomainInfo.REGISTRAR_DOMAIN_ID);
                    domainInfo.put(DomainInfo.REGISTRAR_NAME, DomainInfo.REGISTRAR_NAME);
                    domainInfo.put(DomainInfo.REGISTRAR_EXPIRY_DATE, DomainInfo.REGISTRAR_EXPIRY_DATE);
                    domainInfo.put(DomainInfo.DOMAIN_TIMESTAMP, DomainInfo.DOMAIN_TIMESTAMP);
                    DomainInfo.getInstance().addDomain(whitelist, domainInfo);

                }
                whiteListArrayAdapter.notifyDataSetChanged();
                addWhiteList.getText().clear();
            }
        });

        return root;
    }
}