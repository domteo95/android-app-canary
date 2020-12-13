package com.chelseatroy.canary.ui.main

import org.junit.Assert.*
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chelseatroy.canary.MainActivity
import com.chelseatroy.canary.R
import com.chelseatroy.canary.data.MoodEntrySQLiteDBHelper
import com.chelseatroy.canary.data.Pastimes
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun checkLabel() {
        val scenario = launchFragment<ProfileFragment>()
        scenario.onFragment { fragment ->
            val profilelabel = fragment.view!!.findViewById<TextView>(R.id.profile_label)
            assertEquals("Activities to choose from...", profilelabel.text)
        }
    }

    @Test
    fun checkDefaultPastimes() {
        val scenario = launchFragment<ProfileFragment>()
        scenario.onFragment { fragment ->
            val listPastimes = fragment.pastimesList
            val namePastimes: ArrayList<String> = ArrayList()
            listPastimes.forEach { namePastimes.add(it.name_pastime)}
            assertTrue("Check Eating", namePastimes.contains("EATING"))
            assertTrue("Check Sleep", namePastimes.contains("SLEEP"))
            assertTrue("Check Eating", namePastimes.contains("EATING"))
            assertTrue("Check Movie", namePastimes.contains("MOVIE"))
            assertTrue("Check Reading", namePastimes.contains("READING"))
            assertTrue("Check Exercise", namePastimes.contains("EXERCISE"))
            assertTrue("Check Socializing", namePastimes.contains("SOCIALIZING"))
        }
    }

    @Test
    fun checkDeletingPastimes(){
        val scenario = launchFragment<ProfileFragment>()
        scenario.onFragment { fragment ->
            fragment.recyclerViewAdapter.deletePastimesRow("EATING")
            val listPastimes = MoodEntrySQLiteDBHelper(fragment.activity).fetchPastimes()
            val namePastimes: ArrayList<String> = ArrayList()
            listPastimes.forEach { namePastimes.add(it.name_pastime)}
            assertFalse("Check Eating has been deleted", namePastimes.contains("EATING"))
            fragment.submitPastime("EATING")
        }
    }
    @Test
    fun checkAddingPastimes(){
        val scenario = launchFragment<ProfileFragment>()
        scenario.onFragment { fragment ->
            fragment.submitPastime("TEST PASTIME")
            val listPastimes = MoodEntrySQLiteDBHelper(fragment.activity).fetchPastimes()
            val namePastimes: ArrayList<String> = ArrayList()
            listPastimes.forEach { namePastimes.add(it.name_pastime)}
            assertTrue("Check that TEST PASTIME has been added", namePastimes.contains("TEST PASTIME"))
            fragment.recyclerViewAdapter.deletePastimesRow("TEST PASTIME")
        }
    }
}


