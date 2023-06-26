package com.adarsh.wallzee.utils

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.ringtone.RingtoneDataClass
import com.adarsh.walzee.R
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class Ringtone {

    private val STORAGE_PERMISSION_REQUEST_CODE = 1
    private val WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 2

    fun getRingtones(
        ringtoneList: ArrayList<RingtoneDataClass>,
        rvRingtone: RecyclerView,
        lottieAnimationView: LottieAnimationView
    ) {
        val docRef =
            Firebase.firestore.collection("ringtones").whereEqualTo("creatorVerified", true)
        ringtoneList.clear()
        docRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val ringtone = document.toObject<RingtoneDataClass>()
                ringtoneList.add(
                    RingtoneDataClass(
                        ringtone.ringtoneid,
                        ringtone.ringtoneName,
                        ringtone.ringtoneDuration,
                        ringtone.ringtoneImage,
                        ringtone.ringtoneUrl,
                        ringtone.downloads,
                        ringtone.dateUploaded,
                        ringtone.ringtoneSize,
                        ringtone.creatorName,
                        ringtone.creatorEmail,
                        ringtone.creatorVerified
                    )
                )
            }
            rvRingtone.visibility = View.VISIBLE
            lottieAnimationView.visibility = View.GONE
            rvRingtone.adapter?.notifyItemInserted(0)
        }
    }

    fun shareRingtone(context: Context, currentItem: RingtoneDataClass) {
        val storageRef = Firebase.storage.getReferenceFromUrl(currentItem.ringtoneUrl.toString())

        val localFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            "${currentItem.ringtoneName}.mp3"
        )

        storageRef.getFile(localFile).addOnSuccessListener {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(localFile)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)

            val shareUri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", localFile
            )

            // Create an intent to share the ringtone
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri)

            // Grant temporary read permission to the receiving app
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Start the sharing activity
            context.startActivity(Intent.createChooser(shareIntent, "Share Ringtone"))


        }.addOnFailureListener() {
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
            Log.i("sivadi", it.toString())
        }
    }
  /*Todo-- Task 2, set ringtone*/
          fun setRingtone(
              context: Context?,
              ringtoneLinearProgressIndicator: LinearProgressIndicator,
              ringtone: RingtoneDataClass?
          ) {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(context)) {
                  val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                  intent.data = Uri.parse("package:" + context?.packageName)
                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                  context?.startActivity(intent)
                  return
              }
              val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
              val hasPermission = ContextCompat.checkSelfPermission(context!!, storagePermission) == PackageManager.PERMISSION_GRANTED

              if (hasPermission) {
                  downloadAndSetRingtone(context, ringtoneLinearProgressIndicator, ringtone)
              } else {
                  ActivityCompat.requestPermissions(
                      context as Activity,
                      arrayOf(storagePermission),
                      STORAGE_PERMISSION_REQUEST_CODE
                  )
              }
  }

    private fun downloadAndSetRingtone(
        context: Context,
        ringtoneLinearProgressIndicator: LinearProgressIndicator,
        ringtone: RingtoneDataClass?
    ) {
        ringtoneLinearProgressIndicator.visibility = View.VISIBLE

        val storageRef = Firebase.storage.getReferenceFromUrl(ringtone?.ringtoneUrl.toString())

        val localFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            "${ringtone?.ringtoneName}.mp3"
        )

        storageRef.getFile(localFile).addOnSuccessListener {
            if (localFile.exists() && localFile.length() > 0) {
                ringtoneLinearProgressIndicator.visibility = View.INVISIBLE

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DATA, localFile.absolutePath)
                    put(MediaStore.MediaColumns.TITLE, ringtone?.ringtoneName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                    put(MediaStore.Audio.Media.IS_RINGTONE, true)
                    put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
                    put(MediaStore.Audio.Media.IS_ALARM, false)
                    put(MediaStore.Audio.Media.IS_MUSIC, false)
                }

                val contentResolver = context.contentResolver
                val uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)

                // Set the newly inserted ringtone as the default ringtone
                uri?.let {
                    RingtoneManager.setActualDefaultRingtoneUri(
                        context,
                        RingtoneManager.TYPE_RINGTONE,
                        it
                    )
                    Toast.makeText(context, "Ringtone Set!", Toast.LENGTH_SHORT).show()
                }
            } else {
                ringtoneLinearProgressIndicator.visibility = View.INVISIBLE
                Toast.makeText(context, "Ringtone Download Failed", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            ringtoneLinearProgressIndicator.visibility = View.INVISIBLE
            Toast.makeText(context, "Ringtone Download Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }



    /*  fun setRingtone(
        context: Context?,
        ringtoneLinearProgressIndicator: LinearProgressIndicator,
        ringtone: RingtoneDataClass?
    ) {
        ringtoneLinearProgressIndicator.visibility = View.VISIBLE

        val storageRef = Firebase.storage.getReferenceFromUrl(ringtone?.ringtoneUrl.toString())
        val localFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            "${ringtone?.ringtoneName}.mp3"
        )

        storageRef.getFile(localFile).addOnSuccessListener {
            ringtoneLinearProgressIndicator.visibility = View.INVISIBLE
            Toast.makeText(context, "Ringtone Downloaded!", Toast.LENGTH_SHORT).show()

            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            )
            context?.startActivity(intent)
        }

    }
*/
    fun download(
        context: Context?,
        ringtoneLinearProgressIndicator: LinearProgressIndicator,
        ringtone: RingtoneDataClass?
    ) {
        ringtoneLinearProgressIndicator.visibility = View.VISIBLE

        val storageRef = Firebase.storage.getReferenceFromUrl(ringtone?.ringtoneUrl.toString())

        val localFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            "${ringtone?.ringtoneName}.mp3"
        )

        storageRef.getFile(localFile).addOnSuccessListener {
            ringtoneLinearProgressIndicator.visibility = View.INVISIBLE
            Toast.makeText(context, "Ringtone Downloaded!", Toast.LENGTH_SHORT).show()
        }

    }

    fun showMenu(
        requireContext: Context,
        it: View?,
        sortByMenu: Int,
        ringtoneList: ArrayList<RingtoneDataClass>,
        rvRingtone: RecyclerView
    ) {
        val popup = PopupMenu(requireContext, it)
        popup.menuInflater.inflate(sortByMenu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            // Respond to menu item click.
            when (menuItem.itemId) {
                R.id.action_wallpaper_new_to_old -> {
                    ringtoneList.sortByDescending { it.dateUploaded }
                    rvRingtone.adapter?.notifyItemRangeChanged(0, ringtoneList.size)
                    true
                }

                R.id.action_wallpaper_popular -> {
                    ringtoneList.sortByDescending { it.downloads }
                    rvRingtone.adapter?.notifyItemRangeChanged(0, ringtoneList.size)
                    true
                }

                else -> {
                    false
                }
            }
        }
        // Show the popup menu.
        popup.show()
    }

}