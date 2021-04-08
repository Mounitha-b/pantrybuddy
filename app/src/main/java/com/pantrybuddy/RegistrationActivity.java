package com.pantrybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private EditText eRegUName;
    private EditText eRegPassword;
    private Button eNext;

    public  Credentials cred;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        eRegUName=findViewById(R.id.etRegUName);
        eRegPassword=findViewById(R.id.etRegPwd);
        eNext=findViewById(R.id.btnNext1);
        cred=new Credentials();
        sharedPreferences=getApplicationContext().getSharedPreferences("CredentialsDB",MODE_PRIVATE);
        sharedPrefEditor=sharedPreferences.edit();

        if(sharedPreferences!=null){

            Map<String,?> preferencesMap= sharedPreferences.getAll();

            if(preferencesMap.size() !=0) {
                cred.loadCredentials(preferencesMap);
            }
        }

        eNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String regUserName=eRegUName.getText().toString();
                String regPassword=eRegPassword.getText().toString();

                if(validate(regUserName,regPassword)){
                    if(cred.checkUserName(regUserName)){
                        Toast.makeText(RegistrationActivity.this, "User Name already exists. Please try another User name", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        cred.addCredentials(regUserName,regPassword);


                        /* Storing in shared Pref */
                        sharedPrefEditor.putString(regUserName,regPassword);

                        sharedPrefEditor.putString("LastSavedUserName", regUserName);
                        sharedPrefEditor.putString("LastSavedPassword", regPassword);


                        /* Commits the changes and adds to the file */
                        sharedPrefEditor.apply();

                        //TODO: Change to redirect to food prefernces after sprint 1
                        startActivity(new Intent(RegistrationActivity.this,CongratulationsActivity.class));


                        Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }


    private boolean validate(String uname,String pwd){
        if (uname.isEmpty() || pwd.length()<5){
            Toast.makeText(RegistrationActivity.this, "Please enter all the details. Password should be greater than 5 chars.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}