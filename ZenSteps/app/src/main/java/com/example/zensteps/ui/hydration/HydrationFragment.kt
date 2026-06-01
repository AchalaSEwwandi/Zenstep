package com.example.zensteps.ui.hydration

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zensteps.R
import com.example.zensteps.data.HydrationRecord
import com.example.zensteps.receivers.HydrationAlarmReceiver
import com.example.zensteps.repository.HydrationRepository
import com.example.zensteps.utils.SharedPreferencesManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButton
import java.util.*

class HydrationFragment : Fragment(), 
    CustomIntakeDialog.OnIntakeAddedListener, 
    SimpleAlarmSettingsDialog.OnAlarmSettingsSavedListener,
    HydrationGoalDialog.OnGoalSetListener {
    
    private lateinit var hydrationRepository: HydrationRepository
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var hydrationHistoryAdapter: HydrationHistoryAdapter
    private lateinit var hydrationPieChart: PieChart
    private lateinit var hydrationProgressTextView: TextView
    private lateinit var drinkNowButton: Button
    private lateinit var snoozeButton: Button
    private lateinit var settingsButton: MaterialButton
    private lateinit var hydrationHistoryRecyclerView: RecyclerView
    private lateinit var historyEmptyState: View
    
    // Hydration goal in ml
    private var dailyHydrationGoal = 3000
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        hydrationRepository = HydrationRepository.getInstance(requireContext())
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        
        // Load the saved hydration goal
        dailyHydrationGoal = sharedPreferencesManager.getHydrationGoal()
        
        initViews(view)
        setupRecyclerView()
        setClickListeners()
        loadHydrationData()
        setupChart()
    }
    
    private fun initViews(view: View) {
        hydrationPieChart = view.findViewById(R.id.hydrationPieChart)
        hydrationProgressTextView = view.findViewById(R.id.hydrationProgressTextView)
        drinkNowButton = view.findViewById(R.id.drinkNowButton)
        snoozeButton = view.findViewById(R.id.snoozeButton)
        settingsButton = view.findViewById(R.id.settingsButton)
        hydrationHistoryRecyclerView = view.findViewById(R.id.hydrationHistoryRecyclerView)
        historyEmptyState = view.findViewById(R.id.historyEmptyState)
    }
    
    private fun setupRecyclerView() {
        hydrationHistoryAdapter = HydrationHistoryAdapter(
            emptyList(),
            onDeleteClick = { record ->
                deleteHydrationRecord(record)
            }
        )
        hydrationHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        hydrationHistoryRecyclerView.adapter = hydrationHistoryAdapter
    }
    
    private fun setClickListeners() {
        drinkNowButton.setOnClickListener {
            showCustomIntakeDialog()
        }
        
        snoozeButton.setOnClickListener {
            showHydrationGoalDialog()
        }
        
        settingsButton.setOnClickListener {
            showAlarmSettingsDialog()
        }
    }
    
    private fun loadHydrationData() {
        val records = hydrationRepository.getHydrationRecordsForToday()
        hydrationHistoryAdapter.updateHydrationRecords(records)
        
        // Show/hide empty state
        if (records.isEmpty()) {
            historyEmptyState.visibility = View.VISIBLE
            hydrationHistoryRecyclerView.visibility = View.GONE
        } else {
            historyEmptyState.visibility = View.GONE
            hydrationHistoryRecyclerView.visibility = View.VISIBLE
        }
        
        updateProgressText()
        updateChart()
        
        // Check if goal is reached
        checkHydrationGoalReached()
    }
    
    private fun checkHydrationGoalReached() {
        val consumed = hydrationRepository.getTotalHydrationForToday()
        if (consumed >= dailyHydrationGoal) {
            // Show goal reached notification
            showGoalReachedNotification()
        }
    }
    
    private fun showGoalReachedNotification() {
        val intent = Intent(requireContext(), HydrationAlarmReceiver::class.java).apply {
            action = "com.example.zensteps.HYDRATION_ALARM"
        }
        requireContext().sendBroadcast(intent)
    }
    
    private fun showCustomIntakeDialog() {
        val dialog = CustomIntakeDialog.newInstance()
        dialog.setOnIntakeAddedListener(this)
        dialog.show(parentFragmentManager, "CustomIntakeDialog")
    }
    
    private fun showHydrationGoalDialog() {
        val dialog = HydrationGoalDialog.newInstance(dailyHydrationGoal)
        dialog.setOnGoalSetListener(this)
        dialog.show(parentFragmentManager, "HydrationGoalDialog")
    }
    
    private fun showAlarmSettingsDialog() {
        // Get current alarm settings from SharedPreferences
        val isAlarmEnabled = sharedPreferencesManager.getHydrationAlarmEnabled()
        val hour = sharedPreferencesManager.getHydrationAlarmHour()
        val minute = sharedPreferencesManager.getHydrationAlarmMinute()
        val interval = sharedPreferencesManager.getHydrationAlarmInterval()
        
        val dialog = SimpleAlarmSettingsDialog.newInstance(
            isAlarmEnabled, hour, minute, interval
        )
        dialog.setOnAlarmSettingsSavedListener(this)
        dialog.show(parentFragmentManager, "SimpleAlarmSettingsDialog")
    }
    
    private fun addHydrationRecord(amount: Int) {
        val record = HydrationRecord(amount = amount)
        hydrationRepository.addHydrationRecord(record)
        Toast.makeText(requireContext(), R.string.water_consumed, Toast.LENGTH_SHORT).show()
        loadHydrationData()
    }
    
    private fun deleteHydrationRecord(record: HydrationRecord) {
        hydrationRepository.deleteHydrationRecord(record.id)
        Toast.makeText(requireContext(), "Hydration record deleted", Toast.LENGTH_SHORT).show()
        loadHydrationData()
    }
    
    private fun updateProgressText() {
        val consumed = hydrationRepository.getTotalHydrationForToday()
        hydrationProgressTextView.text = "${consumed}ml of ${dailyHydrationGoal}ml"
    }
    
    private fun setupChart() {
        hydrationPieChart.description.isEnabled = false
        hydrationPieChart.legend.isEnabled = false
        hydrationPieChart.holeRadius = 70f
        hydrationPieChart.transparentCircleRadius = 75f
    }
    
    private fun updateChart() {
        val consumed = hydrationRepository.getTotalHydrationForToday()
        val remaining = (dailyHydrationGoal - consumed).coerceAtLeast(0)
        
        val entries = listOf(
            PieEntry(consumed.toFloat(), "Consumed"),
            PieEntry(remaining.toFloat(), "Remaining")
        )
        
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            requireContext().getColor(R.color.success_green),
            requireContext().getColor(R.color.medium_gray)
        )
        dataSet.sliceSpace = 3f
        
        val data = PieData(dataSet)
        data.setValueTextSize(12f)
        data.setValueTextColor(requireContext().getColor(R.color.white))
        
        hydrationPieChart.data = data
        hydrationPieChart.invalidate() // refresh
    }
    
    override fun onIntakeAdded(amount: Int) {
        addHydrationRecord(amount)
    }
    
    override fun onGoalSet(goal: Int) {
        dailyHydrationGoal = goal
        sharedPreferencesManager.setHydrationGoal(goal)
        loadHydrationData() // This will update progress text and chart as well
        Toast.makeText(requireContext(), "Hydration goal updated to ${goal}ml", Toast.LENGTH_SHORT).show()
    }
    
    override fun onAlarmSettingsSaved(
        isEnabled: Boolean,
        hour: Int,
        minute: Int,
        interval: Int
    ) {
        // Save settings to SharedPreferences
        sharedPreferencesManager.setHydrationAlarmEnabled(isEnabled)
        sharedPreferencesManager.setHydrationAlarmHour(hour)
        sharedPreferencesManager.setHydrationAlarmMinute(minute)
        sharedPreferencesManager.setHydrationAlarmInterval(interval)
        
        // Set up or cancel alarms based on settings
        if (isEnabled) {
            scheduleHydrationAlarms()
            Toast.makeText(requireContext(), "Hydration reminders enabled", Toast.LENGTH_SHORT).show()
        } else {
            cancelHydrationAlarms()
            Toast.makeText(requireContext(), "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun scheduleHydrationAlarms() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Check for alarm scheduling permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Show dialog to request permission
                Toast.makeText(
                    requireContext(),
                    "Please grant permission to schedule exact alarms",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                return
            }
        }
        
        // Create intent for the alarm receiver with custom action
        val intent = Intent(requireContext(), HydrationAlarmReceiver::class.java).apply {
            action = "com.example.zensteps.HYDRATION_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Get alarm time
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, sharedPreferencesManager.getHydrationAlarmHour())
        calendar.set(Calendar.MINUTE, sharedPreferencesManager.getHydrationAlarmMinute())
        calendar.set(Calendar.SECOND, 0)
        
        // If the alarm time is in the past, set it for the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        // Set repeating alarm
        val intervalMillis = sharedPreferencesManager.getHydrationAlarmInterval() * 60 * 1000L
        
        try {
            // Use setAlarmClock for more reliable alarms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
                alarmManager.setAlarmClock(alarmInfo, pendingIntent)
            } else {
                // Use setInexactRepeating for better battery optimization on older devices
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    intervalMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Toast.makeText(
                requireContext(),
                "Failed to schedule alarm. Please check permissions.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun cancelHydrationAlarms() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Create intent for the alarm receiver with custom action
        val intent = Intent(requireContext(), HydrationAlarmReceiver::class.java).apply {
            action = "com.example.zensteps.HYDRATION_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
}