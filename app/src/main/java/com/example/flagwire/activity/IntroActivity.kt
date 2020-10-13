package com.example.flagwire.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flagwire.R
import com.google.android.material.button.MaterialButton

class IntroActivity : AppCompatActivity() {

    private lateinit var btn_login : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        init()
    }

    private fun init() {
        btn_login = findViewById(R.id.btn_login)
        btn_login.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
}