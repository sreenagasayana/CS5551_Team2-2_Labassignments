package com.example.jayantis.lab3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Created by Kranthi on 2/25/2018.
 */

public class RegistrationActivity extends AppCompatActivity {

    EditText regUserName;
    EditText regEmailAddress;
    EditText regPassword;
    EditText regConfirmPassword;

    TextView regMessage;
    // Email Address RegExpression
    private static final String EMAIL_PATTERN = "^(.+)@(.+)$";

    // Firebase Database reference
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        regUserName = findViewById(R.id.regusername);
        regEmailAddress = findViewById(R.id.regemailaddress);
        regPassword = findViewById(R.id.regpassword);
        regConfirmPassword = findViewById(R.id.regconfirmpassword);
        regMessage = findViewById(R.id.regmessage);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    // On Login Button Click
    public void loginredirect(View view) {
        Intent loginRedirect = new Intent(RegistrationActivity.this,LoginActivity.class);
        startActivity(loginRedirect);
    }

    // On Reset Button Click - Reset all Fields
    public void resetfields(View view) {
        regUserName.setText("");
        regEmailAddress.setText("");
        regPassword.setText("");
        regConfirmPassword.setText("");
        regMessage.setText("");
        regMessage.setVisibility(View.INVISIBLE);
    }

    // On Register Button Click
    public void register(View view) {
        // Doing Basic Validations.
        if (!(isFieldNotNull(regUserName)
                && isFieldNotNull(regEmailAddress)
                && isFieldNotNull(regPassword)
                && isFieldNotNull(regConfirmPassword))) {
            regMessage.setText("Please fill all the fields,Fields cannot be empty");
            regMessage.setVisibility(View.VISIBLE);
        }else if (!Pattern.matches(EMAIL_PATTERN, regEmailAddress.getText().toString())) {
            regMessage.setText("Please enter Valid Email Address,Ex: Kranthi@gmail.com");
            regMessage.setVisibility(View.VISIBLE);
        }else if (!regPassword.getText().toString().equals(regConfirmPassword.getText().toString())) {
            regMessage.setText("Password & Confirm passwords should match");
            regMessage.setVisibility(View.VISIBLE);
        }else if(regPassword.getText().toString().length() < 6){
            regMessage.setText("Password Length should be more than 5");
            regMessage.setVisibility(View.VISIBLE);
        } else {
            progressDialog.setMessage("Registering User");
            progressDialog.show();
            // Inserting into Firebase Database
            firebaseAuth.createUserWithEmailAndPassword(regEmailAddress.getText().toString(),regPassword.getText()
                    .toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.hide();
                    if(task.isSuccessful()){
                        Toast.makeText(RegistrationActivity.this,"Registered Successfully",Toast.LENGTH_SHORT).show();

                        // Setting Successful Message
                        regMessage.setText("You '" + regEmailAddress.getText().toString() + "' have successfully Registered, LOGIN to continue");
                        regMessage.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(RegistrationActivity.this,"User already exists",Toast.LENGTH_SHORT).show();

                        // Setting UnSuccessful Message
                        regMessage.setText("User already exists, Please Login to continue");
                        regMessage.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    // Common Method for Checking Not Null, StringUtils Empty
    private boolean isFieldNotNull(EditText fieldValue) {
        if (fieldValue != null && StringUtils.isNotBlank(fieldValue.getText().toString())) {
            return true;
        }
        return false;
    }
}
