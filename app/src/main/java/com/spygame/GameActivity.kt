package com.spygame

import android.app.Dialog
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.spygame.databinding.ActivityGameBinding
import com.spygame.databinding.DialogRoleRevealBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.random.Random

class GameActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityGameBinding
    private lateinit var sharedPreferences: SharedPreferences
    
    private var totalPlayers = 5
    private var numberOfSpies = 2
    private var gameDurationMinutes = 15
    private var currentPlayer = 1
    private var spyNumbers = mutableSetOf<Int>()
    private var selectedWord = ""
    private var wordList = mutableListOf<String>()
    private var gameTimer: CountDownTimer? = null
    private var alarmMediaPlayer: MediaPlayer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPreferences = getSharedPreferences("SpyGamePrefs", MODE_PRIVATE)
        
        getGameSettings()
        loadWordList()
        initializeGame()
        setupClickListeners()
    }
    
    private fun getGameSettings() {
        totalPlayers = intent.getIntExtra("total_players", 5)
        numberOfSpies = intent.getIntExtra("number_of_spies", 2)
        gameDurationMinutes = intent.getIntExtra("game_duration", 15)
    }
    
    private fun loadWordList() {
        wordList.clear()
        
        // First load from raw resource file
        try {
            val inputStream = resources.openRawResource(R.raw.word_list)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val csvContent = reader.readText()
            reader.close()
            
            wordList.addAll(csvContent.split(",").map { it.trim() }.filter { it.isNotEmpty() })
        } catch (e: IOException) {
            e.printStackTrace()
            // Fallback words
            wordList.addAll(listOf("spy", "agent", "secret", "mission", "code", "stealth"))
        }
        
        // Then add user-added words from shared preferences
        val savedWordList = sharedPreferences.getString("word_list", null)
        if (savedWordList != null) {
            val userWords = savedWordList.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            wordList.addAll(userWords)
        }
    }
    
    private fun initializeGame() {
        // Set game info
        binding.gameInfoText.text = getString(R.string.players_info, totalPlayers, numberOfSpies)
        
        // Randomly select spies
        spyNumbers.clear()
        val playerNumbers = (1..totalPlayers).toList().shuffled()
        for (i in 0 until numberOfSpies) {
            spyNumbers.add(playerNumbers[i])
        }
        
        // Randomly select a word
        selectedWord = wordList.random()
        
        // Show first player button
        showPlayerButton(currentPlayer)
    }
    
    private fun setupClickListeners() {
        binding.startGameButton.setOnClickListener {
            startGameTimer()
        }
        
        binding.newGameButton.setOnClickListener {
            finish() // Return to main activity
        }
        
        binding.closeAppButton.setOnClickListener {
            showExitConfirmationDialog()
        }
        
        binding.abortGameButton.setOnClickListener {
            showAbortGameDialog()
        }
    }
    
    private fun showAbortGameDialog() {
        AlertDialog.Builder(this)
            .setTitle("Abort Game")
            .setMessage("Are you sure you want to abort the current game?")
            .setPositiveButton("Abort") { _, _ ->
                gameTimer?.cancel()
                alarmMediaPlayer?.apply {
                    if (isPlaying) {
                        stop()
                    }
                    release()
                }
                alarmMediaPlayer = null
                finish()
            }
            .setNegativeButton("Continue", null)
            .show()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit Game")
            .setMessage("Are you sure you want to exit the game? Your current game will be lost.")
            .setPositiveButton("Exit") { _, _ ->
                finishAffinity() // Close entire app
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onBackPressed() {
        if (binding.gameOverLayout.visibility == View.VISIBLE) {
            // If game is over, allow normal back navigation
            finish()
        } else {
            // During game, show confirmation dialog
            AlertDialog.Builder(this)
                .setTitle("Leave Game")
                .setMessage("Are you sure you want to leave the current game?")
                .setPositiveButton("Leave") { _, _ ->
                    finish()
                }
                .setNegativeButton("Stay", null)
                .show()
        }
    }
    
    private fun showPlayerButton(playerNumber: Int) {
        binding.playersContainer.removeAllViews()
        
        val button = Button(this).apply {
            text = getString(R.string.player_button, playerNumber)
            setTextAppearance(R.style.PlayerButton)
            setBackgroundColor(getColor(R.color.button_enabled))
            setOnClickListener {
                showRoleRevealDialog(playerNumber)
            }
        }
        
        binding.playersContainer.addView(button)
        
        // Scroll to show the button
        binding.playersScrollView.post {
            binding.playersScrollView.smoothScrollTo(0, button.bottom)
        }
    }
    
    private fun showRoleRevealDialog(playerNumber: Int) {
        val dialogBinding = DialogRoleRevealBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        
        val isSpy = spyNumbers.contains(playerNumber)
        val message = if (isSpy) {
            getString(R.string.you_are_spy)
        } else {
            getString(R.string.selected_word, selectedWord)
        }
        
        dialogBinding.messageText.text = message
        dialogBinding.messageText.setTextColor(
            getColor(if (isSpy) R.color.spy_red else R.color.word_blue)
        )
        
        // Auto-close timer
        var countdown = 5
        val handler = Handler(Looper.getMainLooper())
        val timerRunnable = object : Runnable {
            override fun run() {
                if (countdown > 0) {
                    dialogBinding.autoCloseText.text = getString(R.string.auto_close_timer, countdown)
                    countdown--
                    handler.postDelayed(this, 1000)
                } else {
                    dialog.dismiss()
                    onPlayerRevealed()
                }
            }
        }
        handler.post(timerRunnable)
        
        dialogBinding.closeButton.setOnClickListener {
            handler.removeCallbacks(timerRunnable)
            dialog.dismiss()
            onPlayerRevealed()
        }
        
        dialog.show()
    }
    
    private fun onPlayerRevealed() {
        currentPlayer++
        
        if (currentPlayer <= totalPlayers) {
            // Show next player button
            showPlayerButton(currentPlayer)
        } else {
            // All players have seen their roles, show start game button
            binding.playersContainer.removeAllViews()
            binding.startGameButton.visibility = View.VISIBLE
            
            // Focus on the start button
            binding.startGameButton.requestFocus()
        }
    }
    
    private fun startGameTimer() {
        binding.startGameButton.visibility = View.GONE
        binding.timerText.visibility = View.VISIBLE
        binding.gameIconImageView.visibility = View.VISIBLE
        binding.abortGameButton.visibility = View.VISIBLE
        
        val totalTimeMillis = gameDurationMinutes * 60 * 1000L
        
        gameTimer = object : CountDownTimer(totalTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.timerText.text = getString(R.string.time_remaining, String.format("%02d:%02d", minutes, seconds))
                
                // Change color when less than 1 minute remaining
                if (millisUntilFinished < 60000) {
                    binding.timerText.setTextColor(getColor(R.color.timer_warning))
                }
            }
            
            override fun onFinish() {
                onGameFinished()
            }
        }
        gameTimer?.start()
    }
    
    private fun onGameFinished() {
        binding.timerText.visibility = View.GONE
        binding.gameIconImageView.visibility = View.GONE
        binding.abortGameButton.visibility = View.GONE
        
        // Trigger alarm and red flashing
        triggerAlarm()
        
        // Show game over screen after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            binding.gameOverLayout.visibility = View.VISIBLE
        }, 5000)
    }
    
    private fun triggerAlarm() {
        // Play alarm sound
        try {
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            
            alarmMediaPlayer = MediaPlayer().apply {
                setDataSource(this@GameActivity, alarmUri)
                isLooping = true
                prepare()
                start()
            }
            
            // Stop alarm after 5 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                alarmMediaPlayer?.apply {
                    if (isPlaying) {
                        stop()
                    }
                    release()
                }
                alarmMediaPlayer = null
            }, 5000)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Vibrate for 5 seconds
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(5000)
        }
        
        // Flash red screen for 5 seconds
        val handler = Handler(Looper.getMainLooper())
        var flashCount = 0
        
        val flashRunnable = object : Runnable {
            override fun run() {
                if (flashCount < 10) { // Flash 10 times over 5 seconds
                    binding.backgroundOverlay.setBackgroundColor(
                        if (flashCount % 2 == 0) getColor(R.color.alarm_red) 
                        else getColor(R.color.background_overlay)
                    )
                    flashCount++
                    handler.postDelayed(this, 500)
                } else {
                    // Reset to original background
                    binding.backgroundOverlay.setBackgroundColor(getColor(R.color.background_overlay))
                }
            }
        }
        handler.post(flashRunnable)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        gameTimer?.cancel()
        alarmMediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        alarmMediaPlayer = null
    }
}
