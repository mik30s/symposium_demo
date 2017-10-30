package edu.tarleton.mike168m.lidarmapping;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class BTCommunicationThread extends Thread {
    private BluetoothSocket socket;
    private InputStream istream;
    private OutputStream ostream;
    private Activity mainActivity;

    public BTCommunicationThread(BluetoothSocket socket, Activity activity) throws IOException {
        this.socket = socket;
        this.istream = socket.getInputStream();
        this.ostream = socket.getOutputStream();
        this.mainActivity = activity;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[256];
        int bytes;
        try {
            while(true) {
                DataInputStream distream = new DataInputStream(istream);
                distream.readFully(buffer, 0, buffer.length);
                final String readings = new String(buffer, 0, buffer.length);
                //Log.i(TAG, "Read "+bytes+" bytes.");
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)mainActivity.findViewById(R.id.rraw_data_textview)).setText(readings);
                    }
                });
            }
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
