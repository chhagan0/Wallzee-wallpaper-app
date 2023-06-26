package com.adarsh.wallzee.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.category.CategoryDataClass
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class Category {

    fun getCategories(
        categoryList: ArrayList<CategoryDataClass>,
        rvCategories: RecyclerView,
        lottieAnimationView: LottieAnimationView
    ) {
        val docRef = Firebase.firestore.collection("categories").orderBy("categoryName")

//        for (i in 1..50) {
//            docRef.document(i.toString()).set(
//                CategoryDataClass(
//                    i,
//                    "Abstract",
//                    "https://firebasestorage.googleapis.com/v0/b/walzee.appspot.com/o/categories%2Fcategoryimage.webp?alt=media&token=5a1d06a5-5acc-43fc-85d0-f98c4620cdc4"
//                )
//            )
//        }
        docRef.get().addOnSuccessListener { documents ->
//
//            if (documents.isEmpty) {
////                binding.tvNewQuestionsComingSoon.visibility = View.VISIBLE
//            }
            for (document in documents) {
                val category = document.toObject<CategoryDataClass>()
                categoryList.add(
                    CategoryDataClass(
                        category.categoryID,
                        category.categoryName,
                        category.categoryImageUrl
                    )
                )
            }
            rvCategories.adapter?.notifyItemInserted(0)
            rvCategories.visibility = View.VISIBLE
            lottieAnimationView.visibility = View.INVISIBLE
        }
    }
}