package com.example.logonrm.iot;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private final int REQ_CODE_SPEECH_INPUT = 100;

    private TextToSpeech t1;

    @BindView(R.id.tvResultado)
    TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        iniciarTextToSpeech();

    }

    private void iniciarTextToSpeech() {
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    private Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.20.52.7:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
    @OnClick(R.id.btLigar)
    public void ligar() {

        LedApi service = getRetrofit().create(LedApi.class);

        Call<ResponseAPI> call = service.ligarLed();

        call.enqueue(new Callback<ResponseAPI>() {
            @Override
            public void onResponse(Call<ResponseAPI> call, Response<ResponseAPI> response) {
                Toast.makeText(MainActivity.this,
                        response.body().getMensagem(),
                        Toast.LENGTH_SHORT).show();

                t1.speak(response.body().getMensagem(), TextToSpeech.QUEUE_FLUSH, null);
            }

            @Override
            public void onFailure(Call<ResponseAPI> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.btDesligar)
    public void desligar() {

        LedApi service = getRetrofit().create(LedApi.class);

        Call<ResponseAPI> call = service.desligarLed();

        call.enqueue(new Callback<ResponseAPI>() {
            @Override
            public void onResponse(Call<ResponseAPI> call, Response<ResponseAPI> response) {
                Toast.makeText(MainActivity.this,
                        response.body().getMensagem(),
                        Toast.LENGTH_SHORT).show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    t1.speak(response.body().getMensagem(),TextToSpeech.QUEUE_FLUSH,null,null);
                } else {
                    t1.speak(response.body().getMensagem(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }

            @Override
            public void onFailure(Call<ResponseAPI> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.btComandoDeVoz)
    public void comandoVoz() {
        promptSpeechInput();
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "O que deseja?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Speech nao suportado",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                    String comando =  result.get(0);
                    if(comando.equals("ligar")) {
                        ligar();
                    } else if (comando.equals("desligar")) {
                        desligar();
                    }
                }
                break;
            }

        }
    }
}
