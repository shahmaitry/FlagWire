package com.example.flagwire.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flagwire.R
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.activity_final.*

class FinalActivity : AppCompatActivity() {

    private lateinit var thats_it :MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        thats_it = findViewById(R.id.btn_thats_it)

        thats_it.setOnClickListener {

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        iv_back.setOnClickListener {
             val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}