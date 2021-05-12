package com.example.bowlnow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CenterRegister extends AppCompatActivity {

    private String message;
    private String CenterName;
    private String CenterEmail;
    private String MemberID;
    private String Platform = "BowlingCenter";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_register);

        Button CreateCenter = findViewById(R.id.CreateCenter);
        builder = new AlertDialog.Builder(CenterRegister.this);
        final EditText CenterNameField = findViewById(R.id.editCenterName);
        final EditText CenterEmailField = findViewById(R.id.editCenterEmail);
        final EditText CenterMemberID = findViewById(R.id.editBpaa);

        CreateCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO MAKE SURE FIELDS ARE NOT EMPTY
                CenterEmail = CenterEmailField.getText().toString();
                CenterName = CenterNameField.getText().toString();
                MemberID = CenterMemberID.getText().toString();

                try {
                    CenterRegistration(CenterName, CenterEmail, MemberID, Platform);
                }catch(IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void CenterRegistration(String Email, String Bpaa, String Center, String Platform) throws IOException {

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://openbowlservice.com/api/v1/center/registration";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("Email", Email );
            postdata.put("Center", Center);
            postdata.put("MemberID", Bpaa);
            postdata.put("Platform", Platform);
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
                final String mMessage = response.body().string();
                if (response.code() == 200) {
                    try {
                        JSONObject resObj = new JSONObject(mMessage);
                        message = resObj.getString("Results");
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                    CenterRegister.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Success!")
                                    .setMessage(message)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {

                                        }
                                    }).show();
                        }
                    });
                } else if (response.code() == 400){
                    try {
                        JSONObject resObj = new JSONObject(mMessage);
                        message = resObj.getString("Results");
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                    CenterRegister.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Registration Failed!")
                                    .setMessage(message)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });

                } else {
                    System.out.println(response.code());
                    CenterRegister.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Registration Failed!")
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
}
