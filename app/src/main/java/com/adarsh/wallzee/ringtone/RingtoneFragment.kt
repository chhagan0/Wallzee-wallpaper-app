package com.adarsh.wallzee.ringtone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.utils.Ringtone
import com.adarsh.wallzee.utils.Wallpaper
import com.adarsh.walzee.R
import com.airbnb.lottie.LottieAnimationView


class RingtoneFragment : Fragment() {
    private lateinit var ringtoneList: ArrayList<RingtoneDataClass>
    private val ringtoneFunctions = RingtoneFunctions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ringtoneList = arrayListOf()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ringtone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvRingtone = view.findViewById<RecyclerView>(R.id.rvRingtone)

        rvRingtone.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvRingtone.adapter = RingtoneRecyclerAdapter(context, ringtoneList, findNavController())

        rvRingtone.visibility = View.GONE
        view.findViewById<LottieAnimationView>(R.id.lottie_loading_diamond).visibility =
            View.VISIBLE

        view.findViewById<Button>(R.id.btSortBy).setOnClickListener {
            Ringtone().showMenu(requireContext(),it, R.menu.sort_by_menu,ringtoneList,rvRingtone)
        }
        Ringtone().getRingtones(
            ringtoneList,
            view.findViewById(R.id.rvRingtone),
            view.findViewById(R.id.lottie_loading_diamond)
        )
    }

    override fun onPause() {
        ringtoneFunctions.releaseMediaPlayer()
        super.onPause()

    }


    override fun onStop() {
        ringtoneFunctions.releaseMediaPlayer()
        super.onStop()

    }
}