package com.example.zensteps.ui.mood

import android.app.Dialog
import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.zensteps.R

class MoodSelectionDialog : DialogFragment() {
    
    private var selectedMood: String = "😊"
    private var listener: ((String) -> Unit)? = null
    
    interface OnMoodSelectedListener {
        fun onMoodSelected(mood: String)
    }
    
    companion object {
        private const val ARG_SELECTED_MOOD = "selected_mood"
        
        fun newInstance(selectedMood: String, listener: (String) -> Unit): MoodSelectionDialog {
            val dialog = MoodSelectionDialog()
            val args = Bundle()
            args.putString(ARG_SELECTED_MOOD, selectedMood)
            dialog.arguments = args
            dialog.listener = listener
            return dialog
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedMood = arguments?.getString(ARG_SELECTED_MOOD) ?: "😊"
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_mood_selection, null)
        
        val gridView = view.findViewById<GridView>(R.id.moodGridView)
        val moodEmojis = arrayOf("😊", "😃", "😐", "😔", "😢", "😡", "😴", "🤩")
        
        val adapter = MoodGridAdapter(requireContext(), moodEmojis.toList(), selectedMood) { mood ->
            listener?.invoke(mood)
            dismiss()
        }
        
        gridView.adapter = adapter
        
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}