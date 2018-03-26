package devimap.com.miguiaprevia;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "Debug";
    Button btnPaired;
    ListView devicelist;
    MiGuiaPreviaApp app;
    private TextToSpeech myTTS;
    private TextView instructionsTV;
    private int MY_DATA_CHECK_CODE = 0;

    //bluetooth
    private BluetoothAdapter myBluetooth = null;// permite tarefas fundamentais do bluetooth: instanciar com add MAC, descobrir dispositivos...
    private Set<BluetoothDevice> pairedDevices;// cria conexoes com outros dispositivos, consultando suas info: nome,add, class and bonding state
    //private OutputStream outputStream = null;
    public static String EXTRA_ADDRESS ="device_address";
    private ArrayList<String> listDevicesAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a748e")));

        app = ((MiGuiaPreviaApp)getApplicationContext());
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

                //Calling widgets
        btnPaired = (Button)findViewById(R.id.id_buttonDevices);
        devicelist = (ListView)findViewById(R.id.id_listDevices);
        instructionsTV = (TextView)findViewById(R.id.id_textViewInstructions);

        instructionsTV.setText("Aperte no botão \"" + btnPaired.getText().toString() + "\" e, em seguida, escolha na lista abaixo o item correspondente ao dispositivo acoplado à bengala.");

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null){

            //Show a mensag. that the device has no bluetooth adapter
            app.msg("Não foi encontrado um dispositivo Bluetooth no seu aparelho");
            app.speechText("Não foi encontrado um dispositivo Bluetooth no seu aparelho");
            //finish apk
            finish();
        }
        else{

            if (myBluetooth.isEnabled()) //Return true if Bluetooth is currently enabled and ready for use
            { }
            else{
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //ACTION_REQUEST_ENABLE: Show a system activity that allows the user to turn on Bluetooth.
                startActivityForResult(turnBTon,1);
            }
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList(); //method that will be called
            }
        });

        app.speechText("Por favor, conecte seu aparelho ao dispositivo acoplado à bengala.");
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        TextToSpeech tts = app.getTextSpeech();
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void atualizarStatusDisponibilidadeLocaleSelecionado(int available) {
        switch (available) {

            case TextToSpeech.LANG_AVAILABLE:
                Log.d("TTS","Locale suportada, mas não por país ou variante!");
                app.setReadyToSpeak(true);
                break;

            case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                Log.d("TTS", "Locale suportada pela Localidade, mas não por país ou variante!");
                app.setReadyToSpeak(true);
                break;
            case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                Log.d("TTS", "Locale suportada !");
                app.setReadyToSpeak(true);
                break;

            case TextToSpeech.LANG_MISSING_DATA:
                Log.e("TTS", "Locale com dados faltando !");
                break;

            case TextToSpeech.LANG_NOT_SUPPORTED:
                Log.e("TTS", "Locale nao suportada !");
                break;

            default:
                break;
        }
    }

    private void pairedDevicesList(){
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> listDevicesName = new ArrayList();
        listDevicesAddress = new ArrayList();
        if (pairedDevices.size()>0){
            app.msg("Exibindo dispositivos Bluetooth pareados");
            for(BluetoothDevice bt : pairedDevices){
                listDevicesName.add(bt.getName());
                listDevicesAddress.add(bt.getAddress());
                //Get the device's name and the address
            }
        }
        else{
            String instructions = "No momento, não há nenhum dispositivo Bluetooth pareado. Por favor, vá nas configurações de Bluetooth, conecte com o dispositivo acoplado à bengala e, em seguida, acesse este aplicativo novamente.";
            app.msg("No momento, não há nenhum dispositivo Bluetooth pareado.");
            app.speechText(instructions);
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, listDevicesName);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener);
        //Method called when the device from the list is clicked
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener(){
        public void onItemClick (AdapterView<?> av, View v, int position, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            //String info = ((TextView) v).getText().toString();
            String address = listDevicesAddress.get(position);
            app.speechText("Conectando com " + (String) av.getItemAtPosition(position));

            // Make an intent to start next activity.
            Intent i = new Intent(MainActivity.this, ListTagMaps.class);

            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address);
            //this will be received at ledControl (class) Activity
            startActivity(i);
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
                app.setTextSpeech(myTTS);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = new Locale("pt", "br");
            myTTS.setLanguage(locale);
            int available = myTTS.isLanguageAvailable(locale);
            atualizarStatusDisponibilidadeLocaleSelecionado(available);
            app.setReadyToSpeak(true);
        } else if (status == TextToSpeech.ERROR) {
            app.setReadyToSpeak(false);
            Log.d(TAG,"Não foi possível iniciar TTs Engine");
            app.speechText("Não foi possível iniciar TTs Engine");

        }
    }
}
