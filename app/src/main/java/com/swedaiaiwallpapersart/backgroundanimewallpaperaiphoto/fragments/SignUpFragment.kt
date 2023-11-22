package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding
.FragmentSignUpBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.FirebaseUtils

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private var context :Context? = null
    private lateinit var emailTVRegister: EditText
    private lateinit var passwordTVRegister: EditText
    private lateinit var confirmPasswordTVRegister: EditText
    private lateinit var regBtn: TextView
    private lateinit var progressBarRegister: ProgressBar

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
           if(context != null){
               onCreateCalling()
           }
        return  binding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }
    private fun onCreateCalling() {
        initializeUI()
        regBtn.setOnClickListener {registerNewUser()}
        binding.signinText.setOnClickListener {findNavController().popBackStack()}

    }
    private fun initializeUI() {
        emailTVRegister = binding.enterMail
        passwordTVRegister = binding.password
        confirmPasswordTVRegister = binding.confirmPassword

        regBtn = binding.register
        progressBarRegister = binding.progressBar

      binding.passwordToggle.setOnClickListener {
            if (passwordTVRegister.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Password is currently visible, so switch to a password field
                passwordTVRegister.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//                passwordToggle.text = "Show"
            } else {
                // Password is currently hidden, so switch to visible text
                passwordTVRegister.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//                passwordToggle.text = "Hide"
            }
            // Move the cursor to the end of the text
          passwordTVRegister.setSelection(passwordTVRegister.text.length)
        }

        binding.confirmPasswordToggle.setOnClickListener {
            if (confirmPasswordTVRegister.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Password is currently visible, so switch to a password field
                confirmPasswordTVRegister.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//                passwordToggle.text = "Show"
            } else {
                // Password is currently hidden, so switch to visible text
                confirmPasswordTVRegister.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//                passwordToggle.text = "Hide"
            }
            // Move the cursor to the end of the text
            confirmPasswordTVRegister.setSelection(confirmPasswordTVRegister.text.length)
        }
    }

    private fun registerNewUser() {
//        val email: String = emailTVRegister.text.toString()
//        val password: String = passwordTVRegister.text.toString()
//        val confirmPassword  = confirmPasswordTVRegister.text.toString()
//        if (TextUtils.isEmpty(email)) {
//            Toast.makeText(context, "Please enter email...", Toast.LENGTH_LONG).show()
//            return
//        }
//        if (TextUtils.isEmpty(password)) {
//            Toast.makeText(context, "Please enter password!", Toast.LENGTH_LONG).show()
//            return
//        }
//        if (TextUtils.isEmpty(confirmPassword)) {
//            Toast.makeText(context, "Please enter confirm password!", Toast.LENGTH_LONG).show()
//            return
//        }
//        if(!TextUtils.equals(password,confirmPassword)){
//            Toast.makeText(context, "Password not match", Toast.LENGTH_LONG).show()
//            return
//        }
//        progressBarRegister.visibility = VISIBLE
//        FirebaseUtils.firebaseAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
//                    findNavController().popBackStack()
//                    progressBarRegister.visibility = GONE
//                }else{
//                    Toast.makeText(context, "Registration failed! Please try again later", Toast.LENGTH_LONG).show()
//                    progressBarRegister.visibility = GONE
//                }
//            }
    }

}