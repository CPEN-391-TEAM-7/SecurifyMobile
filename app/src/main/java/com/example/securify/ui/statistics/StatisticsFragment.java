package com.example.securify.ui.statistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.toolbox.Volley;
import com.example.securify.R;
import com.example.securify.adapters.StatisticsListAdapter;
import com.example.securify.model.User;
import com.example.securify.ui.volley.VolleyRequest;
import com.example.securify.ui.volley.VolleyResponseListener;
import com.example.securify.ui.volley.VolleySingleton;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;


public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;

    private PieChart pieChart;
    private BarChart barChart;
    private LineChart lineChart;

    private ArrayList<Integer> colors = new ArrayList<>();

    String[] spinnerItems = {"Daily", "Weekly", "Monthly", "Yearly", "All Time"};

    private HashMap<String, HashMap<String, Object>> topDomainsData = new HashMap<>();
    private StatisticsListAdapter statisticsListAdapter;

    private Description description = new Description();
    private final String TAG = "StatisticsFragment";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private HashMap<String, Integer> pieChartData =  new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> barChartData = new HashMap<>();
    private HashMap<String, Integer> lineChartData = new HashMap<>();
    private ArrayList<String> days = new ArrayList<String>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        //final TextView textView = root.findViewById(R.id.text_statistics);

        pieChart = root.findViewById(R.id.pieChart_view);
        barChart = root.findViewById(R.id.barChart_view);
        lineChart = root.findViewById(R.id.lineChart_view);

        description.setText("");
        initColors();
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        initData();
        // getWeeklyData();
        // getMonthlyData();

        initPieChart();
        showPieChart();

        showBarChart();
        showLineChart();

        Spinner topDomainsSpinner = root.findViewById(R.id.top_domains_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        topDomainsSpinner.setAdapter(spinnerAdapter);

        LocalDateTime currentDateTime = LocalDateTime.now();
        topDomainsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                // TODO: add volley requests
                JSONObject getData = new JSONObject();
                try {
                    getData.put(VolleySingleton.startDate, simpleDateFormat.format(currentDateTime));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (selectedItem) {
                    case "Daily":
                        try {
                            getData.put(VolleySingleton.endDate, simpleDateFormat.format(currentDateTime.minusDays(1)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Weekly":
                        try {
                            getData.put(VolleySingleton.endDate, simpleDateFormat.format(currentDateTime.minusWeeks(1)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Monthly":
                        try {
                            getData.put(VolleySingleton.endDate, simpleDateFormat.format(currentDateTime.minusMonths(1)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Yearly":
                        try {
                            getData.put(VolleySingleton.endDate, simpleDateFormat.format(currentDateTime.minusYears(1)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "All Time":
                        break;
                }

                VolleyRequest.addRequest(getContext(), VolleyRequest.GET_BY_DATE_MOST_REQUESTED_DOMAINS, User.getInstance().getUserID(), "", "", getData, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Log.e(TAG, message);
                    }

                    @Override
                    public void onResponse(Object response) {
                        topDomainsData.clear();


                        try {
                            JSONObject jsonArray = new JSONObject(response.toString());

                            Iterator iterator = jsonArray.keys();

                            while(iterator.hasNext()) {

                                HashMap<String, Object> topDomainsDataEntry = new HashMap<>();

                                String domainName = iterator.next().toString();
                                topDomainsDataEntry.put(VolleySingleton.domainName, domainName);

                                JSONObject jsonObject = new JSONObject(jsonArray.getString(domainName));
                                topDomainsDataEntry.put(VolleySingleton.listType, jsonObject.get(VolleySingleton.listType));
                                topDomainsDataEntry.put(VolleySingleton.num_of_accesses, jsonObject.get(VolleySingleton.count));

                                topDomainsData.put(domainName, topDomainsDataEntry);
                            }


                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }


                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });
        
        /* Test Data */
        HashMap<String, Object> testData = new HashMap<>();
        testData.put(VolleySingleton.domainName, "testName");
        testData.put(VolleySingleton.listType, VolleySingleton.Whitelist);
        testData.put(VolleySingleton.num_of_accesses, 10);
        /* Test Data */

        topDomainsData.put("testName", testData);
        statisticsListAdapter = new StatisticsListAdapter(topDomainsData, getContext());
        ListView topDomainsList = root.findViewById(R.id.top_domains_list);
        topDomainsList.setAdapter(statisticsListAdapter);

//        statisticsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void initData() {

        ArrayList<String> listTypes = new ArrayList<>();
        listTypes.add(VolleySingleton.Whitelist);
        listTypes.add(VolleySingleton.Blacklist);
        listTypes.add(VolleySingleton.Safe);
        listTypes.add(VolleySingleton.Malicious);
        listTypes.add(VolleySingleton.Undefined);

        days.add("MON");
        days.add("TUE");
        days.add("WED");
        days.add("THU");
        days.add("FRI");
        days.add("SAT");
        days.add("SUN");

        for (String type: listTypes) {
            pieChartData.put(type, 0);
        }

        HashMap<String, Integer> init;

        for (String day: days) {
            barChartData.put(day, new HashMap<>());
            init = barChartData.get(day);
            for (String type: listTypes) {
                init.put(type, 0);
            }
        }
    }

    private void getWeeklyData() {

        JSONObject domainRequest = new JSONObject();

        VolleyResponseListener volleyResponseListener = new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.e(TAG, message);
            }

            @Override
            public void onResponse(Object response) {

                // TODO: update barChartData
                // TODO: update pieChartData

                try {
                    JSONObject jsonArray = new JSONObject(response.toString());

                    Iterator iterator = jsonArray.keys();

                    while(iterator.hasNext()) {

                        String domainName = iterator.next().toString();
                        JSONObject jsonObject = new JSONObject(jsonArray.getString(domainName));

                        switch (domainName) {
                            case "Whitelist":
                                break;
                            case "Blacklist":
                                break;
                            case "Safe":
                                break;
                            case "Malicious":
                                break;
                            case "Undefined":
                                break;
                        }
                    }


                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
               
            }
        };

        LocalDateTime currentDateTime = LocalDateTime.now();
        try {
            domainRequest.put(VolleySingleton.startDate, simpleDateFormat.format(currentDateTime));
            domainRequest.put(VolleySingleton.endDate, simpleDateFormat.format(currentDateTime.minusWeeks(1)));
            // TODO: add userID
            VolleyRequest.addRequest(getContext(), VolleyRequest.GET_BY_DATE_MOST_REQUESTED_DOMAINS, "", "", "", domainRequest, volleyResponseListener);
        } catch (JSONException e) {
            Log.e(TAG, "Initialize Data Failed");
            e.printStackTrace();
        }

    }

    private void initColors() {
        colors.add(getResources().getColor(R.color.main2));
        colors.add(getResources().getColor(R.color.main3));
        colors.add(getResources().getColor(R.color.main4));
        colors.add(getResources().getColor(R.color.main5));
        colors.add(getResources().getColor(R.color.main6));
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
        String label = "";

        pieEntries.add(new PieEntry(100, VolleySingleton.Whitelist));
        pieEntries.add(new PieEntry(200, VolleySingleton.Blacklist));
        pieEntries.add(new PieEntry(300, VolleySingleton.Safe));
        pieEntries.add(new PieEntry(400, VolleySingleton.Malicious));
        pieEntries.add(new PieEntry(500, VolleySingleton.Undefined));

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

        // TODO: get actual data

        for (int i = 0; i < 7; i++) {
            float whiteListedDomains = (float) (Math.random() * i + 100);
            float blackListedDomains = (float) (Math.random() * i + 100);
            float safeDomains = (float) (Math.random() * i + 100);
            float maliciousDomains = (float) (Math.random() * i + 100);
            float undefinedDomains = (float) (Math.random() * i + 100);

            entries.add(new BarEntry(
                    i,
                    new float[]{whiteListedDomains, blackListedDomains, safeDomains, maliciousDomains, undefinedDomains}
            ));
        }

        BarDataSet barDataSet1;

        barDataSet1 = new BarDataSet(entries, "");
        barDataSet1.setDrawIcons(false);
        barDataSet1.setColors(colors);
        barDataSet1.setStackLabels(new String[]{"Whitelist", "BlackList", "Safe", "Malicious", "Undefined"});

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        BarData data = new BarData(dataSets);
        data.setDrawValues(false);
        barChart.setData(data);
        barChart.animateY(5000);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));

        barChart.setDescription(description);
    }

    private void showLineChart() {
        ArrayList<Entry> lineData = new ArrayList<Entry>();
        ArrayList<String> xAxisLabels = new ArrayList<>(30);

        for (int i = 0; i < 30; i++) {
            lineData.add(new Entry(i,  i * 100));
            xAxisLabels.add("March " + i);
        }

        LineDataSet setComp1 = new LineDataSet(lineData, "");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);


        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        LineData data = new LineData(dataSets);
        data.setDrawValues(false);
        lineChart.setData(data);
        lineChart.invalidate(); // refresh
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        lineChart.getLegend().setEnabled(false);

        lineChart.setDescription(description);
    }
}