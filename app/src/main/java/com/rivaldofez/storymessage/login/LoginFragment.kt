package com.rivaldofez.storymessage.login

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
import com.rivaldofez.storymessage.databinding.FragmentLoginBinding
import com.rivaldofez.storymessage.extension.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var loginJob: Job = Job()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
    }

    private fun setActions(){
        binding.apply {
            btnLogin.setOnClickListener {
                doUserLogin()
            }
            tvRegister.setOnClickListener {
                val goToRegister = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(goToRegister)
            }
        }
    }

    private fun doUserLogin(){
        showLoading(isLoading = true)
        var isAllFieldValid = true

        if (binding.edtEmail.text.isNullOrBlank() || !binding.edtEmail.error.isNullOrEmpty())
            isAllFieldValid = false

        if (binding.edtPassword.text.isNullOrBlank() || !binding.edtPassword.error.isNullOrEmpty())
            isAllFieldValid = false


        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()

        if (loginJob.isActive) loginJob.cancel()

        if (isAllFieldValid) {
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                loginJob = launch {
                    loginViewModel.userLogin(email = email, password = password).collect { result ->
                        result.onSuccess { loginResponse ->
                            loginResponse.loginResult?.token?.let { token ->
                                loginViewModel.saveAuthenticationToken(token = token)

                                showLoading(isLoading = false)
                                Snackbar.make(
                                    binding.root,
                                    getString(R.string.success_login),
                                    Snackbar.LENGTH_SHORT
                                ).show()

                                val goToStory = LoginFragmentDirections.actionLoginFragmentToStoryFragment()
                                findNavController().navigate(goToStory)
                            }
                        }

                        result.onFailure {
                            showLoading(isLoading = false)
                            Snackbar.make(
                                binding.root,
                                getString(R.string.error_while_login),
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

            layoutLoading.root.animateVisibility(isLoading)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}