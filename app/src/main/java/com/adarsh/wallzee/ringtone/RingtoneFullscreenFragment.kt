package com.adarsh.wallzee.ringtone

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.adarsh.wallzee.utils.Favourite
import com.adarsh.wallzee.utils.Ringtone
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.FragmentRingtoneFullscreenBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class RingtoneFullscreenFragment : Fragment() {
    private var _binding: FragmentRingtoneFullscreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRingtoneFullscreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ringtoneId = arguments?.getInt("ringtoneId")
        val drawable = arguments?.getInt("drawable")
        getRingtones(ringtoneId)

        binding.btReport.setOnClickListener {
            showPopUp(ringtoneId)
        }

        binding.ivRingtone.setImageDrawable(
            drawable?.let {
                ResourcesCompat.getDrawable(
                    resources,
                    it,
                    null
                )
            }
        )
    }


    private fun getRingtones(
        ringtoneId: Int?
    ) {
        val docRef = Firebase.firestore.collection("ringtones").document(ringtoneId.toString())
        docRef.get().addOnSuccessListener {
            val ringtone = it.toObject<RingtoneDataClass>()
            if (ringtone != null) {
                val ringtoneFunctions = RingtoneFunctions()

                binding.btPlay.setOnClickListener {
                    ringtoneFunctions.playAudio(
                        ringtone.ringtoneUrl,
                        binding.btPlay,
                        binding.btPause,
                        null,
                        binding.ringtoneLinearProgressIndicator
                    )
                }

                binding.btPause.setOnClickListener {
                    ringtoneFunctions.pauseAudio(
                        binding.btPause,
                        binding.btPlay,
                        context, null,

                        binding.ringtoneLinearProgressIndicator
                    )
                }

                binding.tvRingtoneName.text = ringtone.ringtoneName
                binding.btShare.setOnClickListener {
                    context?.let { it1 -> Ringtone().shareRingtone(it1, ringtone) }
                }
                binding.btFavourite.setOnClickListener {

                    if (Firebase.auth.currentUser?.email != null) {
                        Favourite().checkFavourites(
                            context,
                            3,
                            ringtoneId,
                            binding.btFavourite,
                            null
                        )
                    } else {
                        findNavController().navigate(R.id.loginFragment)
                    }
                }

                binding.btDownload.setOnClickListener {
                    showRingtonePopUp(ringtone)
                }

                Favourite().isFavourite(
                    3,
                    ringtoneId,
                    binding.btFavourite,
                    null,
                    requireContext()
                )
            }
        }
    }

    private fun showPopUp(ringtoneId: Int?) {
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
        val adapter = context?.let {
            ArrayAdapter(
                it, R.layout.report_list_view_item, mobileArray
            )
        }

        val reportListView = popUpView.findViewById<ListView>(R.id.lsReport)
        val btSubmit = popUpView.findViewById<MaterialButton>(R.id.btSubmit)
        reportListView.adapter = adapter
        var report: String? = null
        reportListView.setOnItemClickListener { _, _, i, _ ->
            report = mobileArray[i]
        }
        btSubmit.setOnClickListener {
            val docRef = Firebase.firestore.collection("reports")
            if (report != null)
                docRef.document(ringtoneId.toString()).set(
                    hashMapOf("id" to ringtoneId, "report" to report)
                ).addOnSuccessListener {
                    Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT)
                        .show()
                }
            else
                Toast.makeText(context, "Select an issue", Toast.LENGTH_SHORT)
                    .show()

            popUpWindow.dismiss()

        }

    }


    private fun showRingtonePopUp(ringtone: RingtoneDataClass?) {
        val popUpView = View.inflate(this.context, R.layout.ringtone_pop_up_layout, null)
        val attribute = LinearLayout.LayoutParams.WRAP_CONTENT
        val widthAttribute = LinearLayout.LayoutParams.MATCH_PARENT
        val popUpWindow = PopupWindow(
            popUpView, widthAttribute, attribute, true
        )
        popUpWindow.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0)

        val mobileArray = arrayListOf(
            "Set as Ringtone",
            "Download"
        )
        val adapter = context?.let {
            ArrayAdapter(
                it, R.layout.report_list_view_item, mobileArray
            )
        }

        val reportListView = popUpView.findViewById<ListView>(R.id.lsReport)
        reportListView.adapter = adapter
        reportListView.setOnItemClickListener { _, _, i, _ ->

            when (i) {
                0 -> {
                    Ringtone().setRingtone(
                        context,
                        binding.ringtoneLinearProgressIndicator,
                        ringtone
                    )
                    popUpWindow.dismiss()
                }

                1 -> {
                    Ringtone().download(
                        context,
                        binding.ringtoneLinearProgressIndicator,
                        ringtone
                    )
                    popUpWindow.dismiss()
                }
            }


        }


    }
}