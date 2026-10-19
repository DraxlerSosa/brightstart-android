package com.brightstart.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.brightstart.app.R
import com.brightstart.app.databinding.FragmentRegisterBinding
import com.brightstart.app.ui.SharedViewModel
import com.brightstart.app.utils.Validators
import com.brightstart.app.utils.gone
import com.brightstart.app.utils.visible

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roles = arrayOf("Volunteer", "Coordinator", "Management")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.tvLogin.setOnClickListener { findNavController().navigateUp() }
        binding.tvBack.setOnClickListener { findNavController().navigateUp() }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !loading
        }
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result?.fold(
                onSuccess = { msg ->
                    binding.tvError.gone()
                    binding.cardSuccess.visible()
                    binding.tvSuccessMsg.text = msg
                    binding.btnGoLogin.setOnClickListener {
                        findNavController().navigate(R.id.loginFragment)
                    }
                },
                onFailure = {
                    binding.tvError.text = it.message
                    binding.tvError.visible()
                }
            )
        }
    }

    private fun attemptRegister() {
        binding.tvError.gone()
        val fullName = binding.etFullName.text.toString().trim()
        val email    = binding.etEmail.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm  = binding.etConfirmPassword.text.toString()
        val role     = binding.spinnerRole.selectedItem.toString()
        val error = Validators.firstError(
            Validators.fullName(fullName),
            Validators.email(email),
            Validators.username(username),
            Validators.password(password),
            Validators.passwordsMatch(password, confirm)
        )
        if (error != null) {
            binding.tvError.text = error; binding.tvError.visible()
        } else {
            viewModel.register(fullName, email, username, password, role)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

