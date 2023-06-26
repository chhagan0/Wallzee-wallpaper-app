package com.adarsh.wallzee

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.adarsh.wallzee.InternetConnection.InternetConnection
import com.adarsh.wallzee.ads.GenerateAd
import com.adarsh.wallzee.creator.CreatorDataClass
import com.adarsh.wallzee.notification.PushNotificationService
import com.adarsh.walzee.R
import com.adarsh.walzee.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : InternetConnection() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var rewardedAd: RewardedAd? = null
    private final var TAG = "MainActivity"
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { _: Boolean ->
//        if (_) {
//
//        } else {
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GenerateAd().bannerAds(this, binding.adManagerAdView)


        binding.splashScreen.cl.visibility = View.GONE
//
//        Handler().postDelayed({
//            binding.splashScreen.cl.visibility = View.GONE
//        }, 100)

//        binding.circularProgressIndicator2.visibility = View.VISIBLE
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val contentMain = findViewById<ConstraintLayout>(R.id.include)
        val toolbar = contentMain.findViewById<MaterialToolbar>(R.id.toolbar)

//        contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
//            View.GONE

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.FirstFragment -> {
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = "Wallzee"
                    binding.adManagerAdView.visibility = View.VISIBLE
                    toolbar.setNavigationOnClickListener {
                        binding.drawerLayout.open()
                    }
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_menu_24, null
                    )
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.VISIBLE
                    contentMain.findViewById<ChipGroup>(R.id.chip_group).check(R.id.chip_1)
                    // Scroll to the left
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
                        .postDelayed({
                            val targetView =
                                contentMain.findViewById<ChipGroup>(R.id.chip_group)
                                    .findViewById<View>(R.id.chip_1)
                            contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
                                .smoothScrollTo(targetView.left, 0)
                        }, 100)
                    toolbar.menu.clear()
                    toolbar.inflateMenu(R.menu.menu_main)
                }

                R.id.wallpaperChipFragment -> {
                    binding.adManagerAdView.visibility = View.VISIBLE
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = "Wallzee"
                    toolbar.setNavigationOnClickListener {
                        binding.drawerLayout.open()
                    }
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_menu_24, null
                    )
//                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
//                        View.VISIBLE
                }

                R.id.categoryChipFragment -> {
                    binding.adManagerAdView.visibility = View.VISIBLE
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = "Wallzee"
                    toolbar.setNavigationOnClickListener {
                        binding.drawerLayout.open()
                    }
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_menu_24, null
                    )
