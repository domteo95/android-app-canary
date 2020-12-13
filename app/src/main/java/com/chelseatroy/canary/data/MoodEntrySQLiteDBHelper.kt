package com.chelseatroy.canary.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class MoodEntrySQLiteDBHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    val context = context

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(createMoodEntriesTableQuery)
        Log.i("REACHED", "reached createMoodEntriesTable")
        saveMood(MoodEntry(Mood.NEUTRAL, 1590422400000, "Work again", "EXERCISE"), sqLiteDatabase)

        sqLiteDatabase.execSQL(createPastTimesTableQuery)
        Log.i("REACHED", "reached createPastimesTable")
        savePastimes("EXERCISE", sqLiteDatabase)
        savePastimes("SOCIALIZING", sqLiteDatabase)
        savePastimes("EATING", sqLiteDatabase)
        savePastimes("READING", sqLiteDatabase)
        savePastimes("MOVIE", sqLiteDatabase)
        savePastimes("SLEEP", sqLiteDatabase)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL(dropMoodEntriesTableQuery)
        sqLiteDatabase.execSQL(dropPastTimesTableQuery)
        onCreate(sqLiteDatabase)
    }

    fun saveMood(moodEntry: MoodEntry) {
        Log.i("REACHED", "reached MoodEntrySQLiteDBHelper saveMood")
        saveMood(moodEntry, null)
    }

    fun saveMood(
            moodEntry: MoodEntry,
            database: SQLiteDatabase?
    ) {
        var confirmedDatabase = database
        if(confirmedDatabase == null) {
            confirmedDatabase = MoodEntrySQLiteDBHelper(context).getWritableDatabase()
        }
        val values = ContentValues()
        Log.i("REACHED", "reached MoodEntrySQLiteDBHelper saveMood2")
        values.put(MOOD_ENTRY_COLUMN_MOOD, moodEntry.mood.toString())
        values.put(MOOD_ENTRY_COLUMN_LOGGED_AT, moodEntry.loggedAt)
        values.put(MOOD_ENTRY_COLUMN_NOTES, moodEntry.notes)
        values.put(MOOD_ENTRY_COLUMN_PASTIMES, MoodEntry.formatForDatabase(moodEntry.recentPastimes))

        val newRowId = confirmedDatabase?.insert(MOOD_ENTRY_TABLE_NAME, null, values)

        if (newRowId == -1.toLong() ) {
            Log.wtf("SQLITE INSERTION FAILED", "We don't know why")
        } else {
            Log.i("MOOD ENTRY SAVED!", "Saved in row ${newRowId}: ${moodEntry.toString()}")
        }
    }



    fun savePastimes(pastime: String) {
        Log.i("REACHED", "reached MoodEntrySQLiteDBHelper saveMood")
        savePastimes(pastime, null)
    }
    fun savePastimes(pastime: String, database: SQLiteDatabase?) {
        Log.i("REACHED", "reached MoodEntrySQLiteDBHelper savePastimes")
        var confirmedDatabase = database
        if(confirmedDatabase == null) {
            confirmedDatabase = MoodEntrySQLiteDBHelper(context).getWritableDatabase()
        }
        val values = ContentValues()
        values.put(PAST_TIMES_COLUMN_PASTIMES, pastime)

        val newRowId = confirmedDatabase?.insert(PAST_TIMES_TABLE_NAME, null, values)

        if (newRowId == -1.toLong() ) {
            Log.wtf("SQLITE INSERTION FAILED", "We don't know why")
        } else {
            Log.i("TYPE OF PASTIME SAVED!", "Saved in row ${newRowId}: ${pastime.toString()}")
        }
    }

    fun listPastTimesEntries(): Cursor {
        Log.i("REACHED", "reached listPastimes")
        val database: SQLiteDatabase = MoodEntrySQLiteDBHelper(context).getReadableDatabase()
        Log.i("REACHED", "reached listPastimes2")

        val cursor: Cursor = database.query(
                MoodEntrySQLiteDBHelper.PAST_TIMES_TABLE_NAME,
                MoodEntrySQLiteDBHelper.allPastimesColumns,
                null,
                null,
                null,
                null,
                MoodEntrySQLiteDBHelper.PAST_TIMES_COLUMN_ID + " DESC"
        )
        Log.i("DATA FETCHED!", "Number of different Past Times returned: " + cursor.getCount())
        return cursor
    }


    fun listMoodEntries(limitToPastWeek: Boolean = false): Cursor {
        val database: SQLiteDatabase = MoodEntrySQLiteDBHelper(context).getReadableDatabase()

        var filterOnThis: String? = null
        var usingTheseValues: Array<String>? = null

        if (limitToPastWeek) {
            val nowInMilliseconds = Calendar.getInstance().timeInMillis.toInt()
            filterOnThis = LOGGED_WITHIN
            usingTheseValues = arrayOf("${nowInMilliseconds - ONE_WEEK_AGO_IN_MILLISECONDS}")
        }

        val cursor: Cursor = database.query(
            MOOD_ENTRY_TABLE_NAME,
            allMoodColumns,
            filterOnThis,
            usingTheseValues,
            null,
            null,
            MOOD_ENTRY_COLUMN_LOGGED_AT + " DESC"
        )
        Log.i("DATA FETCHED!", "Number of mood entries returned: " + cursor.getCount())
        return cursor
    }

    fun create() {
        onCreate(writableDatabase)
    }

    fun clear() {
        writableDatabase.execSQL("DROP TABLE IF EXISTS $MOOD_ENTRY_TABLE_NAME")
    }


    fun fetchPastimes(): ArrayList<Pastimes> {
        Log.i("REACHED", "reached MoodEntrySQLiteDBHelper fetchPastimesData")
        var pastimesList = ArrayList<Pastimes>()
        val cursor = listPastTimesEntries()

        val fromPastimesColumn =
                cursor.getColumnIndex(PAST_TIMES_COLUMN_PASTIMES)

        if (cursor.getCount() == 0) {
            Log.i("NO PASTIMES ENTRIES", "Fetched data and found none.")
        } else {
            Log.i("PASTIMES FETCHED!", "Fetched data and found unique pastimes.")
            while (cursor.moveToNext()) {
                val nextPastime = Pastimes(cursor.getString(fromPastimesColumn))
                pastimesList.add(nextPastime)
            }
        }
        return pastimesList
    }

    //endregion

    //region Porcelain

    fun fetchMoodData(limitToPastWeek: Boolean = false): ArrayList<MoodEntry> {
        var moodEntries = ArrayList<MoodEntry>()
        val cursor = listMoodEntries(limitToPastWeek = limitToPastWeek)

        val fromMoodColumn = cursor.getColumnIndex(MoodEntrySQLiteDBHelper.MOOD_ENTRY_COLUMN_MOOD)
        val fromNotesColumn = cursor.getColumnIndex(MoodEntrySQLiteDBHelper.MOOD_ENTRY_COLUMN_NOTES)
        val fromLoggedAtColumn =
            cursor.getColumnIndex(MOOD_ENTRY_COLUMN_LOGGED_AT)
        val fromPastimesColumn =
            cursor.getColumnIndex(MoodEntrySQLiteDBHelper.MOOD_ENTRY_COLUMN_PASTIMES)

        if (cursor.getCount() == 0) {
            Log.i("NO MOOD ENTRIES", "Fetched data and found none.")
        } else {
            Log.i("MOOD ENTRIES FETCHED!", "Fetched data and found mood entries.")
            while (cursor.moveToNext()) {
                val nextMood = MoodEntry(
                    Mood.valueOf(cursor.getString(fromMoodColumn)),
                    cursor.getLong(fromLoggedAtColumn),
                    cursor.getString(fromNotesColumn),
                    cursor.getString(fromPastimesColumn)
                )
                moodEntries.add(nextMood)
            }
        }
        return moodEntries
    }

    companion object {
        private const val DATABASE_VERSION = 15
        const val DATABASE_NAME = "canary_database"
        const val MOOD_ENTRY_TABLE_NAME = "mood_entry"
        const val MOOD_ENTRY_COLUMN_ID = "_id"
        const val MOOD_ENTRY_COLUMN_MOOD = "mood"
        const val MOOD_ENTRY_COLUMN_LOGGED_AT = "logged_at"
        const val MOOD_ENTRY_COLUMN_NOTES = "notes"
        const val MOOD_ENTRY_COLUMN_PASTIMES = "pastimes"

        val allMoodColumns = arrayOf<String>(
            MOOD_ENTRY_COLUMN_ID,
            MOOD_ENTRY_COLUMN_MOOD,
            MOOD_ENTRY_COLUMN_LOGGED_AT,
            MOOD_ENTRY_COLUMN_PASTIMES,
            MOOD_ENTRY_COLUMN_NOTES
        )

        val createMoodEntriesTableQuery = "CREATE TABLE ${MOOD_ENTRY_TABLE_NAME} " +
                "(${MOOD_ENTRY_COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${MOOD_ENTRY_COLUMN_MOOD} TEXT, " +
                "${MOOD_ENTRY_COLUMN_LOGGED_AT} INTEGER, " +
                "${MOOD_ENTRY_COLUMN_NOTES} TEXT, " +
                "${MOOD_ENTRY_COLUMN_PASTIMES} TEXT);"

        const val ONE_WEEK_AGO_IN_MILLISECONDS = 604800000
        const val LOGGED_WITHIN = "${MOOD_ENTRY_COLUMN_LOGGED_AT} >= ?"


        const val PAST_TIMES_TABLE_NAME = "past_times"
        const val PAST_TIMES_COLUMN_ID = "_id"
        const val PAST_TIMES_COLUMN_PASTIMES = "types_pastimes"

        val allPastimesColumns = arrayOf<String>(
                PAST_TIMES_COLUMN_ID,
                PAST_TIMES_COLUMN_PASTIMES
        )

        val createPastTimesTableQuery = "CREATE TABLE ${PAST_TIMES_TABLE_NAME} " +
                "(${PAST_TIMES_COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${PAST_TIMES_COLUMN_PASTIMES} TEXT);"

        val dropPastTimesTableQuery = "DROP TABLE IF EXISTS $PAST_TIMES_TABLE_NAME"

        val dropMoodEntriesTableQuery = "DROP TABLE IF EXISTS $MOOD_ENTRY_TABLE_NAME"

    }
}
