package com.rivaldofez.storymessage.page.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rivaldofez.storymessage.databinding.FragmentSplashBinding
import com.rivaldofez.storymessage.util.wrapEspressoIdlingResource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUserTheme()
        checkUserSession()
    }

    private fun checkUserTheme(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            launch {
                splashViewModel.getThemeSetting().collect { theme ->
                    if (theme.isNullOrEmpty()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        splashViewModel.saveThemeSetting(themeId = 0)
                    } else {
                        when(theme.toInt()){
                            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            else ->  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }
                    }
                }
            }
        }
    }

    private fun checkUserSession(){
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                launch {
                    splashViewModel.getAuthenticationToken().collect { token ->
                        wrapEspressoIdlingResource {
                        delay(3000)
                        if (token.isNullOrEmpty()) {
                            val goToLogin =
                                SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                            findNavController().navigate(goToLogin)
                        } else {
                            val goToStory =
                                SplashFragmentDirections.actionSplashFragmentToStoryFragment()
                            findNavController().navigate(goToStory)
                        }}
                    }
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}