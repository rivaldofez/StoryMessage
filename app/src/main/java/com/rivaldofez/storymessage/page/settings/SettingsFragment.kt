package com.rivaldofez.storymessage.page.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val themeTitles = requireContext().resources.getStringArray(R.array.themes)

        val arrayAdapter = ArrayAdapter(
            requireContext(), R.layout.item_spinner_theme,
            themeTitles
        )
        binding.spnTheme.adapter = arrayAdapter
        binding.spnTheme.isSelected = false

        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.getThemeSetting().collect { theme ->
                if (!theme.isNullOrEmpty()) {
                    binding.spnTheme.setSelection(theme.toInt())
                }
            }
        }

        binding.spnTheme.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    else ->  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                settingsViewModel.saveThemeSetting(themeId = position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

//        checkUserTheme()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}