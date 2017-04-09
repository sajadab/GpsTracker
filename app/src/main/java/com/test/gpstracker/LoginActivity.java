package com.test.gpstracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Button;

import model.LoginRequestBody;
import model.LoginResponse;
import restOptions.ServiceHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import utils.GeneralHelper;

/**
 * Created by SAJJAD on 4/6/2017.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN_STATUS = "login";
    public static final String LOGIN_KEY = "login_key";
    private Button loginBtn;
    private Button registerBtn;
    private EditText username;
    private EditText password;
    private TextView title;

    private ProgressDialog progressDialog;
    private ServiceHelper serviceHelper = new ServiceHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GeneralHelper.setStatusBarColor(this, R.color.status_color);
        if (getSharedPreferences(LOGIN_STATUS, 0).getBoolean(LOGIN_KEY, false)) {
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            initialParams();
            itemClickListener();
        }
    }

    private void itemClickListener() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                Pair<View, String> pair1 = Pair.create(findViewById(R.id.username), "user");
                Pair<View, String> pair2 = Pair.create(findViewById(R.id.password), "pass");
                Pair<View, String> pair3 = Pair.create(findViewById(R.id.title), "title");
                Pair<View, String> pair4 = Pair.create(findViewById(R.id.login), "btn");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this,
                        pair1, pair2, pair3, pair4
                );
                ActivityCompat.startActivity(LoginActivity.this, intent, options.toBundle());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Transition fade = new Fade();
                    fade.excludeTarget(android.R.id.statusBarBackground, true);
                    fade.excludeTarget(android.R.id.navigationBarBackground, true);
                    getWindow().setExitTransition(fade);
                    getWindow().setEnterTransition(fade);
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_InputValid()) {
                    progressDialog.show();
                    LoginRequestBody loginRequestBody = new LoginRequestBody();
                    loginRequestBody.setUsername(username.getText().toString());
                    loginRequestBody.setPassword(password.getText().toString());
                    serviceHelper.setLogin(loginRequestBody, new Callback<LoginResponse>() {
                        @Override
                        public void success(LoginResponse loginResponse, Response response) {
                            if (loginResponse.getSuccess()) {
                                SharedPreferences.Editor editor = getSharedPreferences(LOGIN_STATUS, 0).edit();
                                editor.putBoolean(LOGIN_KEY, true);
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                progressDialog.dismiss();
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Username or Password is incorrect.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e("retrofit error", error.getMessage());
                        }
                    });

                }
            }
        });
    }

    private boolean is_InputValid() {
        if (!username.getText().toString().equals("") && !password.getText().toString().equals("")) {
            return true;
        } else {
            Toast.makeText(LoginActivity.this, "Please complete input fields.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void initialParams() {
        loginBtn = (Button) findViewById(R.id.login);
        registerBtn = (Button) findViewById(R.id.register);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        title = (TextView) findViewById(R.id.title);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(true);
        serviceHelper.createAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progressDialog.dismiss();
    }
}
