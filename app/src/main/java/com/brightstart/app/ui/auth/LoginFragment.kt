package com.brightstart.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brightstart.app.BuildConfig
import com.brightstart.app.R
import com.brightstart.app.data.models.LoginResponse
import com.brightstart.app.data.repository.BrightStartRepository
import com.brightstart.app.databinding.FragmentLoginBinding
import com.brightstart.app.utils.SessionManager
import com.brightstart.app.utils.Validators
import com.brightstart.app.utils.gone
import com.brightstart.app.utils.visible
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private val repo = BrightStartRepository()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        lifecycleScope.launch {
            if (sessionManager.isLoggedIn.first() && sessionManager.token() != null) {
                findNavController().navigate(R.id.dashboardFragment)
            }
        }

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.btnGoogle.setOnClickListener { signInWithGoogle() }
        binding.tvRegister.setOnClickListener { findNavController().navigate(R.id.registerFragment) }
        binding.tvBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun attemptLogin() {
        binding.tvError.gone()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        val error = Validators.firstError(Validators.email(email), if (password.isEmpty()) "Password is required" else null)
        if (error != null) { showError(error); return }

        setBusy(true)
        lifecycleScope.launch {
            repo.login(email, password).fold(
                onSuccess = { handleLoginResponse(it) },
                onFailure = { setBusy(false); showError("Could not reach the server. Check your internet connection and try again.") }
            )
        }
    }

    private fun signInWithGoogle() {
        binding.tvError.gone()
        val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        if (clientId.startsWith("YOUR_")) {
            showError("Google sign-in is not set up in this build yet.")
            return
        }
        setBusy(true)
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()

        lifecycleScope.launch {
            try {
                val result = CredentialManager.create(requireContext()).getCredential(requireActivity(), request)
                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    repo.googleLogin(idToken).fold(
                        onSuccess = { handleLoginResponse(it) },
                        onFailure = { setBusy(false); showError("Could not reach the server. Please try again.") }
                    )
                } else {
                    setBusy(false); showError("Unexpected sign-in response. Please try again.")
                }
            } catch (e: GetCredentialCancellationException) {
                setBusy(false)
            } catch (e: NoCredentialException) {
                setBusy(false); showError("No Google account found on this phone. Add one in Settings and try again.")
            } catch (e: GetCredentialException) {
                setBusy(false); showError("Google sign-in failed. Please try again.")
            }
        }
    }

    private suspend fun handleLoginResponse(response: Response<LoginResponse>) {
        val body = response.body()
        val user = body?.user
        val token = body?.token
        if (_binding == null) return
        if (body?.success == true && user != null && !token.isNullOrBlank()) {
            sessionManager.saveSession(user.adminId, user.fullName, user.username, user.email, user.role, token)
            setBusy(false)
            findNavController().navigate(R.id.dashboardFragment)
        } else {
            setBusy(false)
            showError(body?.message ?: "Login failed. Please try again.")
        }
    }

    private fun setBusy(busy: Boolean) {
        if (_binding == null) return
        binding.progressBar.visibility = if (busy) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !busy
        binding.btnGoogle.isEnabled = !busy
    }

    private fun showError(message: String) {
        if (_binding == null) return
        binding.tvError.text = message
        binding.tvError.visible()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
