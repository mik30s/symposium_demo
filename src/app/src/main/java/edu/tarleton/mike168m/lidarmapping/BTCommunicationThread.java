package edu.tarleton.mike168m.lidarmapping;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        byte[] buffer = new byte[1024];
        int bytes;

        while(true) {
            try{
                bytes = istream.read(buffer);
                final String strReceived = new String(buffer, 0, bytes);
                final String strByteCnt = String.valueOf(bytes) + " bytes received.\n";

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)mainActivity.findViewById(R.id.rraw_data_textview)).setText(strReceived);
                    }
                });
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
