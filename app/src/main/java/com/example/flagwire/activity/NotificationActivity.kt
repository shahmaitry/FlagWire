package com.example.flagwire.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import com.example.flagwire.R

class NotificationActivity : AppCompatActivity() {
    private lateinit var iv_back_notification:AppCompatImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        iv_back_notification = findViewById(R.id.iv_back_notification)
        iv_back_notification.setOnClickListener {

            finish()
        }
    }
}