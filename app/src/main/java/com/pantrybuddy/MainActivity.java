package com.pantrybuddy;

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

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText eName;
    private EditText ePassword;
    private TextView eattmptsrem;
    private Button eloginBut;
    private Button eSignUp;
    private CheckBox eRemMe;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;

    private boolean isValid = false;
    private int counter = 5;

    public Credentials credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eName = findViewById(R.id.etName);
        ePassword = findViewById(R.id.etPassword);
        eattmptsrem = findViewById(R.id.tvattmptsrem);
        eloginBut = findViewById(R.id.elogin);
        eSignUp = findViewById(R.id.btnSignUp);
        eRemMe = findViewById(R.id.cbRememberMe);
        credentials = new Credentials();

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();
        if (sharedPreferences != null) {

            Map<String, ?> preferencesMap = sharedPreferences.getAll();

            if (preferencesMap.size() != 0) {
                credentials.loadCredentials(preferencesMap);
            }


            String savedUname = sharedPreferences.getString("LastSavedUserName", "");
            String savedPwd = sharedPreferences.getString("LastSavedPassword", "");



            if (sharedPreferences.getBoolean("RememberMeCheckBox", false)) {
                eName.setText(savedUname);
                ePassword.setText(savedPwd);
                eRemMe.setChecked(true);
            }
        }
        eloginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputName = eName.getText().toString();
                String inputPassword = ePassword.getText().toString();
                if (inputName.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please enter all the details!", Toast.LENGTH_SHORT).show();
                } else {
                    isValid = validate(inputName, inputPassword);
                    if (!isValid) {
                        counter--;
                        Toast.makeText(MainActivity.this, "Incorrect Credentials entered!", Toast.LENGTH_SHORT).show();
                        eattmptsrem.setText("No. of attempts remaining : " + counter);
                        if (counter == 0) {
                            eloginBut.setEnabled(false);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        sharedPrefEditor.putString("LastSavedUserName", inputName);
                        sharedPrefEditor.putString("LastSavedPassword", inputPassword);
                        sharedPrefEditor.apply();
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
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
    }

    private boolean validate(String ename, String pword) {
        return credentials.verifyCredentials(ename,pword);
    }
}