package com.rivaldofez.storymessage.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.rivaldofez.storymessage.BaseActivity
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_TIME: Long = 3000
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Glide.with(this).load(R.drawable.placeholder).into(binding.imgLogo)
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, SPLASH_TIME)

    }


    private fun checkUserSession(){
        lifecycleScope.launchWhenCreated {
            launch {
                splashViewModel.getAuthenticationToken().collect { token ->
                    if (token.isNullOrEmpty()) {
                        Intent(this@SplashActivity, BaseActivity::class.java).also { intent ->
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
}