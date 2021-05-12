package com.example.bowlnow;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //Create UI objects
    CheckBox RememberMe;
    EditText PasswordField;
    EditText EmailField;
    SharedPreferences MyPreferences;
    private ProgressDialog loginDialogue;
    AlertDialog.Builder builder;

    static final String SHARED_PREF_NAME = "BowlNowData";
    static final String KEY_EMAIL = "Email";
    static final String KEY_PASSWORD = "Password";
    static final String KEY_REMEMBER = "RememberMe";
    static final String KEY_AUTHTOKEN = "AuthToken";
    static final String KEY_ACCESSLEVEL = "AccessLevel";
    public String AuthToken;
    public String AccessLevel;
    public String Platform;
    public String Center;
    public String CenterEmail;
    public String MemberID;
    public String Status;
    public String Timestamp;
    public String Moid;
    public String BannerURL;
    public String Path;
    public String FirstName;
    public String LastName;
    public String CenterMoid;
    public String BirthDate;
    public String Type;
    public String IamUserMoid;
    ArrayList<String> ActiveCenters = new ArrayList<String>();
    ArrayList<String> MyCenters = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reference to widgets
        Button Login = findViewById(R.id.Login);
        Button SignUp = findViewById(R.id.LoginButton1);
        Button ForgotPassword = findViewById(R.id.LoginButton2);
        Button Privacy = findViewById(R.id.LoginButton3);
        RememberMe = findViewById(R.id.checkBox);
        PasswordField = findViewById(R.id.editPassword);
        EmailField = findViewById(R.id.editEmail);
        builder = new AlertDialog.Builder(MainActivity.this);
        MyPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);

        CheckAuthToken();

        CheckSharedPreferences();

        //Set button listeners
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckMyFields();
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToSignUp();
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToForgotPassword();
            }
        });

        Privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO get privacy policy doc
            }
        });

    }

    //Make sure text fields are not empty
    public void CheckMyFields() {
        final String Email = EmailField.getText().toString();
        final String Password = PasswordField.getText().toString();

        if (Email.length() == 0) {
            EmailField.requestFocus();
            EmailField.setError("Email field cannot be empty!");
        }
        else if(Password.length() == 0) {
            PasswordField.requestFocus();
            PasswordField.setError("Password field cannot be empty!");
        }
        else {
            loginDialogue = ProgressDialog.show(MainActivity.this, "Logging in", "Please wait...");
            HandlePreferences(Email, Password);
        }
    }

    //Set shared preferences if remember me is checked
    public void HandlePreferences(String Email, String Password) {
        SharedPreferences.Editor editor = MyPreferences.edit();
        if (RememberMe.isChecked()) {
            editor.putString(KEY_EMAIL, Email);
            editor.putString(KEY_PASSWORD, Password);
            editor.putBoolean(KEY_REMEMBER, RememberMe.isChecked());
            editor.apply();
        }
        try {
            LoginRequest(Email, Password);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Check for stored shared preferences
    public void CheckSharedPreferences () {
        String SavedEmail = MyPreferences.getString(KEY_EMAIL, null);
        String SavedPassword = MyPreferences.getString(KEY_PASSWORD, null);
        boolean SavedCheck = MyPreferences.getBoolean(KEY_REMEMBER, false);

        if (SavedEmail != null) {
            EmailField.setText(SavedEmail);
        }
        if (SavedPassword != null) {
            PasswordField.setText(SavedPassword);
        }
        if (SavedCheck) {
            RememberMe.setChecked(true);
        }
    }

    public void CheckAuthToken() {
        String CheckToken = MyPreferences.getString(KEY_AUTHTOKEN, null);
        if (CheckToken != null) {
            loginDialogue = ProgressDialog.show(MainActivity.this, "Logging in", "Please wait...");
            try {
                VerifyToken(CheckToken);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Start new activity intents
    public void GlobalAdminHome(ArrayList<String> ActiveCenters) {
        Intent intent = new Intent(this, GlobalAdminHome.class);
        intent.putExtra("ActiveCenters", ActiveCenters);
        startActivity(intent);
    }

    public void ToSignUp() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void ToForgotPassword() {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }

    public void ToMyCenters(ArrayList<String> MyCenters) {
        Intent intent = new Intent(this, MyCenters.class);
        intent.putExtra("MyCenters", MyCenters);
        startActivity(intent);
    }

    private void VerifyToken(String CheckToken) throws IOException {

        final String AuthToken = CheckToken;
        String url = "https://openbowlservice.com/api/v1/iam/VerifyAuth";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Auth-Token", AuthToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final SharedPreferences.Editor editor = MyPreferences.edit();
                if (response.code() == 200) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("here", "Got 200");
                            if (MyPreferences.getString(KEY_ACCESSLEVEL, "").equals("User") || MyPreferences.getString(KEY_ACCESSLEVEL, "").equals("Admin")) {
                                try {
                                    GetActiveCenters(AuthToken);
                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                            else {
                                if (loginDialogue != null){
                                    loginDialogue.dismiss();
                                }
                                builder.setTitle("Login Failed")
                                        .setMessage("Unknown access level please try again!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).show();
                            }
                        }
                    });
                } else if (response.code() == 400){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            builder.setTitle("Login Failed")
                                    .setMessage("Invalid username or password")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });

                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            builder.setTitle("Login Failed")
                                    .setMessage("Invalid username or password")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });
                }
            }
        });
    }

    private void LoginRequest(String Email, String Password) throws IOException {

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://openbowlservice.com/api/v1/iam/Login";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("Email", Email );
            postdata.put("Password", Password );
        } catch(JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(postdata.toString(), MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final SharedPreferences.Editor editor = MyPreferences.edit();
                final String mMessage = response.body().string();
                if (response.code() == 200) {
                    try {
                        JSONObject authObj = new JSONObject(mMessage);
                        AuthToken = authObj.getString("AuthToken");
                        AccessLevel = authObj.getString("AccessLevel");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editor.putString(KEY_AUTHTOKEN, AuthToken);
                    editor.putString(KEY_ACCESSLEVEL, AccessLevel);
                    editor.apply();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MyPreferences.getString(KEY_ACCESSLEVEL, "").equals("User") || MyPreferences.getString(KEY_ACCESSLEVEL, "").equals("Admin")) {
                                try {
                                    GetActiveCenters(AuthToken);
                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                            else {
                                if (loginDialogue != null){
                                    loginDialogue.dismiss();
                                }
                                builder.setTitle("Login Failed")
                                        .setMessage("Unknown access level please try again!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        }).show();
                            }
                        }
                    });
                } else if (response.code() == 400){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            builder.setTitle("Login Failed")
                                    .setMessage("Invalid username or password")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });

                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            builder.setTitle("Reset Failed")
                                    .setMessage("Oh no! Something went wrong on our end. Please try again!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });
                }
            }
        });
    }

    public void GetActiveCenters(String authToken) throws IOException {
        final String AuthToken = authToken;
        String url = "https://openbowlservice.com/api/v1/center/registration";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Auth-Token", AuthToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                if (response.code() == 200) {
                    try {
                        JSONObject resObj = new JSONObject(mMessage);
                        JSONArray activeCenters = resObj.getJSONArray("Results");
                        System.out.println(activeCenters);
                        for (int i = 0; i < activeCenters.length(); i++) {
                            JSONObject jsonObject = activeCenters.getJSONObject(i);
                            Center = jsonObject.getString("Center");
                            CenterEmail = jsonObject.getString("Email");
                            Platform = jsonObject.getString("Platform");
                            MemberID = jsonObject.getString("MemberID");
                            Status = jsonObject.getString("Status");
                            BannerURL = jsonObject.getString("BannerURL");
                            Timestamp = jsonObject.getString("Timestamp");
                            Moid = jsonObject.getString("Moid");

                            ActiveCenters.add(BannerURL);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MyPreferences.getString(KEY_ACCESSLEVEL, "").equals("User")) {
                                try {
                                    GetCenterUsers(AuthToken);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (MyPreferences.getString(KEY_ACCESSLEVEL, "").equals("Admin")) {
                                GlobalAdminHome(ActiveCenters);
                            }
                        }
                    });
                }  else if (response.code() == 401){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            showAlert();
                        }
                    });
                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            builder.setTitle("Unexpected Error Occurred")
                                    .setMessage("Failed to load data")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            showAlert();
                                        }
                                    }).show();
                        }
                    });
                }
            }
        });

    }

    public void GetCenterUsers(String AuthToken) throws IOException {
        String url = "https://openbowlservice.com/api/v1/center/users";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Auth-Token", AuthToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                if (response.code() == 200) {
                    try {
                        JSONObject resObj = new JSONObject(mMessage);
                        JSONArray activeCenters = resObj.getJSONArray("Results");
                        for (int i = 0; i < activeCenters.length(); i++) {
                            JSONObject jsonObject = activeCenters.getJSONObject(i);
                            FirstName = jsonObject.getString("Platform");
                            LastName = jsonObject.getString("Email");
                            CenterMoid = jsonObject.getString("MemberID");
                            BirthDate = jsonObject.getString("Status");
                            Type = jsonObject.getString("Center");
                            IamUserMoid = jsonObject.getString("BannerURL");
                            Moid = jsonObject.getString("Timestamp");

                            MyCenters.add(CenterMoid);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            ToMyCenters(MyCenters);
                        }
                    });
                }  else if (response.code() == 401){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            showAlert();
                        }
                    });
                } else {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (loginDialogue != null){
                                loginDialogue.dismiss();
                            }
                            builder.setTitle("Unexpected Error Occurred")
                                    .setMessage("Failed to load data")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            showAlert();
                                        }
                                    }).show();
                        }
                    });
                }
            }
        });

    }

    public void showAlert() {
        builder.setTitle("Logged Out")
                .setMessage("Session has expired")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
}


