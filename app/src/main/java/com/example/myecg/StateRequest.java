package com.example.myecg;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StateRequest extends StringRequest {

    // 서버 url 설정(php연동)
    final static String URL = "http://kimbum123.dothome.co.kr/Log.php";
    private Map<String, String> map;

    public StateRequest(String id, int ecg_mean, String state, String date, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("id", id);
        map.put("ecg_mean",ecg_mean+"");
        map.put("state", state);
        map.put("date", date);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}