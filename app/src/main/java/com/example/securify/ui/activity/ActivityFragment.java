package com.example.securify.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.example.securify.ui.adapters.ActivityDomainListAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityFragment extends Fragment {

    // TODO: prevent dialogs from appearing twice
    private ActivityViewModel activityViewModel;
    private OutputStream outputStream;
    private ArrayList<String> domainList = new ArrayList<>();
    private ActivityDomainListAdapter domainListAdapter;
    private boolean domainNameAscending = false;
    private boolean timeStampAscending = false;
    private boolean listAscending = false;

    String[] listSelectorItems = {"All Domains", "Blacklist Domains Only", "Whitelist Domains Only"};


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

                /*
                if (isChecked) {
                    WritetoBTDevice("1");
                } else {
                    WritetoBTDevice("0");
                }

                 */
                Toast.makeText(getContext(), "Not connected to bluetooth device", Toast.LENGTH_LONG).show();
            }
        });
        activityViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        domainList = DomainLists.getInstance().getAllDomainsList();
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
        ArrayAdapter<String> listSelectorAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, listSelectorItems);
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
                domainListAdapter.constraints.put(domainListAdapter.DOMAIN_NAME_FILTER, domainAutoComplete.getText().toString());
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