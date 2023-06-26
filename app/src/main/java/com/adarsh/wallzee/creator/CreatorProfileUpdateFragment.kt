package com.adarsh.wallzee.creator

import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.trimmedLength
import androidx.navigation.fragment.findNavController
import com.adarsh.wallzee.upload.UploadFragment
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.FragmentCreatorProfileUpdateBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import kotlin.properties.Delegates

class CreatorProfileUpdateFragment : Fragment() {
    private var _binding: FragmentCreatorProfileUpdateBinding? = null
    private val binding get() = _binding!!
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private var imageSelected by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatorProfileUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCreator()

        binding.btUpdateProfile.setOnClickListener {
            if (binding.etCreatorName.text!!.trimmedLength() >= 3) {
                if (selectedImageUri != null) {
                    uploadPhotoToStorage(
                        binding.etCreatorName.text.toString(),
                        binding.etCreatorSocialLink.text.toString()
                    )
                } else {
                    Toast.makeText(context, "select an profile photo", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.etCreatorName.error = "Name must be longer!"

            }
        }

        binding.ivProfile.setOnClickListener {
            imageChooser()
        }

    }

    private fun imageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Complete action using"),
            UploadFragment.SELECT_IMAGE_FROM_GALLERY_CODE
        )

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val ivHomeScreenPreview = binding.ivProfile

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == UploadFragment.SELECT_PICTURE) {
                selectedImageUri = data?.data!!
                // update the preview image in the layout
                imageSelected = true
                ivHomeScreenPreview.setImageURI(selectedImageUri)
            }
        }
    }

    private fun uploadPhotoToStorage(creatorName: String, creatorProfileLink: String) {
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        val userEmail = user.substring(0, user.length - 4)
        val uploadProgressIndicator = binding.uploadProgressIndicator
        val profileStorageRef = storage.reference.child(userEmail)
            .child(System.currentTimeMillis().toString() + "profile")
        val homeScreen = binding.ivProfile
        homeScreen.isDrawingCacheEnabled = true
        homeScreen.buildDrawingCache()
        val bitmap = (homeScreen.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 30, baos)
        val data = baos.toByteArray()

        uploadProgressIndicator.visibility = View.VISIBLE
        val profileUploadTask = profileStorageRef.putBytes(data)
        profileUploadTask.addOnSuccessListener {
            downloadUrl(
                profileStorageRef, creatorName, creatorProfileLink
            )
            uploadProgressIndicator.visibility = View.INVISIBLE
            binding.etCreatorName.text?.clear()
            binding.etCreatorSocialLink.text?.clear()
            binding.btUpdateProfile.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary))
            binding.btUpdateProfile.isClickable = true
            findNavController().navigate(R.id.FirstFragment)
        }.addOnFailureListener {
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadUrl(
        profileStorageRef: StorageReference,
        creatorName: String,
        creatorProfileLink: String
    ) {


        profileStorageRef.downloadUrl.addOnSuccessListener { imageUrl ->
            val updates = hashMapOf<String, Any>(
                "creatorName" to creatorName,
                "creatorLinks" to creatorProfileLink,
                "creatorProfileUrl" to imageUrl
            )
            val collectionRef = Firebase.firestore.collection("creators")
            val query = collectionRef.whereEqualTo("creatorEmail", Firebase.auth.currentUser?.email)
                .limit(1)
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        for (document in querySnapshot.documents) {
                            collectionRef.document(Firebase.auth.currentUser?.email.toString())
                                .update(updates)
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
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle query failure
                }

        }.addOnFailureListener {
            Toast.makeText(context, "Unable to store url in database!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCreator() {
        Firebase.firestore.collection("creators").whereEqualTo(
            "creatorEmail",
            Firebase.auth.currentUser?.email
        )
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val creator = document.toObject<CreatorDataClass>()
                    binding.etCreatorName.setText(creator.creatorName)
                    binding.etCreatorSocialLink.setText(creator.creatorLinks)

                    Glide
                        .with(requireContext())
                        .load(document.toObject<CreatorDataClass>().creatorProfileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .centerCrop()
                        .into(binding.ivProfile)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}