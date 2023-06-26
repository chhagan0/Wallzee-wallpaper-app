package com.adarsh.wallzee.upload

import android.content.ContentResolver
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.adarsh.wallzee.ringtone.RingtoneDataClass
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.FragmentUploadBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class UploadFragment : Fragment() {
    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!

    private lateinit var storage: FirebaseStorage
    private lateinit var selectedImageUri: Uri
    private lateinit var selectedVideoUri: Uri
    private lateinit var selectedRingtoneUri: Uri
    private var imageSelected = false
    private var videoSelected = false
    private var ringtoneSelected = false
    private var category = ""
    private var wallpaperName = ""
    private var type = -1
    private var isCreatorVerified = false


    companion object {
        const val SELECT_PICTURE = 12
        const val SELECT_VIDEO = 13
        const val SELECT_RINGTONE = 14
        const val SELECT_IMAGE_FROM_GALLERY_CODE = 12
        const val SELECT_VIDEO_FROM_GALLERY_CODE = 13
        const val SELECT_RINGTONE_FROM_GALLERY_CODE = 14
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageSelected = false
        videoSelected = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Firebase.auth.currentUser?.email == null) findNavController().navigate(
            R.id.loginFragment,
            bundleOf("fragment" to "upload")
        )
//        val btProfile = binding.btProfile
        val btPost = binding.btPost
        val etCategories = binding.etCategories
        val ivUpload = binding.ivUpload
        val categories = resources.getStringArray(R.array.categories_array)
        val arrayAdapter = context?.let { ArrayAdapter(it, R.layout.dropdown_item, categories) }

        etCategories.setAdapter(arrayAdapter)

        storage = FirebaseStorage.getInstance()
//        database = FirebaseDatabase.getInstance().reference


        //select from gallery
        ivUpload.setOnClickListener {
            showPopUp()
        }

        //post
        btPost.setOnClickListener {

            val etName = binding.etName
//            val notification = NotificationService(this).showProgressNotification()
            category = etCategories.text.toString()

            when (type) {
                1 -> {
                    if (imageSelected) {
                        if (etName.text.toString() != "") {
                            if (category != "") {
                                wallpaperName = etName.text.toString()
                                uploadToStorage(category, 1)
//                        NotificationService(this).showProgress(notification, 0, 1)
                            } else Toast.makeText(context, "choose a category", Toast.LENGTH_SHORT)
                                .show()
                        } else Toast.makeText(context, "enter wallpaper's name", Toast.LENGTH_SHORT)
                            .show()
                    } else Toast.makeText(context, "select an image", Toast.LENGTH_SHORT).show()
                }

                2 -> {
                    if (videoSelected) {
                        if (etName.text.toString() != "") {
                            if (category != "") {
                                wallpaperName = etName.text.toString()
                                uploadToStorage(category, 2)
//                        NotificationService(this).showProgress(notification, 0, 1)
                            } else Toast.makeText(context, "choose a category", Toast.LENGTH_SHORT)
                                .show()
                        } else Toast.makeText(context, "enter wallpaper's name", Toast.LENGTH_SHORT)
                            .show()
                    } else Toast.makeText(context, "select an video", Toast.LENGTH_SHORT).show()
                }

                3 -> {
                    if (ringtoneSelected) {
                        if (etName.text.toString() != "") {
                            wallpaperName = etName.text.toString()
                            uploadToStorage(category, 3)
//                        NotificationService(this).showProgress(notification, 0, 1)
                        } else Toast.makeText(context, "enter wallpaper's name", Toast.LENGTH_SHORT)
                            .show()
                    } else Toast.makeText(context, "select an ringtone", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showPopUp() {
        val popUpView = View.inflate(this.context, R.layout.upload_pop_up_layout, null)
        val attribute = LinearLayout.LayoutParams.WRAP_CONTENT
        val widthAttribute = LinearLayout.LayoutParams.MATCH_PARENT
        val popUpWindow = PopupWindow(
            popUpView, widthAttribute, attribute, true
        )
        popUpWindow.showAtLocation(popUpView, Gravity.CENTER, 0, 0)

        val btClose = popUpView.findViewById<MaterialButton>(R.id.btUploadPopUpClose)
        val btWallpaperUpload = popUpView.findViewById<MaterialButton>(R.id.btWallpaperUpload)
        val btRingtoneUpload = popUpView.findViewById<MaterialButton>(R.id.btRingtoneUpload)
        val btLiveWallpaperUpload =
            popUpView.findViewById<MaterialButton>(R.id.btLiveWallpaperUpload)

        btLiveWallpaperUpload.setOnClickListener {
            popUpWindow.dismiss()
            videoChooser()
            type = 2
        }

        btWallpaperUpload.setOnClickListener {
            popUpWindow.dismiss()
            imageChooser()
            type = 1
        }

        btRingtoneUpload.setOnClickListener {
            popUpWindow.dismiss()
            ringtoneChooser()
            type = 3
        }
        btClose.setOnClickListener { popUpWindow.dismiss() }


    }

    private fun ringtoneChooser() {
        val intent = Intent()
        intent.type = "audio/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Complete action using"), SELECT_RINGTONE_FROM_GALLERY_CODE
        )
    }

    private fun videoChooser() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Complete action using"), SELECT_VIDEO_FROM_GALLERY_CODE
        )
    }

    private fun imageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Complete action using"), SELECT_IMAGE_FROM_GALLERY_CODE
        )

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val ivWallpaperPreview = binding.ivWallpaperPreview

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data?.data!!
                imageSelected = true
                binding.etCategoriesLayout.visibility = View.VISIBLE
                ivWallpaperPreview.setImageURI(selectedImageUri)
            }

            if (requestCode == SELECT_VIDEO) {
                selectedVideoUri = data?.data!!
                if (getVideoSize() < 5 * 1024 * 1024) {
                    videoSelected = true
                    binding.etCategoriesLayout.visibility = View.VISIBLE
                    ivWallpaperPreview.setImageBitmap(createVideoThumbnail(selectedVideoUri))
                } else {
                    Toast.makeText(
                        context, "Live wallpaper should not be more than 5Mb .", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            if (requestCode == SELECT_RINGTONE) {
                selectedRingtoneUri = data?.data!!
                if (getRingtoneSize(selectedRingtoneUri) < 10 * 1024 * 1024) {
                    Toast.makeText(
                        context, "Ringtone selected.", Toast.LENGTH_SHORT
                    ).show()

                    ringtoneSelected = true
                    binding.etName.setText(getRingtoneName(selectedRingtoneUri))
                    binding.etCategoriesLayout.visibility = View.GONE
                } else {
                    Toast.makeText(
                        context, "Ringtones should not be more than 10Mb .", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getRingtoneName(uri: Uri): String {
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor.use {
            it?.moveToFirst()
            val nameIndex = it?.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: -1
            return if (nameIndex != -1) {
                it!!.getString(nameIndex)
            } else {
                "unknown_file"
            }
        }
    }

    private fun getRingtoneSize(uri: Uri): Long {
        val cursor = context?.contentResolver?.query(uri, null, null, null, null)
        cursor.use {
            it?.moveToFirst()
            val sizeIndex = it?.getColumnIndex(OpenableColumns.SIZE) ?: -1
            return if (sizeIndex != -1) {
                it!!.getLong(sizeIndex)
            } else {
                0
            }
        }
    }

    private fun getVideoSize(): Long {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(selectedVideoUri, null, null, null, null)
        cursor?.moveToFirst()
        val sizeIndex = cursor?.getColumnIndex(MediaStore.Video.Media.SIZE)
        val size = cursor?.getLong(sizeIndex ?: 0)
        cursor?.close()
        return size ?: 0
    }

    private fun uploadToStorage(categoryChip: String, type: Int) {
        val user = FirebaseAuth.getInstance().currentUser?.email.toString()
        val userEmail = user.substring(0, user.length - 4)
        val uploadWallpaperIcon = binding.ivUpload
        val uploadProgressIndicator = binding.uploadProgressIndicator
        val btPost = binding.btPost
        val etName = binding.etName
        val etCategories = binding.etCategories
        val linearLayoutWallpaperName = binding.linearLayoutWallpaperName

        linearLayoutWallpaperName.visibility = View.GONE
        uploadProgressIndicator.visibility = View.VISIBLE

        btPost.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))
//        NotificationService(this).showProgressNotification()
        uploadWallpaperIcon.visibility = View.GONE

        when (type) {
            1 -> {
                uploadWallpaper(
                    userEmail,
                    uploadProgressIndicator,
                    etName,
                    etCategories,
                    categoryChip,
                    linearLayoutWallpaperName,
                    uploadWallpaperIcon,
                    btPost
                )
            }

            2 -> {
                uploadLiveWallpaper(
                    userEmail,
                    uploadProgressIndicator,
                    etName,
                    etCategories,
                    categoryChip,
                    linearLayoutWallpaperName,
                    uploadWallpaperIcon,
                    btPost
                )
            }

            3 -> {
                uploadRingtone(
                    userEmail,
                    uploadProgressIndicator,
                    etName,
                    etCategories,
                    categoryChip,
                    linearLayoutWallpaperName,
                    uploadWallpaperIcon,
                    btPost
                )
            }
        }
    }

    private fun uploadRingtone(
        userEmail: String,
        uploadProgressIndicator: LinearProgressIndicator,
        etName: TextInputEditText,
        etCategories: AutoCompleteTextView,
        categoryChip: String,
        linearLayoutWallpaperName: LinearLayout,
        uploadWallpaperIcon: ImageView,
        btPost: MaterialButton
    ) {
        val ringtoneStorageRef = storage.reference.child(userEmail)
            .child(System.currentTimeMillis().toString() + "ringtone")
        val ringtoneUploadTask = ringtoneStorageRef.putFile(selectedRingtoneUri)

        ringtoneUploadTask.addOnProgressListener {
            val byteTransferred = it.bytesTransferred.toInt()
            val total = it.totalByteCount.toInt()

            uploadProgressIndicator.progress = byteTransferred / total

        }.addOnSuccessListener {
            val ringtoneSize = ((it.totalByteCount) / 1024).toInt()

            downloadUrl(
                ringtoneStorageRef, ringtoneStorageRef, categoryChip, ringtoneSize, 3
            )
            uploadProgressIndicator.visibility = View.GONE
            etName.text?.clear()
            etCategories.text.clear()
            linearLayoutWallpaperName.visibility = View.VISIBLE
            uploadWallpaperIcon.visibility = View.VISIBLE
            btPost.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.primary))
            btPost.isClickable = true
        }
    }

    private fun createVideoThumbnail(uri: Uri): Bitmap? {
        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, uri)
            return mediaMetadataRetriever.frameAtTime
        } catch (ex: Exception) {
            Toast.makeText(context, "Error retrieving bitmap", Toast.LENGTH_SHORT).show()
        }
        return null
    }
    //Live Wallpaper********************************************************************

    private fun uploadLiveWallpaper(
        userEmail: String,
        uploadProgressIndicator: LinearProgressIndicator,
        etName: TextInputEditText,
        etCategories: AutoCompleteTextView,
        categoryChip: String,
        linearLayoutWallpaperName: LinearLayout,
        uploadWallpaperIcon: ImageView,
        btPost: MaterialButton
    ) {

        val liveWallpaperThumbnailStorageRef = storage.reference.child(userEmail)
            .child(System.currentTimeMillis().toString() + "live_thumbnail")

        val bitmap = createVideoThumbnail(selectedVideoUri)
        val baos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.WEBP, 30, baos)
        val data = baos.toByteArray()
        val liveWallpaperThumbnailUploadTask = liveWallpaperThumbnailStorageRef.putBytes(data)

        liveWallpaperThumbnailUploadTask.addOnFailureListener {}.addOnSuccessListener {
            val liveWallpaperStorageRef = storage.reference.child(userEmail)
                .child(System.currentTimeMillis().toString() + "live")

            val uploadTask = liveWallpaperStorageRef.putFile(selectedVideoUri)
            uploadTask.addOnFailureListener {
                Toast.makeText(context, "Failed To upload!Try again later", Toast.LENGTH_SHORT)
                    .show()
            }.addOnProgressListener {
                //                val notification = NotificationService(this).showProgressNotification()

                val byteTransferred = it.bytesTransferred.toInt()
                val total = it.totalByteCount.toInt()

                uploadProgressIndicator.progress = byteTransferred / total
                //                NotificationService(this).showProgress(notification, byteTransferred, total)

            }.addOnSuccessListener {
                val wallpaperSize = ((it.totalByteCount) / 1024).toInt()

                downloadUrl(
                    liveWallpaperThumbnailStorageRef,
                    liveWallpaperStorageRef,
                    categoryChip,
                    wallpaperSize,
                    2
                )

                uploadProgressIndicator.visibility = View.GONE

                etName.text?.clear()
                etCategories.text.clear()

                linearLayoutWallpaperName.visibility = View.VISIBLE
                uploadWallpaperIcon.visibility = View.VISIBLE
                btPost.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                btPost.isClickable = true
            }

        }

    }

    //Wallpaper********************************************************************
    private fun uploadWallpaper(
        userEmail: String,
        uploadProgressIndicator: LinearProgressIndicator,
        etName: TextInputEditText,
        etCategories: AutoCompleteTextView,
        categoryChip: String,
        linearLayoutWallpaperName: LinearLayout,
        uploadWallpaperIcon: ImageView,
        btPost: MaterialButton
    ) {
        val thumbnailStorageRef = storage.reference.child(userEmail)
            .child(System.currentTimeMillis().toString() + "thumbnail")
        val homeScreen = binding.ivWallpaperPreview
        homeScreen.isDrawingCacheEnabled = true
        homeScreen.buildDrawingCache()
        val bitmap = (homeScreen.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 30, baos)
        val data = baos.toByteArray()
        val thumbnailUploadTask = thumbnailStorageRef.putBytes(data)

        thumbnailUploadTask.addOnFailureListener {}.addOnSuccessListener {
            val imageStorageRef =
                storage.reference.child(userEmail).child(System.currentTimeMillis().toString())

            val uploadTask = selectedImageUri.let { it1 -> imageStorageRef.putFile(it1) }
            uploadTask.addOnFailureListener {
                Toast.makeText(context, "Failed To upload!Try again later", Toast.LENGTH_SHORT)
                    .show()
            }.addOnProgressListener {
                //                val notification = NotificationService(this).showProgressNotification()

                val byteTransferred = it.bytesTransferred.toInt()
                val total = it.totalByteCount.toInt()

                uploadProgressIndicator.progress = byteTransferred / total
                //                NotificationService(this).showProgress(notification, byteTransferred, total)

            }.addOnSuccessListener {
                val wallpaperSize = ((it.totalByteCount) / 1024).toInt()
                downloadUrl(thumbnailStorageRef, imageStorageRef, categoryChip, wallpaperSize, 1)
                uploadProgressIndicator.visibility = View.GONE

                etName.text?.clear()
                etCategories.text.clear()

                linearLayoutWallpaperName.visibility = View.VISIBLE
                uploadWallpaperIcon.visibility = View.VISIBLE
                btPost.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.primary))
                btPost.isClickable = true
            }

        }
    }


    private fun downloadUrl(
        thumbnailStorageRef: StorageReference,
        imageStorageRef: StorageReference,
        categoryChip: String,
        wallpaperSize: Int,
        type: Int
    ) {

        imageStorageRef.downloadUrl.addOnSuccessListener { imageUrl ->
            thumbnailStorageRef.downloadUrl.addOnSuccessListener { thumbnailUrl ->
                getId(
                    thumbnailUrl.toString(), imageUrl.toString(), categoryChip, wallpaperSize, type
                )
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Unable to store url in database!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getId(
        thumbnailUrl: String, imageUrl: String, categoryChip: String, wallpaperSize: Int, type: Int
    ) {
        val docRef = Firebase.firestore.collection("id").document("id")
        val email = FirebaseAuth.getInstance().currentUser?.email.toString()
        docRef.get().addOnSuccessListener { document ->
            val id = (document.data?.get("id") as Long).toInt()
            val creatorRef =
                Firebase.firestore.collection("creators")
                    .document(Firebase.auth.currentUser?.email.toString())
            creatorRef.get().addOnSuccessListener { document1 ->
                isCreatorVerified = document1.get("verified") as Boolean

                storeInDatabase(
                    id,
                    thumbnailUrl,
                    imageUrl,
                    categoryChip,
                    wallpaperSize,
                    type,
                    isCreatorVerified
                )
            }
        }
    }

    private fun storeInDatabase(
        id: Int,
        thumbnailUrl: String,
        imageUrl: String,
        categoryChip: String,
        wallpaperSize: Int,
        type: Int,
        isVerified: Boolean
    ) {
        val db = Firebase.firestore

        val profileName = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        val email = FirebaseAuth.getInstance().currentUser?.email.toString()

        val date = SimpleDateFormat("dd-MMMM", Locale.getDefault()).format(Date())

        when (type) {
            1 -> {
                db.collection("wallpapers").document(id.toString()).set(
                    WallpaperDataClass(
                        id,
                        thumbnailUrl,
                        imageUrl,
                        wallpaperName.lowercase(),
                        categoryChip,
                        1,
                        date,
                        wallpaperSize,
                        profileName,
                        email,
                        isVerified
                    )
                ).addOnSuccessListener {
                    db.collection("id").document("id").update("id", id + 1)
                }
            }

            2 -> {
                db.collection("live wallpapers").document(id.toString()).set(
                    WallpaperDataClass(
                        id,
                        thumbnailUrl,
                        imageUrl,
                        wallpaperName.lowercase(),
                        categoryChip,
                        1,
                        date,
                        wallpaperSize,
                        profileName,
                        email,
                        isVerified

                    )
                ).addOnSuccessListener {
                    db.collection("id").document("id").update("id", id + 1)
                }

            }

            3 -> {
                db.collection("ringtones").document(id.toString()).set(
                    RingtoneDataClass(
                        id,
                        wallpaperName,
                        0,
                        null,
                        imageUrl,
                        1,
                        date,
                        wallpaperSize,
                        profileName,
                        email,
                        isVerified

                    )
                ).addOnSuccessListener {
                    db.collection("id").document("id").update("id", id + 1)
                }
            }
        }

    }
}


