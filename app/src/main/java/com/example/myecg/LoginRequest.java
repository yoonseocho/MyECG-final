package com.example.myecg;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    // 서버 url 설정(php연동)
    final static String URL = "http://kimbum123.dothome.co.kr/Login.php";
    private Map<String, String> map;

    public LoginRequest(String id, String password, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("id", id);
        map.put("password", password);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}