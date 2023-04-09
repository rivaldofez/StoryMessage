package com.rivaldofez.storymessage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rivaldofez.storymessage.databinding.ActivityBaseBinding
import dagger.hilt.android.AndroidEntryPoint

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

        binding.bottomBar.setItemSelected(R.id.home, true)
        binding.bottomBar.setOnItemSelectedListener { menuItem ->
            when(menuItem){
                R.id.home -> {
                    navController?.navigate(R.id.storyFragment)
                }
                R.id.maps -> {
                    navController?.navigate(R.id.mapsFragment)
                }
                R.id.settings -> {
                    navController?.navigate(R.id.settingsFragment)
                }
            }
        }
    }
}