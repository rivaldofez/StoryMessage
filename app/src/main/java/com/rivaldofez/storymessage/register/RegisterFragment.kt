package com.rivaldofez.storymessage.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.databinding.FragmentRegisterBinding
import com.rivaldofez.storymessage.extension.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var registerJob: Job = Job()
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
    }

    private fun setActions(){
        binding.apply {
            btnRegister.setOnClickListener {
                doUserRegister()
            }

            tvLogin.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun doUserRegister() {
        showLoading(isLoading = true)
        var isAllFieldValid = true

        if (binding.edtEmail.text.isNullOrBlank() || !binding.edtEmail.error.isNullOrEmpty())
            isAllFieldValid = false

        if (binding.edtFullname.text.isNullOrBlank() || !binding.edtFullname.error.isNullOrEmpty())
            isAllFieldValid = false

        if (binding.edtPassword.text.isNullOrBlank() || !binding.edtPassword.error.isNullOrEmpty())
            isAllFieldValid = false

        if (isAllFieldValid) {
            val email = binding.edtEmail.text.toString().trim()
            val name = binding.edtFullname.text.toString()
            val password = binding.edtPassword.text.toString()

            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                if (registerJob.isActive) registerJob.cancel()

                registerJob = launch {
                    registerViewModel.registerUser(name = name, email = email, password = password)
                        .collect { result ->
                            result.onSuccess {
                                showLoading(isLoading = false)
                                Snackbar.make(
                                    binding.root,
                                    getString(R.string.success_register),
                                    Snackbar.LENGTH_SHORT
                                ).show()

                                val goToLogin =
                                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                                findNavController().navigate(goToLogin)
                            }

                            result.onFailure {
                                showLoading(isLoading = false)
                                Snackbar.make(
                                    binding.root,
                                    getString(R.string.error_while_register),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        } else {
            showLoading(isLoading = false)
            Snackbar.make(
                binding.root,
                getString(R.string.error_field_not_valid),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.apply {
            edtEmail.isEnabled = !isLoading
            edtPassword.isEnabled = !isLoading
            edtFullname.isEnabled = !isLoading

            layoutLoading.root.animateVisibility(isLoading)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}