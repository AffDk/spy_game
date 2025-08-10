package com.spygame

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.spygame.databinding.ActivityInstructionsBinding

class InstructionsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityInstructionsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstructionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup back button handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        
        setupClickListeners()
        loadInstructions()
    }
    
    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
    
    private fun loadInstructions() {
        val instructions = """
بازی جاسوس یک بازی دورهمی است که توسط کمپانی NightGames ساخته شده است. در این بازی، افراد دور یک میز یا در یک محل دلخواه قرار می‌گیرند. سپس توسط نرم‌افزار بازی جاسوس، یک کلمه مانند "کامپیوتر" به همه نشان داده می‌شود، با این تفاوت که جاسوس این کلمه را نمی‌بیند.

هدف جاسوس در این بازی این است که با استفاده از سوالاتی که از دیگر بازیکنان می‌پرسد، بتواند کلمه را حدس بزند. از سوی دیگر، سایر بازیکنان باید با حفظ کلمه و با پرسیدن سوال‌هایی از یکدیگر، تشخیص دهند که کدام یک از بازیکنان جاسوس است. به عنوان مثال، با توجه به کلمه "کامپیوتر"، می‌توان سوالی مانند "آیا قدیمی است یا جدید؟" پرسید. اگر فرد مورد نظر پاسخ نادرستی دهد، احتمال اینکه جاسوس باشد بیشتر می‌شود.

در این بازی، کلماتی که برای جاسوس باید استفاده شوند، باید مربوط به مکان باشند. این به این معنی است که داستان جاسوسی به آنجا برمی‌گردد که همه افراد قصد دارند در یک مکان خاص جمع شوند، اما جاسوس نمی‌داند کدام مکان است. بنابراین، جاسوس باید با پرسیدن سوال‌ها و دریافت پاسخ‌ها، مکان مورد نظر را کشف کند.
        """.trimIndent()
        
        binding.instructionsText.text = instructions
    }
}
