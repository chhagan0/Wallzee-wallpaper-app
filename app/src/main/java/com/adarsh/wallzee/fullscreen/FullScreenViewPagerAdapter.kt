package com.adarsh.wallzee.fullscreen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.MainActivity
import com.adarsh.wallzee.creator.Followers
import com.adarsh.wallzee.utils.Favourite
import com.adarsh.wallzee.wallpaper.WallpaperDataClass
import com.adarsh.walzee.R
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FullScreenViewPagerAdapter(
    private val list: ArrayList<WallpaperDataClass>,
    val context: Context,
    private val fragmentManager: FragmentManager,
    private val type: Int,
    private val lifecycleScope: LifecycleCoroutineScope,

    ) : RecyclerView.Adapter<FullScreenViewPagerAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvFullScreenCategory)
        val tvCreatorName: TextView = itemView.findViewById(R.id.tvCreatorName)
        val ivFullScreen: ImageView = itemView.findViewById(R.id.ivFullscreen)
        val toolbar: MaterialToolbar = itemView.findViewById(R.id.toolbar)
        val ivCreatorProfile: ImageView = itemView.findViewById(R.id.ivCreatorProfile)
        val vvFullScreen: PlayerView = itemView.findViewById(R.id.vvFullScreen)
        val btSetWallpaper: ImageView = itemView.findViewById(R.id.btSetWallpaper)
        val btWallpaperInfo: ImageView = itemView.findViewById(R.id.btWallpaperInfo)
        val btDownloadWallpaper: ImageView = itemView.findViewById(R.id.btDownloadWallpaper)
        val btAddToFavourites: ImageView = itemView.findViewById(R.id.ivAddToFavourites)
        val tvDownloads: TextView = itemView.findViewById(R.id.tvFullScreenDownloads)
        val btFullScreenDelete: MaterialButton = itemView.findViewById(R.id.btFullScreenDelete)
        val btReport: MaterialButton = itemView.findViewById(R.id.btReport)
        val btShare: ImageView = itemView.findViewById(R.id.btShareWallpaper)
        val btFollow: MaterialButton = itemView.findViewById(R.id.btFollow)
        val tvWallpaperSize: TextView = itemView.findViewById(R.id.tvFullScreenWallpaperSize)
        val tvDate: TextView = itemView.findViewById(R.id.tvFullScreenDate)

        //        val tvWallpaperName: TextView = itemView.findViewById(R.id.tvWallpaperName)
        val downloadAnimation: LottieAnimationView =
            itemView.findViewById(R.id.lottieFullScreenDownloading)
        val favouriteIcon: ImageView = itemView.findViewById(R.id.ivAddToFavourites)
        val ivBlueTick: ImageView = itemView.findViewById(R.id.ivBlueTick)
        val setWallpaperAnimation: LottieAnimationView =
            itemView.findViewById(R.id.lottieFullScreenSetWallpaper)
        val lottieFullScreenSwipeUp: LottieAnimationView? =
            itemView.findViewById(R.id.lottieFullScreenSwipeUp)!!

        val ivMinimize: ImageView = itemView.findViewById(R.id.ivMinimize)
        val linearLayoutFlScreen: LinearLayout =
            itemView.findViewById(R.id.linearLayoutWallpaperInfo)
        val linearProgress: LinearProgressIndicator = itemView.findViewById(R.id.linearProgress)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fullscreen_viewpager_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        val thumbnailRequestBuilder: RequestBuilder<Drawable> =
            Glide.with(holder.itemView.context).asDrawable().sizeMultiplier(0.1f)


