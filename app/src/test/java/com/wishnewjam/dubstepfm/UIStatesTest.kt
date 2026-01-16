package com.wishnewjam.dubstepfm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UIStatesTest {

    @Test
    fun `STATUS_UNDEFINED should be 0`() {
        assertEquals(0, UIStates.STATUS_UNDEFINED)
    }

    @Test
    fun `STATUS_STOP should be 1`() {
        assertEquals(1, UIStates.STATUS_STOP)
    }

    @Test
    fun `STATUS_PLAY should be 3`() {
        assertEquals(3, UIStates.STATUS_PLAY)
    }

    @Test
    fun `STATUS_LOADING should be 6`() {
        assertEquals(6, UIStates.STATUS_LOADING)
    }

    @Test
    fun `STATUS_ERROR should be 7`() {
        assertEquals(7, UIStates.STATUS_ERROR)
    }

    @Test
    fun `STATUS_WAITING should be 8`() {
        assertEquals(8, UIStates.STATUS_WAITING)
    }

    @Test
    fun `all status values should be unique`() {
        val statuses = listOf(
            UIStates.STATUS_UNDEFINED,
            UIStates.STATUS_STOP,
            UIStates.STATUS_PLAY,
            UIStates.STATUS_LOADING,
            UIStates.STATUS_ERROR,
            UIStates.STATUS_WAITING
        )

        val uniqueStatuses = statuses.toSet()
        assertEquals(statuses.size, uniqueStatuses.size)
    }

    @Test
    fun `status values should not be negative`() {
        val statuses = listOf(
            UIStates.STATUS_UNDEFINED,
            UIStates.STATUS_STOP,
            UIStates.STATUS_PLAY,
            UIStates.STATUS_LOADING,
            UIStates.STATUS_ERROR,
            UIStates.STATUS_WAITING
        )

        statuses.forEach { status ->
            assert(status >= 0) { "Status $status should not be negative" }
        }
    }
}
