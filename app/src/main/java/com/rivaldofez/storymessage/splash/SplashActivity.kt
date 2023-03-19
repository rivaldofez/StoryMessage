package com.rivaldofez.storymessage.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.rivaldofez.storymessage.BaseActivity
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_TIME: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Glide.with(this).load(R.drawable.placeholder).into(binding.imgLogo)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, BaseActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME)

    }
}