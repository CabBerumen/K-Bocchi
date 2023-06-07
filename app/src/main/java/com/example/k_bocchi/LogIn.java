package com.example.k_bocchi;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.k_bocchi.interfaces.ApiService;
import com.example.k_bocchi.request.LoginRequest;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LogIn extends AppCompatActivity {
    private static final String BASE_URL = "https://kbocchi.onrender.com/";
    private ApiService apiService;
    EditText editusuario;
    EditText editpass;
    SharedPreferences archivo;
    Button botonlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        editusuario = findViewById(R.id.user);
        editpass = findViewById(R.id.pass);
        botonlogin = findViewById(R.id.login);
        archivo = this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        if(archivo.contains("email")){
            Intent inicio = new Intent(this, MainActivity.class);
            startActivity(inicio);
            finish();
        }

        botonlogin.setOnClickListener(new View.OnClickListener() {
            HttpLoggingInterceptor loggin = new HttpLoggingInterceptor();

            @Override
            public void onClick(View view) {
                String email = editusuario.getText().toString().trim();
                String contrasena = editpass.getText().toString().trim();

                loggin.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(loggin);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService login = retrofit.create(ApiService.class);
                Call<LoginRequest> call = login.login_call(email, contrasena);
                call.enqueue(new Callback<LoginRequest>() {
                    @Override
                    public void onResponse(Call<LoginRequest> call, Response<LoginRequest> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            editusuario.getText().clear();
                            editpass.getText().clear();
                            String tokenInter = response.body().getToken();

                           Intent inicio = new Intent(LogIn.this, MainActivity.class);
                            inicio.putExtra("token", tokenInter);
                            startActivity(inicio);
                            Toast.makeText(LogIn.this, "Inicio exitoso", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(LogIn.this, "Datos Incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRequest> call, Throwable t) {
                        Toast.makeText(LogIn.this, "Error en la conexión", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }
}







