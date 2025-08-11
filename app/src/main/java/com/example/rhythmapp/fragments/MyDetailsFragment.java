package com.example.rhythmapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
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


import com.example.rhythmapp.adapters.AdviceAdapter;
import com.example.rhythmapp.api.ApiService;
import com.example.rhythmapp.api.RetrofitClient;
import com.example.rhythmapp.databinding.FragmentMyDetailsBinding;
import com.example.rhythmapp.models.Advice;
import com.example.rhythmapp.models.ApiResponse;
import com.example.rhythmapp.models.Doctor;
import com.example.rhythmapp.models.Patient;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyDetailsFragment extends Fragment {

    private FragmentMyDetailsBinding binding;
    private List<Advice> adviceList = new ArrayList<>();
    AdviceAdapter adviceAdapter;
    int limitMultiplier = 1;
    final int LIMIT = 4;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMyDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("login", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "error");
        String password = sharedPreferences.getString("password", "error");

        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse<Patient>> patientApiResponseCall = apiService.getUserByCredential(userId, password);
        Call<ApiResponse<Doctor>> doctorApiResponseCall = apiService.getDoctorDetails(userId);
        Call<ApiResponse<Advice>> adviceApiResponseCall = apiService.getLoggedAdvice(userId, LIMIT * limitMultiplier);

        patientApiResponseCall.enqueue(new Callback<ApiResponse<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Patient>> call, @NonNull Response<ApiResponse<Patient>> response) {

                if (response.isSuccessful()){
                    ApiResponse<Patient> apiResponse = response.body();
                    assert apiResponse != null;
                    Patient patient = apiResponse.getItems().get(0);

                   loadMyInformationTable(patient);

                } else {
                    System.err.println("Error: " + response.code() + " - " + response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Patient>> call, @NonNull Throwable throwable) {
                Log.e("api", "onFailure: patient", throwable);
            }
        });

        doctorApiResponseCall.enqueue(new Callback<ApiResponse<Doctor>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Doctor>> call, @NonNull Response<ApiResponse<Doctor>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<Doctor> apiResponse = response.body();

                    Doctor doctor = apiResponse.getItems().get(0);

                    loadAssignedDoctorTable(doctor);

                } else {
                    System.err.println("Error: " + response.code() + " - " + response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Doctor>> call, @NonNull Throwable throwable) {
                Log.e("api", "onFailure: doctor", throwable);
            }
        });

        adviceApiResponseCall.enqueue(new Callback<ApiResponse<Advice>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Advice>> call, @NonNull Response<ApiResponse<Advice>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    adviceList = response.body().getItems();
                    adviceAdapter = new AdviceAdapter(adviceList);
                    binding.rvAdvice.setAdapter(adviceAdapter);
                } else {
                    System.err.println("Error: " + response.code() + " - " + response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Advice>> call, @NonNull Throwable throwable) {
                Log.e("api", "onFailure: advice", throwable);
            }
        });

        binding.tvLoadMore.setOnClickListener(view1 -> loadMoreAdviceRecyclerView(apiService, userId, adviceAdapter));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addTableRow(String label, String value) {

        TableRow row = new TableRow(getContext());

        // Create TextView for the label (first column)
        TextView labelTextView = new TextView(requireContext());
        labelTextView.setTextColor(Color.WHITE);
        labelTextView.setText(label);
        labelTextView.setPadding(16, 16, 16, 16); // Add padding
        labelTextView.setTextSize(16); // Set text size
        row.addView(labelTextView);

        // Create TextView for the value (second column)
        TextView valueTextView = new TextView(requireContext());
        valueTextView.setText(value != null ? value : "N/A"); // Handle null values
        valueTextView.setPadding(16, 16, 16, 16); // Add padding
        valueTextView.setTextSize(16); // Set text size
        row.addView(valueTextView);

        // Add the row to the TableLayout
        binding.tlMyInformation.addView(row);
    }

    private void loadMyInformationTable(@NonNull Patient patient){

        addTableRow("Patient ID", patient.getPatient_id());
        addTableRow("Name", patient.getP_name());
        addTableRow("Date of Birth", patient.getP_dob());
        addTableRow("Gender", patient.getP_sex());
        addTableRow("House No", patient.getP_house_no());
        addTableRow("Street Name", patient.getP_street_name());
        addTableRow("City", patient.getP_city());
        addTableRow("Pin Code", patient.getP_pin_cdoe());
        addTableRow("State", patient.getP_state());
        addTableRow("Country", patient.getP_country());
        addTableRow("Mobile", patient.getP_mobile());
        addTableRow("Email", patient.getP_email());
        addTableRow("Enlisted Date", patient.getP_start_date());

        //Notes Table
        binding.tvNotes.setText(patient.getP_notes() != null ? patient.getP_notes() : "Nothing to show here." );

        binding.progressBar.setVisibility(View.GONE);

    }

    private void loadAssignedDoctorTable(@NonNull Doctor doctor) {

        binding.tvDoctorId.setText(doctor.getDoctor_id() != null ? doctor.getDoctor_id() : "N/A");
        binding.tvDoctorName.setText(doctor.getD_name() != null ? doctor.getD_name() : "N/A");
        binding.tvAdmissionDate.setText(doctor.getFormattedPd_admission_date() != null ? doctor.getFormattedPd_admission_date() : "N/A");
        binding.tvHospitalName.setText(doctor.getPd_hospital_name() != null ? doctor.getPd_hospital_name() : "N/A");
        binding.tvBoardingId.setText(doctor.getBoarding_id() != null ? doctor.getBoarding_id() : "N/A");

    }

    private void loadMoreAdviceRecyclerView( ApiService apiService, String userId, AdviceAdapter adviceAdapter ) {
        Call<ApiResponse<Advice>> adviceApiResponseCall = apiService.getLoggedAdvice(userId, LIMIT * ++limitMultiplier);
        adviceApiResponseCall.enqueue(new Callback<ApiResponse<Advice>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Advice>> call, @NonNull Response<ApiResponse<Advice>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    int oldSize = adviceList.size();
                    adviceList.clear();
                    adviceList.addAll(response.body().getItems());
                    adviceAdapter.notifyItemRangeChanged(oldSize, adviceList.size());
                } else {
                    System.err.println("Error: " + response.code() + " - " + response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Advice>> call, @NonNull Throwable throwable) {
                Log.e("api", "onFailure: advice", throwable);
            }
        });
    }
}