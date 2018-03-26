package devimap.com.miguiaprevia;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Locale;

public class SplashActivity extends Activity /*implements TextToSpeech.OnInitListener*/ {

    private static int SPLASH_TIME_OUT = 3000;
    private int MY_DATA_CHECK_CODE = 0;
    MiGuiaPreviaApp app;
    private TextToSpeech myTTS;
    private static final String TAG = "Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*app = ((MiGuiaPreviaApp)getApplicationContext());
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);*/

        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                // Esse método será executado sempre que o timer acabar
                // E inicia a activity principal

                /*while (!app.isReadyToSpeak())
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // Fecha esta activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void atualizarStatusDisponibilidadeLocaleSelecionado(int available) {
        switch (available) {

            case TextToSpeech.LANG_AVAILABLE:
                Log.d("TTS", "Locale suportada, mas não por país ou variante!");
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
    }*/
}
