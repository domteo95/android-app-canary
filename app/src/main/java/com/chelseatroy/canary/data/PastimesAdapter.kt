package com.chelseatroy.canary.data

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.chelseatroy.canary.R
import com.chelseatroy.canary.data.MoodEntrySQLiteDBHelper.Companion.PAST_TIMES_TABLE_NAME
import com.chelseatroy.canary.ui.main.ProfileFragment
import java.security.AccessController.getContext

class PastimesAdapter(context: Context, pastimes_list: ArrayList<Pastimes>) :
RecyclerView.Adapter<PastimesAdapter.PastimesViewHolder>() {

    var context: Context
    var pastimes_list: ArrayList<Pastimes>

    init {
        this.context = context
        this.pastimes_list = pastimes_list
    }

    inner class PastimesViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var pastimesTextView: TextView
        var deleteButton: ImageButton

        init {
            pastimesTextView = itemView.findViewById(R.id.unique_pastime)
            deleteButton = itemView.findViewById<ImageButton>(R.id.delete_pastimes_button)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastimesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val pastimesListItem: View = inflater
                .inflate(R.layout.pastime_list, parent, false)

        return PastimesViewHolder(pastimesListItem)
    }

    override fun getItemCount(): Int {
        return pastimes_list.size
    }

    override fun onBindViewHolder(holder: PastimesViewHolder, position: Int) {
        holder.pastimesTextView.text = pastimes_list.get(position).name_pastime
        holder.deleteButton.setOnClickListener { view ->
            AlertDialog.Builder(holder.deleteButton.getContext()).setTitle("Confirm Deletion")
                    ?.setMessage("Are you sure you want to delete this activity")
                    ?.setPositiveButton("Ok", { dialog, which ->
                        deletePastimesRow(holder.pastimesTextView.text.toString())
                        pastimes_list.remove(pastimes_list[position])
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, itemCount)
                    })
                    ?.setNegativeButton("Cancel", { dialog, which -> })
                    ?.create()
                    ?.show()
        }

    }

    fun deletePastimesRow(pastime: String) {
        Log.i("REACHED", "reached MoodEntrySQLiteDBHelper deletePastimesRow")
        var confirmedDatabase = MoodEntrySQLiteDBHelper(context).getWritableDatabase()
        confirmedDatabase.execSQL("DELETE FROM " + PAST_TIMES_TABLE_NAME + " WHERE " + MoodEntrySQLiteDBHelper.PAST_TIMES_COLUMN_PASTIMES + "=\"" + pastime + "\";")
        notifyDataSetChanged()
    }


}
