package edu.tarleton.mike168m.lidarmapping;

import android.app.Activity;
import android.util.Log;
import java.io.InputStream;
import java.io.IOException;
import android.widget.Toast;
import android.content.Context;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import static android.content.ContentValues.TAG;

public class BTConnectionThread extends Thread {
    private Context ctx;
    private InputStream istream;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private Activity activity;

    public BTConnectionThread(Activity activity, BluetoothDevice device) {
        this.device = device;
        this.ctx =  activity.getApplicationContext();
        this.activity = activity;
        try {
            socket = this.device.createRfcommSocketToServiceRecord(MainActivity.APP_UUID);
            istream = socket.getInputStream();
        }
        catch(IOException ex){
            Log.e(TAG, "Sockets listening() method failed", ex);
        }
    }

    @Override
    public void run() {
        try {
            socket.connect();
            // we are connected so start receiving data from LIDAR device
            if (socket.isConnected()) {
                try {
                    new BTCommunicationThread(socket,activity).start();
                } catch(IOException ex){
                    Toast.makeText(ctx,"Can start communication",Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ctx,"Not connected!",Toast.LENGTH_SHORT).show();
            }
        }
        catch(IOException ex) {
            try {socket.close();}
            catch(IOException ed){ed.printStackTrace();}
            ex.printStackTrace();
        }
    }

    public void cancel() {

    }
}
