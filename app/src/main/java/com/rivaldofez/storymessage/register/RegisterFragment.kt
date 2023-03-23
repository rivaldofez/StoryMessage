package com.rivaldofez.storymessage.register

import android.os.Bundle
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
import com.rivaldofez.storymessage.databinding.FragmentRegisterBinding
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
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRegister.setOnClickListener {
                doUserRegister()
            }
        }
    }

    private fun doUserRegister(){
        val email = binding.edtEmail.text.toString()
        val name = binding.edtFullname.text.toString()
        val password = binding.edtPassword.text.toString()

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            if(registerJob.isActive) registerJob.cancel()

            registerJob = launch {
                registerViewModel.registerUser(name = name, email = email, password = password).collect { result ->
                    result.onSuccess {
                        Toast.makeText(
                            requireContext(),
                            "Registration Success",
                            Toast.LENGTH_SHORT
                        ).show()

                        val goToLogin = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                        findNavController().navigate(goToLogin)
                    }

                    result.onFailure {
                        Snackbar.make(binding.root, "Register Gagal", Snackbar.LENGTH_SHORT).show()
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