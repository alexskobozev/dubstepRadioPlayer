package com.wishnewjam.dubstepfm

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.skyfishjy.library.RippleBackground
import java.lang.ref.WeakReference
import javax.inject.Inject

class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {

    val TAG = MainActivity::class.java.name

    private var rippleBackground: RippleBackground? = null
    private var loadingIndicator: View? = null

    @Inject
    lateinit var mediaPlayerInstance: MediaPlayerInstance

    private val callback: MediaPlayerInstance.CallbackInterface = object : MediaPlayerInstance.CallbackInterface {
        override fun onChangeStatus(status: Int) {
            when (status) {
                UIStates.STATUS_PLAY -> showPlaying()
                UIStates.STATUS_LOADING -> showLoading()
                UIStates.STATUS_STOP -> showStopped()
            }
        }

        override fun onError(error: String) {
            showError()
        }

    }

    private fun showLoading() {
        loadingIndicator?.visibility = View.VISIBLE
    }

    private fun showStopped() {
        loadingIndicator?.visibility = View.GONE
        rippleBackground?.stopRippleAnimation()

    }

    private fun showPlaying() {
        loadingIndicator?.visibility = View.GONE
        rippleBackground?.startRippleAnimation()

    }

    private fun showError() {
        loadingIndicator?.visibility = View.GONE
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
        loadingIndicator = findViewById(R.id.ll_loading)
        MyApplication.graph.inject(this)
        mediaPlayerInstance.activityCallback = WeakReference(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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