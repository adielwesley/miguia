package devimap.com.miguiaprevia;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

/**
 * Created by nossopc on 01/10/16.
 */
public class MiGuiaPreviaApp extends Application {
    private BluetoothSocket btSocket;
    private TextToSpeech textSpeech;
    private boolean readyToSpeak = false;

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }

    public TextToSpeech getTextSpeech() {
        return textSpeech;
    }

    public void setTextSpeech(TextToSpeech textSpeech) {
        this.textSpeech = textSpeech;
    }

    public boolean isReadyToSpeak() {
        return readyToSpeak;
    }

    public void setReadyToSpeak(boolean readyToSpeak) {
        this.readyToSpeak = readyToSpeak;
    }

    public void speechText (String text) {
        if(isReadyToSpeak())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                textSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null);
            else
                textSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    public void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
