package devimap.com.miguiaprevia;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SpeakDetinationAcivity extends AppCompatActivity {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    //private ListView mlvTextMatches;
    private TextView mtvDirection;
    private TextView mtvDestinationNotFound;
    private Button mbtSpeak;
    BluetoothSocket btSocket = null;
    MiGuiaPreviaApp app;

    //private Handler bluetoothIn;
    private Handler handler;

    private TagMap map;
    private String dest;
    private String readedTag = "";
    private ArrayList<String> textMatchlist;

    private final String noReadadTag = "Prosseguir no piso tátil";
    private final String readANewTag = noReadadTag;

    private ToneGenerator toneG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak_detination_acivity);

        app = ((MiGuiaPreviaApp)getApplicationContext());
        btSocket = app.getBtSocket();

        app.speechText("Preciso identificar onde você está. Para isso, prossiga adiante no piso tátil");
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
        tone();

        Intent newint = getIntent();
        map = (TagMap) newint.getSerializableExtra("mapObject");

        mbtSpeak       = (Button) findViewById(R.id.id_buttonSpeak);
        mtvDirection = (TextView)findViewById(R.id.id_edittextDirection);
        mtvDestinationNotFound = (TextView) findViewById(R.id.id_textviewDestNotFound);
        mtvDestinationNotFound.setVisibility(View.INVISIBLE);
        mbtSpeak.setEnabled(false);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a748e")));

        readedTag = noReadadTag;

        handler = new Handler() {

            public void handleMessage(android.os.Message msg) {
                String readMessage = "";
                if (msg.obj != null) {
                    readMessage = (String) msg.obj;                                      // msg.arg1 = bytes from connect thread
                }
                if (msg.what == 1) {
                    mtvDirection.setText(readMessage);
                } else if (msg.what == 2) {
                    mtvDestinationNotFound.setVisibility(View.INVISIBLE);
                    mbtSpeak.setEnabled(false);
                } else if (msg.what == 3) {
                    mtvDestinationNotFound.setVisibility(View.VISIBLE);
                    mtvDestinationNotFound.setText(readMessage);
                } else if (msg.what == 4) {
                    mbtSpeak.setEnabled(true);
                } else if (msg.what == 5) {
                    mbtSpeak.setEnabled(false);
                }
            }
        };

        waitANewTagRead();

        CheckVoiceRecognation();
        mbtSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Speak(v);
            }
        });

        if (btSocket!=null) {
            ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
            //I send a character when resuming.beginning transmission to check device is connected
            //If it is not an exception will be thrown in the write method and finish() will be called
            mConnectedThread.write("x");
        }
    }

    private void tone() {
        toneG.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200);
    }

    private void dealWithReceivedStringFromBluetooth (String receivedString) {
        int endOfLineIndex = receivedString.indexOf("~");                   // determine the end-of-line
        if (endOfLineIndex > 0) {                           // make sure there data before ~
            String dataInPrint = receivedString.substring(0, endOfLineIndex);    // extract string
            int dataLength = dataInPrint.length();                          //get length of data received

            if (receivedString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
            {

                //Log.d("Tag lida", "Tag lida = " + tag);
                readedTag = receivedString.substring(1, dataLength);   //update the textviews with sensor values
            }
        }
    }

    private void waitANewTagRead() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.obtainMessage(2, -1, -1, null).sendToTarget();
                handler.obtainMessage(1, readANewTag.length(), -1, readANewTag).sendToTarget();
                while (readedTag.equals(noReadadTag));
                app.speechText("Aperte o botão Falar e, após ouvir um bip, diga o nome do local para onde deseja ir.");
                tone();
                handler.obtainMessage(1, ("Tag Lida").length(), -1, "Tag Lida").sendToTarget();
                handler.obtainMessage(4, -1, -1, null).sendToTarget();
            }
        }).start();
    }

    // Verifica se os pacotes para reconhecimento de voz estão instalados no aplicativo
    public  void CheckVoiceRecognation(){

        PackageManager pm = getPackageManager(); // Class for retrieving various kinds of information related to the application packages that are currently installed on the device.
        // getPackageManager() : Return PackageManager instance to find global package information.
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0); // Recupera as activities para reconhecimento de voz
        // List<ResolveInfo> queryIntentActivities (Intent intent, int flags) : Retrieve all activities that can be performed for the given intent.

        if(activities.size() == 0) // Se nenhuma activity de reconhecimento de voz foi recuperada
        {
            mbtSpeak.setEnabled(false);
            msg("O reconhecedor de voz não está disponível");
            app.speechText("O reconhecedor de voz não está disponível no seu aparelho");
        }


    }

    public void Speak(View v)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // Starts an activity that will prompt the user for speech and send it through a speech recognizer.

        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getClass().getPackage().getName()); // RecognizerIntent.EXTRA_CALLING_PACKAGE: The extra key used in an intent to the speech recognizer for voice search.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Fale o local para onde deseja ir."); // RecognizerIntent.EXTRA_PROMPT: Optional text prompt to show to the user when asking them to speak.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH); // RecognizerIntent.EXTRA_LANGUAGE_MODEL: Informs the recognizer which speech model to prefer when performing ACTION_RECOGNIZE_SPEECH. RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH: Use a language model based on web search terms.

        int noOfMatches = 1;
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,noOfMatches);
        // RecognizerIntent.EXTRA_MAX_RESULTS: Optional limit on the maximum number of results to return. If omitted the recognizer will choose how many results to return. Must be an integer.

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE); // Inicia uma activity que irá requisitar uma fala do usuário
        /*
            Starting another activity doesn't have to be one-way. You can also start another activity and receive a result back.
            To receive a result, call startActivityForResult() (instead of startActivity()).

            Of course, the activity that responds must be designed to return a result.
            When it does, it sends the result as another Intent object.
            Your activity receives it in the onActivityResult() callback.

            The integer argument is a "request code" that identifies your request. When you receive the result Intent,
            the callback provides the same request code so that your app can properly identify the result
            and determine how to handle it.
         */
    }




    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        /*
            int requestCode:
            The request code you passed to startActivityForResult().

            int resultCode:
            A result code specified by the second activity (no nosso caso, "the second activity" é a activity que foi iniciada em Speak). This is either RESULT_OK if the operation was successful
            or RESULT_CANCELED if the user backed out or the operation failed for some reason.

            Intent data:
            An Intent that carries the result data.
         */
        if(resultCode == RESULT_OK)
        {
            textMatchlist = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); // Obtém uma lista de string dos resultados retornados na Intent data

            if(!textMatchlist.isEmpty() ) // Se a lista de resultados não está vazia
            {
                final TagDAO tagDAO = new TagDAO(this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        dest = textMatchlist.get(0);
                        String direction;
                        Log.i("TAG", "Li essa tag #" + readedTag + "~");
                        int srcId = tagDAO.getTagIdByHexKey(readedTag, map.getMapID());//map.getTagIdByHexKey(readedTag);
                        int destId = tagDAO.getTagIdByPlaceName(dest.toLowerCase(), map.getMapID());//map.getTagIdByPlaceName(dest);

                        int prev = srcId, current = srcId, next;

                        if(srcId!=-1 && destId!=-1)
                            /*
                                Verifica se o destino existe
                             */
                        {
                            app.speechText("Você solicitou: " + dest);
                            /*String s = "origem: " + srcId + ", destino: " + destId;
                            handler.obtainMessage(1, s.length(), -1, s).sendToTarget();*/

                            // Declaração de variáveis locais
                            int readedTagId = srcId;
                            int attempts = 1, i = 1;
                            boolean outOfPath = false;
                            handler.obtainMessage(2, -1, -1, null).sendToTarget();

                            // Encontra caminho entre origem e destino
                            map.findCaminho(srcId, destId);
                            next = map.findNextInThePath(current);

                            direction = map.getDirection(prev, current, next);
                            Log.i("Debug " + i, "Direction: " + direction);
                            app.speechText(direction);
                            handler.obtainMessage(1, direction.length(), -1, direction).sendToTarget();



                            // Imprime anterior, atual e próximo da primeira iteração
                            Log.i("Debug " + i, "Leu a tag: " + readedTagId);
                            Log.i("Debug " + i, "prev: " + prev); // anterior
                            Log.i("Debug " + i, "current: " + current); // atual
                            Log.i("Debug " + i, "next: " + next); // próximo

                            while (current != destId && next!=-1) {
                                readedTagId = tagDAO.getTagIdByHexKey(readedTag, map.getMapID());//map.getTagIdByHexKey(readedTag);
                                if (readedTagId != current) {
                                    Log.i("Debug " + i, "Leu a tag: " + readedTagId);
                                    while (readedTagId != next && attempts < 4) {
                                        Log.i("Debug " + i, "entrou com readedTagId: " + readedTagId);
                                        outOfPath = true;
                                        int readedTagAux;

                                        // Diz para o usuário voltar
                                        String turnBack = "Vire para trás e siga adiante";
                                        //Log.i("Debug", "Vire para trás e siga adiante");
                                        app.speechText(turnBack);
                                        handler.obtainMessage(1, turnBack.length(), -1, turnBack).sendToTarget();

                                        Log.i("Debug " + i, "Está aguardando a tag: " + current);
                                        do {
                                            readedTagAux = tagDAO.getTagIdByHexKey(readedTag, map.getMapID()); //map.getTagIdByHexKey(readedTag);
                                        }  while (readedTagAux == readedTagId);

                                        tone();

                                        Log.i("Debug " + i, "Leu a tag: " + readedTagAux);
                                        if (readedTagAux != current) {
                                            //refaz o caminho e começa de novo
                                            Log.i("Debug " + i, "É necessário recalcular o caminho");

                                            map.findCaminho(readedTagAux, destId);
                                            prev = readedTagAux;
                                            current = readedTagAux;
                                            readedTagId = current;
                                            break;


                                        } else {
                                            direction = "Prossiga adiante";
                                            app.speechText(direction);
                                            handler.obtainMessage(1, direction.length(), -1, direction).sendToTarget();
                                            Log.i("Debug " + i, "Está aguardando a tag: " + next);
                                            do {
                                                readedTagId = tagDAO.getTagIdByHexKey(readedTag, map.getMapID());
                                            }  while (readedTagId == readedTagAux);
                                            tone();
                                            Log.i("Debug " + i, "Leu a a tag: " + readedTagId);
                                            prev = current;
                                            current = readedTagId;
                                            /*Log.i("Debug", "prev: " + prev);
                                            Log.i("Debug", "current: " + current);
                                            Log.i("Debug", "next: " + next);*/
                                        }
                                        attempts++;
                                    }
                                    if(outOfPath) {
                                        Log.i("Debug", "saiu entrou com readedTagId:" + readedTagId);
                                        outOfPath = false;
                                        next = map.findNextInThePath(current);
                                    }
                                    else{
                                        prev = current;
                                        current = readedTagId;
                                        next = map.findNextInThePath(current);
                                    }

                                    i++;
                                    Log.i("Debug " + i, "prev: " + prev);
                                    Log.i("Debug " + i, "current: " + current);
                                    Log.i("Debug " + i, "next: " + next);

                                    tone();
                                    direction = map.getDirection(prev, current, next);
                                    Log.i("Debug " + i, "Direction: " + direction);
                                    app.speechText(direction);
                                    handler.obtainMessage(1, direction.length(), -1, direction).sendToTarget();
                                }
                            }
                            /*direction = map.getDirection(prev, current, next);
                            Log.i("Debug", "Direction: " + direction);
                            app.speechText(direction);*/
                            readedTag=noReadadTag;
                            handler.obtainMessage(4, -1, -1, null).sendToTarget();
                        }
                        else
                        {
                            String msg;
                            if (srcId == -1) {
                                msg = "Origem Não Reconhecida, Tag: " + readedTag;
                                app.speechText("Desculpe, não identifiquei onde você está.");
                            } else {
                                msg = "Destino não existente: " + dest;
                                app.speechText("O local " + dest + " não é um local reconhecido.");
                            }
                            handler.obtainMessage(3, msg.length(), -1, msg).sendToTarget();
                        }
                        tagDAO.closeConnection();
                    }
                }).start();

            }
        } else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) //int RESULT_AUDIO_ERROR: Result code returned when an audio error was encountered
        {
            msg("Erro de Audio");
        } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) //int RESULT_CLIENT_ERROR: Result code returned when there is a generic client error
        {
            msg("Erro no Cliente");
        } else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) //int RESULT_NETWORK_ERROR: Result code returned when a network error was encountered
        {
            msg("Erro na Rede");
        } else if(resultCode == RecognizerIntent.RESULT_NO_MATCH) // int RESULT_NO_MATCH: Result code returned when no matches are found for the given speech
        {
            msg("Sem Correspondências");
        } else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR) // int RESULT_SERVER_ERROR: Result code returned when the recognition server returns an error
        {
            msg("Erro no Servidor");
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private  boolean running = true;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int length;

            // Keep looping to listen for received messages
            while (running) {
                try {
                    length = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, length);
                    // Send the obtained bytes to the UI Activity via handler
                    //bluetoothIn.obtainMessage(handlerState, length, -1, readMessage).sendToTarget();
                    dealWithReceivedStringFromBluetooth(readMessage);
                } catch (IOException e) {
                    running = false;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                app.msg("Falha de conexão bluetooth");
                finish();
            }
        }
    }
}
