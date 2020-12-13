package com.chelseatroy.canary.data

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MoodEntryPieAnalysisTest {
    lateinit var systemUnderTest: MoodEntryPieAnalysis

    @Before
    fun setUp() {
        systemUnderTest = MoodEntryPieAnalysis()
    }

    var moodEntries:  List<MoodEntry> = listOf(MoodEntry(Mood.NEUTRAL, 1590422400000, "Work again", "EXERCISE"),
        MoodEntry(Mood.ELATED,1570422400000,"", "SLEEP"),
        MoodEntry(Mood.DOWN,1510422400000,"", "EXERCISE"))

    @Test
    fun testGetMoods() {
        var pieSections = systemUnderTest.getMoodsFrom(moodEntries)
        var pie0 = pieSections[0]
        assertEquals(pie0.label.toString(), "NEUTRAL")
        assertEquals(pie0.value.toString(), ((1.toFloat()/3.toFloat())*100).toString())

        var pie1 = pieSections[1]
        assertEquals(pie1.label.toString(), "ELATED")
        assertEquals(pie1.value.toString(), ((1.toFloat()/3.toFloat())*100).toString())

        var pie2 = pieSections[2]
        assertEquals(pie2.label.toString(), "DOWN")
        assertEquals(pie2.value.toString(), ((1.toFloat()/3.toFloat())*100).toString())
    }

    @Test
    fun testGetActivities() {
        var pieSections = systemUnderTest.getActivitiesFrom(moodEntries)
        var pie0 = pieSections[0]
        assertEquals(pie0.label.toString(), "EXERCISE")
        assertEquals(pie0.value.toString(), ((2.toFloat()/3.toFloat())*100).toString())

        var pie1 = pieSections[1]
        assertEquals(pie1.label.toString(), "SLEEP")
        assertEquals(pie1.value.toString(), ((1.toFloat()/3.toFloat())*100).toString())
    }
}