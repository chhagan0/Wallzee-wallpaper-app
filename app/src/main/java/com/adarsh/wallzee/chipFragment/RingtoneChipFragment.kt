package com.adarsh.wallzee.chipFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.ringtone.RingtoneDataClass
import com.adarsh.wallzee.ringtone.RingtoneFunctions
import com.adarsh.wallzee.ringtone.RingtoneRecyclerAdapter
import com.adarsh.wallzee.utils.Ringtone
import com.adarsh.walzee.R
import com.airbnb.lottie.LottieAnimationView


class RingtoneChipFragment : Fragment() {
    private lateinit var ringtoneList: ArrayList<RingtoneDataClass>
    private val ringtoneFunctions = RingtoneFunctions()
    private lateinit var adapter: RingtoneRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ringtoneList = arrayListOf()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            stopMusicPlaybackAtPosition(1)
//            stopMusicPlaybackAtPosition(2)
//            stopMusicPlaybackAtPosition(3)
//            stopMusicPlaybackAtPosition(4)
//            stopMusicPlaybackAtPosition(5)
//            stopMusicPlaybackAtPosition(6)
//            stopMusicPlaybackAtPosition(7)
//            stopMusicPlaybackAtPosition(8)
//            stopMusicPlaybackAtPosition(9)
//            stopMusicPlaybackAtPosition(10)
//            stopMusicPlaybackAtPosition(11)
//            stopMusicPlaybackAtPosition(12)
//            stopMusicPlaybackAtPosition(13)
//            stopMusicPlaybackAtPosition(14)
//            stopMusicPlaybackAtPosition(15)
//            stopMusicPlaybackAtPosition(16)
            requireActivity().moveTaskToBack(false)
        }

        callback.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          return inflater.inflate(R.layout.fragment_ringtone_chip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvRingtone = view.findViewById<RecyclerView>(R.id.rvRingtone_1)
        rvRingtone.visibility = View.GONE
        view.findViewById<LottieAnimationView>(R.id.lottie_loading_diamond).visibility =
            View.VISIBLE

        rvRingtone.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvRingtone.adapter = RingtoneRecyclerAdapter(context, ringtoneList, findNavController())

        view.findViewById<Button>(R.id.btSortBy).setOnClickListener {
            Ringtone().showMenu(requireContext(), it, R.menu.sort_by_menu, ringtoneList, rvRingtone)
        }

        Ringtone().getRingtones(
            ringtoneList,
            rvRingtone,
            view.findViewById(R.id.lottie_loading_diamond)
        )
    }
    private fun stopMusicPlaybackAtPosition(position: Int) {
        val rvRingtone = view?.findViewById<RecyclerView>(R.id.rvRingtone_1)

        val viewHolder = rvRingtone?.findViewHolderForAdapterPosition(position) as? RingtoneRecyclerAdapter.ViewHolder
        viewHolder?.let { ringtoneFunctions.pauseAudio(it.btRingtonePause,viewHolder.btRingtonePlay,requireContext(),viewHolder.circularProgressIndicator,null) }

    }
    override fun onPause() {
        stopMusicPlaybackAtPosition(1)
//        stopMusicPlaybackAtPosition(2)
//        stopMusicPlaybackAtPosition(3)
//        stopMusicPlaybackAtPosition(4)
//        stopMusicPlaybackAtPosition(5)
//        stopMusicPlaybackAtPosition(6)
//        stopMusicPlaybackAtPosition(7)
//        stopMusicPlaybackAtPosition(8)
//        stopMusicPlaybackAtPosition(9)
//        stopMusicPlaybackAtPosition(10)
//        stopMusicPlaybackAtPosition(11)
//        stopMusicPlaybackAtPosition(12)
//        stopMusicPlaybackAtPosition(13)
//        stopMusicPlaybackAtPosition(14)
//        stopMusicPlaybackAtPosition(15)
//        stopMusicPlaybackAtPosition(16)
        super.onPause()

      }

    override fun onStop() {
        stopMusicPlaybackAtPosition(1)
        super.onStop()
//        stopMusicPlaybackAtPosition(2)
//        stopMusicPlaybackAtPosition(3)
//        stopMusicPlaybackAtPosition(4)
//        stopMusicPlaybackAtPosition(5)
//        stopMusicPlaybackAtPosition(6)
//        stopMusicPlaybackAtPosition(7)
//        stopMusicPlaybackAtPosition(8)
//        stopMusicPlaybackAtPosition(9)
//        stopMusicPlaybackAtPosition(10)
//        stopMusicPlaybackAtPosition(11)
//        stopMusicPlaybackAtPosition(12)
//        stopMusicPlaybackAtPosition(13)
//        stopMusicPlaybackAtPosition(14)
//        stopMusicPlaybackAtPosition(15)
//        stopMusicPlaybackAtPosition(16)


    }

}