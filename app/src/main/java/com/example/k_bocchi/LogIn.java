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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.k_bocchi.interfaces.ApiService;
import com.example.k_bocchi.request.LoginRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LogIn extends AppCompatActivity {
    private static final String BASE_URL = "https://kbocchi.onrender.com/";

    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;


    private ApiService apiService;
    EditText editusuario;
    EditText editpass;
    SharedPreferences archivo;
    Button botonlogin;

    SignInButton mSignInButtonGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        editusuario = findViewById(R.id.user);
        editpass = findViewById(R.id.pass);
        botonlogin = findViewById(R.id.login);
        mSignInButtonGoogle = findViewById(R.id.btngoogle);

        archivo = this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        if(archivo.contains("email")){
            Intent inicio = new Intent(this, MainActivity.class);
            startActivity(inicio);
            finish();
        }

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mSignInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            irHome();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LogIn.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser user) {
        user = mAuth.getCurrentUser();
        if (user != null){
            irHome();
        }
    }

    private void irHome() {
        Intent intent = new Intent(LogIn.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}










