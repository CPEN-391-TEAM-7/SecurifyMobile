package com.example.securify.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.VolleyError;
import com.example.securify.R;
import com.example.securify.adapters.ActivityDomainListAdapter;
import com.example.securify.bluetooth.BluetoothOutputStream;
import com.example.securify.comparators.ActivityListComparator;
import com.example.securify.domain.DomainInfo;
import com.example.securify.domain.DomainLists;
import com.example.securify.model.User;
import com.example.securify.ui.BluetoothActivity;
import com.example.securify.ui.volley.VolleyRequest;
import com.example.securify.ui.volley.VolleyResponseListener;
import com.example.securify.ui.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ActivityFragment extends Fragment {

    private ActivityViewModel activityViewModel;
    private OutputStream outputStream;
    private ArrayList<String> domainList = new ArrayList<>();
    private ActivityDomainListAdapter domainListAdapter;
    private boolean domainNameAscending = false;
    private boolean timeStampAscending = false;

    private final String TAG = "ActivityFragment";
    private String START_DATE = "";
    private final int LIMIT = 20;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private final String[] listSelectorItems = {"All Domains", "Blacklist Domains Only", "Whitelist Domains Only"};

    private int listPriority = ActivityListComparator.priorityWhiteList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activityViewModel =
                new ViewModelProvider(this).get(ActivityViewModel.class);
        View root = inflater.inflate(R.layout.fragment_activity, container, false);
        final TextView textView = root.findViewById(R.id.text_activity);

        outputStream = BluetoothOutputStream.getInstance().getOutputStream();

        START_DATE = Instant.now().toString();
        SwitchCompat switchCompat = root.findViewById(R.id.switch_compat);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//                if (isChecked) {
//                    WritetoBTDevice("1");
//                } else {
//                    WritetoBTDevice("0");
//                }
            }
        });

        activityViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        ImageButton blueToothButton = root.findViewById(R.id.bluetooth_button);
        blueToothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BluetoothActivity.class));
            }
        });

        domainList = DomainLists.getInstance().getActivityDomainsList();
        domainListAdapter = new ActivityDomainListAdapter(getContext(), domainList);

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
                switch (listPriority) {
                    case ActivityListComparator.priorityWhiteList:
                        domainListAdapter.sortList(listPriority);
                        listPriority = ActivityListComparator.priorityBlackList;
                        break;
                    case ActivityListComparator.priorityBlackList:
                        domainListAdapter.sortList(listPriority);
                        listPriority = ActivityListComparator.priorityUndefined;
                        break;
                    case ActivityListComparator.priorityUndefined:
                        domainListAdapter.sortList(listPriority);
                        listPriority = ActivityListComparator.priorityWhiteList;
                        break;
                }
            }
        });

        Button loadMoreButton = root.findViewById(R.id.load_more_button);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateDomains();
                Log.d(TAG, "[Activity] the list: " + domainList.toString());
                domainListAdapter.notifyDataSetChanged();
            }
        });

        View filterMenu = root.findViewById(R.id.filter_menu);
        Button filterButton = root.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterMenu.getVisibility() == View.VISIBLE) {
                    filterMenu.setVisibility(View.GONE);
                } else {
                    filterMenu.setVisibility(View.VISIBLE);
                }
            }
        });

        AutoCompleteTextView domainAutoComplete = root.findViewById(R.id.filter_domain_name_autocomplete);
        ArrayAdapter<String> domainAutoCompleteAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, domainList);
        domainAutoComplete.setAdapter(domainAutoCompleteAdapter);

        EditText startDate = root.findViewById(R.id.filter_start_date_text);
        startDate.setInputType(InputType.TYPE_NULL);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                int mMonth = calendar.get(Calendar.MONTH);
                int mYear = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        EditText startTime = root.findViewById(R.id.filter_start_time_text);
        startTime.setInputType(InputType.TYPE_NULL);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startDate.getText().toString().matches("")) {
                    Toast.makeText(getContext(), "Please enter a starting date", Toast.LENGTH_LONG).show();
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMminute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMminute, true);
                timePickerDialog.show();
            }
        });

        EditText endDate = root.findViewById(R.id.filter_end_date_text);
        endDate.setInputType(InputType.TYPE_NULL);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                int mMonth = calendar.get(Calendar.MONTH);
                int mYear = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        EditText endTime = root.findViewById(R.id.filter_end_time_text);
        endTime.setInputType(InputType.TYPE_NULL);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (endDate.getText().toString().matches("")) {
                    Toast.makeText(getContext(), "Please enter a ending date", Toast.LENGTH_LONG).show();
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMminute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMminute, true);
                timePickerDialog.show();
            }
        });

        Spinner listSelector = root.findViewById(R.id.filter_list_selector);
        ArrayAdapter<String> listSelectorAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, listSelectorItems);
        listSelector.setAdapter(listSelectorAdapter);

        Button resetButton = root.findViewById(R.id.filter_reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                domainAutoComplete.getText().clear();
                startDate.getText().clear();
                startTime.getText().clear();
                endDate.getText().clear();
                endTime.getText().clear();
                listSelector.setSelection(0);
            }
        });

        Button applyButton = root.findViewById(R.id.filter_apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                domainListAdapter.constraints.put(domainListAdapter.DOMAIN_NAME_FILTER, domainAutoComplete.getText().toString().toLowerCase());
                domainListAdapter.constraints.put(domainListAdapter.START_DATE_FILTER, startDate.getText().toString());
                domainListAdapter.constraints.put(domainListAdapter.START_TIME_FILTER, startTime.getText().toString());
                domainListAdapter.constraints.put(domainListAdapter.END_DATE_FILTER, endDate.getText().toString());
                domainListAdapter.constraints.put(domainListAdapter.END_TIME_FILTER, endTime.getText().toString());
                domainListAdapter.constraints.put(domainListAdapter.LIST_FILTER, listSelector.getSelectedItem().toString());
                domainListAdapter.getFilter().filter("");
            }
        });

        return root;
    }

    /**
     * Populate activity domain list
     */
    private void populateDomains() {

        JSONObject getRequest = new JSONObject();

        Log.i(TAG, "getRequest body " +  getRequest.toString());
        try {
            getRequest.put(VolleySingleton.startDate, START_DATE);
            getRequest.put(VolleySingleton.limit, LIMIT);
            Log.d(TAG, getRequest.toString());

        } catch (JSONException e) {
            Log.e(TAG, "Populate Domain Request Creation Failed");
            e.printStackTrace();
        }

        Log.d(TAG, "HELLO before the volley request??");

        // Set up backend request
        VolleyRequest.addRequest(getContext(),
                VolleyRequest.GET_RECENT_DOMAIN_REQUEST_ACTIVITY,
                User.getInstance().getUserID(),
                "",
                getRequest,
                new VolleyResponseListener() {
                    @Override
                    public void onError(Object response) {
                        VolleyError error = (VolleyError) response;
                        Log.i(TAG, "What's the status code? => " + String.valueOf(error.networkResponse.statusCode));
                        Log.e(TAG, "Error: " + response.toString());
                        Toast.makeText(getContext(), "Could not load anymore domains", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Object response) {
                        Log.i(TAG, "I got a response: " + response.toString());
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response.toString()); // Moved this line to here
                            JSONArray activities =  jsonObject.getJSONArray("activities");
                            DomainInfo domainInfo = DomainInfo.getInstance();
                            JSONObject activity;

                            for (int i = 0; i < activities.length(); i++) {
                                activity = activities.getJSONObject(i);
                                String domainName;
                                try {
                                    domainName = activity.getString("domainName");
                                } catch (JSONException e){
                                    Log.e(TAG, e.toString());
                                    continue;
                                }
                                if(!domainList.contains(domainName)) domainList.add(domainName);

                                HashMap<String, String> info;

                                if (!DomainInfo.getInstance().contains(domainName)) {
                                    info = new HashMap<>();
                                    info.put(DomainInfo.REGISTRAR_DOMAIN_ID, "");
                                    info.put(DomainInfo.REGISTRAR_NAME, "");
                                    info.put(DomainInfo.REGISTRAR_EXPIRY_DATE, "");
                                } else {
                                    info = DomainInfo.getInstance().getInfo(domainName);
                                }

                                info.put(DomainInfo.DOMAIN_NAME, domainName);
                                info.put(DomainInfo.DEVICE_IP, "");

                                String timeStamp = activity.getString(VolleySingleton.timestamp);
                                timeStamp = String.valueOf(simpleDateFormat.parse(timeStamp));

                                info.put(DomainInfo.DOMAIN_TIMESTAMP, timeStamp);
                                String deviceIP = activity.getString(VolleySingleton.ipAddress);
                                info.put(DomainInfo.DEVICE_IP, deviceIP);

                                domainInfo.addDomain(domainName, info);

                                if (activity.get(VolleySingleton.listType).equals(VolleySingleton.Blacklist)) {
                                    DomainLists.getInstance().addToBlackList(domainName);
                                }

                                if (activity.get(VolleySingleton.listType).equals(VolleySingleton.Whitelist)) {
                                    DomainLists.getInstance().addToWhiteList(domainName);
                                }

                                domainListAdapter.notifyDataSetChanged();
                            }
                            START_DATE = jsonObject.getString(VolleySingleton.lastEndDate);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error On Response from Populate Domain Request");
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
        });


    }

    /**
     * Writes a string message to connected bluetooth device
     * @param message string message to be written to bluetooth device
     */
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