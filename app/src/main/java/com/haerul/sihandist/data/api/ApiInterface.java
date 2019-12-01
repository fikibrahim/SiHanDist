package com.haerul.sihandist.data.api;

import com.google.gson.JsonObject;
import com.haerul.sihandist.data.entity.Base64Data;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    
    @POST("api/login")
    Call<JsonObject> login(@Body JsonObject jsonObject);

    @POST("api/init_db")
    Call<JsonObject> initDB(
            @Header(Constants.SECURITY_KEY) String auth_token,
            @Body JsonObject jsonObject);
    
    @POST("api/inspeksi")
    Call<JsonObject> postInspeksi(
            @Header(Constants.SECURITY_KEY) String auth_token,
            @Body JsonObject body);

    @PUT("api/inspeksi")
    Call<JsonObject> putInspeksi(
            @Header(Constants.SECURITY_KEY) String auth_token,
            @Body JsonObject body);

    @GET("api/inspeksi/{unit}/{last_date}")
    Call<List<Inspeksi>> getInspeksi(
            @Header(Constants.SECURITY_KEY) String auth_token,
            @Path("unit") String user_sid,
            @Path("last_date") String lastDate);

    @POST("api/base64_data")
    Call<JsonObject> postBase64Data(
            @Header(Constants.SECURITY_KEY) String auth_token,
            @Body JsonObject body);

    @GET("api/base64_data/{data_sid}")
    Call<Base64Data> getBase64Data(
            @Header(Constants.SECURITY_KEY) String auth_token,
            @Path("data_sid") String dataSid);

    @PUT("api/gangguan")
    Call<JsonObject> putGangguan(
            @Header(Constants.SECURITY_KEY) String auth_token,
            @Body JsonObject body);

    @GET("api/gangguan/{unit}/{last_date}")
    Call<List<Gangguan>> getGangguan(
            @Header(Constants.SECURITY_KEY) String auth_token,
            @Path("unit") String user_sid,
            @Path("last_date") String lastDate);

    @GET("api/checkdistance/{lat}/{lon}/{jtemuan}")
    Call<JsonObject> checkDistance(
            @Header(Constants.SECURITY_KEY) String auth_token,
            @Path("lat") String lat,
            @Path("lon") String lon,
            @Path("jtemuan") String jtemuan);
}
