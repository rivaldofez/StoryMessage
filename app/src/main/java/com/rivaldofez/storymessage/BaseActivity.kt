package com.rivaldofez.storymessage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rivaldofez.storymessage.databinding.ActivityBaseBinding
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar

@AndroidEntryPoint
class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.storyFragment -> binding.bottomBar.visibility = View.VISIBLE
                R.id.mapsFragment -> binding.bottomBar.visibility = View.VISIBLE
                R.id.settingsFragment -> binding.bottomBar.visibility = View.VISIBLE
                else -> binding.bottomBar.visibility = View.GONE
            }
        }

        binding.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newIndex) {
                    0 -> navController?.navigate(R.id.storyFragment)
                    1 -> navController?.navigate(R.id.mapsFragment)
                    else -> navController?.navigate(R.id.settingsFragment)
                }
            }
        })
    }
}