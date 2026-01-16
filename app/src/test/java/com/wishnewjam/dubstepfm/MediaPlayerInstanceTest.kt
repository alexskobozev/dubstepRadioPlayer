package com.wishnewjam.dubstepfm

import android.content.Context
import android.content.SharedPreferences
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class MediaPlayerInstanceTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mockCallback: MediaPlayerInstance.CallbackInterface

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        mockCallback = mockk(relaxed = true)

        // Mock SharedPreferences for CurrentUrl
        sharedPreferences = mockk(relaxed = true)
        editor = mockk(relaxed = true)
    }

    @Test
    fun `initial status should be STATUS_UNDEFINED`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        assertEquals(UIStates.STATUS_UNDEFINED, mediaPlayerInstance.status)
    }

    @Test
    fun `serviceCallback should be null initially`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        assertEquals(null, mediaPlayerInstance.serviceCallback)
    }

    @Test
    fun `callPlay should not change status when already playing`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.status = UIStates.STATUS_PLAY

        mediaPlayerInstance.callPlay()

        // Status should remain PLAY (not change to LOADING)
        assertEquals(UIStates.STATUS_PLAY, mediaPlayerInstance.status)
    }

    @Test
    fun `callPlay should not change status when loading`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.status = UIStates.STATUS_LOADING

        mediaPlayerInstance.callPlay()

        // Status should remain LOADING
        assertEquals(UIStates.STATUS_LOADING, mediaPlayerInstance.status)
    }

    @Test
    fun `callPlay should start playing when stopped`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.status = UIStates.STATUS_STOP

        mediaPlayerInstance.callPlay()

        // Status should be LOADING after calling play
        assertEquals(UIStates.STATUS_LOADING, mediaPlayerInstance.status)
    }

    @Test
    fun `callPlay should start playing when undefined`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)

        mediaPlayerInstance.callPlay()

        // Status should be LOADING after calling play
        assertEquals(UIStates.STATUS_LOADING, mediaPlayerInstance.status)
    }

    @Test
    fun `callStop should stop when playing`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.serviceCallback = mockCallback
        mediaPlayerInstance.status = UIStates.STATUS_PLAY

        mediaPlayerInstance.callStop()

        assertEquals(UIStates.STATUS_STOP, mediaPlayerInstance.status)
        verify { mockCallback.onChangeStatus(UIStates.STATUS_STOP) }
    }

    @Test
    fun `callStop should do nothing when not playing`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.serviceCallback = mockCallback
        mediaPlayerInstance.status = UIStates.STATUS_STOP

        mediaPlayerInstance.callStop()

        // Should not notify callback since we weren't playing
        verify(exactly = 0) { mockCallback.onChangeStatus(any()) }
    }

    @Test
    fun `onPlayerStateChanged should notify loading when buffering`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.serviceCallback = mockCallback

        mediaPlayerInstance.onPlayerStateChanged(true, Player.STATE_BUFFERING)

        assertEquals(UIStates.STATUS_LOADING, mediaPlayerInstance.status)
        verify { mockCallback.onChangeStatus(UIStates.STATUS_LOADING) }
    }

    @Test
    fun `onPlayerStateChanged should notify play when ready and playWhenReady`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.serviceCallback = mockCallback

        mediaPlayerInstance.onPlayerStateChanged(true, Player.STATE_READY)

        assertEquals(UIStates.STATUS_PLAY, mediaPlayerInstance.status)
        verify { mockCallback.onChangeStatus(UIStates.STATUS_PLAY) }
    }

    @Test
    fun `onPlayerStateChanged should not notify when ready but not playWhenReady`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.serviceCallback = mockCallback

        mediaPlayerInstance.onPlayerStateChanged(false, Player.STATE_READY)

        // Should not change status or notify
        verify(exactly = 0) { mockCallback.onChangeStatus(UIStates.STATUS_PLAY) }
    }

    @Test
    fun `onPlayerStateChanged should notify stop when ended`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.serviceCallback = mockCallback

        mediaPlayerInstance.onPlayerStateChanged(true, Player.STATE_ENDED)

        assertEquals(UIStates.STATUS_STOP, mediaPlayerInstance.status)
        verify { mockCallback.onChangeStatus(UIStates.STATUS_STOP) }
    }

    @Test
    fun `onPlayerStateChanged should do nothing when idle`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.serviceCallback = mockCallback
        val initialStatus = mediaPlayerInstance.status

        mediaPlayerInstance.onPlayerStateChanged(true, Player.STATE_IDLE)

        // Status should not change
        assertEquals(initialStatus, mediaPlayerInstance.status)
        verify(exactly = 0) { mockCallback.onChangeStatus(any()) }
    }

    @Test
    fun `onPlayerError should set status to error`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        mediaPlayerInstance.serviceCallback = mockCallback
        val mockError = mockk<ExoPlaybackException>(relaxed = true)

        mediaPlayerInstance.onPlayerError(mockError)

        assertEquals(UIStates.STATUS_ERROR, mediaPlayerInstance.status)
        verify { mockCallback.onError(any()) }
    }

    @Test
    fun `changeUrl should update url and play when url is different`() {
        val mediaPlayerInstance = MediaPlayerInstance(context)
        val newUrl = "http://stream.dubstep.fm/256mp3"

        mediaPlayerInstance.changeUrl(newUrl)

        // Should start loading since URL changed
        assertEquals(UIStates.STATUS_LOADING, mediaPlayerInstance.status)
    }

    @Test
    fun `callback interface should have required methods`() {
        val callback = object : MediaPlayerInstance.CallbackInterface {
            var statusChanged = false
            var errorReceived = false
            var metadataChanged = false

            override fun onChangeStatus(status: Int) {
                statusChanged = true
            }

            override fun onError(error: String) {
                errorReceived = true
            }

            override fun onMetaDataTrackChange(trackName: String) {
                metadataChanged = true
            }
        }

        callback.onChangeStatus(UIStates.STATUS_PLAY)
        callback.onError("test error")
        callback.onMetaDataTrackChange("test track")

        assertEquals(true, callback.statusChanged)
        assertEquals(true, callback.errorReceived)
        assertEquals(true, callback.metadataChanged)
    }
}
