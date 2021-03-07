package com.example.securify.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.securify.BluetoothStreams;
import com.example.securify.DomainLists;
import com.example.securify.R;
import com.example.securify.ui.adapters.DomainListAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ActivityFragment extends Fragment {

    private ActivityViewModel activityViewModel;
    private OutputStream outputStream;
    private ArrayList<String> domainList;
    private DomainListAdapter domainListAdapter;
    private boolean domainNameAscending = false;
    private boolean timeStampAscending = false;
    private boolean listAscending = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activityViewModel =
                new ViewModelProvider(this).get(ActivityViewModel.class);
        View root = inflater.inflate(R.layout.fragment_activity, container, false);
        final TextView textView = root.findViewById(R.id.text_activity);
        outputStream = BluetoothStreams.getInstance().getOutputStream();

        ToggleButton toggleButton = root.findViewById(R.id.toggle);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    WritetoBTDevice("1");
                } else {
                    WritetoBTDevice("0");
                }
            }
        });
        activityViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        domainList = DomainLists.getInstance().getAllDomainsList();
        domainListAdapter = new DomainListAdapter(getContext(), domainList, false, true);

        ExpandableListView allDomains = root.findViewById(R.id.activity_domain_list);
        allDomains.setAdapter(domainListAdapter);
        allDomains.setGroupIndicator(null);

        TextView domainTitle = root.findViewById(R.id.domain_title);
        domainTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (domainNameAscending) {
                    domainNameAscending = false;
                    domainListAdapter.sortDomainNameDescending();;
                } else {
                    domainNameAscending = true;
                    domainListAdapter.sortDomainNameAscending();
                }
            }
        });

        TextView timeStampTitle = root.findViewById(R.id.timestamp_title);
        timeStampTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeStampAscending) {
                    timeStampAscending = false;
                    domainListAdapter.sortTimeStampDescending();
                } else {
                    timeStampAscending = true;
                    domainListAdapter.sortTimeStampAscending();
                }
            }
        });

        TextView listTitle = root.findViewById(R.id.list_title);
        listTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listAscending) {
                    listAscending = false;
                    domainListAdapter.sortListDescending();
                } else {
                    listAscending = true;
                    domainListAdapter.sortListAscending();
                }
            }
        });

        Button loadMoreButton = root.findViewById(R.id.load_more_button);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: load more events
                domainListAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    public void WritetoBTDevice(String message) {
        String s = "\r\n";

        byte[] msgBuffer = message.getBytes();
        byte[] newline = s.getBytes();

        if (outputStream == null) {
            Toast.makeText(getContext(), "Not connected to bluetooth device", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            outputStream.write(msgBuffer);
            outputStream.write(newline);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}