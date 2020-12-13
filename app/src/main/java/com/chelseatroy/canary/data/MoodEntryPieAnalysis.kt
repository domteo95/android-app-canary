package com.chelseatroy.canary.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.data.PieEntry
import kotlin.math.round

class MoodEntryPieAnalysis() {
    @RequiresApi(Build.VERSION_CODES.N)
    fun getActivitiesFrom(moodEntries: List<MoodEntry>): ArrayList<PieEntry> {
        val pieSections = ArrayList<PieEntry>()
        val map = mutableMapOf<String, Int>("total" to 0)
        for (moodEntry in moodEntries) {
            var pastimeList = moodEntry.recentPastimes
            for (pastime in pastimeList) {
                if (pastime != "") {
                    map[pastime] =  map.getOrDefault(pastime,0).plus(1)
                    map["total"] =  map.get("total")!!.plus(1)
                }
            }
        }
        var total = map.get("total")!!
        map.forEach{ (key,value) ->
            if (key != "total"){
                var percent = getPercentage(value,total)
                pieSections.add(PieEntry(percent, key))
            }
        }
        return pieSections
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getMoodsFrom(moodEntries: List<MoodEntry>): ArrayList<PieEntry> {
        val pieSections = ArrayList<PieEntry>()
        val map = mutableMapOf<String, Int>("total" to 0)
        for (moodEntry in  moodEntries){
            var mood = moodEntry.mood.toString()
            map[mood] = map.getOrDefault(mood,0).plus(1)
            map["total"] =  map.get("total")!!.plus(1)
        }
        var total = map.get("total")!!
        map.forEach{ (key,value) ->
            if (key != "total"){
                var percent = getPercentage(value,total)
                pieSections.add(PieEntry(percent, key))
            }
        }
        return pieSections
    }

    fun getPercentage(value: Int, total:Int): Float {
        return (value.toFloat()/total.toFloat())*100
    }

}