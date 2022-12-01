package com.example.myecg;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText login_id,login_pw;
    private Button login_login,login_register;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_id = findViewById(R.id.login_id);
        login_pw = findViewById(R.id.login_pw);
        login_login = findViewById(R.id.login_login);
        login_register = findViewById(R.id.login_register);



        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = login_id.getText().toString();
                String password = login_pw.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if(success){
                                String id = jsonObject.getString("id");
                                String password = jsonObject.getString("password");
                                Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent( LoginActivity.this,MainActivity.class);
                                intent.putExtra("id",id);
                                startActivity(intent);

                                preferences = getSharedPreferences("id", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("id",id);
                                editor.apply();

                            } else{
                                Toast.makeText(getApplicationContext(),"로그인 실패!",Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(id,password,responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });



    }
}