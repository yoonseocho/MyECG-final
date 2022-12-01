package com.example.myecg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    Button buttonON, buttonOFF, devicesButton;
    ListView listView;
    BluetoothAdapter myBluetoothAdapter;

    Intent btenablingIntent;
    int requestCodeForeEnable;
    public static String Extra_ADRESS = "device_adress";
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonON = findViewById(R.id.openButtonID);
        buttonOFF = findViewById(R.id.closeButtonID);
        devicesButton = findViewById(R.id.devicesButtonID);

        listView = findViewById(R.id.devicesListviewID);

        // Get the default adapter
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }

        btenablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForeEnable = 1;

        bluetoothONMethod();
        bluetoothOFMethod();
        exeButton();
    }

    private void exeButton() {
        devicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Set<BluetoothDevice> bt = myBluetoothAdapter.getBondedDevices();
                ArrayList list = new ArrayList();

                if (bt.size() > 0) {
                    Toast.makeText(getApplicationContext(), "Devices Detected", Toast.LENGTH_SHORT).show();
                    for (BluetoothDevice device : bt) {
                        list.add(device.getName() + "\n" + device.getAddress());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(selectDevice);
                }
            }
        });
    }

    private void bluetoothOFMethod() {
        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    myBluetoothAdapter.disable();
                    Toast.makeText(getApplicationContext(), "Bluetooth is Closed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeForeEnable) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth is Enable", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth Enabling is Cancelled", Toast.LENGTH_LONG).show();
            }

        }

    }

    private void bluetoothONMethod() {
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth does not support on this Device", Toast.LENGTH_LONG).show();
                } else {
                    if (!myBluetoothAdapter.isEnabled()) {
                        startActivityForResult(btenablingIntent, requestCodeForeEnable);
                    }
                }
            }
        });

    }

    public AdapterView.OnItemClickListener selectDevice = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            //((TextView) view).setTextColor(Color.WHITE); //


            Intent comIntent = new Intent(MainActivity.this, ECG.class);
            comIntent.putExtra(Extra_ADRESS, address);
            startActivity(comIntent);
            //Toast.makeText(getApplicationContext(), "페어링 성공해다악!!!!!!!!!", Toast.LENGTH_LONG).show();
        }
    };

}