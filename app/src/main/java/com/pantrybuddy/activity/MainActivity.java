package com.pantrybuddy.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pantrybuddy.R;
import com.pantrybuddy.server.Server;
import com.pantrybuddy.stubs.GlobalClass;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements IWebService {

    private EditText eEmail;
    private EditText ePassword;
    private TextView eattmptsrem;
    private Button eloginBut;
    private Button eSignUp;
    private CheckBox eRemMe;
    private  TextView eForgotPwd;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;

    private boolean isValid = false;
    private int counter = 5;
    private String savedEmail;
    private String savedPwd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eEmail = findViewById(R.id.etEmail);
        ePassword = findViewById(R.id.etPassword);
        eattmptsrem = findViewById(R.id.tvattmptsrem);
        eloginBut = findViewById(R.id.elogin);
        eSignUp = findViewById(R.id.btnSignUp);
        eRemMe = findViewById(R.id.cbRememberMe);
        eForgotPwd=findViewById(R.id.tvForgotPwd);

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();
        if (sharedPreferences != null) {

            savedEmail = sharedPreferences.getString("LastSavedEmail", "");
            savedPwd = sharedPreferences.getString("LastSavedPassword", "");

            if (sharedPreferences.getBoolean("RememberMeCheckBox", false)) {
                eEmail.setText(savedEmail);
                ePassword.setText(savedPwd);
                eRemMe.setChecked(true);
            }
        }

        eloginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputEmail = eEmail.getText().toString();
                String inputPassword = ePassword.getText().toString();
                savedEmail =inputEmail;
                savedPwd =inputPassword;
                if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(getBaseContext(), getString(R.string.msg_details_missing), Toast.LENGTH_SHORT).show();
                } else {
                    validate(inputEmail, inputPassword);
                }
            }
        });

        eSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });

        eRemMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefEditor.putBoolean("RememberMeCheckBox", eRemMe.isChecked());
                sharedPrefEditor.apply();
            }
        });

        eForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void validate(String ename, String pword) {
        Server server = new Server(this);
        server.loginEmailPwd(ename, pword);
    }

    @Override
    public void processResponse(JSONObject responseObj) throws JSONException {
            if (responseObj != null) {
                String code = responseObj.get("Code").toString();
                String message = responseObj.get("Message").toString();
                if (code != null && message != null) {
                    if (code.equalsIgnoreCase("200")) {
                        sharedPrefEditor.putString("LastSavedUserName", savedEmail);
                        sharedPrefEditor.putString("LastSavedPassword", savedPwd);
                        sharedPrefEditor.apply();

                        GlobalClass globalClass=(GlobalClass)getApplicationContext();
                        globalClass.setEmail(eEmail.getText().toString());
                        //TODO: Set the first name and last name once logged in by accessing API
//                        globalClass.setFirstName(regFirstName);
//                        globalClass.setLastName(regLastName);
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    } else if(code.equalsIgnoreCase("401") || code.equalsIgnoreCase("116")) {
                        counter--;
                        eattmptsrem.setText(getString(R.string.attmps_rem_1) + counter);
                        if (counter == 0) {
                            eloginBut.setEnabled(false);
                        }
                    }
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
    }
}