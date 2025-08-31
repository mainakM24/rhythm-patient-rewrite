package com.example.rhythmapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
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
import com.example.rhythmapp.databinding.FragmentCheckReportBinding;
import com.example.rhythmapp.models.ApiResponse;
import com.example.rhythmapp.models.Session;
import com.example.rhythmapp.utils.PdfUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckReportFragment extends Fragment {


    private FragmentCheckReportBinding binding;
    private List<Session> sessionList = new ArrayList<>();
    private String userId;
    private boolean dataLoaded = false; // Track if data has been loaded

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve credentials to avoid repeating this in onViewCreated
        if (getContext() != null) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("login", MODE_PRIVATE);
            userId = sharedPreferences.getString("userId", "error");
            //password = sharedPreferences.getString("password", "error");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCheckReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!dataLoaded || sessionList.isEmpty()) {
            fetchSessionData();
        } else {
            displayData(); // If we already have data, just redisplay it
        }

        binding.btPdf.setOnClickListener(view1 -> downloadPdf());
    }

    private void fetchSessionData() {
        if (getContext() == null) return;

        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse<Session>> sessionApiResponseCall = apiService.getSessionDetails(userId);

        sessionApiResponseCall.enqueue(new Callback<ApiResponse<Session>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Session>> call, @NonNull Response<ApiResponse<Session>> response) {
                // Always check if fragment is still attached to context
                if (!isAdded() || binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    sessionList = response.body().getItems();
                    dataLoaded = true;
                    displayData();
                } else {
                    System.err.println("Error: " + response.code() + " - " + response);
                    // Show error to user
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Session>> call, @NonNull Throwable throwable) {
                // Always check if fragment is still attached to context
                if (!isAdded() || binding == null) return;

                Log.e("api", "onFailure: advice", throwable);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayData() {
        if (binding == null || sessionList == null || sessionList.isEmpty()) return;

        // Clear existing rows first to prevent duplication
        for (int i = binding.tlSessionList.getChildCount() - 1; i > 0; i--) {
            binding.tlSessionList.removeViewAt(i);
        }

        // Set data to table
        for (Session session : sessionList) {
            addTableRow(session);
        }

        // Configure and set data for the BPM bar chart
        configureBarChart(binding.bcBpmAnalysis);
        setDataBarChart(sessionList);

        // Configure and set data for the BPM bar chart
        setDataAnomalyPieChart(sessionList);

        // Configure and set data for the Session wise Anomaly Chart
        configureBarChart(binding.bcAnomalyDistribution);
        setDataAnomalyBarChart(sessionList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }

    private void addTableRow(Session session) {
        int pad_6 = dpToPx(6);
        TableRow tableRow = new TableRow(requireContext());
        tableRow.setBackgroundColor(getResources().getColor(R.color.table_background, requireContext().getTheme()));
        tableRow.setPadding(0, pad_6, 0, pad_6);

        TextView[] textViews = new TextView[8];
        String[] data = {
                session.getSession_id(),
                session.getStart_date(),
                session.getEnd_date(),
                String.valueOf(session.getTotal_analyzed_duration()),
                String.valueOf(session.getTotal_analyzed_beat()),
                String.valueOf(session.getMin_bpm()),
                String.valueOf(session.getMed_bpm()),
                String.valueOf(session.getMax_bpm())
        };


        for (int i = 0; i < textViews.length; i++) {
            textViews[i] = new TextView(requireContext());
            styleTextView(textViews[i]);
            textViews[i].setText(data[i] != null ? data[i] : "N/A");
            tableRow.addView(textViews[i]);
        }

        textViews[0].setTextColor(getResources().getColor(R.color.link, requireContext().getTheme()));
        textViews[0].setTypeface(null, Typeface.BOLD);
        textViews[0].setOnClickListener(v -> navigateToSessionDetailsFragment(session.getSession_id()));

        binding.tlSessionList.addView(tableRow);
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

    private void navigateToSessionDetailsFragment(String sessionId) {
        SessionDetailsFragment sessionDetailsFragment = new SessionDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("SESSION_ID", sessionId);

        sessionDetailsFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, sessionDetailsFragment)
                .addToBackStack(null)
                .commit();
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

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int dataSetIndex = h.getDataSetIndex();
                if (dataSetIndex >= 0 && dataSetIndex < sessionList.size()) {

                    String sessionId = sessionList.get(h.getDataSetIndex()).getSession_id();
                    navigateToSessionDetailsFragment(sessionId);

                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void setDataBarChart(List<Session> sessionList) {

        BarData barData;

        if ( sessionList.size() == 1) {

            ArrayList<BarEntry> entries = new ArrayList<>();

            entries.add(new BarEntry(0f, sessionList.get(0).getMin_bpm()));
            entries.add(new BarEntry(1f, sessionList.get(0).getMed_bpm()));
            entries.add(new BarEntry(2f, sessionList.get(0).getMax_bpm()));

            BarDataSet barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

            barData = new BarData(barDataSet);
            barData.setBarWidth(0.9f);

            binding.bcBpmAnalysis.setData(barData);

        } else {
            ArrayList<IBarDataSet> allDataSets = new ArrayList<>();
            int[] sessionColors = ColorTemplate.VORDIPLOM_COLORS;

               // Create each session's dataset with its own color
            for (int sessionIndex = 0; sessionIndex < sessionList.size(); sessionIndex++) {
                   // Create entry list for this specific session
                   ArrayList<BarEntry> sessionEntries = new ArrayList<>();

                   // Add entries for MIN, MED, MAX for this session
                   // Use 0, 1, 2 as X values to position them in different groups
                   sessionEntries.add(new BarEntry(0, sessionList.get(sessionIndex).getMin_bpm()));
                   sessionEntries.add(new BarEntry(1, sessionList.get(sessionIndex).getMed_bpm()));
                   sessionEntries.add(new BarEntry(2, sessionList.get(sessionIndex).getMax_bpm()));

                   // Create dataset for this session
                   BarDataSet sessionDataSet = new BarDataSet(sessionEntries, sessionList.get(sessionIndex).getSession_id());
                   sessionDataSet.setColor(sessionColors[sessionIndex]);

                   allDataSets.add(sessionDataSet);
               }

            // Create BarData with all sessions
            barData = new BarData(allDataSets);

            // Set bar width and spacing
            float groupSpace = 0.3f; // Space between MIN, MED, MAX groups
            float barSpace = 0.02f; // Space between session bars within a group
            float barWidth = (1f - (sessionList.size() * barSpace + groupSpace)) / sessionList.size(); // Width of each bar

            // Group the bars
            barData.setBarWidth(barWidth);
            binding.bcBpmAnalysis.setData(barData);
            binding.bcBpmAnalysis.groupBars(0, groupSpace, barSpace);
        }


        XAxis xAxis = binding.bcBpmAnalysis.getXAxis();
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(3);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"MIN BPM", "MED BPM", "MAX BPM"}));

        // Set data to chart
        binding.bcBpmAnalysis.getBarData().setValueTextColor(Color.WHITE);
        binding.bcBpmAnalysis.invalidate();
    }

    private void setDataAnomalyPieChart(List<Session> sessionList) {

        float nb_beat = 0f, ap_beat = 0f;

        for (Session session : sessionList) {
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

    private void setDataAnomalyBarChart(List<Session> sessionList) {

        BarData barData;

        if (sessionList.size() == 1){
            BarDataSet barDataSet;
            ArrayList<BarEntry> entries = new ArrayList<>();

            entries.add(new BarEntry(0f, sessionList.get(0).getNb_count()));
            entries.add(new BarEntry(1f, sessionList.get(0).getApb_count()));

            barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

            barData = new BarData(barDataSet);
            barData.setBarWidth(0.9f);
            binding.bcAnomalyDistribution.setData(barData);
        } else {
            int[] sessionColors = ColorTemplate.VORDIPLOM_COLORS;
            ArrayList<IBarDataSet> allDataSets = new ArrayList<>();
            for (int sessionIndex = 0; sessionIndex < sessionList.size(); sessionIndex++) {
                // Create entry list for this specific session
                ArrayList<BarEntry> sessionEntries = new ArrayList<>();

                // Add entries for MIN, MED, MAX for this session
                // Use 0, 1, 2 as X values to position them in different groups
                sessionEntries.add(new BarEntry(0, sessionList.get(sessionIndex).getNb_count()));
                sessionEntries.add(new BarEntry(1, sessionList.get(sessionIndex).getApb_count()));


                // Create dataset for this session
                BarDataSet sessionDataSet = new BarDataSet(sessionEntries, sessionList.get(sessionIndex).getSession_id());
                sessionDataSet.setColor(sessionColors[sessionIndex]);

                allDataSets.add(sessionDataSet);
            }
            // Create BarData with all sessions
            barData = new BarData(allDataSets);

            // Set bar width and spacing
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

        // Set data to chart
        binding.bcAnomalyDistribution.getBarData().setValueTextColor(Color.WHITE);
        binding.bcAnomalyDistribution.invalidate();
    }

    private void downloadPdf() {
        binding.btPdf.setVisibility(View.INVISIBLE);
        Toast.makeText(requireContext(), "Downloading pdf...", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1232);
        PdfUtil.createPdfFromCurrentScreen(binding.scrollView);

        new Handler(Looper.getMainLooper()).postDelayed(() -> binding.scrollView.requestLayout(), 500);

        Toast.makeText(requireContext(), "Downloaded at  Downloads/", Toast.LENGTH_SHORT).show();
        binding.btPdf.setVisibility(View.VISIBLE);
    }
}

