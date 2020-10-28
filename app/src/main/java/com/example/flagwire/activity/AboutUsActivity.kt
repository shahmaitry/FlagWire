package com.example.flagwire.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.example.flagwire.R
import com.example.flagwire.model.profileData.ProfileDataResponse
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.material.textview.MaterialTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutUsActivity : AppCompatActivity() {

    private lateinit var back_button: AppCompatImageView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var txt_about_us:MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        txt_about_us = findViewById(R.id.txt_about_us)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading")

        back_button = findViewById(R.id.iv_back_about_us)
        back_button.setOnClickListener {

            finish()
        }

        GetAboutUs()

    }

    private fun GetAboutUs() {
        progressDialog.show()

        val call = RetrofitBuilder.instance.retrofit.profileData()
        call.enqueue(object : Callback<ProfileDataResponse> {
            override fun onFailure(call: Call<ProfileDataResponse>, t: Throwable) {
                Toast.makeText(
                    this@AboutUsActivity, "Please check your internet connection",
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

                        txt_about_us.setText(Html.fromHtml(response.body()!!.data!!.get(0)!!.aboutus))

                    } else {
                        Toast.makeText(
                            this@AboutUsActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@AboutUsActivity,
                        response.body().toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        })
    }
}