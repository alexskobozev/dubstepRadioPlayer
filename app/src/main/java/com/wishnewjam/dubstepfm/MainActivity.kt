package com.wishnewjam.dubstepfm

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.skyfishjy.library.RippleBackground
import javax.inject.Inject

class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {

    val TAG = MainActivity::class.java.name

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

    @Inject
    lateinit var mediaPlayerInstance: MediaPlayerInstance

//    private fun observer(): Observer<Int> = Observer {
//        when (it) {
//            UIStates.STATUS_PLAY -> showPlaying()
//            UIStates.STATUS_STOP -> showStopped()
//            UIStates.STATUS_ERROR -> showError()
//        }
//    }

    private fun showStopped() {
        rippleBackground?.stopRippleAnimation()

    }

    private fun showPlaying() {


    }

    private fun showError() {
        showBuffering(false)
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
        stopPlaying(null)
    }

    fun startPlaying(view: View?) {
        mediaPlayerInstance.callPlay()
    }

    fun stopPlaying(view: View?) {
        mediaPlayerInstance.callStop()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rippleBackground = findViewById(R.id.ripple_background) as RippleBackground
        MyApplication.graph.inject(this)
//        handler = Handler()
    }


    private fun showBuffering(b: Boolean) {

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
        rippleBackground?.stopRippleAnimation()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDismiss(dialog: DialogInterface) {
        startPlaying(null)
    }


}