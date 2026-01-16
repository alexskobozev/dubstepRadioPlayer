package com.wishnewjam.dubstepfm

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for MainService constants and configurations.
 * Service lifecycle tests are in MainServiceInstrumentedTest.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class MainServiceTest {

    @Test
    fun `SP_KEY_BITRATE should be correct`() {
        assertEquals("link", MainService.SP_KEY_BITRATE)
    }

    @Test
    fun `MAX_ERROR_ATTEMPTS should be 10`() {
        assertEquals(10, MainService.MAX_ERROR_ATTEMPTS)
    }

    @Test
    fun `NOTIFICATION_STATUS_STOP should be 0`() {
        assertEquals(0, MainService.NOTIFICATION_STATUS_STOP)
    }

    @Test
    fun `NOTIFICATION_STATUS_PLAY should be 1`() {
        assertEquals(1, MainService.NOTIFICATION_STATUS_PLAY)
    }

    @Test
    fun `NOTIFICATION_STATUS_CONNECTING should be 2`() {
        assertEquals(2, MainService.NOTIFICATION_STATUS_CONNECTING)
    }

    @Test
    fun `NOTIFICATION_STATUS_LOADING should be 3`() {
        assertEquals(3, MainService.NOTIFICATION_STATUS_LOADING)
    }

    @Test
    fun `NOTIFICATION_STATUS_ERROR should be 4`() {
        assertEquals(4, MainService.NOTIFICATION_STATUS_ERROR)
    }

    @Test
    fun `notification status constants should be unique`() {
        val statuses = listOf(
            MainService.NOTIFICATION_STATUS_STOP,
            MainService.NOTIFICATION_STATUS_PLAY,
            MainService.NOTIFICATION_STATUS_CONNECTING,
            MainService.NOTIFICATION_STATUS_LOADING,
            MainService.NOTIFICATION_STATUS_ERROR
        )

        assertEquals(statuses.size, statuses.toSet().size)
    }

    @Test
    fun `notification status constants should not be negative`() {
        val statuses = listOf(
            MainService.NOTIFICATION_STATUS_STOP,
            MainService.NOTIFICATION_STATUS_PLAY,
            MainService.NOTIFICATION_STATUS_CONNECTING,
            MainService.NOTIFICATION_STATUS_LOADING,
            MainService.NOTIFICATION_STATUS_ERROR
        )

        statuses.forEach { status ->
            assert(status >= 0) { "Status $status should not be negative" }
        }
    }

    @Test
    fun `MAX_ERROR_ATTEMPTS should be reasonable`() {
        // Should be between 1 and 20 for good retry behavior
        assert(MainService.MAX_ERROR_ATTEMPTS in 1..20) {
            "MAX_ERROR_ATTEMPTS should be between 1 and 20"
        }
    }
}