//bottom sheet
        val bottomSheetFragment =
            SetWallpaperBottomSheet(context, currentItem, holder.linearProgress)

        holder.tvCategory.text = currentItem.wallpaperCategory
        holder.tvDownloads.text = currentItem.downloads.toString()
        holder.tvDate.text = currentItem.dateUploaded!!.substring(0, 6)

        if (currentItem.isCreatorVerified)
            holder.ivBlueTick.visibility = View.VISIBLE

        val sizeInMb = currentItem.imageSize!!.toFloat() / 1024
        val size = if (sizeInMb < 1) "${currentItem.imageSize} Kb"
        else String.format("%.2f", currentItem.imageSize!!.toFloat() / 1024) + " Mb"

        holder.tvWallpaperSize.text = size

        when (type) {
//        wallpaper
            1 -> {
                Glide.with(context).load(currentItem.thumbnailUrl)
                    .thumbnail(thumbnailRequestBuilder).centerCrop().into(holder.ivFullScreen)

                //set wallpaper
                holder.btSetWallpaper.setOnClickListener {
                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
                }

//        download wallpaper
                holder.btDownloadWallpaper.setOnClickListener {
                    it.startAnimation(
                        AnimationUtils.loadAnimation(
                            context, androidx.transition.R.anim.abc_fade_out
                        )
                    )
                    currentItem.imageUrl?.let { it1 ->
                        currentItem.wallpaperName?.let { it2 ->
                            FullScreenWallpaperAction(context).downloadWallpaper(
                                it1,
                                currentItem.wallpaperid,
                                currentItem.downloads,
                                context,
                                it2,
                                holder.downloadAnimation,
                                holder.linearProgress
                            )
                        }
                    }
                }
//        share wallpaper
                holder.btShare.setOnClickListener {
                    it.startAnimation(
                        AnimationUtils.loadAnimation(
                            context, androidx.transition.R.anim.abc_fade_out
                        )
                    )
                    currentItem.thumbnailUrl?.let { it1 ->
                        currentItem.wallpaperName?.let { it2 ->
                            FullScreenWallpaperAction(context).shareWallpaper(
                                it1, it2
                            )
                        }
                    }
                }

            }

            2 -> {
                holder.ivFullScreen.visibility = View.GONE
                holder.vvFullScreen.visibility = View.VISIBLE
                currentItem.imageUrl?.let { streamVideo(context, holder.vvFullScreen, it) }
                holder.btDownloadWallpaper.setOnClickListener {
                    FullScreenLiveWallpaperAction().download(
                        context,
                        holder.linearProgress,
                        currentItem,
                        lifecycleScope
                    )
                }

//                holder.btSetWallpaper.visibility = View.GONE

                holder.btSetWallpaper.setOnClickListener {
                    FullScreenLiveWallpaperAction().setVideoAsWallpaper(
                        context,
                        holder.linearProgress,
                        currentItem,
                        lifecycleScope,
                        )
                }
            }
        }

        holder.tvCreatorName.text = "Uploaded By \n" + currentItem.creatorName


        //        check if favourite
        Favourite().isFavourite(
            type,
            currentItem.wallpaperid,
            null,
            holder.favouriteIcon,
            context
        )

        holder.btAddToFavourites.setOnClickListener {
            it.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    androidx.transition.R.anim.abc_fade_out
                )
            )
            //check for fav
            Favourite().checkFavourites(
                context,
                type,
                currentItem.wallpaperid,
                null,
                holder.favouriteIcon
            )
        }

        //delete wallpaper
        val currUid = Firebase.auth.currentUser?.email

        if (currUid != null) {
            if (currentItem.creatorEmail == currUid)
                holder.btFullScreenDelete.visibility = View.VISIBLE
        }

        holder.btFullScreenDelete.setOnClickListener {
            MaterialAlertDialogBuilder(context).setBackground(
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.dialog_bg,
                    null
                )
            ).setMessage("Are you sure you want to delete?")
                .setPositiveButton("Confirm") { _, _ ->
                    FullScreenWallpaperAction(context).deleteWallpaper(
                        currentItem.wallpaperid, type
                    )
                    context.startActivity(Intent(context, MainActivity::class.java))

                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()

        }

        holder.ivCreatorProfile.setImageDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.user,
                null
            )
        )

