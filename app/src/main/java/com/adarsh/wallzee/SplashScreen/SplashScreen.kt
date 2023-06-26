package com.adarsh.wallzee.SplashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.adarsh.wallzee.MainActivity
import com.adarsh.walzee.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val handler=Handler()
        handler.postDelayed(Runnable {
            startActivity(Intent(this,MainActivity::class.java))
        },1600)
    }
}