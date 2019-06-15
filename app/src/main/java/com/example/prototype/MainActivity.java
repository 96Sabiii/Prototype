package com.example.prototype;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ListView listView;
    boolean isPlaying = false;
    TextView seekBarHint;
    MediaPlayer mp;
    SeekBar seekBar;
    SensorManager sensorManager;
    Sensor accelerometer;
    Context context;
    AudioManager am;

    //https://stackoverflow.com/questions/4777272/android-listview-with-different-layouts-for-each-row
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setContentView(R.layout.activity_main); // here, you can create a single layout with a listview

        //Create "Chat"
        listView = (ListView) findViewById(R.id.messages_view);

        final ListViewItem[] items = new ListViewItem[7];
        items[0] = new ListViewItem("Hi", CustomAdapter.TYPE_THEIR);
        items[1] = new ListViewItem("Hi", CustomAdapter.TYPE_MY);
        items[2] = new ListViewItem("wie gehts?", CustomAdapter.TYPE_THEIR);
        items[3] = new ListViewItem("gut und dir?", CustomAdapter.TYPE_MY);
        items[4] = new ListViewItem("muss dir unbedingt was erzählen", CustomAdapter.TYPE_THEIR);
        items[5] = new ListViewItem("aber das darf sonst keiner wissen, ok?", CustomAdapter.TYPE_THEIR);
        items[6] = new ListViewItem("", CustomAdapter.TYPE_THEIR_VOICE);

        CustomAdapter customAdapter = new CustomAdapter(this, R.id.text, items);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                //Toast.makeText(getBaseContext(), items[i].getText(), Toast.LENGTH_SHORT).show();
            }
        });

        //neues Layout da es sonst nicht findet
        RelativeLayout voiceLayout = (RelativeLayout) View.inflate(this, R.layout.their_voice, null);
        seekBarHint = voiceLayout.findViewById(R.id.textView);
        seekBar = voiceLayout.findViewById(R.id.seekBar);
        //seekBar();

        Log.d("MainActivity", "onCreate: Initializing Sensor Service");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        Log.d("MainActivity", "onCreate: Registered accelerometer listener");
    }

    //Timeline soll im Progress mitlaufen -> Funktioniert nicht
    //https://www.journaldev.com/22203/android-media-player-song-with-seekbar
    public void seekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if (x < 10)
                    seekBarHint.setText("0:0" + x);
                else
                    seekBarHint.setText("0:" + x);

                double percent = progress / (double) seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                int seekWidth = seekBar.getWidth();
                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                int labelWidth = seekBarHint.getWidth();
                seekBarHint.setX(offset + seekBar.getX() + val
                        - Math.round(percent * offset)
                        - Math.round(percent * labelWidth / 2));

                if (progress > 0 && mp != null && !mp.isPlaying()) {
                    //clearMediaPlayer();
                    //playButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                    MainActivity.this.seekBar.setProgress(0);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mp != null && mp.isPlaying()) {
                    mp.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    //Handle button Clicks
    public void ButtonOnClick(View v) {
        switch (v.getId()) {
            case R.id.playButton:
                if(isPlaying)
                {
                    mp.pause();
                    isPlaying = false;
                }
                else {
                    if (mp == null) {
                        //initalisieren
                        mp = MediaPlayer.create(this, R.raw.sound);
                        /*context = this.getBaseContext();
                        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        am.setMode(AudioManager.STREAM_MUSIC);
                        am.setSpeakerphoneOn(false);*/
                    }
                    mp.start();
                    isPlaying = true;
                }
                break;
        }
    }

    //sobald das Smartphone in z-Richtung um mehr als +/- 10 bewegt wird, wird die Nachricht abgespielt bzw. gestoppt
    //wird das Smartphone während des Abspiels in x-Richtung mehr als +/- 10 bewegt, so wird die Nachricht vor- bzw. zurückgespult
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.d("MainActivity", "onSensorChanged: x: " +  sensorEvent.values[0] + " y: " + sensorEvent.values[1] + " z: " + sensorEvent.values[2]);
        if (sensorEvent.values[2] <= -10){
            if (mp == null) {
                mp = MediaPlayer.create(this, R.raw.sound);
            }
            mp.start();
            isPlaying = true;
        }
        if (sensorEvent.values[2] > 10){
            if(isPlaying) {
                mp.pause();
                isPlaying = false;
            }
        }
        if (sensorEvent.values[0] <= -10){
            if (isPlaying){
                Log.d("MainActivity", "zurückspulen");
                mp.seekTo(mp.getCurrentPosition()-10000);
            }
        }
        if (sensorEvent.values[0] > 10){
            if (isPlaying){
                Log.d("MainActivity", "vorspulen");
                mp.seekTo(mp.getCurrentPosition()+10000);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}