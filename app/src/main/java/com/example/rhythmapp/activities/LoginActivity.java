package com.example.rhythmapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;

import com.example.rhythmapp.api.ApiService;
import com.example.rhythmapp.api.RetrofitClient;
import com.example.rhythmapp.databinding.ActivityLoginBinding;
import com.example.rhythmapp.models.ApiResponse;
import com.example.rhythmapp.models.Patient;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = new Intent(this, BaseActivity.class);

        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        boolean remembered = sharedPreferences.getBoolean("remembered", false);
        if (remembered){
            startActivity(intent);
            finish();
        }

        //clicking sign in button
        binding.btSignIn.setOnClickListener(view -> {


            binding.tilUsername.setError(null);
            binding.tilPassword.setError(null);

            //retrieving form data
            String username = Objects.requireNonNull(binding.etUsername.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();


            //Calling API using retrofit
            ApiService apiService = RetrofitClient.getApiService();
            Call<ApiResponse<Patient>> apiResponseCall = apiService.getUserByCredential(username, password);

            apiResponseCall.enqueue(new Callback<ApiResponse<Patient>>() {
                @Override
                public void onResponse(Call<ApiResponse<Patient>> call, Response<ApiResponse<Patient>> response) {
                    if (response.isSuccessful()){
                        ApiResponse<Patient> apiResponse = response.body();
                        if (apiResponse.getCount() == 1){
                            SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (binding.cbRememberMe.isChecked()){
                                editor.putBoolean("remembered", true);
                            }
                            editor.putString("userId", username);
                            editor.putString("password", password);
                            editor.apply();
                            startActivity(intent);
                            finish();
                        } else {
                            binding.tilUsername.setErrorEnabled(true);
                            binding.tilPassword.setErrorEnabled(true);

                            binding.tilUsername.setError("Check Username");
                            binding.tilPassword.setError("Check Password");
                        }
                    } else {
                        System.err.println("Error: " + response.code() + " - " + response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Patient>> call, Throwable throwable) {
                    Log.e("api", "onFailure: ", throwable);
                }
            });

        });
    }
}