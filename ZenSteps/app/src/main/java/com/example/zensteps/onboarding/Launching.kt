package com.example.zensteps.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zensteps.R
import com.example.zensteps.utils.SharedPreferencesManager

class Launching : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_launching)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Add a 3-second delay before navigating to the next screen
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 3000) // 3 seconds delay
    }
    
    private fun navigateToNextScreen() {
        val sharedPreferencesManager = SharedPreferencesManager.getInstance(this)
        
        // Check if onboarding is completed
        if (!sharedPreferencesManager.isOnboardingCompleted()) {
            // Navigate to Onboard1 if onboarding is not completed
            val intent = Intent(this, Onboard1::class.java)
            startActivity(intent)
        } else {
            // Check if user is logged in
            if (sharedPreferencesManager.isLoggedIn()) {
                // Navigate to MainActivity if user is logged in
                val intent = Intent(this, com.example.zensteps.MainActivity::class.java)
                startActivity(intent)
            } else {
                // Navigate to LoginActivity if user is not logged in
                val intent = Intent(this, com.example.zensteps.auth.LoginActivity::class.java)
                startActivity(intent)
            }
        }
        
        // Finish this activity so user can't come back to it
        finish()
    }
}