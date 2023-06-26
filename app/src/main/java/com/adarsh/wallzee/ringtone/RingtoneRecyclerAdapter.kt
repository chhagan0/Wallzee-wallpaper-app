package com.adarsh.wallzee.ringtone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.utils.Favourite
import com.adarsh.wallzee.utils.Ringtone
import com.adarsh.walzee.R
import com.bumptech.glide.Glide.init
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RingtoneRecyclerAdapter(
    var context: Context?,
    private val ringtoneList: ArrayList<RingtoneDataClass>,
    private val findNavController: NavController
) : RecyclerView.Adapter<RingtoneRecyclerAdapter.ViewHolder>() {
    private val ringtoneFunctions = RingtoneFunctions()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvRingToneName: TextView = itemView.findViewById(R.id.tvRingtoneName)
        val ivRingtoneImage: ImageView = itemView.findViewById(R.id.ivRingtoneImage)
        val btRingtonePlay: MaterialButton = itemView.findViewById(R.id.btPlayRingtone)
        val btRingtonePause: MaterialButton = itemView.findViewById(R.id.btPauseRingtone)
        val btFavouriteRingtone: MaterialButton = itemView.findViewById(R.id.btFavouriteRingtone)
        val btShareRingtone: MaterialButton = itemView.findViewById(R.id.btShareRingtone)
        val circularProgressIndicator: CircularProgressIndicator =
            itemView.findViewById(R.id.circularProgressIndicator)

        val cvRingtone: CardView = itemView.findViewById(R.id.cvRingtone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.ringtone_recycler_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return ringtoneList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = ringtoneList[position]
        holder.tvRingToneName.text = currentItem.ringtoneName

        fun stopMusicPlayback() {
            ringtoneFunctions.pauseAudio(
                holder.btRingtonePause,
                holder.btRingtonePlay,
                context,
                holder.circularProgressIndicator,
                null
            )
        }


        var isPlaying = false
        holder.btRingtonePlay.setOnClickListener {
            if (isPlaying) {
//                ringtoneFunctions.releaseMediaPlayer()
            } else {
                holder.cvRingtone.findViewTreeLifecycleOwner()?.lifecycleScope?.launch(Dispatchers.IO) {
                    ringtoneFunctions.playAudio(
                        currentItem.ringtoneUrl,
                        holder.btRingtonePlay,
                        holder.btRingtonePause,
                        holder.circularProgressIndicator,
                        null
                    )
                }
            }
            isPlaying = !isPlaying
        }

        holder.btRingtonePause.setOnClickListener {

            ringtoneFunctions.pauseAudio(
                holder.btRingtonePause,
                holder.btRingtonePlay,
                context,
                holder.circularProgressIndicator,
                null
            )
        }

        context?.let {
            Favourite().isFavourite(
                3, currentItem.ringtoneid, holder.btFavouriteRingtone, null, it
            )
        }

        holder.btFavouriteRingtone.setOnClickListener {
            if (Firebase.auth.currentUser?.email != null) {
                Favourite().checkFavourites(
                    context, 3, currentItem.ringtoneid, holder.btFavouriteRingtone, null
                )
            } else {
                findNavController.navigate(R.id.loginFragment)
            }
        }

        holder.btShareRingtone.setOnClickListener {
            if (context != null) {
                Ringtone().shareRingtone(context!!, currentItem)
            }
        }

        var drawable = R.drawable.ringtone_bg_1
        when (position % 4) {
            0 -> {
                drawable = R.drawable.ringtone_bg_1
            }
            1 -> {
                drawable = R.drawable.ringtone_bg_2
            }
            2 -> {
                drawable = R.drawable.ringtone_bg_3
            }
            3 -> {
                drawable = R.drawable.ringtone_bg_4
            }
        }

        holder.cvRingtone.setOnClickListener {
            findNavController.navigate(
                R.id.ringtoneFullscreenFragment,
                bundleOf("ringtoneId" to currentItem.ringtoneid, "drawable" to drawable)
            )
        }

        holder.ivRingtoneImage.setImageDrawable(context?.let {
            ResourcesCompat.getDrawable(
                it.resources, drawable, null
            )
        })

        // Animation
        holder.cvRingtone.animation =
            AnimationUtils.loadAnimation(context, R.anim.recycler_item_scale)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        ringtoneFunctions.releaseMediaPlayer()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        ringtoneFunctions.releaseMediaPlayer()
    }


    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.itemView.clearAnimation()
    }
}
