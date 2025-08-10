package com.spygame

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.spygame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPreferences = getSharedPreferences("SpyGamePrefs", MODE_PRIVATE)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.startGameButton.setOnClickListener {
            startNewGame()
        }
        
        binding.settingsButton.setOnClickListener {
            openSettings()
        }
        
        binding.instructionsButton.setOnClickListener {
            openInstructions()
        }

        binding.exitButton.setOnClickListener {
            showExitConfirmationDialog()
        }
    }

    private fun openInstructions() {
        val intent = Intent(this, InstructionsActivity::class.java)
        startActivity(intent)
    }    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit Game")
            .setMessage("Are you sure you want to exit the game?")
            .setPositiveButton("Exit") { _, _ ->
                finishAffinity() // Close entire app
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onBackPressed() {
        showExitConfirmationDialog()
    }
    
    private fun startNewGame() {
        val totalPlayers = sharedPreferences.getInt("total_players", 5)
        val numberOfSpies = sharedPreferences.getInt("number_of_spies", 2)
        val gameDuration = sharedPreferences.getInt("game_duration", 15)
        
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("total_players", totalPlayers)
            putExtra("number_of_spies", numberOfSpies)
            putExtra("game_duration", gameDuration)
        }
        startActivity(intent)
    }
    
    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}
