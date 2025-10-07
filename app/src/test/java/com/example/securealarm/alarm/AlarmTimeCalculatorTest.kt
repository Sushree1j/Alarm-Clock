package com.example.securealarm.alarm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmTimeCalculatorTest {

    private val zoneId = ZoneId.systemDefault()

    @Test
    fun `nextTrigger returns null when repeat pattern is absent`() {
        val now = LocalDateTime.of(2025, 1, 1, 7, 0)
        val millis = now.atZone(zoneId).toInstant().toEpochMilli()

        val next = AlarmTimeCalculator.nextTriggerMillis(millis, null)

        assertNull(next)
    }

    @Test
    fun `daily repeat advances by one day`() {
        val now = LocalDateTime.of(2025, 1, 1, 7, 0)
        val millis = now.atZone(zoneId).toInstant().toEpochMilli()

        val next = AlarmTimeCalculator.nextTriggerMillis(millis, "DAILY")

        val expected = now.plusDays(1).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals(expected, next)
    }

    @Test
    fun `weekly repeat selects the next available day`() {
        val wednesday = LocalDateTime.of(2025, 1, 1, 7, 0) // Wednesday
        val millis = wednesday.atZone(zoneId).toInstant().toEpochMilli()

        val next = AlarmTimeCalculator.nextTriggerMillis(millis, "WEEKLY:5,7")

        val expectedFriday = wednesday.plusDays(2).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals(expectedFriday, next)
    }

    @Test
    fun `weekly repeat rolls over to the first configured day`() {
        val sunday = LocalDateTime.of(2025, 1, 5, 7, 0)
        val millis = sunday.atZone(zoneId).toInstant().toEpochMilli()

        val next = AlarmTimeCalculator.nextTriggerMillis(millis, "WEEKLY:3,5")

        val expectedWednesday = sunday.plusDays(3).atZone(zoneId).toInstant().toEpochMilli()
        assertEquals(expectedWednesday, next)
    }
}