//                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
//                        View.VISIBLE
                }

                R.id.ringtonechipFragment -> {
                    binding.adManagerAdView.visibility = View.VISIBLE
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = "Wallzee"
                    toolbar.setNavigationOnClickListener {
                        binding.drawerLayout.open()
                    }
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_menu_24, null
                    )
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.VISIBLE
                }

                R.id.uploadFragment -> {
                    binding.adManagerAdView.visibility = View.GONE
                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = "Upload"
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.exploreFragment -> {
                    binding.adManagerAdView.visibility = View.VISIBLE

                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.title = "Wallpapers"
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.ringtoneFragment -> {
                    binding.adManagerAdView.visibility = View.GONE

                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.title = "Ringtones"
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.categoryFullScreenFragment -> {
                    binding.adManagerAdView.visibility = View.VISIBLE
                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
//                    toolbar.title = "Ringtones"
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.searchFragment -> {
                    binding.adManagerAdView.visibility = View.VISIBLE
                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = ""
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.creatorProfileFragment -> {
                    binding.adManagerAdView.visibility = View.GONE
                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = ""
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.creatorProfileUpdateFragment -> {
                    binding.adManagerAdView.visibility = View.GONE
                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = "Update Profile"
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.ringtoneFullscreenFragment -> {
                    binding.adManagerAdView.visibility = View.GONE
                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = ""
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.loginFragment -> {
                    binding.adManagerAdView.visibility = View.GONE

                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = ""
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.registerFragment -> {
                    binding.adManagerAdView.visibility = View.GONE

                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = ""
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                R.id.favouriteFragment -> {
                    binding.adManagerAdView.visibility = View.GONE
                    toolbar.menu.clear()
                    toolbar.visibility = View.VISIBLE
                    toolbar.title = "Favourites"
                    toolbar.navigationIcon = ResourcesCompat.getDrawable(
                        resources, R.drawable.baseline_arrow_back_24, null
                    )
                    toolbar.setNavigationOnClickListener {
                        navController.navigate(R.id.FirstFragment)
                    }
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }

                else -> {
                    binding.adManagerAdView.visibility = View.GONE
                    toolbar.visibility = View.GONE
                    contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView).visibility =
                        View.GONE
                }
            }
        }

        //check msg from FullscreenViewpager
        val creatorEmail = intent.getStringExtra("creatorEmail")
        if (creatorEmail != null) {
            navController.navigate(
                R.id.creatorProfileFragment,
                bundleOf("creatorEmail" to creatorEmail)
            )
        }

        val login = intent.getStringExtra("login")
        if (login != null) {
            navController.navigate(
                R.id.loginFragment,
                bundleOf("creatorEmail" to creatorEmail)
            )
        }

        binding.navigationView.setNavigationItemSelectedListener { menuId ->
            binding.drawerLayout.close()
            when (menuId.itemId) {
                R.id.action_upload -> {
                    navController.navigate(R.id.uploadFragment)
                    true
                }

                R.id.action_profile -> {
                    if (Firebase.auth.currentUser?.email != null) {
                        navController.navigate(
                            R.id.creatorProfileFragment,
                            bundleOf(
                                "creatorEmail" to Firebase.auth.currentUser!!.email
                            )

                        )
                    } else {
                        navController.navigate(R.id.loginFragment)
                    }
                    true
                }

                R.id.action_wallpaper -> {
                    navController.navigate(R.id.exploreFragment, bundleOf("type" to 1))
                    true
                }

                R.id.action_live_wallpaper -> {
                    navController.navigate(R.id.exploreFragment, bundleOf("type" to 2))
                    true
                }

                R.id.action_ringtones -> {
                    navController.navigate(R.id.ringtoneFragment)
                    true
                }

                R.id.action_favourite -> {
                    navController.navigate(R.id.favouriteFragment)
                    true
                }

                R.id.action_instagram -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://www.instagram.com/wallzeeapp/")
                    startActivity(intent)
                    true
                }

                R.id.action_privacy_policy -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://wallzee.net/privacy.html")
                    startActivity(intent)
                    true
                }

                else -> {
                    false
                }
            }
        }

        contentMain.findViewById<ChipGroup>(R.id.chip_group)
            .setOnCheckedChangeListener { _, checkedId ->
                // Responds to child chip checked/unchecked
                when (checkedId) {
                    R.id.chip_1 -> {
                        navController.navigate(R.id.FirstFragment)
                    }

                    R.id.chip_2 -> {
                        navController.navigate(
                            R.id.wallpaperChipFragment, bundleOf("type" to 1)
                        )
                        // Scroll to the left
                        contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
                            .postDelayed({
                                val targetView =
                                    contentMain.findViewById<ChipGroup>(R.id.chip_group)
                                        .findViewById<View>(R.id.chip_1)
                                contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
                                    .smoothScrollTo(targetView.left, 0)
                            }, 100)
                    }

                    R.id.chip_3 -> {
                        navController.navigate(
                            R.id.wallpaperChipFragment, bundleOf("type" to 2)
                        )
                    }


                    R.id.chip_4 -> {
                        navController.navigate(R.id.ringtonechipFragment)
                        // Scroll to the right
                        contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
                            .postDelayed({
                                val targetView =
                                    contentMain.findViewById<ChipGroup>(R.id.chip_group)
                                        .findViewById<View>(R.id.chip_4)
                                contentMain.findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
                                    .smoothScrollTo(targetView.right, 0)
                            }, 100)
                    }

                    R.id.chip_5 -> {
                        navController.navigate(R.id.categoryChipFragment)
                    }
                }
            }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_search -> {
                    navController.navigate(R.id.searchFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
        })

        askNotificationPermission()


    }


    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                PushNotificationService()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}