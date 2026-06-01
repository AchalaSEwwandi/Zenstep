package com.example.zensteps.ui.mood

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zensteps.R
import com.example.zensteps.data.MoodEntry
import com.example.zensteps.repository.MoodRepository
import com.example.zensteps.utils.SharedPreferencesManager
import com.google.android.material.button.MaterialButton
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class MoodChartFragment : Fragment() {
    
    private lateinit var moodRepository: MoodRepository
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var moodDistributionChart: PieChart
    private lateinit var totalMoodsTextView: TextView
    private lateinit var mostCommonMoodTextView: TextView
    private lateinit var currentStreakTextView: TextView
    private lateinit var listViewButton: MaterialButton
    private lateinit var calendarViewButton: MaterialButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_chart, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        moodRepository = MoodRepository.getInstance(requireContext())
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        
        initViews(view)
        setClickListeners()
        loadMoodData()
    }
    
    private fun initViews(view: View) {
        moodDistributionChart = view.findViewById(R.id.moodDistributionChart)

        totalMoodsTextView = view.findViewById(R.id.totalMoodsTextView)
        mostCommonMoodTextView = view.findViewById(R.id.mostCommonMoodTextView)
        currentStreakTextView = view.findViewById(R.id.currentStreakTextView)
        listViewButton = view.findViewById(R.id.listViewButton)
        calendarViewButton = view.findViewById(R.id.calendarViewButton)
    }
    
    private fun setClickListeners() {
        listViewButton.setOnClickListener {
            // Navigate to list view
            findNavController().navigate(R.id.action_moodChartFragment_to_moodFragment)
        }
        
        calendarViewButton.setOnClickListener {
            // Navigate to calendar view
            findNavController().navigate(R.id.action_moodChartFragment_to_moodCalendarFragment)
        }
    }
    
    private fun loadMoodData() {
        val moodEntries = moodRepository.getAllMoodEntries()
        
        // Update statistics
        totalMoodsTextView.text = moodEntries.size.toString()
        
        if (moodEntries.isNotEmpty()) {
            // Calculate most common mood
            val moodCountMap = mutableMapOf<String, Int>()
            for (entry in moodEntries) {
                moodCountMap[entry.mood] = moodCountMap.getOrDefault(entry.mood, 0) + 1
            }
            
            val mostCommonMood = moodCountMap.maxByOrNull { it.value }?.key ?: "😊"
            mostCommonMoodTextView.text = mostCommonMood
            
            // Calculate current streak
            val currentStreak = calculateCurrentStreak(moodEntries)
            currentStreakTextView.text = currentStreak.toString()
            
            // Setup mood distribution chart
            setupMoodDistributionChart(moodCountMap)
        } else {
            mostCommonMoodTextView.text = "😊"
            currentStreakTextView.text = "0"
        }
    }
    
    private fun calculateCurrentStreak(moodEntries: List<MoodEntry>): Int {
        if (moodEntries.isEmpty()) return 0
        
        val sortedEntries = moodEntries.sortedByDescending { it.timestamp }
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        
        var streak = 0
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        for (entry in sortedEntries) {
            val entryDate = Calendar.getInstance()
            entryDate.timeInMillis = entry.timestamp
            entryDate.set(Calendar.HOUR_OF_DAY, 0)
            entryDate.set(Calendar.MINUTE, 0)
            entryDate.set(Calendar.SECOND, 0)
            entryDate.set(Calendar.MILLISECOND, 0)
            
            if (entryDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                entryDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                streak++
                today.add(Calendar.DAY_OF_YEAR, -1)
            } else if (entryDate.before(today)) {
                break
            }
        }
        
        return streak
    }
    
    private fun setupMoodDistributionChart(moodCountMap: Map<String, Int>) {
        moodDistributionChart.description.isEnabled = false
        moodDistributionChart.legend.isEnabled = true
        moodDistributionChart.holeRadius = 40f
        moodDistributionChart.transparentCircleRadius = 45f
        
        val entries = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()
        
        // Define colors for different moods
        val moodColors = mapOf(
            "😊" to Color.parseColor("#4CAF50"),
            "😃" to Color.parseColor("#8BC34A"),
            "😐" to Color.parseColor("#FFC107"),
            "😔" to Color.parseColor("#FF9800"),
            "😢" to Color.parseColor("#F44336"),
            "😡" to Color.parseColor("#E91E63"),
            "😴" to Color.parseColor("#9C27B0"),
            "🤩" to Color.parseColor("#3F51B5")
        )
        
        for ((mood, count) in moodCountMap) {
            entries.add(PieEntry(count.toFloat(), mood))
            colors.add(moodColors[mood] ?: Color.parseColor("#9E9E9E"))
        }
        
        if (entries.isNotEmpty()) {
            val dataSet = PieDataSet(entries, "")
            dataSet.colors = colors
            dataSet.sliceSpace = 3f
            dataSet.valueTextSize = 12f
            
            val data = PieData(dataSet)
            data.setValueTextSize(12f)
            data.setValueTextColor(Color.WHITE)
            
            moodDistributionChart.data = data
            moodDistributionChart.invalidate()
        }
    }
}