//report
        holder.btReport.setOnClickListener {
            showPopUp(currentItem)
        }

        //info
        holder.btWallpaperInfo.setOnClickListener {
            holder.linearLayoutFlScreen.visibility = View.VISIBLE
            holder.ivMinimize.rotation = 270f
        }

        holder.linearLayoutFlScreen.visibility = View.GONE
        holder.ivMinimize.updatePadding(0, 28, 0, 28)
        holder.ivMinimize.rotation = 90f

        var test = false
        holder.ivMinimize.setOnClickListener {
            val linearLayout = holder.linearLayoutFlScreen
            test = !test
            if (test) {
                linearLayout.visibility = View.GONE
                holder.ivMinimize.updatePadding(0, 28, 0, 28)
                holder.ivMinimize.rotation = 90f
            } else {
                linearLayout.visibility = View.VISIBLE
                holder.ivMinimize.rotation = 270f
            }
        }

        //toolbar
        holder.toolbar.setNavigationOnClickListener {
            (context as Activity).finish()
        }

        //profile
        holder.tvCreatorName.setOnClickListener {
            context.startActivity(
                Intent(context, MainActivity::class.java).putExtra(
                    "creatorEmail",
                    currentItem.creatorEmail.toString() + ".com"
                )
            )
        }
        //follow
        val email = Firebase.auth.currentUser?.email
        if (email != null) {
            if (currentItem.creatorEmail == email.substring(0, email.length - 4))
                holder.btFollow.visibility = View.INVISIBLE
        }

        if (Firebase.auth.currentUser?.email != null) {
            currentItem.creatorEmail?.let {
                Followers().checkIfFollowing(
                    it,
                    holder.btFollow,
                    context.resources
                )
            }

            holder.btFollow.setOnClickListener {
                currentItem.creatorEmail?.let { it1 ->
                    Followers().manageFollow(
                        it1,
                        holder.btFollow,
                        context.resources,
                        null
                    )
                }
            }
        } else {
            holder.btFollow.setOnClickListener {
                context.startActivity(
                    Intent(context, MainActivity::class.java).putExtra(
                        "login",
                        "login"
                    )
                )
            }
        }
    }


    private fun streamVideo(context: Context, playerView: PlayerView, url: String) {
        val simpleExoPlayer = ExoPlayer.Builder(context).build()
        playerView.player = simpleExoPlayer
        simpleExoPlayer.volume = 0f
        val mediaItem: MediaItem = MediaItem.fromUri(url)
        simpleExoPlayer.addMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.playWhenReady = true

    }

    private fun showPopUp(currentItem: WallpaperDataClass) {
        val popUpView = View.inflate(this.context, R.layout.report_pop_up_layout, null)
        val attribute = LinearLayout.LayoutParams.WRAP_CONTENT
        val widthAttribute = LinearLayout.LayoutParams.MATCH_PARENT
        val popUpWindow = PopupWindow(
            popUpView, widthAttribute, attribute, true
        )
        popUpWindow.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0)

        val mobileArray = arrayListOf(
            "Copyright infringement",
            "harassment",
            "pornography",
            "violence",
            "hate propaganda",
            "other"
        )
        val adapter = ArrayAdapter(
            context, R.layout.report_list_view_item, mobileArray
        )

        val reportListView = popUpView.findViewById<ListView>(R.id.lsReport)
        val btSubmit = popUpView.findViewById<MaterialButton>(R.id.btSubmit)
        reportListView.adapter = adapter
        var report: String? = null
        reportListView.setOnItemClickListener { _, _, i, _ ->
            report = mobileArray[i]
        }
        btSubmit.setOnClickListener {
            popUpWindow.dismiss()
            val docRef = Firebase.firestore.collection("reports")
            if (report != null)
                docRef.document(currentItem.wallpaperid.toString()).set(
                    hashMapOf("id" to currentItem.wallpaperid, "report" to report)
                ).addOnSuccessListener {
                    Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT)
                        .show()
                }
            else
                Toast.makeText(context, "Select an issue", Toast.LENGTH_SHORT)
                    .show()
        }

    }

}