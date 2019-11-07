package com.psyovs.mp3player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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

    public class ServiceBinder extends Binder {

    }
}
