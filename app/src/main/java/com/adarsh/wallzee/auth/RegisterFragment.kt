package com.adarsh.wallzee.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = arguments?.getString("email_arg")
        if (email != null)
            binding.etEmail.setText(email)

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    binding.etEmail.error = "Invalid email address"

                } else {
                    binding.etEmail.error = null
                }
            }
        })

        binding.btSignInWithEmail.setOnClickListener {

            if (binding.etEmail.error == null) {
                if (binding.etPassword.text?.length!! >= 8) {
                    registerUser()
                } else if (binding.etEmail.text?.isEmpty() == true) {
                    binding.etEmail.error = "Email must not be empty!"
                } else {
                    Snackbar
                        .make(
                            binding.root,
                            "Password must be at least 8 characters",
                            Snackbar.LENGTH_SHORT
                        ).show()
                }
            }
        }
    }

    private fun registerUser() {
        val actionCodeSettings = actionCodeSettings {
            url = "https://www.example.com/finishSignUp?cartId=1234"
            // This must be true
            handleCodeInApp = true
            setIOSBundleId("com.example.ios")
            setAndroidPackageName(
                "com.example.android",
                true, /* installIfNotAvailable */
                "12" /* minimumVersion */)
        }


        auth.createUserWithEmailAndPassword(
            binding.etEmail.text.toString().trim(),
            binding.etPassword.text.toString().trim()
        )
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("itsivag", "createUserWithEmail:success")

                    val user = auth.currentUser!!
                    user.sendEmailVerification().addOnSuccessListener {
                        Toast.makeText(context,"Email Verification sent", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }.addOnFailureListener {
                        Toast.makeText(context,"failed", Toast.LENGTH_SHORT).show()
                    }
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Snackbar.make(
                            binding.root,
                            "User already exists try signing in",
                            Snackbar.LENGTH_LONG
                        ).show()
//                        Toast.makeText(context, "user exists", Toast.LENGTH_SHORT).show()
                    }
//                    updateUI(null)
                }
            }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}