package com.wishnewjam.dubstepfm

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class MediaViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var application: MyApplication
    private lateinit var mediaPlayerInstance: MediaPlayerInstance
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        application = mockk(relaxed = true)
        mediaPlayerInstance = mockk(relaxed = true)
        sharedPreferences = mockk(relaxed = true)
        editor = mockk(relaxed = true)

        every { application.mediaPlayerInstance } returns mediaPlayerInstance
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } returns Unit

        mockkStatic(PreferenceManager::class)
        every { PreferenceManager.getDefaultSharedPreferences(application) } returns sharedPreferences
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `init should load default bitrate from shared preferences`() {
        every { sharedPreferences.getString(MainService.SP_KEY_BITRATE, Links.LINK_128) } returns Links.LINK_128

        val viewModel = MediaViewModel(application)

        assertEquals(Links.LINK_128, viewModel.currentUrl.value)
    }

    @Test
    fun `init should load saved bitrate from shared preferences`() {
        val savedBitrate = "http://stream.dubstep.fm/256mp3"
        every { sharedPreferences.getString(MainService.SP_KEY_BITRATE, Links.LINK_128) } returns savedBitrate

        val viewModel = MediaViewModel(application)

        assertEquals(savedBitrate, viewModel.currentUrl.value)
    }

    @Test
    fun `changeBitrate should update media player url`() {
        every { sharedPreferences.getString(MainService.SP_KEY_BITRATE, Links.LINK_128) } returns Links.LINK_128
        val viewModel = MediaViewModel(application)

        val newBitrate = "http://stream.dubstep.fm/64mp3"
        viewModel.changeBitrate(newBitrate)

        verify { mediaPlayerInstance.changeUrl(newBitrate) }
    }

    @Test
    fun `changeBitrate should save to shared preferences`() {
        every { sharedPreferences.getString(MainService.SP_KEY_BITRATE, Links.LINK_128) } returns Links.LINK_128
        val viewModel = MediaViewModel(application)

        val newBitrate = "http://stream.dubstep.fm/64mp3"
        viewModel.changeBitrate(newBitrate)

        verify { editor.putString(MainService.SP_KEY_BITRATE, newBitrate) }
    }

    @Test
    fun `changeBitrate should update live data value`() {
        every { sharedPreferences.getString(MainService.SP_KEY_BITRATE, Links.LINK_128) } returns Links.LINK_128
        val viewModel = MediaViewModel(application)

        val newBitrate = "http://stream.dubstep.fm/64mp3"
        viewModel.changeBitrate(newBitrate)

        assertEquals(newBitrate, viewModel.currentUrl.value)
    }

    @Test
    fun `changeBitrate should work for all bitrate options`() {
        every { sharedPreferences.getString(MainService.SP_KEY_BITRATE, Links.LINK_128) } returns Links.LINK_128
        val viewModel = MediaViewModel(application)

        Links.AS_ARRAY.forEach { bitrate ->
            viewModel.changeBitrate(bitrate)
            assertEquals(bitrate, viewModel.currentUrl.value)
            verify { mediaPlayerInstance.changeUrl(bitrate) }
        }
    }
}
