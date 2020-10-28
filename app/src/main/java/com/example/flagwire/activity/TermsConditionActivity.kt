package com.example.flagwire.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.example.flagwire.R
import com.example.flagwire.model.profileData.ProfileDataResponse
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.material.textview.MaterialTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TermsConditionActivity : AppCompatActivity() {

    private lateinit var back_button:AppCompatImageView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var txt_terms_condition: MaterialTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_condition)
        back_button = findViewById(R.id.iv_back_terms_condition)
        txt_terms_condition = findViewById(R.id.txt_terms_condition)
        progressDialog = ProgressDialog(this)


        progressDialog.setMessage("Loading")
        back_button.setOnClickListener {
            finish()
        }
        Terms_condtion()
    }

    private fun Terms_condtion() {
        progressDialog.show()
        val call = RetrofitBuilder.instance.retrofit.profileData()
        call.enqueue(object : Callback<ProfileDataResponse> {
            override fun onFailure(call: Call<ProfileDataResponse>, t: Throwable) {
                Toast.makeText(
                    this@TermsConditionActivity, "Please check your internet connection",
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

                        txt_terms_condition.setText(Html.fromHtml(response.body()!!.data!!.get(0)!!.termCondition))

                    } else {
                        Toast.makeText(
                            this@TermsConditionActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    Log.d("error",response.message())
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@TermsConditionActivity,
                        response.body().toString(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        })
    }
}