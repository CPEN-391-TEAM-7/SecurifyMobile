package com.example.securify.ui.whitelist;

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

public class WhiteListFragment extends Fragment {

    private WhiteListViewModel whiteListViewModel;
    private ArrayList<String> whiteList;
    private DomainArrayAdapter whiteListArrayAdapter;

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
        whiteListArrayAdapter =  new DomainArrayAdapter(getContext(), R.layout.domain_row, whiteList, true);
        ListView whiteListDomains = root.findViewById(R.id.whitelist_domains);
        whiteListDomains.setAdapter(whiteListArrayAdapter);

        EditText addWhiteList = root.findViewById(R.id.add_whitelist_text);

        Button addWhiteListDomain =  root.findViewById(R.id.add_whitelist_domain_button);
        addWhiteListDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whitelist = addWhiteList.getText().toString();
                whiteList.add(whitelist);
                whiteListArrayAdapter.notifyDataSetChanged();
            }
        });

        return root;
    }
}