package com.example.securify.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.securify.BluetoothActivity;
import com.example.securify.BluetoothStreams;
import com.example.securify.MainActivity;
import com.example.securify.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ActivityFragment extends Fragment {

    private ActivityViewModel activityViewModel;
    private OutputStream outputStream;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        activityViewModel =
                new ViewModelProvider(this).get(ActivityViewModel.class);
        View root = inflater.inflate(R.layout.fragment_activity, container, false);
        final TextView textView = root.findViewById(R.id.text_activity);

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
        return root;
    }

    public void WritetoBTDevice(String message) {
        String s = "\r\n";

        byte[] msgBuffer = message.getBytes();
        byte[] newline = s.getBytes();
        if (outputStream == null) {
            outputStream = BluetoothStreams.getInstance().getOutputStream();
        }

        try {
            outputStream.write(msgBuffer);
            outputStream.write(newline);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}