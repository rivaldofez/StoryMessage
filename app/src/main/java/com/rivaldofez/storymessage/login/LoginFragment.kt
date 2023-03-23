package com.rivaldofez.storymessage.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()

        if (loginJob.isActive) loginJob.cancel()

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            loginJob = launch {
                loginViewModel.userLogin(email = email, password = password).collect { result ->
                    result.onSuccess { loginResponse ->
                        loginResponse.loginResult?.token?.let { token ->
                            loginViewModel.saveAuthenticationToken(token = token)
                            val goToStory = LoginFragmentDirections.actionLoginFragmentToStoryFragment()
                            findNavController().navigate(goToStory)
                        }

                        Toast.makeText(
                            requireContext(),
                            "Login Berhasil",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    result.onFailure {
                        Snackbar.make(binding.root, "Login Gagal", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}