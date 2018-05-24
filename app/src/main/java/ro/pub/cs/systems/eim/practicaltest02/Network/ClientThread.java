package ro.pub.cs.systems.eim.practicaltest02.Network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.General.Constants;
import ro.pub.cs.systems.eim.practicaltest02.General.Utilities;

public class ClientThread extends Thread {

    String clientAddress;
    int clientPort;
    String city;
    String informationType;
    TextView weatherTextView;
    Socket socket = null;

    public ClientThread(String clientAddress, int clientPort, String city, String informationType, TextView weatherTextView) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.city = city;
        this.informationType = informationType;
        this.weatherTextView = weatherTextView;
    }

    public void run() {
        try {
            socket = new Socket(clientAddress, clientPort);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(city);
            printWriter.flush();
            printWriter.println(informationType);
            printWriter.flush();
            String weatherInformation;
            while ((weatherInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeateherInformation = weatherInformation;
                weatherTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherTextView.setText(finalizedWeateherInformation);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
