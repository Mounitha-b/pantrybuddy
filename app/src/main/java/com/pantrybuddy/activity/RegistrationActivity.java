package com.pantrybuddy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pantrybuddy.R;
import com.pantrybuddy.server.Server;
import com.pantrybuddy.stubs.GlobalClass;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.stream.Stream;

public class RegistrationActivity extends AppCompatActivity implements IWebService {
    private EditText eLastName;
    private EditText eFirstName;
    private EditText eEmail;
    private EditText eMobile;
    private EditText eRegPassword;
    private Button eNext;
    private TextView ePwdMsg;

    public Credentials cred;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        eLastName = findViewById(R.id.etLastName);
        eRegPassword = findViewById(R.id.etRegPwd);
        eNext = findViewById(R.id.btnNext1);
        eFirstName = findViewById(R.id.etFirstName);
        eMobile = findViewById(R.id.etPhone);
        eEmail = findViewById(R.id.etMail);
        ePwdMsg = findViewById(R.id.etPwdMsg);


        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();


        eRegPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ePwdMsg.setVisibility(View.VISIBLE);
            }
        });

        eNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regLastName = eLastName.getText().toString();
                String regPassword = eRegPassword.getText().toString();
                String regFirstName = eFirstName.getText().toString();
                String regMobile = eMobile.getText().toString();
                String regEmail = eEmail.getText().toString();

                boolean valid =  Stream.of(regLastName, regPassword, regFirstName, regMobile, regEmail)
                        .allMatch(StringUtils::isNotBlank);
                if(!valid){
                    Toast.makeText(RegistrationActivity.this, getString(R.string.msg_details_missing), Toast.LENGTH_SHORT).show();
                }else {
                    /* Storing in shared Pref */
                    sharedPrefEditor.putString(regLastName, regPassword);

                    sharedPrefEditor.putString("LastSavedEmail", regEmail);
                    sharedPrefEditor.putString("LastSavedPassword", regPassword);
                    /* Commits the changes and adds to the file */
                    sharedPrefEditor.apply();
                    GlobalClass globalClass=(GlobalClass)getApplicationContext();
                    globalClass.setEmail(regEmail);
                    globalClass.setFirstName(regFirstName);
                    globalClass.setLastName(regLastName);

                    //TODO: change this uncomment after testing
                    callSignUpApi(regEmail, regMobile, regFirstName, regLastName, regPassword);
                    startActivity(new Intent(RegistrationActivity.this, FoodPreferencesActivity.class));


                }



        }
    });
}

    private void callSignUpApi(String regEmail, String regMobile, String regFirstName, String regLastName, String regPassword) {
        Server server = new Server(this);
        server.signUp(regEmail, regMobile, regFirstName, regLastName, regPassword);
    }


    @Override
    public void processResponse(JSONObject responseObj) throws JSONException {
        if (responseObj != null) {
            String code = responseObj.get("Code").toString();
            String message = responseObj.get("Message").toString();
            if (code != null && message != null) {
                if (code.equalsIgnoreCase("201")) {
                    //TODO: Change to redirect to food preferences after sprint 1

                    //startActivity(new Intent(RegistrationActivity.this, CongratulationsActivity.class));
                    startActivity(new Intent(RegistrationActivity.this, FoodPreferencesActivity.class));

                    Toast.makeText(RegistrationActivity.this, getString(R.string.msg_registration_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}