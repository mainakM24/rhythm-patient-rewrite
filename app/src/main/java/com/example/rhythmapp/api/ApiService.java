package com.example.rhythmapp.api;

import com.example.rhythmapp.models.Advice;
import com.example.rhythmapp.models.ApiResponse;
import com.example.rhythmapp.models.DetailedSession;
import com.example.rhythmapp.models.Device;
import com.example.rhythmapp.models.Doctor;
import com.example.rhythmapp.models.EcgData;
import com.example.rhythmapp.models.GranularSession;
import com.example.rhythmapp.models.Patient;
import com.example.rhythmapp.models.Session;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @GET("user/v1/validate/patient/{uid}/{upw}")
    Call<ApiResponse<Patient>> getUserByCredential(
            @Path("uid") String userId,
            @Path("upw") String userPassword
    );

    @GET("patient/v1/assigneddoctor/{uid}")
    Call<ApiResponse<Doctor>> getDoctorDetails(
            @Path("uid") String userId
    );

    @GET("patient/v1/assigneddevice/{uid}")
    Call<ApiResponse<Device>> getDeviceDetails(
            @Path("uid") String userId
    );

    @GET("patient/v1/advicelog/{uid}")
    Call<ApiResponse<Advice>> getLoggedAdvice(
            @Path("uid") String userId,
            @Query("limit") int limit
    );

    @GET("data/v1/get/summary/{uid}")
    Call<ApiResponse<Session>> getSessionDetails(
            @Path("uid") String userId
    );

    @GET("data/v1/get/details/5min/{uid}/{sid}")
    Call<ApiResponse<DetailedSession>> getDetailedSessionDetails(
            @Path("uid") String userId,
            @Path("sid") String sessionId
    );

    @GET("data/v1/get/details/{uid}/{sid}/{stdate}/{enddate}")
    Call<ApiResponse<GranularSession>> getGranularSessionDetails(
            @Path("uid") String userId,
            @Path("sid") String sessionId,
            @Path("stdate") String startDate,
            @Path("enddate") String endDate
    );

    @GET("data/v1/get/details/ecg/{uid}/{sid}/{stdate}/{enddate}")
    Call<ApiResponse<EcgData>> getEcgData(
            @Path("uid") String userId,
            @Path("sid") String sessionId,
            @Path("stdate") String startDate,
            @Path("enddate") String endDate
    );

    @GET("user/v1/changepassword/{uid}/{opass}/{npass}")
    Call<Void> changePassword(
            @Path("uid") String userId,
            @Path("opass") String oldPassword,
            @Path("npass") String newPassword
    );

}
