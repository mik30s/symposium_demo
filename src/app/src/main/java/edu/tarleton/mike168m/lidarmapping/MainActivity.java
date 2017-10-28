package edu.tarleton.mike168m.lidarmapping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bta;
    public static final UUID APP_UUID =
        UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");

    public final int REQUEST_ENABLE_BT = 1;
    public static TextView rawDataView;

    private void initialize() {
        bta = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
        if (pairedDevices.size() > 0) {
            BluetoothDevice device  = (BluetoothDevice)pairedDevices.toArray()[0];
            new BTConnectionThread(this, device).start();
            Toast.makeText(this, device.getName(),Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
                    "No devices to connect to!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rawDataView = findViewById(R.id.rraw_data_textview);
        initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                initialize();
            }else{
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
