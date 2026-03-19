package com.example.mad_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.viewpager2.widget.ViewPager2
import com.example.mad_project.adapter.OnboardingAdapter
import com.example.mad_project.databinding.ActivityOnboardingBinding
import com.example.mad_project.model.OnboardingItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf(
            OnboardingItem(R.drawable.onboard1, "Choose a service", "Find the right service for your needs \n" +
                    "easily, with a variety of options \n" +
                    "available at your fingertips."),
            OnboardingItem(R.drawable.onboard2, "Get a quote", "Request price estimates from \n" +
                    "professionals to help you make informed \n" +
                    "decisions with ease."),
            OnboardingItem(R.drawable.onboard3, "Work done", "Sit back and relax while skilled experts\n" +
                    " efficiently take care of your tasks, \n" +
                    "ensuring a job well done.")
        )

        adapter = OnboardingAdapter(items)
        binding.viewpager.adapter = adapter

        // --- YAHAN SE CHANGES HAIN ---

        // 1. ViewPager ka listener jo check karega screen change hui ya nahi
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Agar aakhri screen hai (items.size - 1)
                if (position == items.size - 1) {
                    binding.btnNext.text = "Finish"
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })

        val viewPager = findViewById<ViewPager2>(R.id.viewpager)
        val dots = findViewById<TabLayout>(R.id.dotsIndicator)
        TabLayoutMediator(dots, viewPager) { _, _ -> }.attach()


        // 2. Button ka Logic
        binding.btnNext.setOnClickListener {
            if (binding.viewpager.currentItem < items.size - 1) {
                // Agli screen par jao
                binding.viewpager.currentItem += 1
            } else {
                // Aakhri screen par hain, ab Verification par jao
                goToVerification()
            }
        }

        binding.tvSkip.setOnClickListener {
            goToVerification()
        }

    }

    private fun goToVerification() {
        val intent = Intent(this, securityverification::class.java)
        startActivity(intent)
        finish()
    }
}