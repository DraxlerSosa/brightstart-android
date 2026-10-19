package com.brightstart.app.ui.settings

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brightstart.app.BuildConfig
import com.brightstart.app.R
import com.brightstart.app.data.repository.BrightStartRepository
import com.brightstart.app.databinding.FragmentSettingsBinding
import com.brightstart.app.utils.SessionManager
import com.brightstart.app.utils.Validators
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager
    private val repo = BrightStartRepository()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager(requireContext())

        binding.tvVersion.text = "BrightStart version ${BuildConfig.VERSION_NAME}"
        binding.tvServer.text = "Server: ${Uri.parse(BuildConfig.BASE_URL).host}"

        lifecycleScope.launch {
            showAccount(session.userName.first(), session.username.first(), session.userEmail.first(), session.userRole.first())
            // Refresh from the server so the username is always current
            repo.getProfile().onSuccess { response ->
                val user = response.body()?.user
                if (response.body()?.success == true && user != null && _binding != null) {
                    session.updateProfile(user.fullName, user.username)
                    showAccount(user.fullName, user.username, user.email, user.role)
                }
            }
        }

        binding.btnSaveProfile.setOnClickListener { saveProfile() }
        binding.btnChangePassword.setOnClickListener { changePassword() }
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun showAccount(name: String, username: String, email: String, role: String) {
        if (_binding == null) return
        binding.tvName.text = name
        binding.tvEmail.text = email
        binding.tvRole.text = role
        binding.tvAvatar.text = name.trim().firstOrNull()?.uppercase() ?: "?"
        binding.etFullName.setText(name)
        binding.etUsername.setText(username)
    }

    private fun saveProfile() {
        val name = binding.etFullName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val error = Validators.firstError(Validators.fullName(name), Validators.username(username))
        if (error != null) { message(binding.tvProfileMsg, error, false); return }

        binding.btnSaveProfile.isEnabled = false
        lifecycleScope.launch {
            repo.updateProfile(name, username).fold(
                onSuccess = { response ->
                    val body = response.body()
                    if (body?.success == true) {
                        session.updateProfile(name, username)
                        if (_binding != null) {
                            showAccount(name, username, binding.tvEmail.text.toString(), binding.tvRole.text.toString())
                            message(binding.tvProfileMsg, "Profile updated", true)
                        }
                    } else if (_binding != null) {
                        message(binding.tvProfileMsg, body?.message ?: "Could not update your profile", false)
                    }
                },
                onFailure = { if (_binding != null) message(binding.tvProfileMsg, "Could not reach the server. Check your connection.", false) }
            )
            if (_binding != null) binding.btnSaveProfile.isEnabled = true
        }
    }

    private fun changePassword() {
        val current = binding.etCurrentPassword.text.toString()
        val new = binding.etNewPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()
        val error = Validators.firstError(
            if (current.isEmpty()) "Enter your current password" else null,
            Validators.password(new),
            Validators.passwordsMatch(new, confirm),
            if (new == current) "New password must be different from the current one" else null
        )
        if (error != null) { message(binding.tvPasswordMsg, error, false); return }

        binding.btnChangePassword.isEnabled = false
        lifecycleScope.launch {
            repo.changePassword(current, new).fold(
                onSuccess = { response ->
                    val body = response.body()
                    if (_binding != null) {
                        if (body?.success == true) {
                            binding.etCurrentPassword.text?.clear()
                            binding.etNewPassword.text?.clear()
                            binding.etConfirmPassword.text?.clear()
                            message(binding.tvPasswordMsg, "Password changed. Other devices have been signed out.", true)
                        } else {
                            message(binding.tvPasswordMsg, body?.message ?: "Could not change your password", false)
                        }
                    }
                },
                onFailure = { if (_binding != null) message(binding.tvPasswordMsg, "Could not reach the server. Check your connection.", false) }
            )
            if (_binding != null) binding.btnChangePassword.isEnabled = true
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            repo.logout()               // invalidate the token on the server (best effort)
            session.clearSession()
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun message(view: TextView, text: String, success: Boolean) {
        view.text = text
        view.setTextColor(if (success) Color.parseColor("#B8E04A") else Color.parseColor("#FF5252"))
        view.visibility = View.VISIBLE
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
