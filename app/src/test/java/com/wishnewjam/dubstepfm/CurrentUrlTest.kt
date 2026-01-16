package com.wishnewjam.dubstepfm

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class CurrentUrlTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        sharedPreferences = mockk(relaxed = true)
        editor = mockk(relaxed = true)

        every { context.getSharedPreferences(CurrentUrl.SP_KEY, Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } returns Unit
    }

    @Test
    fun `init should load default url when no preference saved`() {
        every { sharedPreferences.getString(CurrentUrl.URL_KEY, Links.LINK_128) } returns Links.LINK_128

        val currentUrl = CurrentUrl(context)

        assertEquals(Links.LINK_128, currentUrl.currentUrl)
    }

    @Test
    fun `init should load saved url from shared preferences`() {
        val savedUrl = "http://stream.dubstep.fm/256mp3"
        every { sharedPreferences.getString(CurrentUrl.URL_KEY, Links.LINK_128) } returns savedUrl

        val currentUrl = CurrentUrl(context)

        assertEquals(savedUrl, currentUrl.currentUrl)
    }

    @Test
    fun `init should use default when shared preferences returns null`() {
        every { sharedPreferences.getString(CurrentUrl.URL_KEY, Links.LINK_128) } returns null

        val currentUrl = CurrentUrl(context)

        assertEquals(Links.LINK_128, currentUrl.currentUrl)
    }

    @Test
    fun `updateUrl should update local value`() {
        every { sharedPreferences.getString(CurrentUrl.URL_KEY, Links.LINK_128) } returns Links.LINK_128
        val currentUrl = CurrentUrl(context)

        val newUrl = "http://stream.dubstep.fm/64mp3"
        currentUrl.updateUrl(newUrl)

        assertEquals(newUrl, currentUrl.currentUrl)
    }

    @Test
    fun `updateUrl should persist to shared preferences`() {
        every { sharedPreferences.getString(CurrentUrl.URL_KEY, Links.LINK_128) } returns Links.LINK_128
        val currentUrl = CurrentUrl(context)

        val newUrl = "http://stream.dubstep.fm/64mp3"
        currentUrl.updateUrl(newUrl)

        verify { editor.putString(CurrentUrl.URL_KEY, newUrl) }
        verify { editor.apply() }
    }

    @Test
    fun `updateUrl should work with all bitrate options`() {
        every { sharedPreferences.getString(CurrentUrl.URL_KEY, Links.LINK_128) } returns Links.LINK_128
        val currentUrl = CurrentUrl(context)

        Links.AS_ARRAY.forEach { url ->
            currentUrl.updateUrl(url)
            assertEquals(url, currentUrl.currentUrl)
            verify { editor.putString(CurrentUrl.URL_KEY, url) }
        }
    }

    @Test
    fun `SP_KEY constant should be correct`() {
        assertEquals("dubstepfm", CurrentUrl.SP_KEY)
    }

    @Test
    fun `URL_KEY constant should be correct`() {
        assertEquals("currentUrl", CurrentUrl.URL_KEY)
    }
}
