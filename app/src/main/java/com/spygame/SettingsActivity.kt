package com.spygame

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.spygame.databinding.ActivitySettingsBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.math.floor
import kotlin.math.min

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var totalPlayers = 5
    private var numberOfSpies = 2
    private var gameDuration = 15
    private lateinit var wordList: MutableList<String>
    private lateinit var userAddedWords: MutableList<String>
    private var testAlarmMediaPlayer: MediaPlayer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPreferences = getSharedPreferences("SpyGamePrefs", MODE_PRIVATE)
        
        // Setup back button handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveSettings()
                finish()
            }
        })
        
        loadWordList()
        loadSettings()
        setupClickListeners()
        setupEditTextListeners()
        updateUI()
    }
    
    private fun loadWordList() {
        wordList = mutableListOf()
        userAddedWords = mutableListOf()
        
        // First load from raw resource file
        try {
            val inputStream = resources.openRawResource(R.raw.word_list)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val csvContent = reader.readText()
            reader.close()
            
            // Parse CSV and add words to list
            wordList.addAll(csvContent.split(",").map { it.trim() }.filter { it.isNotEmpty() })
        } catch (e: IOException) {
            e.printStackTrace()
            // Add some default words if file loading fails
            wordList.addAll(listOf("spy", "agent", "secret", "mission", "code", "stealth"))
        }
        
        // Then load user-added words from shared preferences
        val savedWordList = sharedPreferences.getString("word_list", null)
        if (savedWordList != null) {
            val userWords = savedWordList.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            userAddedWords.addAll(userWords)
            wordList.addAll(userWords)
        }
    }
    
    private fun loadSettings() {
        totalPlayers = sharedPreferences.getInt("total_players", 5)
        numberOfSpies = sharedPreferences.getInt("number_of_spies", 2)
        gameDuration = sharedPreferences.getInt("game_duration", 15)
    }
    
    private fun setupClickListeners() {
        // Players count controls
        binding.increasePlayersButton.setOnClickListener {
            totalPlayers++
            validateAndUpdateSpies()
            updateUI()
        }
        
        binding.decreasePlayersButton.setOnClickListener {
            if (totalPlayers > 3) {
                totalPlayers--
                validateAndUpdateSpies()
                updateUI()
            } else {
                showToast(getString(R.string.min_players_error))
            }
        }
        
        // Spies count controls
        binding.increaseSpiesButton.setOnClickListener {
            val maxSpies = min(floor(totalPlayers / 2.0).toInt(), totalPlayers - 2)
            if (numberOfSpies < maxSpies) {
                numberOfSpies++
                updateUI()
            } else {
                showToast(getString(R.string.spy_count_error))
            }
        }
        
        binding.decreaseSpiesButton.setOnClickListener {
            if (numberOfSpies > 1) {
                numberOfSpies--
                updateUI()
            } else {
                showToast(getString(R.string.spy_count_error))
            }
        }
        
        // Duration controls
        binding.increaseDurationButton.setOnClickListener {
            if (gameDuration < 60) {
                gameDuration++
                updateUI()
            } else {
                showToast(getString(R.string.max_duration_error))
            }
        }
        
        binding.decreaseDurationButton.setOnClickListener {
            if (gameDuration > 5) {
                gameDuration--
                updateUI()
            } else {
                showToast(getString(R.string.min_duration_error))
            }
        }
        
        // Add word functionality
        binding.addWordButton.setOnClickListener {
            addNewWord()
        }
        
        // Test alarm functionality
        binding.testAlarmButton.setOnClickListener {
            showTestAlarmDialog()
        }
        
        // Save settings
        binding.saveSettingsButton.setOnClickListener {
            saveSettings()
            finish()
        }
    }

    private fun setupEditTextListeners() {
        // Players count EditText listener
        binding.playersCountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val newValue = s.toString().toIntOrNull()
                if (newValue != null && newValue >= 3 && newValue <= 20) {
                    totalPlayers = newValue
                    validateAndUpdateSpies()
                } else if (newValue != null && (newValue < 3 || newValue > 20)) {
                    showToast("Players must be between 3 and 20")
                    binding.playersCountEditText.setText(totalPlayers.toString())
                    binding.playersCountEditText.setSelection(binding.playersCountEditText.text.length)
                }
            }
        })

        // Spies count EditText listener
        binding.spiesCountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val newValue = s.toString().toIntOrNull()
                val maxSpies = min(floor(totalPlayers / 2.0).toInt(), totalPlayers - 2)
                if (newValue != null && newValue >= 1 && newValue <= maxSpies) {
                    numberOfSpies = newValue
                } else if (newValue != null && (newValue < 1 || newValue > maxSpies)) {
                    showToast("Spies must be between 1 and $maxSpies")
                    binding.spiesCountEditText.setText(numberOfSpies.toString())
                    binding.spiesCountEditText.setSelection(binding.spiesCountEditText.text.length)
                }
            }
        })

        // Duration EditText listener
        binding.durationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val newValue = s.toString().toIntOrNull()
                if (newValue != null && newValue >= 5 && newValue <= 60) {
                    gameDuration = newValue
                } else if (newValue != null && (newValue < 5 || newValue > 60)) {
                    showToast("Duration must be between 5 and 60 minutes")
                    binding.durationEditText.setText(gameDuration.toString())
                    binding.durationEditText.setSelection(binding.durationEditText.text.length)
                }
            }
        })
    }
    
    private fun validateAndUpdateSpies() {
        val maxSpies = min(floor(totalPlayers / 2.0).toInt(), totalPlayers - 2)
        if (numberOfSpies > maxSpies) {
            numberOfSpies = maxSpies
        }
    }
    
    private fun updateUI() {
        binding.playersCountEditText.setText(totalPlayers.toString())
        binding.spiesCountEditText.setText(numberOfSpies.toString())
        binding.durationEditText.setText(gameDuration.toString())
    }
    
    private fun addNewWord() {
        val newWord = binding.newWordEditText.text.toString().trim()
        
        if (newWord.isEmpty()) {
            showToast(getString(R.string.empty_word_error))
            return
        }
        
        if (wordList.contains(newWord.lowercase())) {
            showToast(getString(R.string.word_exists_warning))
            return
        }
        
        wordList.add(newWord.lowercase())
        userAddedWords.add(newWord.lowercase())
        saveWordList()
        binding.newWordEditText.text.clear()
        showToast(getString(R.string.word_added_success))
    }
    
    private fun saveWordList() {
        // Save only the user-added words to shared preferences
        val userWordListString = userAddedWords.joinToString(",")
        sharedPreferences.edit().putString("word_list", userWordListString).apply()
    }
    
    private fun showTestAlarmDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.test_alarm_title))
            .setMessage(getString(R.string.test_alarm_message))
            .setPositiveButton(getString(R.string.start_test)) { _, _ ->
                testAlarm()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun testAlarm() {
        // Play alarm sound
        try {
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            
            testAlarmMediaPlayer = MediaPlayer().apply {
                setDataSource(this@SettingsActivity, alarmUri)
                isLooping = true
                prepare()
                start()
            }
            
            // Stop alarm after 5 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                testAlarmMediaPlayer?.apply {
                    if (isPlaying) {
                        stop()
                    }
                    release()
                }
                testAlarmMediaPlayer = null
            }, 5000)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Trigger alarm with red flashing screen
        val handler = Handler(Looper.getMainLooper())
        var flashCount = 0
        
        // Vibrate
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
        
        // Flash red screen
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
                    // Reset to original
                    binding.backgroundOverlay.setBackgroundColor(getColor(R.color.background_overlay))
                }
            }
        }
        handler.post(flashRunnable)
    }
    
    private fun saveSettings() {
        sharedPreferences.edit().apply {
            putInt("total_players", totalPlayers)
            putInt("number_of_spies", numberOfSpies)
            putInt("game_duration", gameDuration)
            apply()
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        testAlarmMediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        testAlarmMediaPlayer = null
    }
}
