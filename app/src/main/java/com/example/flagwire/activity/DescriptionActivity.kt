package com.example.flagwire.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import com.example.flagwire.R
import com.example.flagwire.others.SessionManagement
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.textview.MaterialTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_description.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class DescriptionActivity : AppCompatActivity() {

    private lateinit var flag_image :AppCompatImageView
    private lateinit var flag_description :MaterialTextView
    private lateinit var category_icon :AppCompatImageView
    private lateinit var txt_flag_source:MaterialTextView
    private lateinit var sessionManagement:SessionManagement


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        flag_image = findViewById(R.id.iv_image_flag)
        flag_description = findViewById(R.id.txt_flag_description)
        category_icon = findViewById(R.id.iv_category)
        txt_flag_source = findViewById(R.id.txt_flag_source)
        sessionManagement = SessionManagement(this)

        val description =  intent.getStringExtra("description")
        val image_url = intent.getStringExtra("image_url")
        val flagSource = intent.getStringExtra("flagSource")
        Picasso.get().load(image_url).into(flag_image)
        Log.d("image_url",image_url.toString())
        flag_description.setText(Html.fromHtml(description))
        txt_flag_source.setText(Html.fromHtml(flagSource))


        iv_back.setOnClickListener {
            finish()
        }

        iv_home_icon.setOnClickListener {
            finish()
        }

        iv_notification.setOnClickListener {

            startActivity(Intent(this,NotificationActivity::class.java))
        }

        category_icon.setOnClickListener {

            val intent   = Intent(this,FlagListActivity::class.java)
            startActivity(intent)

        }

        val pop_up_menu = findViewById(R.id.iv_popup_menu) as AppCompatImageView
        pop_up_menu.setOnClickListener {

            val popup = PopupMenu(this,it)
            popup.inflate(R.menu.header_menu)

            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.menu_profile -> {

                        startActivity(Intent(this,ProfileActivity::class.java))
                    }

                    R.id.menu_about_us ->{
                        startActivity(Intent(this,AboutUsActivity::class.java))
                    }

                    R.id.menu_contact -> {
                        startActivity(Intent(this,ContactUsActivity::class.java))
                    }

                    R.id.menu_terms_condition ->{
                        startActivity(Intent(this,TermsConditionActivity::class.java))
                    }

                    R.id.menu_logout ->{
                        sessionManagement.LogoutUser()
                        val i = Intent(this, LoginActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                        finish()
                    }

                }

                true
            })

            popup.show()

        }



    }


}