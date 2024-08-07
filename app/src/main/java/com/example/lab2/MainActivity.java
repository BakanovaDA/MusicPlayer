package com.example.lab2;

import static com.example.lab2.R.xml.activity_main;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    SoundPool sSoundPool;
    Button stopButton;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch loopButton;
    SeekBar seekBar;
    private HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();
    private int id; // id трека
    private int streamId = 0; // счетчик потоков
    private float rate = 1; // скорость воспроизведения
    private int loop = 0; // количество повторов

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        sSoundPool = createNewSoundPool();

        AssetFileDescriptor hollydolly, popcorn, superstar, barbie;
        try {
            hollydolly = getAssets().openFd("holly-dolly.m4a");
            popcorn = getAssets().openFd("popcorn.m4a");
            superstar = getAssets().openFd("superstar.m4a");
            barbie = getAssets().openFd("barbie-girl.m4a");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageButton hollydollyButton = (ImageButton) findViewById(R.id.hollydolly);
        ImageButton crazyfrogButton = (ImageButton) findViewById(R.id.crazyfrog);
        ImageButton jakartaButton = (ImageButton) findViewById(R.id.jakarta);
        ImageButton barbieButton = (ImageButton) findViewById(R.id.barbie);

        soundID.put(1 , sSoundPool.load(hollydolly, 1));
        soundID.put(2 , sSoundPool.load(popcorn, 2));
        soundID.put(3 , sSoundPool.load(superstar, 3));
        soundID.put(4 , sSoundPool.load(barbie, 4));

        hollydollyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { playSound(1); }
        });
        crazyfrogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { playSound(2); }
        });
        jakartaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { playSound(3); }
        });
        barbieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { playSound(4); }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setProgress(50);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin((int) 0);
        }
        seekBar.setMax((int) 100);
        seekBar.setClickable(false);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        loopButton = (Switch) findViewById(R.id.loop);
        stopButton = (Button) findViewById(R.id.stop);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { sSoundPool.autoPause(); }
        });
    }

    private SoundPool createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sSoundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(attributes)
                .build();
        return sSoundPool;
    }

    private void playSound(int sound) {

        if (loopButton.isChecked())
            loop = -1;
        else
            loop = 0;

        id = sound;
        streamId = sSoundPool.play(soundID.get(id), 1, 1, 0, loop, rate);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            rate = (float) ((float) progress / (float) seekBar.getMax() + 0.5);
            sSoundPool.setRate(streamId, rate);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    protected void onDestroy() {
        if (sSoundPool != null) {
            sSoundPool.release();
            sSoundPool = null;
        }
        super.onDestroy();
    }
}
