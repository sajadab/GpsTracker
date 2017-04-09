package com.test.gpstracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rey.material.widget.Button;

import model.RegisterRequestBody;
import model.RegisterResponse;
import restOptions.ServiceHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import utils.GeneralHelper;

/**
 * Created by SAJJAD on 4/6/2017.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText first_name;
    private EditText last_name;
    private EditText confirm;
    private Button register;

    private ProgressDialog progressDialog;
    private ServiceHelper serviceHelper=new ServiceHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        GeneralHelper.setStatusBarColor(this,R.color.status_color);
        initialParams();
        itemClickListener();
    }

    private void itemClickListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_ValidInput()) {
                    progressDialog.show();
                    RegisterRequestBody registerRequestBody=new RegisterRequestBody();
                    registerRequestBody.setUsername(username.getText().toString());
                    registerRequestBody.setPassword(password.getText().toString());
                    registerRequestBody.setFirst_name(first_name.getText().toString());
                    registerRequestBody.setLast_name(last_name.getText().toString());
                    serviceHelper.setRegister(registerRequestBody, new Callback<RegisterResponse>() {
                        @Override
                        public void success(RegisterResponse registerResponse, Response response) {
                            if (registerResponse.success) {
                                SharedPreferences.Editor editor = getSharedPreferences(LoginActivity.LOGIN_STATUS, 0).edit();
                                editor.putBoolean(LoginActivity.LOGIN_KEY, true);
                                editor.apply();
                                Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                progressDialog.dismiss();
                                finish();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }
            }
        });
    }

    private boolean is_ValidInput() {
        if (!username.getText().toString().equals("") && !first_name.getText().toString().equals("") &&
                !last_name.getText().toString().equals("") && !password.getText().toString().equals("") &&
                !confirm.getText().toString().equals("")) {
            if (password.getText().toString().equals(confirm.getText().toString())) {
                return true;
            } else {
                Toast.makeText(RegisterActivity.this,"Confirm password is fault",Toast.LENGTH_LONG).show();
                confirm.setText("");
                return false;
            }
        } else {
            Toast.makeText(RegisterActivity.this,"Please complete input fields.",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void initialParams() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);
        }
        username= (EditText) findViewById(R.id.username);
        first_name= (EditText) findViewById(R.id.first_name);
        last_name= (EditText) findViewById(R.id.last_name);
        password= (EditText) findViewById(R.id.password);
        confirm= (EditText) findViewById(R.id.confirm);
        register= (Button) findViewById(R.id.registerBtn);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        serviceHelper.createAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progressDialog.dismiss();
    }
}
