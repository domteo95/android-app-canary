package com.chelseatroy.canary.data

import android.os.Build
import com.chelseatroy.canary.MainActivity
import com.chelseatroy.canary.R
import com.chelseatroy.canary.ui.main.HistoryFragment
import com.chelseatroy.canary.ui.main.ProfileFragment
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.apache.tools.ant.Main
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.KITKAT])

class MoodEntrySQLiteDBHelperTest {
    @Test
    fun creationQuery_assemblesMoodEntryTable() {
        val expectedQuery = "CREATE TABLE mood_entry (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mood TEXT, " +
                "logged_at INTEGER, " +
                "notes TEXT, " +
                "pastimes TEXT);"
        assertEquals(expectedQuery, MoodEntrySQLiteDBHelper.createMoodEntriesTableQuery)
    }

    @Test
    fun deletionQuery_removesMoodEntryTable() {
        val expectedQuery = "DROP TABLE IF EXISTS mood_entry"
        assertEquals(expectedQuery, MoodEntrySQLiteDBHelper.dropMoodEntriesTableQuery)
    }

    @Test
    fun creationQuery_assemblesPastimesTable() {
        val expectedQuery = "CREATE TABLE past_times ("  +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "types_pastimes TEXT);"
        assertEquals(expectedQuery, MoodEntrySQLiteDBHelper.createPastTimesTableQuery)
    }

    @Test
    fun deletionQuery_removesPastimesTable() {
        val expectedQuery = "DROP TABLE IF EXISTS past_times"
        assertEquals(expectedQuery, MoodEntrySQLiteDBHelper.dropPastTimesTableQuery)
    }

}