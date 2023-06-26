package com.adarsh.wallzee.chipFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.wallzee.category.CategoryChipRecyclerAdapter
import com.adarsh.wallzee.category.CategoryDataClass
import com.adarsh.wallzee.utils.Category
import com.adarsh.walzee.R
import com.airbnb.lottie.LottieAnimationView

class CategoryChipFragment : Fragment() {
    private lateinit var categoryList: ArrayList<CategoryDataClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryList = arrayListOf()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().moveTaskToBack(false)
        }

        callback.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_category_chip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val rvCategories = view.findViewById<RecyclerView>(R.id.rvCategories_1)
        rvCategories.visibility = View.GONE
        view.findViewById<LottieAnimationView>(R.id.lottie_loading_diamond).visibility =
            View.VISIBLE
        rvCategories.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rvCategories.adapter = CategoryChipRecyclerAdapter(
            context,
            categoryList,
            findNavController()
        )

        Category().getCategories(categoryList, rvCategories,view.findViewById(R.id.lottie_loading_diamond))

    }

}