package com.example.flagwire.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.flagwire.R
import com.example.flagwire.retrofit.RetrofitBuilder
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPassActivity : AppCompatActivity() {

    private lateinit var et_forget_password_email:TextInputEditText
    private lateinit var btn_forget_password:MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_pass)

        et_forget_password_email = findViewById(R.id.et_forget_password_email)
        btn_forget_password = findViewById(R.id.btn_forget_password)

        btn_forget_password.setOnClickListener {

            if (TextUtils.isEmpty(et_forget_password_email.text)){
                Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show()
            }else{
                ForgetPassword()
            }
        }


    }


    private fun ForgetPassword() {

        val request :HashMap<String,String> =HashMap<String,String>()
        request.put("email_id",et_forget_password_email.text.toString())



        val call = RetrofitBuilder.instance.retrofit.forget_password(request)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ForgetPassActivity,"Please check your internet connection",
                    Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if (response.isSuccessful){

                    val json = JSONObject(response.body()!!.string())
                    val meassge = json.getString("message")
                    Toast.makeText(this@ForgetPassActivity, meassge, Toast.LENGTH_LONG).show()

                }else{

                    Toast.makeText(this@ForgetPassActivity, response.body().toString(), Toast.LENGTH_LONG).show()
                }
            }

        })



    }
}