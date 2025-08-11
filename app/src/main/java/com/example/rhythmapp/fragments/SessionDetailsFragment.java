package com.example.rhythmapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.rhythmapp.databinding.FragmentSessionDetailsBinding;
import com.example.rhythmapp.models.ApiResponse;
import com.example.rhythmapp.models.DetailedSession;
import com.example.rhythmapp.utils.PdfUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SessionDetailsFragment extends Fragment {

    FragmentSessionDetailsBinding binding;
    List<DetailedSession> detailedSessionList = new ArrayList<>();
    String sessionId;
    String userId;
    private boolean dataLoaded = false;

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
        binding = FragmentSessionDetailsBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle != null ) sessionId = bundle.getString("SESSION_ID");

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Only fetch data if it hasn't been loaded or if sessionList is empty
        if (!dataLoaded || detailedSessionList.isEmpty()) {

            fetchSessionData();
        } else {
            // If we already have data, just redisplay it

            displayData();
        }

        binding.btPdf.setOnClickListener(view1 -> downloadPdf());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchSessionData() {
        if (getContext() == null) return;
        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse<DetailedSession>> sessionApiResponseCall =  apiService.getDetailedSessionDetails(userId,sessionId);

        sessionApiResponseCall.enqueue(new Callback<ApiResponse<DetailedSession>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<DetailedSession>> call, @NonNull Response<ApiResponse<DetailedSession>> response) {
                if (!isAdded() || binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    detailedSessionList = response.body().getItems();
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
            public void onFailure(@NonNull Call<ApiResponse<DetailedSession>> call, @NonNull Throwable throwable) {
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
        if (binding == null || detailedSessionList == null) return;

        for (DetailedSession session : detailedSessionList) {
            addTableRow(session);
        }

        setDataAnomalyPieChart(detailedSessionList);
        configureBarChart(binding.bcAnomalyDistribution);
        setDataAnomalyBarChart(detailedSessionList);

    }

    private void addTableRow(DetailedSession detailedSession) {
        int pad_6 = dpToPx(6);
        TableRow tableRow = new TableRow(requireContext());
        tableRow.setBackgroundColor(getResources().getColor(R.color.table_background, requireContext().getTheme()));
        tableRow.setPadding(0, pad_6, 0, pad_6);

        TextView[] textViews = new TextView[6];

        String[] data = {
                "🔍",
                detailedSession.getStart_date(),
                detailedSession.getEnd_date(),
                String.valueOf(detailedSession.getTotal_analyzed_duration()),
                String.valueOf(detailedSession.getTotal_analyzed_beat()),
                String.valueOf(detailedSession.getBpm())
        };

        for (int i = 0; i < textViews.length; i++) {
            textViews[i] = new TextView(requireContext());
            styleTextView(textViews[i]);
            textViews[i].setText(data[i] != null ? data[i] : "N/A");
            tableRow.addView(textViews[i]);
        }

        textViews[0].setOnClickListener(v -> navigateToGranularSessionFragment(detailedSession.hidstart_date, detailedSession.hidend_date));

        binding.tlDetailedSessionList.addView(tableRow);
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

    private void setDataAnomalyPieChart(List<DetailedSession> sessionList) {
        float nb_beat = 0f, ap_beat = 0f;

        for (DetailedSession session : sessionList) {
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

    private void setDataAnomalyBarChart(List<DetailedSession> sessionList) {

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
                BarDataSet sessionDataSet = new BarDataSet(sessionEntries, sessionList.get(sessionIndex).start_datetime);
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

    private void navigateToGranularSessionFragment(String startDate, String endDate){
        SessionGranularFragment fragment = new SessionGranularFragment();

        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userId);
        bundle.putString("SESSION_ID", sessionId);
        bundle.putString("START_DATE", startDate);
        bundle.putString("END_DATE", endDate);

        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void downloadPdf() {
        binding.btPdf.setVisibility(View.INVISIBLE);
        Toast.makeText(requireContext(), "Downloading pdf...", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1232);
        PdfUtil.createPdfFromCurrentScreen(binding.scrollView);

        new Handler(Looper.getMainLooper()).postDelayed(() -> binding.scrollView.requestLayout(), 500);

        Toast.makeText(requireContext(), "Downloaded", Toast.LENGTH_SHORT).show();
        binding.btPdf.setVisibility(View.VISIBLE);
    }
}