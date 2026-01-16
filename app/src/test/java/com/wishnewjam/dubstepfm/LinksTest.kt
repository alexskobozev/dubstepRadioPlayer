package com.wishnewjam.dubstepfm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LinksTest {

    @Test
    fun `PRIVACY_POLICY should have correct URL`() {
        assertEquals(
            "http://sites.google.com/view/dubstep-fm-app-privacy-policy",
            Links.PRIVACY_POLICY
        )
    }

    @Test
    fun `LINK_128 should be the default bitrate`() {
        assertEquals("http://stream.dubstep.fm/128mp3", Links.LINK_128)
    }

    @Test
    fun `LINK_128 should be included in AS_ARRAY`() {
        assertTrue(Links.AS_ARRAY.contains(Links.LINK_128))
    }

    @Test
    fun `AS_ARRAY should contain exactly 4 bitrate options`() {
        assertEquals(4, Links.AS_ARRAY.size)
    }

    @Test
    fun `AS_ARRAY should be ordered from lowest to highest bitrate`() {
        val expectedOrder = listOf(
            "http://stream.dubstep.fm/24mp3",
            "http://stream.dubstep.fm/64mp3",
            "http://stream.dubstep.fm/128mp3",
            "http://stream.dubstep.fm/256mp3"
        )
        assertEquals(expectedOrder, Links.AS_ARRAY)
    }

    @Test
    fun `AS_ARRAY should contain 24kbps link`() {
        assertTrue(Links.AS_ARRAY.any { it.contains("24mp3") })
    }

    @Test
    fun `AS_ARRAY should contain 64kbps link`() {
        assertTrue(Links.AS_ARRAY.any { it.contains("64mp3") })
    }

    @Test
    fun `AS_ARRAY should contain 128kbps link`() {
        assertTrue(Links.AS_ARRAY.any { it.contains("128mp3") })
    }

    @Test
    fun `AS_ARRAY should contain 256kbps link`() {
        assertTrue(Links.AS_ARRAY.any { it.contains("256mp3") })
    }

    @Test
    fun `all links should have correct base URL`() {
        Links.AS_ARRAY.forEach { link ->
            assertTrue(
                "Link $link should start with http://stream.dubstep.fm/",
                link.startsWith("http://stream.dubstep.fm/")
            )
        }
    }

    @Test
    fun `all links should be valid HTTP URLs`() {
        val links = Links.AS_ARRAY + Links.PRIVACY_POLICY
        links.forEach { link ->
            assertTrue(
                "Link $link should start with http://",
                link.startsWith("http://")
            )
        }
    }

    @Test
    fun `LINK_128 index in AS_ARRAY should be 2`() {
        assertEquals(2, Links.AS_ARRAY.indexOf(Links.LINK_128))
    }
}
