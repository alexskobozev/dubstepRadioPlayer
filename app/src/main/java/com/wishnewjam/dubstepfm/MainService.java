package com.wishnewjam.dubstepfm;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class MainService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final int NOTIFICATION_ID = 43432;
    public static final long TIME_TO_RETRY = 3000;

    public static final String ACTION_PLAY = "com.example.action.PLAY";
    public static final String WAKE_LOCK = "mp_wakelock";
    public static final String SP_KEY_BITRATE = "link";

    public static final String PLAYING = "playing";
    public static final String WAITING = "waiting";
    public static final String ERROR = "error";

    MediaPlayer mMediaPlayer = null;
    private WifiManager.WifiLock wifiLock;
    private String currentLink;
    private Statuses currentStatus = Statuses.IDLE;
    private AudioManager audioManager;
    private static Handler retryHandler;
    private MediaButtonIntentReceiver mMediaButtonReceiver;

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (retryHandler != null) {
            retryHandler.postDelayed(retryRunnable, TIME_TO_RETRY);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        sendMessage(ERROR);
        stopSelf();
        return false;
    }

    public enum Statuses {
        PLAYING, WAITING, IDLE
    }

    public MainService() {
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        if (retryHandler == null) {
            retryHandler = new Handler();
        }
        try {
            initMediaPlayer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }


    private void initMediaPlayer() throws IOException {
        if (currentStatus == Statuses.WAITING) return;
        currentStatus = Statuses.WAITING;
        sendMessage(WAITING);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        String link = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(SP_KEY_BITRATE, Links.LINK_128);

        if (mMediaPlayer != null
                && mMediaPlayer.isPlaying()) {
            if (link.equals(currentLink)) {
                sendMessage(PLAYING);
                return;
            } else {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer = null;
            }
        }
        initHeadsetReceiver();

        currentLink = link;

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setDataSource(link);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.prepareAsync();

    }

    private void initHeadsetReceiver() {
        if (mMediaButtonReceiver != null) {
            unregisterReceiver(mMediaButtonReceiver);
        }
        mMediaButtonReceiver = new MediaButtonIntentReceiver();
        IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        mediaFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(mMediaButtonReceiver, mediaFilter);

    }

    private final Runnable retryRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                initMediaPlayer();
            } catch (IOException e) {
                if (retryHandler != null) {
                    retryHandler.postDelayed(retryRunnable, 10000);
                }
            }
        }
    };

    private void initWakeLock() {
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WAKE_LOCK);
        wifiLock.acquire();
    }

    private void releaseWakeLock() {
        if (wifiLock != null) {
            wifiLock.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    public Statuses getStatus() {
        return currentStatus;
    }

    public class LocalBinder extends Binder {
        MainService getService() {
            return MainService.this;
        }
    }


    public void onPrepared(MediaPlayer player) {

        int result = audioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            player.start();
            initNotification();
            initWakeLock();
            currentStatus = Statuses.PLAYING;
            sendMessage(PLAYING);
        }

    }

    private AudioManager.OnAudioFocusChangeListener afChangeListener
            = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (mMediaPlayer != null) mMediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mMediaPlayer != null) mMediaPlayer.start();
                    else {
                        try {
                            initMediaPlayer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (mMediaPlayer != null) {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                        }
                        mMediaPlayer.reset();
                        mMediaPlayer.release();
                        mMediaPlayer = null;

                    } else {
                        stopSelf();
                    }
                    break;

            }


            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                if (mMediaPlayer != null) mMediaPlayer.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                try {
                    initMediaPlayer();
                } catch (IOException e) {
                    e.printStackTrace();
                    stopSelf();
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                stopSelf();
            }

        }
    };

    private void initNotification() {

        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_text))
                        .setContentIntent(resultPendingIntent);

        startForeground(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaButtonReceiver != null) {
            unregisterReceiver(mMediaButtonReceiver);
        }
        if (audioManager != null) {
            audioManager.abandonAudioFocus(afChangeListener);
        }
        releaseWakeLock();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (retryHandler != null) {
            retryHandler.removeCallbacks(retryRunnable);
        }
    }

    private void sendMessage(String message) {
        Intent intent = new Intent(MainActivity.ACTION_RECEIVE_AUDIO_INFO);
        intent.putExtra(MainActivity.TAG_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public class MediaButtonIntentReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "Media_receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                return;
            }
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return;
            }
            int action = event.getAction();
            if (action == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                    if (mMediaPlayer != null) {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.pause();
                        } else {
                            mMediaPlayer.prepareAsync();
                        }
                    }
                }

                if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY) {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.prepareAsync();
                    }
                }
            }
            abortBroadcast();

        }

    }

}
