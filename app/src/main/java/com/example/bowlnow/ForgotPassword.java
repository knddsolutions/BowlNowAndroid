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

public class ForgotPassword extends AppCompatActivity {

    AlertDialog.Builder builder;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button RecoverButton = findViewById(R.id.Login);
        final EditText EmailField = findViewById(R.id.editReset);
        builder = new AlertDialog.Builder(ForgotPassword.this);

        RecoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO MAKE SURE FIELD IS NOT EMPTY
                final String Email = EmailField.getText().toString();
                try {
                    PasswordReset(Email);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void PasswordReset(String Email) throws IOException {

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://openbowlservice.com/api/v1/iam/ForgotPassword";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("Email", Email );
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
                    ForgotPassword.this.runOnUiThread(new Runnable() {
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
                    ForgotPassword.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setTitle("Reset Failed")
                                    .setMessage(message)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    });

                } else {
                    builder.setTitle("Reset Failed")
                            .setMessage("Oh no! Something went wrong on our end. PLease try again!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            }
        });
    }
}
