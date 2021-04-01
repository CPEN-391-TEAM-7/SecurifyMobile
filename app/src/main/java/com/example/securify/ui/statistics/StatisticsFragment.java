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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.securify.R;
import com.example.securify.adapters.StatisticsListAdapter;
import com.example.securify.domain.TopDomainsInfo;
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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;

    private PieChart pieChart;
    private BarChart barChart;
    private LineChart lineChart;

    private ArrayList<Integer> colors = new ArrayList<>();

    String[] spinnerItems = {"Daily", "Weekly", "Monthly", "Yearly", "All Time"};

    private ArrayList<String> topDomainsData = new ArrayList<>();
    private StatisticsListAdapter statisticsListAdapter;

    private Description description = new Description();
    private final String TAG = "StatisticsFragment";

    private HashMap<String, Integer> pieChartData =  new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> barChartData = new HashMap<>();
    private Map<LocalDateTime, Float> lineChartData = new TreeMap<>();
    private ArrayList<String> days = new ArrayList<String>();

    private final DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter lineChartDayFormat = DateTimeFormatter.ofPattern("MMM dd");

    private LocalDateTime currentDateTime;

    private boolean domainNameAscending = false;
    private boolean countAscending = false;
    private boolean listAscending = false;

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
        currentDateTime = LocalDateTime.now();

        initColors();
        initData();
        getWeeklyData();
        getMonthlyData();

        initPieChart();
        showPieChart();

        showBarChart();
        showLineChart();

        Spinner topDomainsSpinner = root.findViewById(R.id.top_domains_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        topDomainsSpinner.setAdapter(spinnerAdapter);

        topDomainsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                // TODO: add volley requests
                JSONObject getData = new JSONObject();
                try {
                    getData.put(VolleySingleton.startDate, currentDateTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (selectedItem) {
                    case "Daily":
                        try {
                            getData.put(VolleySingleton.endDate, currentDateTime.minusDays(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Weekly":
                        try {
                            getData.put(VolleySingleton.endDate, currentDateTime.minusWeeks(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Monthly":
                        try {
                            getData.put(VolleySingleton.endDate, currentDateTime.minusMonths(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Yearly":
                        try {
                            getData.put(VolleySingleton.endDate, currentDateTime.minusYears(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "All Time":
                        break;
                }

                VolleyRequest.addRequest(getContext(), VolleyRequest.GET_BY_DATE_MOST_REQUESTED_DOMAINS, User.getInstance().getUserID(), "", "", getData, new VolleyResponseListener() {

                    @Override
                    public void onError(Object response) {
                        Log.e(TAG, response.toString());
                    }

                    @Override
                    public void onResponse(Object response) {
                        topDomainsData.clear();

                        try {
                            JSONObject jsonArray = new JSONObject(response.toString());

                            Iterator iterator = jsonArray.keys();

                            while(iterator.hasNext()) {

                                HashMap<String, String> topDomainsDataEntry = new HashMap<>();

                                String domainName = iterator.next().toString();
                                topDomainsDataEntry.put(VolleySingleton.domainName, domainName);

                                JSONObject jsonObject = new JSONObject(jsonArray.getString(domainName));
                                topDomainsDataEntry.put(VolleySingleton.listType, jsonObject.getString(VolleySingleton.listType));
                                topDomainsDataEntry.put(VolleySingleton.num_of_accesses, jsonObject.get(VolleySingleton.count).toString());

                                TopDomainsInfo.getInstance().addDomain(domainName, topDomainsDataEntry);
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
        HashMap<String, String> testData1 = new HashMap<>();
        testData1.put(VolleySingleton.domainName, "testName1");
        testData1.put(VolleySingleton.listType, VolleySingleton.Whitelist);
        testData1.put(VolleySingleton.num_of_accesses, String.valueOf(10));
        HashMap<String, String> testData2 = new HashMap<>();
        testData2.put(VolleySingleton.domainName, "testName2");
        testData2.put(VolleySingleton.listType, VolleySingleton.Blacklist);
        testData2.put(VolleySingleton.num_of_accesses, String.valueOf(20));
        /* Test Data */

        TopDomainsInfo.getInstance().addDomain("testName1", testData1);
        TopDomainsInfo.getInstance().addDomain("testName2", testData2);
        topDomainsData.add("testName1");
        topDomainsData.add("testName2");
        statisticsListAdapter = new StatisticsListAdapter(topDomainsData,getContext());
        ListView topDomainsList = root.findViewById(R.id.top_domains_list);
        topDomainsList.setAdapter(statisticsListAdapter);

        TextView domainTitle = root.findViewById(R.id.domain_text);
        domainTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (domainNameAscending) {
                    domainNameAscending = false;
                    statisticsListAdapter.sortDomainNameDescending();;
                } else {
                    domainNameAscending = true;
                    statisticsListAdapter.sortDomainNameAscending();
                }

            }
        });

        TextView countTitle = root.findViewById(R.id.count_text);
        countTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countAscending) {
                    countAscending = false;
                    statisticsListAdapter.sortCountDescending();
                } else {
                    countAscending = true;
                    statisticsListAdapter.sortCountAscending();
                }
            }
        });

        TextView listTitle = root.findViewById(R.id.list_text);
        listTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listAscending) {
                    listAscending = false;
                    statisticsListAdapter.sortListDescending();
                } else {
                    listAscending = true;
                    statisticsListAdapter.sortListAscending();
                }
            }
        });
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

        // Test Data
        int i  = 1;
        for (String type: listTypes) {
            pieChartData.put(type, 100 * i++);
        }

        /*
        for (String type: listTypes) {
            pieChartData.put(type, 0 );
        }
         */

        HashMap<String, Integer> init;

        // Test Data
        for (String day: days) {
            barChartData.put(day, new HashMap<>());
            init = barChartData.get(day);
            int j = 1;
            for (String type: listTypes) {
                init.put(type, 10 * j++);
            }
        }
        /*
        for (String day: days) {
            barChartData.put(day, new HashMap<>());
            init = barChartData.get(day);
            for (String type: listTypes) {
                init.put(type, 0);
            }
        }

         */

        LocalDateTime localDateTime = LocalDateTime.now();

        // Test Data
        for (int j = 30; j >= 0; j--) {
            lineChartData.put(localDateTime.minusDays(j), (float) j);
            Log.i(TAG, localDateTime.minusDays(j).format(lineChartDayFormat));
        }

        /*
        for (int j = 30; j >= 0; j--) {
            mLineChartData.put(localDateTime.minusDays(j).format(lineChartDayFormat), 0);
        }
         */

    }

    private void getWeeklyData() {

        JSONObject domainRequest = new JSONObject();

        VolleyResponseListener volleyResponseListener = new VolleyResponseListener() {
            @Override
            public void onError(Object response) {
                Log.e(TAG, response.toString());
            }

            @Override
            public void onResponse(Object response) {

                HashMap<String, String> dateHashMap = new HashMap<>();

                int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                int dayIndex = 0;

                switch (dayOfWeek) {
                    case Calendar.MONDAY:
                        dayIndex = 0;
                        break;
                    case Calendar.TUESDAY:
                        dayIndex = 1;
                        break;
                    case Calendar.WEDNESDAY:
                        dayIndex = 2;
                        break;
                    case Calendar.THURSDAY:
                        dayIndex = 3;
                        break;
                    case Calendar.FRIDAY:
                        dayIndex = 4;
                        break;
                    case Calendar.SATURDAY:
                        dayIndex = 5;
                        break;
                    case Calendar.SUNDAY:
                        dayIndex = 6;
                        break;
                }

                LocalDateTime localDateTime = LocalDateTime.now();
                String dayDate;

                for (int i = 0; i < 7; i++) {

                    dayDate = localDateTime.minusDays(i).format(dayFormat);

                    switch(dayIndex) {
                        case 0:
                            dateHashMap.put("MON", dayDate);
                            break;
                        case 1:
                            dateHashMap.put("TUE", dayDate);
                            break;
                        case 2:
                            dateHashMap.put("WED", dayDate);
                            break;
                        case 3:
                            dateHashMap.put("THU", dayDate);
                            break;
                        case 4:
                            dateHashMap.put("FRI", dayDate);
                            break;
                        case 5:
                            dateHashMap.put("SAT", dayDate);
                            break;
                        case 6:
                            dateHashMap.put("SUN", dayDate);
                            break;
                    }

                    dayIndex--;

                    if (dayIndex < 0) {
                        dayIndex = 6;
                    }

                }


                try {

                    JSONObject jsonObject;
                    jsonObject = new JSONObject(response.toString());


                    JSONArray domainList =  jsonObject.getJSONArray(VolleySingleton.activities);
                    Log.i(TAG, response.toString());


                    int numDomains;


                    for (int i = 0; i < domainList.length(); i++) {

                        JSONObject jsonArrayElement = domainList.getJSONObject(i);
                        String listType = jsonArrayElement.getString(VolleySingleton.listType);

                        switch (listType) {
                            case "Whitelist":
                                numDomains = pieChartData.get(VolleySingleton.Whitelist);
                                pieChartData.put(VolleySingleton.Whitelist, ++numDomains);
                                break;
                            case "Blacklist":
                                numDomains = pieChartData.get(VolleySingleton.Blacklist);
                                pieChartData.put(VolleySingleton.Blacklist, ++numDomains);
                                break;
                            case "Safe":
                                numDomains = pieChartData.get(VolleySingleton.Safe);
                                pieChartData.put(VolleySingleton.Safe, ++numDomains);
                                break;
                            case "Malicious":
                                numDomains = pieChartData.get(VolleySingleton.Malicious);
                                pieChartData.put(VolleySingleton.Malicious, ++numDomains);
                                break;
                            case "Undefined":
                                numDomains = pieChartData.get(VolleySingleton.Undefined);
                                pieChartData.put(VolleySingleton.Undefined, ++numDomains);
                                break;
                        }

                        String timeStamp = jsonArrayElement.get(VolleySingleton.timestamp).toString();
                        Log.i(TAG, "response timeStamp: " + timeStamp);

                        LocalDateTime date = LocalDateTime.parse(timeStamp, dayFormat);

                        timeStamp = date.toString();
                        Log.i(TAG, "modified timeStamp: " + timeStamp);


                        if (timeStamp.equals(dateHashMap.get("MON"))) {
                            numDomains = barChartData.get("MON").get(listType);
                            barChartData.get("MON").put(listType, ++numDomains);
                            continue;
                        }

                        if (timeStamp.equals(dateHashMap.get("TUE"))) {
                            numDomains = barChartData.get("TUE").get(listType);
                            barChartData.get("TUE").put(listType, ++numDomains);
                            continue;
                        }

                        if (timeStamp.equals(dateHashMap.get("WED"))) {
                            numDomains = barChartData.get("WED").get(listType);
                            barChartData.get("WED").put(listType, ++numDomains);
                            continue;
                        }

                        if (timeStamp.equals(dateHashMap.get("THU"))) {
                            numDomains = barChartData.get("THU").get(listType);
                            barChartData.get("THU").put(listType, ++numDomains);
                            continue;
                        }

                        if (timeStamp.equals(dateHashMap.get("FRI"))) {
                            numDomains = barChartData.get("FRI").get(listType);
                            barChartData.get("FRI").put(listType, ++numDomains);
                            continue;
                        }

                        if (timeStamp.equals(dateHashMap.get("SAT"))) {
                            numDomains = barChartData.get("SAT").get(listType);
                            barChartData.get("SAT").put(listType, ++numDomains);
                            continue;
                        }

                        if (timeStamp.equals(dateHashMap.get("SUN"))) {
                            numDomains = barChartData.get("SUN").get(listType);
                            barChartData.get("SUN").put(listType, ++numDomains);
                        }

                    }


                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
               
            }
        };


        try {
            domainRequest.put(VolleySingleton.startDate, currentDateTime.toString());
            domainRequest.put(VolleySingleton.endDate, currentDateTime.minusWeeks(1).toString());
            VolleyRequest.addRequest(getContext(), VolleyRequest.GET_RECENT_DOMAIN_REQUEST_ACTIVITY, User.getInstance().getUserID(), "", "", domainRequest, volleyResponseListener);
        } catch (JSONException e) {
            Log.e(TAG, "Initialize Data Failed");
            e.printStackTrace();
        }

    }

    private void getMonthlyData() {

        for (int i = 0; i <= 30; i++) {
            JSONObject domainRequest = new JSONObject();
            try {
                domainRequest.put(VolleySingleton.startDate, dayFormat.format(currentDateTime) + "T23:59:59.999Z");
                domainRequest.put(VolleySingleton.endDate, dayFormat.format(currentDateTime) + "T00:00:00.000Z");
                int finalI = i;
                VolleyRequest.addRequest(getContext(), VolleyRequest.GET_RECENT_DOMAIN_REQUEST_ACTIVITY, User.getInstance().getUserID(), "", "", domainRequest, new VolleyResponseListener() {
                    @Override
                    public void onError(Object response) {
                        Log.e(TAG, response.toString());
                    }

                    @Override
                    public void onResponse(Object response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response.toString());
                            Float numAccesses = (Float) jsonResponse.get(VolleySingleton.count);
                            lineChartData.put(currentDateTime.minusDays(finalI), numAccesses);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

        pieEntries.add(new PieEntry(pieChartData.get(VolleySingleton.Whitelist), VolleySingleton.Whitelist));
        pieEntries.add(new PieEntry(pieChartData.get(VolleySingleton.Blacklist), VolleySingleton.Blacklist));
        pieEntries.add(new PieEntry(pieChartData.get(VolleySingleton.Safe), VolleySingleton.Safe));
        pieEntries.add(new PieEntry(pieChartData.get(VolleySingleton.Malicious), VolleySingleton.Malicious));
        pieEntries.add(new PieEntry(pieChartData.get(VolleySingleton.Undefined), VolleySingleton.Undefined));

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

        int i = 0;
        for (String day: days) {
            float whiteListedDomains = barChartData.get(day).get(VolleySingleton.Whitelist);
            float blackListedDomains = barChartData.get(day).get(VolleySingleton.Blacklist);
            float safeDomains = barChartData.get(day).get(VolleySingleton.Safe);
            float maliciousDomains = barChartData.get(day).get(VolleySingleton.Malicious);
            float undefinedDomains = barChartData.get(day).get(VolleySingleton.Undefined);

            entries.add(new BarEntry(
                    i++,
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

        Iterator iterator = lineChartData.entrySet().iterator();

        int i = 0;
        while (iterator.hasNext()) {

            Map.Entry pair = (Map.Entry) iterator.next();
            lineData.add(new Entry(i++, (Float) pair.getValue()));

            LocalDateTime localDateTime = (LocalDateTime) pair.getKey();
            xAxisLabels.add(localDateTime.format(lineChartDayFormat));

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