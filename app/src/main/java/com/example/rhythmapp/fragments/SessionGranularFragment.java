package com.example.rhythmapp.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhythmapp.R;
import com.example.rhythmapp.api.ApiService;
import com.example.rhythmapp.api.RetrofitClient;
import com.example.rhythmapp.databinding.FragmentSessionGranularBinding;
import com.example.rhythmapp.models.ApiResponse;
import com.example.rhythmapp.models.DetailedSession;
import com.example.rhythmapp.models.EcgData;
import com.example.rhythmapp.models.GranularSession;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SessionGranularFragment extends Fragment {
    ApiService apiService;
    FragmentSessionGranularBinding binding;
    String userId, sessionId, startDate, endDate;
    List<GranularSession> granularSessionList = new ArrayList<>();
    List<EcgData> ecgDataList = new ArrayList<>();
    private boolean dataLoaded = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSessionGranularBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle != null){
            sessionId = bundle.getString("SESSION_ID");
            userId = bundle.getString("USER_ID");
            startDate = bundle.getString("START_DATE");
            endDate = bundle.getString("END_DATE");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!dataLoaded || granularSessionList.isEmpty()) {

            fetchSessionData();
        } else {

            displayData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchSessionData(){
        if (getContext() == null) return;
        apiService = RetrofitClient.getApiService();
        Call<ApiResponse<GranularSession>> gSessionResponseCall = apiService.getGranularSessionDetails(userId, sessionId, startDate, endDate);

        gSessionResponseCall.enqueue(new Callback<ApiResponse<GranularSession>>() {
            @Override
            public void onResponse(Call<ApiResponse<GranularSession>> call, Response<ApiResponse<GranularSession>> response) {
                if (!isAdded() || binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    granularSessionList = response.body().getItems();
                    dataLoaded = true;
                    displayData();
                } else {
                    System.err.println("Error: " + response.code() + " - " + response);

                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GranularSession>> call, Throwable throwable) {
                if (!isAdded() || binding == null) return;

                Log.e("api", "onFailure: granular", throwable);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayData(){
        if (binding == null || granularSessionList == null) return;

        for (GranularSession session : granularSessionList){
            addTableRow(session);
        }

        setDataAnomalyPieChart(granularSessionList);
        configureBarChart(binding.bcAnomalyDistribution);
        setDataAnomalyBarChart(granularSessionList);
    }

    private void addTableRow(GranularSession granularSession){
        int pad_6 = dpToPx(6);
        TableRow tableRow = new TableRow(requireContext());
        tableRow.setBackgroundColor(getResources().getColor(R.color.table_background, requireContext().getTheme()));
        tableRow.setPadding(0, pad_6, 0, pad_6);

        TextView[] textViews = new TextView[6];

        String[] data = {
                "🔍",
                granularSession.getStart_date(),
                granularSession.getEnd_date(),
                String.valueOf(granularSession.getTotal_analyzed_duration()),
                String.valueOf(granularSession.getTotal_analyzed_beat()),
                String.valueOf(granularSession.getBpm())
        };

        for (int i = 0; i < textViews.length; i++) {
            textViews[i] = new TextView(requireContext());
            styleTextView(textViews[i]);
            textViews[i].setText(data[i] != null ? data[i] : "N/A");
            tableRow.addView(textViews[i]);
        }

        textViews[0].setOnClickListener(v -> {
            fetchEcgData(granularSession.getHidstart_date(), granularSession.getHidend_date());
        });

        binding.tlGranularSessionList.addView(tableRow);
    }

    private void styleTextView(TextView textView) {
        // Set layout gravity
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = android.view.Gravity.CENTER;
        textView.setLayoutParams(layoutParams);

        // Set margin
        TableRow.LayoutParams params = (TableRow.LayoutParams) textView.getLayoutParams();
        params.setMargins(1, 1, 1, 1); // 1dp margin on all sides
        textView.setLayoutParams(params);

        // Set padding
        int paddingInPx = dpToPx(6); // Convert 6dp to pixels
        textView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void configureBarChart(BarChart barChart) {
        // Enable touch gestures
        barChart.setTouchEnabled(true);
        barChart.getDescription().setEnabled(false);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(true);
        barChart.setHighlightPerTapEnabled(true);
        barChart.animateY(1000);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.setDrawValueAboveBar(true);


        // Configure X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);

        // Configure Y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void setDataAnomalyPieChart(List<GranularSession> sessionList) {
        float nb_beat = 0f, ap_beat = 0f;

        for (GranularSession session : sessionList) {
            nb_beat += session.getNb_count();
            ap_beat += session.getApb_count();
        }

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(nb_beat, "Normal Beat"));
        entries.add(new PieEntry(ap_beat, "Atrial Premature Beat"));

        PieDataSet pieDataSet = new PieDataSet(entries, "Anomaly");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(pieDataSet);

        Legend l = binding.pcAnomalyDistribution.getLegend();
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);

        binding.pcAnomalyDistribution.setData(pieData);
        binding.pcAnomalyDistribution.setEntryLabelTextSize(10f);
        binding.pcAnomalyDistribution.getDescription().setEnabled(false);
        binding.pcAnomalyDistribution.setDrawHoleEnabled(false);
        binding.pcAnomalyDistribution.setEntryLabelColor(Color.WHITE);
        binding.pcAnomalyDistribution.setCenterTextColor(Color.WHITE);
        binding.pcAnomalyDistribution.animateY(1000);
        binding.pcAnomalyDistribution.invalidate();
    }

    private void setDataAnomalyBarChart(List<GranularSession> sessionList) {

        ArrayList<IBarDataSet> allDataSets = new ArrayList<>();
        BarData barData;
        BarDataSet barDataSet;

        if (sessionList.size() < 2) {

            ArrayList<BarEntry> entries = new ArrayList<>();

            entries.add(new BarEntry(0.5f, sessionList.get(0).getNb_count()));
            entries.add(new BarEntry(1.5f, sessionList.get(0).getApb_count()));

            barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

            barData = new BarData(barDataSet);
            barData.setBarWidth(0.5f);

            binding.bcAnomalyDistribution.setData(barData);
        } else {
            int[] sessionColors = ColorTemplate.VORDIPLOM_COLORS;

            for (int sessionIndex = 0; sessionIndex < sessionList.size(); sessionIndex++) {
                // Create entry list for this specific session
                ArrayList<BarEntry> sessionEntries = new ArrayList<>();

                // Add entries for MIN, MED, MAX for this session
                // Use 0, 1, 2 as X values to position them in different groups
                sessionEntries.add(new BarEntry(0, sessionList.get(sessionIndex).getNb_count()));
                sessionEntries.add(new BarEntry(1, sessionList.get(sessionIndex).getApb_count()));


                // Create dataset for this session
                BarDataSet sessionDataSet = new BarDataSet(sessionEntries, sessionList.get(sessionIndex).getHidstart_date());
                sessionDataSet.setColor(sessionColors[sessionIndex]);

                allDataSets.add(sessionDataSet);
            }

            barData = new BarData(allDataSets);

            float groupSpace = 0.3f;
            float barSpace = 0.02f;
            float barWidth = (1f - (sessionList.size() * barSpace + groupSpace)) / sessionList.size();

            barData.setBarWidth(barWidth);
            binding.bcAnomalyDistribution.setData(barData);
            binding.bcAnomalyDistribution.groupBars(0, groupSpace, barSpace);
        }

        XAxis xAxis = binding.bcAnomalyDistribution.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(2);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"NB Count", "APB Count"}));


        binding.bcAnomalyDistribution.getBarData().setValueTextColor(Color.WHITE);
        binding.bcAnomalyDistribution.invalidate();
    }

    private void fetchEcgData(String starTime, String endTime) {
        if (getContext() == null) return;
        Call<ApiResponse<EcgData>> ecgResponseCall = apiService.getEcgData(userId, sessionId, starTime, endTime);
        ecgResponseCall.enqueue(new Callback<ApiResponse<EcgData>>() {
            @Override
            public void onResponse(Call<ApiResponse<EcgData>> call, Response<ApiResponse<EcgData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ecgDataList = response.body().getItems();
                    addEcgGraph(ecgDataList);
                } else {
                    System.err.println("Error: " + response.code() + " - " + response);

                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<EcgData>> call, Throwable throwable) {

                Log.e("api", "onFailure: granular", throwable);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addEcgGraph(List<EcgData> ecgDataList) {
        binding.lcEcg.clear();
        binding.lcEcg.invalidate();

        List<Entry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (EcgData ecgData : ecgDataList) {
            Entry entry = new Entry(ecgData.getSeq_no(), (float) ecgData.getVoltage());
            entries.add(entry);

            // Determine color based on anomaly flag
            if (ecgData.getAnamoly_flag().equals("N")) {
                colors.add(Color.CYAN);  // Normal data
            } else {
                colors.add(Color.RED);   // Abnormal data
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "ECG Data");

        // Configure the dataset
        dataSet.setColors(colors);
        dataSet.setLineWidth(1.5f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        // Enable color per data point
        dataSet.setDrawVerticalHighlightIndicator(true);

        LineData lineData = new LineData(dataSet);
        binding.lcEcg.setData(lineData);

        // Styling and configuration
        binding.lcEcg.getDescription().setEnabled(false);
        binding.lcEcg.getLegend().setTextColor(Color.WHITE);
        binding.lcEcg.getXAxis().setDrawLabels(false);
        binding.lcEcg.getAxisLeft().setTextColor(Color.WHITE);
        binding.lcEcg.getAxisRight().setEnabled(false);
        binding.lcEcg.setTouchEnabled(true);
        binding.lcEcg.setDragEnabled(true);
        binding.lcEcg.setScaleEnabled(true);
        binding.lcEcg.getLegend().setEnabled(false);

        binding.lcEcg.invalidate();
        binding.lcEcg.setVisibility(View.VISIBLE);
        binding.tvEcgChartTitle.setVisibility(View.VISIBLE);

        binding.tvGranularPath.setVisibility(View.GONE);
        binding.tvEcgPath.setVisibility(View.VISIBLE);
    }

}