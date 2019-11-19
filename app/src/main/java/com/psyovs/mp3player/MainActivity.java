package com.psyovs.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.pszmdf.mp3test.MP3Player;

import java.io.File;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //ToggleButton playPause;
    //Button refresh;
    private ProgressBar bar;
    TextView songStart, songEnd;
    long songLength;
    //private String defaultLength = "00:00";
    private Button play, pause, stop;
    private MusicService.ServiceBinder musicBinder = null;
//    private CountDownTimer songTimer;
    private int seconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songStart = (TextView) findViewById(R.id.timeCurrent);
        songEnd = (TextView) findViewById(R.id.timeLeft);
        bar = (ProgressBar) findViewById(R.id.timeElapsed);

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
                musicBinder.getSong(selectedFromList.getAbsolutePath());
                musicBinder.notification();
                progress();
                //updateGraphic();
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

        if(musicBinder.state() == MP3Player.MP3PlayerState.PLAYING) {
            musicBinder.notification();
        } else {
            musicBinder.stopNotifs();
            if(serviceConnection!=null) {
                unbindService(serviceConnection);
                serviceConnection = null;
            }
        }
    }

    public void onToggle (View v) {

        play = (Button) findViewById(R.id.buttonPlay);
        pause = (Button) findViewById(R.id.buttonPause);
        stop = (Button) findViewById(R.id.buttonStop);
        //refresh = (Button) findViewById(R.id.buttonRefresh);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicBinder.state() == MP3Player.MP3PlayerState.PAUSED) {
                    musicBinder.notification();
                }
                musicBinder.play();
                progress();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicBinder.pause();
                if(musicBinder.state() == MP3Player.MP3PlayerState.PAUSED) {
                    musicBinder.stopNotifs();
                }
                progress();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicBinder.stop();
                musicBinder.stopNotifs();
                progress();
            }
        });

//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }


    // have to change the toggle to 2 buttons because it wont change the state correctly if a song is selected first...
//    public void onToggle (View v) {
//
//        playPause = (ToggleButton) findViewById(R.id.buttonState);
//
//        playPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    musicBinder.play();
//                } else {
//                    musicBinder.pause();
//                }
//            }
//        });
//
//    }

    // added to the new onToggle instead
//    public void onStop (View v) {
//        musicBinder.stop();
//    }

//    app crashes if this is called and song is selected first.
//    public void updateGraphic() {
//        if(musicBinder.state() == MP3Player.MP3PlayerState.PLAYING) {
//            playPause.setChecked(true);
//        }
//    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder
                service) {
            Log.d("g53mdp", "MainActivity onServiceConnected");
            musicBinder = (MusicService.ServiceBinder) service;
            progress();

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("g53mdp", "MainActivity onServiceDisconnected");
            musicBinder = null;
        }
    };



//  tried a countdowntimer at first, but i also needed a countup timer and it didnt work as expected
    // used a timertask in the end; but i tried using them nested, also didnt work as planned.
    class TimePassed extends TimerTask {

        @Override
        public void run() {

            seconds = musicBinder.progress()/1000;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    songStart.setText(formatSongTime(musicBinder.progress()));
                    songEnd.setText(formatSongTime(songLength));

                }
            });
//            songTimer = new CountDownTimer(songLength, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    songEnd.setText(formatSongTime(millisUntilFinished));
//                }
//
//                @Override
//                public void onFinish() {
//                    songEnd.setText(defaultLength);
//                }
//            }.start();
            bar.setProgress(seconds);
        }

    }

//    private void progress() {
//
//        songTimer = new CountDownTimer(songLength, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                songEnd.setText(formatSongTime(millisUntilFinished));
//                bar.setProgress((int) millisUntilFinished/1000);
//            }
//
//            @Override
//            public void onFinish() {
//                songEnd.setText(defaultLength);
//            }
//        }.start();
//        songTimer.start();
//
//    }
//
//    private void setupBar() {
//        bar.setMax();
//        bar.setProgress();
//    }

    private void progress() {
        songLength = musicBinder.duration();
        bar.setMax((int) songLength/1000);
        Timer playing = new Timer();
        playing.schedule(new TimePassed(),0, 1000);
    }


    // converts milliseconds to minutes and seconds for mm:ss formats (00:00)
    private String formatSongTime (long milS) {
        String ms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(milS), TimeUnit.MILLISECONDS.toSeconds(milS) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milS)));
        return ms;
    }

}
