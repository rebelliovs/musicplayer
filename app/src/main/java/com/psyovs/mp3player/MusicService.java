package com.psyovs.mp3player;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.pszmdf.mp3test.MP3Player;

public class MusicService extends Service {

    MP3Player mp3 = new MP3Player();

    private final IBinder binder = new ServiceBinder();

    private final String CHANNEL_ID = "100";
    int NOTIFICATION_ID = 001;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    private void notification() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music";
            String description = "Song is playing";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp) // taken from https://material.io/resources/icons/?search=music&icon=music_note&style=baseline
                .setContentTitle("Music..")
                .setContentText("..is playing")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_ID, mBuilder.build());

    }

    private void stopNotifs() {
        stopForeground(true);
        stopSelf();
    }

    public class ServiceBinder extends Binder {

        // these 2 need to be outside and called here again..
        // tried having the stopnotifs in the stop button, but not working how i planned
        // so i'll have them separate.
        void notification() {MusicService.this.notification();}
        void stopNotifs() {MusicService.this.stopNotifs();}

        public void play() {
            mp3.play();
        }

        public void pause() {
            mp3.pause();
        }

        public void stop() {
            mp3.stop();
        }

        public void getSong(String selected) {
            //check if a song is already playing, if so, stop it
            if(mp3.getState() == MP3Player.MP3PlayerState.PLAYING) {
                stop();
            }

            mp3.load(selected);
        }

        MP3Player.MP3PlayerState state() {
            return mp3.getState();
        }

        int duration() {
            return mp3.getDuration();
        }

        int progress() {
            return mp3.getProgress();
        }

    }

}
