package com.example.securify.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.securify.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;

    private PieChart pieChart;
    private BarChart barChart;

    ArrayList<Integer> colors = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        //final TextView textView = root.findViewById(R.id.text_statistics);

        pieChart = root.findViewById(R.id.pieChart_view);
        barChart = root.findViewById(R.id.barChart_view);
        initColors();

        initPieChart();
        showPieChart();

        showBarChart();

//        statisticsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void initColors() {
        colors.add(getResources().getColor(R.color.main2));
        colors.add(getResources().getColor(R.color.main3));
        colors.add(getResources().getColor(R.color.main4));
        colors.add(getResources().getColor(R.color.main5));
        colors.add(getResources().getColor(R.color.main6));
        colors.add(getResources().getColor(R.color.main7));
        colors.add(getResources().getColor(R.color.main1));
    }

    private void initPieChart(){
        //using percentage as values instead of amount
        pieChart.setUsePercentValues(true);

        //remove the description label on the lower left corner, default true if not set
        pieChart.getDescription().setEnabled(false);

        //remove name of slices
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawMarkers(false);

        //enabling the user to rotate the chart, default true
        pieChart.setRotationEnabled(true);
        //adding friction when rotating the pie chart
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        //setting the first entry start from right hand side, default starting from top
        pieChart.setRotationAngle(0);

        //highlight the entry when it is tapped, default true if not set
        pieChart.setHighlightPerTapEnabled(true);
        //adding animation so the entries pop up from 0 degree
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleRadius(0f);
    }

    private void showPieChart(){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Malicious",200);
        typeAmountMap.put("Blacklisted",230);
        typeAmountMap.put("Whitelisted",100);
        typeAmountMap.put("Safe",500);
        typeAmountMap.put("Undefined",50);

        //initializing colors for the entries


        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(false);

        pieData.setValueFormatter(new PercentFormatter());

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void showBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 40f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 10f));
        entries.add(new BarEntry(4f, 5f));
        entries.add(new BarEntry(5f, 45f));

        BarDataSet bardataset = new BarDataSet(entries, "Cells");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("MON");
        labels.add("TUE");
        labels.add("WED");
        labels.add("THU");
        labels.add("FRI");
        labels.add("WEEKEND");

        BarData data = new BarData(bardataset);
        barChart.setData(data); // set the data and list of labels into chart
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        bardataset.setColors(colors);
        barChart.animateY(5000);
    }
}