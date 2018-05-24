package ro.pub.cs.systems.eim.practicaltest02.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.General.Constants;
import ro.pub.cs.systems.eim.practicaltest02.Network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.Network.ServerThread;
import ro.pub.cs.systems.eim.practicaltest02.R;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    // Server widgets
    private EditText serverPortEditText = null;
    private Button serverStartButton = null;

    // Client widgets
    private EditText addressClient = null;
    private EditText portClient = null;
    private EditText cityClient = null;
    private Button getButton = null;
    private Spinner spinner = null;
    private TextView weatherForecastTextView = null;

    private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = addressClient.getText().toString();
            String clientPort = portClient.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String city = cityClient.getText().toString();
            String informationType = spinner.getSelectedItem().toString();
            if (city == null || city.isEmpty()
                    || informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            weatherForecastTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), city, informationType, weatherForecastTextView
            );
            clientThread.start();
        }

    }

    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText) findViewById(R.id.server_port_edit_text);
        serverStartButton = (Button) findViewById(R.id.connect_button);

        addressClient = (EditText) findViewById(R.id.client_address_edit_text);
        portClient = (EditText) findViewById(R.id.client_port_edit_text);
        cityClient = (EditText) findViewById(R.id.city_edit_text);
        spinner = (Spinner) findViewById(R.id.information_type_spinner);
        weatherForecastTextView = (TextView) findViewById(R.id.weather_forecast_text_view);
        getButton = (Button) findViewById(R.id.get_weather_forecast_button);

        serverStartButton.setOnClickListener(new ConnectButtonClickListener());
        getButton.setOnClickListener(new GetWeatherForecastButtonClickListener());
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
