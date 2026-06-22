package com.brightstart.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brightstart.app.R
import com.brightstart.app.data.repository.BrightStartRepository
import com.brightstart.app.databinding.FragmentLoginBinding
import com.brightstart.app.utils.SessionManager
import com.brightstart.app.utils.gone
import com.brightstart.app.utils.visible
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        lifecycleScope.launch {
            if (sessionManager.isLoggedIn.first()) {
                findNavController().navigate(R.id.dashboardFragment)
            }
        }

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
        binding.tvBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun attemptLogin() {
        binding.tvError.gone()
        val email    = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            binding.tvError.text = "Please enter email and password"
            binding.tvError.visible()
            return
        }
        binding.progressBar.visible()
        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val result = BrightStartRepository().login(email, password)
                binding.progressBar.gone()
                binding.btnLogin.isEnabled = true
                result.fold(
                    onSuccess = { response ->
                        val body = response.body()
                        if (body?.success == true && body.user != null) {
                            val user = body.user
                            sessionManager.saveSession(
                                user.adminId, user.fullName, user.email, user.role
                            )
                            findNavController().navigate(R.id.dashboardFragment)
                        } else {
                            binding.tvError.text = body?.message ?: "Login failed"
                            binding.tvError.visible()
                        }
                    },
                    onFailure = {
                        binding.tvError.text = "Network error: ${it.message}"
                        binding.tvError.visible()
                    }
                )
            } catch (e: Exception) {
                binding.progressBar.gone()
                binding.btnLogin.isEnabled = true
                binding.tvError.text = "Error: ${e.message}"
                binding.tvError.visible()
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

