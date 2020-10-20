package com.example.flagwire.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flagwire.R
import com.google.android.material.button.MaterialButton

class IntroActivity : AppCompatActivity() {

    private lateinit var btn_login : MaterialButton
    private lateinit var btn_sign_up :MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        init()
    }

    private fun init() {
        btn_login = findViewById(R.id.btn_login)
        btn_sign_up = findViewById(R.id.btn_sign_up)
        btn_login.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        btn_sign_up.setOnClickListener {
            startActivity(Intent(this,RegistrationActivity::class.java))
        }
    }
}