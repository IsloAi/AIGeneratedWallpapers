package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.FragmentSignInBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.menuFragments.HomeFragment
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.GoogleLogin

class SignInFragment : Fragment() {
    private lateinit var binding : FragmentSignInBinding
    private var myContext : Context?= null
    private val googleLogin = GoogleLogin()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignInBinding.inflate(inflater,container,false)
         if(myContext != null){
             onCreatingCalling()
         }
        return  binding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    private fun onCreatingCalling() {
        initializeUI()
//        loginBtn.setOnClickListener {loginUserAccount()}
//        binding.signUpText.setOnClickListener {
//            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
//        }
//        binding.forgetPassword.setOnClickListener {
//            resetPassword()
//        }
//        binding.googleLogin.setOnClickListener {
//            login()
//        }
    }

    private fun initializeUI() {
//        emailTV = binding.enterMail
//        passwordTV = binding.password
//        loginBtn = binding.login
//        progressBar = binding.progressBar
//
//        binding.passwordToggle.setOnClickListener {
//            if (passwordTV.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
//                // Password is currently visible, so switch to a password field
//                passwordTV.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
////                passwordToggle.text = "Show"
//            } else {
//                // Password is currently hidden, so switch to visible text
//                passwordTV.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
////                passwordToggle.text = "Hide"
//            }
//            // Move the cursor to the end of the text
//            passwordTV.setSelection(passwordTV.text.length)
//        }
    }

    private fun loginUserAccount() {
//        val email = emailTV.text.toString()
//        val password = passwordTV.text.toString()
//
//        if (email.isEmpty()) {
//            Toast.makeText(myContext, "Please enter email...", Toast.LENGTH_LONG).show()
//            return
//        }
//        if (password.isEmpty()) {
//            Toast.makeText(myContext, "Please enter password!", Toast.LENGTH_LONG).show()
//            return
//        }
//        progressBar.visibility = VISIBLE
//        firebaseAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(myContext, "Login successful!", Toast.LENGTH_LONG).show()
//                    progressBar.visibility = GONE
//
//                } else {
//                    Toast.makeText(myContext, "Login failed! Please try again later", Toast.LENGTH_LONG).show()
//                    progressBar.visibility = GONE
//                }
//            }
    }

    private fun resetPassword() {
//        val email = emailTV.text.toString()
//        if (email.isEmpty()) {
//            Toast.makeText(myContext, "Please enter your email...", Toast.LENGTH_LONG).show()
//            return
//        }
//        progressBar.visibility = VISIBLE
//        firebaseAuth.sendPasswordResetEmail(email)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(myContext, "Password reset email sent. Check your email to reset your password.", Toast.LENGTH_LONG).show()
//                    progressBar.visibility = GONE
//                } else {
//                    Toast.makeText(myContext, "Failed to send password reset email. Please try again later.", Toast.LENGTH_LONG).show()
//                    progressBar.visibility = GONE
//                }
//            }
    }
    private fun login() {
//        auth = FirebaseAuth.getInstance()
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, HomeFragment.RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()){ task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(requireContext(), "Successfully login", Toast.LENGTH_SHORT).show()
//                    val user = auth.currentUser
//                    val  name = user?.displayName
//                    val  email = user?.email
//                    val  image = user?.photoUrl
//                    if(email != null){
//                        googleLogin.fetchGems(requireContext(),email)
//                        findNavController().popBackStack()
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            }
    }

}