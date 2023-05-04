package com.wishnewjam.dubstepfm

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RadioServiceConnection(context: Context) {

    // private val serviceConnection = object : ServiceConnection {
    //     override fun onServiceConnected(
    //         name: ComponentName?,
    //         service: IBinder?
    //     ) {
    //         val binder = service as RadioService.RadioBinder
    //         radioService = binder.getService()
    //         _isConnected.value = true
    //         observeRadioServiceState()
    //     }
    //
    //     override fun onServiceDisconnected(name: ComponentName?) {
    //         radioService = null
    //         _isConnected.value = false
    //     }
    // }
    //
    // private val applicationContext = context.applicationContext
    // private var radioService: RadioService? = null
    //
    // private val _isConnected = MutableStateFlow(false)
    // val isConnected: StateFlow<Boolean> get() = _isConnected
    //
    // private val _radioState =
    //     MutableStateFlow<RadioService.State>(RadioService.State.Stopped)
    // val radioState: StateFlow<RadioService.State> get() = _radioState
    //
    // init {
    //     bindService()
    // }
    //
    // private fun bindService() {
    //     Intent(applicationContext, RadioService::class.java).also { intent ->
    //         applicationContext.bindService(
    //             intent,
    //             serviceConnection,
    //             Context.BIND_AUTO_CREATE
    //         )
    //     }
    // }
    //
    // fun unbindService() {
    //     applicationContext.unbindService(serviceConnection)
    // }
    //
    // private fun observeRadioServiceState() {
    //     // TODO:
    //     // radioService?.state?.collectIn(viewModelScope) { state ->
    //     //     _radioState.value = state
    //     // }
    // }
    //
    // fun play() {
    //     radioService?.play()
    // }
    //
    // fun stop() {
    //     radioService?.stop()
    // }
}
