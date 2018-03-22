package com.example.jayantis.lab3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    EditText emailText;
    EditText passwordText;
    TextView loginMessage;

    // Added for Facebook Authentication
    LoginButton loginButton;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    String fbUsername = "";

    // FireBase Auth
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing SDK first before going to any activity
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.loginusername);
        passwordText = findViewById(R.id.loginpassword);
        loginMessage = findViewById(R.id.loginmessage);

        // Facebook Initialization
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        // Checking if User Already Logged in...
        Profile profile = Profile.getCurrentProfile();
        if(profile != null) {
            // If Profile already exists, Go to HomeActivity Directly....

            // Redirecting to Home Activity
            Intent homeActivityRedirect = new Intent(LoginActivity.this,HomeActivity.class);
            // Passing Username to Homeactivity
            homeActivityRedirect.putExtra("emailId",profile.getName());
            startActivity(homeActivityRedirect);
        }

        loginWithFacebook();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    // Logging in with Facebook
    private void loginWithFacebook(){
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getFbUserName(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                        // App code
                        loginMessage.setText("Facebook Login Cancelled, Try again");
                        loginMessage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        loginMessage.setText("Error occured while signing in : "+exception.getMessage());
                        loginMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    // Getting FB user details from GraphRequest
    private void getFbUserName(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                        Log.v("Main", response.toString());
                        if(jsonObject != null){
                            try {
                                fbUsername = jsonObject.getString("name");
                                // Redirecting to Home Activity
                                Intent homeActivityRedirect = new Intent(LoginActivity.this,HomeActivity.class);
                                // Passing Username to Homeactivity
                                homeActivityRedirect.putExtra("emailId",fbUsername);
                                startActivity(homeActivityRedirect);

                            }catch (Exception e){
                                // App code
                                loginMessage.setText("Error occured while signing in and getting profile name : "+e.getMessage());
                                loginMessage.setVisibility(View.VISIBLE);
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }


    // Facebook Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // On Login Click
    public void login(View view){
        if(!(isFieldNotNull(emailText) && isFieldNotNull(passwordText))){
            // Throwing Error if any of the field is empty
            loginMessage.setText("Email Address or Password fields cannot be Empty !!");
            loginMessage.setVisibility(View.VISIBLE);
        }else{
            progressDialog.setMessage("Signing in...");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.hide();
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                Intent homeActivityRedirect = new Intent(LoginActivity.this, HomeActivity.class);
                                // Adding Email Id to Intent to use that in HomeActivity as a Greeting...
                                homeActivityRedirect.putExtra("emailId",emailText.getText().toString());
                                startActivity(homeActivityRedirect);
                            }else{
                                Toast.makeText(LoginActivity.this,"Login Unsuccessful",Toast.LENGTH_SHORT).show();
                                loginMessage.setText("Invalid Login,Please Register if you haven't registered yet !!");
                                loginMessage.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    // Common Method to check for NULL
    private boolean isFieldNotNull(EditText fieldValue){
        if (fieldValue != null && StringUtils.isNotBlank(fieldValue.getText().toString())){
            return true;
        }
        return false;
    }

    // On Click of Register
    public void register(View view) {
        Intent registerActivityRedirect = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(registerActivityRedirect);
    }
}

