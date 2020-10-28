package com.example.flagwire.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.flagwire.R
import com.example.flagwire.others.SessionManagement

class SplashActivity : AppCompatActivity() {

    private lateinit var sessionManagement: SessionManagement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sessionManagement = SessionManagement(this)

        Handler().postDelayed(Runnable {

            if (sessionManagement.isLoggedIn()){

                val intent  = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent  = Intent(this,IntroActivity::class.java)
                startActivity(intent)
                finish()
            }

        },3000)

    }
}