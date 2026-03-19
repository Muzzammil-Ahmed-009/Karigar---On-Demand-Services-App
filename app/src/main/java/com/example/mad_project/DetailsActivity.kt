package com.example.mad_project

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mad_project.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        // Style "Pro Tip:" bold + blue
        val proTipFull = "Pro Tip: The more details you add, the better your service will be. Consider adding technical specs, deadlines, or specific style preferences."
        val spannable = SpannableString(proTipFull)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, 8, 0)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#2563EB")), 0, 8, 0)
        binding.tvProTip.text = spannable

        // Character count live update
        binding.etOrderDetails.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val len = s?.length ?: 0
                binding.tvCharCount.text = "Character count: $len"
            }
        })

        binding.btnSave.setOnClickListener {
            val detailsText = binding.etOrderDetails.text.toString().trim()

            if (detailsText.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("order_details", detailsText)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please enter some details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
