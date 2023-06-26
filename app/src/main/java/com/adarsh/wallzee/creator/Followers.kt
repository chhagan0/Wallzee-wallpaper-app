package com.adarsh.wallzee.creator

import android.content.res.Resources
import android.widget.TextView
import com.adarsh.walzee.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Followers {

    fun checkIfFollowing(creatorEmail: String, btFollow: MaterialButton, resources: Resources) {
        Firebase.firestore.collection("creators")
            .document(Firebase.auth.currentUser?.email.toString())
            .get()
            .addOnSuccessListener { document ->
                val isFollowing: ArrayList<String> =
                    document.get("followingList") as ArrayList<String>
                if (isFollowing.contains(creatorEmail)) {
                    btFollow.text = "Following"
                    btFollow.setBackgroundColor(resources.getColor(R.color.white))
                    btFollow.setTextColor(resources.getColor(R.color.black))
                } else {
                    btFollow.text = "Follow"
                    btFollow.setBackgroundColor(resources.getColor(R.color.transparent))
                    btFollow.setTextColor(resources.getColor(R.color.white))
                }
            }
    }


    fun manageFollow(
        creatorEmail: String,
        btFollow: MaterialButton,
        resources: Resources,
        tvFollowers: TextView?
    ) {
        Firebase.firestore.collection("creators")
            .document(Firebase.auth.currentUser?.email.toString())
            .get()
            .addOnSuccessListener { document ->
                val isFollowing =
                    document.get("followingList") as ArrayList<String>
                if (isFollowing.contains(creatorEmail)) {
                    isFollowing.remove(creatorEmail)
                    Firebase.firestore.collection("creators")
                        .document(Firebase.auth.currentUser?.email.toString())
                        .update("followingList", isFollowing)
                        .addOnSuccessListener {
                            btFollow.text = "Follow"
                            btFollow.setBackgroundColor(resources.getColor(R.color.transparent))
                            btFollow.setTextColor(resources.getColor(R.color.white))
                            if (tvFollowers != null) {
                                Firebase.firestore.collection("creators")
                                    .document(creatorEmail)
                                    .update("followers", tvFollowers.text.toString().toInt() - 1)
                            }
                        }

                } else {
                    isFollowing.add(creatorEmail.toString())
                    Firebase.firestore.collection("creators")
                        .document(Firebase.auth.currentUser?.email.toString())
                        .update("followingList", isFollowing).addOnSuccessListener {

                            btFollow.text = "Following"
                            btFollow.setBackgroundColor(resources.getColor(R.color.white))
                            btFollow.setTextColor(resources.getColor(R.color.black))
                            if (tvFollowers != null) {
                                Firebase.firestore.collection("creators")
                                    .document(creatorEmail)
                                    .update("followers", tvFollowers.text.toString().toInt() + 1)
                            }
                        }
                }
            }
    }
}