package com.example.k_bocchi.interfaces;

import com.example.k_bocchi.request.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("usuarios/fisioterapeutas/login")
    Call<LoginRequest>login_call(@Field("email") String email, @Field("contrasena") String contrasena);

}
