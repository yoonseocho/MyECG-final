package com.example.myecg;

import static android.text.TextUtils.substring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Iterator;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import java.text.SimpleDateFormat;

import javax.sql.DataSource;

public class ECG extends AppCompatActivity {
    //전역변수
    int ecg_mean = 0;
    private Map<String, String> map;

    String[] array = {"0"};
    public LineChart chart;

    String globalData;

    private final String CHANNEL_ID = "bambam";
    private final int NOTIFICATION_ID = 001;

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    BluetoothDevice remoteDevice;
    BluetoothServerSocket mService;
    Button getdataButton, finishButton;
    TextView txtV;
    TextView txtV2;
    TextView txtV3;
    InputStream mmInputStream;
    Thread workerThread;


    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;


    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    TextView dateTextview;

    Queue<Float> queue = new LinkedList<>();
    Queue<Float> queue2 = new LinkedList<>();
    double ecg;

    Context context = this;
    private Toolbar toolbar;

    static final int[] receivedDateArray = new int[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        Intent newintent = getIntent();
        address = newintent.getStringExtra(MainActivity.Extra_ADRESS);

        getdataButton = findViewById(R.id.getDataButtonID);
        txtV = findViewById(R.id.String_Data_text);
        txtV2 = findViewById(R.id.String_Data_text2);
        txtV3 = findViewById(R.id.heart_beat);
        finishButton = findViewById(R.id.finish);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dateTextview = findViewById(R.id.dateTextID);




        getdataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getdataButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                if (btSocket != null) {
                    try {
                        mmInputStream = btSocket.getInputStream();
                        beginListenForData();
                        receiveData();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void receiveData() {
                final Handler handler = new Handler();
                //데이터 수신을 위한 버퍼 생성
                readBufferPosition = 0;
                readBuffer = new byte[1024];

                //데이터 수신을 위한 쓰레드 생성
                workerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                //데이터 수신 확인
                                int byteAvailable = mmInputStream.available();
                                //데이터 수신 된 경우
                                if (byteAvailable > 0) {
                                    //입력 스트림에서 바이트 단위로 읽어옴
                                    byte[] bytes = new byte[byteAvailable];
                                    mmInputStream.read(bytes);
                                    //입력 스트림 바이트를 한 바이트씩 읽어옴
                                    for (int i = 0; i < byteAvailable; i++) {
                                        byte tempByte = bytes[i];
                                        //개행문자를 기준으로 받음 (한줄)
                                        if (tempByte == '\n') {
                                            //readBuffer 배열을 encodeBytes로 복사
                                            byte[] encodedBytes = new byte[readBufferPosition];
                                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                            //인코딩 된 바이트 배열을 문자열로 변환
                                            final String text = new String(encodedBytes, "UTF-8");
                                            readBufferPosition = 0;
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    array = text.split(",", 3);

                                                    txtV.setText("ecg: " + array[0]);
                                                    txtV2.setText("beat: " + array[1]);



                                                    SimpleDateFormat time;
                                                    Date today = new Date();

                                                    time = new SimpleDateFormat("sSSS");

                                                    float ms_time = Float.valueOf(time.format(today));

//                                                    Log.d("DKU", "큐에 추가된 시간: " + ms_time);

                                                    if(Float.valueOf(array[1]) > 1.0){
                                                        queue2.add(ms_time);
                                                    }



                                                    if(queue2.size() >= 2){
                                                        float time1 = queue2.poll();
                                                        float time2 = queue2.poll();
                                                        float diff = Math.abs(time2 - time1);

                                                        Log.d("DKU", "시간차: " + diff +"ms");

                                                        double diff_s = diff * 0.001;
                                                        if(diff_s != 0){
                                                            ecg = 60 / (diff_s);
                                                        }

//                                                        Log.d("DKU", "심박수: " + ecg);
                                                    }

                                                    int heart_beat = (int) ecg;
                                                    txtV3.setText("heart beat: "+heart_beat);

                                                    if(heart_beat>=60 && heart_beat<=150){
                                                        ecg_mean = heart_beat;
                                                    }
                                                    else{
                                                        ecg_mean = 60;
                                                    }




                                                    //평균심박수 구하기
//                                                    if(ecg > ecg_max){ecg_max = ecg;}
//                                                    if(ecg < ecg_min){ecg_min = ecg;}
//                                                    ecg_sum = ecg_sum + ecg;
//                                                    ecg_cnt++;
//                                                    ecg_mean = ecg_sum / ecg_cnt;



                                                    chart = findViewById(R.id.line_chart);
                                                    ArrayList<Entry> values = new ArrayList<>();

                                                    queue.add(Float.valueOf(array[0]));
                                                    int count = 0;
                                                    if(queue.size() > 30){
                                                        queue.remove();
                                                    }
                                                    Iterator iter = queue.iterator();
//                                                    Log.d("DKM","size : "+ queue.size());
                                                    while(iter.hasNext()){
                                                        values.add(new Entry(count, (Float) iter.next()));
                                                        count++;
                                                    }

                                                    LineDataSet set1;
                                                    set1 = new LineDataSet(values, "ECG 즉, 심전도입니다!");

                                                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                                    dataSets.add(set1);
                                                    LineData data = new LineData(dataSets);

                                                    set1.setColor(Color.BLACK);
                                                    set1.setDrawCircles(false);
                                                    set1.setDrawValues(false);


                                                    // set data
                                                    chart.setData(data);
                                                    chart.invalidate();
                                                    chart.setAutoScaleMinMaxEnabled(true);
                                                    chart.getXAxis().setGranularityEnabled(true);
                                                    chart.getAxisLeft().setAxisMaximum(5f);
                                                    chart.getAxisLeft().setAxisMinimum(0f);


                                                }

                                            });

                                        } // 개행문자가 아닐경우
                                        else {
                                            readBuffer[readBufferPosition++] = tempByte;
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        }
                        try {
                            //1초 마다 받아옴
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                workerThread.start();
            }


        });

        new BTbaglan().execute();





        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                //평균 심박수 분석페이지로 넘기기
                Toast.makeText(getApplicationContext(), "DB로 이동중! 평균심박수는? "+ecg_mean, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ECG.this, State.class);
                intent.putExtra("ecg_mean",ecg_mean);
                startActivity(intent);
            }
        });
    }




    void beginListenForData() // Getting data from remote bluetooth device
    {
        final Handler handler = new Handler();
        final byte delimiter = 10;

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            globalData = data;
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();

            } catch (IOException e) {
                //msg("Error");
            }

        }
        finish();
    }

    private class BTbaglan extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ECG.this, "Connecting...", "Please Wait");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice cihaz = myBluetooth.getRemoteDevice(address);
                    btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();

                }

            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {

                Toast.makeText(getApplicationContext(), "Connection Error, Try Again.", Toast.LENGTH_SHORT).show();
                finish();
            } else {

                Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_SHORT).show();

                isBtConnected = true;
            }
            progress.dismiss();
        }

    }
}