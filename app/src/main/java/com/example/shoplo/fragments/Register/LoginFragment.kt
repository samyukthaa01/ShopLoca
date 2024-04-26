package com.example.shoplo.fragments.Register

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shoplo.R

import com.example.shoplo.activities.ManagingActivity
import com.example.shoplo.databinding.FragmentLoginBinding
import com.example.shoplo.dialog.setupBottomSheetDialog
import com.example.shoplo.util.Resource
import com.example.shoplo.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel by viewModels<LoginViewModel>()

    private val RC_SIGN_IN = 591020719 // Example value, you can choose any integer value
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase Auth instance
        auth = FirebaseAuth.getInstance()

        binding.apply {
            tvDontHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registeraFragment)
            }

            buttonLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email, password)
            }

            tvForgotPasswordLogin.setOnClickListener {
                setupBottomSheetDialog { email ->
                    viewModel.sendPasswordResetEmail(email) // Use the new function to send reset password email
                }
            }

            googleSignInButton.setOnClickListener {
                // Call a function to start the Google Sign-In process
                startGoogleSignIn()
            }

        }

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{ // Observe resetPassword flow
                when (it) {
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(),"Reset link was sent to your email",Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(),"Error: ${it.message}",Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonLoginLogin.revertAnimation()
                        Intent(requireActivity(), ManagingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLoginLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun startGoogleSignIn() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            // Handle sign-in failure
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    // Proceed with navigating to ManagingActivity
                    val intent = Intent(requireActivity(), ManagingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }

    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}
