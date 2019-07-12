package com.example.prototype2;

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
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ListView listView;
    boolean isPlaying = false;
    TextView seekBarHint;
    MediaPlayer mp;
    SeekBar seekBar;
    SensorManager sensorManager;
    Sensor proximitySensor;
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

        final ListViewItem[] items = new ListViewItem[6];
        items[0] = new ListViewItem("Hi", CustomAdapter.TYPE_MY);
        items[1] = new ListViewItem("Hi, wie gehts?", CustomAdapter.TYPE_THEIR);
        items[2] = new ListViewItem("gut und dir?", CustomAdapter.TYPE_MY);
        items[3] = new ListViewItem("muss dir unbedingt was erzählen", CustomAdapter.TYPE_THEIR);
        items[4] = new ListViewItem("aber das darf sonst keiner wissen, ok?", CustomAdapter.TYPE_THEIR);
        items[5] = new ListViewItem("", CustomAdapter.TYPE_THEIR_VOICE);

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

        Log.d("MainActivity", "onCreate: Initializing Sensor Service");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(MainActivity.this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("MainActivity", "onCreate: Registered proximity listener");
    }

    //Handle button Clicks
    public void ButtonOnClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if(isPlaying) {
                    mp.pause();
                    isPlaying = false;
                }
                else {
                    if (mp == null) {
                        //initalisieren
                        context = this.getBaseContext();

                        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        am.setMode(AudioManager.STREAM_MUSIC);
                        mp = MediaPlayer.create(this, R.raw.sound);
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        am.setSpeakerphoneOn(true);
                    }
                    mp.start();
                    isPlaying = true;
                }
                break;
            case R.id.ff:
                if(isPlaying){
                    seekForward();
                }
                break;
            case R.id.rew:
                if (isPlaying){
                    seekRewind();
                }
                break;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (isPlaying){
            if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (sensorEvent.values[0] == 0) {
                    am.setSpeakerphoneOn(false);
                } else {
                    am.setSpeakerphoneOn(true);
                    mp.pause();
                    isPlaying = false;
                }
            }
        }
    }

    public void seekForward() {
        if (mp.getCurrentPosition() + 10000 <= mp.getDuration()){
            mp.seekTo(mp.getCurrentPosition() + 10000);
        } else {
            // forward to end
            mp.seekTo(mp.getDuration());
        }
        Toast t1 = Toast.makeText(this, "10 Sekunden vorgespult", Toast.LENGTH_SHORT-2000);
        t1.setGravity(0,0,270);
        t1.show();
    }

    public void seekRewind() {
        if (mp.getCurrentPosition() - 10000 >= 0){
            mp.seekTo(mp.getCurrentPosition() - 10000);
        } else {
            // backward to start
            mp.seekTo(0);
        }
        Toast t2= Toast.makeText(this, "10 Sekunden zurück gespult", Toast.LENGTH_SHORT-2000);
        t2.setGravity(0,0,270);
        t2.show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}