package com.example.myecg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Analysis extends AppCompatActivity {
    TextView txt_weather;
    TextView txt2_weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        Toast.makeText(getApplicationContext(), "심박수 분석페이지 입니다!", Toast.LENGTH_SHORT).show();

        txt_weather = (TextView) findViewById(R.id.txt_weather);
        txt2_weather = (TextView) findViewById(R.id.txt2_weather);

        txt2_weather.setText("서울민국의 온도");
        //https://119.seoul.go.kr/asims/wether/selectWetherList.do 사용

        new WeatherAsyncTask(txt_weather).execute("https://119.seoul.go.kr/asims/wether/selectWetherList.do", "ul[class=w_r]");
        //new WeatherAsyncTask(text1).execute("https://119.seoul.go.kr/asims/wether/selectWetherList.do",)
    }
}

class WeatherAsyncTask extends AsyncTask<String, Void, String> {

    TextView textView;

    public WeatherAsyncTask(TextView textView){
        this.textView = textView;
    }
    @Override
    protected String doInBackground(String... params) {

        String URL = params[0];
        String El = params[1];
        String result = "";

        try{
            Document document = Jsoup.connect(URL).get();
            Elements elements = document.select(El);

            for(Element element : elements){

                result = result+element.text()+"\n";
            }

            return result;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
        Log.d("DKU","받아온 값 : "+s);
        String[] temp = s.split(" ");
        String good = "온도 : "+temp[0]+"\n"+temp[1]+" : "+temp[2]+"\n"+temp[3] + temp[4] + " : "+temp[5]+"\n"+temp[6]+temp[7]+ " : "+temp[8]+"\n"+temp[9]+temp[10];
        Log.d("DKU", "문자열로 바꾼값 : "+good);

        textView.setText(good);
    }
}