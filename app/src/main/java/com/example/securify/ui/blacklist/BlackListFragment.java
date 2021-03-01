package com.example.securify.ui.blacklist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.securify.DomainLists;
import com.example.securify.R;
import com.example.securify.ui.adapters.DomainArrayAdapter;

import java.util.ArrayList;

public class BlackListFragment extends Fragment {

    private BlackListViewModel blackListViewModel;
    private ArrayList<String> blackList;
    private DomainArrayAdapter blackListArrayAdapter;

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
        blackListArrayAdapter = new DomainArrayAdapter(getContext(), R.layout.domain_row, blackList, false);
        ListView blackListDomains = root.findViewById(R.id.blacklist_domains);
        blackListDomains.setAdapter(blackListArrayAdapter);

        EditText addBlackList = root.findViewById(R.id.add_blacklist_text);

        Button addBlackListDomain = root.findViewById(R.id.add_blacklist_domain_button);
        addBlackListDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blacklist = addBlackList.getText().toString();
                blackList.add(blacklist);
                blackListArrayAdapter.notifyDataSetChanged();
            }
        });

        return root;
    }
}