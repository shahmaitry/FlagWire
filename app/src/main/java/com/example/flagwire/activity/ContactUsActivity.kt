package com.example.flagwire.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.example.flagwire.R
import com.example.flagwire.model.profileData.ProfileDataResponse
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.material.textview.MaterialTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsActivity : AppCompatActivity() {

    private lateinit var back_button: AppCompatImageView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var txt_email_us: MaterialTextView
    private lateinit var iv_whats_up:AppCompatImageView
    private lateinit var iv_facebook:AppCompatImageView
    private lateinit var txt_website_contact:MaterialTextView
    private var fb_link = ""
    private var whatsup = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        back_button = findViewById(R.id.iv_back_connect_us)
        txt_email_us = findViewById(R.id.txt_email_connect_us)
        txt_website_contact = findViewById(R.id.txt_website_contact)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading")
        iv_facebook = findViewById(R.id.iv_facebook)
        iv_whats_up = findViewById(R.id.iv_whats_up)
        back_button.setOnClickListener {

            finish()
        }


        iv_whats_up.setOnClickListener {

            val url = "https://api.whatsapp.com/send?phone="+whatsup
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }


        iv_facebook.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/"+fb_link))
            startActivity(intent)

        }

        GetConnect()
    }


    private fun GetConnect() {
        progressDialog.show()

        val call = RetrofitBuilder.instance.retrofit.profileData()
        call.enqueue(object : Callback<ProfileDataResponse> {
            override fun onFailure(call: Call<ProfileDataResponse>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(
                    this@ContactUsActivity, "Please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(
                call: Call<ProfileDataResponse>,
                response: Response<ProfileDataResponse>
            ) {

                if (response.isSuccessful) {
                    progressDialog.dismiss()

                    if (response.body()!!.message.equals("success", false)) {

                        txt_website_contact.setOnClickListener {

                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.addCategory(Intent.CATEGORY_BROWSABLE)
                            intent.data = Uri.parse(response.body()!!.data!!.get(0)!!.websiteLink)
                            startActivity(intent)

                        }

                        txt_website_contact.setText(response.body()!!.data!!.get(0)!!.websiteLink)
                        txt_email_us.setText(response.body()!!.data!!.get(0)!!.emailId)
                        fb_link = response.body()!!.data!!.get(0)!!.facebookLink!!
                        whatsup =  response.body()!!.data!!.get(0)!!.mobileno!!

                    } else {
                        Toast.makeText(
                            this@ContactUsActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@ContactUsActivity,
                        response.body().toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        })
    }
}