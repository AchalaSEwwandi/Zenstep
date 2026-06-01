package com.example.zensteps.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.zensteps.R
import com.example.zensteps.auth.LoginActivity
import com.example.zensteps.utils.SharedPreferencesManager

class SettingsFragment : Fragment() {
    
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var nameEditText: TextView
    private lateinit var emailEditText: TextView
    private lateinit var saveProfileButton: Button
    private lateinit var themeRadioGroup: RadioGroup
    private lateinit var redColorButton: ImageButton
    private lateinit var orangeColorButton: ImageButton
    private lateinit var blueColorButton: ImageButton
    private lateinit var yellowColorButton: ImageButton
    private lateinit var logoutButton: Button
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
        
        initViews(view)
        setClickListeners()
        loadUserData()
    }
    
    private fun initViews(view: View) {
        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        saveProfileButton = view.findViewById(R.id.saveProfileButton)
        themeRadioGroup = view.findViewById(R.id.themeRadioGroup)
        redColorButton = view.findViewById(R.id.redColorButton)
        orangeColorButton = view.findViewById(R.id.orangeColorButton)
        blueColorButton = view.findViewById(R.id.blueColorButton)
        yellowColorButton = view.findViewById(R.id.yellowColorButton)
        logoutButton = view.findViewById(R.id.logoutButton)
    }
    
    private fun setClickListeners() {
        saveProfileButton.setOnClickListener {
            saveProfile()
        }
        
        redColorButton.setOnClickListener {
            sharedPreferencesManager.setTheme("red")
            requireActivity().recreate()
        }
        
        orangeColorButton.setOnClickListener {
            sharedPreferencesManager.setTheme("orange")
            requireActivity().recreate()
        }
        
        blueColorButton.setOnClickListener {
            sharedPreferencesManager.setTheme("blue")
            requireActivity().recreate()
        }
        
        yellowColorButton.setOnClickListener {
            sharedPreferencesManager.setTheme("yellow")
            requireActivity().recreate()
        }
        
        logoutButton.setOnClickListener {
            sharedPreferencesManager.clearUserData()
            sharedPreferencesManager.setIsLoggedIn(false)
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    
    private fun loadUserData() {
        val email = sharedPreferencesManager.getUserEmail()
        emailEditText.text = email
    }
    
    private fun saveProfile() {
        // In a real app, you would save the profile data
        // For this app, we'll just show a toast
    }
}