package com.adarsh.wallzee.auth

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.navigation.fragment.findNavController
import com.adarsh.wallzee.creator.CreatorDataClass
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val reqCode: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let { FirebaseApp.initializeApp(it) }
        auth = Firebase.auth

        //google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)).requestEmail().build()

        mGoogleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }!!
        firebaseAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btSignInWithGoogle.setOnClickListener { signInGoogle() }
        binding.btSignInAnonymously.setOnClickListener { signInAnonymously() }

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
                    signInWithEmail()
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


        binding.tvRegisterNow.setOnClickListener {

            val bundle = Bundle()
            if (binding.etEmail.text?.isNotEmpty() == true) {
                bundle.putString("email_arg", binding.etEmail.text.toString())
            } else {
                bundle.putString("email_arg", null)
            }

            findNavController().navigate(R.id.registerFragment, bundle)
        }
    }

    private fun signInWithEmail() {
        auth.signInWithEmailAndPassword(
            binding.etEmail.text.toString().trim(),
            binding.etPassword.text.toString().trim()
        )
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    if (user.isEmailVerified) {
                        storeCreator()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Please verify your email!",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        Snackbar.make(
                            binding.root,
                            "New to app? Register now",
                            Snackbar.LENGTH_LONG
                        ).show()
//                        Toast.makeText(context, "Create account", Toast.LENGTH_SHORT).show()
                    }

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.textInputLayoutPassword.error = " "
                        Snackbar.make(
                            binding.root,
                            "Check your password and try again!",
                            Snackbar.LENGTH_LONG
                        ).show()

                        binding.tvForgotPassword.visibility = View.VISIBLE

                    }

                }
            }
    }

    private fun storeCreator() {
        val collectionRef = Firebase.firestore.collection("creators")
        collectionRef.document(Firebase.auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    val followingList = arrayListOf<String>()
                    followingList.add("")
                    collectionRef.document(Firebase.auth.currentUser?.email.toString())
                        .set(
                            CreatorDataClass(
                                "Wallzee creator",
                                "",
                                Firebase.auth.currentUser?.email,
                                "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/user.png?alt=media&token=046ce786-80c7-4695-9c7c-02f98d022d76",
                                0,
                                followingList,
                                false,
                                admin = false
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Profile updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Profile update failed!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    if (arguments?.getString("fragment") == "upload")
                        findNavController().navigate(R.id.uploadFragment)
                    else if (arguments?.getString("fragment") == "profile")
                        findNavController().navigate(R.id.creatorProfileFragment)
                    else
                        findNavController().navigate(R.id.FirstFragment)

                } else {
                    if (arguments?.getString("fragment") == "upload")
                        findNavController().navigate(R.id.uploadFragment)
                    else if (arguments?.getString("fragment") == "profile")
                        findNavController().navigate(R.id.creatorProfileFragment)
                    else
                        findNavController().navigate(R.id.FirstFragment)
                }
            }

    }


    private fun signInAnonymously() {
        val handler = HandlerCompat.createAsync(Looper.getMainLooper())
        handler.postDelayed({
            auth.signInAnonymously()
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_loginFragment_to_FirstFragment)
                    } else {
                        Toast.makeText(context, "System busy try again later!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }, 500)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, reqCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUIOnGoogleSignIn(account)
            }
        } catch (e: ApiException) {
//            Log.i("itsivag", e.toString())
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUIOnGoogleSignIn(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storeCreator()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            val user = auth.currentUser!!
            if (user.isEmailVerified) {
                if (arguments?.getString("fragment") == "upload")
                    findNavController().navigate(R.id.uploadFragment)
                else if (arguments?.getString("fragment") == "profile")
                    findNavController().navigate(R.id.creatorProfileFragment)
                else
                    findNavController().navigate(R.id.FirstFragment)
            }
        }
    }

}