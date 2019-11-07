package com.psyovs.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    ToggleButton playPause;
    private MusicService.ServiceBinder musicBinder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView lv = (ListView) findViewById(R.id.songs);
        File musicDir = new File(
                Environment.getExternalStorageDirectory().getPath()+ "/Music/");
        File list[] = musicDir.listFiles();
        lv.setAdapter(new ArrayAdapter<File>(this,
                android.R.layout.simple_list_item_1, list));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter,
                                    View myView,
                                    int myItemInt,
                                    long mylng) {
                File selectedFromList =(File) (lv.getItemAtPosition(myItemInt));
                Log.d("g53mdp", selectedFromList.getAbsolutePath());
                // do something with selectedFromList...
            }
        });

        Intent intent = new Intent(this, MusicService.class);
        this.startService(intent);
        this.bindService(new Intent(this, MusicService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onToggle (View v) {

        playPause = (ToggleButton) findViewById(R.id.buttonState);

        playPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                } else {

                }
            }
        });

    }

    public void onStop (View v) {

    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder
                service) {
            Log.d("g53mdp", "MainActivity onServiceConnected");
            musicBinder = (MusicService.ServiceBinder) service;

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("g53mdp", "MainActivity onServiceDisconnected");
            musicBinder = null;
        }
    };

}
