package com.wishnewjam.dubstepfm

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.skyfishjy.library.RippleBackground

class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {

    companion object {
        private val DOT = "."
        val TAG_MESSAGE = "message"
        val ACTION_RECEIVE_AUDIO_INFO = "audio_info"
    }

    private var mIsBound: Boolean = false
    private var rippleBackground: RippleBackground? = null
    private var dotsCount: Int = 0
    private var mBoundService: MainService? = null
    private var handler: Handler? = null

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra(TAG_MESSAGE)
            if (MainService.PLAYING == message) {
                rippleBackground?.startRippleAnimation()
                showBuffering(false)
            } else if (MainService.WAITING == message) {
                showBuffering(true)
            } else if (MainService.ERROR == message) {
                showError()
            }
        }
    }

    private val mHandlerRunnable = Runnable {
        if (mBoundService?.status == MainService.Statuses.PLAYING) {
            title = getString(R.string.app_name)
        } else {
            dotsCount++
            if (dotsCount > 5) dotsCount = 0
            var msg = getString(R.string.buffering)
            for (i in 0..dotsCount - 1) {
                msg += DOT
            }
            title = msg
            post(500)

        }
    }

    private fun post(i: Long) {
        handler?.postDelayed(mHandlerRunnable, i)
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mBoundService = (service as MainService.LocalBinder).service
            checkStatus()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBoundService = null
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler()
        rippleBackground = findViewById(R.id.ripple_background) as RippleBackground
    }

    private fun showError() {
        showBuffering(false)
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
        stopPlaying(null)
    }

    private fun showBuffering(b: Boolean) {
        dotsCount = 0
        if (b) {
            handler?.postDelayed(mHandlerRunnable, 0)
        } else {
            handler?.removeCallbacks(mHandlerRunnable)
            title = getString(R.string.app_name)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when (item.itemId) {
            R.id.action_bitrate -> {
                showBitrateChooser()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showBitrateChooser() {
        val bitrateFragment = ChooseBitrateDialogFragment()
        bitrateFragment.show(supportFragmentManager, "bitrate")
    }

    override fun onPause() {
        super.onPause()
        doUnbindService()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        rippleBackground?.stopRippleAnimation()
    }

    override fun onResume() {
        super.onResume()
        doBindService()
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                IntentFilter(ACTION_RECEIVE_AUDIO_INFO))
        //        checkStatus();
    }

    private fun checkStatus() {
        if (mBoundService?.status == MainService.Statuses.PLAYING) {
            rippleBackground?.startRippleAnimation()
            showBuffering(false)
        } else if (mBoundService?.status == MainService.Statuses.WAITING) {
            rippleBackground?.stopRippleAnimation()
            showBuffering(true)
        } else {
            rippleBackground?.stopRippleAnimation()
            showBuffering(false)
        }


    }

    private fun doBindService() {
        bindService(Intent(this@MainActivity,
                MainService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        mIsBound = true
    }

    private fun doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection)
            mIsBound = false
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        startPlaying(null)
    }

    fun startPlaying(view: View?) {
        if (mBoundService?.status == MainService.Statuses.WAITING) {
            return
        }
        startService(Intent(this, MainService::class.java))
    }

    fun stopPlaying(view: View?) {
        doUnbindService()
        rippleBackground?.stopRippleAnimation()
        stopService(Intent(this, MainService::class.java))
    }

}