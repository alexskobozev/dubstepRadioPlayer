package com.wishnewjam.dubstepfm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        DialogInterface.OnDismissListener {

    public static final String ACTION_RECEIVE_AUDIO_INFO = "audio_info";
    private static final String DOT = ".";
    public static final String TAG_MESSAGE = "message";
    private boolean mIsBound;
    private RippleBackground rippleBackground;
    private static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        Button playButton = (Button) findViewById(R.id.tv_play);
        playButton.setOnClickListener(this);

        Button stopButton = (Button) findViewById(R.id.tv_stop);
        stopButton.setOnClickListener(this);

//        ImageView logoImageView = (ImageView) findViewById(R.id.iv_logo);
//        logoImageView.setOnClickListener(this);

        rippleBackground = (RippleBackground) findViewById(R.id.ripple_background);
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(TAG_MESSAGE);
            if (MainService.PLAYING.equals(message)) {
                rippleBackground.startRippleAnimation();
                showBuffering(false);
            } else if (MainService.WAITING.equals(message)) {
                showBuffering(true);
            } else if (MainService.ERROR.equals(message)) {
                showError();
            }
        }
    };

    private void showError() {
        showBuffering(false);
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        stopPlaying();
    }

    private void showBuffering(boolean b) {
        dotsCount = 0;
        if (b) {
            handler.postDelayed(mHandlerRunnable, 0);
        } else {
            if (handler != null) {
                handler.removeCallbacks(mHandlerRunnable);
            }
            setTitle(getString(R.string.app_name));
        }
    }

    private int dotsCount;
    private final Runnable mHandlerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBoundService.getStatus().equals(MainService.Statuses.PLAYING)) {
                setTitle(getString(R.string.app_name));
            } else {
                dotsCount++;
                if (dotsCount > 5) dotsCount = 0;
                String msg = getString(R.string.buffering);
                for (int i = 0; i < dotsCount; i++) {
                    msg += DOT;
                }
                setTitle(msg);
                handler.postDelayed(mHandlerRunnable, 500);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_bitrate:
                showBitrateChooser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showBitrateChooser() {
        DialogFragment bitrateFragment = new ChooseBitrateDialogFragment();
        bitrateFragment.show(getSupportFragmentManager(), "bitrate");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_play:
                startPlaying();
                break;
            case R.id.tv_stop:
                stopPlaying();
                break;
//            case R.id.iv_logo:
//                showTrackInfo();
//                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnbindService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        rippleBackground.stopRippleAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ACTION_RECEIVE_AUDIO_INFO));
//        checkStatus();
    }


    private void stopPlaying() {
        doUnbindService();
        rippleBackground.stopRippleAnimation();
        stopService(new Intent(this, MainService.class));
    }

    private void startPlaying() {
        if (mBoundService != null && mBoundService.getStatus() == MainService.Statuses.WAITING) {
            return;
        }
        startService(new Intent(this, MainService.class));
    }


    private MainService mBoundService;

    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((MainService.LocalBinder) service).getService();
            checkStatus();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    private void checkStatus() {
        if (mBoundService != null) {
            if (mBoundService.getStatus() == MainService.Statuses.PLAYING) {
                rippleBackground.startRippleAnimation();
                showBuffering(false);
            } else if (mBoundService.getStatus() == MainService.Statuses.WAITING) {
                rippleBackground.stopRippleAnimation();
                showBuffering(true);
            } else {
                rippleBackground.stopRippleAnimation();
                showBuffering(false);
            }
        }


    }

    private void doBindService() {
        bindService(new Intent(MainActivity.this,
                MainService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        startPlaying();
    }
}
