package com.example.myecg;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class State extends AppCompatActivity {

    private ImageButton bt_rest, bt_walk, bt_exercise;
    private TextView beat_tv;
    private Button bt_move;
    private SharedPreferences preferences;


    String state;
    int ecg_mean = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        bt_rest = findViewById(R.id.bt_rest);
        bt_walk = findViewById(R.id.bt_walk);
        bt_exercise = findViewById(R.id.bt_exercise);
        beat_tv = findViewById(R.id.beat_tv);
        bt_move = findViewById(R.id.moveButton);



        bt_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = "rest";
            }
        });

        bt_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = "walk";
            }
        });

        bt_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = "exercise";
            }
        });



        Intent intent = getIntent();
        int value;
        if(ecg_mean != 0){
            value = intent.getIntExtra("ecg_mean",ecg_mean);
        }
        else{
            value = intent.getIntExtra("ecg_mean",60);
        }
        beat_tv.setText(""+value);




        Intent intent2 = new Intent(State.this, Analysis.class);

        bt_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_move.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                startActivity(intent2);


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            boolean success = jsonObject.getBoolean("success");

                            if(success){
                                Toast.makeText(getApplicationContext(),"데이터 저장 성공",Toast.LENGTH_SHORT).show();

                            } else{
                                Toast.makeText(getApplicationContext(),"데이터 저장 실패!",Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

//                Intent intent_id = getIntent();
//                String id = intent_id.getStringExtra("id");

                SharedPreferences sharedPreferences= getSharedPreferences("id", MODE_PRIVATE);
                String id = sharedPreferences.getString("id","");



//                tv_result.setText("USERID = " + preferences.getString("userid","")
//                        + "\n USERPWD = " + preferences.getString("userpwd",""));


                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date123 = new Date();
                String date = dateFormat.format(date123);

                Toast.makeText(getApplicationContext(), "id: "+id, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "state: "+state, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "ecg_mean: "+ecg_mean, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "date: "+date, Toast.LENGTH_SHORT).show();

                //서버로 Volley 이용하여 요청
                StateRequest stateRequest = new StateRequest(id,ecg_mean,state,date,responseListener);
                RequestQueue queue = Volley.newRequestQueue(State.this);
                queue.add(stateRequest);

            }


        });


    }
}