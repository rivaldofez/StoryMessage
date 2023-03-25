package com.rivaldofez.storymessage.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rivaldofez.storymessage.BaseActivity
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val SPLASH_TIME: Long = 3000
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireActivity()).load(R.drawable.logo).into(binding.imgLogo)
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, SPLASH_TIME)

    }

    private fun checkUserSession(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            launch {
                splashViewModel.getAuthenticationToken().collect { token ->
                    if (token.isNullOrEmpty()) {
                        Log.d("Hexa", "Call GetAuth gotologin if")

                        val goToLogin = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                        findNavController().navigate(goToLogin)
                    } else {
                        val goToStory = SplashFragmentDirections.actionSplashFragmentToStoryFragment()
                        findNavController().navigate(goToStory)
                    }


                    Log.d("Hexa", "Call GetAuth")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}