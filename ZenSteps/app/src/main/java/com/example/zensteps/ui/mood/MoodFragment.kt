package com.example.zensteps.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zensteps.R
import com.example.zensteps.data.MoodEntry
import com.example.zensteps.repository.MoodRepository
import com.example.zensteps.utils.SharedPreferencesManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MoodFragment : Fragment(), LogMoodDialog.OnMoodLoggedListener {
    
    private lateinit var moodRepository: MoodRepository
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var moodRecyclerView: RecyclerView
    private lateinit var addMoodFab: ExtendedFloatingActionButton
    private lateinit var viewToggleChipGroup: ChipGroup
    private lateinit var listViewChip: Chip
    private lateinit var calendarViewChip: Chip
    private lateinit var chartButton: MaterialButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        moodRepository = MoodRepository.getInstance(requireContext())
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        
        initViews(view)
        setupRecyclerView()
        setClickListeners()
        loadMoodEntries()
    }
    
    private fun initViews(view: View) {
        moodRecyclerView = view.findViewById(R.id.moodRecyclerView)
        addMoodFab = view.findViewById(R.id.addMoodFab)
        viewToggleChipGroup = view.findViewById(R.id.viewToggleChipGroup)
        listViewChip = view.findViewById(R.id.listViewChip)
        calendarViewChip = view.findViewById(R.id.calendarViewChip)
        chartButton = view.findViewById(R.id.chartButton)
    }
    
    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            emptyList(),
            onMoodClick = { moodEntry ->
                // Handle mood entry click (e.g., share)
                shareMoodEntry(moodEntry)
            },
            onMoodDelete = { moodEntry ->
                showDeleteConfirmationDialog(moodEntry)
            }
        )
        
        moodRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        moodRecyclerView.adapter = moodAdapter
    }
    
    private fun setClickListeners() {
        addMoodFab.setOnClickListener {
            showLogMoodDialog()
        }
        
        chartButton.setOnClickListener {
            // Navigate to mood chart view
            findNavController().navigate(R.id.action_moodFragment_to_moodChartFragment)
        }
        
        viewToggleChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.calendarViewChip -> {
                    // Navigate to calendar view using Navigation Component
                    findNavController().navigate(R.id.action_moodFragment_to_moodCalendarFragment)
                }
                R.id.listViewChip -> {
                    // Already in list view, do nothing
                }
            }
        }
    }
    
    private fun loadMoodEntries() {
        val moodEntries = moodRepository.getAllMoodEntries()
        moodAdapter.updateMoodEntries(moodEntries)
    }
    
    private fun showLogMoodDialog() {
        val dialog = LogMoodDialog.newInstance()
        dialog.setOnMoodLoggedListener(this)
        dialog.show(parentFragmentManager, "LogMoodDialog")
    }
    
    private fun shareMoodEntry(moodEntry: MoodEntry) {
        val shareText = "I'm feeling ${moodEntry.mood} today! #ZenSteps"
        val sendIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        
        val shareIntent = android.content.Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
    
    private fun showDeleteConfirmationDialog(moodEntry: MoodEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_mood)
            .setMessage(R.string.delete_mood_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                moodRepository.deleteMoodEntry(moodEntry.id)
                loadMoodEntries()
                Toast.makeText(requireContext(), R.string.mood_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    override fun onMoodLogged(moodEntry: MoodEntry) {
        moodRepository.addMoodEntry(moodEntry)
        Toast.makeText(requireContext(), R.string.mood_logged, Toast.LENGTH_SHORT).show()
        loadMoodEntries()
    }
}