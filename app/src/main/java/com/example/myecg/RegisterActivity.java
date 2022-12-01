package com.example.myecg;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText et_id,et_pw,et_age,et_name;
    private Button bt_register,bt_man,bt_woman;
    String g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        bt_register = findViewById(R.id.bt_register);
        bt_man = findViewById(R.id.bt_man);
        bt_woman = findViewById(R.id.bt_woman);

        bt_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g = "MALE";
            }
        });

        bt_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g = "FEMALE";
            }
        });



        //회원가입 버튼 클릭시 수행
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Edit text에 현재 입력되어있는 값을 가져옴
                String id = et_id.getText().toString();
                String password = et_pw.getText().toString();
                String  name = et_id.getText().toString();
                int age = Integer.parseInt(et_age.getText().toString());
                String gender = g;




                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            boolean success = jsonObject.getBoolean("success");

                            if(success){
                                Toast.makeText(getApplicationContext(),"회원가입 성공",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent( RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                            } else{
                                Toast.makeText(getApplicationContext(),"회원가입 실패!",Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                //서버로 Volley 이용하여 요청
                RegisterRequest registerRequest = new RegisterRequest(id,password,name,age,gender,responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);

            }
        });

    }
}