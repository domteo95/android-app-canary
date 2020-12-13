package com.chelseatroy.canary.ui.main

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chelseatroy.canary.R
import com.chelseatroy.canary.data.MoodEntrySQLiteDBHelper
import com.chelseatroy.canary.data.Pastimes
import kotlinx.android.synthetic.main.pastime_list.view.*
import com.chelseatroy.canary.data.PastimesAdapter as PastimesAdapter1


class ProfileFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewAdapter: PastimesAdapter1
    lateinit var databaseHelper: MoodEntrySQLiteDBHelper
    lateinit var pastimesList: ArrayList<Pastimes>
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("REACHED", "reached Profile Fragment onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        Log.i("REACHED", "reached Profile Fragment onCreateView")
        return root
    }

    override fun onResume() {
        super.onResume()

        // Because of the !! non-null assertion, the app is going to crash if it can't find this id.
        // I am OK with this because if that happens, the developer will catch that when they run
        // the app to look at the list (so the likelihood that this would go uncaught is very low).
        recyclerView = view?.findViewById(R.id.pastimes_list)!!
        Log.i("REACHED", "reached Profile Fragment onresume")
        databaseHelper = MoodEntrySQLiteDBHelper(activity)
        pastimesList = ArrayList<Pastimes>()
        recyclerView = view?.findViewById(R.id.pastimes_list)!!
        databaseHelper = MoodEntrySQLiteDBHelper(activity)
        this.pastimesList = databaseHelper.fetchPastimes()

        recyclerViewAdapter = PastimesAdapter1(activity?.applicationContext!!, this.pastimesList)
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext!!)
        swipeRefreshLayout = view?.findViewById(R.id.profile_swipe_refresh_layout)!!
        swipeRefreshLayout.setOnRefreshListener {
            this.pastimesList.clear()
            this.pastimesList.addAll(databaseHelper.fetchPastimes())
            recyclerViewAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }

        val addPastimesText = view?.findViewById<EditText>(R.id.add_activity)
        val addPastimesButton = view?.findViewById<Button>(R.id.add_activity_button)
        val deletePastimesButton = view?.findViewById<Button>(R.id.delete_pastimes_button)

        addPastimesButton?.setOnClickListener { view ->
            val newPastime = addPastimesText?.text.toString()
            submitPastime(newPastime)
            Log.i("REACHED", "reached submitPastime")
            addPastimesText?.setText("")
            refresh()
        }
    }

    fun submitPastime(addPastimesText: String) {
        val databaseHelper = MoodEntrySQLiteDBHelper(activity)
        databaseHelper.savePastimes(addPastimesText )
    }

    fun refresh() {
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.detach(this@ProfileFragment).attach(this@ProfileFragment).commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(sectionNumber: Int): ProfileFragment {
            return ProfileFragment()
        }
    }
}