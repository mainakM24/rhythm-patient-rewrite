package com.example.rhythmapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rhythmapp.api.ApiService;
import com.example.rhythmapp.api.RetrofitClient;
import com.example.rhythmapp.databinding.FragmentChangePasswordBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordFragment extends Fragment {

    FragmentChangePasswordBinding binding;
    SharedPreferences.Editor editor;
    ApiService apiService;
    String oldPassword;
    String newPassword;
    String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getContext() != null) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("login", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            userId = sharedPreferences.getString("userId", "error");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btChangePassword.setOnClickListener(view1 -> {
            oldPassword = binding.etOldPass.getText().toString().trim();
            newPassword = binding.etNewPassword.getText().toString().trim();

            apiService = RetrofitClient.getApiService();
            Call<Void> call = apiService.changePassword(userId, oldPassword, newPassword);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(getContext(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Wrong credentials", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {

                }
            });
        });


    }
}